package esac.archive.esasky.cl.web.client.view.ctrltoolbar;

import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.http.client.URL;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextBox;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.exttap.TapRegistrySelectEvent;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.repository.DescriptorRepository;
import esac.archive.esasky.cl.web.client.utility.*;
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
import java.util.UUID;

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
    private static final String DESCRIPTION_COL = "description";
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
                TabulatorWrapper table = getCurrentTable();
                String[] columns = getTableFilterColumns(table);
                table.columnIncludesFilter(searchBox.getText(), columns);
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

    private TabulatorWrapper getCurrentTable() {
        return tapTablesContainer.getStyleName().contains(DISPLAY_NONE) ? tapServicesWrapper : tapTablesWrapper;
    }

    private String[] getTableFilterColumns(TabulatorWrapper wrapper) {
        return wrapper.equals(tapServicesWrapper)
                ? new String[] {"res_title", "short_name", "res_subjects", "publisher"}
                : new String[] {"schema_name", TABLE_NAME_COL, "description"};
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
                exploreTapServiceTables(accessUrl);
            } else  {
                // Query specific table
                String tableName = ExtTapUtils.encapsulateTableName(rowData.getStringProperty(TABLE_NAME_COL));
                String description = rowData.getStringProperty(DESCRIPTION_COL);
                String query = "SELECT * FROM " + tableName;
                queryExternalTapServiceData(accessUrl, tableName, description, query, true, false);
            }
        }

        @Override
        public void onAdqlButtonPressed(GeneralJavaScriptObject rowData) {
            if (queryPopupPanel == null) {
                queryPopupPanel = new QueryPopupPanel(GoogleAnalytics.CAT_GLOBALTAP_ADQLPANEL, true);
                queryPopupPanel.addCloseHandler(event -> setGlassEnabled(false));
                queryPopupPanel.addOpenHandler(event -> setGlassEnabled(true));
                String description = TextMgr.getInstance().getText("global_tap_panel_custom_query_description") + " ";
                queryPopupPanel.addQueryHandler(event -> queryExternalTapServiceData(event.getTapUrl(), event.getTableName(),
                        description + event.getQuery(), event.getQuery(), false, true));
                queryPopupPanel.setSuggestedPositionCenter();
            }
            queryPopupPanel.setTapServiceUrl(storedAccessUrl);
            queryPopupPanel.setTapTable(rowData.getStringProperty(TABLE_NAME_COL));
            queryPopupPanel.setTapDescription(rowData.getStringProperty(DESCRIPTION_COL));
            showQueryPanel();
        }

        @Override
        public void onAddObscoreTableClicked(GeneralJavaScriptObject rowData) {
            String tableName = ExtTapUtils.encapsulateTableName(rowData.getStringProperty(TABLE_NAME_COL));
            String schemaQuery = "SELECT * FROM tap_schema.columns WHERE table_name='" + tableName + "'";
            queryExternalTapServiceMetadata(storedAccessUrl, schemaQuery, new JSONUtils.IJSONRequestCallback() {
                @Override
                public void onSuccess(String responseText) {
                    setIsLoading(false);

                    TapDescriptorListMapper mapper = GWT.create(TapDescriptorListMapper.class);
                    TapDescriptorList descriptorList = mapper.read(responseText);

                    if (descriptorList != null) { 
                        List<TapMetadataDescriptor> metadataDescriptorList = ExtTapUtils.getMetadataFromTapDescriptorList(descriptorList, true);
                        CommonTapDescriptor commonTapDescriptor = DescriptorRepository.getInstance().createExternalDescriptor(metadataDescriptorList,
                                storedAccessUrl, tableName, storedName, null, "", true, false);
                        DescriptorRepository.getInstance().addExternalDataCenterDescriptor(commonTapDescriptor);
                    }
                }

                @Override
                public void onError(String errorCause) {
                    showErrorMessage(storedName);
                    setIsLoading(false);
                }
            });

        }

        @Override
        public void onMetadataButtonPressed(GeneralJavaScriptObject rowData) {
            String tableName = rowData.getStringProperty(TABLE_NAME_COL);
            String query = "SELECT * FROM tap_schema.columns where table_name='" + tableName + "'";

            queryExternalTapServiceData(storedAccessUrl, "tap_schema.columns", null, query,false, false);
        }

        private void exploreTapServiceTables(String tapUrl) {
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
                    showErrorMessage(storedName);
                    setIsLoading(false);
                }
            });
        }

        private void queryExternalTapServiceData(String tapUrl, String tableName, String description, String query, boolean fovLimit, boolean useUnprocessedQuery) {
            queryExternalTapServiceMetadata(tapUrl, query, new JSONUtils.IJSONRequestCallback() {
                @Override
                public void onSuccess(String responseText) {
                    setIsLoading(false);

                    TapDescriptorListMapper mapper = GWT.create(TapDescriptorListMapper.class);
                    TapDescriptorList descriptorList = mapper.read(responseText);

                    if (descriptorList != null) {
                        List<TapMetadataDescriptor> metadataDescriptorList = ExtTapUtils.getMetadataFromTapDescriptorList(descriptorList, false);
                        CommonTapDescriptor commonTapDescriptor = DescriptorRepository.getInstance().createExternalDescriptor(metadataDescriptorList, tapUrl,
                                tableName, storedName, description, query, fovLimit && fovLimiterEnabled, useUnprocessedQuery);
                        CommonEventBus.getEventBus().fireEvent(new TapRegistrySelectEvent(commonTapDescriptor));
                    }
                }

                @Override
                public void onError(String errorCause) {
                    showErrorMessage(storedName);
                    setIsLoading(false);
                }
            });
        }

        private void queryExternalTapServiceMetadata(String tapUrl, String query, JSONUtils.IJSONRequestCallback callback) {
            setIsLoading(true);

            String url = EsaSkyWebConstants.EXT_TAP_URL + "?"
                    + EsaSkyConstants.EXT_TAP_ACTION_FLAG + "=" + EsaSkyConstants.EXT_TAP_ACTION_REQUEST + "&"
                    + EsaSkyConstants.EXT_TAP_ADQL_FLAG + "=" + URL.encodeQueryString(query) + "&"
                    + EsaSkyConstants.EXT_TAP_URL_FLAG + "=" + tapUrl + "&"
                    + EsaSkyConstants.EXT_TAP_MAX_REC_FLAG + "=" + 1;

            JSONUtils.getJSONFromUrl(url, callback);
        }

        private void showErrorMessage(String name) {
            String title = TextMgr.getInstance().getText("global_tap_panel_query_failed_title");
            String body = TextMgr.getInstance().getText("global_tap_panel_query_failed_body").replace("$TAP_SERVICE$", name);
            DisplayUtils.showMessageDialogBox(body, title, UUID.randomUUID().toString(), GoogleAnalytics.CAT_GLOBALTAPPANEL_ERRORDIALOG);
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


