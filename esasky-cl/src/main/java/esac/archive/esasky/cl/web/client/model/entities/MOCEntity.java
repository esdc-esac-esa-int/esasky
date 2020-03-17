package esac.archive.esasky.cl.web.client.model.entities;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.URL;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Image;

import esac.archive.esasky.ifcs.model.coordinatesutils.Coordinate;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.MetadataDescriptor;
import esac.archive.esasky.ifcs.model.shared.ColumnType;
import esac.archive.esasky.ifcs.model.shared.ESASkyResultMOC;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;
import esac.archive.esasky.cl.web.client.callback.MOCCallback;
import esac.archive.esasky.cl.web.client.callback.MetadataCallback;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.SelectableImage;
import esac.archive.esasky.cl.web.client.model.ShapeId;
import esac.archive.esasky.cl.web.client.model.TapRowList;
import esac.archive.esasky.cl.web.client.query.TAPMetadataCatalogueService;
import esac.archive.esasky.cl.web.client.query.TAPMetadataObservationService;
import esac.archive.esasky.cl.web.client.query.TAPUtils;
import esac.archive.esasky.cl.web.client.repository.MocRepository;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.CoordinateUtils;
import esac.archive.esasky.cl.web.client.view.resultspanel.AbstractTableFilterObserver;
import esac.archive.esasky.cl.web.client.view.resultspanel.ITablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.stylemenu.StylePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.ClosingObserver;
import esac.archive.esasky.cl.web.client.view.resultspanel.GeneralJavaScriptObject;

public class MOCEntity implements GeneralEntityInterface {

	final int DISPLAY_LIMIT = 2000;
	
    private final Resources resources = GWT.create(Resources.class);
    private ESASkyResultMOC moc = new ESASkyResultMOC(2,-1);
    private ITablePanel tablePanel;
    private TapRowList data;
    protected DefaultEntity defaultEntity;
    protected IShapeDrawer drawer;
    protected IShapeDrawer combinedDrawer;
    private IDescriptor descriptor;
    private JavaScriptObject overlay;
    private int currentDataOrder = 8; 
    private int currentDisplayOrder; 
    private int currentMinOrder; 
    private boolean shouldBeShown = true;
    private GeneralEntityInterface parentEntity;
    
    private AbstractTableFilterObserver filterObserver;
    Map<Integer, Map<Long, Integer>> countMap = new HashMap<Integer, Map<Long, Integer>>();
    
	
	Timer refreshTimer = new Timer() {
		
		long lastChangeTime = (long) 0;
		long timoutInMillis = 2000;
		
		@Override
		public void run() {
			if(System.currentTimeMillis() - lastChangeTime > timoutInMillis && shouldBeShown) {
				refreshMOC();
			}
		}
		
		@Override
		public void schedule(int delayMillis) {
			super.schedule(delayMillis);
			lastChangeTime = System.currentTimeMillis();
		}
	};

    public interface Resources extends ClientBundle {

        @Source("galaxy_light.png")
        @ImageOptions(flipRtl = true)
        ImageResource tabDefaultImagingIcon();

        @Source("galaxy_dark.png")
        @ImageOptions(flipRtl = true)
        ImageResource tabSelectedImagingIcon();

    }
    
	public MOCEntity(IDescriptor descriptor, CountStatus countStatus, GeneralEntityInterface parent) {
		
		overlay = null;
		drawer = null;
		this.descriptor = descriptor;
		
		defaultEntity = new DefaultEntity(descriptor, countStatus , new SkyViewPosition(new Coordinate(0, 0), 0.0), "MOC", (long) 0,
				EntityContext.MOC, drawer, TAPMetadataObservationService.getInstance());
		
		parentEntity = parent;
		
		MocRepository.getInstance().addMocEntity(this);
		
//		MOVED to MOCRepo
//		CommonEventBus.getEventBus().addHandler(AladinLiteMOCIpixClickedEvent.TYPE, new AladinLiteMOCIpixClickedEventHandler () {
//
//			@Override
//			public void onMOCClicked(AladinLiteMOCIpixClickedEvent event) {
//				MOCClicked(event.getNorder(), event.getIpix(), event.getScreenX(), event.getScreenY());
//			}
//		});
		
		filterObserver = new AbstractTableFilterObserver() {
			
			@Override
			public void filterChanged(Map<String, String> tapFilters) {
				refreshTimer.schedule(3000);
			}
		};
		
	}
	
	public void setTablePanel(ITablePanel panel) {
		if(this.tablePanel != panel) {
			this.tablePanel = panel;
			panel.registerFilterObserver(filterObserver);
		}
	}
    	
	
	@Override
    public SelectableImage getTypeIcon() {
        return new SelectableImage(resources.tabDefaultImagingIcon(), resources.tabSelectedImagingIcon());
    }
    
    @Override
    public String getMetadataAdql() {
        //TODO
    	return null;
    }
    
    
    public void updateCountMap() {
    	countMap = new HashMap<Integer, Map<Long, Integer>>();
    	
    	int index = 0;
    	double fov = AladinLiteWrapper.getInstance().getFovDeg();
    	int minOrder = ESASkyResultMOC.getMinOrderFromFoV(fov);
    	int maxOrder = ESASkyResultMOC.getMaxOrderFromFoV(fov);
    	
		moc.populateCountMap(countMap, index, minOrder, maxOrder);
    	
    }
    
    public String MOCClicked(final String orders, final String ipixels, int screenX, int screenY) {
    	Log.debug("[MOCEntity] MOCClicked " + orders + ", " + ipixels );
    	String tooltipText = "";
    	String[] orderArray = orders.split(",");
    	String[] ipixArray = ipixels.split(",");
    	for(int i = 0; i < orderArray.length; i++) {
    	
    		int order = Integer.parseInt(orderArray[i]);
    		int ipix = Integer.parseInt(ipixArray[i]);
	    	try {
				int count = countMap.get(order).get((long)ipix);
				
				
				if(count > 0) {
		
					tooltipText += descriptor.getMission() + ": " + count + "\n";
					
				}
	    	}catch(Exception e){
	    		//Catch here if the countMap doesn't have that order or ipix
	    	}
    	}
		return tooltipText;
		
//		MOCTooltip tooltip = new MOCTooltip(screenX, screenY);
//		tooltip.registerObserver(new MOCTooltipObserver() {
//			
//			@Override
//			public void onSplit() {
//				sendSplitQuery(order, ipix);
//				
//			}
//			
//			@Override
//			public void onLoad() {
//				sendLoadQuery(order, ipix);
//				
//			}
//		});
//		
//		tooltip.show(tooltipText);
		
    }
    
    public void sendLoadQuery(int order, int ipix) {
    	final String debugPrefix = "[fetchMoc][" + getDescriptor().getGuiShortName() + "]";

        String adql = TAPMetadataCatalogueService.getInstance().getMetadataAdqlFromIpix(descriptor, order, ipix);
        
    	String filter = tablePanel.getFilterString();
        adql += filter;
    	
    	String url = TAPUtils.getTAPQuery(URL.decodeQueryString(adql), EsaSkyConstants.JSON).replaceAll("#", "%23");
        
        Log.debug(debugPrefix + "Query [" + url + "]");
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
        try {
            builder.sendRequest(null,
                new MetadataCallback(tablePanel, adql, TextMgr.getInstance().getText("JsonRequestCallback_retrievingMOC")));
        } catch (RequestException e) {
            Log.error(e.getMessage());
            Log.error("[getMocMetadata] Error fetching JSON data from server");
        }

    }
    
    public void sendLoadQuery() {
//    	final String debugPrefix = "[fetchMoc][" + getDescriptor().getGuiShortName() + "]";
//    	
//    	SkyViewPosition pos = CoordinateUtils.getCenterCoordinateInJ2000();

    	parentEntity.fetchDataWithoutMOC(tablePanel);
    	shouldBeShown = false;
    	clearAll();
    	updateOverlay();
    	
//        String adql = TAPMetadataCatalogueService.getInstance().getMetadataAdqlRadial(descriptor, pos, filter);
//        
//    	
//    	String url = TAPUtils.getTAPQuery(URL.decodeQueryString(adql), EsaSkyConstants.JSON).replaceAll("#", "%23");
//        
//        Log.debug(debugPrefix + "Query [" + url + "]");
//        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
//        try {
//            builder.sendRequest(null,
//                new MetadataCallback(tablePanel, adql, TextMgr.getInstance().getText("JsonRequestCallback_retrievingData"), new MetadataCallback.OnComplete() {
//					
//					@Override
//					public void onComplete() {
//						shouldBeShown = false;
//						clearMOC();
//						
//					}
//				}));
//        } catch (RequestException e) {
//            Log.error(e.getMessage());
//            Log.error("[getMocMetadata] Error fetching JSON data from server");
//        }

    }
    
    public void sendSplitQuery(int order, int ipix) {
    	
//    	final String debugPrefix = "[fetchMoc][" + getDescriptor().getGuiShortName() + "]";
//    	
//    	String pixelPart = Integer.toString(order) + "," + Integer.toString(ipix) + ", " + Integer.toString(order+2);
//    	String filter = "";
////        for(String key : tablePanel.tapFilters.keySet()) {
////			filter += " AND ";
////			filter += tablePanel.tapFilters.get(key);
////    	}
//        
//    	String adql = "SELECT esasky_q3c_moc_split('" + descriptor.getTapTable() + "'," + pixelPart +", '" + filter + "' ) as rec from dual";
//    		
//    	String url = TAPUtils.getTAPQuery(URL.decodeQueryString(adql), EsaSkyConstants.JSON).replaceAll("#", "%23");
//    	
//    	Log.debug(debugPrefix + "Query [" + url + "]");
//    	RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
//    	try {
//    		builder.sendRequest(null,
//    				new MOCAsRecordCallback(tablePanel, adql,this, TextMgr.getInstance().getText("JsonRequestCallback_retrievingData")
//    						));
//    	} catch (RequestException e) {
//    		Log.error(e.getMessage());
//    		Log.error("[getMocMetadata] Error fetching JSON data from server");
//    	}
    	
    }
    
    public void refreshMOC() {
    	clearAll();
    	
    	shouldBeShown = true;
    	int count = getCountStatus().getCount(descriptor.getMission());
    	double fov = CoordinateUtils.getCenterCoordinateInJ2000().getFov();
    	if(count > Math.pow(10, 7)) {
    		getPrecomputedMOC();
    		currentDataOrder = 8;
    		return;
    	}
    	
    	int targetOrder = ESASkyResultMOC.getTargetOrderFromFoV(fov);
    	
    	if(targetOrder == 8 && tablePanel.getTapFilters().size() == 0) {
//		if(targetOrder == 8 ) {
    		getPrecomputedMOC();
    		currentDataOrder = 8;
    	}
    	else {
    		getSplitMOC(targetOrder);
    		currentDataOrder = targetOrder;
    	}
    	
    }
    
    private void getPrecomputedMOC() {
    	final String debugPrefix = "[fetchMoc][" + getDescriptor().getGuiShortName() + "]";

//        tablePanel.clearTable();
        
        SkyViewPosition pos = CoordinateUtils.getCenterCoordinateInJ2000();
        
        String adql = "SELECT * from " + descriptor.getTapTable().replace("public", "moc_schema")  
       		+ " WHERE '1' = esasky_q3c_moc_radial_query(moc_order, moc_ipix,"
        		+ Double.toString(pos.getCoordinate().ra) + ", " +  Double.toString(pos.getCoordinate().dec) + ", " + Double.toString(pos.getFov()/2) + " )";
        
        String url = TAPUtils.getTAPQuery(URL.decodeQueryString(adql), EsaSkyConstants.JSON).replaceAll("#", "%23");

        Log.debug(debugPrefix + "Query [" + url + "]");
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
        try {
            builder.sendRequest(null,
                new MOCCallback(tablePanel, adql, this, TextMgr.getInstance().getText("JsonRequestCallback_retrievingMOC"), new MOCCallback.OnComplete() {
               	 
                	@Override
                	public void onComplete() {
//                		tablePanel.setMOCTable(TextMgr.getInstance().getText("commonObservationTablePanel_showingGlobalSkyCoverage"));
                		updateCountMap();
                		if(getTotalCount() < 2000 && getTotalCount() > 0) {
                			sendLoadQuery();
                		}
                	}
                }));
        } catch (RequestException e) {
            Log.error(e.getMessage());
            Log.error("[getMocMetadata] Error fetching JSON data from server");
        }

    }
    
    private void getSplitMOC(int order) {
    	final String debugPrefix = "[fetchMoc][" + getDescriptor().getGuiShortName() + "]";
    	SkyViewPosition pos = CoordinateUtils.getCenterCoordinateInJ2000();
    	
    	String filter = tablePanel.getFilterString();

		String adql = "SELECT " + Integer.toString(order) + " as moc_order,"
				+ "esasky_q3c_bitshift_right(q3c_ang2ipix(ra,dec), " + Integer.toString(60 - 2 * order) + ") as moc_ipix, count(*) as moc_count"
				+ " FROM " + descriptor.getTapTable() + " WHERE \'1\' = q3c_radial_query(ra,dec, "
				+ Double.toString(pos.getCoordinate().ra) + ", "  +  Double.toString(pos.getCoordinate().dec) + ", "
				+ Double.toString(pos.getFov()/2) + ")" + filter + " GROUP BY moc_ipix";
				
    	String url = TAPUtils.getTAPQuery(URL.decodeQueryString(adql), EsaSkyConstants.JSON).replaceAll("#", "%23");
    	
    	Log.debug(debugPrefix + "Query [" + url + "]");
    	RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
    	try {
    		builder.sendRequest(null,
    				new MOCCallback(tablePanel, adql, this, TextMgr.getInstance().getText("JsonRequestCallback_retrievingMOC"), new MOCCallback.OnComplete() {
	                	 
	                 	@Override
	                 	public void onComplete() {
//	                 		tablePanel.setMOCTable(TextMgr.getInstance().getText("commonObservationTablePanel_showingGlobalSkyCoverage"));
	                		if(getTotalCount() < 2000) {
	                			sendLoadQuery();
	                		}
	                 	}
    				 }));
    	} catch (RequestException e) {
    		Log.error(e.getMessage());
    		Log.error("[getMocMetadata] Error fetching JSON data from server");
    	}
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
	
	public ESASkyResultMOC getMOC(){
		return moc;
	}
	
	@Override
	public void clearAll() {
		moc = new ESASkyResultMOC(2, -1);
		updateOverlay();
	}
	
	public void replaceData(ITablePanel tablePanel, ESASkyResultMOC newData) {
		
		this.tablePanel = tablePanel;
		this.moc = newData;
		updateOverlay();
	}

	public void addData(final ITablePanel tablePanel, ESASkyResultMOC newData) {
		
		this.tablePanel = tablePanel;
		
		tablePanel.registerClosingObserver(new ClosingObserver() {
			
			@Override
			public void onClose() {
				closingPanel(tablePanel);
				
			}
		});
		
		moc.addData(newData);
		moc.updateCount();
		
		updateOverlay();
	}
	
	private void closingPanel(ITablePanel tablePanel) {
		clearAll();
		MocRepository.getInstance().removeEntity(this);
		shouldBeShown = false;
		
	}
	
	public void updateOverlay() {
		if(overlay == null) {
			String options = "{\"opacity\":0.2, \"color\":\"" + descriptor.getHistoColor() + "\"}";
			overlay = AladinLiteWrapper.getAladinLite().createQ3CMOC(options);
			AladinLiteWrapper.getAladinLite().addMOC(overlay);
//			
//			MOVED to MOCRepp
//			CommonEventBus.getEventBus().addHandler(AladinLiteFoVChangedEvent.TYPE, new AladinLiteFoVChangedEventHandler () {
//
//				@Override
//				public void onChangeEvent(AladinLiteFoVChangedEvent fovEvent) {
//					checkUpdateMOCNorder(fovEvent.getFov()); 
//					Log.debug("Total MOC count: " + Integer.toString(getTotalCount()));
//				}
//			});
		}
		
		updateCountMap();
		
//		Log.debug("CountMAp: " + Long.toString(System.currentTimeMillis() - curr));
		
		AladinLiteWrapper.getAladinLite().clearMOC(overlay);
		
		double fov = AladinLiteWrapper.getInstance().getFovDeg();
    	int minOrder = ESASkyResultMOC.getMinOrderFromFoV(fov);
    	int maxOrder = ESASkyResultMOC.getMaxOrderFromFoV(fov);
		
		String mocData = getAladinMOCString(minOrder, maxOrder);
		
		AladinLiteWrapper.getAladinLite().addMOCData(overlay, mocData);
		
	}
	
	private int getTotalCount() {
		int count = 0;
		for(Map <Long, Integer> order : countMap.values()) {
			for(int c : order.values()) {
				count += c;
			}
		}
		Log.debug("MOC count: " + Integer.toString(count));
		return count;
	}
	
	public void checkUpdateMOCNorder(double fov) {
		if(shouldBeShown) {
			int minOrder = ESASkyResultMOC.getMinOrderFromFoV(fov);
			int maxOrder = ESASkyResultMOC.getMaxOrderFromFoV(fov);
			int targetOrder = ESASkyResultMOC.getTargetOrderFromFoV(fov);
			
			if(currentDataOrder != targetOrder) {
				refreshMOC();
				currentDataOrder = targetOrder;
			}
			
			if(maxOrder != currentDisplayOrder || minOrder != currentMinOrder) {
				updateOverlay();
				currentDisplayOrder = maxOrder;
				currentMinOrder = minOrder;
			}
		}
	}
	
	private native int getMOCOrder(JavaScriptObject moc)/*-{
		return moc.currentOrder;
	}-*/;
	
	public String getAladinMOCString(int minOrder, int maxOrder) {
		
		String MOCString = "{";
		boolean firstOrder = true;
		
		for(int order = 3; order <= maxOrder; order++) {
			
			if(countMap.containsKey(order)) {
				
				if(!firstOrder) {
					MOCString += ",";
				}else {
					firstOrder = false;
				}
				
				MOCString += "\"" +  Integer.toString(order) + "\":[";
				boolean firstIpix = true;
				
				for(long ipix : countMap.get(order).keySet()) {
					if(!firstIpix) {
						MOCString += ",";
					}else {
						firstIpix = false;
					}
					MOCString += Long.toString(ipix);
				}
				
				MOCString += "]";
				
			}
		}
		
		MOCString += "}";

		return MOCString;
	}
	
	public void setDescriptorMetaData() {
		List<MetadataDescriptor> metaList = new LinkedList<>();
		
		MetadataDescriptor metaDatadescriptorNorder = new MetadataDescriptor();
		metaDatadescriptorNorder.setTapName("Norder");
		metaDatadescriptorNorder.setType(ColumnType.INTEGER);
		metaDatadescriptorNorder.setIndex(0);
		metaDatadescriptorNorder.setLabel("Norder");
		metaDatadescriptorNorder.setVisible(true);
		metaDatadescriptorNorder.setMaxDecimalDigits(0);
		metaList.add(metaDatadescriptorNorder);
		
		MetadataDescriptor metaDatadescriptorNpix = new MetadataDescriptor();
		metaDatadescriptorNpix.setTapName("Npix");
		metaDatadescriptorNpix.setType(ColumnType.LONG);
		metaDatadescriptorNpix.setIndex(1);
		metaDatadescriptorNpix.setLabel("Npix");
		metaDatadescriptorNpix.setVisible(true);
		metaDatadescriptorNpix.setMaxDecimalDigits(0);
		metaList.add(metaDatadescriptorNpix);
		
		int i = 0;
		MetadataDescriptor metaDatadescriptor = new MetadataDescriptor();
		metaDatadescriptor.setTapName("Count");
		metaDatadescriptor.setType(ColumnType.LONG);
		metaDatadescriptor.setIndex(i + 2);
		metaDatadescriptor.setLabel("Count");
		metaDatadescriptor.setVisible(true);
		metaDatadescriptor.setMaxDecimalDigits(0);
		metaList.add(metaDatadescriptor);
		i++;
		descriptor.setMetadata(metaList);
		
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
	public IDescriptor getDescriptor() {
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
	public String getColor() {
		return defaultEntity.getColor();
	}

	@Override
	public void setColor(String color) {
		defaultEntity.setColor(color);
	}

	@Override
	public ITablePanel createTablePanel() {
		//TODO
		return null;
	}

	@Override
	public boolean isSampEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRefreshable() {
		return defaultEntity.isRefreshable();
	}

	@Override
	public boolean hasDownloadableDataProducts() {
		// TODO Auto-generated method stub
		return false;
	}

    @Override
    public boolean isCustomizable() {
    	return defaultEntity.isCustomizable();
    }

	@Override
	public void addShapes(TapRowList shapeList, GeneralJavaScriptObject javaScriptObject) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fetchData(ITablePanel tablePanel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void coneSearch(ITablePanel tablePanel, SkyViewPosition conePos) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void refreshData(ITablePanel tablePanel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public StylePanel createStylePanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void fetchDataWithoutMOC(ITablePanel tablePanel) {
		// TODO Auto-generated method stub
		
	}

}
