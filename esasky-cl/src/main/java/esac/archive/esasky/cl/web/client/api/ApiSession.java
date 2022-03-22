package esac.archive.esasky.cl.web.client.api;


import esac.archive.esasky.cl.web.client.Controller;
import esac.archive.esasky.cl.web.client.utility.Session;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;

public class ApiSession extends ApiBase{
	
	
	public ApiSession(Controller controller) {
		this.controller = controller;
	}
	
	public void saveState() {
		Session session = new Session();
		session.saveState();
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
