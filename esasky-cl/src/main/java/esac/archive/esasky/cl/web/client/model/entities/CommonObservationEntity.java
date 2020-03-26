package esac.archive.esasky.cl.web.client.model.entities;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.allen_sauer.gwt.log.client.Log;
import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.Image;

import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.CommonObservationDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.Modules;
import esac.archive.esasky.cl.web.client.event.ESASkySampEvent;
import esac.archive.esasky.cl.web.client.model.PolygonShape;
import esac.archive.esasky.cl.web.client.model.Shape;
import esac.archive.esasky.cl.web.client.model.ShapeId;
import esac.archive.esasky.cl.web.client.model.TapRowList;
import esac.archive.esasky.cl.web.client.query.TAPMetadataObservationService;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.UncachedRequestBuilder;
import esac.archive.esasky.cl.web.client.utility.SampConstants.SampAction;
import esac.archive.esasky.cl.web.client.utility.samp.SampMessageItem;
import esac.archive.esasky.cl.web.client.utility.samp.SampXmlParser;
import esac.archive.esasky.cl.web.client.view.resultspanel.GeneralJavaScriptObject;
import esac.archive.esasky.cl.web.client.view.resultspanel.ITablePanel;

public abstract class CommonObservationEntity implements GeneralEntityInterface {

    protected DefaultEntity defaultEntity;
    protected IShapeDrawer drawer;
    private CommonObservationDescriptor descriptor;
    protected ShapeBuilder shapeBuilder = new ShapeBuilder() {

        @Override
        public Shape buildShape(int rowId, TapRowList rowList, GeneralJavaScriptObject row) {
            PolygonShape polygon = new PolygonShape();
            polygon.setShapeId(rowId);
            if(Modules.useTabulator) {
                polygon.setStcs(row.invokeFunction("getData").getStringProperty(getDescriptor().getTapSTCSColumn()));
            } else {
                polygon.setStcs((String) getTAPDataByTAPName(rowList, rowId, descriptor
                        .getTapSTCSColumn()));
            }
            polygon.setJsObject(AladinLiteWrapper.getAladinLite().createFootprintFromSTCS(
                    polygon.getStcs(), rowId));
            return polygon;
        }
    };

    public CommonObservationEntity(CommonObservationDescriptor descriptor,
            CountStatus countStatus, SkyViewPosition skyViewPosition, String esaSkyUniqId) {
        JavaScriptObject overlay = AladinLiteWrapper.getAladinLite().createOverlay(esaSkyUniqId,
                descriptor.getPrimaryColor());
        this.descriptor = descriptor;
        drawer = new CombinedSourceFootprintDrawer(AladinLiteWrapper.getAladinLite().createCatalog(
                esaSkyUniqId, CombinedSourceFootprintDrawer.DEFAULT_SOURCE_SIZE, descriptor.getPrimaryColor()), overlay, shapeBuilder);
        defaultEntity = new DefaultEntity(descriptor, countStatus, skyViewPosition, esaSkyUniqId,
                drawer, TAPMetadataObservationService.getInstance());

    }


    public interface DescriptorMapper extends ObjectMapper<IDescriptor> {}

    public void executeSampFileList(String obsId) {

        String completeUrl = EsaSkyWebConstants.TAP_CONTEXT + "/samp-files?";

        StringBuilder data = new StringBuilder();
        DescriptorMapper mapper = GWT.create(DescriptorMapper.class);

        String json = mapper.write(descriptor);

        data.append("descriptor="
                + URL.encodeQueryString(json));

        data.append("&observation_id=" + obsId);

        Log.debug("[CommonObservationRow][executeSampFileList]URL:"
                + completeUrl);
        Log.debug("[CommonObservationRow][executeSampFileList]JSON:"
                + data.toString());

        completeUrl = completeUrl + data.toString();
        Log.debug("[CommonObservationRow][executeSampFileList]CompleteURL:"
                + completeUrl);

        UncachedRequestBuilder requestBuilder = new UncachedRequestBuilder(
                RequestBuilder.GET, completeUrl);

        try {
            requestBuilder.sendRequest(null, new RequestCallback() {

                @Override
                public void onError(
                        final com.google.gwt.http.client.Request request,
                        final Throwable exception) {
                    Log.debug(
                            "[ESASkySampEventHandlerImpl/processEvent()] Failed file reading",
                            exception);
                }

                @Override
                public void onResponseReceived(final Request request,
                        final Response response) {
                    String data = "";
                    data = response.getText();
                    List<SampMessageItem> messageItems = SampXmlParser.parse(data);
                    try {

                        int counter = 0;
                        String tableNameTmp="";

                        // Send all URL to Samp
                        HashMap<String, String> sampUrlsPerMissionMap = new HashMap<String, String>();
                        for (SampMessageItem i : messageItems) {
                            // Prepare sending message
                            tableNameTmp = descriptor.getTapTable() + "_" + counter;
                            String fullUrl = descriptor.getDdBaseURL() + "retrieval_type=PRODUCT&hcss_urn="+ i.getUrn();
                            if (fullUrl.contains("README")) {
                                continue;
                            }
                            sampUrlsPerMissionMap.put(tableNameTmp, fullUrl);
                            Log.debug("SAMP URL=" + fullUrl);
                            counter++;
                        }
                        ESASkySampEvent sampEvent  = new ESASkySampEvent(SampAction.SEND_PRODUCT_TO_SAMP_APP, sampUrlsPerMissionMap);
                        CommonEventBus.getEventBus().fireEvent(sampEvent);

                    } catch (Exception e) {

                        Log.debug("[ESASkySampEventHandlerImpl/processEvent()] Exception in ESASkySampEventHandlerImpl.processEvent",e);

                        throw new IllegalStateException(
                                "[ESASkySampEventHandlerImpl.processEvent] Unexpected SampAction: SEND_VO_TABLE");
                    }
                }

            });
        } catch (RequestException e) {
            Log.debug(
                    "[ESASkySampEventHandlerImpl/processEvent()] Failed file reading",
                    e);
        }
    }

    public String buildSAMPURL(GeneralJavaScriptObject rowData) {
        String[] archiveProductURI = getDescriptor().getDdProductURI().split("@@@");
        String tapName = archiveProductURI[1];
        String valueURI = rowData.getStringProperty(tapName);
        return getDescriptor().getDdBaseURL()
                + getDescriptor().getDdProductURI().replace("@@@" + tapName + "@@@", valueURI);
    }


    @Override
    public void setColor(String color) {
        defaultEntity.setColor(color);
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
        defaultEntity.addShapes(rowList, javaScriptObject);
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
        return defaultEntity.getTabLabel();
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
    public Object getTAPDataByTAPName(TapRowList tapRowList, int rowIndex, String tapName) {
        return defaultEntity.getTAPDataByTAPName(tapRowList, rowIndex, tapName);
    }

    @Override
    public Double getDoubleByTAPName(TapRowList tapRowList, int rowIndex, String tapName, Double defaultValue) {
        return defaultEntity.getDoubleByTAPName(tapRowList, rowIndex, tapName, defaultValue);
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
    public void fetchData(ITablePanel tablePanel) {
        defaultEntity.fetchData(tablePanel);
    }

    @Override
    public void fetchDataWithoutMOC(ITablePanel tablePanel) {
        defaultEntity.fetchData(tablePanel);
    }

    @Override
    public void setShapeBuilder(ShapeBuilder shapeBuilder) {
        defaultEntity.setShapeBuilder(shapeBuilder);
    }

    @Override
    public boolean isSampEnabled() {
        return defaultEntity.isRefreshable();
    }

    @Override
    public boolean isRefreshable() {
        return defaultEntity.isRefreshable();
    }

    @Override
    public boolean hasDownloadableDataProducts() {
        return defaultEntity.hasDownloadableDataProducts();
    }

    @Override
    public boolean isCustomizable() {
        return defaultEntity.isCustomizable();
    }

    @Override
    public String getMetadataAdql() {
        return defaultEntity.getMetadataAdql();
    }

    @Override
    public CommonObservationDescriptor getDescriptor() {
        return descriptor;
    }

    @Override
    public ITablePanel createTablePanel() {
        return defaultEntity.createTablePanel();
    }


    @Override
    public Image getTypeLogo() {
        return defaultEntity.getTypeLogo();
    }

    @Override 
    public void refreshData(ITablePanel tablePanel) {
        fetchData(tablePanel);
    }

    @Override
    public void coneSearch(ITablePanel tablePanel, SkyViewPosition conePos) {
        defaultEntity.coneSearch(tablePanel, conePos);
    }
    
    @Override
    public String getShapeType() {
        return defaultEntity.getShapeType();
    }

    @Override
    public void setShapeType(String shapeType) {
        defaultEntity.setShapeType(shapeType);
    }
}
