package esac.archive.esasky.cl.web.client.api;


import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import esac.archive.esasky.cl.web.client.Controller;
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
		openNeutrinoPanel();
		JSONObject data = controller.getRootPresenter().getCtrlTBPresenter().getNeutrinoEventData();
		JSONObject obj = new JSONObject();
		obj.put("data",data);
		sendBackToWidget(obj, null, widget);
	}
	
	public void getAvailableGWEvents(JavaScriptObject widget) {
		openGWPanel();
		JSONArray ids = controller.getRootPresenter().getCtrlTBPresenter().getGWIds();
		JSONObject obj = new JSONObject();
		obj.put("Available_ids", ids);
		sendBackToWidget(obj, null, widget);
	}

	public void getGWEventData(JavaScriptObject widget, String id) {
		openGWPanel();
		JSONObject data;
		try {
			data = controller.getRootPresenter().getCtrlTBPresenter().getGWData(id);
			sendBackToWidget(data, null, widget);
		} catch (IllegalArgumentException e) {
			Log.error(e.toString(), e);
			JSONObject error = new JSONObject();
			error.put(ApiConstants.MESSAGE, new JSONString("Id not available: " + id));
			JSONArray ids = controller.getRootPresenter().getCtrlTBPresenter().getGWIds();
			error.put(ApiConstants.ERROR_AVAILABLE, ids);
			sendBackErrorToWidget(error, widget);
		}
	}
	
	public void getAllGWData(JavaScriptObject widget) {
		openGWPanel();
		JSONObject data = controller.getRootPresenter().getCtrlTBPresenter().getAllGWData();
		JSONObject obj = new JSONObject();
		obj.put("data",data);
		sendBackToWidget(obj, null, widget);
	}
	
	public void showGWEvent(JavaScriptObject widget, String id) {
		try {
			controller.getRootPresenter().getCtrlTBPresenter().showGWEvent(id);
		} catch (IllegalArgumentException e) {
			Log.error(e.toString(), e);
			JSONObject error = new JSONObject();
			error.put(ApiConstants.MESSAGE, new JSONString("Id not available: " + id));
			JSONArray ids = controller.getRootPresenter().getCtrlTBPresenter().getGWIds();
			error.put(ApiConstants.ERROR_AVAILABLE, ids);
			sendBackErrorToWidget(error, widget);
		}
	}
}
