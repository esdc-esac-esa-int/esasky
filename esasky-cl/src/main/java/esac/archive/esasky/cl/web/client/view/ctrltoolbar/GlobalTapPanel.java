package esac.archive.esasky.cl.web.client.view.ctrltoolbar;

import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.URL;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.DialogActionEvent;
import esac.archive.esasky.cl.web.client.event.TreeMapNewDataEvent;
import esac.archive.esasky.cl.web.client.event.TreeMapNewDataEventHandler;
import esac.archive.esasky.cl.web.client.event.exttap.TapRegistrySelectEvent;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.repository.DescriptorRepository;
import esac.archive.esasky.cl.web.client.utility.*;
import esac.archive.esasky.cl.web.client.view.ColumnSelectorPopupPanel;
import esac.archive.esasky.cl.web.client.view.common.ConfirmationPopupPanel;
import esac.archive.esasky.cl.web.client.view.common.EsaSkySwitch;
import esac.archive.esasky.cl.web.client.view.common.GlassFlowPanel;
import esac.archive.esasky.cl.web.client.view.common.LoadingSpinner;
import esac.archive.esasky.cl.web.client.view.common.buttons.CloseButton;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyButton;
import esac.archive.esasky.cl.web.client.view.common.icons.Icons;
import esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.DefaultTabulatorCallback;
import esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorSettings;
import esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.TapDescriptorList;
import esac.archive.esasky.ifcs.model.descriptor.TapMetadataDescriptor;
import esac.archive.esasky.ifcs.model.shared.ESASkyColors;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;
import esac.archive.esasky.ifcs.model.shared.contentdescriptors.UCD;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class GlobalTapPanel extends FlowPanel {


    public interface TapDescriptorListMapper extends ObjectMapper<TapDescriptorList> {
    }

    private final Resources resources;
    private CssResource style;
    private TabulatorWrapper tapServicesWrapper;
    private TabulatorWrapper tapTablesWrapper;

    private GlassFlowPanel tapServicesGlass;
    private GlassFlowPanel tapTablesGlass;
    private GlassFlowPanel currentContainer;
    private TabulatorWrapper currentWrapper;
    private TextBox searchBox;
    private CloseButton searchClearBtn;
    private EsaSkyButton backButton;
    private LoadingSpinner loadingSpinner;
    private TabulatorCallback tabulatorCallback;
    private final PopupHeader<? extends Panel> parentHeader;
    private EsaSkySwitch switchBtn;

    private boolean fovLimiterEnabled;
    private static final String TABLE_NAME_COL = "table_name";
    private static final String DESCRIPTION_COL = "description";
    private static final String ACCESS_URL_COL = "access_url";
    private static final String SHORT_NAME_COL = "short_name";
    private static final String DISPLAY_NONE = "displayNone";
    private static final String PLACEHOLDER = "placeholder";

    public interface Resources extends ClientBundle {
        @Source("globalTapPanel.css")
        @CssResource.NotStrict
        CssResource style();
    }

    public GlobalTapPanel(PopupHeader<? extends Panel> parentHeader) {
        this.resources = GWT.create(GlobalTapPanel.Resources.class);
        this.style = this.resources.style();
        this.style.ensureInjected();
        this.parentHeader = parentHeader;
        initView();
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        initTapServicesWrapper();
        initTapServicesTableWrapper();
        loadData();

    }

    private void initView() {
        this.addStyleName("globalTapPanel");

        FlowPanel mainContainer = new FlowPanel();
        mainContainer.addStyleName("globalTapPanel__container");

        tapServicesGlass = new GlassFlowPanel();
        tapServicesGlass.addStyleName("globalTapPanel__tabulatorGlassContainer");
        FlowPanel tapServicesContainer = new FlowPanel();
        tapServicesContainer.getElement().setId("browseTap__tabulatorServicesContainer");
        tapServicesContainer.addStyleName("globalTapPanel__tabulatorContainer");
        tapServicesGlass.add(tapServicesContainer);

        tapTablesGlass = new GlassFlowPanel();
        tapTablesGlass.addStyleName("globalTapPanel__tabulatorGlassContainer");
        FlowPanel tapTablesContainer = new FlowPanel();
        tapTablesContainer.getElement().setId("browseTap__tabulatorTablesContainer");
        tapTablesContainer.addStyleName("globalTapPanel__tabulatorContainer");
        tapTablesGlass.add(tapTablesContainer);

        currentContainer = tapServicesGlass;
        currentWrapper = tapServicesWrapper;

        FlowPanel searchRowContainer = new FlowPanel();
        searchRowContainer.addStyleName("globalTapPanel__searchRowContainer");

        FlowPanel searchBoxContainer = new FlowPanel();
        searchBoxContainer.setStyleName("globalTapPanel__searchBoxContainer");
        searchBox = new TextBox();
        searchBox.setStyleName("globalTapPanel__searchBox");
        searchBox.getElement().setPropertyString(PLACEHOLDER, TextMgr.getInstance().getText("global_tap_panel_filter_tap_services"));
        Timer searchDelayTimer = new Timer() {
            @Override
            public void run() {
                TabulatorWrapper table = currentWrapper;
                String[] columns = getTableFilterColumns(table);
                table.columnIncludesFilter(searchBox.getText(), columns);
                searchClearBtn.setVisible(searchBox.getText().length() > 0);
            }
        };

        searchBox.addKeyUpHandler(event -> {
            searchDelayTimer.cancel();
            searchDelayTimer.schedule(500);
        });

        searchBox.addChangeHandler(event -> {
            searchDelayTimer.cancel();
            searchDelayTimer.schedule(500);
        });

        searchClearBtn = new CloseButton();
        searchClearBtn.addStyleName("globalTapPanel__searchClearButton");
        searchClearBtn.setDarkStyle();
        searchClearBtn.setSecondaryIcon();
        searchClearBtn.addClickHandler(event -> {
            searchBox.setText("");
            searchBox.setFocus(true);
            searchClearBtn.setVisible(false);
            searchDelayTimer.run();
        });
        searchClearBtn.setVisible(false);

        searchBoxContainer.add(searchBox);
        searchBoxContainer.add(searchClearBtn);

        fovLimiterEnabled = true;
        switchBtn = new EsaSkySwitch("fovLimiterSwitch", fovLimiterEnabled,
                TextMgr.getInstance().getText("global_tap_panel_toggle_fov_restricted"),
                TextMgr.getInstance().getText("global_tap_panel_toggle_fov_restricted_tooltip"));
        switchBtn.addStyleName("globalTapPanel__fovSwitch");

        switchBtn.addClickHandler(event -> {
            fovLimiterEnabled = !fovLimiterEnabled;
            switchBtn.setChecked(fovLimiterEnabled);
        });


        FlowPanel backButtonContainer = new FlowPanel();
        backButtonContainer.setWidth("10px");
        backButton = new EsaSkyButton(Icons.getBackArrowIcon());
        backButton.setVisible(false);
        backButton.addClickHandler(event -> {
            backButton.setVisible(false);
            showTable(tapServicesWrapper);
        });
        backButtonContainer.add(backButton);

        searchRowContainer.add(backButtonContainer);
        searchRowContainer.add(searchBoxContainer);

        tabulatorCallback = new TabulatorCallback();

        loadingSpinner = new LoadingSpinner(true);
        loadingSpinner.setStyleName("globalTapPanel__loadingSpinner");
        loadingSpinner.setVisible(false);

        mainContainer.add(searchRowContainer);
        mainContainer.add(tapServicesGlass);
        mainContainer.add(tapTablesGlass);
        mainContainer.add(loadingSpinner);

        this.add(mainContainer);
    }


    private void initTapServicesWrapper() {
        TabulatorSettings settings = new TabulatorSettings();
        settings.setAddAdqlColumn(false);
        settings.setAddMetadataColumn(false);
        settings.setIsDownloadable(false);
        settings.setSelectable(1);
        tapServicesWrapper = new TabulatorWrapper("browseTap__tabulatorServicesContainer", tabulatorCallback, settings);
        final Element rowCountFooter = Document.get().getElementById("browseTap__tabulatorServicesContainer_rowCount");
        rowCountFooter.getStyle().setMarginTop(-25, Style.Unit.PX);

    }

    private void initTapServicesTableWrapper() {
        TabulatorSettings settings = new TabulatorSettings();
        settings.setAddAdqlColumn(true);
        settings.setAddMetadataColumn(false);
        settings.setIsDownloadable(false);
        settings.setSelectable(1);
        settings.setAddObscoreTableColumn(true);
        settings.setAddOpenTableColumn(true);
        tapTablesWrapper = new TabulatorWrapper("browseTap__tabulatorTablesContainer", tabulatorCallback, settings);
        tapTablesWrapper.groupByColumns("schema_name");
        final Element rowCountFooter = Document.get().getElementById("browseTap__tabulatorTablesContainer_rowCount");
        rowCountFooter.getStyle().setMarginTop(-25, Style.Unit.PX);
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


//        if (openVizier) {
//            GeneralJavaScriptObject[] formatted = GeneralJavaScriptObject.convertToArray(ExtTapUtils.formatExternalTapData(data, metadata));
//            GeneralJavaScriptObject row = findVizier(formatted);
//            wrapper.onRowSelection(row);
//        }

    }

    private native GeneralJavaScriptObject findVizier(GeneralJavaScriptObject[] dataArr) /*-{
        for (var i = 0; i < dataArr.length; i++) {
            if (dataArr[i].ivoid === "ivo://cds.vizier/tap") {
                return dataArr[i];
            }
        }

        return {}
    }-*/;

    public void loadData() {
        setIsLoading(true);
        JSONUtils.getJSONFromUrl(EsaSkyWebConstants.TAPREGISTRY_URL, new JSONUtils.IJSONRequestCallback() {
            @Override
            public void onSuccess(String responseText) {
                setIsLoading(false);
                onDataLoaded(responseText, tapServicesWrapper);
            }

            @Override
            public void onError(String errorCause) {
                setIsLoading(false);
            }
        });
    }




    private void showTable(TabulatorWrapper wrapper) {
        searchBox.setText(wrapper.getFilterQuery());
        searchClearBtn.setVisible(searchBox.getText().length() > 0);

        if (wrapper.equals(tapServicesWrapper)) {
            tapTablesGlass.addStyleName(DISPLAY_NONE);
            tapServicesGlass.removeStyleName(DISPLAY_NONE);
            currentContainer = tapServicesGlass;
            searchBox.getElement().setPropertyString(PLACEHOLDER, TextMgr.getInstance().getText("global_tap_panel_filter_tap_services"));
        } else {
            tapServicesGlass.addStyleName(DISPLAY_NONE);
            tapTablesGlass.removeStyleName(DISPLAY_NONE);
            currentContainer = tapTablesGlass;
            searchBox.getElement().setPropertyString(PLACEHOLDER, TextMgr.getInstance().getText("global_tap_panel_filter_tap_tables"));
        }

        currentWrapper = wrapper;

    }

    private String[] getTableFilterColumns(TabulatorWrapper wrapper) {
        return wrapper.equals(tapServicesWrapper)
                ? new String[]{"res_title", SHORT_NAME_COL, "res_subjects", "publisher"}
                : new String[]{"schema_name", TABLE_NAME_COL, DESCRIPTION_COL};
    }

    private void setIsLoading(boolean isLoading) {
        if (isLoading){
            currentContainer.showGlass();
        } else {
            currentContainer.hideGlass();
        }

        loadingSpinner.setVisible(isLoading);
    }


    // Tabulator interaction
    private class TabulatorCallback extends DefaultTabulatorCallback {
        private String storedAccessUrl;
        private String storedName;
        private QueryPopupPanel queryPopupPanel;

        @Override
        public void onRowSelection(GeneralJavaScriptObject row) {
            GeneralJavaScriptObject rowData;
            if (row.hasProperty(ACCESS_URL_COL)){
                rowData = row;
            } else {
                rowData = row.invokeFunction("getData");
            }

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
            }
        }

        @Override
        public void onOpenTableClicked(GeneralJavaScriptObject rowData) {
            String encapsulateTableName = ExtTapUtils.encapsulateTableName(rowData.getStringProperty(TABLE_NAME_COL));
            String description = rowData.getStringProperty(DESCRIPTION_COL);
            String query = "SELECT * FROM " + encapsulateTableName;

            queryExternalTapServiceData(storedAccessUrl, encapsulateTableName, description, query, true, false);
        }

        @Override
        public void onAdqlButtonPressed(GeneralJavaScriptObject rowData) {
            if (queryPopupPanel == null) {
                queryPopupPanel = createQueryPopupPanel();
            }

            queryPopupPanel.setTapServiceUrl(storedAccessUrl);
            queryPopupPanel.setTapTable(rowData.getStringProperty(TABLE_NAME_COL));
            queryPopupPanel.setTapDescription(rowData.getStringProperty(DESCRIPTION_COL));
            queryPopupPanel.show();
        }

        @Override
        public void onAddObscoreTableClicked(GeneralJavaScriptObject rowData) {
            String tableName = ExtTapUtils.encapsulateTableName(rowData.getStringProperty(TABLE_NAME_COL));
            String query = "SELECT * FROM " + tableName;
            queryExternalTapServiceMetadata(storedAccessUrl, query, new JSONUtils.IJSONRequestCallback() {
                @Override
                public void onSuccess(String responseText) {
                    setIsLoading(false);

                    TapDescriptorListMapper mapper = GWT.create(TapDescriptorListMapper.class);
                    TapDescriptorList descriptorList = mapper.read(responseText);

                    if (descriptorList != null) {
                        List<TapMetadataDescriptor> metadataDescriptorList = ExtTapUtils.getMetadataFromTapDescriptorList(descriptorList, false);
                        CommonTapDescriptor commonTapDescriptor = DescriptorRepository.getInstance().createExternalDescriptor(metadataDescriptorList,
                                storedAccessUrl, tableName, storedName, null, "", true, false);
                        DescriptorRepository.getInstance().addExternalDataCenterDescriptor(commonTapDescriptor);
                        fireEvent(new TreeMapNewDataEvent(null));
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

            queryExternalTapServiceData(storedAccessUrl, "tap_schema.columns", null, query, false, false);
        }

        private void exploreTapServiceTables(String tapUrl) {
            setIsLoading(true);

            // First we attempt to get tables from the tap_schema.
            // If that fails, we attempt to retrieve tables from tap/tables route.
            String query = "SELECT schema_name, table_name, description FROM tap_schema.tables";
            String url = EsaSkyWebConstants.EXT_TAP_URL + "?"
                    + EsaSkyConstants.EXT_TAP_ACTION_FLAG + "=" + EsaSkyConstants.EXT_TAP_ACTION_REQUEST + "&"
                    + EsaSkyConstants.EXT_TAP_ADQL_FLAG + "=" + query + "&"
                    + EsaSkyConstants.EXT_TAP_URL_FLAG + "=" + tapUrl + "&"
                    + EsaSkyConstants.EXT_TAP_MAX_REC_FLAG + "=" + 100000;

            JSONUtils.getJSONFromUrl(url, new JSONUtils.IJSONRequestCallback() {
                @Override
                public void onSuccess(String responseText) {
                    setIsLoading(false);
                    backButton.setVisible(true);
                    GlobalTapPanel.this.onDataLoaded(responseText, tapTablesWrapper);
                }

                @Override
                public void onError(String errorCause) {

                    String url = EsaSkyWebConstants.TAPREGISTRY_URL
                            + "?" + EsaSkyConstants.REGISTRY_TAP_TARGET + "=" + tapUrl;
                    JSONUtils.getJSONFromUrl(url, new JSONUtils.IJSONRequestCallback() {
                        @Override
                        public void onSuccess(String responseText) {
                            setIsLoading(false);
                            backButton.setVisible(true);
                            GlobalTapPanel.this.onDataLoaded(responseText, tapTablesWrapper);
                        }

                        @Override
                        public void onError(String errorCause) {
                            showErrorMessage(storedName);
                            setIsLoading(false);
                        }
                    });
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
                        commonTapDescriptor.setColor(ESASkyColors.getNext());
                        if (fovLimit && fovLimiterEnabled && commonTapDescriptor.isFovLimitDisabled()) {
                            handleMissingColumns(commonTapDescriptor);
                        } else {
                            CommonEventBus.getEventBus().fireEvent(new TapRegistrySelectEvent(commonTapDescriptor));
                        }
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

        private void handleMissingColumns(CommonTapDescriptor descriptor) {
            ConfirmationPopupPanel confirmationPopupPanel = createConfirmationPopupPanel();

            confirmationPopupPanel.addDialogEventHandler(action -> {
                if (action.getAction() == DialogActionEvent.DialogAction.YES) {
                    handleColumnSelection(descriptor);
                } else if (action.getAction() == DialogActionEvent.DialogAction.NO) {
                    CommonEventBus.getEventBus().fireEvent(new TapRegistrySelectEvent(descriptor));
                }
            });

            confirmationPopupPanel.show();
        }

        private void handleColumnSelection(CommonTapDescriptor descriptor) {
            ColumnSelectorPopupPanel columnSelectorPopupPanel = createColumnSelectionPopupPanel(descriptor.getMetadata());
            columnSelectorPopupPanel.addColumnSelectionHandler(event -> {
                if (event.isRegionQuery()) {
                    descriptor.getMetadata().stream().filter(x -> Objects.equals(x.getName(), event.getRegionColumn())).findFirst().ifPresent(y -> y.setUcd(UCD.POS_OUTLINE.getValue()));
                    descriptor.setUseIntersectsPolygon(true);
                } else {
                    descriptor.getMetadata().stream().filter(x -> Objects.equals(x.getName(), event.getRaColumn())).findFirst().ifPresent(y -> y.setUcd(UCD.POS_EQ_RA.getValue()));
                    descriptor.getMetadata().stream().filter(x -> Objects.equals(x.getName(), event.getDecColumn())).findFirst().ifPresent(y -> y.setUcd(UCD.POS_EQ_DEC.getValue()));
                }

                descriptor.setFovLimitDisabled(false);
                CommonEventBus.getEventBus().fireEvent(new TapRegistrySelectEvent(descriptor));
            });

            columnSelectorPopupPanel.show();
        }

        private QueryPopupPanel createQueryPopupPanel() {
            QueryPopupPanel popupPanel = new QueryPopupPanel();

            String description = TextMgr.getInstance().getText("global_tap_panel_custom_query_description") + " ";
            popupPanel.addQueryHandler(event -> queryExternalTapServiceData(event.getTapUrl(), event.getTableName(),
                    description + event.getQuery(), event.getQuery(), false, true));

            return popupPanel;
        }

        private ConfirmationPopupPanel createConfirmationPopupPanel() {
            return new ConfirmationPopupPanel(
                    GoogleAnalytics.CAT_GLOBALTAP_SELECTCOLUMNPANEL,
                    TextMgr.getInstance().getText("global_tap_panel_missing_column_title"),
                    TextMgr.getInstance().getText("global_tap_panel_missing_column_body"),
                    TextMgr.getInstance().getText("global_tap_panel_missing_column_body"));
        }

        private ColumnSelectorPopupPanel createColumnSelectionPopupPanel(List<TapMetadataDescriptor> metadataList) {
            return new ColumnSelectorPopupPanel(TextMgr.getInstance().getText("global_tap_panel_column_selector_header"),
                    TextMgr.getInstance().getText("global_tap_panel_column_selector_help"), metadataList);
        }
    }

    public HandlerRegistration addTreeMapNewDataHandler(TreeMapNewDataEventHandler handler) {
        return addHandler(handler, TreeMapNewDataEvent.TYPE);
    }

    public void hideActionWidgets() {
        parentHeader.removeActionWidget(switchBtn);
    }

    public void showActionWidgets() {
        parentHeader.addActionWidget(switchBtn);
    }

}


