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

package esac.archive.esasky.cl.web.client.presenter.login;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.*;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.Window;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.*;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.login.UserDetails;
import esac.archive.esasky.cl.web.client.status.GUISessionStatus;
import esac.archive.esasky.cl.web.client.utility.*;
import esac.archive.esasky.cl.web.client.view.common.ConfirmationPopupPanel;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;

import java.util.Date;
import java.util.UUID;

import static esac.archive.esasky.cl.web.client.utility.GoogleAnalytics.CAT_USERAREA;

public class UserAreaPresenter {

    public interface View {
        default void addCasRegisterClickHandler(ClickHandler handler) {}

        default void addCasLoginClickHandler(ClickHandler handler) {}

        default void addCasLogoutClickHandler(ClickHandler handler) {}

        default void togglePanel() {
            if (isShowing()) {
                hide();
            } else {
                show();
            }
        }

        void show();

        void hide();

        void hideAll();

        boolean isShowing();

        HandlerRegistration addErrorHandler(ErrorEventHandler handler);

    }

    public interface Resources extends ClientBundle {
        @Source("userAreaPresenter.css")
        @CssResource.NotStrict
        CssResource style();
    }

    private final View unAuthenticatedView;
    private final View authenticatedView;

    private Date lastSessionSave = new Date();

    public UserAreaPresenter(View unAuthenticatedView, View authenticatedView) {
        this.unAuthenticatedView = unAuthenticatedView;
        this.authenticatedView = authenticatedView;

        authenticatedView.addErrorHandler(this::showErrorMessage);
        unAuthenticatedView.addErrorHandler(this::showErrorMessage);

        CommonEventBus.getEventBus().addHandler(UserIdleEvent.TYPE, event -> {
            if (event.isUserIdle() && GUISessionStatus.isUserAuthenticated()) {
                saveCurrentSession();
            }
        });

        CommonEventBus.getEventBus().addHandler(ProgressIndicatorPopEvent.TYPE, event -> {
            Date now = new Date();
            long elapsedTime = now.getTime() - lastSessionSave.getTime();

            if (GUISessionStatus.isUserAuthenticated() && GUISessionStatus.isUserActive() && elapsedTime >= 30*1000) {
                saveCurrentSession();
                lastSessionSave = now;
            }
        });

        bind();
        init();
    }

    private void bind() {
        unAuthenticatedView.addCasLoginClickHandler(event -> {
            GoogleAnalytics.sendEvent(CAT_USERAREA, GoogleAnalytics.ACT_HEADER_LOGIN, "");
            doCasLogin();
        });
        unAuthenticatedView.addCasRegisterClickHandler(event -> {
            GoogleAnalytics.sendEvent(CAT_USERAREA, GoogleAnalytics.ACT_HEADER_REGISTER, "");
            doRegisterNewCasUser();
        });

        authenticatedView.addCasLogoutClickHandler(event -> {
            GoogleAnalytics.sendEvent(CAT_USERAREA, GoogleAnalytics.ACT_HEADER_LOGOUT, "");
            doCasLogout();
        });
    }

    private void init() {
        checkCasLoginStatus();
    }

    private void doRegisterNewCasUser() {
        Window.open("https://www.cosmos.esa.int/web/esasky/registration", "_blank", "");
    }

    public void doCasLogin() {
        String casLoginUrl = EsaSkyWebConstants.TAP_CONTEXT + "/CasLogin?redirect=" + URL.encode(Window.Location.getHref());
        Log.info("CAS login URL: " + casLoginUrl);
        Window.Location.assign(casLoginUrl);
    }

    public void doCasLogout() {
        GUISessionStatus.setUserDetails(null);
        refreshView();

        String logoutUrl = EsaSkyWebConstants.TAP_CONTEXT + "/logout";

        RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, logoutUrl);
        builder.setHeader("If-Modified-Since", "01 Jan 1970 00:00:00 GMT");
        try {
            Log.info("Sending logout request...");
            builder.sendRequest(null, new RequestCallback() {
                public void onResponseReceived(Request request, Response response) {
                    Log.debug("Logout HTTP response = " + response.getStatusCode());
                    if (response.getStatusCode() == 200) {
                        String casLogoutUrl = EsaSkyWebConstants.TAP_CONTEXT + "/CasLogout?redirect=" + URL.encode(Window.Location.getHref());
                        Window.Location.assign(casLogoutUrl);
                    } else {
                        String msg = "Logout STATUS CODE [" + response.getStatusCode()
                                + " unknown";
                        Log.info(msg);
                        showErrorMessage("The server encountered an error", response.getText());
                    }
                }

                public void onError(Request request, Throwable throwable) {
                    Log.error("logout error " + throwable.getMessage());
                    showErrorMessage("The server encountered an error", throwable.getMessage());
                }
            });

        } catch (RequestException e) {
            Log.error(e.getMessage(), e);
            showErrorMessage("The server encountered an error", e.getMessage());
        }
    }

    public void togglePanel() {
        if (isShowing()) {
            hide();
        } else {
            show();
        }
    }

    public void show() {
        if (GUISessionStatus.isUserAuthenticated()) {
            authenticatedView.show();
            unAuthenticatedView.hide();
        } else {
            unAuthenticatedView.show();
            authenticatedView.hide();
//            authenticatedView.show();
//            unAuthenticatedView.hide();
        }
    }

    public void hide() {
        authenticatedView.hide();
        unAuthenticatedView.hide();
    }

    public boolean isShowing() {
        return authenticatedView.isShowing() || unAuthenticatedView.isShowing();
    }

    private void checkCasLoginStatus() {
        String encodedCheckUserIsLoggedInURL = EsaSkyWebConstants.TAP_CONTEXT + "/LoginStatus";

        Log.info(encodedCheckUserIsLoggedInURL);

        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, encodedCheckUserIsLoggedInURL);
        builder.setHeader("If-Modified-Since", "01 Jan 1970 00:00:00 GMT");
        try {
            Log.info("Sending user 'is user logged in' check request...");
            builder.sendRequest(null, new RequestCallback() {
                public void onResponseReceived(Request request, Response response) {
                    Log.debug("LoginStatus HTTP response = " + response.getStatusCode());
                    if (response.getStatusCode() == 200) {
                        String responseText = response.getText();

                        if (responseText == null || responseText.trim().isEmpty()) {
                            Log.info("User is not logged in");
                            GUISessionStatus.setUserDetails(null);
                        } else {
                            String text = response.getText();
                            UserDetails userDetails = UserDetails.parseFromXml(text);
                            GUISessionStatus.setUserDetails(userDetails);
                            restoreCurrentSession();
                        }

                        refreshView();

                    } else {
                        Log.info("No users logged in from previous session");
                    }
                }

                public void onError(Request request, Throwable exception) {
                    Log.info("'is user logged in' check error");
                }
            });
        } catch (RequestException e) {
            Log.error(e.getMessage(), e);
        }
    }

    private void restoreCurrentSession() {
        if (GUISessionStatus.isUserAuthenticated()) {
            JSONUtils.getJSONFromUrl(EsaSkyWebConstants.TAP_USERSESSIONS_URL + "?auto_save=true", new JSONUtils.IJSONRequestCallback() {
                @Override
                public void onSuccess(String responseText) {
                    GeneralJavaScriptObject obj = GeneralJavaScriptObject.createJsonObject(responseText);
                    GeneralJavaScriptObject data = obj.getProperty("data");
                    GeneralJavaScriptObject metadata = obj.getProperty("columns");

                    GeneralJavaScriptObject metaObj = ExtTapUtils.formatExternalTapMetadata(metadata);
                    GeneralJavaScriptObject dataObj = ExtTapUtils.formatExternalTapData(data, metaObj);
                    String sessionJson = getFirstSession(dataObj);

                    if (sessionJson != null) {
                        ConfirmationPopupPanel restoreSessionDialog = new ConfirmationPopupPanel(CAT_USERAREA,
                                TextMgr.getInstance().getText("userArea__userAreaPresenter_restoreSession_title"),
                                TextMgr.getInstance().getText("userArea__userAreaPresenter_restoreSession_body"),
                                TextMgr.getInstance().getText("userArea__userAreaPresenter_restoreSession_help"));
                        restoreSessionDialog.addDialogEventHandler(action -> {
                            if (action.getAction() == DialogActionEvent.DialogAction.YES) {
                                new Session().restoreState(sessionJson);
                            }
                        });
                        restoreSessionDialog.show();

                    }
                }

                @Override
                public void onError(String errorCause) {

                }

                @Override
                public void whenComplete() {

                }
            });
        }
    }

    private native String getFirstSession(GeneralJavaScriptObject data) /*-{
        if(data.length > 0) {
            return data[0].json;
        } else {
            return null;
        }
    }-*/;

    private void refreshView() {
        if (isShowing()) {
            if (GUISessionStatus.isUserAuthenticated()) {
                authenticatedView.show();
                unAuthenticatedView.hide();
            } else {
                authenticatedView.hide();
                unAuthenticatedView.show();
            }
        }
    }

    private void saveCurrentSession() {
        String url = EsaSkyWebConstants.TAP_USERSESSIONS_URL + "?session_name=session-" + DateTimeFormat.getFormat("yyyyMMdd_HHmmss").format(new Date()) + "&auto_save=true";
        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.PUT, url);
        requestBuilder.setHeader("Content-Type", "application/json");

        try {
            String sessionStr = new Session().saveStateAsObj().toString();
            requestBuilder.sendRequest(sessionStr, new RequestCallback() {
                @Override
                public void onResponseReceived(com.google.gwt.http.client.Request request, com.google.gwt.http.client.Response response) {
                    Log.debug("Successfully saved current session for user: " + GUISessionStatus.getUserDetails().getId());
                }

                @Override
                public void onError(com.google.gwt.http.client.Request request, Throwable exception) {
                    Log.warn("Failed to save current session", exception);
                }
            });
        } catch (Exception ex) {
            if (GUISessionStatus.isUserAuthenticated()) {
                Log.warn("Failed to save current session for user: " + GUISessionStatus.getUserDetails().getId());
            } else {
                Log.error("Trying to save current session for unauthenticated user!");
            }

        }

    }

    private void showErrorMessage(ErrorEvent event) {
        if (event.getStatus() == 401) {
            checkCasLoginStatus();
            authenticatedView.hideAll();
            showErrorMessage("Unauthorized. Your session might have expired, please log in again", "");
        } else {
            showErrorMessage(event.getMessage(), event.getDetails());
        }
    }

    private void showErrorMessage(String message, String details) {
        String title = "Error";
        String body = message;
        body += "<br><br><details style=\"overflow: scroll; max-height: 400px;\">" + details + "</details>";
        DisplayUtils.showMessageDialogBox(body, title, UUID.randomUUID().toString(), CAT_USERAREA);
    }
}