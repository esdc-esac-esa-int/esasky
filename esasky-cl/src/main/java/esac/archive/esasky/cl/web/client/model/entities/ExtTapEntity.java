package esac.archive.esasky.cl.web.client.model.entities;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.Image;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.MetadataDescriptor;
import esac.archive.esasky.ifcs.model.shared.ColumnType;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.AladinShape;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.Modules;
import esac.archive.esasky.cl.web.client.callback.GetMissionDataCountRequestCallback;
import esac.archive.esasky.cl.web.client.callback.GetMissionDataCountRequestCallback.OnComplete;
import esac.archive.esasky.cl.web.client.callback.MetadataCallback;
import esac.archive.esasky.cl.web.client.event.AddShapeTooltipEvent;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.PolygonShape;
import esac.archive.esasky.cl.web.client.model.Shape;
import esac.archive.esasky.cl.web.client.model.ShapeId;
import esac.archive.esasky.cl.web.client.model.SourceShape;
import esac.archive.esasky.cl.web.client.model.SourceShapeType;
import esac.archive.esasky.cl.web.client.model.TapMetadata;
import esac.archive.esasky.cl.web.client.model.TapRowList;
import esac.archive.esasky.cl.web.client.query.AbstractTAPService;
import esac.archive.esasky.cl.web.client.query.TAPUtils;
import esac.archive.esasky.cl.web.client.status.CountObserver;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.CoordinateUtils;
import esac.archive.esasky.cl.web.client.utility.DeviceUtils;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.JSONUtils;
import esac.archive.esasky.cl.web.client.utility.SourceConstant;
import esac.archive.esasky.cl.web.client.view.allskypanel.CatalogueTooltip;
import esac.archive.esasky.cl.web.client.view.allskypanel.Tooltip;
import esac.archive.esasky.cl.web.client.view.resultspanel.ExtTapTablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.GeneralJavaScriptObject;
import esac.archive.esasky.cl.web.client.view.resultspanel.ITablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorTablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.stylemenu.StylePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.stylemenu.StylePanel.StylePanelCallback;

public class ExtTapEntity implements GeneralEntityInterface {

    public interface SecondaryShapeAdder{
        void createSpecializedOverlayShape(Map<String, Object> details);
        void addSecondaryShape(GeneralJavaScriptObject rowData, String ra, String dec, Map<String, String> details);
    }

    protected DefaultEntity defaultEntity;
    protected IShapeDrawer drawer;
    protected CombinedSourceFootprintDrawer combinedDrawer;
    private IDescriptor descriptor;
    private MOCEntity mocEntity;
    private AbstractTAPService metadataService;
    private SecondaryShapeAdder secondaryShapeAdder;
    protected ITablePanel tablePanel;
    protected Tooltip tooltip;

    public ExtTapEntity(IDescriptor descriptor, CountStatus countStatus,
            SkyViewPosition skyViewPosition, String esaSkyUniqId, AbstractTAPService metadataService, SecondaryShapeAdder secondaryShapeAdder) {
        this(descriptor, countStatus, skyViewPosition, esaSkyUniqId, metadataService, CombinedSourceFootprintDrawer.DEFAULT_SOURCE_SIZE, 
                SourceShapeType.SQUARE.getName(), secondaryShapeAdder);

    }
    public ExtTapEntity(IDescriptor descriptor, CountStatus countStatus,
            SkyViewPosition skyViewPosition, String esaSkyUniqId, AbstractTAPService metadataService) {
        this(descriptor, countStatus, skyViewPosition, esaSkyUniqId, metadataService, 
                CombinedSourceFootprintDrawer.DEFAULT_SOURCE_SIZE, SourceShapeType.SQUARE.getName());
    }

    public ExtTapEntity(IDescriptor descriptor, CountStatus countStatus,
            SkyViewPosition skyViewPosition, String esaSkyUniqId, 
            AbstractTAPService metadataService, int shapeSize, Object shapeType) {
        this(descriptor, countStatus, skyViewPosition, esaSkyUniqId, metadataService, shapeSize, shapeType, null);
    }

    public ExtTapEntity(IDescriptor descriptor, CountStatus countStatus,
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

        defaultEntity = new DefaultEntity(descriptor, countStatus, skyViewPosition, esaSkyUniqId,
                drawer, metadataService);
    }

    protected ShapeBuilder shapeBuilder = new ShapeBuilder() {
        @Override
        public Shape buildShape(int rowId, TapRowList rowList, GeneralJavaScriptObject rowData) {
            String stcs = rowData.getStringProperty(getDescriptor().getTapSTCSColumn());
            if(stcs == null || stcs.toUpperCase().startsWith("POSITION")) {
                return catalogBuilder(rowId, rowData);
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

    public SourceShape catalogBuilder(int shapeId, GeneralJavaScriptObject rowData) {
        String ra = rowData.getStringProperty(getDescriptor().getTapRaColumn());
        String dec = rowData.getStringProperty(getDescriptor().getTapDecColumn());
        String sourceName = rowData.getStringProperty(getDescriptor().getUniqueIdentifierField());

        SourceShape mySource = new SourceShape();
        mySource.setShapeId(shapeId);

        mySource.setRa(ra);
        mySource.setDec(dec);
        mySource.setSourceName(sourceName);

        Map<String, String> details = new HashMap<String, String>();

        details.put(SourceConstant.SOURCE_NAME, mySource.getSourceName());

        details.put(SourceConstant.CATALOGE_NAME, getEsaSkyUniqId());
        details.put(SourceConstant.ID, Integer.toString(shapeId));

        if (this.getDescriptor().getExtraPopupDetailsByTapName() == null) {
            details.put(SourceConstant.EXTRA_PARAMS, null);
        } else {
            details.put(SourceConstant.EXTRA_PARAMS,
                    this.getDescriptor().getExtraPopupDetailsByTapName());
            String[] extraDetailsTapName = this.getDescriptor().getExtraPopupDetailsByTapName()
                    .split(",");

            for (String currTapName : extraDetailsTapName) {
                MetadataDescriptor cmd = this.getDescriptor()
                        .getMetadataDescriptorByTapName(currTapName);
                Integer precision = null;
                String value = rowData.getStringProperty(currTapName);
                if (cmd != null && cmd.getMaxDecimalDigits() != null
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
                details.put(currTapName, value);
            }
        }

        if(secondaryShapeAdder != null) {
            secondaryShapeAdder.addSecondaryShape(rowData, ra, dec, details);
        }

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


    public boolean hasReachedFovLimit() {
        return descriptor.getFovLimit() > 0 && CoordinateUtils.getCenterCoordinateInJ2000().getFov() > descriptor.getFovLimit();
    }

    public class MocBuilder implements ShapeBuilder{

        @Override
        public Shape buildShape(int rowId, TapRowList rowList, GeneralJavaScriptObject row) {
            PolygonShape shape = new PolygonShape();
            String stcs = row.invokeFunction("getData").getStringProperty(getDescriptor().getTapSTCSColumn());
            shape.setStcs(stcs);
            shape.setJsObject(AladinLiteWrapper.getAladinLite().createFootprintFromSTCS(shape.getStcs()));
            return shape;
        }
    }


    @Override
    public void fetchData() {
        if (getCountStatus().hasMoved(descriptor.getMission()) && descriptor.getFovLimit() == 0 ) {
	        getCountStatus().registerObserver(new CountObserver() {
				@Override
				public void onCountUpdate(int newCount) {
	                fetchShapesAndMetadata();
					getCountStatus().unregisterObserver(this);
				}
			});
        } else {
            fetchShapesAndMetadata();
        }
    }
    
    private void fetchShapesAndMetadata() {
        clearAll();
        int shapeLimit = descriptor.getShapeLimit();
        if (shapeLimit > 0 && DeviceUtils.isMobile()){
            shapeLimit = EsaSkyWebConstants.MAX_SHAPES_FOR_MOBILE;
        }

        if (shapeLimit > 0 && getCountStatus().getCount(descriptor.getMission()) > shapeLimit) {
            Log.debug("Showing dynamic moc");
            if(mocEntity == null){
                this.mocEntity = new MOCEntity(descriptor, getCountStatus(), this, defaultEntity);
            }
            mocEntity.setTablePanel(tablePanel);
            mocEntity.refreshMOC();
        } else if(hasReachedFovLimit()) {
            Log.debug("Showing fov limit moc. FoVLimit = " + descriptor.getFovLimit());
            drawer = new MocDrawer(descriptor.getPrimaryColor());
            defaultEntity.setDrawer(drawer);
            getMocMetadata();
        } else {
            fetchDataWithoutMOC();
        }
    }
    
    private void updateCount(OnComplete onComplete) {
        String url = metadataService.getCount(AladinLiteWrapper.getAladinLite(), descriptor);
        tablePanel.clearTable();
        JSONUtils.getJSONFromUrl(url, new GetMissionDataCountRequestCallback(this, 
                tablePanel, 
                TextMgr.getInstance().getText("GetMissionDataCountRequestCallback_searchingInArchive").replace("$NAME$", getDescriptor().getGuiShortName()),
                url, onComplete));
    }

    @Override
    public void fetchDataWithoutMOC() {
        Log.debug("Showing real data");
        drawer = combinedDrawer;
        defaultEntity.setDrawer(drawer);
        if(mocEntity != null){
	        mocEntity.clearAll();
	        mocEntity.setShouldBeShown(false);
        }

        clearAll();
        Log.debug(descriptor.getTapQuery(metadataService.getRequestUrl(), defaultEntity.getMetadataAdql(tablePanel.getFilterString()), EsaSkyConstants.JSON));
        tablePanel.insertData(null, descriptor.getTapQuery(metadataService.getRequestUrl(), defaultEntity.getMetadataAdql(tablePanel.getFilterString()), EsaSkyConstants.JSON));
    }

    private void getMocMetadata() {

        tablePanel.clearTable();
        String adql = metadataService.getMocAdql(getDescriptor(), "");

        String url = TAPUtils.getTAPQuery(URL.decodeQueryString(adql), EsaSkyConstants.JSON).replaceAll("#", "%23");

        JSONUtils.getJSONFromUrl(url, new MetadataCallback(tablePanel, adql, TextMgr.getInstance().getText("JsonRequestCallback_retrievingMOC"), new MetadataCallback.OnComplete() {

            @Override
            public void onComplete() {
                tablePanel.setEmptyTable(TextMgr.getInstance().getText("commonObservationTablePanel_showingGlobalSkyCoverage"));
            }
        }));

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
        Log.debug("Count is " + getCountStatus().getCount(descriptor.getMission()));
        drawer.addShapes(rowList, javaScriptObject);
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
    public void clearAll() {
        defaultEntity.clearAll();
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
        if(Modules.useTabulator) {
            tablePanel = new TabulatorTablePanel(getTabLabel(), getEsaSkyUniqId(), this);
        } else {
            tablePanel = new ExtTapTablePanel(getTabLabel(), getEsaSkyUniqId(), this);
        }
        return tablePanel;
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
    public void refreshData() {
        fetchData();
    }

    @Override
    public void coneSearch(final SkyViewPosition conePos) {
        if (getCountStatus().hasMoved(descriptor.getMission()) && descriptor.getFovLimit() ==  0 ) {
            updateCount(new GetMissionDataCountRequestCallback.OnComplete() {

                @Override
                public void onComplete() {
                    defaultEntity.coneSearch(conePos);
                }
            });
        } 
    }

    @Override
    public StylePanel createStylePanel() {
        Boolean showAvgProperMotion = null;
        if(secondaryShapeAdder != null) {
            showAvgProperMotion = combinedDrawer.getShowAvgProperMotion();
        }
        
        return new StylePanel(getEsaSkyUniqId(), getTabLabel(), getColor(), getSize(), 
                getShapeType(), descriptor.getSecondaryColor(), combinedDrawer.getSecondaryScale(), showAvgProperMotion, 
                combinedDrawer.getUseMedianOnAvgProperMotion(), new StylePanelCallback() {

            @Override
            public void onShapeSizeChanged(double value) {
                setSizeRatio(value);
            }

            @Override
            public void onShapeColorChanged(String color) {
                getDescriptor().setPrimaryColor(color);
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
    }

    @Override
    public String getShapeType() {
        return defaultEntity.getShapeType();
    }

    @Override
    public void setShapeType(String shapeType) {
        defaultEntity.setShapeType(shapeType);
    }
    
    @Override
    public void onShapeSelection(AladinShape shape) {
        select();
        tablePanel.selectRow(new Integer(shape.getId()));
        if(shape.getRa() != null && shape.getDec() != null) {
            tooltip = new CatalogueTooltip(shape);
            CommonEventBus.getEventBus().fireEvent(new AddShapeTooltipEvent(tooltip));
        }
    }
    
    @Override
    public void onShapeDeselection(AladinShape shape) {
        tablePanel.deselectRow(new Integer(shape.getId()));
        if(tooltip != null) {
            tooltip.removeFromParent();
            tooltip = null;
        }
    }

    @Override
    public void onShapeHover(AladinShape shape) {
        tablePanel.hoverStartRow(new Integer(shape.getId()));
    }

    @Override
    public void onShapeUnhover(AladinShape shape) {
        tablePanel.hoverStopRow(new Integer(shape.getId()));
    }

    @Override
    public void select() {
        tablePanel.selectTablePanel();
    }

}
