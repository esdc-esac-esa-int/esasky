package esac.archive.esasky.cl.web.client.api;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

import esac.archive.esasky.cl.web.client.Controller;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;

public abstract class ApiBase {
	protected Controller controller;
	protected static String googleAnalyticsCat = GoogleAnalytics.CAT_Pyesasky;
	
	public void setGoogleAnalyticsCatToPython() {
		ApiBase.googleAnalyticsCat = GoogleAnalytics.CAT_Pyesasky;
	}

	public void setGoogleAnalyticsCatToAPI() {
		ApiBase.googleAnalyticsCat = GoogleAnalytics.CAT_JavaScriptAPI;
	}
	
	
	
	protected void sendInitMessage( JavaScriptObject widget) {
		JSONObject init = new JSONObject();
		init.put(ApiConstants.INITIALISED, JSONBoolean.getInstance(true));
		sendBackToWidget(init, widget);
	}
	
	protected void sendBackToWidget(JSONObject values, JavaScriptObject widget) {
		sendBackToWidget(values, null, null, null, widget);
	}
	
	protected void sendBackEventToWidget(JSONObject event, JavaScriptObject widget) {
		sendBackToWidget(null, null, null, event, widget);
	}
	
	protected void sendBackMessageToWidget(String message, JavaScriptObject widget) {
		JSONObject callbackMessage = new JSONObject();
		callbackMessage.put(ApiConstants.MESSAGE, new JSONString(message));
		sendBackToWidget(null, callbackMessage, null, null, widget);
	}

	protected void sendBackErrorToWidget(JSONObject error, JavaScriptObject widget) {
		sendBackToWidget(null, error, error, null, widget);
	}
	
	protected void sendBackErrorMsgToWidget(String errorMsg, JavaScriptObject widget) {
		JSONObject error = new JSONObject();
		error.put(ApiConstants.MESSAGE, new JSONString(errorMsg));
		sendBackToWidget(null, error, error, null, widget);
	}
	
	protected void sendBackToWidget(JSONObject values, JSONObject extras, JavaScriptObject widget) {
		sendBackToWidget(values, extras, null, null, widget);
	}
	
	
	protected void sendBackToWidget(JSONObject values, JSONObject extras, JSONObject error, JSONObject event, JavaScriptObject widget) {
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
		
		String msgId = ((GeneralJavaScriptObject) widget).getProperty(ApiConstants.DATA).getStringProperty(ApiConstants.MSGID);
		if(msgId != null && !"".equals(msgId)) {
			msg.put(ApiConstants.MSGID, new JSONString(msgId));
		}
		
		sendBackToWidgetJS(msg,widget);
	}

	
	protected native void sendBackToWidgetJS(JSONObject msg, JavaScriptObject widget) /*-{
		// Javascript adds a wrapper object around the values and extras which we remove
		msg = Object.values(msg)[0];
		widget.source.postMessage(msg, widget.origin);
	}-*/;
	
}
