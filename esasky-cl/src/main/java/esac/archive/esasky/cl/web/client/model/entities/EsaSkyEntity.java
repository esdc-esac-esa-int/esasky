package esac.archive.esasky.cl.web.client.model.entities;

import com.allen_sauer.gwt.log.client.Log;
import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.Image;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.AladinShape;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.AddShapeTooltipEvent;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPopEvent;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.*;
import esac.archive.esasky.cl.web.client.presenter.MainPresenter;
import esac.archive.esasky.cl.web.client.query.AbstractTAPService;
import esac.archive.esasky.cl.web.client.repository.EntityRepository;
import esac.archive.esasky.cl.web.client.status.CountObserver;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.cl.web.client.utility.*;
import esac.archive.esasky.cl.web.client.view.allskypanel.CatalogueTooltip;
import esac.archive.esasky.cl.web.client.view.allskypanel.Tooltip;
import esac.archive.esasky.cl.web.client.view.resultspanel.ITablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.stylemenu.StylePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.stylemenu.StylePanel.StylePanelCallback;
import esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorSettings;
import esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorTablePanel;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.TapDescriptorList;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;

import java.util.*;

public class EsaSkyEntity implements GeneralEntityInterface {

    public interface SecondaryShapeAdder{
        void createSpecializedOverlayShape(Map<String, Object> details);
        void addSecondaryShape(GeneralJavaScriptObject rowData, String ra, String dec, Map<String, String> details);
    }

    protected IShapeDrawer drawer;
    protected CombinedSourceFootprintDrawer combinedDrawer;
    protected CommonTapDescriptor descriptor;
    protected MOCEntity mocEntity;
    protected AbstractTAPService metadataService;
    private SecondaryShapeAdder secondaryShapeAdder;
    protected ITablePanel tablePanel;
    protected Tooltip tooltip;
    private SkyViewPosition skyViewPosition;
    private String histoLabel;
    private String id;
    private String regionColumn;
    private TapRowList metadata;
    private CountStatus countStatus;
    private boolean isRefreshable = true;

    private boolean customRefreshable = false;
    private StylePanel stylePanel;
    private LinkedList<ColorChangeObserver> colorChangeObservers = new LinkedList<>();
    private LinkedList<QueryChangeObserver> queryChangeObservers = new LinkedList<>();
    protected LinkedList<Integer> shapeRecentlySelected = new LinkedList<>();
    private String adql; 

    public EsaSkyEntity(CommonTapDescriptor descriptor, CountStatus countStatus,
                        SkyViewPosition skyViewPosition, String esaSkyUniqId, AbstractTAPService metadataService, SecondaryShapeAdder secondaryShapeAdder) {
        this(descriptor, countStatus, skyViewPosition, esaSkyUniqId, metadataService, CombinedSourceFootprintDrawer.DEFAULT_SOURCE_SIZE, 
                SourceShapeType.SQUARE.getName(), secondaryShapeAdder, "solid");

    }
    public EsaSkyEntity(CommonTapDescriptor descriptor, CountStatus countStatus,
                        SkyViewPosition skyViewPosition, String esaSkyUniqId, AbstractTAPService metadataService) {
        this(descriptor, countStatus, skyViewPosition, esaSkyUniqId, metadataService, 
                CombinedSourceFootprintDrawer.DEFAULT_SOURCE_SIZE, SourceShapeType.SQUARE.getName());
    }

    public EsaSkyEntity(CommonTapDescriptor descriptor, SkyViewPosition skyViewPosition, String esaSkyUniqId, String lineStyle) {
    	this(descriptor, null, skyViewPosition, esaSkyUniqId, null,
    			CombinedSourceFootprintDrawer.DEFAULT_SOURCE_SIZE, SourceShapeType.SQUARE.getName(), null, lineStyle);
    }

    public EsaSkyEntity(CommonTapDescriptor descriptor, SkyViewPosition skyViewPosition, String esaSkyUniqId, String lineStyle, AbstractTAPService metadataService) {
        this(descriptor, null, skyViewPosition, esaSkyUniqId, metadataService,
                CombinedSourceFootprintDrawer.DEFAULT_SOURCE_SIZE, SourceShapeType.SQUARE.getName(), null, lineStyle);
    }

    public EsaSkyEntity(CommonTapDescriptor descriptor, SkyViewPosition skyViewPosition, String esaSkyUniqId, String lineStyle, AbstractTAPService metadataService, String regionColumn) {
        this(descriptor, null, skyViewPosition, esaSkyUniqId, metadataService,
                CombinedSourceFootprintDrawer.DEFAULT_SOURCE_SIZE, SourceShapeType.SQUARE.getName(), null, lineStyle, regionColumn);
    }
    public EsaSkyEntity(CommonTapDescriptor descriptor, CountStatus countStatus,
                        SkyViewPosition skyViewPosition, String esaSkyUniqId,
                        AbstractTAPService metadataService, int shapeSize, Object shapeType) {
        this(descriptor, countStatus, skyViewPosition, esaSkyUniqId, metadataService, shapeSize, shapeType, null, "solid");
    }

    public EsaSkyEntity(CommonTapDescriptor descriptor, CountStatus countStatus,
                        SkyViewPosition skyViewPosition, String esaSkyUniqId,
                        AbstractTAPService metadataService, int shapeSize, Object shapeType,
                        SecondaryShapeAdder secondaryShapeAdder, String lineStyle) {
        this(descriptor, countStatus, skyViewPosition, esaSkyUniqId, metadataService, shapeSize, shapeType, secondaryShapeAdder, lineStyle, null);
    }

    public EsaSkyEntity(CommonTapDescriptor descriptor, CountStatus countStatus,
                        SkyViewPosition skyViewPosition, String esaSkyUniqId,
                        AbstractTAPService metadataService, int shapeSize, Object shapeType,
                        SecondaryShapeAdder secondaryShapeAdder, String lineStyle, String regionColumn) {
        this.metadataService = metadataService;
        this.descriptor = descriptor;
        this.secondaryShapeAdder = secondaryShapeAdder;
        this.regionColumn = regionColumn;
        this.id = "ESASKY_ENTITY_" + UUID.randomUUID();

        if (Objects.equals(descriptor.getMission(), "Gaia-DR3")) {
            descriptor.setSecondaryColor("#33ccff");
        }

        String color = descriptor.getColor();
        JavaScriptObject footprints = AladinLiteWrapper.getAladinLite().createOverlay(id, color, lineStyle);

        Map<String, Object> details = new HashMap<>();

        if (secondaryShapeAdder != null) {
            secondaryShapeAdder.createSpecializedOverlayShape(details);
        } else {
            details.put("shape", shapeType);
        }

        JavaScriptObject catalogue = AladinLiteWrapper.getAladinLite().createCatalogWithDetails(id, shapeSize, color, details);

        combinedDrawer = new CombinedSourceFootprintDrawer(catalogue, footprints, shapeBuilder, shapeType);

        drawer = combinedDrawer;

        drawer.setPrimaryColor(color);
        drawer.setSecondaryColor(descriptor.getSecondaryColor());

        this.skyViewPosition = skyViewPosition;
        this.countStatus = countStatus;
    }


    protected ShapeBuilder shapeBuilder = new ShapeBuilder() {
    	private GeneralJavaScriptObject getRowWithName(GeneralJavaScriptObject metadata, String name) {
    		for(GeneralJavaScriptObject entry : GeneralJavaScriptObject.convertToArray(metadata)) {
    			if(entry.getStringProperty("name").equals(name)) {
    				return entry;
    			}
    		}
    		return null;
    	}
        @Override
        public Shape buildShape(int rowId, TapRowList rowList, GeneralJavaScriptObject rowData, GeneralJavaScriptObject metadata) {
            String stcs = null;

            String stcsColumn = EsaSkyEntity.this.regionColumn;
            if (stcsColumn == null || stcsColumn.isEmpty()) {
                stcsColumn = descriptor.getRegionColumn();
            }

            if(stcsColumn != null && !stcsColumn.isEmpty()) {
                stcs = rowData.getStringProperty(stcsColumn);

                GeneralJavaScriptObject stcsMetadata = getRowWithName(metadata, stcsColumn);
                String xtype = stcsMetadata != null ? stcsMetadata.getStringProperty("xtype") : null;
                if(xtype != null && stcs != null) {
                	xtype = xtype.toLowerCase();
                	if(xtype.contains("polygon") && !stcs.trim().toLowerCase().startsWith("polygon")) {
                		stcs = "POLYGON " + stcs.replace(",", " ");
                	} else if(xtype.contains("circle") && !stcs.trim().toLowerCase().startsWith("circle")) {
                		stcs = "CIRCLE " + stcs.replace(",", " ");
                	} else if(xtype.contains("point") && !stcs.trim().toLowerCase().startsWith("point")) {
                		stcs = "POINT " + stcs.replace(",", " ");
                	}
                }
            }
            if(stcs == null || stcs.toUpperCase().startsWith("POSITION") || !(isValidSTCS(stcs))) {
                return catalogBuilder(rowId, rowData);
            }

            stcs = makeSureSTCSHasFrame(stcs);
            PolygonShape polygon = new PolygonShape();
            polygon.setShapeId(rowId);
            polygon.setStcs(stcs);
            polygon.setJsObject(((GeneralJavaScriptObject) AladinLiteWrapper.getAladinLite().createFootprintFromSTCS(
                    polygon.getStcs(), rowId)));

            polygon.setShapeId(rowId);
            String shapeName = rowData.getStringProperty(descriptor.getIdColumn());
            polygon.setShapeName(shapeName);

            String ra = rowData.getStringProperty(descriptor.getRaColumn());
            String dec = rowData.getStringProperty(descriptor.getDecColumn());
            polygon.setRa(ra);
            polygon.setDec(dec);

            return polygon;
        }
    };


    public SourceShape catalogBuilder(int shapeId, GeneralJavaScriptObject rowData) {
        String ra = rowData.getStringProperty(descriptor.getRaColumn());
        String dec = rowData.getStringProperty(descriptor.getDecColumn());
        String sourceName = rowData.getStringProperty(descriptor.getIdColumn());

        SourceShape mySource = new SourceShape();
        mySource.setShapeId(shapeId);

        mySource.setRa(ra);
        mySource.setDec(dec);
        mySource.setShapeName(sourceName);

        Map<String, String> details = new HashMap<>();

        details.put(SourceConstant.SOURCE_NAME, mySource.getShapeName());

        details.put(SourceConstant.CATALOGE_NAME, getId());
        details.put(SourceConstant.ID, Integer.toString(shapeId));

        if (this.getDescriptor().getCategory().equals(EsaSkyWebConstants.CATEGORY_PUBLICATIONS)) {
            final String bibcount = "bibcount";
            if (rowData.hasProperty(bibcount)) {
                details.put(SourceConstant.EXTRA_PARAMS, bibcount);
                details.put(bibcount, rowData.getStringProperty(bibcount));
            }
        }
        else {
            details.put(SourceConstant.EXTRA_PARAMS, null);
        }

        if(secondaryShapeAdder != null) {
            secondaryShapeAdder.addSecondaryShape(rowData, ra, dec, details);
        }

        mySource.setJsObject((GeneralJavaScriptObject) AladinLiteWrapper.getAladinLite().newApi_createSourceJSObj(
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

    private boolean isValidSTCS(String input) {
        String[] validShapes = new String[]{"polygon", "circle", "ellipse"};
        return Arrays.stream(validShapes).anyMatch(input.toLowerCase()::contains);
    }


    @Override
    public void fetchData() {
        if (getCountStatus().hasMoved(descriptor) && Double.compare(descriptor.getFovLimit(), 0) == 0 && !descriptor.hasSearchArea()) {
	        getCountStatus().registerObserver(new CountObserver() {
				@Override
				public void onCountUpdate(long newCount) {
				    CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPopEvent("WaitingForCount" + getId()));
	                fetchShapesAndMetadata();
					getCountStatus().unregisterObserver(this);
				}
			});
        } else {
            fetchShapesAndMetadata();
        }
    }

    @Override
    public void fetchData(String adql) {
    	 Log.debug("Showing real data");
         drawer = combinedDrawer;
         tablePanel.setMOCMode(false);
         tablePanel.notifyObservers();

         setQuery(adql);
         if(mocEntity != null){
 	        mocEntity.clearAll();
 	        mocEntity.setShouldBeShown(false);
         }

         String url = descriptor.createTapUrl(metadataService.getRequestUrl(), adql, EsaSkyConstants.JSON);

         clearAll();
         tablePanel.insertData(url);
    }

    @Override
    public void insertExternalData(GeneralJavaScriptObject data) {
        drawer = combinedDrawer;
        tablePanel.setMOCMode(false);
        tablePanel.notifyObservers();

        if(mocEntity != null){
            mocEntity.clearAll();
            mocEntity.setShouldBeShown(false);
        }

        clearAll();
        tablePanel.insertExternalTapData(data);
    }

    public interface TapDescriptorListMapper extends ObjectMapper<TapDescriptorList> {
    }

    protected void fetchShapesAndMetadata() {
        clearAll();
        int shapeLimit = DeviceUtils.getDeviceShapeLimit(descriptor);

        if (shapeLimit > 0 && getCountStatus().getCount(descriptor) > shapeLimit) {
            Log.debug("Showing dynamic moc");
            if(mocEntity == null){
                mocEntity = new MOCEntity(descriptor, getCountStatus(), EsaSkyEntity.this);
            }

            if(getLineStyle() == null) {
                setLineStyle(LineStyle.SOLID.getName());
            }
            mocEntity.setTablePanel(tablePanel);
            mocEntity.setShouldBeShown(true);
            tablePanel.setMOCMode(true);
            tablePanel.notifyObservers();
            mocEntity.refreshMOC();
        } else {
            fetchDataWithoutMOC();
        }
    }

    @Override
    public void fetchDataWithoutMOC() {
        Log.debug("Showing real data");
        tablePanel.setMOCMode(false);
        tablePanel.notifyObservers();

        if(mocEntity != null){
	        mocEntity.clearAll();
	        mocEntity.setShouldBeShown(false);
        }
        String url = getTapUrl();

        clearAll();
        tablePanel.insertData(url);
    }
    
    public String getTapUrl() {
    	setQuery(metadataService.getMetadataAdql(descriptor, tablePanel.getFilterString()));
    	return descriptor.createTapUrl(metadataService.getRequestUrl(), this.adql, EsaSkyConstants.JSON);
    }

    public void fetchDataWithoutMOC(String whereQuery) {
    	Log.debug("Showing real data");
        tablePanel.setMOCMode(false);
        tablePanel.notifyObservers();
    	if(mocEntity != null){
    		mocEntity.clearAll();
    		mocEntity.setShouldBeShown(false);
    	}
    	
    	clearAll();
    	setQuery(metadataService.getMetadataFromMOCPixelsADQL(getDescriptor(), whereQuery));
        tablePanel.insertData(descriptor.createTapUrl(metadataService.getRequestUrl(), this.adql, EsaSkyConstants.JSON));
    }
    public void fetchDataWithoutMOC(MOCInfo mocInfo, String whereQuery) {

    	//Open this in a new table
    	
    	String adql = metadataService.getMetadataFromMOCPixelsADQL(getDescriptor(), whereQuery);
    	
    	String filter = tablePanel.getFilterString();
    	if(!"".equals(filter)) {
    		adql += " AND " + filter;
    	}
        
        GeneralEntityInterface entity = EntityRepository.getInstance().createEntity(descriptor);
        setQuery(adql);
        MainPresenter.getInstance().getRelatedMetadata(entity, adql);
        entity.setSkyViewPosition(CoordinateUtils.getCenterCoordinateInJ2000());
    	
    }
    
    public MOCEntity createMocEntity() {
    	this.mocEntity = new MOCEntity(descriptor, getCountStatus(), this);
	    if(getLineStyle() == null) {
	    	setLineStyle(LineStyle.SOLID.getName());
	    }
	    mocEntity.setTablePanel(tablePanel);
	    mocEntity.setShouldBeShown(true);
	    tablePanel.setMOCMode(true);
	    return this.mocEntity;
    }

    @Override
    public void setSizeRatio(double size) {
    	drawer.setSizeRatio(size);
    }

    @Override
    public double getSize() {
        return drawer.getSize();
    }

    @Override
    public void removeAllShapes() {
    	drawer.removeAllShapes();
    }

    @Override
    public void addShapes(GeneralJavaScriptObject javaScriptObject, GeneralJavaScriptObject metadata) {
        drawer.addShapes(javaScriptObject, metadata);
    }

    @Override
    public void selectShapes(int shapeId) {
    	drawer.selectShapes(shapeId);
    }

    @Override
    public void deselectShapes(int shapeId) {
    	drawer.deselectShapes(shapeId);
    }
    
    @Override
    public LinkedList<Integer> selectShapes(String shapeName) {
    	LinkedList<Integer> shapeIds = drawer.selectShapes(shapeName);
    	for(int id : shapeIds) {
            if(tablePanel != null) {
            	tablePanel.selectRow(id);
            }
    	}
    	return shapeIds;
    }

    @Override
    public LinkedList<Integer> deselectShapes(String shapeName) {
    	LinkedList<Integer> shapeIds = drawer.deselectShapes(shapeName);
    	for(int id : shapeIds) {
            if(tablePanel != null) {
            	tablePanel.deselectRow(id);
            }
    	}
    	return shapeIds;
    }

    @Override
    public void deselectAllShapes() {
    	drawer.deselectAllShapes();
        if(tablePanel != null) {
        	tablePanel.deselectAllRows();
        }
    }

    @Override
    public void showShape(int rowId) {
    	drawer.showShape(rowId);
    }

    @Override
    public void showShapes(List<Integer> shapeIds) {
    	drawer.showShapes(shapeIds);
    }

    @Override
    public void showAndHideShapes(List<Integer> rowIdsToShow, List<Integer> rowIdsToHide) {
    	drawer.showAndHideShapes(rowIdsToShow, rowIdsToHide);
    }

    @Override
    public void setShapeBuilder(ShapeBuilder shapeBuilder) {
    	drawer.setShapeBuilder(shapeBuilder);
    }

    @Override
    public void hideShape(int rowId) {
    	drawer.hideShape(rowId);
    }

    @Override
    public void hideShapes(List<Integer> shapeIds) {
    	drawer.hideShapes(shapeIds);
    }

    @Override
    public void hideAllShapes() {
    	drawer.hideAllShapes();
    }

    @Override
    public void hoverStart(int hoveredRowId) {
    	drawer.hoverStart(hoveredRowId);
    }

    @Override
    public void hoverStop(int hoveredRowId) {
    	drawer.hoverStop(hoveredRowId);
    }

    @Override
    public SkyViewPosition getSkyViewPosition() {
        return skyViewPosition;
    }

    @Override
    public void setSkyViewPosition(SkyViewPosition skyViewPosition) {
        this.skyViewPosition = skyViewPosition;
    }

    @Override
    public String getHistoLabel() {
        return histoLabel;
    }

    @Override
    public void setHistoLabel(String histoLabel) {
        this.histoLabel = histoLabel;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getIcon() {
        switch (descriptor.getCategory()) {
            case EsaSkyWebConstants.CATEGORY_PUBLICATIONS:
                return "publications";
            case EsaSkyWebConstants.CATEGORY_CATALOGUES:
                return "catalog";
            case EsaSkyWebConstants.CATEGORY_EXTERNAL:
                return "ext_tap";
            case EsaSkyWebConstants.CATEGORY_SSO:
                return "sso";
            case EsaSkyWebConstants.CATEGORY_SPECTRA:
                return "spectra";
            case EsaSkyWebConstants.CATEGORY_OBSERVATIONS:
            case EsaSkyWebConstants.CATEGORY_NEUTRINOS:
            case EsaSkyWebConstants.CATEGORY_GRAVITATIONAL_WAVES:
            default:
                return "galaxy";
        }
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public TapRowList getMetadata() {
        return metadata;
    }
    
    @Override
    public void setMetadata(TapRowList metadata) {
        this.metadata = metadata;
    }

    @Override
    public String getTabLabel() {
        return getDescriptor().getShortName();
    }

    @Override
    public Image getTypeLogo() {
        return null;
    }
    
    @Override
    public Object getTAPDataByTAPName(TapRowList tapRowList, int rowIndex, String tapName) {
    	Object data = null;
        for (TapMetadata tapMetadata : tapRowList.getMetadata()) {
            if (tapMetadata.getName().equals(tapName)) {
                int dataIndex = tapRowList.getMetadata().indexOf(tapMetadata);
                data = (tapRowList.getData().get(rowIndex)).get(dataIndex);
                break;
            }
        }
        return data;
    }

    @Override
    public Double getDoubleByTAPName(TapRowList tapRowList, int rowIndex, String tapName, Double defaultValue) {
        if (tapName != null) {
            String value = getTAPDataByTAPName(tapRowList, rowIndex, tapName).toString();
            if ((value != null) && (!value.isEmpty()) && (!value.equals("null"))) {
                return Double.parseDouble(value);
            }
        }
        
        return defaultValue;
    }

    @Override
    public CommonTapDescriptor getDescriptor() {
        return descriptor;
    }

    @Override
    public CountStatus getCountStatus() {
        return countStatus;
    }

    @Override
    public void clearAll() {
    	if (this.getMetadata() != null) {
            if (this.getMetadata().getMetadata() != null) {
                this.getMetadata().getMetadata().clear();
            }
            if (this.getMetadata().getData() != null) {
                this.getMetadata().getData().clear();
            }
        }
        drawer.removeAllShapes();
    }

    @Override
    public String getColor() {
        return drawer.getPrimaryColor();
    }

    @Override
    public String getPrimaryColor() {
    	return drawer.getPrimaryColor();
    }

    @Override
    public void setPrimaryColor(String color) {
    	drawer.setPrimaryColor(color);
    	if(mocEntity != null) {
    		mocEntity.setPrimaryColor(color);
    	}
    	notifyColorChangeObservers(color);
    }
    
    public void registerColorChangeObserver(ColorChangeObserver colorChangeObserver) {
    	colorChangeObservers.add(colorChangeObserver);
    }

    public void unregisterColorChangeObserver(ColorChangeObserver colorChangeObserver) {
    	colorChangeObservers.remove(colorChangeObserver);
    }

    private void notifyColorChangeObservers(String color) {
    	for(ColorChangeObserver obs : colorChangeObservers) {
    		obs.onColorChange(color);
    	}
    }
    
    @Override
	public void setSecondaryColor(String color) {
    	drawer.setSecondaryColor(color);
		
	}
	@Override
	public String getSecondaryColor() {
		return drawer.getSecondaryColor();
	}
	
	@Override
    public ITablePanel createTablePanel() {
        setTablePanel(new TabulatorTablePanel(getTabLabel(), getId(), this));
        return tablePanel;
    }

    @Override
    public boolean isSampEnabled() {
        return true;
    }

    @Override
    public boolean isRefreshable() {
        return isRefreshable;
    }
    
    @Override
    public void setRefreshable(boolean isRefreshable) {
        this.isRefreshable = isRefreshable;
    }

    @Override
    public void setCustomRefreshable(boolean customRefreshable) {
        this.customRefreshable = customRefreshable;
    }

    @Override
    public boolean isCustomRefreshable() {
        return customRefreshable;
    }

    @Override
    public boolean isCustomizable() {
        return true;
    }

    @Override
    public void coneSearch(final SkyViewPosition conePos) {
        drawer = combinedDrawer;
        tablePanel.setMOCMode(false);
        tablePanel.notifyObservers();

        if(mocEntity != null){
	        mocEntity.clearAll();
	        mocEntity.setShouldBeShown(false);
        }
        String url = descriptor.createTapUrl(metadataService.getRequestUrl(), metadataService.getMetadataAdqlRadial(getDescriptor(), conePos), EsaSkyConstants.JSON);

        clearAll();
        tablePanel.insertData(url);
    }

    @Override
    public StylePanel createStylePanel() {
        Boolean showAvgProperMotion = null;
        if(secondaryShapeAdder != null) {
            showAvgProperMotion = combinedDrawer.getShowAvgProperMotion();
        }
        
        stylePanel = new StylePanel(getId(), getTabLabel(), getColor(), getSize(), getShapeType(),
                getLineStyle(), getSecondaryColor(), combinedDrawer.getSecondaryScale(), showAvgProperMotion, 
                combinedDrawer.getUseMedianOnAvgProperMotion(), new StylePanelCallback() {

            @Override
            public void onShapeSizeChanged(double value) {
            	if(mocEntity != null && mocEntity.isShouldBeShown()) {
            		mocEntity.setSizeRatio(value);
            	}else {
            		setSizeRatio(value);
            	}
            }

            @Override
            public void onShapeColorChanged(String color) {
               setPrimaryColor(color);
            }
                
            @Override
            public void onLineStyleChanged(String lineStyle) {
            	if(mocEntity != null && mocEntity.isShouldBeShown()) {
            		mocEntity.setLineStyle(lineStyle);
            	}else {
            		setLineStyle(lineStyle);
            	}
            }

            @Override
            public void onShapeChanged(String shape) {
                setShapeType(shape);
            }

            @Override
            public void onSecondaryShapeScaleChanged(double value) {
                combinedDrawer.setSecondaryScale(value);
            }

            @Override
            public void onSecondaryColorChanged(String color) {
                descriptor.setSecondaryColor(color);
                combinedDrawer.setSecondaryColor(color);
            }

            @Override
            public void onArrowAvgCheckChanged(boolean checkedOne, boolean checkedTwo) {
                combinedDrawer.setShowAvgProperMotion(checkedOne, checkedTwo);
            }
        });
        
        return stylePanel;
    }
    
    

    @Override
	public void setStylePanelVisibility() {
		if(stylePanel != null) {
			
			if(getShapeType() != null) {
				stylePanel.showShapeTypeDropDown(getShapeType());
			}else {
				stylePanel.hideShapeTypeDropDown();
			}
			
			setStylePanelLineStyleVisibility();
			setStylePanelSecondaryContainerVisibility();
			
			if(mocEntity != null && mocEntity.isShouldBeShown()) {
				stylePanel.showPrimary(mocEntity.getColor(), mocEntity.getSize() );
			}else {
				stylePanel.showPrimary(getColor(), getSize() );
			}
			
		}
	}
    private void setStylePanelSecondaryContainerVisibility() {
        if((descriptor.hasProperMotion() || descriptor.getCategory().equals(EsaSkyWebConstants.CATEGORY_SSO))
                && (mocEntity == null || !mocEntity.isShouldBeShown())) {
            Boolean showAvgProperMotion = null;
            if(secondaryShapeAdder != null) {
                showAvgProperMotion = combinedDrawer.getShowAvgProperMotion();
            }
        	stylePanel.showSecondaryContainer(descriptor.getSecondaryColor(), combinedDrawer.getSecondaryScale(),
        			showAvgProperMotion, combinedDrawer.getUseMedianOnAvgProperMotion());
        }else {
        	stylePanel.hideSecondaryContainer();
        }
    }
    private void setStylePanelLineStyleVisibility() {
        if(mocEntity != null && mocEntity.isShouldBeShown()) {
        	stylePanel.showLineStyleDropDown(mocEntity.getLineStyle());
        }else if (getLineStyle() != null){
        	stylePanel.showLineStyleDropDown(getLineStyle());
        }else {
        	stylePanel.hideLineStyleDropDown();
        }
    }
    
	@Override
    public String getShapeType() {
        return drawer.getShapeType();
    }

    @Override
    public void setShapeType(String shapeType) {
        drawer.setShapeType(shapeType);
    }
    
    @Override
    public String getLineStyle() {
    	if(mocEntity != null && mocEntity.isShouldBeShown()) {
    		return mocEntity.getLineStyle();
    	}
    	return drawer.getLineStyle();
    }
    
    @Override
    public void setLineStyle(String lineStyle) {
    	drawer.setLineStyle(lineStyle);
    }
    
    @Override
    public void onShapeSelection(AladinShape shape) {
    	int shapeId =  Integer.parseInt(shape.getId());
    	if(shapeRecentlySelected.contains(shapeId)) {
    		shapeRecentlySelected.remove(new Integer(shapeId));
    		return;
    	}
    	
    	selectShapes(shapeId);
    	
    	if(tablePanel != null) {
    		select();
    		tablePanel.selectRow(shapeId);
    	}
        if(shape.getRa() != null && shape.getDec() != null) {
            tooltip = new CatalogueTooltip(shape);
            CommonEventBus.getEventBus().fireEvent(new AddShapeTooltipEvent(tooltip));
        }
    }

    @Override
    public void onMultipleShapesSelection(LinkedList<AladinShape> shapes) {
    	int[] shapeIds = new int[shapes.size()];
    	int i = 0;
    	for(AladinShape shape : shapes) {
	    	int shapeId =  Integer.parseInt(shape.getId());
	    	shapeIds[i++] = shapeId;
	    	
	    	selectShapes(shapeId);
    	}
    	if(tablePanel != null) {
    		tablePanel.selectRows(shapeIds);
    	}
    	select();
    	
    }

    @Override
    public void onMultipleShapesDeselection(LinkedList<AladinShape> shapes) {
    	int[] shapeIds = new int[shapes.size()];
    	int i = 0;
    	for(AladinShape shape : shapes) {
    		int shapeId =  Integer.parseInt(shape.getId());
    		shapeIds[i++] = shapeId;
    		
    		deselectShapes(shapeId);
    	}
    	if(tablePanel != null) {
    		tablePanel.deselectRows(shapeIds);
    	}
    }
    
    @Override
    public void onShapeDeselection(AladinShape shape) {
        if(tablePanel != null) {
        	tablePanel.deselectRow(new Integer(shape.getId()));
        }
        if(tooltip != null) {
            tooltip.removeFromParent();
            tooltip = null;
        }
    }

    @Override
    public void onShapeHover(AladinShape shape) {
    	if(tablePanel != null) {
    		tablePanel.hoverStartRow(new Integer(shape.getId()));
    	}
	}

    @Override
    public void onShapeUnhover(AladinShape shape) {
        if(tablePanel != null) {
        	tablePanel.hoverStopRow(new Integer(shape.getId()));
        }
    }

    @Override
    public void select() {
    	if(tablePanel != null) {
    		tablePanel.selectTablePanel();
    	}
    }

	@Override
	public ITablePanel getTablePanel() {
		return tablePanel;
	}
	
	@Override
	public void setTablePanel(ITablePanel panel) {
		this.tablePanel = panel;
		
	}

	@Override
	public String getHelpText() {
		return TextMgr.getInstance().getText("resultsPresenter_helpDescription_"
                + getDescriptor().getCategory() + "_" + descriptor.getMission());
	}

    @Override
    public String getHelpTitle() {
        return this.getDescriptor().getLongName();
    }
    
	@Override
	public Shape getShape(int shapeId) {
		return drawer.getShape(shapeId);
	}
	
	public MOCEntity getMocEntity() {
		return mocEntity;
	}
	
	public void setMocEntity(MOCEntity mocEntity) {
		this.mocEntity = mocEntity;
	}
	
	@Override
	public TabulatorSettings getTabulatorSettings() {
		TabulatorSettings settings = new TabulatorSettings();

        if (descriptor != null) {
            boolean isPub = getDescriptor().getCategory().equals(EsaSkyWebConstants.CATEGORY_PUBLICATIONS);
            boolean isExternal = getDescriptor().getCategory().equals(EsaSkyWebConstants.CATEGORY_EXTERNAL);
            settings.setAddSendToVOApplicationColumn(descriptor.isSampEnabled());

            if (isExternal && descriptor.getArchiveProductURI() != null && descriptor.getArchiveBaseURL().toLowerCase().contains("datalink")) {
                settings.setAddDatalinkLink2ArchiveColumn(!Objects.equals(descriptor.getLongName(), "rassfsc"));
            } else {
                settings.setAddLink2ArchiveColumn(getDescriptor().getArchiveProductURI() != null && !isPub);
            }

            settings.setAddLink2AdsColumn(isPub);
            settings.setAddSourcesInPublicationColumn(isPub);
            settings.setAddSelectionColumn(true);
            settings.setUseUcd(true);
            settings.setShowDetailedErrors(descriptor.isExternal() && descriptor.isCustom());
            settings.setFovLimiterDisabled(descriptor.isFovLimitDisabled());
        }

		
		return settings;
	}
	
	@Override
	public String getQuery() {
		return this.adql;
	}

    @Override
	public void setQuery(String query) {
		this.adql = query;
        notifyQueryChangedObserver(query);
	}

    public void registerQueryChangedObserver(QueryChangeObserver queryChangeObserver) {
        queryChangeObservers.add(queryChangeObserver);
    }

    public void unregisterQueryChangedObserver(QueryChangeObserver queryChangeObserver) {
        queryChangeObservers.remove(queryChangeObserver);
    }

    private void notifyQueryChangedObserver(String query) {
        for(QueryChangeObserver obs : queryChangeObservers) {
            obs.onQueryChange(query);
        }
    }

    @Override
    public int getNumberOfShapes() {
    	return drawer.getNumberOfShapes();
    }

}
