/*
ESASky
Copyright (C) 2025 European Space Agency

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published
by the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package esac.archive.esasky.cl.web.client.view.ctrltoolbar;

import com.allen_sauer.gwt.log.client.Log;
import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextBox;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.DialogActionEvent;
import esac.archive.esasky.cl.web.client.event.TreeMapNewDataEvent;
import esac.archive.esasky.cl.web.client.event.TreeMapNewDataEventHandler;
import esac.archive.esasky.cl.web.client.event.exttap.TapRegistrySelectEvent;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.ifcs.model.shared.GeoFeature;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
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
    private boolean initialDataLoaded = false;
    private boolean fovLimiterEnabled = false;
    private static final String TABLE_NAME_COL = "table_name";
    private static final String DESCRIPTION_COL = "description";
    private static final String ACCESS_URL_COL = "access_url";
    private static final String SHORT_NAME_COL = "short_name";
    private static final String PUBLISHER_COL = "publisher";
    private static final String DISPLAY_NONE = "displayNone";
    private static final String PLACEHOLDER = "placeholder";
    private static final String TABULATOR_SERVICE_ID_PREFIX = "browseTap__tabulatorServicesContainer_";
    private static final String TABULATOR_TABLE_ID_PREFIX = "browseTap__tabulatorTablesContainer_";
    private static final String RESULT_HELP_DESCRIPTION_KEY = "resultsPresenter_helpDescription_custom_tap_entity";
    private static final String PUBLISHER_REPLACE_KEY = "$PUBLISHER$";
    
    public enum Modes {REGISTRY, VIZIER, ESA}

    private final Modes mode;

    private static final Map<String, Boolean> tapGeoFeatures = new HashMap<>();

    private final EsaSkySwitch fovSwitch;


    public interface Resources extends ClientBundle {
        @Source("globalTapPanel.css")
        @CssResource.NotStrict
        CssResource style();
    }

    public GlobalTapPanel() {
        this(Modes.REGISTRY);
    }

    public GlobalTapPanel(Modes mode) {
        this.resources = GWT.create(GlobalTapPanel.Resources.class);
        this.style = this.resources.style();
        this.style.ensureInjected();
        this.mode = mode;
        fovSwitch = new EsaSkySwitch("fovLimiterSwitch_" + mode, fovLimiterEnabled,
                TextMgr.getInstance().getText("global_tap_panel_toggle_fov_restricted"),
                TextMgr.getInstance().getText("global_tap_panel_toggle_fov_restricted_tooltip"));
        initView();
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        initTapServicesWrapper();
        initTapServicesTableWrapper();
    }

    private void initView() {
        this.addStyleName("globalTapPanel");

        FlowPanel mainContainer = new FlowPanel();
        mainContainer.addStyleName("globalTapPanel__container");

        tapServicesGlass = new GlassFlowPanel();
        tapServicesGlass.addStyleName("globalTapPanel__tabulatorGlassContainer");
        FlowPanel tapServicesContainer = new FlowPanel();
        tapServicesContainer.getElement().setId(TABULATOR_SERVICE_ID_PREFIX + mode);
        tapServicesContainer.addStyleName("globalTapPanel__tabulatorContainer");
        tapServicesGlass.add(tapServicesContainer);

        tapTablesGlass = new GlassFlowPanel();
        tapTablesGlass.addStyleName("globalTapPanel__tabulatorGlassContainer");
        FlowPanel tapTablesContainer = new FlowPanel();
        tapTablesContainer.getElement().setId(TABULATOR_TABLE_ID_PREFIX + mode);
        tapTablesContainer.addStyleName("globalTapPanel__tabulatorContainer");
        tapTablesGlass.add(tapTablesContainer);
        tapTablesGlass.addStyleName(DISPLAY_NONE);

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

        fovSwitch.addStyleName("globalTapPanel__fovSwitch");
        fovSwitch.addClickHandler(event -> {
            fovLimiterEnabled = !fovLimiterEnabled;
            fovSwitch.setChecked(fovLimiterEnabled);
        });
        fovSwitch.setVisible(false);

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

        backButton = new EsaSkyButton(Icons.getBackArrowIcon());
        backButton.setBackgroundColor("#aaa");
        backButton.setRoundStyle();
        setBackButtonVisible(false);
        backButton.addClickHandler(event -> {
            setBackButtonVisible(false);
            showTable(tapServicesWrapper);
            fovSwitch.setVisible(false);
        });

        searchRowContainer.add(backButton);
        searchRowContainer.add(searchBoxContainer);
        searchRowContainer.add(fovSwitch);

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
        settings.setAddObscoreTableColumn(true);
        settings.setSelectable(1);
        tapServicesWrapper = new TabulatorWrapper(TABULATOR_SERVICE_ID_PREFIX + mode, tabulatorCallback, settings);
        final Element rowCountFooter = Document.get().getElementById(TABULATOR_SERVICE_ID_PREFIX + mode + "_rowCount");
        rowCountFooter.getStyle().setMarginTop(-25, Style.Unit.PX);

    }

    private void initTapServicesTableWrapper() {
        TabulatorSettings settings = new TabulatorSettings();
        settings.setAddAdqlColumn(true);
        settings.setAddMetadataColumn(true);
        settings.setIsDownloadable(false);
        settings.setSelectable(1);
        settings.setAddObscoreTableColumn(false);
        settings.setAddOpenTableColumn(true);
        tapTablesWrapper = new TabulatorWrapper(TABULATOR_TABLE_ID_PREFIX + mode, tabulatorCallback, settings);
        tapTablesWrapper.groupByColumns("schema_name");
        final Element rowCountFooter = Document.get().getElementById(TABULATOR_TABLE_ID_PREFIX + mode + "_rowCount");
        rowCountFooter.getStyle().setMarginTop(-25, Style.Unit.PX);
    }


    private void onDataLoaded(String jsonString, TabulatorWrapper wrapper) {
        onDataLoaded(jsonString, wrapper, null, (String) null);
    }

    private void onDataLoaded(String jsonString, TabulatorWrapper wrapper, String sortByColumn, String... hiddenColumns) {
        GeneralJavaScriptObject obj = GeneralJavaScriptObject.createJsonObject(jsonString);

        GeneralJavaScriptObject data = obj.getProperty("data");
        GeneralJavaScriptObject metadata = obj.hasProperty("columns")
                ? obj.getProperty("columns")
                : obj.getProperty("metadata");

        wrapper.clearFilters(true);
        showTable(wrapper);
        wrapper.insertExternalTapData(data, metadata);

        if (hiddenColumns != null) {
            for (String column : hiddenColumns) {
                wrapper.hideColumn(column);
            }
        }

        if (sortByColumn != null) {
            wrapper.sortByColumn(sortByColumn, false);
        }

        wrapper.restoreRedraw();
        wrapper.redrawAndReinitializeHozVDom();
    }

    public void loadData() {
        if (!initialDataLoaded) {
            setIsLoading(true);

            if (mode.equals(Modes.REGISTRY)) {
                loadRegistryData("");
            } else if (mode.equals(Modes.VIZIER)) {
                loadVizierData();
            } else if (mode.equals(Modes.ESA)) {
                loadRegistryData("ivo://esavo");
            }

            initialDataLoaded = true;
        }
    }


    private void loadRegistryData(String ivoid) {
        String url = EsaSkyWebConstants.TAPREGISTRY_URL;
        if (ivoid != null && !ivoid.isEmpty()) {
            url += "?" + EsaSkyConstants.REGISTRY_TAP_IVOID + "=" + ivoid;
        }
        JSONUtils.getJSONFromUrl(url, new JSONUtils.IJSONRequestCallback() {
            @Override
            public void onSuccess(String responseText) {
                setIsLoading(false);
                onDataLoaded(responseText, tapServicesWrapper, "table_count", "table_count", "table_names");
            }

            @Override
            public void onError(String errorCause) {
                setIsLoading(false);
                showErrorMessage("Registry", errorCause);
            }
        }, true);
    }

    private void loadVizierData() {
        exploreTapServiceTables("http://tapvizier.cds.unistra.fr/TAPVizieR/tap", "VizieR", Optional.of(true));
        tabulatorCallback.storedAccessUrl = "http://tapvizier.cds.unistra.fr/TAPVizieR/tap";
        tabulatorCallback.storedName = "VizieR";
        tabulatorCallback.storedPublisher = "CDS VizieR service";
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
                ? new String[]{"res_title", SHORT_NAME_COL, "res_subjects", PUBLISHER_COL, "table_names"}
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

    private void queryExternalTapServiceData(String tapUrl, String tableName, String mission, String description, String query, boolean fovLimit, boolean useUnprocessedQuery) {
        queryExternalTapServiceMetadata(tapUrl, query, new JSONUtils.IJSONRequestCallback() {
            @Override
            public void onSuccess(String responseText) {
                setIsLoading(false);

                TapDescriptorListMapper mapper = GWT.create(TapDescriptorListMapper.class);
                TapDescriptorList descriptorList = mapper.read(responseText);

                if (descriptorList != null) {
                    List<TapMetadataDescriptor> metadataDescriptorList = ExtTapUtils.getMetadataFromTapDescriptorList(descriptorList, false);
                    CommonTapDescriptor commonTapDescriptor = DescriptorRepository.getInstance().createExternalDescriptor(metadataDescriptorList, tapUrl,
                            tableName, mission, description, query, fovLimit && fovLimiterEnabled, useUnprocessedQuery);
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
                showErrorMessage(mission, errorCause);
                setIsLoading(false);
            }
        });
    }

    private void getCapabilities(String tapUrl) {
        String url = EsaSkyWebConstants.EXT_TAP_URL + "?"
                + EsaSkyConstants.EXT_TAP_ACTION_FLAG + "=" + EsaSkyConstants.EXT_TAP_CAPABILITIES_REQUEST + "&"
                + EsaSkyConstants.EXT_TAP_URL_FLAG + "=" + tapUrl;
        JSONUtils.getJSONFromUrl(url, new RequestCallback() {
            @Override
            public void onResponseReceived(Request request, Response response) {
                if (response.getStatusCode() == 200) {
                    GeneralJavaScriptObject responseObj = GeneralJavaScriptObject.createJsonObject(response.getText());

                    Set<GeoFeature> features = new HashSet<>();
                    for (GeneralJavaScriptObject featureObject : GeneralJavaScriptObject.convertToArray(responseObj.getProperty("features"))) {
                        try {
                            features.add(GeoFeature.valueOf(featureObject.toString()));
                        } catch (IllegalArgumentException ignore) {}
                    }
                    boolean hasFoVSupport = features.containsAll(Arrays.asList(GeoFeature.CONTAINS, GeoFeature.CIRCLE, GeoFeature.POLYGON));
                    tapGeoFeatures.put(tapUrl, hasFoVSupport);
                    setFoVState(hasFoVSupport);
                } else {
                    Log.warn(this.getClass().getSimpleName() + " Capabilities request got a bad response code: " + response.getStatusCode() + ": " + response.getStatusText());
                    setFoVState(true);
                }
            }

            @Override
            public void onError(Request request, Throwable exception) {
                setFoVState(true);
            }
        });
    }

    private void setFoVState(boolean hasFoVSupport) {
        fovLimiterEnabled = hasFoVSupport;
        fovSwitch.setChecked(hasFoVSupport);
        fovSwitch.setVisible(true);
    }

    private void queryExternalTapServiceMetadata(String tapUrl, String query, JSONUtils.IJSONRequestCallback callback) {
        setIsLoading(true);

        String url = EsaSkyWebConstants.EXT_TAP_URL + "?"
                + EsaSkyConstants.EXT_TAP_ACTION_FLAG + "=" + EsaSkyConstants.EXT_TAP_ACTION_REQUEST + "&"
                + EsaSkyConstants.EXT_TAP_ADQL_FLAG + "=" + URL.encodeQueryString(query) + "&"
                + EsaSkyConstants.EXT_TAP_URL_FLAG + "=" + tapUrl + "&"
                + EsaSkyConstants.EXT_TAP_MAX_REC_FLAG + "=" + 1;

        JSONUtils.getJSONFromUrl(url, callback, true);
    }

    public void exploreTapServiceTables(String tapUrl, String mission, final Optional<Boolean> fovCapabilityOverride) {
        setIsLoading(true);

        // First we attempt to get tables from the tap_schema.
        // If that fails, we attempt to retrieve tables from tap/tables route.
        String query = "SELECT schema_name, table_name, description FROM TAP_SCHEMA.tables";
        String url = EsaSkyWebConstants.EXT_TAP_URL + "?"
                + EsaSkyConstants.EXT_TAP_ACTION_FLAG + "=" + EsaSkyConstants.EXT_TAP_ACTION_REQUEST + "&"
                + EsaSkyConstants.EXT_TAP_ADQL_FLAG + "=" + query + "&"
                + EsaSkyConstants.EXT_TAP_URL_FLAG + "=" + tapUrl + "&"
                + EsaSkyConstants.EXT_TAP_MAX_REC_FLAG + "=" + 100000;

        JSONUtils.getJSONFromUrl(url, new JSONUtils.IJSONRequestCallback() {
            @Override
            public void onSuccess(String responseText) {
                setIsLoading(false);
                setBackButtonVisible(true);
                GlobalTapPanel.this.onDataLoaded(responseText, tapTablesWrapper);

                if (tapGeoFeatures.containsKey(tapUrl)) {
                    setFoVState(tapGeoFeatures.get(tapUrl));
                } else if (fovCapabilityOverride.isPresent()) {
                    setFoVState(fovCapabilityOverride.get());
                } else {
                    getCapabilities(tapUrl);
                }
            }

            @Override
            public void onError(String errorCause) {

                String url = EsaSkyWebConstants.TAPREGISTRY_URL
                        + "?" + EsaSkyConstants.REGISTRY_TAP_TARGET + "=" + tapUrl;
                JSONUtils.getJSONFromUrl(url, new JSONUtils.IJSONRequestCallback() {
                    @Override
                    public void onSuccess(String responseText) {
                        setIsLoading(false);
                        setBackButtonVisible(true);
                        GlobalTapPanel.this.onDataLoaded(responseText, tapTablesWrapper);
                    }

                    @Override
                    public void onError(String errorCause) {
                        showErrorMessage(mission, errorCause);
                        setIsLoading(false);
                    }
                });
            }
        }, true);
    }

    private void showErrorMessage(String name, String details) {
        String title = TextMgr.getInstance().getText("global_tap_panel_query_failed_title");
        String body = TextMgr.getInstance().getText("global_tap_panel_query_failed_body").replace("$TAP_SERVICE$", name);
        body += "<br><br><details style=\"overflow: scroll; max-height: 80px;\">" + details + "</details>";
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


    public HandlerRegistration addTreeMapNewDataHandler(TreeMapNewDataEventHandler handler) {
        return addHandler(handler, TreeMapNewDataEvent.TYPE);
    }

    public void setBackButtonVisible(boolean visible) {
        backButton.setVisible(visible && !mode.equals(Modes.VIZIER));
    }

    // Tabulator interaction
    private class TabulatorCallback extends DefaultTabulatorCallback {
        private String storedAccessUrl;
        private String storedName;
        private String storedPublisher;
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

            if (!Objects.equals(accessUrl, storedAccessUrl)) {

                storedAccessUrl = accessUrl;
                storedName =  rowData.hasProperty(SHORT_NAME_COL)
                        ? rowData.getStringProperty(SHORT_NAME_COL)
                        : rowData.getStringProperty("res_title");
                storedPublisher = rowData.hasProperty(PUBLISHER_COL)
                        ? rowData.getStringProperty(PUBLISHER_COL)
                        : "";
            }


            if (!rowData.hasProperty(TABLE_NAME_COL)) {
                // Query for all tables in tap_schema.tables
                exploreTapServiceTables(accessUrl, storedName, Optional.<Boolean>empty());
            }
        }

        @Override
        public void onOpenTableClicked(GeneralJavaScriptObject rowData) {
            String encapsulateTableName = ExtTapUtils.encapsulateTableName(rowData.getStringProperty(TABLE_NAME_COL));
            String description =  TextMgr.getInstance().getText(RESULT_HELP_DESCRIPTION_KEY).replace(PUBLISHER_REPLACE_KEY, storedPublisher);
            String query = "SELECT * FROM " + encapsulateTableName;

            queryExternalTapServiceData(storedAccessUrl, encapsulateTableName, storedName, description, query, true, false);
        }

        @Override
        public void onAdqlButtonPressed(GeneralJavaScriptObject rowData) {
            if (queryPopupPanel == null) {
                queryPopupPanel = createQueryPopupPanel();
            }

            queryPopupPanel.setTapServiceUrl(storedAccessUrl);
            queryPopupPanel.setTapTable(rowData.getStringProperty(TABLE_NAME_COL));
            queryPopupPanel.setTapDescription(rowData.getStringProperty(DESCRIPTION_COL));
            queryPopupPanel.setPublisher(storedPublisher);
            queryPopupPanel.show();
        }

        @Override
        public void onAddObscoreTableClicked(GeneralJavaScriptObject rowData) {
            if (rowData.hasProperty(ACCESS_URL_COL)) {
                String accessUrl = rowData.getStringProperty(ACCESS_URL_COL);
                String missionName = rowData.getStringProperty(SHORT_NAME_COL);
                String publisher = rowData.getStringProperty(PUBLISHER_COL);
                String tableName = ExtTapUtils.encapsulateTableName("ivoa.obscore");
                String query = "SELECT * FROM " + tableName;
                queryExternalTapServiceMetadata(accessUrl, query, new JSONUtils.IJSONRequestCallback() {
                    @Override
                    public void onSuccess(String responseText) {
                        setIsLoading(false);

                        TapDescriptorListMapper mapper = GWT.create(TapDescriptorListMapper.class);
                        TapDescriptorList descriptorList = mapper.read(responseText);

                        if (descriptorList != null) {
                            List<TapMetadataDescriptor> metadataDescriptorList = ExtTapUtils.getMetadataFromTapDescriptorList(descriptorList, false);
                            CommonTapDescriptor commonTapDescriptor = DescriptorRepository.getInstance().createExternalDescriptor(metadataDescriptorList,
                                    accessUrl, tableName, missionName,
                                    TextMgr.getInstance().getText(RESULT_HELP_DESCRIPTION_KEY).replace(PUBLISHER_REPLACE_KEY, publisher),
                                    "", true, false);
                            DescriptorRepository.getInstance().addExternalDataCenterDescriptor(commonTapDescriptor);
                            fireEvent(new TreeMapNewDataEvent(null));
                        }
                    }

                    @Override
                    public void onError(String errorCause) {
                        showErrorMessage(missionName, errorCause);
                        setIsLoading(false);
                    }
                });
            }
        }

        @Override
        public void onMetadataButtonPressed(GeneralJavaScriptObject rowData) {
            String tableName = rowData.getStringProperty(TABLE_NAME_COL);
            String query = "SELECT * FROM TAP_SCHEMA.columns where table_name='" + tableName + "'";
            String description =  TextMgr.getInstance().getText(RESULT_HELP_DESCRIPTION_KEY).replace(PUBLISHER_REPLACE_KEY, storedPublisher);
            queryExternalTapServiceData(storedAccessUrl, "TAP_SCHEMA.columns", storedName, description, query, false, false);
        }

        private QueryPopupPanel createQueryPopupPanel() {
            QueryPopupPanel popupPanel = new QueryPopupPanel();

            popupPanel.addQueryHandler(event -> queryExternalTapServiceData(event.getTapUrl(), event.getTableName(), storedName,
                    TextMgr.getInstance().getText(RESULT_HELP_DESCRIPTION_KEY)
                            .replace(PUBLISHER_REPLACE_KEY, event.getPublisher()), event.getQuery(), false, true));

            return popupPanel;
        }
    }

}


