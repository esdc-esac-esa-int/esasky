package esac.archive.esasky.cl.web.client.model.entities;

import com.allen_sauer.gwt.log.client.Log;
import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Image;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.AladinShape;
import esac.archive.esasky.cl.web.client.callback.MocCallback;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.LineStyle;
import esac.archive.esasky.cl.web.client.model.MOCInfo;
import esac.archive.esasky.cl.web.client.model.Shape;
import esac.archive.esasky.cl.web.client.model.TapRowList;
import esac.archive.esasky.cl.web.client.query.TAPMOCService;
import esac.archive.esasky.cl.web.client.query.TAPUtils;
import esac.archive.esasky.cl.web.client.repository.MocRepository;
import esac.archive.esasky.cl.web.client.status.CountObserver;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.cl.web.client.utility.*;
import esac.archive.esasky.cl.web.client.view.resultspanel.ITablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.TableFilterObserver;
import esac.archive.esasky.cl.web.client.view.resultspanel.stylemenu.StylePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorSettings;
import esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorTablePanel;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.coordinatesutils.Coordinate;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.*;

import java.util.*;

public class MOCEntity implements GeneralEntityInterface {

	final int DISPLAY_LIMIT = 2000;
	
    private ITablePanel tablePanel;
    protected IShapeDrawer drawer;
    protected IShapeDrawer combinedDrawer;
    private CommonTapDescriptor descriptor;
    private GeneralJavaScriptObject overlay;
    private int currentDataOrder = 8; 
    private int currentDisplayOrder; 
    private int currentMinOrder; 
    private int currentVisibleCount = 0;
    private boolean shouldBeShown = true;
    private GeneralEntityInterface parentEntity;
    private TAPMOCService metadataService;
    private boolean filterRequested = false;
    private boolean loadMOCRequested = false;
    private boolean freshLoad = true;
    private double size;
    private TableFilterObserver filterObserver;
    Map<Integer, Map<Long, Integer>> countMap = new HashMap<Integer, Map<Long, Integer>>();
    
    private String adql; 
    
    private String lineStyle;
    
    private CountObserver countObserver = new MOCCountObserver();
    
    private class MOCCountObserver implements CountObserver{
        
		@Override
		public void onCountUpdate(long newCount) {
			int perMissionNewCount = getCountStatus().getCount(descriptor);
			if(perMissionNewCount > EsaSkyWebConstants.MOC_FILTER_LIMIT) {
				setTableCountText();
				tablePanel.disableFilters();
				return;
			}

			tablePanel.enableFilters();
			getVisibleCount();
			setTableCountText();

			if(filterRequested && perMissionNewCount < EsaSkyWebConstants.MOC_FILTER_LIMIT) {
				loadMOC();
				filterRequested = false;
			}

			if(loadMOCRequested && shouldBeShown) {
				loadMOC();
				loadMOCRequested = false;
			}
		}
	}
	
	Timer filterTimer = new Timer() {
	
		@Override
		public void run() {
			if (getCountStatus().hasMoved(descriptor)) {
	    		filterRequested = true;
	    	} else if( getCountStatus().getCount(descriptor) < EsaSkyWebConstants.MOC_FILTER_LIMIT){
	    		filterRequested = true;
	    		loadMOC();
	    		filterRequested = false;
	    	}
		}
		
		@Override
		public void schedule(int delayMillis) {
			super.cancel();
			super.schedule(delayMillis);
		}
	};
	

	public MOCEntity(CommonTapDescriptor descriptor, CountStatus countStatus, GeneralEntityInterface parent) {
		
		overlay = null;
		drawer = null;
		this.descriptor = descriptor;
		
		metadataService = TAPMOCService.getInstance();
		parentEntity = parent;
		this.size = parentEntity.getSize();
		
		this.lineStyle = parentEntity.getLineStyle();
		if(this.lineStyle == null) {
			this.lineStyle = LineStyle.SOLID.getName();
		}
		
		MocRepository.getInstance().addMocEntity(this);
		filterObserver = tapFilters -> filterTimer.schedule(2000);
		
		getCountStatus().registerObserver(countObserver);
		setShouldBeShown(true);
	}

	public MOCEntity(CommonTapDescriptor descriptor) {
		overlay = null;
		drawer = null;
		this.descriptor = descriptor;
		metadataService = TAPMOCService.getInstance();
		EsaSkyEntity esaskyEntity = new EsaSkyEntity(descriptor,null, CoordinateUtils.getCenterCoordinateInJ2000(), descriptor.getId(), null);
		esaskyEntity.setMocEntity(this);
		this.parentEntity = esaskyEntity;
		this.lineStyle = LineStyle.SOLID.getName();
		this.size = parentEntity.getSize();
		MocRepository.getInstance().addMocEntity(this);
		setShouldBeShown(true);
	}
	
	@Override
	public void setTablePanel(ITablePanel panel) {
		if(this.tablePanel != panel) {
			this.tablePanel = panel;
			panel.registerFilterObserver(filterObserver);
			
			tablePanel.registerClosingObserver(this::closingTablePanel);
		}
	}
	
	public ITablePanel getTablePanel() {
		return tablePanel;
	}

    public void sendLoadQuery() {
		GeneralJavaScriptObject visibleIpixels = (GeneralJavaScriptObject)AladinLiteWrapper.getAladinLite().getVisiblePixelsInMOC(overlay, MocRepository.getMinOrderFromFoV(), false);


		if(Objects.equals(descriptor.getSchemaName(), "catalogues") && "".equals(tablePanel.getFilterString())){
			parentEntity.fetchDataWithoutMOC();
		}else {
			String whereQuery = metadataService.getWhereQueryFromPixels(descriptor, visibleIpixels, tablePanel.getFilterString());
			((EsaSkyEntity) parentEntity).fetchDataWithoutMOC(whereQuery);
		}

    	parentEntity.setSkyViewPosition(CoordinateUtils.getCenterCoordinateInJ2000());
    	setShouldBeShown(false);
    	clearAll();
    	updateOverlay();

    }

    public void sendLoadQuery(MOCInfo mocInfo) {
    	String whereQuery = metadataService.getWhereQueryFromPixels(descriptor, mocInfo.pixels, tablePanel.getFilterString());
    	
    	((EsaSkyEntity) parentEntity).fetchDataWithoutMOC(mocInfo, whereQuery);
    }
    
    public void refreshMOC() {
    	if(shouldBeShown) {
    		freshLoad = true;
    		clearAll();
    		checkLoadMOC();
    	}
    }

    public void checkLoadMOC() {
    	if (getCountStatus().hasMoved(descriptor) && !descriptor.hasSearchArea()) {
    		loadMOCRequested = true;
    	} else {
    		loadMOC();
    	}
    }
    
    private void loadMOC() {
    	
    	int count = getCountStatus().getCount(descriptor);

    	if(count > EsaSkyWebConstants.MOC_FILTER_LIMIT) {
    		getPrecomputedMOC(true);
    		currentDataOrder = 8;
    		tablePanel.disableFilters();
    		freshLoad = false;
    		return;
    	}


    	int targetOrder = MocRepository.getTargetOrderFromFoV();

    	boolean precomputedMaxmin = count >  EsaSkyWebConstants.MOC_GLOBAL_MINMAX_LIMIT;

    	if((targetOrder == 8 && tablePanel.getTapFilters().size() == 0)) {
    		getPrecomputedMOC(precomputedMaxmin);
    		currentDataOrder = 8;
    	}
    	else {
			getSplitMOC(targetOrder, precomputedMaxmin);
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
    
    private void getPrecomputedMOC(boolean precomputedMaxmin) {
    	final String debugPrefix = "[fetchMoc][" + getDescriptor().getShortNameColumn() + "]";

        String constraint = metadataService.getPrecomputedMocConstraint(descriptor);
		Coordinate coord = CoordinateUtils.getCenterCoordinateInJ2000().getCoordinate();
		String center = "POINT(" + coord.getRa() + "," + coord.getDec() + ")";
        String url = TAPUtils.getTAPMocQuery(center, URL.encodeQueryString(constraint),
        		descriptor.getTableName(), 8,  null, precomputedMaxmin).replaceAll("#", "%23");

        Log.debug(debugPrefix + "Query [" + url + "]");
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
        this.adql = url;
        ((EsaSkyEntity) this.parentEntity).setAdql(url);
        try {
            builder.sendRequest(null,
                new MocCallback(tablePanel, constraint, this, TextMgr.getInstance().getText("mocEntity_retrievingMissionCoverage").replace("$MISSIONNAME$", descriptor.getLongNameColumn()), () -> {
					getVisibleCount();
					setTableCountText();
					onFoVChanged();

					if(currentVisibleCount< DeviceUtils.getDeviceShapeLimit(descriptor) && currentVisibleCount > 0) {
						sendLoadQuery();
					}
				}));
        } catch (RequestException e) {
            Log.error(e.getMessage());
            Log.error("[getMocMetadata] Error fetching JSON data from server");
        }

    }
    
    private void setTableCountText() {
    	if(getCountStatus() == null || getCountStatus().getCount(descriptor) == null ) {
    		return;
    	}

    	String text = "";
    	int count = getCountStatus().getCount(descriptor);

    	if(count > EsaSkyWebConstants.MOC_FILTER_LIMIT) {
    		text = TextMgr.getInstance().getText("MOC_large_count_text");
    	}
    	else {
    		count = currentVisibleCount;
    		text = TextMgr.getInstance().getText("MOC_count_text");
    		text = text.replace("$limit$", NumberFormatter.formatToNumberWithSpaces(DeviceUtils.getDeviceShapeLimit(descriptor)));
    	}

    	String countString = NumberFormatter.formatToNumberWithSpaces(count);
    	text = text.replace("$count$", countString);
 		tablePanel.setPlaceholderText(text);
    }

    private void getSplitMOC(int order, boolean precomputedMaxmin) {
    	
    	GeneralJavaScriptObject visibleIpixels = (GeneralJavaScriptObject) AladinLiteWrapper.getAladinLite().getVisiblePixelsInMOC(overlay);
    	String url = "";
    	if(visibleIpixels.jsonStringify().length() > 2 || freshLoad) {

        	if(freshLoad) {
                String constraint = metadataService.getPrecomputedMocConstraint(descriptor);
        		Coordinate coord = CoordinateUtils.getCenterCoordinateInJ2000().getCoordinate();
        		String center = "POINT(" + coord.getRa() + "," + coord.getDec() + ")";
        		url = TAPUtils.getTAPMocQuery(center, URL.encodeQueryString(constraint), descriptor.getTableName(),
        				order, tablePanel.getFilterString(), precomputedMaxmin).replaceAll("#", "%23");
        	}else {
        		url = TAPUtils.getTAPMocFilteredQuery(descriptor.getTableName(), order, visibleIpixels, tablePanel.getFilterString(), freshLoad).replaceAll("#", "%23");
        	}
        	clearAll();
	    	loadMOC(url);
	    	
    	}
    }
    
    public void loadMOC(String url) {
    	final String debugPrefix = "[fetchMoc][" + getDescriptor().getShortNameColumn() + "]";

    	Log.debug(debugPrefix + "Query [" + url + "]");
    	RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
    	try {
    		builder.sendRequest(null,
    				new MocCallback(tablePanel, url, this, TextMgr.getInstance().getText("mocEntity_retrievingMissionCoverage").replace("$MISSIONNAME$", descriptor.getLongNameColumn()), () -> {
						getVisibleCount();
						setTableCountText();
					   	onFoVChanged();

						if(currentVisibleCount < DeviceUtils.getDeviceShapeLimit(descriptor) && currentVisibleCount > 0) {
						   sendLoadQuery();
					   }
					}));
    	} catch (RequestException e) {
    		Log.error(e.getMessage());
    		Log.error("[getMocMetadata] Error fetching JSON data from server");
    	}
    }
    
	@Override
	public void setSizeRatio(double size) {
		this.size = size;
		if(overlay != null) {
    		overlay.invokeFunction("setOpacity", size);
    	}
	}
	
	@Override
	public double getSize() {
		return size;
	}

	@Override
	public void removeAllShapes() {
		clearAll();
	}
	
	
	@Override
	public void clearAll() {
		updateOverlay();
		getVisibleCount();
	}
	

	public void addJSON(final ITablePanel tablePanel, final GeneralJavaScriptObject data) {
		
		setTablePanel(tablePanel);
		if(data.hasProperty("metadata")) {
			tablePanel.insertHeader(data, "maxMin");
		}
		
		if(overlay == null) {
			String options = "{\"opacity\":0.2, \"color\":\"" + descriptor.getColor() + "\", \"name\":\"" + parentEntity.getEsaSkyUniqId() + "\"}";
			overlay = (GeneralJavaScriptObject) AladinLiteWrapper.getAladinLite().createQ3CMOC(options);
			AladinLiteWrapper.getAladinLite().addMOC(overlay);
		}
		
		overlay.invokeFunction("dataFromESAJSON", data);
		onFoVChanged();
	}
	
	public void addJSON(final GeneralJavaScriptObject data, GeneralJavaScriptObject options) {
		if(overlay == null) {
			overlay = (GeneralJavaScriptObject) AladinLiteWrapper.getAladinLite().createQ3CMOC(options);
			AladinLiteWrapper.getAladinLite().addMOC(overlay);
		}
		
		overlay.invokeFunction("dataFromJSON", data);
		onFoVChanged();
	}
	
	public void closingTablePanel() {
		
		MocRepository.getInstance().removeEntity(this);
		clearAll();
		AladinLiteWrapper.getAladinLite().removeMOC(overlay);
		overlay = null;
		setShouldBeShown(false);
		if(getCountStatus() != null) {
			getCountStatus().unregisterObserver(countObserver);
		}
		
	}
	
	public void closeFromAPI() {
		clearAll();
		AladinLiteWrapper.getAladinLite().removeMOC(overlay);
		overlay = null;
		setShouldBeShown(false);
		if(getCountStatus() != null) {
			getCountStatus().unregisterObserver(countObserver);
		}		
		if(tablePanel != null) {
			tablePanel.closeTablePanel();
		}
	}
	
	public void updateOverlay() {
		if(overlay == null) {
			String options = "{\"opacity\":0.2, \"color\":\"" + descriptor.getColor() + "\", \"name\":\"" + parentEntity.getEsaSkyUniqId() + "\"}";
			overlay = (GeneralJavaScriptObject) AladinLiteWrapper.getAladinLite().createQ3CMOC(options);
			AladinLiteWrapper.getAladinLite().addMOC(overlay);
		}
		
		AladinLiteWrapper.getAladinLite().clearMOC(overlay);

		int minOrder = MocRepository.getMinOrderFromFoV();
		int maxOrder = MocRepository.getMaxOrderFromFoV();

    	overlay.invokeFunction("setShowOrders", minOrder, maxOrder);
    	overlay.invokeFunction("reportChange");

	}
	
	
	Timer updateTimer = new Timer() {
		
		@Override
		public void run() {

				if (descriptor.hasSearchArea()) {
					return;
				}

				getVisibleCount();
				
				setTableCountText();	
				
				if(currentVisibleCount < DeviceUtils.getDeviceShapeLimit(parentEntity.getDescriptor()) && currentVisibleCount > 0) {
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
		 if(shouldBeShown && overlay != null) {

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
	
	@Override
	public void selectShapes(int shapeId) {
	}
	
	@Override
	public LinkedList<Integer> selectShapes(String shapeName) {
		return null;
	}

	@Override
	public void deselectShapes(int shapeId) {
	}

	@Override
	public LinkedList<Integer> deselectShapes(String shapeName) {
		return null;
	}

	@Override
	public void deselectAllShapes() {
		return;
	}

	@Override
	public void showShape(int rowId) {
		return;
	}

	@Override
	public void showShapes(List<Integer> shapeIds) {
		return;
	}

	@Override
	public void showAndHideShapes(List<Integer> rowIdsToShow, List<Integer> rowIdsToHide) {
		return;
	}
	
	@Override
	public void setShapeBuilder(ShapeBuilder shapeBuilder) {
		return;
	}

	@Override
	public void hideShape(int rowId) {
		return;
	}

	@Override
	public void hideShapes(List<Integer> shapeIds) {
		return;
	}
	
	@Override
	public void hideAllShapes() {
		return;
	}

	@Override
	public void hoverStart(int hoveredRowId) {
		return;
	}

	@Override
	public void hoverStop(int hoveredRowId) {
		return;
	}

	@Override
	public SkyViewPosition getSkyViewPosition() {
		return parentEntity.getSkyViewPosition();
	}

	@Override
	public void setSkyViewPosition(SkyViewPosition skyViewPosition) {
		parentEntity.setSkyViewPosition(skyViewPosition);
	}

	@Override
	public String getHistoLabel() {
		return parentEntity.getHistoLabel();
	}

	@Override
	public void setHistoLabel(String histoLabel) {
		parentEntity.setHistoLabel(histoLabel);
	}

	@Override
	public String getEsaSkyUniqId() {
		return parentEntity.getEsaSkyUniqId();
	}

	@Override
	public void setEsaSkyUniqId(String esaSkyUniqId) {
		parentEntity.setEsaSkyUniqId(esaSkyUniqId);
	}

	@Override
	public TapRowList getMetadata() {
		return parentEntity.getMetadata();
	}

	@Override
	public void setMetadata(TapRowList metadata) {
		parentEntity.setMetadata(metadata);
	}

	@Override
	public String getTabLabel() {
		return getDescriptor().getLongNameColumn();
	}
	
	@Override
	public Image getTypeLogo() {
		return parentEntity.getTypeLogo();
	}

	@Override
	public Object getTAPDataByTAPName(TapRowList tapRowList, int rowIndex, String tapName) {
		return parentEntity.getTAPDataByTAPName(tapRowList, rowIndex, tapName);
	}

	@Override
	public Double getDoubleByTAPName(TapRowList tapRowList, int rowIndex, String tapName, Double defaultValue) {
		return parentEntity.getDoubleByTAPName(tapRowList, rowIndex, tapName, defaultValue);
	}

	@Override
	public CommonTapDescriptor getDescriptor() {
		return descriptor;
	}

	@Override
	public CountStatus getCountStatus() {
		return parentEntity.getCountStatus();
	}

	@Override
	public ITablePanel createTablePanel() {
        setTablePanel(new TabulatorTablePanel(getTabLabel(), getEsaSkyUniqId(), this));
        return tablePanel;
	}

	@Override
	public boolean isSampEnabled() {
		return false;
	}

	@Override
	public boolean isRefreshable() {
    	if(parentEntity != null) {

    		return parentEntity.isRefreshable();
    	}
    	return false;
	}

    @Override
    public boolean isCustomizable() {
    	if(parentEntity != null) {
    		return parentEntity.isCustomizable();
    	}
    	return true;
    }

	@Override
	public void addShapes(GeneralJavaScriptObject javaScriptObject, GeneralJavaScriptObject metadata) {
	  //This entity has only MOC data
	}

	@Override
	public void fetchData() {
	  //This entity has only MOC data
	}

	@Override
	public void insertExternalData(GeneralJavaScriptObject data) {
		//This entity has only MOC data
	}

	@Override
	public void fetchData(String adql) {
	  //This entity has only MOC data
	}
	
	@Override
	public void coneSearch(SkyViewPosition conePos) {
	  //This entity has only MOC data
	}

	@Override
	public StylePanel createStylePanel() {
		return parentEntity.createStylePanel();
	}
	
    @Override
	public void setStylePanelVisibility() {
    	parentEntity.setStylePanelVisibility();
	}

	@Override
	public void fetchDataWithoutMOC() {
	  //This entity has only MOC data
	}

    @Override
    public String getShapeType() {
        return parentEntity.getShapeType();
    }

    @Override
    public void setShapeType(String shapeType) {
    	parentEntity.setShapeType(shapeType);
    }

    @Override
    public String getLineStyle() {
    	return lineStyle;
    }
    
    @Override
    public void setLineStyle(String lineStyle) {
    	this.lineStyle = lineStyle;
    	if(overlay != null) {
    		overlay.invokeFunction("setLineStyle", lineStyle);
    	}
    }

    @Override
    public void onShapeSelection(AladinShape shape) {
      //No hover events needed in MOCEntity
    }

    @Override
    public void onShapeDeselection(AladinShape shape) {
      //No hover events needed in MOCEntity
    }

    @Override
    public void onShapeHover(AladinShape shape) {
      //No hover events needed in MOCEntity
    }

    @Override
    public void onShapeUnhover(AladinShape shape) {
        //No hover events needed in MOCEntity
    }

    @Override
    public void select() {
        //Cannot be selected
    }
    
    @Override
    public void setPrimaryColor(String color) {
    	if(overlay != null) {
    		overlay.setProperty("color", color);
    	}
    }
    
    @Override
    public String getPrimaryColor() {
    	return parentEntity.getPrimaryColor();
    }
    

    @Override
	public void setSecondaryColor(String color) {
      //Has no secondarColor
	}

	@Override
	public String getSecondaryColor() {
		return null;
	}

	@Override
	public String getColor() {
		return getPrimaryColor();
	}

	@Override
    public void setRefreshable(boolean isRefreshable) {
        parentEntity.setRefreshable(isRefreshable);
    }

    
	@Override
	public String getHelpText() {
		if(parentEntity != null) {
			return parentEntity.getHelpText();
		}
		else {
			return "";
		}
	}

	@Override
	public Shape getShape(int shapeId) {
		return parentEntity.getShape(shapeId);
	}

	@Override
	public void registerColorChangeObserver(ColorChangeObserver colorChangeObserver) {
	    //There is no current reason to register an observer for this
	}

	@Override
	public void onMultipleShapesSelection(LinkedList<AladinShape> shapes) {
		
	}

	@Override
	public void onMultipleShapesDeselection(LinkedList<AladinShape> linkedList) {
		
	}
	
	@Override
	public TabulatorSettings getTabulatorSettings() {
		return new TabulatorSettings();
	}
	
	@Override
	public String getAdql() {
		return this.adql;
	}

	@Override
	public int getNumberOfShapes() {
		return 0;
	}
    
}
