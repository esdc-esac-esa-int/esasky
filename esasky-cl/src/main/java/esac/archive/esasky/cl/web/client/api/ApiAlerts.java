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

package esac.archive.esasky.cl.web.client.api;


import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import esac.archive.esasky.cl.web.client.Controller;
import esac.archive.esasky.cl.web.client.callback.Promise;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.GwPanel;

public class ApiAlerts extends ApiBase{
	
	
	public ApiAlerts(Controller controller) {
		this.controller = controller;
	}
	
	public void openGWPanel() {
		controller.getRootPresenter().getCtrlTBPresenter().openGWPanel(GwPanel.TabIndex.GW.ordinal());
	}
	
	public void openNeutrinoPanel() {
		controller.getRootPresenter().getCtrlTBPresenter().openGWPanel(GwPanel.TabIndex.NEUTRINO.ordinal());
	}
	
	public void closeAlertPanel() {
		controller.getRootPresenter().getCtrlTBPresenter().closeGWPanel();
	}

	public void minimiseAlertPanel() {
		controller.getRootPresenter().getCtrlTBPresenter().minimiseGWPanel();
	}

	public void getNeutrinoEventData(JavaScriptObject widget) {
		controller.getRootPresenter().getCtrlTBPresenter().getNeutrinoEventData(new Promise<JSONObject>() {
			@Override
			public void success(JSONObject data) {
				JSONObject obj = new JSONObject();
				obj.put("data",data);
				sendBackToWidget(obj, null, widget);
			}
		});

	}
	
	public void getAvailableGWEvents(JavaScriptObject widget) {
		controller.getRootPresenter().getCtrlTBPresenter().getGWIds(new Promise<JSONArray>() {
			@Override
			protected void success(JSONArray ids) {
				JSONObject obj = new JSONObject();
				obj.put("Available_ids", ids);
				sendBackToWidget(obj, null, widget);
			}
		});

	}

	public void getGWEventData(JavaScriptObject widget, String id) {
		controller.getRootPresenter().getCtrlTBPresenter().getGWData(id,
				new Promise<JSONObject>() {
					@Override
					public void success(JSONObject data) {
						sendBackToWidget(data, null, widget);
					}

					@Override
					public void failure() {
						JSONObject error = new JSONObject();
						error.put(ApiConstants.MESSAGE, new JSONString("Id not available: " + id));
						controller.getRootPresenter().getCtrlTBPresenter().getGWIds(new Promise<JSONArray>() {
							@Override
							protected void success(JSONArray ids) {
								error.put(ApiConstants.ERROR_AVAILABLE, ids);
								sendBackErrorToWidget(error, widget);
							}
						});
					}
				});

	}
	
	public void getAllGWData(JavaScriptObject widget) {
		controller.getRootPresenter().getCtrlTBPresenter().getAllGWData(new Promise<JSONObject>() {
			@Override
			protected void success(JSONObject data) {
				JSONObject obj = new JSONObject();
				obj.put("data",data);
				sendBackToWidget(obj, null, widget);
			}
		});

	}

	public void showGWEvent(JavaScriptObject widget, String id) {
		controller.getRootPresenter().getCtrlTBPresenter().showGWEvent(id, new Promise<Boolean>() {
			@Override
			protected void success(Boolean data) {
				// No need to notify widget
			}

			@Override
			protected void failure() {
				JSONObject error = new JSONObject();
				error.put(ApiConstants.MESSAGE, new JSONString("Id not available: " + id));
				controller.getRootPresenter().getCtrlTBPresenter().getGWIds(new Promise<JSONArray>() {
					@Override
					protected void success(JSONArray ids) {
						error.put(ApiConstants.ERROR_AVAILABLE, ids);
					}

					@Override
					protected void whenComplete() {
						sendBackErrorToWidget(error, widget);
					}
				});
			}
		});
	}
}
