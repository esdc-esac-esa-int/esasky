package esac.archive.esasky.cl.web.client.view.ctrltoolbar;

import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextBox;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.exttap.TapRegistrySelectEvent;
import esac.archive.esasky.cl.web.client.repository.DescriptorRepository;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.JSONUtils;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.EsaSkySwitch;
import esac.archive.esasky.cl.web.client.view.common.LoadingSpinner;
import esac.archive.esasky.cl.web.client.view.common.MovableResizablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.DefaultTabulatorCallback;
import esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorSettings;
import esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.TapDescriptorList;
import esac.archive.esasky.ifcs.model.descriptor.TapMetadataDescriptor;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;

import java.util.List;
import java.util.stream.Collectors;

public class GlobalTapPanel extends MovableResizablePanel<GlobalTapPanel> {
    public interface TapDescriptorListMapper extends ObjectMapper<TapDescriptorList> { }
    private FlowPanel container;
    private PopupHeader<GlobalTapPanel> header;
    private final GlobalTapPanel.Resources resources;
    private CssResource style;
    private TabulatorWrapper tabulatorTable;
    private FlowPanel tabulatorContainer;
    private FlowPanel searchContainer;

    private QueryPopupPanel queryPopupPanel;
    private LoadingSpinner loadingSpinner;
    private boolean fovLimiterEnabled;

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

        header = new PopupHeader<>(this, "External Tap Registry", "help text","help title");

        fovLimiterEnabled = true;
        EsaSkySwitch switchBtn = new EsaSkySwitch("fovLimiterSwitch", fovLimiterEnabled,
                "Limit table data to FOV", "Limit query results to the FOV");

        switchBtn.addClickHandler(event -> {
            fovLimiterEnabled = !fovLimiterEnabled;
            switchBtn.setChecked(fovLimiterEnabled);
        });

        FlowPanel switchContainer = new FlowPanel();
        switchContainer.add(switchBtn);
        header.addActionWidget(switchContainer);

        searchContainer = new FlowPanel();
        searchContainer.addStyleName("globalTapPanel__searchContainer");

        TextBox searchBox = new TextBox();
        searchBox.getElement().setPropertyString("placeholder", "Filter tap services...");
        searchBox.setStyleName("globalTapPanel__searchBox");
        Timer searchDelayTimer = new Timer() {
            @Override
            public void run() {
                tabulatorTable.columnIncludesFilter(searchBox.getText(), "res_title", "table_description", "access_url", "table_name");
            }
        };

        searchBox.addKeyUpHandler(event -> {
            searchDelayTimer.cancel();
            searchDelayTimer.schedule(500);
        });

        searchContainer.add(searchBox);


        loadingSpinner = new LoadingSpinner(true);
        loadingSpinner.setStyleName("globalTapPanel__loadingSpinner");
        loadingSpinner.setVisible(false);

        container.add(header);
        container.add(searchContainer);
        container.add(tabulatorContainer);
        container.add(loadingSpinner);
        container.getElement().setId("globalTapPanelContainer");

        this.add(container);
        setDefaultSize();
    }

    public void onJsonLoaded(String jsonString) {
        GeneralJavaScriptObject obj = GeneralJavaScriptObject.createJsonObject(jsonString);
        TabulatorSettings settings = new TabulatorSettings();
        settings.setAddAdqlColumn(true);
        settings.setAddMetadataColumn(true);
        settings.setIsDownloadable(false);
        settings.setSelectable(1);
        tabulatorTable = new TabulatorWrapper("browseTap__tabulatorContainer", new TabulatorCallback(), settings);
        tabulatorTable.groupByColumns("harvested_from","res_title");
        tabulatorTable.insertExternalTapData(obj.getProperty("data"), obj.getProperty("columns"));
        tabulatorTable.restoreRedraw();
        tabulatorTable.redrawAndReinitializeHozVDom();
        setTabulatorHeight();
    }

    private void loadData() {
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

    private void queryExternalTapTable(String tapUrl, String tableName, String query, boolean fovLimit) {
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
                + EsaSkyConstants.EXT_TAP_URL_FLAG + "=" + tapUrl;

        JSONUtils.getJSONFromUrl(url, new JSONUtils.IJSONRequestCallback() {
            @Override
            public void onSuccess(String responseText) {
                setIsLoading(false);

                TapDescriptorListMapper mapper = GWT.create(TapDescriptorListMapper.class);
                TapDescriptorList descriptorList = mapper.read(responseText);

                if (descriptorList != null) {
                    List<TapMetadataDescriptor> metadataDescriptorList  = descriptorList.getDescriptors().stream()
                            .map(TapMetadataDescriptor::fromTapDescriptor).collect(Collectors.toList());
                    CommonTapDescriptor commonTapDescriptor = DescriptorRepository.getInstance().addExtTapDescriptor(metadataDescriptorList, tapUrl, tableName, query, fovLimit && fovLimiterEnabled);
                    CommonEventBus.getEventBus().fireEvent(new TapRegistrySelectEvent(commonTapDescriptor));
                }
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
            GlobalTapPanel.this.queryExternalTapTable(accessUrl, tableName, query, true);
        }

        @Override
        public void onAdqlButtonPressed(GeneralJavaScriptObject rowData) {
            if (queryPopupPanel == null) {
                queryPopupPanel = new QueryPopupPanel("test", true);
                queryPopupPanel.addCloseHandler(event -> setGlassEnabled(false));
                queryPopupPanel.addOpenHandler(event -> setGlassEnabled(true));
                queryPopupPanel.addQueryHandler(event -> queryExternalTapTable(event.getTapUrl(), event.getTableName(), event.getQuery(), true));
                queryPopupPanel.setSuggestedPositionCenter();
            }
            queryPopupPanel.setTapServiceUrl(rowData.getStringProperty("access_url"));
            queryPopupPanel.setTapTable(rowData.getStringProperty("table_name"));
            showQueryPanel();
        }

        @Override
        public void onMetadataButtonPressed(GeneralJavaScriptObject rowData) {
            String tableName = rowData.getStringProperty("table_name");
            String accessUrl = rowData.getStringProperty("access_url");
            String query = "SELECT * FROM tap_schema.columns where table_name='" + tableName + "'";

            GlobalTapPanel.this.queryExternalTapTable(accessUrl, "tap_schema.columns", query, false);
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
        setTabulatorHeight();
    }


    private void setTabulatorHeight() {
        int occupiedHeight = tabulatorContainer.getAbsoluteTop() - this.getAbsoluteTop();
        int height = container.getOffsetHeight() - occupiedHeight;

        if (height > MainLayoutPanel.getMainAreaHeight()) {
            height = MainLayoutPanel.getMainAreaHeight() - occupiedHeight;
        }

        tabulatorContainer.getElement().getStyle().setPropertyPx("height", height);
    }


    private void setDefaultSize() {
        int width = (int) (MainLayoutPanel.getMainAreaWidth() * 0.6);
        int height = (int) (MainLayoutPanel.getMainAreaHeight() * 0.6);
        container.setWidth(width + "px");
        container.setHeight(height + "px");

        Style containerStyle = container.getElement().getStyle();
        containerStyle.setPropertyPx("minWidth", 350);
        containerStyle.setPropertyPx("minHeight", 300);
        setTabulatorHeight();
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
        queryPopupPanel.show();
    }

    public void hideQueryPanel() {
        queryPopupPanel.hide();
    }

    private void setIsLoading(boolean isLoading) {
        loadingSpinner.setVisible(isLoading);
        setGlassEnabled(isLoading);
    }

}


