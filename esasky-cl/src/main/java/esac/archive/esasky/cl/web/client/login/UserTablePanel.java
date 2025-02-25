/*
ESASky
Copyright (C) 2025 Henrik Norman

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

package esac.archive.esasky.cl.web.client.login;

import com.allen_sauer.gwt.log.client.Log;
import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.*;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.DialogActionEvent;
import esac.archive.esasky.cl.web.client.event.exttap.TapRegistrySelectEvent;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.query.TAPUserTablesService;
import esac.archive.esasky.cl.web.client.query.TAPUtils;
import esac.archive.esasky.cl.web.client.repository.DescriptorRepository;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.ExtTapUtils;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.utility.JSONUtils;
import esac.archive.esasky.cl.web.client.view.ColumnSelectorPopupPanel;
import esac.archive.esasky.cl.web.client.view.common.ConfirmationPopupPanel;
import esac.archive.esasky.cl.web.client.view.common.TabulatorPopupSearchPanel;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.QueryPopupPanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.DefaultTabulatorCallback;
import esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorSettings;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.TapDescriptorList;
import esac.archive.esasky.ifcs.model.descriptor.TapMetadataDescriptor;
import esac.archive.esasky.ifcs.model.shared.ESASkyColors;
import esac.archive.esasky.ifcs.model.shared.contentdescriptors.UCD;

import java.util.List;
import java.util.Objects;


public class UserTablePanel extends TabulatorPopupSearchPanel {
    private final UploadTablePopupPanel uploadTablePopupPanel;


    public interface TapDescriptorListMapper extends ObjectMapper<TapDescriptorList> {
    }

    private static final TabulatorCallback tabulatorCallback = new TabulatorCallback();
    private static final String TABLE_NAME_COL = "table_name";


    public UserTablePanel() {
        super(GoogleAnalytics.CAT_USERAREA, true, TextMgr.getInstance().getText("userArea__userTablePanel_title"), TextMgr.getInstance().getText("userArea__userTablePanel_help"), tabulatorCallback, getTabulatorSettings());
        uploadTablePopupPanel = new UploadTablePopupPanel();
        uploadTablePopupPanel.addSubmitHandler(event -> {
            uploadTablePopupPanel.addStyleName("displayNone");
            setIsLoading(true);
        });
        uploadTablePopupPanel.addSubmitCompleteHandler(event -> {
            String results = event.getResults();
            if (results.contains("TAP_SERVICE_STATUS=Uploaded")) {
                uploadTablePopupPanel.hide();
                loadUserTables();
            } else {
                fireErrorEvent("Failed to upload table", results);
            }

            setIsLoading(false);
        });

        uploadTablePopupPanel.addErrorHandler(this::fireEvent);
        this.setSearchColumns("schema_name", "table_name");
        setSearchPlaceholder(TextMgr.getInstance().getText("userArea__userTablePanel_searchTables"));

    }

    @Override
    protected void onLoad() {
        super.onLoad();
        tabulatorCallback.setUserTablePanel(this);
        loadUserTables();
    }

    public void showUploadPanel() {
        uploadTablePopupPanel.show();
    }

    private static TabulatorSettings getTabulatorSettings() {
        TabulatorSettings settings = new TabulatorSettings();
        settings.setAddAdqlColumn(true);
        settings.setAddMetadataColumn(false);
        settings.setIsDownloadable(false);
        settings.setSelectable(1);
        settings.setAddObscoreTableColumn(false);
        settings.setAddOpenTableColumn(true);
        settings.setDeleteRowColumn(true);
        settings.setCustomMetadata(false);
        settings.setShowCreateRowButton(true, TextMgr.getInstance().getText("userArea__userTablePanel_addTableTitle"), "images/upload-icon.png");
        return settings;

    }

    private void loadUserTables() {
        this.setIsLoading(true);
        TAPUserTablesService.getInstance().fetchUserTables(new JSONUtils.IJSONRequestCallback() {
            @Override
            public void onSuccess(String responseText) {
                onJsonLoaded(responseText);
            }

            @Override
            public void onError(int status, String errorCause) {
                Log.error("[UserTables] initUserTables ERROR: " + errorCause);
                fireErrorEvent(status, "Failed to load tables", errorCause);
            }

            @Override
            public void whenComplete() {
                setIsLoading(false);
            }
        });
    }

    private void onJsonLoaded(String responseText) {
        GeneralJavaScriptObject obj = GeneralJavaScriptObject.createJsonObject(responseText);
        GeneralJavaScriptObject data = obj.getProperty("data");

        GeneralJavaScriptObject metadata = createMetadata();
        tabulatorWrapper.insertData(data, metadata);
        tabulatorWrapper.restoreRedraw();
        tabulatorWrapper.redrawAndReinitializeHozVDom();
    }

    public native GeneralJavaScriptObject createMetadata() /*-{

        var metadata = [];
        metadata.push(
            {
                name: "schema_name",
                displayName: "schema_name",
                datatype: "STRING",
                visible: true
            });
        metadata.push(
            {
                name: "table_name",
                displayName: "table_name",
                datatype: "STRING",
                visible: true
            });
        return metadata;
    }-*/;

    private static class TabulatorCallback extends DefaultTabulatorCallback {
        QueryPopupPanel queryPopupPanel;
        UserTablePanel userTablePanel;

        public TabulatorCallback() {
            super();
        }

        public void setUserTablePanel(UserTablePanel userTablePanel) {
            this.userTablePanel = userTablePanel;
        }

        private QueryPopupPanel createQueryPopupPanel() {
            QueryPopupPanel popupPanel = new QueryPopupPanel();

            popupPanel.addQueryHandler(event -> {
                String query = event.getQuery();
                String fullTableName = event.getTableName();
                TabulatorCallback.this.doQuery(query, fullTableName, true);
            });

            return popupPanel;
        }

        @Override
        public void onOpenTableClicked(GeneralJavaScriptObject rowData) {
            String schemaName = rowData.getStringProperty("schema_name");
            String tableName = rowData.getStringProperty(TABLE_NAME_COL);
            String fullTableName = schemaName + "." + tableName;
            String query = "SELECT * FROM " + fullTableName;

            this.doQuery(query, fullTableName, false);
        }

        @Override
        public void onAdqlButtonPressed(GeneralJavaScriptObject rowData) {
            if (queryPopupPanel == null) {
                queryPopupPanel = createQueryPopupPanel();
            }

            queryPopupPanel.setTapServiceUrl(EsaSkyWebConstants.TAP_CONTEXT + "/tap/sync");
            queryPopupPanel.setTapTable(rowData.getStringProperty(TABLE_NAME_COL));
            queryPopupPanel.disableExample(QueryPopupPanel.PopupMenuItems.METADATA);
            queryPopupPanel.setTapDescription("test_desc");
            queryPopupPanel.setPublisher("test_pub");
            queryPopupPanel.show();
        }


        @Override
        public void onMetadataButtonPressed(GeneralJavaScriptObject rowData) {
            String schemaName = rowData.getStringProperty("schema_name");
            String tableName = rowData.getStringProperty(TABLE_NAME_COL);
            String fullTableName = schemaName + "." + tableName;
            String query = "SELECT * FROM TAP_SCHEMA.columns where table_name='" + fullTableName + "'";


            this.doQuery(query, fullTableName, false);

        }

        @Override
        public void onDeleteRowClicked(GeneralJavaScriptObject rowData) {
            String schemaName = rowData.getStringProperty("schema_name");
            String tableName = rowData.getStringProperty(TABLE_NAME_COL);
            String fullTableName = schemaName + "." + tableName;


            ConfirmationPopupPanel confirmationPopupPanel = new ConfirmationPopupPanel(
                    GoogleAnalytics.CAT_USERAREA,
                    TextMgr.getInstance().getText("userArea__deleteTableTitle"),
                    TextMgr.getInstance().getText("userArea__deleteTableBody"),
                    TextMgr.getInstance().getText("userArea__deleteTableHelp"));

            confirmationPopupPanel.addDialogEventHandler(event -> {
                if (event.getAction() == DialogActionEvent.DialogAction.YES) {
                    sendDeleteRequest(fullTableName);
                }
            });

            confirmationPopupPanel.show();
        }


        @Override
        public void onCreateRowClicked() {
            userTablePanel.showUploadPanel();
        }


        private void sendDeleteRequest(String fullTableName) {
            userTablePanel.setIsLoading(true);
            String postData = "TABLE_NAME=" + fullTableName + "&DELETE=TRUE";

            RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST, EsaSkyWebConstants.UPLOAD_TABLE_URL);
            requestBuilder.setHeader("Content-Type", "application/x-www-form-urlencoded");
            requestBuilder.setRequestData(postData);

            try {
                requestBuilder.sendRequest(postData, new RequestCallback() {
                    @Override
                    public void onResponseReceived(Request request, Response response) {
                        if (response.getStatusCode() == 200) {
                            userTablePanel.loadUserTables();
                        } else {
                            userTablePanel.fireErrorEvent(response.getStatusCode(), "Failed to delete table", response.getText());
                        }

                        userTablePanel.setIsLoading(false);
                    }

                    @Override
                    public void onError(Request request, Throwable throwable) {
                        Log.info("Request error: " + throwable.getMessage());
                        userTablePanel.fireErrorEvent("Failed to delete table", throwable.getMessage());
                        userTablePanel.setIsLoading(false);
                    }
                });
            } catch (RequestException e) {
                Log.info("Request exception: " + e.getMessage());
                userTablePanel.fireErrorEvent("Failed to delete table", e);
                userTablePanel.setIsLoading(false);
            }
        }

        private void doQuery(String query, String fullTableName, boolean custom) {
            userTablePanel.setIsLoading(true);
            JSONUtils.getJSONFromUrl(TAPUtils.getTAPQuery(query, "json"), new JSONUtils.IJSONRequestCallback() {
                @Override
                public void onSuccess(String responseText) {
                    GeneralJavaScriptObject obj = GeneralJavaScriptObject.createJsonObject(responseText);
                    TapDescriptorListMapper mapper = GWT.create(TapDescriptorListMapper.class);
                    TapDescriptorList descriptorList = mapper.read(responseText);
                    if (descriptorList != null) {
                        List<TapMetadataDescriptor> metadataDescriptorList = ExtTapUtils.getMetadataFromTapDescriptorList(descriptorList, false);
                        CommonTapDescriptor commonTapDescriptor = DescriptorRepository.getInstance().createExternalDescriptor(metadataDescriptorList, EsaSkyWebConstants.TAP_CONTEXT + "/tap/sync",
                                fullTableName, fullTableName, "user_description", query, false, true);
                        commonTapDescriptor.setColor(ESASkyColors.getNext());
                        commonTapDescriptor.setIsUserTable(true);

                        boolean isMissingSpatial = !custom &&
                                ((commonTapDescriptor.getRaColumn() == null || commonTapDescriptor.getDecColumn() == null)
                                        && commonTapDescriptor.getRegionColumn() == null);

                        if (isMissingSpatial) {
                            handleMissingColumns(commonTapDescriptor, obj);
                        } else {
                            CommonEventBus.getEventBus().fireEvent(new TapRegistrySelectEvent(commonTapDescriptor, obj));
                        }

                    }

                }

                @Override
                public void onError(int status, String errorCause) {
                    userTablePanel.fireErrorEvent(status, "Failed to query table. ", errorCause);
                    Log.error("[UserTables] onMetadataButtonPressed ERROR: " + errorCause);
                }

                @Override
                public void whenComplete() {
                    userTablePanel.setIsLoading(false);
                }
            }, true);
        }

        private void handleMissingColumns(CommonTapDescriptor descriptor, GeneralJavaScriptObject obj) {
            ConfirmationPopupPanel confirmationPopupPanel = new ConfirmationPopupPanel(
                    GoogleAnalytics.CAT_GLOBALTAP_SELECTCOLUMNPANEL,
                    TextMgr.getInstance().getText("global_tap_panel_missing_column_title"),
                    TextMgr.getInstance().getText("global_tap_panel_missing_column_body"),
                    TextMgr.getInstance().getText("global_tap_panel_missing_column_body"));

            confirmationPopupPanel.addDialogEventHandler(action -> {
                if (action.getAction() == DialogActionEvent.DialogAction.YES) {
                    handleColumnSelection(descriptor, obj);
                } else if (action.getAction() == DialogActionEvent.DialogAction.NO) {
                    CommonEventBus.getEventBus().fireEvent(new TapRegistrySelectEvent(descriptor, obj));
                }
            });

            confirmationPopupPanel.show();
        }


        private void handleColumnSelection(CommonTapDescriptor descriptor, GeneralJavaScriptObject obj) {
            ColumnSelectorPopupPanel columnSelectorPopupPanel = new ColumnSelectorPopupPanel(TextMgr.getInstance().getText("global_tap_panel_column_selector_header"),
                    TextMgr.getInstance().getText("global_tap_panel_column_selector_help"), descriptor.getMetadata());

            columnSelectorPopupPanel.addColumnSelectionHandler(event -> {
                if (event.isRegionQuery()) {
                    descriptor.getMetadata().stream().filter(x -> Objects.equals(x.getName(), event.getRegionColumn())).findFirst().ifPresent(y -> y.setUcd(UCD.POS_OUTLINE.getValue()));
                    descriptor.setUseIntersectsPolygon(true);
                } else {
                    descriptor.getMetadata().stream().filter(x -> Objects.equals(x.getName(), event.getRaColumn())).findFirst().ifPresent(y -> y.setUcd(UCD.POS_EQ_RA.getValue()));
                    descriptor.getMetadata().stream().filter(x -> Objects.equals(x.getName(), event.getDecColumn())).findFirst().ifPresent(y -> y.setUcd(UCD.POS_EQ_DEC.getValue()));
                }

                descriptor.setFovLimitDisabled(false);
                CommonEventBus.getEventBus().fireEvent(new TapRegistrySelectEvent(descriptor, obj));
            });

            columnSelectorPopupPanel.show();
        }
    }
}
