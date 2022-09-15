package esac.archive.esasky.cl.web.client.view.ctrltoolbar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.registry.TapRegistrySelectEvent;
import esac.archive.esasky.cl.web.client.repository.DescriptorRepository;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.JSONUtils;
import esac.archive.esasky.cl.web.client.view.common.LoadingSpinner;
import esac.archive.esasky.cl.web.client.view.common.MovableResizablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.DefaultTabulatorCallback;
import esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorSettings;
import esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.descriptor.ExtTapDescriptor;
import esac.archive.esasky.ifcs.model.shared.ESASkyColors;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;

public class GlobalTapPanel extends MovableResizablePanel<GlobalTapPanel> {

    private final FlowPanel container;
    private final PopupHeader<GlobalTapPanel> header;
    private final LoadingSpinner loadingSpinner;

    private final GlobalTapPanel.Resources resources;
    private CssResource style;
    TabulatorWrapper tabulatorTable;
    private final FlowPanel tabulatorContainer;


    public interface Resources extends ClientBundle {
        @Source("globalTapPanel.css")
        @CssResource.NotStrict
        CssResource style();
    }

    public GlobalTapPanel(String googleEventCategory, boolean isSuggestedPositionCenter) {
        super(googleEventCategory, isSuggestedPositionCenter);
        this.resources = GWT.create(GlobalTapPanel.Resources.class);
        this.style = this.resources.style();
        this.style.ensureInjected();

        container = new FlowPanel();
        tabulatorContainer = new FlowPanel();
        tabulatorContainer.getElement().setId("browseTap__tabulatorContainer");
        this.addStyleName("globalTapPanel__container");

        header = new PopupHeader<>(this, "header",
                "helptext",
                "help title",
                event -> {}, "Close panel");

        loadingSpinner = new LoadingSpinner(true);
        loadingSpinner.addStyleName("globalTapPanel__loadingSpinner");


        container.add(header);
        container.add(tabulatorContainer);
        this.add(container);
//        loadData();
    }

    public void onJsonLoaded(String jsonString) {
        this.show();
//        JSONObject jsonObj = new JSONObject(JsonUtils.safeEval(jsonString));
        GeneralJavaScriptObject obj =  GeneralJavaScriptObject.createJsonObject(jsonString);

        TabulatorSettings settings = new TabulatorSettings();
        tabulatorTable = new TabulatorWrapper("browseTap__tabulatorContainer", new TabulatorCallback(), settings);
//        tabulatorTable.setAddHipsColumn(true);
        tabulatorTable.insertObscoreData(obj.getProperty("data"), obj.getProperty("columns"));

//        tabulatorTable.restoreRedraw();
//        tabulatorTable.redrawAndReinitializeHozVDom();
    }

    private void loadData() {
//        MainLayoutPanel.addElementToMainArea(loadingSpinner);

        JSONUtils.getJSONFromUrl(EsaSkyWebConstants.TAPREGISTRY_URL, new JSONUtils.IJSONRequestCallback() {

            @Override
            public void onSuccess(String responseText) {
                onJsonLoaded(responseText);
            }

            @Override
            public void onError(String errorCause) {
                int a = 1+2;
            }
        });
    }

    private class TabulatorCallback extends DefaultTabulatorCallback {

        @Override
        public void onRowSelection(GeneralJavaScriptObject row) {
            GeneralJavaScriptObject rowData =  row.invokeFunction("getData");
            String tableName = rowData.getStringProperty("table_name");
            String accessUrl = rowData.getStringProperty("axxess_url");
            String url = EsaSkyWebConstants.EXT_TAP_URL  + "?"
                    + EsaSkyConstants.EXT_TAP_ACTION_FLAG + "=" + EsaSkyConstants.EXT_TAP_ACTION_REQUEST + "&"
                    + EsaSkyConstants.EXT_TAP_ADQL_FLAG + "=" + "SELECT top 0 * from " + tableName + "&"
                    + EsaSkyConstants.EXT_TAP_URL + "=" + accessUrl;
            JSONUtils.getJSONFromUrl(url, new JSONUtils.IJSONRequestCallback() {

                @Override
                public void onSuccess(String responseText) {
                    GeneralJavaScriptObject obj =  GeneralJavaScriptObject.createJsonObject(responseText);
                    GeneralJavaScriptObject meta = obj.getProperty("metadata");
                    ExtTapDescriptor extd = DescriptorRepository.getInstance().getExtTapDescriptors().getDescriptors().get(0);
                    extd.setTapUrl(accessUrl + "/sync");
                    extd.setTapTable(tableName);
                    extd.setTapTableMetadata(meta);
                    extd.setSelectADQL("SELECT *");
                    extd.setPrimaryColor(ESASkyColors.getNext());
                    extd.setFovLimit(360.0);
                    extd.setShapeLimit(10);
                    extd.setInBackend(false);
                    extd.setUseUcd(true);
                    extd.setDateADQL(null);
                    extd.setSearchFunction("cointainsPoint");
                    CommonEventBus.getEventBus().fireEvent(new TapRegistrySelectEvent(extd));
                }

                @Override
                public void onError(String errorCause) {
                    // TODO: Handle
                }
            });
        }
    }


    @Override
    protected Element getMovableElement() {
        return header.getElement();
    }

    @Override
    protected void onResize() {

    }

    @Override
    protected Element getResizeElement() {
        return container.getElement();
    }

    @Override
    public void show() {
       if (!isShowing())
            loadData();
       super.show();
    }
}


