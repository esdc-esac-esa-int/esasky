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

	public void getNeutrinoEventData(JavaScriptObject widget) {
		controller.getRootPresenter().getCtrlTBPresenter().getNeutrinoEventData(new Promise<JSONObject>() {
			@Override
			public void success(JSONObject data) {
				JSONObject obj = new JSONObject();
				obj.put("data",data);
				sendBackToWidget(obj, null, widget);
			}
		});

		openNeutrinoPanel();
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

		openGWPanel();
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

		openGWPanel();
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

		openGWPanel();
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
