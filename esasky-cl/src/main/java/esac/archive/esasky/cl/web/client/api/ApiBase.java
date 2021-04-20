package esac.archive.esasky.cl.web.client.api;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

import esac.archive.esasky.cl.web.client.Controller;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;

public abstract class ApiBase {
	protected Controller controller;
	protected static String googleAnalyticsCat = GoogleAnalytics.CAT_Pyesasky;
	
	public static void setGoogleAnalyticsCatToPython() {
		ApiBase.googleAnalyticsCat = GoogleAnalytics.CAT_Pyesasky;
	}

	public static void setGoogleAnalyticsCatToAPI() {
		ApiBase.googleAnalyticsCat = GoogleAnalytics.CAT_JavaScriptAPI;
	}
	
	
	
	protected void sendInitMessage( JavaScriptObject widget) {
		
		
		JSONObject init = new JSONObject();
		// New intialised response
		init.put(ApiConstants.INITIALISED, JSONBoolean.getInstance(true));
		//Kept for backward compatibility
		init.put("Text", new JSONString("Initialised"));
		sendBackToWidget(init, widget);
		
	}
	
	protected void sendBackValuesToWidget(JSONObject values, JavaScriptObject widget) {
		sendBackToWidget(values, null, null, null, null, widget);
	}
	
	protected void sendBackSingleValueToWidget(JSONValue value, JavaScriptObject widget) {
		JSONObject msg = new JSONObject();
		msg.put(ApiConstants.VALUES, value);
		sendBackToWidget(msg, widget);
	}
	
	protected void sendBackEventToWidget(JSONObject event, JavaScriptObject widget) {
		sendBackToWidget(event, null, null, event, null,  widget);
	}
	
	protected void sendBackMessageToWidget(String message, JavaScriptObject widget) {
		JSONObject callbackMessage = new JSONObject();
		callbackMessage.put(ApiConstants.MESSAGE, new JSONString(message));
		sendBackToWidget(null, callbackMessage, null, null, null, widget);
	}

	protected void sendBackErrorToWidget(JSONObject error, JavaScriptObject widget) {
		sendBackToWidget(null, error, error, null, null, widget);
	}
	
	protected void sendBackSuccessToWidget(JavaScriptObject widget) {
		sendBackToWidget(null, null, null, null, new JSONObject(), widget);
	}
	
	protected void sendBackErrorMsgToWidget(String errorMsg, JavaScriptObject widget) {
		JSONObject error = new JSONObject();
		error.put(ApiConstants.MESSAGE, new JSONString(errorMsg));
		sendBackToWidget(null, error, error, null, null, widget);
	}
	
	protected void sendBackToWidget(JSONObject values, JSONObject extras, JavaScriptObject widget) {
		sendBackToWidget(values, extras, null, null, null, widget);
	}
	
	
	protected void sendBackToWidget(JSONObject values, JSONObject extras, JSONObject error, JSONObject event, JSONObject success, JavaScriptObject widget) {
		JSONObject msg = new JSONObject();

		if(values != null){
			msg.put(ApiConstants.VALUES, values);
		}
		
		if(extras != null){
			msg.put(ApiConstants.EXTRAS, extras);
		}
		
		if(error != null){
			msg.put(ApiConstants.ERROR, error);
		}
		
		if(event != null){
			msg.put(ApiConstants.EVENT, event);
		}
		
		if(success != null){
			msg.put(ApiConstants.SUCCESS, success);
		}
		
		sendBackToWidget(msg, widget);
	}
	
	protected void sendBackToWidget(JSONObject msg, JavaScriptObject widget) {
		String msgId = ((GeneralJavaScriptObject) widget).getProperty(ApiConstants.DATA).getStringProperty(ApiConstants.MSGID);
		if(msgId != null && !"".equals(msgId)) {
			msg.put(ApiConstants.MSGID, new JSONString(msgId));
		}
		
		sendBackToWidgetJS(msg, widget);
	}

	
	protected native void sendBackToWidgetJS(JSONObject msg, JavaScriptObject widget) /*-{
		// Javascript adds a wrapper object around the values and extras which we remove
		msg = Object.values(msg)[0];
		widget.source.postMessage(msg, widget.origin);
	}-*/;
	
}
