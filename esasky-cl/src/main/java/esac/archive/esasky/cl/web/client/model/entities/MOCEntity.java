package esac.archive.esasky.cl.web.client.model.entities;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


import com.allen_sauer.gwt.log.client.Log;
import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Image;

import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.CatalogDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.MetadataDescriptor;
import esac.archive.esasky.ifcs.model.shared.ColumnType;
import esac.archive.esasky.ifcs.model.shared.ESASkyResultMOC;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.AladinShape;
import esac.archive.esasky.cl.web.client.callback.MOCAsRecordCallback;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.TapRowList;
import esac.archive.esasky.cl.web.client.query.TAPMOCService;
import esac.archive.esasky.cl.web.client.query.TAPUtils;
import esac.archive.esasky.cl.web.client.repository.MocRepository;
import esac.archive.esasky.cl.web.client.status.CountObserver;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.NumberFormatter;
import esac.archive.esasky.cl.web.client.view.resultspanel.TableFilterObserver;
import esac.archive.esasky.cl.web.client.view.resultspanel.ITablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.stylemenu.StylePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.ClosingObserver;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;

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
    private int currentVisibleCount = 0;
    private boolean shouldBeShown = true;
    private GeneralEntityInterface parentEntity;
    private TAPMOCService metadataService;
    private boolean globalMinMaxLoaded = false;
    private boolean filterRequested = false;
    private boolean loadMOCRequested = false;
    private boolean freshLoad = true;
    
    private TableFilterObserver filterObserver;
    Map<Integer, Map<Long, Integer>> countMap = new HashMap<Integer, Map<Long, Integer>>();
    
    private CountObserver countObserver = new CountObserver() {
		@Override
		public void onCountUpdate(int newCount) {
			
			if(newCount > EsaSkyWebConstants.MOC_FILTER_LIMIT) {
				setTableCountText();
			}
			
			if(filterRequested && newCount < EsaSkyWebConstants.MOC_FILTER_LIMIT) {
				loadFilteredMOC();
				filterRequested = false;
			}

			if(loadMOCRequested && shouldBeShown) {
				loadMOC();
				loadMOCRequested = false;
			}
			
		}
	};
	
	Timer filterTimer = new Timer() {
	
		@Override
		public void run() {
			if (getCountStatus().hasMoved(descriptor.getMission())) {
	    		filterRequested = true;
	    	} else if( getCountStatus().getCount(descriptor.getMission()) < EsaSkyWebConstants.MOC_FILTER_LIMIT){
	    		filterRequested = true;
	    		loadFilteredMOC();
	    		filterRequested = false;
	    	}
		}
		
		@Override
		public void schedule(int delayMillis) {
			super.cancel();
			super.schedule(delayMillis);
		}
	};
	

	public MOCEntity(IDescriptor descriptor, CountStatus countStatus, GeneralEntityInterface parent, DefaultEntity defaultEntity) {
		
		overlay = null;
		drawer = null;
		this.descriptor = descriptor;
		
		metadataService = TAPMOCService.getInstance();
		this.defaultEntity = defaultEntity;
		parentEntity = parent;
		
		MocRepository.getInstance().addMocEntity(this);
		filterObserver = new TableFilterObserver() {
			
			@Override
			public void filterChanged(Map<String, String> tapFilters) {
				filterTimer.schedule(2000);
			}
		};
		
		getCountStatus().registerObserver(countObserver);
		
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
    	
    
    
    public String MOCClicked(final String orders, final String ipixels, String counts) {
    	Log.debug("[MOCEntity] MOCClicked " + orders + ", " + ipixels );
    	String tooltipText = "";
    	String[] orderArray = orders.split(",");
    	String[] ipixArray = ipixels.split(",");
    	String[] countsArray = counts.split(",");
    	for(int i = 0; i < orderArray.length; i++) {
    	
    		int order = Integer.parseInt(orderArray[i]);
    		int ipix = Integer.parseInt(ipixArray[i]);
    		int count = Integer.parseInt(countsArray[i]);
	    	try {
				
				if(count > 0) {
		
					tooltipText += descriptor.getMission() + ": Order: " + order + " Ipix: " + ipix + " Count: " + count + "<br>\n";
					
				}
	    	}catch(Exception e){
	    		//Catch here if the countMap doesn't have that order or ipix
	    	}
    	}
		return tooltipText;
		
    }
    
    public void sendLoadQuery() {

		GeneralJavaScriptObject visibleIpixels = (GeneralJavaScriptObject)AladinLiteWrapper.getAladinLite().getVisiblePixelsInMOC(overlay, MocRepository.getMinOrderFromFoV(), false);
    	
    	String whereQuery = metadataService.getVisibleWhereQuery(descriptor, visibleIpixels, tablePanel.getFilterString());
    	((EsaSkyEntity) parentEntity).fetchDataWithoutMOC(whereQuery);
    	shouldBeShown = false;
    	clearAll();
    	updateOverlay();
    	

    }
    
    public void refreshMOC() {
    	if(shouldBeShown) {
    		freshLoad = true;
    		clearAll();
    		checkLoadMOC();
    	}
    }

    public void loadFilteredMOC() {
    	if(shouldBeShown) {
    		GeneralJavaScriptObject visibleIpixels = (GeneralJavaScriptObject)AladinLiteWrapper.getAladinLite().getVisiblePixelsInMOC(overlay, MocRepository.getMinOrderFromFoV(), false);
    		
    		String adql = "";
    		if(tablePanel.getFilterString().length() > 0) {
		    	if(visibleIpixels.jsonStringify().length() > 2) {
	
			    	if(descriptor instanceof CatalogDescriptor) {
		        		adql = metadataService.getFilteredCatalogueMOCAdql(descriptor,visibleIpixels, tablePanel.getFilterString());
			    	}else {
			    		adql = metadataService.getFilteredObservationMOCAdql(descriptor, tablePanel.getFilterString());
			    	}
			    	clearAll();
			    	loadMOC(adql);
		    	}
    		}else {
    			clearAll();
    			loadMOC();
    		}
    	}
    }
    
    public void checkLoadMOC() {
    	if (getCountStatus().hasMoved(descriptor.getMission())) {
    		loadMOCRequested = true;
    	} else {
    		loadMOC();
    	}
    }
    
    private void loadMOC() {
    	
    	int count = getCountStatus().getCount(descriptor.getMission());

    	if(currentVisibleCount == 0 && !filterRequested) {
    		if(count > EsaSkyWebConstants.MOC_GLOBAL_MINMAX_LIMIT) {
    			defaultEntity.fetchGlobalMinMaxHeaders(tablePanel);
    			globalMinMaxLoaded = true;
    		}else {
    			defaultEntity.fetchLocalMinMaxHeaders(tablePanel);
    			globalMinMaxLoaded = false;
    		}
    	}

    	if(count > EsaSkyWebConstants.MOC_FILTER_LIMIT) {
    		getPrecomputedMOC();
    		currentDataOrder = 8;
    		freshLoad = false;
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
    	
    	freshLoad = false;
    }
    
    public interface IpixMapper extends ObjectMapper<HashMap<String,Long[]>> {
    }
    
    private int getVisibleCount() {
    	
    	if(overlay != null) {
    		currentVisibleCount = AladinLiteWrapper.getAladinLite().getVisibleCountInMOC(overlay);
    		return currentVisibleCount;
    	}
    	
    	return 0;
    }
    
    private void getPrecomputedMOC() {
    	final String debugPrefix = "[fetchMoc][" + getDescriptor().getGuiShortName() + "]";

        String adql = metadataService.getPrecomputedMOCAdql(descriptor);
        String url = TAPUtils.getTAPQuery(URL.encodeQueryString(adql), EsaSkyConstants.JSON).replaceAll("#", "%23");

        Log.debug(debugPrefix + "Query [" + url + "]");
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
        try {
            builder.sendRequest(null,
                new MOCAsRecordCallback(tablePanel, adql, this, TextMgr.getInstance().getText("mocEntity_retrievingMissionCoverage").replace("$MISSIONNAME$", descriptor.getGuiLongName()), new MOCAsRecordCallback.OnComplete() {
               	 
                	@Override
                	public void onComplete() {
                		getVisibleCount();
                		setTableCountText();
                		
                		if(currentVisibleCount< descriptor.getShapeLimit() && currentVisibleCount > 0) {
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
    	int count = getCountStatus().getCount(descriptor.getMission());
    	
    	if(count > EsaSkyWebConstants.MOC_FILTER_LIMIT) {
    		text = TextMgr.getInstance().getText("MOC_large_count_text");
    	}
    	else {
    		count = currentVisibleCount;
    		text = TextMgr.getInstance().getText("MOC_count_text");
    		text = text.replace("$limit$", Integer.toString(descriptor.getShapeLimit()));
    	}

    	String countString = NumberFormatter.formatToNumberWithSpaces(count);
    	text = text.replace("$count$", countString);
 		tablePanel.setPlaceholderText(text);
    }

    private void getSplitMOC(int order) {
    	
    	String filter = tablePanel.getFilterString();
    	
    	String adql;

    	GeneralJavaScriptObject visibleIpixels = (GeneralJavaScriptObject) AladinLiteWrapper.getAladinLite().getVisiblePixelsInMOC(overlay, 8, true);
    	if(visibleIpixels.jsonStringify().length() > 2 || freshLoad) {

	    	if(descriptor instanceof CatalogDescriptor) {
	        	if(freshLoad) {
	        		adql = metadataService.getFilteredCatalogueMOCAdql(descriptor, tablePanel.getFilterString());
	        	}else {
	        		adql = metadataService.getFilteredCatalogueMOCAdql(descriptor,visibleIpixels, tablePanel.getFilterString());
	        	}
	    	}else {
	    		adql = metadataService.getFilteredObservationMOCAdql(descriptor, filter);
	    	}
			
	    	loadMOC(adql);
	    	
    	}
    }
    
    private void loadMOC(String adql) {
    	final String debugPrefix = "[fetchMoc][" + getDescriptor().getGuiShortName() + "]";

    	String url = TAPUtils.getTAPQuery(URL.encodeQueryString(adql), EsaSkyConstants.JSON).replaceAll("#", "%23");
    	
    	Log.debug(debugPrefix + "Query [" + url + "]");
    	RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
    	try {
    		builder.sendRequest(null,
    				new MOCAsRecordCallback(tablePanel, adql, this, TextMgr.getInstance().getText("mocEntity_retrievingMissionCoverage").replace("$MISSIONNAME$", descriptor.getGuiLongName()), new MOCAsRecordCallback.OnComplete() {
	                	 
	                 	@Override
	                 	public void onComplete() {
	                 		getVisibleCount();
	                 		setTableCountText();
	                 		if(currentVisibleCount < descriptor.getShapeLimit() && currentVisibleCount > 0) {
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
		getVisibleCount();
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
		getCountStatus().unregisterObserver(countObserver);
	}
	
	public void updateOverlay() {
		if(overlay == null) {
			String options = "{\"opacity\":0.2, \"color\":\"" + descriptor.getPrimaryColor() + "\", \"name\":\"" + parentEntity.getEsaSkyUniqId() + "\"}";
			overlay = (GeneralJavaScriptObject) AladinLiteWrapper.getAladinLite().createQ3CMOC(options);
			AladinLiteWrapper.getAladinLite().addMOC(overlay);
		}
		
		AladinLiteWrapper.getAladinLite().clearMOC(overlay);
		
    	int minOrder = MocRepository.getMinOrderFromFoV();
    	int maxOrder = MocRepository.getMaxOrderFromFoV();
		
    	overlay.invokeFunction("setShowOrders", minOrder, maxOrder);
	}
	
	
	Timer updateTimer = new Timer() {
		
		@Override
		public void run() {
				getVisibleCount();
				
				setTableCountText();	
				
				if(currentVisibleCount < parentEntity.getDescriptor().getShapeLimit() && currentVisibleCount > 0) {
					sendLoadQuery();
					return;
				}
				
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
	public void selectShapes(int shapeId) {
		defaultEntity.selectShapes(shapeId);
	}

	@Override
	public void deselectShapes(int shapeId) {
		defaultEntity.deselectShapes(shapeId);
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
    public boolean isCustomizable() {
    	return defaultEntity.isCustomizable();
    }

	@Override
	public void addShapes(GeneralJavaScriptObject javaScriptObject) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fetchData() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void coneSearch(SkyViewPosition conePos) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void refreshData() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public StylePanel createStylePanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void fetchDataWithoutMOC() {
		
	}

    @Override
    public String getShapeType() {
        return defaultEntity.getShapeType();
    }

    @Override
    public void setShapeType(String shapeType) {
    }

    @Override
    public void onShapeSelection(AladinShape shape) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onShapeDeselection(AladinShape shape) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onShapeHover(AladinShape shape) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onShapeUnhover(AladinShape shape) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void select() {
        // TODO Auto-generated method stub
        
    }
    
    public void setColor(String color) {
    	if(overlay != null) {
    		overlay.setProperty("color", color);
    	}
    }

    public void setScale(double value) {
    	if(overlay != null) {
    		overlay.setProperty("opacity", value);
    	}
    }
    
    @Override
    public void setRefreshable(boolean isRefreshable) {
        defaultEntity.setRefreshable(isRefreshable);
    }    

}
