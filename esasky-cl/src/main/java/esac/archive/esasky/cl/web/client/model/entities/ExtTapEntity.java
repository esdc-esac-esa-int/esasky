package esac.archive.esasky.cl.web.client.model.entities;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.URL;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Image;

import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.ExtTapDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.MetadataDescriptor;
import esac.archive.esasky.ifcs.model.shared.ColumnType;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.Modules;
import esac.archive.esasky.cl.web.client.callback.MetadataCallback;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPopEvent;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPushEvent;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.PolygonShape;
import esac.archive.esasky.cl.web.client.model.SelectableImage;
import esac.archive.esasky.cl.web.client.model.Shape;
import esac.archive.esasky.cl.web.client.model.ShapeId;
import esac.archive.esasky.cl.web.client.model.SourceShape;
import esac.archive.esasky.cl.web.client.model.TapMetadata;
import esac.archive.esasky.cl.web.client.model.TapRowList;
import esac.archive.esasky.cl.web.client.query.TAPExtTapService;
import esac.archive.esasky.cl.web.client.query.TAPMetadataObservationService;
import esac.archive.esasky.cl.web.client.query.TAPUtils;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.CoordinateUtils;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.SourceConstant;
import esac.archive.esasky.cl.web.client.view.resultspanel.ExtTapTablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.GeneralJavaScriptObject;
import esac.archive.esasky.cl.web.client.view.resultspanel.ITablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorTablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.stylemenu.StylePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.stylemenu.StylePanel.StylePanelCallback;;

public class ExtTapEntity implements GeneralEntityInterface {

    private final Resources resources = GWT.create(Resources.class);

    public interface Resources extends ClientBundle {

        @Source("galaxy_light.png")
        @ImageOptions(flipRtl = true)
        ImageResource tabDefaultImagingIcon();

        @Source("galaxy_dark.png")
        @ImageOptions(flipRtl = true)
        ImageResource tabSelectedImagingIcon();

    }
    
    protected DefaultEntity defaultEntity;
    protected IShapeDrawer drawer;
    protected IShapeDrawer combinedDrawer;
    private ExtTapDescriptor descriptor;
    private boolean willShowMOC = false;
    private MOCEntity mocEntity;
    
    private Timer sourceLimitNotificationTimer = new Timer() {

		@Override
		public void run() {
			CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPopEvent(getEsaSkyUniqId() + "SourceLimit"));
		}
	};
    
	public ExtTapEntity(ExtTapDescriptor descriptor, CountStatus countStatus,
			SkyViewPosition skyViewPosition, String esaSkyUniqId, Long lastUpdate, EntityContext context) {
		
		JavaScriptObject footprints = AladinLiteWrapper.getAladinLite().createOverlay(esaSkyUniqId,
				descriptor.getHistoColor());
		
		JavaScriptObject catalogue = AladinLiteWrapper.getAladinLite().createCatalog(
				esaSkyUniqId, CombinedSourceFootprintDrawer.DEFAULT_SOURCE_SIZE, descriptor.getHistoColor());
		
		combinedDrawer = new CombinedSourceFootprintDrawer(catalogue, footprints, shapeBuilder);
		drawer = combinedDrawer;
//		this.mocEntity = new MOCEntity(descriptor);
		
		defaultEntity = new DefaultEntity(descriptor, countStatus, skyViewPosition, esaSkyUniqId, lastUpdate,
				context, drawer, TAPMetadataObservationService.getInstance());
		this.descriptor = descriptor;
	}
    	
	protected ShapeBuilder shapeBuilder = new ShapeBuilder() {
    	@Override
    	public Shape buildShape(int rowId, TapRowList rowList, GeneralJavaScriptObject row) {
    		String stcs;
    		if(Modules.useTabulator) {
    			stcs = row.invokeFunction("getData").getStringProperty(getDescriptor().getTapSTCSColumn());
    			if(stcs.toUpperCase().startsWith("POSITION")) {
    				String ra = row.invokeFunction("getData").getStringProperty(getDescriptor().getTapRaColumn());
    				String dec = row.invokeFunction("getData").getStringProperty(getDescriptor().getTapDecColumn());
    				String sourceName = row.invokeFunction("getData").getStringProperty(getDescriptor().getUniqueIdentifierField());
    				
    				return catalogBuilder(rowId, ra, dec, sourceName);
    			}
			} else {
				stcs = (String) getTAPDataByTAPName(rowList, rowId, descriptor.getTapSTCSColumn());
				if(stcs.toUpperCase().startsWith("POSITION")) {
		            Double ra = Double.parseDouble(getTAPDataByTAPName(rowList, rowId,
		                    getDescriptor().getTapRaColumn()).toString());

		            Double dec = Double.parseDouble(getTAPDataByTAPName(rowList, rowId,
		            		getDescriptor().getTapDecColumn()).toString());
					return catalogBuilder(rowId, ra.toString(), dec.toString(), ((String) getTAPDataByTAPName(rowList, rowId,
		                    descriptor.getUniqueIdentifierField())).toString());
				}
			}
    		
    		stcs = makeSureSTCSHasFrame(stcs);
    		PolygonShape polygon = new PolygonShape();
    		polygon.setShapeId(rowId);
    		polygon.setStcs(stcs);
    		polygon.setJsObject(AladinLiteWrapper.getAladinLite().createFootprintFromSTCS(
    				polygon.getStcs(), rowId));
    		return polygon;
    	}
    };
    
	public SourceShape catalogBuilder(int shapeId, String ra, String dec, String sourceName) {
            SourceShape mySource = new SourceShape();
            mySource.setShapeId(shapeId);
            
			mySource.setRa(ra);
			mySource.setDec(dec);
			mySource.setSourceName(sourceName);

			Map<String, String> details = new HashMap<String, String>();

            details.put(SourceConstant.SOURCE_NAME, mySource.getSourceName());

            details.put(EsaSkyWebConstants.SOURCE_TYPE,
                    EsaSkyWebConstants.SourceType.CATALOGUE.toString());
            details.put(SourceConstant.CATALOGE_NAME, getEsaSkyUniqId());
            details.put(SourceConstant.IDX, Integer.toString(shapeId));

            details.put(SourceConstant.EXTRA_PARAMS, null);
            
            mySource.setJsObject(AladinLiteWrapper.getAladinLite().newApi_createSourceJSObj(
                    mySource.getRa(), mySource.getDec(), details, shapeId));
            return mySource;
    }
    
    private String makeSureSTCSHasFrame(String input) {
    	String stcs = input.toUpperCase();
    	if(stcs.contains("J2000")  || stcs.contains("ICRS")) {
    		return stcs;
    	}
    	if(stcs.contains("POLYGON")){
    		stcs = stcs.replaceAll("POLYGON", "POLYGON J2000");
    	}
    	if(stcs.contains("CIRCLE")){
    		stcs = stcs.replaceAll("CIRCLE", "CIRCLE J2000");
    	}
    	return stcs;
    	
    }

    
    @Override
    public SelectableImage getTypeIcon() {
        return new SelectableImage(resources.tabDefaultImagingIcon(), resources.tabSelectedImagingIcon());
    }
    
    @Override
    public String getMetadataAdql() {
        return TAPExtTapService.getInstance().getMetadataAdql(getDescriptor());
    }
    
    public boolean showMocData() {
    	if(CoordinateUtils.getCenterCoordinateInJ2000().getFov() < descriptor.getFovLimit()) {
    		return false;
    	}
    	return true;
    }
    
	public class MocBuilder implements ShapeBuilder{

		@Override
		public Shape buildShape(int rowId, TapRowList rowList, GeneralJavaScriptObject row) {
			if(Modules.useTabulator) {
				PolygonShape shape = new PolygonShape();
				String stcs = row.invokeFunction("getData").getStringProperty(getDescriptor().getTapSTCSColumn());
				shape.setStcs(stcs);
				shape.setJsObject(AladinLiteWrapper.getAladinLite().createFootprintFromSTCS(shape.getStcs()));
				return shape;
			} else {
				PolygonShape shape = new PolygonShape();
		    	shape.setStcs((String)getTAPDataByTAPName(rowList, rowId, descriptor.getTapSTCSColumn()));
				shape.setJsObject(AladinLiteWrapper.getAladinLite().createFootprintFromSTCS(shape.getStcs()));
				return shape;
			}
		}
	}
    
    @Override
    public void fetchData(final ITablePanel tablePanel) {
    	if(Modules.useTabulator) {
    		willShowMOC = true;
    		drawer.removeAllShapes();
    		if(showMocData()) {
    			drawer = new MocDrawer(descriptor.getHistoColor());
    			defaultEntity.setDrawer(drawer);
    			getMocMetadata(tablePanel);
    		} else {
    			drawer = combinedDrawer;
    			defaultEntity.setDrawer(drawer);
    			
    			clearAll();
    			tablePanel.insertData(null, TAPUtils.getExtTAPQuery(URL.encodeQueryString(getMetadataAdql()), getDescriptor()));
    		}
    		
    	} else {
    		if(showMocData()) {
    			drawer.removeAllShapes();
    			drawer = new MocDrawer(descriptor.getHistoColor());
    			defaultEntity.setDrawer(drawer);
    			getMocMetadata(tablePanel);
    		} else {
    			drawer.removeAllShapes();
    			drawer = combinedDrawer;
    			defaultEntity.setDrawer(drawer);
    			getData(tablePanel);
    		}
    	}
    }
    
	@Override
	public void fetchDataWithoutMOC(ITablePanel tablePanel) {
		drawer.removeAllShapes();
		drawer = combinedDrawer;
		defaultEntity.setDrawer(drawer);
		getData(tablePanel);
		
	}
    
    private void getData(final ITablePanel tablePanel) {
    	
    	Scheduler.get().scheduleFinally(new ScheduledCommand() {
        	
        	@Override
        	public void execute() {
        		clearAll();
        		final String debugPrefix = "[fetchData][" + getDescriptor().getGuiShortName() + "]";
        		// Get Query in ADQL format.
        		final String adql = getMetadataAdql();
        		
        		String url = TAPUtils.getExtTAPQuery(URL.encodeQueryString(adql),descriptor);
        		
        		Log.debug(debugPrefix + "Query [" + url + "]");
        		
        		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
        		try {
        			builder.sendRequest(null, 
        					new MetadataCallback(tablePanel, 
        							adql, 
        							TextMgr.getInstance().getText("MetadataCallback_retrievingMissionData")
        									.replace("$NAME$", getDescriptor().getGuiLongName())
        							)
        					);
        			
        		} catch (RequestException e) {
        			Log.error(e.getMessage());
        			Log.error(debugPrefix + "Error fetching JSON data from server");
        		}
        	}
        });
    }
    
    private void getMocMetadata(final ITablePanel tablePanel) {
 		final String debugPrefix = "[fetchMoc][" + getDescriptor().getGuiShortName() + "]";

         tablePanel.clearTable();
         String adql = TAPExtTapService.getInstance().getMetadataAdql(getDescriptor(), true);
         
         String url = TAPUtils.getTAPQuery(URL.decodeQueryString(adql), EsaSkyConstants.JSON).replaceAll("#", "%23");

         Log.debug(debugPrefix + "Query [" + url + "]");
         RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
         try {
             builder.sendRequest(null,
                 new MetadataCallback(tablePanel, adql, TextMgr.getInstance().getText("JsonRequestCallback_retrievingMOC"), new MetadataCallback.OnComplete() {
                	 
                 	@Override
                 	public void onComplete() {
                 		tablePanel.setEmptyTable(TextMgr.getInstance().getText("commonObservationTablePanel_showingGlobalSkyCoverage"));
                 	}
                 }));
         } catch (RequestException e) {
             Log.error(e.getMessage());
             Log.error("[getMocMetadata] Error fetching JSON data from server");
         }

         tablePanel.setADQLQueryUrl("");
        
    }
    
    public void setDescriptorMetaData() {
    	List<MetadataDescriptor> metaList = new LinkedList<>();
    	int i = 0;
    	for(TapMetadata tmd : getMetadata().getMetadata()) {
    		MetadataDescriptor metaDatadescriptor = new MetadataDescriptor();
    		metaDatadescriptor.setTapName(tmd.getName());
    		if(tmd.getName().equals("access_url")) {
    			metaDatadescriptor.setType(ColumnType.DATALINK);
    			metaDatadescriptor.setIndex(0);
    		}else {
    			metaDatadescriptor.setType(ColumnType.valueOf(tmd.getDatatype().toUpperCase()));
    			metaDatadescriptor.setIndex(i++);
    		}
    		metaDatadescriptor.setLabel(tmd.getName());
    		metaDatadescriptor.setVisible(true);
    		metaDatadescriptor.setMaxDecimalDigits(4);
    		metaDatadescriptor.setDescription(tmd.getDescription());
    		metaList.add(metaDatadescriptor.getIndex(),metaDatadescriptor);
    	}
    	descriptor.setMetadata(metaList);
    }

	@Override
	public void setSizeRatio(double size) {
		defaultEntity.setSizeRatio(size);
	}
	
	@Override
	public double getSize() {
		return defaultEntity.getSize();
	}

	@Override
	public void removeAllShapes() {
		defaultEntity.removeAllShapes();
	}

	@Override
	public void addShapes(TapRowList rowList, GeneralJavaScriptObject javaScriptObject) {
		drawer.addShapes(rowList, javaScriptObject);
		if(!Modules.useTabulator) {
			if(rowList.getData().size() >= getSourceLimit()) {
				if(sourceLimitNotificationTimer.isRunning()) {
					sourceLimitNotificationTimer.run();
				}
				String sourceLimitDescription = TextMgr.getInstance().getText(getSourceLimitDescription()).replace("$sourceLimit$", getSourceLimit() + "");
				CommonEventBus.getEventBus().fireEvent( 
						new ProgressIndicatorPushEvent(getEsaSkyUniqId() + "SourceLimit", sourceLimitDescription, true));
				sourceLimitNotificationTimer.schedule(6000);
			}
		}
	}
	
	public int getSourceLimit() {
		return descriptor.getSourceLimit();
	}

	public String getSourceLimitDescription() {
		return "sourceLimitDescription_EXTTAP";
	}
	
	@Override
	public void selectShapes(Set<ShapeId> shapes) {
		defaultEntity.selectShapes(shapes);
	}

	@Override
	public void deselectShapes(Set<ShapeId> shapes) {
		defaultEntity.deselectShapes(shapes);
	}

	@Override
	public void deselectAllShapes() {
		defaultEntity.deselectAllShapes();
	}

	@Override
	public void showShape(int rowId) {
		defaultEntity.showShape(rowId);
	}

	@Override
	public void showShapes(List<Integer> shapeIds) {
		defaultEntity.showShapes(shapeIds);
	}

	@Override
	public void showAndHideShapes(List<Integer> rowIdsToShow, List<Integer> rowIdsToHide) {
		defaultEntity.showAndHideShapes(rowIdsToShow, rowIdsToHide);
	}
	
	@Override
	public void setShapeBuilder(ShapeBuilder shapeBuilder) {
		defaultEntity.setShapeBuilder(shapeBuilder);
	}

	@Override
	public void hideShape(int rowId) {
		defaultEntity.hideShape(rowId);
	}

	@Override
	public void hideShapes(List<Integer> shapeIds) {
		defaultEntity.hideShapes(shapeIds);
	}
	
	@Override
	public void hideAllShapes() {
		defaultEntity.hideAllShapes();
	}

	@Override
	public void hoverStart(int hoveredRowId) {
		defaultEntity.hoverStart(hoveredRowId);
	}

	@Override
	public void hoverStop(int hoveredRowId) {
		defaultEntity.hoverStop(hoveredRowId);
	}

	@Override
	public SkyViewPosition getSkyViewPosition() {
		return defaultEntity.getSkyViewPosition();
	}

	@Override
	public void setSkyViewPosition(SkyViewPosition skyViewPosition) {
		defaultEntity.setSkyViewPosition(skyViewPosition);
	}

	@Override
	public String getHistoLabel() {
		return defaultEntity.getHistoLabel();
	}

	@Override
	public void setHistoLabel(String histoLabel) {
		defaultEntity.setHistoLabel(histoLabel);
	}

	@Override
	public String getEsaSkyUniqId() {
		return defaultEntity.getEsaSkyUniqId();
	}

	@Override
	public void setEsaSkyUniqId(String esaSkyUniqId) {
		defaultEntity.setEsaSkyUniqId(esaSkyUniqId);
	}

	@Override
	public TapRowList getMetadata() {
		return defaultEntity.getMetadata();
	}

	@Override
	public void setMetadata(TapRowList metadata) {
		defaultEntity.setMetadata(metadata);
	}

	@Override
	public Long getLastUpdate() {
		return defaultEntity.getLastUpdate();
	}

	@Override
	public void setLastUpdate(Long lastUpdate) {
		defaultEntity.setLastUpdate(lastUpdate);
	}

	@Override
	public String getTabLabel() {
		return getDescriptor().getGuiLongName();
	}
	
	@Override
	public int getTabNumber() {
		return defaultEntity.getTabNumber();
	}

	@Override
	public void setTabNumber(int number) {
		defaultEntity.setTabNumber(number);
	}
	
	@Override
	public Image getTypeLogo() {
		return defaultEntity.getTypeLogo();
	}

	@Override
	public Object getTAPDataByTAPName(TapRowList tapRowList, int rowIndex, String tapName) {
		return defaultEntity.getTAPDataByTAPName(tapRowList, rowIndex, tapName);
	}

	@Override
	public Double getDoubleByTAPName(TapRowList tapRowList, int rowIndex, String tapName, Double defaultValue) {
		return defaultEntity.getDoubleByTAPName(tapRowList, rowIndex, tapName, defaultValue);
	}

	@Override
	public ExtTapDescriptor getDescriptor() {
		return descriptor;
	}

	@Override
	public CountStatus getCountStatus() {
		return defaultEntity.getCountStatus();
	}

	@Override
	public EntityContext getContext() {
		return defaultEntity.getContext();
	}

	@Override
	public void clearAll() {
		defaultEntity.clearAll();
	}

	@Override
	public String getColor() {
		return defaultEntity.getColor();
	}

	@Override
	public void setColor(String color) {
		defaultEntity.setColor(color);
	}

	@Override
	public ITablePanel createTablePanel() {
		if(Modules.useTabulator) {
			return new TabulatorTablePanel(getTabLabel(), getEsaSkyUniqId(), this);
		} else {
			return new ExtTapTablePanel(getTabLabel(), getEsaSkyUniqId(), this);
		}
	}

	@Override
	public boolean isSampEnabled() {
		return true;
	}

	@Override
	public boolean isRefreshable() {
		return defaultEntity.isRefreshable();
	}

	@Override
	public boolean hasDownloadableDataProducts() {
		return false;
	}

    @Override
    public boolean isCustomizable() {
    	return defaultEntity.isCustomizable();
    }

	@Override
	public void refreshData(ITablePanel tablePanel) {
		fetchData(tablePanel);
	}

	@Override
	public void coneSearch(ITablePanel tablePanel, SkyViewPosition conePos) {
		// TODO Auto-generated method stub		
	}
	


	@Override
	public StylePanel createStylePanel() {
		return new StylePanel(getEsaSkyUniqId(), getTabLabel(), getColor(), getSize(), 
				null, null, null, null, null, null, null, new StylePanelCallback() {
					
					@Override
					public void onShapeSizeChanged(double value) {
						setSizeRatio(value);
					}
					
					@Override
					public void onShapeColorChanged(String color) {
						getDescriptor().setHistoColor(color);
					}
					
					@Override
					public void onShapeChanged(String shape) {
					}
					
					@Override
					public void onOrbitScaleChanged(double value) {
					}
					
					@Override
					public void onOrbitColorChanged(String color) {
					}
					
					@Override
					public void onArrowScaleChanged(double value) {
					}
					
					@Override
					public void onArrowColorChanged(String color) {
					}
					
					@Override
					public void onArrowAvgCheckChanged(boolean checkedOne, boolean checkedTwo) {
					}
				});
	}

}
