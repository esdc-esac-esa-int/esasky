/*
ESASky
Copyright (C) 2025 Henrik Norman

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published
by the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package esac.archive.esasky.cl.web.client.view.ctrltoolbar.treemap;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.SearchArea;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.ExtTapFovEvent;
import esac.archive.esasky.cl.web.client.event.TreeMapSelectionEvent;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.entities.EntityContext;
import esac.archive.esasky.cl.web.client.repository.DescriptorRepository;
import esac.archive.esasky.cl.web.client.utility.CoordinateUtils;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;
import org.moxieapps.gwt.highcharts.client.Credits;
import org.moxieapps.gwt.highcharts.client.Point;
import org.moxieapps.gwt.highcharts.client.ToolTip;
import org.moxieapps.gwt.highcharts.client.plotOptions.SeriesPlotOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ExtTapTreeMap extends TreeMap {

	private final EntityContext context;
    private final HashMap<String, Point> allPointMap = new HashMap<>();
    private boolean isColoredByParent = true;
    private boolean firstResultReceived = false;

    private List<TreeMapHeaderChanged> headerObservers = new LinkedList<>();
	
	public ExtTapTreeMap(final EntityContext context) {
		super(context);
		this.context = context;
		series.setOption("drillUpButton", JsonUtils.safeEval("{\"text\":\"Back\"}"));

		String fovText = TextMgr.getInstance().getText("treeMap_large_fov");
		fovText = fovText.replace("$fov_limit$", Double.toString(EsaSkyWebConstants.EXTTAP_FOV_LIMIT));
		ghostPoint.setLargeFovText(fovText);
		registerLargeFovEventObserver();
		
		setRedrawEventHandler(chartRedrawEvent -> {
            if (firstSelection) {
                firstSelection = false;
                return false;
            }

            if (addPointsOnNextRender) {
                addPointsOnNextRender = false;
                addPointsAfterFirstRender();
            }

            if (removePointsOnNextRender) {
                removePointsOnNextRender = false;
                removePointsAfterFirstRender();
            }

            String headerText = getRootPath(series.getNativeSeries());
            notifyHeaderChange(headerText);

            if(getLevelOfRoot(series.getNativeSeries()) < 2 && !isColoredByParent) {

                for(Point point : series.getPoints()) {
                    PointInformation pointInformation = allPoints.get(point.getText());
                    if(pointInformation != null && pointInformation.getParentColor() != null) {
                        String color = pointInformation.getParentColor();
                        setColor(point, color);
                    }
                }
                isColoredByParent = true;
                redraw();

            }else if(getLevelOfRoot(series.getNativeSeries()) >= 2 && isColoredByParent) {
                for(Point point : series.getPoints()) {
                    PointInformation pointInformation = allPoints.get(point.getText());
                    if(pointInformation != null) {
                        String color = pointInformation.descriptor.getColor();
                        setColor(point, color);
                    }
                }
                isColoredByParent = false;
                redraw();

            }
            return false;
        });
		
	   setSeriesPlotOptions(new SeriesPlotOptions()
                .setPointClickEventHandler(pointClickEvent -> {
                    double fov = CoordinateUtils.getCenterCoordinateInJ2000().getFov();
                    SearchArea searchArea = DescriptorRepository.getInstance().getSearchArea();
                    if(!DescriptorRepository.tapAreaTooLargeForExternal(fov, searchArea)) {
                        String id = pointClickEvent.getPoint().getText();
                        if (id != null) {
                            PointInformation pointInformation = allPoints.get(id);

                            CommonEventBus.getEventBus().fireEvent(
                                    new TreeMapSelectionEvent(pointInformation));
                        }
                    }
                    return false;
                }));

	}
	
	private void setColor(Point point, String color) {
			Point option = new Point(point.getValue());
			option.setColor(color);
			point.update(option, false, false);
	}
	
	@Override
	protected void setTapCredits() {
    	setCredits(new Credits().setEnabled(false))
        .setToolTip(new ToolTip().setFormatter(toolTipData -> {
            Point point = toolTipData.getPoint();

            if (point.getText() != null && allPoints.containsKey(point.getText())) {
                PointInformation pointInformation = allPoints.get(point.getText());
                String tooltipText = pointInformation.longName;

                if(pointInformation.credits != null && !pointInformation.credits.isEmpty()
                        && !pointInformation.longName.equals(pointInformation.credits)) {
                     tooltipText += " [" + pointInformation.credits.replace("U+00F8", "\u00F8") + "]";
                }

                return "<div style=\"font-size: 12px !important;\">" + tooltipText + "</div>";
            }

            return point.getName();
        })).setChartTitle(null)
                .setBackgroundColor("rgba(0, 0, 0, 0.65)")
                .setMargin(1, 0, 0, 1);
    }

    @Override
    protected Point getNewPoint(String pointId, CommonTapDescriptor descriptor, String color, PointInformation pointInformation, double logCount) {
        String displayText = descriptor.getShortName();
        if (!pointInformation.getWavelengthShortName().isEmpty() && pointInformation.getTreemapLevel() >= EsaSkyConstants.TREEMAP_LEVEL_2) {
            displayText += " <br/><p style=\"font-size: 10px;\">("
                    + pointInformation.getWavelengthShortName() + ")</p>";
        }

        Point newPoint = new Point(displayText, logCount);
        newPoint.setColor(color);
        newPoint.setText(pointId);
        return newPoint;
    }
	
    private void cleanChildren(CommonTapDescriptor parent) {
        for(PointInformation pointInformation : allPoints.values()) {
            CommonTapDescriptor childDesc =  pointInformation.descriptor;
            if(childDesc.getParent() == parent) {
                Point child = getPoint(childDesc);
                child.update(0,false);
                pointInformation.setCount(0);
                cleanChildren(childDesc);
            }
        }
    }
    
    private void clearAll() {
    	for(PointInformation pointInformation : allPoints.values()) {
            CommonTapDescriptor desc = pointInformation.descriptor;
    		Point child = getPoint(desc);
			child.update(0,false);
    	}

    	makeSureGhostPointIsInGraph(ghostPoint.id);
    }


    @Override
    public void clearData() {
        for (Point point : series.getPoints()) {
            series.removePoint(point, false, false);
            point.setParent(""); // remove from parent, otherwise it will stay in graph
        }

        allPoints.clear();
        allPointMap.clear();

        update();

        addGhostPoint(ghostPoint.getLoadingText());
    }

    @Override
    public void update() {
        // Use other update functions
    }
    
    @Override
    public void addData(final List<CommonTapDescriptor> descriptors, List<Integer> counts) {
        if ((!descriptors.isEmpty()) && (descriptors.size() == counts.size())) {
            List<Integer> zeroCountList = new ArrayList<>();


            for (int i = 0; i < descriptors.size(); i ++) {
                CommonTapDescriptor desc =  descriptors.get(i);
            	if(EsaSkyConstants.TREEMAP_LEVEL_SERVICE == desc.getLevel()) {
            		cleanChildren(desc);
            	}

                if (counts.get(i) > 0) {
                    //Only add descriptors with non zero count
                	addPoints(desc, counts.get(i), false);

                } else {
                    //Store this index for later removing
                    zeroCountList.add(i);
                }

            }

            for (int i : zeroCountList) {
            	addPoints(descriptors.get(i), 0, false);
            }

            onSliderValueChange(sliderValueLow, sliderValueHigh);
        }
    }
    
    private Point getPoint2(CommonTapDescriptor descriptor) {
    	if(allPointMap.containsKey(descriptor.getId())){
    		return allPointMap.get(descriptor.getId());
    	}
    	return null;
    }
    
    private String getDescriptorColor(CommonTapDescriptor descriptor) {
        return descriptor.getOriginalParent().getColor();
    }
    
    @Override
    protected String generateNewPoint(CommonTapDescriptor descriptor, String color, PointInformation pointInformation, int count) {
    	String pointId = descriptor.getId();

    	if(isRendered()) {
	        final Point newPoint = getNewPoint (pointId, descriptor, color, pointInformation, logCount(count));

            if(descriptor.getLevel() > EsaSkyConstants.TREEMAP_LEVEL_SERVICE) {
	            Point parentPoint = getPoint2((CommonTapDescriptor) descriptor.getParent());
	            newPoint.setParent(parentPoint);
	    	}

	        series.addPoint(newPoint, false, false, false);
	        allPointMap.put(descriptor.getId(), newPoint);
    	}

		return pointId;
    }
    
    @Override
    protected void addPoints(CommonTapDescriptor descriptor, int count, boolean updateView) {
        String pointId;

        PointInformation pointInformation = new PointInformation(descriptor.getLongName(),
                descriptor.getMission(), descriptor.getCredits(), count, descriptor, context);
        
        String color = getDescriptorColor(descriptor);
        pointInformation.setParentColor(color);
        
        if (isRendered()) {
            pointId = addRenderedPoint(descriptor, pointInformation, color, count, updateView);
            
        } else {
        	pointId = addNotRenderedPoint(descriptor, pointInformation, color, count, updateView);
        }

        allPoints.put(pointId, pointInformation);

        if (count > 0) {
            removeGhostPoint();
            firstResultReceived = true;
        } else {
            makeSureGhostPointIsInGraph(pointId);
        }
    }
    
    protected void makeSureGhostPointIsInGraph(String pointId) {
        for (PointInformation pointInformation : allPoints.values()) {
            if (!pointInformation.equals(allPoints.get(pointId))
                    && pointInformation.getCount() > 0) {
                return;
            }
        }
        if(firstResultReceived) {
        	addNoResultsGhostPoint();
        }
    }
    
    private void update(boolean redraw) {
    	nativeSetData(series.getNativeSeries(), redraw);
    	String id = getIdOfSelectedLevel(series.getNativeSeries());
        if(redraw && !id.equals("")) {
            zoomToPoint(series.getNativeSeries(), id);
        }
    }
    
    private static native void nativeSetData(JavaScriptObject series, boolean redraw) /*-{
    	if(series.rootNode != ""){
    		
	    	for(var i = 0; i < series.data.length; i++){
	    		if(series.data[i].id == series.rootNode){
	    			if(series.data[i].value < 0.25){
	    				series.drillToNode("")
	    			}
	    			break;
	    		}
	    	}
    	}
    	series.setData(series.data, redraw, false, true);
	}-*/;	
    
    private void notifyHeaderChange(String headerText) {
    	for(TreeMapHeaderChanged observer : headerObservers){
			observer.onHeaderChanged(headerText);
		}
    }
    
    private native String getRootPath(JavaScriptObject series)/*-{
    	var rootNodeId = series.rootNode;
    	var array = []
    	var rootNode = series.nodeMap[rootNodeId]
		
		while(rootNode && rootNode.name != ""){
			array.push(rootNode.name)
			rootNodeId = rootNode.parent
			rootNode = series.nodeMap[rootNodeId]
		}
		
		var text = "";
		while(array.length > 0){
    		text = text + " > " + array.pop()
		}
    
    	return text;
    
    }-*/;
    
    private native String getNativePointId(JavaScriptObject point)/*-{
    	return point.id;
    
    }-*/;
    
    
    private native int getLevelOfRoot(JavaScriptObject series)/*-{
		function loopChildren(parent, targetName){
			var childLength = parent.children.length;
			for(var i = 0; i < childLength; i++){
				var child = parent.children[i]
				if(child.id == targetName){
					return child.level
				}
				
				if(child.levelDynamic == 0){
				 continue;
				}
				
				if(child.children.length > 0){
					var childResponse = loopChildren(child, targetName);
					if(childResponse != -1){
						return childResponse
					}
				}
			}
			return -1;
		}
		
		try {
			var rootNode = series.rootNode;
			if (rootNode == ""){
				return 0;
			}
			var level = loopChildren(series.nodeMap[""], rootNode)
			return level;
			
		} catch (err) {
			return -1
		}
	}-*/;
    
    public void registerHeaderObserver(TreeMapHeaderChanged observer) {
        headerObservers.add(observer);
    }
    
    @Override
    public void onSliderValueChange(double low, double high) {
    	super.onSliderValueChange(low, high);
        update(true);
    }

    private void registerLargeFovEventObserver() {
    	CommonEventBus.getEventBus().addHandler(ExtTapFovEvent.TYPE,
                extTapFovEvent -> {
                    SearchArea searchArea = DescriptorRepository.getInstance().getSearchArea();
                    if (DescriptorRepository.tapAreaTooLargeForExternal(extTapFovEvent.getFov(), searchArea)) {
                        addLargeFovGhostPoint();
                    } else if (!ghostPoint.isRemoved()) {
                        ghostPoint.setLoading();
                        update(true);
                    }
                });
    }
    
    protected void addLargeFovGhostPoint() {
    	zoomToRoot();
    	clearAll();
		addGhostPoint(ghostPoint.getLargeFovText());
		update(true);
    }
    
}