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

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import esac.archive.esasky.cl.web.client.Modules;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.presenter.MainPresenter;
import esac.archive.esasky.cl.web.client.utility.*;
import esac.archive.esasky.cl.web.client.utility.exceptions.MapKeyException;
import esac.archive.esasky.cl.web.client.view.common.TabulatorPopupSearchPanel;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyButton;
import esac.archive.esasky.cl.web.client.view.common.icons.Icons;
import esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.DefaultTabulatorCallback;
import esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorSettings;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import com.allen_sauer.gwt.log.client.Log;

import java.util.Objects;

public class UserLayoutPanel extends TabulatorPopupSearchPanel {

    private static final TabulatorCallback tabulatorCallback = new TabulatorCallback();

    private final EsaSkyButton saveLayoutButton;
    private final EsaSkyButton resetButton;

    private int currentLayoutId = -1;

    public UserLayoutPanel() {
        super(GoogleAnalytics.CAT_USERAREA, true, TextMgr.getInstance().getText("userArea__userLayoutPanel_title"), TextMgr.getInstance().getText("userArea__userLayoutPanel_help"), tabulatorCallback, getTabulatorSettings(true));
        tabulatorCallback.setuserLayoutPanel(this);

        saveLayoutButton = new EsaSkyButton(Icons.getSelectedIcon());
        saveLayoutButton.setTitle(TextMgr.getInstance().getText("userArea__userLayoutPanel_saveBtnTitle"));
        saveLayoutButton.addClickHandler(event -> {
            GeneralJavaScriptObject[] rows = tabulatorWrapper.getAllRows();
            saveLayoutValues(this.currentLayoutId, rows);
        });

        resetButton = new EsaSkyButton(Icons.getUndoArrowIcon());
        resetButton.setTitle(TextMgr.getInstance().getText("userArea__userLayoutPanel_resetBtnTitle"));
        resetButton.addClickHandler(event -> onResetButtonClicked());
    }

    private static TabulatorSettings getTabulatorSettings(boolean showCreateRowButton) {
        TabulatorSettings settings = new TabulatorSettings();
        settings.setAddAdqlColumn(false);
        settings.setAddMetadataColumn(false);
        settings.setIsDownloadable(false);
        settings.setSelectable(1);
        settings.setAddObscoreTableColumn(false);
        settings.setAddOpenTableColumn(false);
        settings.setCustomMetadata(true);
        settings.setShowCreateRowButton(showCreateRowButton, TextMgr.getInstance().getText("userArea__userLayoutPanel_addBtnTitle"));
        return settings;

    }

    @Override
    public void onLoad() {
        super.onLoad();
        loadAllLayouts();
    }

    @Override
    public void show() {
        resetButton.setEnabled(Modules.getMode().startsWith(EsaSkyWebConstants.MODULE_MODE_USER));
        super.show();
    }

    private void onResetButtonClicked() {
        Modules.setMode(EsaSkyWebConstants.MODULE_MODE_ESASKY);
        MainPresenter.getInstance().updateModuleVisibility();
        UrlUtils.updateURLWithoutReloadingJS(UrlUtils.getUrlForCurrentState());
        tabulatorWrapper.restoreRedraw();
        tabulatorWrapper.redrawAndReinitializeHozVDom();
        resetButton.setEnabled(false);
    }

    private void loadAllLayouts() {
        this.setIsLoading(true);
        JSONUtils.getJSONFromUrl(EsaSkyWebConstants.TAP_USERLAYOUTS_URL, new JSONUtils.IJSONRequestCallback() {
            @Override
            public void onSuccess(String responseText) {
                GeneralJavaScriptObject obj = GeneralJavaScriptObject.createJsonObject(responseText);
                GeneralJavaScriptObject data = obj.getProperty("data");
                GeneralJavaScriptObject metadata = obj.getProperty("columns");

                GeneralJavaScriptObject metaObj = ExtTapUtils.formatExternalTapMetadata(metadata);
                GeneralJavaScriptObject dataObj = ExtTapUtils.formatExternalTapData(data, metaObj);
                updateSettings(getTabulatorSettings(true));
                tabulatorWrapper.insertData(dataObj, createAllLayoutsMetadata(UserLayoutPanel.this));
                tabulatorWrapper.restoreRedraw();
                tabulatorWrapper.redrawAndReinitializeHozVDom();

                UserLayoutPanel.this.updateSearchBar(false);


            }

            @Override
            public void onError(int statusCode, String errorCause) {
                fireErrorEvent(statusCode, "Failed to fetch layouts", errorCause);
            }

            @Override
            public void whenComplete() {
                UserLayoutPanel.this.setIsLoading(false);
            }
        }, true);
    }

    private void loadLayout(int layoutId) {
        this.setIsLoading(true);
        String url = EsaSkyWebConstants.TAP_USERLAYOUTS_URL + "?layout_id=" + layoutId;
        JSONUtils.getJSONFromUrl(url, new JSONUtils.IJSONRequestCallback() {
            @Override
            public void onSuccess(String responseText) {
                GeneralJavaScriptObject obj = GeneralJavaScriptObject.createJsonObject(responseText);
                GeneralJavaScriptObject data = obj.getProperty("data");
                GeneralJavaScriptObject metadata = obj.getProperty("columns");

                GeneralJavaScriptObject metaObj = ExtTapUtils.formatExternalTapMetadata(metadata);
                GeneralJavaScriptObject dataObj = ExtTapUtils.formatExternalTapData(data, metaObj);

                activateLayout(Integer.toString(layoutId), dataObj);
                updateSettings(getTabulatorSettings(false));
                tabulatorWrapper.insertData(dataObj, createEditLayoutMetadata(UserLayoutPanel.this));

                tabulatorWrapper.restoreRedraw();
                tabulatorWrapper.redrawAndReinitializeHozVDom();

                currentLayoutId = layoutId;

                UserLayoutPanel.this.updateSearchBar(true);
            }

            @Override
            public void onError(int statusCode, String errorCause) {
                fireErrorEvent(statusCode, "Failed to load layout", errorCause);
            }

            @Override
            public void whenComplete() {
                UserLayoutPanel.this.setIsLoading(false);
            }
        }, true);
    }

    private void updateSearchBar(boolean isEditing) {
        if (isEditing) {
            this.removeActionWidget(resetButton);
            this.addActionWidget(saveLayoutButton);
            this.setSearchPlaceholder("Search settings");
            this.setSearchColumns("key");
        } else {
            this.removeActionWidget(saveLayoutButton);
            this.addActionWidget(resetButton);
            this.setSearchPlaceholder("Search layouts");
            this.setSearchColumns("name");

        }

        this.clearSearchText();
    }

    private void changeLayoutName(int layoutId, String newName) throws RequestException {
        this.setIsLoading(true);
        String url = EsaSkyWebConstants.TAP_USERLAYOUTS_URL + "?layout_id=" + layoutId + "&layout_name=" + newName;
        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.PUT, url);
        requestBuilder.sendRequest(null, new RequestCallback() {
            @Override
            public void onResponseReceived(com.google.gwt.http.client.Request request, com.google.gwt.http.client.Response response) {
                if (response.getStatusCode() == 200) {
                    UserLayoutPanel.this.setIsLoading(false);
                } else {
                    fireErrorEvent(response.getStatusCode(),"Failed to change layout name", response.getText());
                }
            }

            @Override
            public void onError(com.google.gwt.http.client.Request request, Throwable throwable) {
                UserLayoutPanel.this.setIsLoading(false);
                fireErrorEvent("Failed to change layout name", throwable.getMessage());
            }
        });
    }

    private void deleteLayout(int layoutId, GeneralJavaScriptObject row) throws RequestException {
        this.setIsLoading(true);
        String url = EsaSkyWebConstants.TAP_USERLAYOUTS_URL + "?layout_id=" + layoutId;
        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.DELETE, url);
        requestBuilder.sendRequest(null, new RequestCallback() {
            @Override
            public void onResponseReceived(com.google.gwt.http.client.Request request, com.google.gwt.http.client.Response response) {
                UserLayoutPanel.this.setIsLoading(false);
                if (response.getStatusCode() == 200) {
                    tabulatorWrapper.deleteRow(row);
                } else {
                    fireErrorEvent(response.getStatusCode(),"Failed to delete layout", response.getText());
                }
            }

            @Override
            public void onError(com.google.gwt.http.client.Request request, Throwable throwable) {
                UserLayoutPanel.this.setIsLoading(false);
                fireErrorEvent("Failed to delete layout", throwable.getMessage());
            }
        });
    }

    private void createLayout() {
        this.setIsLoading(true);
        try {
            String url = EsaSkyWebConstants.TAP_USERLAYOUTS_URL + "?layout_name=new-layout";
            RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST, url);
            requestBuilder.sendRequest(null, new RequestCallback() {
                @Override
                public void onResponseReceived(com.google.gwt.http.client.Request request, com.google.gwt.http.client.Response response) {
                    UserLayoutPanel.this.setIsLoading(false);
                    if (response.getStatusCode() == 200) {
                        GeneralJavaScriptObject obj = GeneralJavaScriptObject.createJsonObject(response.getText());
                        tabulatorWrapper.addRow(obj);
                    } else {
                        fireErrorEvent(response.getStatusCode(), "Failed to create layout", response.getText());
                    }
                }

                @Override
                public void onError(com.google.gwt.http.client.Request request, Throwable throwable) {
                    UserLayoutPanel.this.setIsLoading(false);
                    fireErrorEvent("Failed to create layout", throwable.getMessage());
                }
            });
        } catch (RequestException ex) {
            fireErrorEvent("Failed to create layout", ex);
            this.setIsLoading(false);
        }
    }


    private void saveLayoutValues(int layoutId, GeneralJavaScriptObject[] rows)  {
        this.setIsLoading(true);
        try {
            StringBuilder url = new StringBuilder(EsaSkyWebConstants.TAP_USERLAYOUTS_URL + "?layout_id=" + layoutId);
            for (GeneralJavaScriptObject row : rows) {
                url.append("&").append(row.getProperty("key")).append("=").append(row.getProperty("is_shown"));
            }


            RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.PUT, url.toString());
            requestBuilder.sendRequest(null, new RequestCallback() {
                @Override
                public void onResponseReceived(com.google.gwt.http.client.Request request, com.google.gwt.http.client.Response response) {
                    UserLayoutPanel.this.setIsLoading(false);
                    if (response.getStatusCode() == 200) {
                        loadAllLayouts();
                    } else {
                        fireErrorEvent(response.getStatusCode(), "Failed to save layout", response.getText());
                    }
                }

                @Override
                public void onError(com.google.gwt.http.client.Request request, Throwable throwable) {
                    UserLayoutPanel.this.setIsLoading(false);
                    fireErrorEvent("Failed to save layout", throwable.getMessage());
                }
            });
        } catch (RequestException ex) {
            fireErrorEvent("Failed to save layout", ex);
        }

    }


    private void activateLayout(String layoutId) {
        this.setIsLoading(true);
        String url = EsaSkyWebConstants.TAP_USERLAYOUTS_URL + "?layout_id=" + layoutId;
        JSONUtils.getJSONFromUrl(url, new JSONUtils.IJSONRequestCallback() {
            @Override
            public void onSuccess(String responseText) {
                GeneralJavaScriptObject obj = GeneralJavaScriptObject.createJsonObject(responseText);
                GeneralJavaScriptObject data = obj.getProperty("data");
                GeneralJavaScriptObject metadata = obj.getProperty("columns");

                GeneralJavaScriptObject metaObj = ExtTapUtils.formatExternalTapMetadata(metadata);
                GeneralJavaScriptObject dataObj = ExtTapUtils.formatExternalTapData(data, metaObj);

                activateLayout(layoutId, dataObj);
            }

            @Override
            public void onError(int status, String errorCause) {
                fireErrorEvent(status,"Failed to load layout", errorCause);
            }

            @Override
            public void whenComplete() {
                UserLayoutPanel.this.setIsLoading(false);
            }
        }, true);
    }

    private void activateLayout(String layoutId, GeneralJavaScriptObject data) {
        Modules.setMode(EsaSkyWebConstants.MODULE_MODE_USER, layoutId);
        for (GeneralJavaScriptObject obj : GeneralJavaScriptObject.convertToArray(data)) {
            String key = obj.getStringProperty("key");
            boolean isShown = Boolean.parseBoolean(obj.getStringProperty("is_shown"));
            updateModuleValue(key, isShown);
        }

        MainPresenter.getInstance().updateModuleVisibility();
        tabulatorWrapper.restoreRedraw();
        tabulatorWrapper.redrawAndReinitializeHozVDom();
        UrlUtils.updateURLWithoutReloadingJS(UrlUtils.getUrlForCurrentState());
        resetButton.setEnabled(true);
    }


    private void updateModuleValue(String key, boolean isShown) {
        try {
            Modules.setModule(key, isShown);
            MainPresenter.getInstance().updateModuleVisibility();
        } catch (MapKeyException e) {
            Log.debug("Error updating module", e.getMessage());
        }
    }

    private native GeneralJavaScriptObject createEditLayoutMetadata(UserLayoutPanel panel) /*-{
        var metadata = [];

        var descriptionFormatter = function (cell, formatterParams) {
            var data = cell.getData();
            if (data.key && $wnd.esasky.hasInternationalizationText(data.key)) {
                return $wnd.esasky.getDefaultLanguageText(data.key);
            } else {
                return "";
            }
        };

        metadata.push({
            name: "is_shown",
            field: "is_shown",
            title: "Enabled",
            visible: true,
            editor: false,
            formatter: "tickCross",
            cellEdited: function (cell) {
                var data = cell.getData();
                if (data.key) {
                    panel.@esac.archive.esasky.cl.web.client.login.UserLayoutPanel::updateModuleValue(*)(data.key, data.is_shown);
                }
            }
        });

        metadata.push({
            name: "id",
            title: "id",
            field: "id",
            visible: false
        });

        metadata.push({
            name: "key",
            title: "Setting",
            field: "key",
            visible: true
        });

        metadata.push({
            name: "description",
            title: "Description",
            formatter: descriptionFormatter,
            visible: true
        });


        return metadata;
    }-*/;


    public boolean isActiveLayout(String layoutId) {
        String mode = Modules.getMode();
        if (mode != null && mode.contains(EsaSkyWebConstants.MODULE_MODE_USER)) {
            String[] modeSplit = mode.split("_");
            return modeSplit.length > 1 && Objects.equals(modeSplit[1], layoutId);
        }

        return false;
    }


    private native GeneralJavaScriptObject createAllLayoutsMetadata(UserLayoutPanel panel) /*-{
        var metadata = [];

        var editIcon = function (cell, formatterParams) {
            return "<div class='buttonCell'><img src='images/edit-pen-icon.png' width='20px' height='20px'/></div>";
        };

        var deleteIcon = function (cell, formatterParams) {
            return "<div class='buttonCell'><img src='images/recycle-bin-line-icon.png' width='20px' height='20px'/></div>";
        };

        var activeIcon = function (cell, formatterParams) {
            var data = cell.getData();
            if (data.internal_layouts_oid && panel.@esac.archive.esasky.cl.web.client.login.UserLayoutPanel::isActiveLayout(*)(data.internal_layouts_oid)) {
                return "<div class='buttonCell'><img class='powerButton__active' src='images/power-blue-icon.png' width='20px' height='20px'/></div>";
            } else {
                return "<div class='buttonCell'><img class='powerButton__active' src='images/power-icon.png' width='20px' height='20px'/></div>";
            }
        }


        metadata.push({
            name: "active",
            title: "Activate",
            formatter: activeIcon,
            hozAlign: "center",
            cellClick: function (e, cell) {
                var data = cell.getData();
                if (data.internal_layouts_oid) {
                    if (panel.@esac.archive.esasky.cl.web.client.login.UserLayoutPanel::isActiveLayout(*)(data.internal_layouts_oid)) {
                        panel.@esac.archive.esasky.cl.web.client.login.UserLayoutPanel::onResetButtonClicked()();
                    } else {
                        panel.@esac.archive.esasky.cl.web.client.login.UserLayoutPanel::activateLayout(Ljava/lang/String;)(data.internal_layouts_oid)
                    }
                }
            }
        });


        metadata.push({
            name: "edit",
            title: "Edit",
            formatter: editIcon,
            hozAlign: "center",
            cellClick: function (e, cell) {
                var data = cell.getData();
                if (data.internal_layouts_oid) {
                    panel.@esac.archive.esasky.cl.web.client.login.UserLayoutPanel::loadLayout(*)(data.internal_layouts_oid)
                }
            }
        });

        metadata.push({
            name: "delete",
            title: "Delete",
            formatter: deleteIcon,
            hozAlign: "center",
            cellClick: function (e, cell) {
                var data = cell.getData();
                if (data.internal_layouts_oid) {
                    panel.@esac.archive.esasky.cl.web.client.login.UserLayoutPanel::deleteLayout(*)(data.internal_layouts_oid, cell.getRow())
                }
            },
            disabled: true
        });


        metadata.push({
            name: "internal_layouts_oid",
            title: "internal_layouts_oid",
            field: "internal_layouts_oid",
            visible: false
        });
        metadata.push({
            name: "name",
            title: "Name",
            field: "name",
            visible: true,
            editor: "input",
            cellEdited: function (cell) {
                var data = cell.getData();
                if (data.internal_layouts_oid && data.name) {
                    panel.@esac.archive.esasky.cl.web.client.login.UserLayoutPanel::changeLayoutName(*)(data.internal_layouts_oid, data.name)
                }
            }
        });

        return metadata;
    }-*/;


    private static class TabulatorCallback extends DefaultTabulatorCallback {
        UserLayoutPanel userLayoutPanel;

        public TabulatorCallback() {
            super();

        }

        public void setuserLayoutPanel(UserLayoutPanel userLayoutPanel) {
            this.userLayoutPanel = userLayoutPanel;
        }


        public void onRowClicked(GeneralJavaScriptObject row) {
            updateIsShown(row);
        }

        private void updateIsShown(GeneralJavaScriptObject row) {
            GeneralJavaScriptObject data = row.invokeFunction("getData");
            String key = data.getStringProperty("key");
            boolean isShown = !Boolean.parseBoolean(data.getStringProperty("is_shown"));
            updateRowIsShown(row, isShown);
            userLayoutPanel.updateModuleValue(key, isShown);
        }

        private native void updateRowIsShown(GeneralJavaScriptObject row, boolean isShown) /*-{
            row.update({"is_shown": isShown});
        }-*/;


        @Override
        public void onCreateRowClicked() {
            userLayoutPanel.createLayout();
        }
    }
}
