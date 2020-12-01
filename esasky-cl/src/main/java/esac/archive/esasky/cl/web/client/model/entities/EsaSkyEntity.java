package esac.archive.esasky.cl.web.client.model.entities;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.Image;

import esac.archive.esasky.ifcs.model.coordinatesutils.Coordinate;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.MetadataDescriptor;
import esac.archive.esasky.ifcs.model.shared.ColumnType;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.AladinShape;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.AddShapeTooltipEvent;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPopEvent;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPushEvent;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.LineStyle;
import esac.archive.esasky.cl.web.client.model.MOCInfo;
import esac.archive.esasky.cl.web.client.model.PolygonShape;
import esac.archive.esasky.cl.web.client.model.Shape;
import esac.archive.esasky.cl.web.client.model.SourceShape;
import esac.archive.esasky.cl.web.client.model.SourceShapeType;
import esac.archive.esasky.cl.web.client.model.TapMetadata;
import esac.archive.esasky.cl.web.client.model.TapRowList;
import esac.archive.esasky.cl.web.client.presenter.MainPresenter;
import esac.archive.esasky.cl.web.client.query.AbstractTAPService;
import esac.archive.esasky.cl.web.client.repository.EntityRepository;
import esac.archive.esasky.cl.web.client.status.CountObserver;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.DeviceUtils;
import esac.archive.esasky.cl.web.client.utility.SourceConstant;
import esac.archive.esasky.cl.web.client.view.allskypanel.CatalogueTooltip;
import esac.archive.esasky.cl.web.client.view.allskypanel.Tooltip;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.cl.web.client.view.resultspanel.ITablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorTablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.stylemenu.StylePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.stylemenu.StylePanel.StylePanelCallback;

public class EsaSkyEntity implements GeneralEntityInterface {

    public interface SecondaryShapeAdder{
        void createSpecializedOverlayShape(Map<String, Object> details);
        void addSecondaryShape(GeneralJavaScriptObject rowData, String ra, String dec, Map<String, String> details);
    }

    protected IShapeDrawer drawer;
    protected CombinedSourceFootprintDrawer combinedDrawer;
    protected IDescriptor descriptor;
    private MOCEntity mocEntity;
    protected AbstractTAPService metadataService;
    private SecondaryShapeAdder secondaryShapeAdder;
    protected ITablePanel tablePanel;
    protected Tooltip tooltip;
    private SkyViewPosition skyViewPosition;
    private String histoLabel;
    private String esaSkyUniqId;
    private TapRowList metadata;
    private CountStatus countStatus;
    private boolean isRefreshable = true;
    private StylePanel stylePanel;
    private LinkedList<ColorChangeObserver> colorChangeObservers = new LinkedList<>();


    public EsaSkyEntity(IDescriptor descriptor, CountStatus countStatus,
            SkyViewPosition skyViewPosition, String esaSkyUniqId, AbstractTAPService metadataService, SecondaryShapeAdder secondaryShapeAdder) {
        this(descriptor, countStatus, skyViewPosition, esaSkyUniqId, metadataService, CombinedSourceFootprintDrawer.DEFAULT_SOURCE_SIZE, 
                SourceShapeType.SQUARE.getName(), secondaryShapeAdder);

    }
    public EsaSkyEntity(IDescriptor descriptor, CountStatus countStatus,
            SkyViewPosition skyViewPosition, String esaSkyUniqId, AbstractTAPService metadataService) {
        this(descriptor, countStatus, skyViewPosition, esaSkyUniqId, metadataService, 
                CombinedSourceFootprintDrawer.DEFAULT_SOURCE_SIZE, SourceShapeType.SQUARE.getName());
    }

    public EsaSkyEntity(IDescriptor descriptor, CountStatus countStatus,
            SkyViewPosition skyViewPosition, String esaSkyUniqId, 
            AbstractTAPService metadataService, int shapeSize, Object shapeType) {
        this(descriptor, countStatus, skyViewPosition, esaSkyUniqId, metadataService, shapeSize, shapeType, null);
    }

    public EsaSkyEntity(IDescriptor descriptor, CountStatus countStatus,
            SkyViewPosition skyViewPosition, String esaSkyUniqId, 
            AbstractTAPService metadataService, int shapeSize, Object shapeType, SecondaryShapeAdder secondaryShapeAdder) {
        this.metadataService = metadataService;
        this.descriptor = descriptor;
        this.secondaryShapeAdder = secondaryShapeAdder;

        JavaScriptObject footprints = AladinLiteWrapper.getAladinLite().createOverlay(esaSkyUniqId,
                descriptor.getPrimaryColor());

        Map<String, Object> details = new HashMap<String, Object>();

        if (secondaryShapeAdder != null) {
            secondaryShapeAdder.createSpecializedOverlayShape(details);
        } else {
            details.put("shape", shapeType);
        }

        JavaScriptObject catalogue = AladinLiteWrapper.getAladinLite().createCatalogWithDetails(
                esaSkyUniqId, shapeSize, descriptor.getPrimaryColor(), details);

        combinedDrawer = new CombinedSourceFootprintDrawer(catalogue, footprints, shapeBuilder, shapeType);
        drawer = combinedDrawer;
        
        drawer.setPrimaryColor(descriptor.getPrimaryColor());
        drawer.setSecondaryColor(descriptor.getSecondaryColor());
        
        this.skyViewPosition = skyViewPosition;
        this.esaSkyUniqId = esaSkyUniqId;
        this.metadataService = metadataService;
        this.countStatus = countStatus;
        

    }

    protected ShapeBuilder shapeBuilder = new ShapeBuilder() {
        @Override
        public Shape buildShape(int rowId, TapRowList rowList, GeneralJavaScriptObject rowData) {
        	String stcs = null;
        	if(getDescriptor().getTapSTCSColumn() != "") {
        		stcs = rowData.getStringProperty(getDescriptor().getTapSTCSColumn());
        	}
            if(stcs == null || stcs.toUpperCase().startsWith("POSITION")) {
                return catalogBuilder(rowId, rowData);
            }

            stcs = makeSureSTCSHasFrame(stcs);
            PolygonShape polygon = new PolygonShape();
            polygon.setShapeId(rowId);
            polygon.setStcs(stcs);
            polygon.setJsObject(((GeneralJavaScriptObject) AladinLiteWrapper.getAladinLite().createFootprintFromSTCS(
                    polygon.getStcs(), rowId)));
            
            polygon.setShapeId(rowId);
            String shapeName = rowData.getStringProperty(getDescriptor().getUniqueIdentifierField());
            polygon.setShapeName(shapeName);
            String ra = rowData.getStringProperty(getDescriptor().getTapRaColumn());
            String dec = rowData.getStringProperty(getDescriptor().getTapDecColumn());
            polygon.setRa(ra);
            polygon.setDec(dec);
            return polygon;
        }
    };

    public SourceShape catalogBuilder(int shapeId, GeneralJavaScriptObject rowData) {
        String ra = rowData.getStringProperty(getDescriptor().getTapRaColumn());
        String dec = rowData.getStringProperty(getDescriptor().getTapDecColumn());
        String sourceName = rowData.getStringProperty(getDescriptor().getUniqueIdentifierField());

        SourceShape mySource = new SourceShape();
        mySource.setShapeId(shapeId);

        mySource.setRa(ra);
        mySource.setDec(dec);
        mySource.setShapeName(sourceName);

        Map<String, String> details = new HashMap<String, String>();

        details.put(SourceConstant.SOURCE_NAME, mySource.getShapeName());

        details.put(SourceConstant.CATALOGE_NAME, getEsaSkyUniqId());
        details.put(SourceConstant.ID, Integer.toString(shapeId));

        if (this.getDescriptor().getExtraPopupDetailsByTapName() == null) {
            details.put(SourceConstant.EXTRA_PARAMS, null);
        } else {
            String[] extraDetailsTapName = this.getDescriptor().getExtraPopupDetailsByTapName()
                    .split(",");
            String extraDetailsLabels = "";
            for (String currTapName : extraDetailsTapName) {
                extraDetailsLabels += getKeyToShow(currTapName) + ",";
            }
            if(extraDetailsLabels.length() > 0) {
                extraDetailsLabels = extraDetailsLabels.substring(0, extraDetailsLabels.length() - 1);
            }
            details.put(SourceConstant.EXTRA_PARAMS, extraDetailsLabels);

            for (String currTapName : extraDetailsTapName) {
                MetadataDescriptor cmd = this.getDescriptor()
                        .getMetadataDescriptorByTapName(currTapName);
                Integer precision = null;
                String value = rowData.getStringProperty(currTapName);
                if (value != null && cmd != null && cmd.getMaxDecimalDigits() != null
                        && (cmd.getType() == ColumnType.RA || cmd.getType() == ColumnType.DEC || cmd
                        .getType() == ColumnType.DOUBLE)) {
                    StringBuilder sb = new StringBuilder();
                    precision = cmd.getMaxDecimalDigits();
                    sb.append("#0.");
                    if (precision != null) {
                        for (int i = 0; i < precision; i++) {
                            sb.append("0");
                        }
                    } else {
                        sb.append("00");
                    }
                	value = NumberFormat.getFormat(sb.toString()).format(Double.parseDouble(value));
                }
                details.put(getKeyToShow(currTapName), value);
            }
        }

        if(secondaryShapeAdder != null) {
            secondaryShapeAdder.addSecondaryShape(rowData, ra, dec, details);
        }

        mySource.setJsObject((GeneralJavaScriptObject) AladinLiteWrapper.getAladinLite().newApi_createSourceJSObj(
                mySource.getRa(), mySource.getDec(), details, shapeId));
        return mySource;
    }

    private String getKeyToShow(String tapName) {
        MetadataDescriptor metadataDescriptor = descriptor.getMetadataDescriptorByTapName(tapName);
        return metadataDescriptor == null ? tapName : TextMgr.getDefaultInstance().getText(metadataDescriptor.getLabel());
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

    public class MocBuilder implements ShapeBuilder{

        @Override
        public Shape buildShape(int rowId, TapRowList rowList, GeneralJavaScriptObject row) {
            PolygonShape shape = new PolygonShape();
            String stcs = row.invokeFunction("getData").getStringProperty(getDescriptor().getTapSTCSColumn());
            shape.setStcs(stcs);
            shape.setJsObject(((GeneralJavaScriptObject) AladinLiteWrapper.getAladinLite().createFootprintFromSTCS(shape.getStcs())).getProperty("0"));
            return shape;
        }
    }

    @Override
    public void fetchData() {
        if (getCountStatus().hasMoved(descriptor) && descriptor.getFovLimit() == 0 ) {
	        getCountStatus().registerObserver(new CountObserver() {
				@Override
				public void onCountUpdate(int newCount) {
				    CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPopEvent("WaitingForCount" + getEsaSkyUniqId()));
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

         if(mocEntity != null){
 	        mocEntity.clearAll();
 	        mocEntity.setShouldBeShown(false);
         }
         String url = descriptor.getTapQuery(metadataService.getRequestUrl(), adql, EsaSkyConstants.JSON);

         clearAll();
         tablePanel.insertData(url);
    }
    
    protected void fetchShapesAndMetadata() {
        clearAll();
        int shapeLimit = DeviceUtils.getDeviceShapeLimit(descriptor);

        if (shapeLimit > 0 && getCountStatus().getCount(descriptor) > shapeLimit) {
            Log.debug("Showing dynamic moc");
            if(mocEntity == null){
                this.mocEntity = new MOCEntity(descriptor, getCountStatus(), this);
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
        String url = descriptor.getTapQuery(metadataService.getRequestUrl(), metadataService.getMetadataAdql(getDescriptor(), tablePanel.getFilterString()), EsaSkyConstants.JSON);

        clearAll();
        tablePanel.insertData(url);
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
    	tablePanel.insertData(descriptor.getTapQuery(metadataService.getRequestUrl(), metadataService.getMetadataFromMOCPixelsADQL(getDescriptor(), whereQuery), EsaSkyConstants.JSON));
    }
    
    public void fetchDataWithoutMOC(MOCInfo mocInfo) {

    	//Open this in a new table
    	
    	String adql = metadataService.getMetadataFromMOCPixel(descriptor, mocInfo);
    	
    	String filter = tablePanel.getFilterString();
    	if(filter != "") {
    		adql += " AND " + filter;
    	}
        
        GeneralEntityInterface entity = EntityRepository.getInstance().createEntity(descriptor);
        MainPresenter.getInstance().getRelatedMetadata(entity, adql);
        GeneralJavaScriptObject positionInfo = (GeneralJavaScriptObject)AladinLiteWrapper.getAladinLite().getQ3CIpix2Ang(mocInfo.order, mocInfo.ipix);
        GeneralJavaScriptObject center = positionInfo.getProperty("center");
        entity.setSkyViewPosition(new SkyViewPosition(new Coordinate(center.getDoubleProperty("0"), center.getDoubleProperty("1")), positionInfo.getDoubleProperty("fov")));
    	
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
    public void addShapes(GeneralJavaScriptObject javaScriptObject) {
        drawer.addShapes(javaScriptObject);
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
    public void deselectAllShapes() {
    	drawer.deselectAllShapes();
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
    public String getEsaSkyUniqId() {
        return esaSkyUniqId;
    }

    @Override
    public void setEsaSkyUniqId(String esaSkyUniqId) {
        this.esaSkyUniqId = esaSkyUniqId;
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
        return getDescriptor().getGuiLongName();
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
                int dataIndex = new Integer(tapRowList.getMetadata().indexOf(tapMetadata));
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
    public IDescriptor getDescriptor() {
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
    	drawer.setPrimaryColor(color);
		
	}
	@Override
	public String getSecondaryColor() {
		return drawer.getPrimaryColor();
	}
	
	@Override
    public ITablePanel createTablePanel() {
        setTablePanel(new TabulatorTablePanel(getTabLabel(), getEsaSkyUniqId(), this));
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
        String url = descriptor.getTapQuery(metadataService.getRequestUrl(), metadataService.getMetadataAdqlRadial(getDescriptor(), conePos), EsaSkyConstants.JSON);

        clearAll();
        tablePanel.insertData(url);
    }

    @Override
    public StylePanel createStylePanel() {
        Boolean showAvgProperMotion = null;
        if(secondaryShapeAdder != null) {
            showAvgProperMotion = combinedDrawer.getShowAvgProperMotion();
        }
        
        stylePanel = new StylePanel(getEsaSkyUniqId(), getTabLabel(), getColor(), getSize(), getShapeType(),
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
        if(descriptor.getSecondaryColor() != null && (mocEntity == null || !mocEntity.isShouldBeShown())) {
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
		return TextMgr.getInstance().getText("resultsPresenter_helpDescription_" + getDescriptor().getDescriptorId());
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
	
	
	

}
