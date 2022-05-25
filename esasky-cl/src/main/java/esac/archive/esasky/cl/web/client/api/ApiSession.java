package esac.archive.esasky.cl.web.client.api;


import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;

import esac.archive.esasky.cl.web.client.Controller;
import esac.archive.esasky.cl.web.client.utility.Session;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;

public class ApiSession extends ApiBase{
	
	
	public ApiSession(Controller controller) {
		this.controller = controller;
	}
	
	public void saveState(JavaScriptObject widget) {
		Session session = new Session();
		JSONObject sessionObj = session.saveStateAsObj();
		JSONObject obj = new JSONObject();
		obj.put("session", sessionObj);
		sendBackToWidget(obj, null, widget);
	}
	
	public void restoreState(GeneralJavaScriptObject saveObj) {
		Session session = new Session();
		if(saveObj != null) {
			session.restoreState(saveObj);
		}else {
			session.restoreState();
		}
	}
	
}
