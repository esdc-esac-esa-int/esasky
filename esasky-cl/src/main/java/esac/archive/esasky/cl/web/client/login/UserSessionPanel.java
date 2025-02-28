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

package esac.archive.esasky.cl.web.client.login;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.utility.*;
import esac.archive.esasky.cl.web.client.view.common.TabulatorPopupSearchPanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.DefaultTabulatorCallback;
import esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorSettings;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;

public class UserSessionPanel extends TabulatorPopupSearchPanel {
    private final Session session;
    private static final TabulatorCallback tabulatorCallback = new TabulatorCallback();

    public UserSessionPanel() {
        super(GoogleAnalytics.CAT_USERAREA, true, TextMgr.getInstance().getText("userArea__userSessionPanel_title"), TextMgr.getInstance().getText("userArea__userSessionPanel_help"), tabulatorCallback, getTabulatorSettings());
        tabulatorCallback.setUserSessionPanel(this);
        session = new Session();

        this.setSearchPlaceholder(TextMgr.getInstance().getText("userArea__userSessionPanel_searchSessions"));
        this.setSearchColumns("name");
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        loadSessions();
    }


    public void loadSessions() {
        this.setIsLoading(true);
        JSONUtils.getJSONFromUrl(EsaSkyWebConstants.TAP_USERSESSIONS_URL, new JSONUtils.IJSONRequestCallback() {
            @Override
            public void onSuccess(String responseText) {
                GeneralJavaScriptObject obj = GeneralJavaScriptObject.createJsonObject(responseText);
                GeneralJavaScriptObject data = obj.getProperty("data");
                GeneralJavaScriptObject metadata = obj.getProperty("columns");

                GeneralJavaScriptObject metaObj = ExtTapUtils.formatExternalTapMetadata(metadata);
                GeneralJavaScriptObject dataObj = ExtTapUtils.formatExternalTapData(data, metaObj);

                tabulatorWrapper.insertData(dataObj, createSessionsTableMetadata(UserSessionPanel.this));
                tabulatorWrapper.restoreRedraw();
                tabulatorWrapper.redrawAndReinitializeHozVDom();


            }

            @Override
            public void onError(int status, String errorCause) {
                fireErrorEvent(status,"Failed to load sessions", errorCause);
            }

            @Override
            public void whenComplete() {
                UserSessionPanel.this.setIsLoading(false);
            }
        }, true);
    }


    private native GeneralJavaScriptObject createSessionsTableMetadata(UserSessionPanel panel) /*-{
        var metadata = [];


        var deleteIcon = function (cell, formatterParams) {
            return "<div class='buttonCell'><img src='images/recycle-bin-line-icon.png' width='20px' height='20px'/></div>";
        };

        var loadIcon = function (cell, formatterParams) {
            return "<div class='buttonCell'><img src='images/load-session-icon.png' width='20px' height='20px'/></div>";
        }


        metadata.push({
            name: "active",
            title: "Restore",
            formatter: loadIcon,
            hozAlign: "center",
            cellClick: function (e, cell) {
                var data = cell.getData();
                if (data.internal_sessions_oid) {
                    panel.@esac.archive.esasky.cl.web.client.login.UserSessionPanel::activateSession(*)(data.json)
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
                if (data.internal_sessions_oid) {
                    panel.@esac.archive.esasky.cl.web.client.login.UserSessionPanel::deleteSession(*)(data.internal_sessions_oid, cell.getRow())
                }
            }
        });


        metadata.push({
            name: "internal_sessions_oid",
            title: "internal_sessions_oid",
            field: "internal_sessions_oid",
            visible: false
        });

        metadata.push({
            name: "json",
            title: "json",
            field: "json",
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
                if (data.internal_sessions_oid && data.name) {
                    panel.@esac.archive.esasky.cl.web.client.login.UserSessionPanel::changeSessionName(*)(data.internal_sessions_oid, data.name)
                }
            }
        });

        return metadata;
    }-*/;


    private void createSession() {
        try {
            this.setIsLoading(true);
            String url = EsaSkyWebConstants.TAP_USERSESSIONS_URL + "?session_name=new-session";
            RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST, url);
            requestBuilder.setHeader("Content-Type", "application/json");
            requestBuilder.sendRequest(session.saveStateAsObj().toString(), new RequestCallback() {
                @Override
                public void onResponseReceived(com.google.gwt.http.client.Request request, com.google.gwt.http.client.Response response) {
                    UserSessionPanel.this.setIsLoading(false);
                    if (response.getStatusCode() == 200) {
                        GeneralJavaScriptObject obj = GeneralJavaScriptObject.createJsonObject(response.getText());
                        tabulatorWrapper.addRow(obj);
                    } else {
                        fireErrorEvent(response.getStatusCode(),"Error creating session", response.getText());
                    }
                }

                @Override
                public void onError(com.google.gwt.http.client.Request request, Throwable exception) {
                    UserSessionPanel.this.setIsLoading(false);
                    fireErrorEvent("Error creating session", exception.getMessage());
                }
            });
        } catch (Exception ex) {
            this.setIsLoading(false);
            fireErrorEvent("Error creating session", ex);
        }


    }

    private void changeSessionName(int sessionId, String newName) {
        try {
            this.setIsLoading(true);
            String url = EsaSkyWebConstants.TAP_USERSESSIONS_URL + "?session_id=" + sessionId + "&session_name=" + newName;
            RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.PUT, url);
            requestBuilder.sendRequest(null, new RequestCallback() {
                @Override
                public void onResponseReceived(com.google.gwt.http.client.Request request, com.google.gwt.http.client.Response response) {
                    if (response.getStatusCode() != 200) {
                        fireErrorEvent(response.getStatusCode(),"Error changing session name", response.getText());
                    }

                    UserSessionPanel.this.setIsLoading(false);
                }

                @Override
                public void onError(com.google.gwt.http.client.Request request, Throwable exception) {
                    UserSessionPanel.this.setIsLoading(false);
                    fireErrorEvent("Error changing session name", exception.getMessage());
                }
            });
        } catch (Exception ex) {
            this.setIsLoading(false);
            fireErrorEvent("Error changing session name", ex);
        }

    }


    private void deleteSession(int sessionId, GeneralJavaScriptObject row) {
        try {
            this.setIsLoading(true);
            String url = EsaSkyWebConstants.TAP_USERSESSIONS_URL + "?session_id=" + sessionId;
            RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.DELETE, url);
            requestBuilder.sendRequest(null, new RequestCallback() {
                @Override
                public void onResponseReceived(com.google.gwt.http.client.Request request, com.google.gwt.http.client.Response response) {
                    UserSessionPanel.this.setIsLoading(false);
                    if (response.getStatusCode() == 200) {
                        tabulatorWrapper.deleteRow(row);
                    } else {
                        fireErrorEvent(response.getStatusCode(),"Failed to delete session", response.getText());
                    }
                }

                @Override
                public void onError(com.google.gwt.http.client.Request request, Throwable exception) {
                    UserSessionPanel.this.setIsLoading(false);
                    fireErrorEvent("Failed to delete session", exception.getMessage());
                }
            });
        } catch (Exception ex) {
            this.setIsLoading(false);
            fireErrorEvent("Failed to delete session", ex);
        }
    }

    private void activateSession(String json) {
        session.restoreState(json);
    }

    private static TabulatorSettings getTabulatorSettings() {
        TabulatorSettings settings = new TabulatorSettings();
        settings.setAddAdqlColumn(false);
        settings.setAddMetadataColumn(false);
        settings.setIsDownloadable(false);
        settings.setSelectable(1);
        settings.setAddObscoreTableColumn(false);
        settings.setAddOpenTableColumn(false);
        settings.setCustomMetadata(true);
        settings.setShowCreateRowButton(true, TextMgr.getInstance().getText("userArea__userSessionPanel_createSession"));
        return settings;

    }

    private static class TabulatorCallback extends DefaultTabulatorCallback {
        UserSessionPanel userSessionPanel;

        public TabulatorCallback() {
            super();

        }

        public void setUserSessionPanel(UserSessionPanel userSessionPanel) {
            this.userSessionPanel = userSessionPanel;
        }

        @Override
        public void onCreateRowClicked() {
            userSessionPanel.createSession();
        }
    }

}
