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
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyButton;
import esac.archive.esasky.cl.web.client.view.common.icons.Icons;
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

    public interface TapDescriptorListMapper extends ObjectMapper<TapDescriptorList> {
    }

    private FlowPanel mainContainer;
    private PopupHeader<GlobalTapPanel> header;
    private final GlobalTapPanel.Resources resources;
    private CssResource style;
    private TabulatorWrapper tapServicesWrapper;
    private TabulatorWrapper tapTablesWrapper;
    private FlowPanel tapServicesContainer;
    private FlowPanel tapTablesContainer;
    private TextBox searchBox;
    private EsaSkyButton backButton;
    private QueryPopupPanel queryPopupPanel;
    private LoadingSpinner loadingSpinner;

    private TabulatorCallback tabulatorCallback;
    private boolean fovLimiterEnabled;
    private boolean dataLoadedOnce = false;

    private static final String TABLE_NAME_COL = "table_name";
    private static final String ACCESS_URL_COL = "access_url";
    private static final String SHORT_NAME_COL = "short_name";
    private static final String DISPLAY_NONE = "displayNone";

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
        initTapServicesWrapper();
        initTapServicesTableWrapper();
        this.addSingleElementAbleToInitiateMoveOperation(header.getElement());
    }

    private void initView() {
        mainContainer = new FlowPanel();
        tapServicesContainer = new FlowPanel();
        tapServicesContainer.getElement().setId("browseTap__tabulatorServicesContainer");

        tapTablesContainer = new FlowPanel();
        tapTablesContainer.getElement().setId("browseTap__tabulatorTablesContainer");

        this.addStyleName("globalTapPanel__container");

        header = new PopupHeader<>(this, "External Tap Registry", "help text", "help title");

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

        FlowPanel searchContainer = new FlowPanel();
        searchContainer.addStyleName("globalTapPanel__searchContainer");

        searchBox = new TextBox();
        searchBox.getElement().setPropertyString("placeholder", "Filter tap services...");
        searchBox.setStyleName("globalTapPanel__searchBox");
        Timer searchDelayTimer = new Timer() {
            @Override
            public void run() {
                tapServicesWrapper.columnIncludesFilter(searchBox.getText(), "res_title", "short_name", "res_subjects", "publisher");
                tapTablesWrapper.columnIncludesFilter(searchBox.getText(), "schema_name", TABLE_NAME_COL, "description");
            }
        };

        searchBox.addKeyUpHandler(event -> {
            searchDelayTimer.cancel();
            searchDelayTimer.schedule(500);
        });

        searchContainer.add(searchBox);

        backButton = new  EsaSkyButton(Icons.getBackArrowIcon());
        backButton.addClickHandler(event -> {
            header.removePrefixWidget(backButton);
            showTable(tapServicesWrapper);
        });

        tabulatorCallback = new TabulatorCallback();

        loadingSpinner = new LoadingSpinner(true);
        loadingSpinner.setStyleName("globalTapPanel__loadingSpinner");
        loadingSpinner.setVisible(false);

        mainContainer.add(header);
        mainContainer.add(searchContainer);
        mainContainer.add(tapServicesContainer);
        mainContainer.add(tapTablesContainer);
        mainContainer.add(loadingSpinner);
        mainContainer.getElement().setId("globalTapPanelContainer");

        this.add(mainContainer);

        setDefaultSize();
    }


    private void initTapServicesWrapper() {
        TabulatorSettings settings = new TabulatorSettings();
        settings.setAddAdqlColumn(false);
        settings.setAddMetadataColumn(false);
        settings.setIsDownloadable(false);
        settings.setSelectable(1);
        settings.setTableLayout("fitColumns");
        tapServicesWrapper = new TabulatorWrapper("browseTap__tabulatorServicesContainer", tabulatorCallback, settings);
    }

    private void initTapServicesTableWrapper() {
        TabulatorSettings settings = new TabulatorSettings();
        settings.setAddAdqlColumn(true);
        settings.setAddMetadataColumn(true);
        settings.setIsDownloadable(false);
        settings.setSelectable(1);
        settings.setTableLayout("fitColumns");
        settings.setAddObscoreTableColumn(true);
        tapTablesWrapper = new TabulatorWrapper("browseTap__tabulatorTablesContainer", tabulatorCallback, settings);
        tapTablesWrapper.groupByColumns("schema_name");
    }

    public void onDataLoaded(String jsonString, TabulatorWrapper wrapper) {
        GeneralJavaScriptObject obj = GeneralJavaScriptObject.createJsonObject(jsonString);

        GeneralJavaScriptObject data = obj.getProperty("data");
        GeneralJavaScriptObject metadata = obj.hasProperty("columns")
                ? obj.getProperty("columns")
                : obj.getProperty("metadata");

        wrapper.clearFilters(true);
        showTable(wrapper);
        wrapper.insertExternalTapData(data, metadata);
        wrapper.restoreRedraw();
        wrapper.redrawAndReinitializeHozVDom();
        setTabulatorHeight();
    }

    private void loadData() {
        setIsLoading(true);
        JSONUtils.getJSONFromUrl(EsaSkyWebConstants.TAPREGISTRY_URL, new JSONUtils.IJSONRequestCallback() {
            @Override
            public void onSuccess(String responseText) {
                onDataLoaded(responseText, tapServicesWrapper);
                setIsLoading(false);
                dataLoadedOnce = true;
            }

            @Override
            public void onError(String errorCause) {
                setIsLoading(false);
            }
        });
    }


    private void showTable(TabulatorWrapper wrapper) {
        searchBox.setText(wrapper.getFilterQuery());

        if(wrapper.equals(tapServicesWrapper)) {
            tapTablesContainer.addStyleName(DISPLAY_NONE);
            tapServicesContainer.removeStyleName(DISPLAY_NONE);
        } else {
            tapServicesContainer.addStyleName(DISPLAY_NONE);
            tapTablesContainer.removeStyleName(DISPLAY_NONE);
        }

        setTabulatorHeight();
    }


    private class TabulatorCallback extends DefaultTabulatorCallback {
        private String storedAccessUrl;
        private String storedName;

        @Override
        public void onRowSelection(GeneralJavaScriptObject row) {
            GeneralJavaScriptObject rowData = row.invokeFunction("getData");
            String accessUrl = rowData.hasProperty(ACCESS_URL_COL)
                    ? rowData.getStringProperty(ACCESS_URL_COL)
                    : storedAccessUrl;

            String name = rowData.hasProperty(SHORT_NAME_COL)
                    ? rowData.getStringProperty(SHORT_NAME_COL)
                    : storedName;

            storedAccessUrl = accessUrl;
            storedName = name;

            if (!rowData.hasProperty(TABLE_NAME_COL)) {
                // Query for all tables in tap_schema.tables
                queryExternalTapService(accessUrl);
            } else  {
                // Query specific table
                String tableName = rowData.getStringProperty(TABLE_NAME_COL);
                String query = "SELECT * FROM " + tableName;
                queryExternalTapTable(accessUrl, tableName, query, true);
            }
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
            queryPopupPanel.setTapServiceUrl(storedAccessUrl);
            queryPopupPanel.setTapTable(rowData.getStringProperty(TABLE_NAME_COL));
            showQueryPanel();
        }

        @Override
        public void onAddObscoreTableClicked(GeneralJavaScriptObject rowData) {
            String tableName = rowData.getStringProperty(TABLE_NAME_COL);
            String schemaQuery = "SELECT * FROM tap_schema.columns WHERE table_name='" + tableName + "'";
            queryExternalTapTable(storedAccessUrl, tableName, schemaQuery, new JSONUtils.IJSONRequestCallback() {
                @Override
                public void onSuccess(String responseText) {
                    setIsLoading(false);

                    TapDescriptorListMapper mapper = GWT.create(TapDescriptorListMapper.class);
                    TapDescriptorList descriptorList = mapper.read(responseText);

                    if (descriptorList != null) {
                        List<TapMetadataDescriptor> metadataDescriptorList = descriptorList.getDescriptors().stream()
                                .map(TapMetadataDescriptor::fromTapDescriptor).collect(Collectors.toList());
                        CommonTapDescriptor commonTapDescriptor = DescriptorRepository.getInstance().createExternalDescriptor(metadataDescriptorList, storedAccessUrl, tableName, storedName, "", true);
                        DescriptorRepository.getInstance().addExternalDataCenterDescriptor(commonTapDescriptor);
                    }
                }

                @Override
                public void onError(String errorCause) {
                    setIsLoading(false);
                }
            });

        }

        @Override
        public void onMetadataButtonPressed(GeneralJavaScriptObject rowData) {
            String tableName = rowData.getStringProperty(TABLE_NAME_COL);
            String query = "SELECT * FROM tap_schema.columns where table_name='" + tableName + "'";

            queryExternalTapTable(storedAccessUrl, "tap_schema.columns", query, false);
        }

        private void queryExternalTapService(String tapUrl) {
            setIsLoading(true);

            String query = "SELECT schema_name, table_name, description FROM tap_schema.tables";
            String url = EsaSkyWebConstants.EXT_TAP_URL + "?"
                    + EsaSkyConstants.EXT_TAP_ACTION_FLAG + "=" + EsaSkyConstants.EXT_TAP_ACTION_REQUEST + "&"
                    + EsaSkyConstants.EXT_TAP_ADQL_FLAG + "=" + query + "&"
                    + EsaSkyConstants.EXT_TAP_URL_FLAG + "=" + tapUrl;

            JSONUtils.getJSONFromUrl(url, new JSONUtils.IJSONRequestCallback() {
                @Override
                public void onSuccess(String responseText) {
                    setIsLoading(false);
                    header.addPrefixWidget(backButton);
                    GlobalTapPanel.this.onDataLoaded(responseText, tapTablesWrapper);

                }

                @Override
                public void onError(String errorCause) {
                    setIsLoading(false);
                }
            });
        }

        private void queryExternalTapTable(String tapUrl, String tableName, String query, boolean fovLimit) {
            queryExternalTapTable(tapUrl, tableName, query, new JSONUtils.IJSONRequestCallback() {
                @Override
                public void onSuccess(String responseText) {
                    setIsLoading(false);

                    TapDescriptorListMapper mapper = GWT.create(TapDescriptorListMapper.class);
                    TapDescriptorList descriptorList = mapper.read(responseText);

                    if (descriptorList != null) {
                        List<TapMetadataDescriptor> metadataDescriptorList = descriptorList.getDescriptors().stream()
                                .map(TapMetadataDescriptor::fromTapDescriptor).collect(Collectors.toList());
                        CommonTapDescriptor commonTapDescriptor = DescriptorRepository.getInstance().createExternalDescriptor(metadataDescriptorList, tapUrl, tableName, storedName, query, fovLimit && fovLimiterEnabled);
                        CommonEventBus.getEventBus().fireEvent(new TapRegistrySelectEvent(commonTapDescriptor));
                    }
                }

                @Override
                public void onError(String errorCause) {
                    setIsLoading(false);
                }
            });
        }

        private void queryExternalTapTable(String tapUrl, String tableName, String query, JSONUtils.IJSONRequestCallback callback) {
            setIsLoading(true);
            StringBuilder schemaQuery = new StringBuilder("SELECT * FROM tap_schema.columns WHERE table_name='" + tableName + "'");

            // Find additional joined tables
            if (query.toUpperCase().contains("JOIN")) {
                String[] split = query.split("\\s");
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

            JSONUtils.getJSONFromUrl(url, callback);
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
        Style elementStyle = mainContainer.getElement().getStyle();
        int maxWidth = MainLayoutPanel.getMainAreaWidth() + MainLayoutPanel.getMainAreaAbsoluteLeft() - getAbsoluteLeft() - 15;
        elementStyle.setPropertyPx("maxWidth", maxWidth);
        elementStyle.setPropertyPx("maxHeight", MainLayoutPanel.getMainAreaHeight() + MainLayoutPanel.getMainAreaAbsoluteTop() - getAbsoluteTop() - 15);
        setTabulatorHeight();
    }


    private void setTabulatorHeight(){
        setTabulatorHeight(tapServicesContainer);
        setTabulatorHeight(tapTablesContainer);
    }

    private void setTabulatorHeight(FlowPanel tableContainer) {
        int occupiedHeight = tableContainer.getAbsoluteTop() - this.getAbsoluteTop();
        int height = mainContainer.getOffsetHeight() - occupiedHeight;

        if (height > MainLayoutPanel.getMainAreaHeight()) {
            height = MainLayoutPanel.getMainAreaHeight() - occupiedHeight;
        }

        tableContainer.getElement().getStyle().setPropertyPx("height", height);
    }


    private void setDefaultSize() {
        int width = (int) (MainLayoutPanel.getMainAreaWidth() * 0.6);
        int height = (int) (MainLayoutPanel.getMainAreaHeight() * 0.6);
        mainContainer.setWidth(width + "px");
        mainContainer.setHeight(height + "px");

        Style containerStyle = mainContainer.getElement().getStyle();
        containerStyle.setPropertyPx("minWidth", 350);
        containerStyle.setPropertyPx("minHeight", 300);
        setTabulatorHeight();
    }

    @Override
    protected Element getResizeElement() {
        return mainContainer.getElement();
    }

    @Override
    public void show() {
        super.show();
        if (!dataLoadedOnce) {
            loadData();
        }
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


