package esac.archive.esasky.cl.web.client.api;


import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;

import esac.archive.esasky.cl.web.client.Controller;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;

public class ApiAlerts extends ApiBase{
	
	
	public ApiAlerts(Controller controller) {
		this.controller = controller;
	}
	
	public void openAlertPanel() {
		controller.getRootPresenter().getCtrlTBPresenter().openGWPanel();
	}
	
	public void closeAlertPanel() {
		controller.getRootPresenter().getCtrlTBPresenter().closeGWPanel();
	}

	public void getAvailableGWEvents(JavaScriptObject widget) {
		JSONArray ids = controller.getRootPresenter().getCtrlTBPresenter().getGWIds();
		JSONObject obj = new JSONObject();
		obj.put("Available_ids", ids);
		sendBackToWidget(obj, null, widget);
	}

	public void getGWEventData(JavaScriptObject widget, String id) {
		GeneralJavaScriptObject data;
		try {
			data = controller.getRootPresenter().getCtrlTBPresenter().getGWData(id);
			JSONObject obj = (JSONObject) JSONParser.parseStrict(data.toJSONString());
			sendBackToWidget(obj, null, widget);
		} catch (IllegalArgumentException e) {
			JSONObject error = new JSONObject();
			error.put(ApiConstants.MESSAGE, new JSONString("Id not available: " + id));
			JSONArray ids = controller.getRootPresenter().getCtrlTBPresenter().getGWIds();
			error.put(ApiConstants.ERROR_AVAILABLE, ids);
			sendBackErrorToWidget(error, widget);
		}
	}
	
	public void getAllGWData(JavaScriptObject widget) {
		GeneralJavaScriptObject data = controller.getRootPresenter().getCtrlTBPresenter().getAllGWData();
		JSONArray arr =  (JSONArray) JSONParser.parseStrict(data.toJSONString());
		JSONObject obj = new JSONObject();
		obj.put("data",arr);
		sendBackToWidget(obj, null, widget);
	}
	
	public void showGWEvent(JavaScriptObject widget, String id) {
		try {
			controller.getRootPresenter().getCtrlTBPresenter().showGWEvent(id);
		} catch (IllegalArgumentException e) {
			JSONObject error = new JSONObject();
			error.put(ApiConstants.MESSAGE, new JSONString("Id not available: " + id));
			JSONArray ids = controller.getRootPresenter().getCtrlTBPresenter().getGWIds();
			error.put(ApiConstants.ERROR_AVAILABLE, ids);
			sendBackErrorToWidget(error, widget);
		}
	}
}
