package esac.archive.esasky.cl.web.client.utility;

import com.allen_sauer.gwt.log.client.Log;
import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import esac.archive.esasky.cl.web.client.model.entities.EsaSkyEntity;
import esac.archive.esasky.cl.web.client.model.entities.GeneralEntityInterface;
import esac.archive.esasky.cl.web.client.model.entities.ImageListEntity;
import esac.archive.esasky.cl.web.client.model.entities.PublicationsByAuthorEntity;
import esac.archive.esasky.cl.web.client.model.entities.PublicationsBySourceEntity;
import esac.archive.esasky.cl.web.client.model.entities.PublicationsEntity;
import esac.archive.esasky.cl.web.client.model.entities.SSOEntity;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.selectsky.SelectSkyPanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.ITablePanel;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.ExtTapDescriptorList;
import esac.archive.esasky.ifcs.model.shared.DatalabsData;

import java.util.Objects;
import java.util.function.Consumer;

import static esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants.DATALABS_EXPORT_URL;

public class DatalabsExport {
    public interface DatalabsDataMapper extends ObjectMapper<DatalabsData> {
    }

    public static final DatalabsDataMapper MAPPER = GWT.create(DatalabsDataMapper.class);

    public static void exportTablePanel(ITablePanel panel) {
        DatalabsData data = new DatalabsData();
        addHips(data);
        DatalabsData.DataEntity dataEntity = serializeEntity(panel.getEntity());
        data.addDataEntity(dataEntity);
        writeData(data, DatalabsExport::datalabsContentHandler);
    }

    private static void addHips(DatalabsData data) {
        SelectSkyPanel skyPanel = SelectSkyPanel.getInstance();
        skyPanel.getHipsList().forEach(row -> data.addHips(row.getSelectedHips().getSurveyName()));
    }

    public static boolean supportsJupyterDownload(final GeneralEntityInterface entity) {
        return entity instanceof EsaSkyEntity &&
                !(entity instanceof ImageListEntity ||
                        entity instanceof PublicationsEntity ||
                        entity instanceof SSOEntity);
        }

    private static DatalabsData.DataEntity serializeEntity(GeneralEntityInterface entity) {
        String authorId = null;
        String sourceId = null;
        String query = null;
        String levelDescriptor = null;
        if (entity instanceof PublicationsBySourceEntity) {
            PublicationsBySourceEntity publicationsBySourceEntity = (PublicationsBySourceEntity) entity;
            sourceId = publicationsBySourceEntity.getId();
        } else if (entity instanceof PublicationsByAuthorEntity) {
            PublicationsByAuthorEntity publicationsByAuthorEntity = (PublicationsByAuthorEntity) entity;
            authorId = publicationsByAuthorEntity.getId();
        } else {
            query = ((EsaSkyEntity) entity).getCurrentQueryWithFilters();
        }
        if (entity.getDescriptor().isExternal()) {
            CommonTapDescriptor descriptor = entity.getDescriptor();
            levelDescriptor = descriptor.getShortName();
            descriptor = descriptor.getParent();
            while (descriptor != null) {
                levelDescriptor = descriptor.getShortName() + "-" + levelDescriptor;
                descriptor = descriptor.getParent();
            }
            if (ExtTapUtils.getLevelDescriptor(levelDescriptor) == null) {
                levelDescriptor = null;
            }
        }
        return new DatalabsData.DataEntity(entity.getDescriptor(), query, entity.getSkyViewPosition(), sourceId, authorId, levelDescriptor);
    }

    private static native void datalabsContentHandler(String content) /*-{
        var uri = 'data:application/x-ipynb+json;charset=utf-8,' + content;

        var downloadLink = document.createElement("a");
        downloadLink.href = uri;
        var file = new Blob([content], { type: 'appliction/xml;charset=utf-8' })
        downloadLink.href = URL.createObjectURL(file);
        var date = new Date().toLocaleDateString();
        var time = new Date().toLocaleTimeString();
        downloadLink.download = "ESASky export " + date + " " + time + ".ipynb";

        document.body.appendChild(downloadLink);
        downloadLink.click();
        document.body.removeChild(downloadLink);

        URL.revokeObjectURL(downloadLink.href);
    }-*/;


    private static void writeData(DatalabsData data, final Consumer<String> contentHandler) {
        String json = MAPPER.write(data);
        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST, DATALABS_EXPORT_URL);

        try {
            requestBuilder.sendRequest(json, new RequestCallback() {

                @Override
                public void onError(final Request request, final Throwable exception) {
                    Log.error("Failed exporting data to datalabs, exception: ", exception);
                }

                @Override
                public void onResponseReceived(final Request request, final Response response) {
                    if (response.getStatusCode() == 200) {
                        contentHandler.accept(response.getText());
                    } else {
                        Log.error("Failed exporting data to datalabs, error code: " + response.getStatusCode());
                    }
                }
            });
        } catch (RequestException e) {
            Log.error("Failed exporting data to datalabs, exception: ", e);
        }
    }

    private static boolean shouldExport(GeneralEntityInterface entity){
        return !(isSpecialEntity(entity) || isMoc(entity) || entity.getTablePanel() == null);
    }

    private static boolean isMoc(GeneralEntityInterface entity) {
        return (entity instanceof EsaSkyEntity && ((EsaSkyEntity) entity).getMocEntity() != null
                && ((EsaSkyEntity) entity).getMocEntity().isShouldBeShown());
    }

    private static boolean isSpecialEntity(GeneralEntityInterface ent) {
        return Objects.equals(ent.getDescriptor().getSchemaName(), "alerts")
                || Objects.equals(ent.getDescriptor().getSchemaName(), "public")
                || ent instanceof ImageListEntity;
    }

}
