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
		} else {
			session.restoreState();
		}
	}
	
	public void login() {
		controller.getRootPresenter().getHeaderPresenter().getView().getUserAreaPresenter().doCasLogin();
	}
	
	public void logout() {
		controller.getRootPresenter().getHeaderPresenter().getView().getUserAreaPresenter().doCasLogout();
	}
	
}
