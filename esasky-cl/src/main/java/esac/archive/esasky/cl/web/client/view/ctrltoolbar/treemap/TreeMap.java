package esac.archive.esasky.cl.web.client.view.ctrltoolbar.treemap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.moxieapps.gwt.highcharts.client.Chart;
import org.moxieapps.gwt.highcharts.client.Credits;
import org.moxieapps.gwt.highcharts.client.Point;
import org.moxieapps.gwt.highcharts.client.Series;
import org.moxieapps.gwt.highcharts.client.Style;
import org.moxieapps.gwt.highcharts.client.ToolTip;
import org.moxieapps.gwt.highcharts.client.ToolTipData;
import org.moxieapps.gwt.highcharts.client.ToolTipFormatter;
import org.moxieapps.gwt.highcharts.client.events.ChartRedrawEvent;
import org.moxieapps.gwt.highcharts.client.events.ChartRedrawEventHandler;
import org.moxieapps.gwt.highcharts.client.events.PointClickEvent;
import org.moxieapps.gwt.highcharts.client.events.PointClickEventHandler;
import org.moxieapps.gwt.highcharts.client.labels.DataLabels;
import org.moxieapps.gwt.highcharts.client.labels.Labels;
import org.moxieapps.gwt.highcharts.client.plotOptions.PlotOptions.Cursor;
import org.moxieapps.gwt.highcharts.client.plotOptions.SeriesPlotOptions;
import org.moxieapps.gwt.highcharts.client.plotOptions.TreemapPlotOptions;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

import esac.archive.esasky.ifcs.model.descriptor.ColorChangeObserver;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.WavelengthDescriptor;
import esac.archive.esasky.ifcs.model.shared.ESASkyColors;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.TreeMapSelectionEvent;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.entities.EntityContext;
import esac.archive.esasky.cl.web.client.view.common.ESASkyMultiRangeSlider;
import esac.archive.esasky.cl.web.client.view.common.EsaSkyMultiRangeSliderObserver;

public class TreeMap extends Chart {

    private final CssResource style;
    private final Resources resources;

    public interface Resources extends ClientBundle {

        @Source("treeMap.css")
        @CssResource.NotStrict
        CssResource style();
    }

    private final EntityContext context;
    
    protected Series series;
    protected HashMap<String, PointInformation> allPoints = new HashMap<String, PointInformation>();

    protected GhostPoint ghostPoint;
    protected boolean firstSelection = false;

    protected boolean removePointsOnNextRender = false;
    private List<Point> pointsToRemove = new LinkedList<Point>();

    protected boolean addPointsOnNextRender = false;
    protected List<Point> pointsToAdd = new LinkedList<Point>();
    protected double sliderValueLow = 0;
    protected double sliderValueHigh = Double.MAX_VALUE;
    private boolean hasSlider = true;

    private List<TreeMapChanged> observers = new LinkedList<TreeMapChanged>();
    
    public TreeMap(final EntityContext context) {
        this.resources = GWT.create(Resources.class);
        this.style = this.resources.style();
        this.style.ensureInjected();
        this.addStyleName("treeMap");
        
        this.context = context;
        
        ghostPoint = new GhostPoint(TextMgr.getInstance().getText("treeMap_loading_" + context),
        		TextMgr.getInstance().getText("treeMap_noData_" + context),
        		TextMgr.getInstance().getText("treeMap_notInRange_" + context));

        setTapCredits();

        series = createSeries().setType(Series.Type.TREEMAP).setPlotOptions(
                new TreemapPlotOptions()
                        .setCursor(Cursor.POINTER)
                        .setBorderColor("#30A0A0")
                .setAlternateStartingDirection(false)
                .setLayoutAlgorithm(TreemapPlotOptions.LayoutAlgorithm.SQUARIFIED)
                .setLevels(
                                new TreemapPlotOptions.Level()
                                        .setLevel(1)
                                        .setBorderWidth(5)
                                        .setBorderColor("rgba(0, 0, 0, 0.65)")
                                        .setDataLabels(
                                                new DataLabels()
                                                        .setAlign(Labels.Align.CENTER)
                                                        .setEnabled(true)
                                                        .setVerticalAlign(
                                                                Labels.VerticalAlign.MIDDLE)
                                                        .setStyle(
                                                                new Style().setFontSize("13px")
                                                                        .setFontWeight("bold"))

                                        ),
                                new TreemapPlotOptions.Level().setLevel(2)
                                .setBorderWidth(2)
                                        .setBorderColor("rgba(0, 0, 0, 0.65)")
                                        .setDataLabels(new DataLabels().setEnabled(false)),
                        		 new TreemapPlotOptions.Level().setLevel(3).setBorderWidth(2)
                                                 .setBorderColor("rgba(0, 0, 0, 0.65)")
                                                 .setDataLabels(new DataLabels().setEnabled(false)))
                        .setLevelIsConstant(false)
                        .setAllowDrillToNode(true)
                        .setInteractByLeaf(false));

        series.addPoint(ghostPoint);

        addSeries(series);

        setSeriesPlotOptions(new SeriesPlotOptions()
                .setPointClickEventHandler(new PointClickEventHandler() {

                    @Override
                    public boolean onClick(PointClickEvent pointClickEvent) {
                        String id = pointClickEvent.getPoint().getText();
                        if (id != null) {
                            PointInformation pointInformation = allPoints.get(id);

                            CommonEventBus.getEventBus().fireEvent(
                                    new TreeMapSelectionEvent(pointInformation));
                        }
                        return false;
                    }
                }));

        setRedrawEventHandler(new ChartRedrawEventHandler() {

            @Override
            public boolean onRedraw(ChartRedrawEvent chartRedrawEvent) {
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
                
                return false;
            }
        });
        
    }
    
    
    protected void setTapCredits() {
    	setCredits(new Credits().setEnabled(false))
        .setToolTip(new ToolTip().setFormatter(new ToolTipFormatter() {

            @Override
            public String format(ToolTipData toolTipData) {
                Point point = toolTipData.getPoint();

                if (point.getText() != null && allPoints.containsKey(point.getText())) {
                    PointInformation pointInformation = allPoints.get(point.getText());
                    String tooltipText = pointInformation.longName;
                   
                	final String wavelengthText = "<br/><p style=\"font-size: 10px;\"> (" + pointInformation.getWavelengthLongName() + ") </p><br/>";
                	tooltipText +=  wavelengthText;
                    
                    if(pointInformation.credits != null && !pointInformation.credits.isEmpty()) {
                    	 tooltipText += " [" + pointInformation.credits.replace("U+00F8", "\u00F8") + "]";
                    }
                    return "<div style=\"font-size: 12px !important;\">" + tooltipText + "</div>";
                }

                return point.getName();
            }

        })).setChartTitle(null).setBackgroundColor("rgba(0, 0, 0, 0.65)").setMargin(1, 0, 0, 1);
    }
    
    protected void addPointsAfterFirstRender() {
        for (Point pointToAdd : pointsToAdd) {
            series.addPoint(pointToAdd, false, false, false);
        }
        pointsToAdd.clear();
        update();
    }

    protected void removePointsAfterFirstRender() {
        List<Point> matches = new LinkedList<Point>();
        for (Point point : series.getPoints()) {
            for (Point pointToRemove : pointsToRemove) {
                if (point.getName().equals(pointToRemove.getName())) {
                    matches.add(point);
                    break;
                }
            }
        }
        for (Point point : matches) {
            removePoint(point, false);
        }
        pointsToRemove.removeAll(matches);
        update();
    }

    public void addData(final List<IDescriptor> descriptors, List<Integer> counts) {
        
        if ((descriptors.size() > 0)
                && (descriptors.size() == counts.size())) {
            
            List<Integer> zeroCountList = new ArrayList<Integer>();
            
            for (int i = 0; i < descriptors.size(); i ++) {
            	
                if (counts.get(i) > 0) {
                    //Only add descriptors with non zero count
                	final IDescriptor descriptor = descriptors.get(i);
            		addPoints(descriptor, counts.get(i), false);
                } else {
                    //Store this index for later removing
                    zeroCountList.add(i);
                }
            }
            
            for (int i : zeroCountList) {
                addPoints(descriptors.get(i), counts.get(i), false);
            }
            
            update();
            onSliderValueChange(sliderValueLow, sliderValueHigh);
        }
    }
    
    protected double logCount(int count) {
        double logCount = Math.log(count);
        if (count == 1) {
            logCount = 0.27; // compensate for log(1) = 0. A value of 0 is invisible in the graph.
        } else if (count == 0) {
            logCount = 0;
        }
        return logCount;
    }
    
    protected Point getPoint(IDescriptor descriptor) {
    	for (Point point : series.getPoints()) {
            if (isMatch(descriptor, point)) {
            	return point;
            }
    	}
    	return null;
    }
    
    private void addPoints(IDescriptor descriptor, int count, boolean updateView) {
        String pointId = null;

        PointInformation pointInformation = new PointInformation(descriptor.getGuiLongName(),
                descriptor.getMission(), descriptor.getCreditedInstitutions(), count, descriptor, context);
        boolean found = false;
        if (isRendered()) {
            Point point = getPoint(descriptor);
            if(point != null) {
            	pointId = point.getText();
            	if (count == 0) {
            		removePoint(point, updateView);
            	} else {
            		point.update(logCount(count), updateView);
            	}
            	found = true;
            }
            
            if (!found) {
                
                pointId = descriptor.generateId();
                final Point newPoint = getNewPoint (pointId, descriptor, descriptor.getPrimaryColor(), pointInformation, logCount(count));

                series.addPoint(newPoint, false, false, false);
                
                if (updateView) {
                    update();
                }
                
        		descriptor.registerColorChangeObservers(new ColorChangeObserver() {
					@Override
					public void onColorChange(IDescriptor descriptor, String newColor) {
						setPointColor(descriptor, newColor);
					}
				});
            }
            
        } else {
            for (Point point : pointsToAdd) {
                if (isMatch(descriptor, point)) {
                    pointId = point.getText();
                    pointsToAdd.remove(point);
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                pointId = descriptor.generateId();
                
        		descriptor.registerColorChangeObservers(new ColorChangeObserver() {
					
					@Override
					public void onColorChange(IDescriptor descriptor, String newColor) {
						setPointColor(descriptor, newColor);
					}
				});
            }
            
            final Point newPoint = getNewPoint (pointId, descriptor, descriptor.getPrimaryColor(), pointInformation, logCount(count));
            
            pointsToAdd.add(newPoint);
            addPointsOnNextRender = true;
        }

        allPoints.put(pointId, pointInformation);

        if (count > 0) {
            removeGhostPoint();
        } else {
            makeSureGhostPointIsInGraph(pointId);
        }
    }
    
    protected Point getNewPoint (String pointId, IDescriptor descriptor, String color, PointInformation pointInformation, double logCount) {
    	String displayText = descriptor.getGuiShortName();
    	if(!pointInformation.getWavelengthShortName().isEmpty()) {
    		displayText += " <br/><p style=\"font-size: 10px;\">("
                    + pointInformation.getWavelengthShortName() + ")</p>";
    	}
    	
        Point newPoint = new Point(displayText, logCount);
        newPoint.setColor(color);
        newPoint.setText(pointId);
        return newPoint;
    }
    
    public void setPointColor(IDescriptor descriptor, String color) {
        
        Point foundPoint = null;
        
        for (Point point : series.getPoints()) {
            if (isMatch(descriptor, point)) {
                foundPoint = point;
                break;
            }
        }
        
        if (foundPoint != null) {
            
            String pointId = new String(foundPoint.getText());
            
            final PointInformation pointInfo = allPoints.get(foundPoint.getText());
            
            //Only way it seems to update the color
            final Point newPoint = getNewPoint (pointId, descriptor, color, pointInfo, logCount(pointInfo.count));
            foundPoint.update(newPoint);

        }
    }

    protected boolean isMatch(IDescriptor descriptor, Point point) {
        final PointInformation pointInfo = allPoints.get(point.getText());
        return point.getName().contains(descriptor.getGuiShortName())
                && pointInfo != null
                && point.getName().contains(pointInfo.getWavelengthShortName())
                && pointInfo.missionName.equals(descriptor.getMission())
                && pointInfo.descriptor.getDescriptorId().equals(descriptor.getDescriptorId());
    }

    protected void removePoint(Point point, boolean update) {
        allPoints.remove(point.getText());
        series.removePoint(point, false, false);
        point.setParent(""); // remove from parent, otherwise it will stay in graph
        if (update) {
            update();
        }
    }

    protected void makeSureGhostPointIsInGraph(String pointId) {
        for (PointInformation pointInformation : allPoints.values()) {
            if (!pointInformation.equals(allPoints.get(pointId))
                    && pointInformation.count > 0) {
                return;
            }
        }
        addNoResultsGhostPoint();
    }

    protected void addNoResultsGhostPoint() {
    	if (ghostPoint.isRemoved() || !ghostPoint.getName().equals(ghostPoint.getNoResultsText())) {
    		addGhostPoint(ghostPoint.getNoResultsText());
    	}
    }
    
    protected void addNotInRangeGhostPoint() {
    	//We don't want to overwrite noResultPointText
    	if (ghostPoint.isRemoved() ) {
    		addGhostPoint(ghostPoint.getNotInRangeText());
    	}
    }
    	
    protected void addGhostPoint(String newName) {
        boolean found = false;
        for (Point point : series.getPoints()) {
            if (point.getName().equals(ghostPoint.getName())) {
                found = true;
                point.setName(newName);
                ghostPoint.setName(newName);
                if (isRendered()) {
                    series.addPoint(point);
                    update();
                } else {
                    addPointsOnNextRender = true;
                    if (!pointsToAdd.contains(point)) {
                        pointsToAdd.add(point);
                    }
                }
                break;
            }
        }
        if (!found) {
        	ghostPoint.setName(newName);
            series.addPoint(ghostPoint);
            update();
        }
        ghostPoint.setRemoved(false);
    }

    protected void removeGhostPoint() {
        if (ghostPoint.isRemoved()) {
            return;
        }
        for (Point point : series.getPoints()) {
            if (point.getName().equals(ghostPoint.getName())) {
                if (isRendered()) {
                    series.removePoint(point);
                    update();
                    ghostPoint.setRemoved(true);
                } else {
                    removePointsOnNextRender = true;
                    if (!pointsToRemove.contains(point)) {
                        pointsToRemove.add(point);
                        ghostPoint.setRemoved(true);
                    }
                }
                break;
            }
        }

        for (Point point : pointsToAdd) {
            if (point.getName().equals(ghostPoint.getName())) {
                pointsToAdd.remove(point);
                break;
            }
        }
    }

    protected void update() {
        series.update(this.series);
    }

    public void firstTimeOpen() {
        firstSelection = true;
        redraw();
    }

    private static native String getId(JavaScriptObject point) /*-{
		return point.id;
    }-*/;

    protected void zoomToRoot() {
    	zoomToRoot(series.getNativeSeries());
    }
    
    protected native void zoomToRoot(JavaScriptObject series)/*-{
    	if(series.rootNode != ""){
	    	series.drillToNode("")
    	}
    }-*/;
    
    protected static native void zoomToPoint(JavaScriptObject series, String id) /*-{
		try {
			if(series.nodeMap.includes[id]){
				series.drillToNode(id);
			}
		} catch (err) {
		}
    }-*/;
    
    protected static native void zoomToPoint(JavaScriptObject series, JavaScriptObject point) /*-{
		try {
			series.drillToNode(point.drillId);
		} catch (err) {
		}
	}-*/;

    protected static native String getIdOfSelectedLevel(JavaScriptObject series)/*-{
		try {
			return series.rootNode;
		} catch (err) {
			return ""
		}
    }-*/;
    
    protected static native String getNameOfSelectedLevel(JavaScriptObject series)/*-{
		try {
			var rootNodeId = series.rootNode;
			return series.nodeMap[rootNodeId].name;
		} catch (err) {
			return ""
		}
    }-*/;


    public void registerObserver(TreeMapChanged observer) {
        observers.add(observer);
    }
    
    public void addSliderObserver(ESASkyMultiRangeSlider slider) {
    	slider.registerValueChangeObserver(new EsaSkyMultiRangeSliderObserver() {

			@Override
			public void onValueChange(double low, double high) {
				TreeMap.this.onSliderValueChange(low, high);
			}
		});
    }
    
    public void onSliderValueChange(double low, double high) {
    	if(hasSlider) {
	    	sliderValueLow = low;
	    	sliderValueHigh = high;
			double highWavelength = ESASkyColors.valueToWaveLength(low);
			double lowWavelength = ESASkyColors.valueToWaveLength(high);
			
			boolean anyPointsAreShown = false;
			
	    	for(Point point : series.getPoints()) {
	    		PointInformation pointInformation = allPoints.get(point.getText());
	    		if(pointInformation != null) {
	    		    boolean shouldBeShown = shouldBeShown(highWavelength, lowWavelength, pointInformation);
	    			Point pointInSeries = getPoint(pointInformation.descriptor);
	    			if(!shouldBeShown) {
	    				pointInSeries.update(0, false);
	    			}else if(shouldBeShown) {
	    				pointInSeries.update(logCount(pointInformation.count), false);
	    				anyPointsAreShown = true;
	    			}
	    		}
	    	}
	    	
	    	if(anyPointsAreShown) {
	    		removeGhostPoint();
	    	}else {
	    		addNotInRangeGhostPoint();
	    	}
	    	update();
    	}
    }


    private boolean shouldBeShown(double highWavelength, double lowWavelength,
            PointInformation pointInformation) {
        if(pointInformation.descriptor.getWavelengths() == null) {
            return false;
        }
        for(WavelengthDescriptor wavelength : pointInformation.descriptor.getWavelengths()) {
        	
        	ArrayList<Double> waveLengthRange = wavelength.getRange();
        	
        	if(waveLengthRange.size() > 0) {
        		if(lowWavelength <= waveLengthRange.get(1) && highWavelength >= waveLengthRange.get(0)
        				&& pointInformation.count > 0) {
        			return true;
        		}
        	}
        }
        return false;
    }


	public boolean isHasSlider() {
		return hasSlider;
	}


	public void setHasSlider(boolean hasSlider) {
		this.hasSlider = hasSlider;
	}
    
}