package esac.archive.esasky.cl.web.client.model.entities;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


import com.allen_sauer.gwt.log.client.Log;
import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Image;

import esac.archive.esasky.ifcs.model.coordinatesutils.Coordinate;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.CatalogDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.MetadataDescriptor;
import esac.archive.esasky.ifcs.model.shared.ColumnType;
import esac.archive.esasky.ifcs.model.shared.ESASkyResultMOC;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;
import esac.archive.esasky.cl.web.client.callback.MOCAsRecordCallback;
import esac.archive.esasky.cl.web.client.callback.MetadataCallback;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.ShapeId;
import esac.archive.esasky.cl.web.client.model.TapRowList;
import esac.archive.esasky.cl.web.client.query.TAPCatalogueService;
import esac.archive.esasky.cl.web.client.query.TAPMOCService;
import esac.archive.esasky.cl.web.client.query.TAPObservationService;
import esac.archive.esasky.cl.web.client.query.TAPUtils;
import esac.archive.esasky.cl.web.client.repository.MocRepository;
import esac.archive.esasky.cl.web.client.status.CountObserver;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.view.resultspanel.AbstractTableFilterObserver;
import esac.archive.esasky.cl.web.client.view.resultspanel.ITablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.stylemenu.StylePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.ClosingObserver;
import esac.archive.esasky.cl.web.client.view.resultspanel.GeneralJavaScriptObject;

public class MOCEntity implements GeneralEntityInterface {

	final int DISPLAY_LIMIT = 2000;
	
    private ESASkyResultMOC moc = new ESASkyResultMOC(2,-1);
    private ITablePanel tablePanel;
    private TapRowList data;
    protected DefaultEntity defaultEntity;
    protected IShapeDrawer drawer;
    protected IShapeDrawer combinedDrawer;
    private IDescriptor descriptor;
    private GeneralJavaScriptObject overlay;
    private int currentDataOrder = 8; 
    private int currentDisplayOrder; 
    private int currentMinOrder; 
    private boolean shouldBeShown = true;
    private GeneralEntityInterface parentEntity;
    private TAPMOCService metadataService;
    
    private AbstractTableFilterObserver filterObserver;
    Map<Integer, Map<Long, Integer>> countMap = new HashMap<Integer, Map<Long, Integer>>();
    
	
	Timer refreshTimer = new Timer() {
		
//		long lastChangeTime = (long) 0;
//		long timoutInMillis = 2000;
		
		@Override
		public void run() {
			refreshMOC();
		}
		
		@Override
		public void schedule(int delayMillis) {
			super.cancel();
			super.schedule(delayMillis);
//			lastChangeTime = System.currentTimeMillis();
		}
	};

	public MOCEntity(IDescriptor descriptor, CountStatus countStatus, GeneralEntityInterface parent) {
		
		overlay = null;
		drawer = null;
		this.descriptor = descriptor;
		
		metadataService = TAPMOCService.getInstance();
		
		defaultEntity = new DefaultEntity(descriptor, countStatus , new SkyViewPosition(new Coordinate(0, 0), 0.0), "MOC",
				drawer, TAPObservationService.getInstance());
		
		parentEntity = parent;
		
		MocRepository.getInstance().addMocEntity(this);
		filterObserver = new AbstractTableFilterObserver() {
			
			@Override
			public void filterChanged(Map<String, String> tapFilters) {
				refreshTimer.schedule(2000);
			}
		};
		
	}
	
	public void setTablePanel(ITablePanel panel) {
		if(this.tablePanel != panel) {
			this.tablePanel = panel;
			panel.registerFilterObserver(filterObserver);
			
			tablePanel.registerClosingObserver(new ClosingObserver() {
				
				@Override
				public void onClose() {
					closingPanel(tablePanel);
					
				}
			});
		}
	}
    	
    
    @Override
    public String getMetadataAdql() {
    	return null;
    }
    
    
    public void updateCountMap() {
    	countMap = new HashMap<Integer, Map<Long, Integer>>();
    	
//    	int index = 0;
//    	int minOrder = MocRepository.getMinOrderFromFoV();
//    	int maxOrder = MocRepository.getMaxOrderFromFoV();
    	
//		moc.populateCountMap(countMap, index, minOrder, maxOrder);
		moc.populateCountMapAll(countMap);
    	
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

        String adql = TAPCatalogueService.getInstance().getMetadataAdqlFromIpix(descriptor, order, ipix);
        
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
    	
//        String adql = TAPCatalogueService.getInstance().getMetadataAdqlRadial(descriptor, pos, filter);
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
    
    private CountObserver countObserver = new CountObserver() {
			@Override
			public void onCountUpdate(int newCount) {
				getCountStatus().unregisterObserver(this);
				loadMOC();
			}
		};
    
    public void refreshMOC() {
    	 clearAll();
    	 checkLoadMOC();
    }
    
    public void checkLoadMOC() {
    	if (getCountStatus().hasMoved(descriptor.getMission())) {
    		if(!getCountStatus().hasObserver(countObserver)) {
    			getCountStatus().registerObserver(countObserver);
    			Log.debug("Registered");
    		}
    	} else {
    		loadMOC();
    	}
    }
    
    private void loadMOC() {
    	
    	shouldBeShown = true;
    	int count = getCountStatus().getCount(descriptor.getMission());

    	if(count > Math.pow(10, 7)) {
    		getPrecomputedMOC();
    		currentDataOrder = 8;
    		return;
    	}
    	
    	int targetOrder = MocRepository.getTargetOrderFromFoV();
    	
    	if(targetOrder == 8 && tablePanel.getTapFilters().size() == 0) {
    		getPrecomputedMOC();
    		currentDataOrder = 8;
    	}
    	else {
    		getSplitMOC(targetOrder);
    		currentDataOrder = targetOrder;
    	}
    	
    }
    
    public interface IpixMapper extends ObjectMapper<HashMap<String,Long[]>> {
    }
    
    private int getVisibleCount() {
    	
    	if(overlay != null) {
    		return AladinLiteWrapper.getAladinLite().getVisibleCountInMOC(overlay);
    	}
    	
    	return 0;
    }
    
    private void getPrecomputedMOC() {
    	final String debugPrefix = "[fetchMoc][" + getDescriptor().getGuiShortName() + "]";

        String adql = metadataService.getPrecomputedMOCAdql(descriptor);
        String url = TAPUtils.getTAPQuery(URL.decodeQueryString(adql), EsaSkyConstants.JSON).replaceAll("#", "%23");

        Log.debug(debugPrefix + "Query [" + url + "]");
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
        try {
            builder.sendRequest(null,
                new MOCAsRecordCallback(tablePanel, adql, this, TextMgr.getInstance().getText("JsonRequestCallback_retrievingMOC"), new MOCAsRecordCallback.OnComplete() {
               	 
                	@Override
                	public void onComplete() {
                		setTableCountText();
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
    
    private void setTableCountText() {
    	String text = "";
    	
    	if(getTotalCount() > Math.pow(10, 7)) {
    		 text = TextMgr.getInstance().getText("MOC_large_count_text");
    	}else {
    		text = TextMgr.getInstance().getText("MOC_count_text");
    	}
 		text = text.replace("$count$", Integer.toString(getVisibleCount()));
 		text = text.replace("$limit$", Integer.toString(descriptor.getShapeLimit()));
 		tablePanel.setPlaceholderText(text);
    }

    
    private void getSplitMOC(int order) {
    	final String debugPrefix = "[fetchMoc][" + getDescriptor().getGuiShortName() + "]";
    	
    	String filter = tablePanel.getFilterString();
    	
    	String adql;

    	if(descriptor instanceof CatalogDescriptor) {
    		adql = metadataService.getFilteredCatalogueMOCAdql(descriptor, filter);
    	}else {
    		adql = metadataService.getFilteredObservationMOCAdql(descriptor, filter);
    	}
				
    	String url = TAPUtils.getTAPQuery(URL.decodeQueryString(adql), EsaSkyConstants.JSON).replaceAll("#", "%23");
    	
    	Log.debug(debugPrefix + "Query [" + url + "]");
    	RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
    	try {
    		builder.sendRequest(null,
    				new MOCAsRecordCallback(tablePanel, adql, this, TextMgr.getInstance().getText("JsonRequestCallback_retrievingMOC"), new MOCAsRecordCallback.OnComplete() {
	                	 
	                 	@Override
	                 	public void onComplete() {
	                 		setTableCountText();
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
		
		setTablePanel(tablePanel);
		
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
	
	public void addJSON(final ITablePanel tablePanel, GeneralJavaScriptObject data) {
		
		setTablePanel(tablePanel);
		
		if(overlay == null) {
			String options = "{\"opacity\":0.2, \"color\":\"" + descriptor.getPrimaryColor() + "\", \"name\":\"" + parentEntity.getEsaSkyUniqId() + "\"}";
			overlay = (GeneralJavaScriptObject) AladinLiteWrapper.getAladinLite().createQ3CMOC(options);
			AladinLiteWrapper.getAladinLite().addMOC(overlay);
		}
		
		overlay.invokeFunction("dataFromESAJSON", data);
	}
	
	private void closingPanel(ITablePanel tablePanel) {
		
		clearAll();
		MocRepository.getInstance().removeEntity(this);
		AladinLiteWrapper.getAladinLite().removeMOC(overlay);
		overlay = null;
		shouldBeShown = false;
	}
	
	public void updateOverlay() {
		
		if(overlay == null) {
			String options = "{\"opacity\":0.2, \"color\":\"" + descriptor.getPrimaryColor() + "\", \"name\":\"" + parentEntity.getEsaSkyUniqId() + "\"}";
			overlay = (GeneralJavaScriptObject) AladinLiteWrapper.getAladinLite().createQ3CMOC(options);
			AladinLiteWrapper.getAladinLite().addMOC(overlay);
		}
		
		updateCountMap();
		
		AladinLiteWrapper.getAladinLite().clearMOC(overlay);
		
    	int minOrder = MocRepository.getMinOrderFromFoV();
    	int maxOrder = MocRepository.getMaxOrderFromFoV();
		
    	overlay.invokeFunction("setShowOrders", minOrder, maxOrder);
//		overlay.setProperty("maxShowOrder",maxOrder);
    
//		String mocData = getAladinMOCString(minOrder, maxOrder);
		String mocData = getAladinMOCStringAll();
		
		AladinLiteWrapper.getAladinLite().addMOCData(overlay, mocData);
	}
	
	private int getTotalCount() {
		int count = 0;
//		for(Map <Long, Integer> order : countMap.values()) {
//			for(int c : order.values()) {
//				count += c;
//			}
//		}
//		Log.debug("MOC count: " + Integer.toString(count));
		return count;
	}
	
	Timer updateTimer = new Timer() {
		
		@Override
		public void run() {
				Long time = System.currentTimeMillis();
				setTableCountText();	
				Log.debug("CountTIME:" + Long.toString(System.currentTimeMillis() - time));
				
				int targetOrder = MocRepository.getTargetOrderFromFoV();
				
				if(currentDataOrder < targetOrder) {
					checkLoadMOC();
					currentDataOrder = targetOrder;
					
				}else {
					currentDataOrder = targetOrder;
				}
				
		}
		
		@Override
		public void schedule(int delayMillis) {
			super.cancel();
			super.schedule(delayMillis);
		}
	};
	
	public void onFoVChanged() {

		if(shouldBeShown) {
			
			int minOrder = MocRepository.getMinOrderFromFoV();
			int maxOrder = MocRepository.getMaxOrderFromFoV();
			
			if(maxOrder != currentDisplayOrder || minOrder != currentMinOrder) {
		    	overlay.invokeFunction("setShowOrders", minOrder, maxOrder);
				currentDisplayOrder = maxOrder;
				currentMinOrder = minOrder;
				
			}
		}
	}

	public void onMove() {
		
		if(shouldBeShown) {
			updateTimer.schedule(300);
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

	public String getAladinMOCStringAll() {
		
		String MOCString = "{";
		boolean firstOrder = true;
		
		for(int order = 3; order <= 14; order++) {
			
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
	
	public boolean isShouldBeShown() {
		return shouldBeShown;
	}

	public void setShouldBeShown(boolean shouldBeShown) {
		this.shouldBeShown = shouldBeShown;
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
	public void setPrimaryColor(String color) {
		defaultEntity.setPrimaryColor(color);
	}

	@Override
	public ITablePanel createTablePanel() {
		return null;
	}

	@Override
	public boolean isSampEnabled() {
		return false;
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
		
	}

    @Override
    public String getShapeType() {
        return defaultEntity.getShapeType();
    }

    @Override
    public void setShapeType(String shapeType) {
    }

}
