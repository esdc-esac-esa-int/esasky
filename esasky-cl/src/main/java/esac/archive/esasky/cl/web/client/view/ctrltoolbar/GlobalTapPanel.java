package esac.archive.esasky.cl.web.client.view.ctrltoolbar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.exttap.TapRegistrySelectEvent;
import esac.archive.esasky.cl.web.client.model.Size;
import esac.archive.esasky.cl.web.client.repository.DescriptorRepository;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.ExtTapUtils;
import esac.archive.esasky.cl.web.client.utility.JSONUtils;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.LoadingSpinner;
import esac.archive.esasky.cl.web.client.view.common.MovableResizablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.DefaultTabulatorCallback;
import esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorSettings;
import esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.descriptor.ExtTapDescriptor;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;

public class GlobalTapPanel extends MovableResizablePanel<GlobalTapPanel> {

    private FlowPanel container;
    private PopupHeader<GlobalTapPanel> header;
    private final GlobalTapPanel.Resources resources;
    private CssResource style;
    TabulatorWrapper tabulatorTable;
    private FlowPanel tabulatorContainer;
    private QueryPopupPanel queryPopupPanel;
    private LoadingSpinner loadingSpinner;

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

        initView();
        setMaxSize();
        MainLayoutPanel.addMainAreaResizeHandler(event -> setDefaultSize());
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        this.addSingleElementAbleToInitiateMoveOperation(header.getElement());
    }

    private void initView() {
        container = new FlowPanel();
        tabulatorContainer = new FlowPanel();
        tabulatorContainer.getElement().setId("browseTap__tabulatorContainer");
        this.addStyleName("globalTapPanel__container");

        header = new PopupHeader<>(this, "header",
                "helptext",
                "help title",
                event -> hide(), "Close panel");


        loadingSpinner = new LoadingSpinner(true);
        loadingSpinner.setStyleName("globalTapPanel__loadingSpinner");
        loadingSpinner.setVisible(false);

        container.add(header);
        container.add(tabulatorContainer);
        container.add(loadingSpinner);
        container.getElement().setId("globalTapPanelContainer");

        queryPopupPanel = new QueryPopupPanel("test", true);
        queryPopupPanel.addCloseHandler(event -> setGlassEnabled(false));
        queryPopupPanel.addOpenHandler(event -> setGlassEnabled(true));
        queryPopupPanel.addQueryHandler(event -> queryExternalTapTable(event.getTapUrl(), event.getTableName(), event.getQuery()));
        queryPopupPanel.setSuggestedPositionCenter();
        queryPopupPanel.hide();
        MainLayoutPanel.addElementToMainArea(queryPopupPanel);

        this.add(container);
    }

    public void onJsonLoaded(String jsonString) {
        GeneralJavaScriptObject obj = GeneralJavaScriptObject.createJsonObject(jsonString);
        TabulatorSettings settings = new TabulatorSettings();
        settings.setAddAdqlColumn(true);
        settings.setAddMetadataColumn(true);
        settings.setIsDownloadable(false);
        tabulatorTable = new TabulatorWrapper("browseTap__tabulatorContainer", new TabulatorCallback(), settings);
        tabulatorTable.groupByColumn("res_title");
        tabulatorTable.insertExternalTapData(obj.getProperty("data"), obj.getProperty("columns"));
        tabulatorTable.restoreRedraw();
        tabulatorTable.redrawAndReinitializeHozVDom();
    }

    private void loadData() {
        setDefaultSize();
        setIsLoading(true);
        JSONUtils.getJSONFromUrl(EsaSkyWebConstants.TAPREGISTRY_URL, new JSONUtils.IJSONRequestCallback() {
            @Override
            public void onSuccess(String responseText) {
                onJsonLoaded(responseText);
                setIsLoading(false);
            }

            @Override
            public void onError(String errorCause) {
                setIsLoading(false);
            }
        });
    }

    private void queryExternalTapTable(String tapUrl, String tableName, String query) {
        setIsLoading(true);

        StringBuilder schemaQuery = new StringBuilder("SELECT * FROM tap_schema.columns WHERE table_name='" + tableName + "'");

        // Find additional joined tables
        if (query.toUpperCase().contains("JOIN")) {
            String[] split = query.split("[\\n\\s]");
            for (int i = 0; i < split.length; i++) {
                int tableIndex = i + 1;
                if (split[i].equalsIgnoreCase("JOIN") && tableIndex < split.length) {
                    schemaQuery.append(" OR table_name='").append(split[tableIndex]).append("'");
                }
            }
        }

        String url = EsaSkyWebConstants.EXT_TAP_URL + "?"
                + EsaSkyConstants.EXT_TAP_ACTION_FLAG + "=" + EsaSkyConstants.EXT_TAP_ACTION_REQUEST + "&"
                + EsaSkyConstants.EXT_TAP_ADQL_FLAG + "=" + schemaQuery + "&"
                + EsaSkyConstants.EXT_TAP_URL + "=" + tapUrl;

        JSONUtils.getJSONFromUrl(url, new JSONUtils.IJSONRequestCallback() {
            @Override
            public void onSuccess(String responseText) {
                setIsLoading(false);
                GeneralJavaScriptObject responseObject = GeneralJavaScriptObject.createJsonObject(responseText);
                GeneralJavaScriptObject meta = responseObject.hasProperty("metadata")
                        ? responseObject.getProperty("metadata")
                        : responseObject.getProperty("columns");

                GeneralJavaScriptObject data = ExtTapUtils.formatExternalTapData(responseObject.getProperty("data"), meta);

                ExtTapDescriptor descriptor = DescriptorRepository.getInstance().addExtTapDescriptor(tapUrl, tableName, query, data);
                CommonEventBus.getEventBus().fireEvent(new TapRegistrySelectEvent(descriptor));
            }

            @Override
            public void onError(String errorCause) {
                setIsLoading(false);
            }
        });
    }

    private class TabulatorCallback extends DefaultTabulatorCallback {

        @Override
        public void onRowSelection(GeneralJavaScriptObject row) {
            GeneralJavaScriptObject rowData = row.invokeFunction("getData");
            String tableName = rowData.getStringProperty("table_name");
            String accessUrl = rowData.getStringProperty("access_url");
            String query = "SELECT * FROM " + tableName;

            GlobalTapPanel.this.queryExternalTapTable(accessUrl, tableName, query);
        }

        @Override
        public void onAdqlButtonPressed(GeneralJavaScriptObject rowData) {
            queryPopupPanel.setTapServiceUrl(rowData.getStringProperty("access_url"));
            queryPopupPanel.setTapTable(rowData.getStringProperty("table_name"));
            showQueryPanel();
        }

        @Override
        public void onMetadataButtonPressed(GeneralJavaScriptObject rowData) {
            String tableName = rowData.getStringProperty("table_name");
            String accessUrl = rowData.getStringProperty("access_url");
            String query = "SELECT * FROM tap_schema.columns where table_name='" + tableName + "'";

            GlobalTapPanel.this.queryExternalTapTable(accessUrl, tableName, query);
        }
    }

    @Override
    protected Element getMovableElement() {
        return header.getElement();
    }

    @Override
    protected void onResize() {
        setMaxSize();
    }

    @Override
    public void setMaxSize() {
        Style elementStyle = container.getElement().getStyle();
        int maxWidth = MainLayoutPanel.getMainAreaWidth() + MainLayoutPanel.getMainAreaAbsoluteLeft() - getAbsoluteLeft() - 15;
        elementStyle.setPropertyPx("maxWidth", maxWidth);
        elementStyle.setPropertyPx("maxHeight", MainLayoutPanel.getMainAreaHeight() + MainLayoutPanel.getMainAreaAbsoluteTop() - getAbsoluteTop() - 15);
        setMaxHeight();
    }

    private void setMaxHeight() {
        int headerSize = header.getOffsetHeight();
        int height = container.getOffsetHeight() - headerSize - 5;

        if (height > MainLayoutPanel.getMainAreaHeight()) {
            height = MainLayoutPanel.getMainAreaHeight() - headerSize - 5;
        }

        tabulatorContainer.getElement().getStyle().setPropertyPx("height", height);
    }


    private void setDefaultSize() {
        Size size = getDefaultSize();
        container.setWidth(size.width + "px");
        container.setHeight(size.height + "px");

        Style containerStyle = container.getElement().getStyle();
        containerStyle.setPropertyPx("minWidth", 350);
        containerStyle.setPropertyPx("minHeight", 300);
    }

    @Override
    protected Element getResizeElement() {
        return container.getElement();
    }

    @Override
    public void show() {
        super.show();
        loadData();
    }

    public void showQueryPanel() {
        queryPopupPanel.setSuggestedPositionCenter();
        queryPopupPanel.show();
        queryPopupPanel.setFocus(true);
        queryPopupPanel.updateZIndex();
    }

    public void hideQueryPanel() {
        queryPopupPanel.hide();
    }

    private void setIsLoading(boolean isLoading) {
        loadingSpinner.setVisible(isLoading);
        setGlassEnabled(isLoading);
    }

}


