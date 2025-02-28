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



import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JavaScriptObject;

import esac.archive.esasky.cl.web.client.EsaSkyWeb;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.Session;

public class Api extends ApiBase{
	
	private static Api instance = null;
	
	public static Api getInstance() {
			return instance;
	}
	
	public static boolean isInitialised() {
		return instance == null;
	}
	
	public static void init(EsaSkyWeb esaSkyWeb) {
		instance = new Api(esaSkyWeb);
	}
	
	public Api(EsaSkyWeb esaSkyWeb) {
		Log.debug("[Api]");

		this.controller = esaSkyWeb.getController();

		ApiCounts apiCounts = new ApiCounts(controller);
		ApiEvents apiEvents = new ApiEvents(controller);
		ApiExtTap apiExtTap = new ApiExtTap(controller);
		ApiHips apiHips = new ApiHips(controller);
		ApiMoc apiMoc = new ApiMoc(controller);
		ApiModules apiModules = new ApiModules(controller, apiEvents);
		ApiOverlay apiOverlay = new ApiOverlay(controller);
		ApiPanel apiPanel = new ApiPanel(controller);
		ApiPlanning apiPlanning = new ApiPlanning(controller);
		ApiPlot apiPlot = new ApiPlot(controller);
		ApiView apiView = new ApiView(controller);
		ApiImage apiImage = new ApiImage(controller);
		ApiAlerts apiAlerts = new ApiAlerts(controller);
		ApiSearch apiSearch = new ApiSearch(controller);
		ApiSession apiSession = new ApiSession(controller);
		ApiPlayer apiPlayer = new ApiPlayer(controller);
		
		
		ApiMessageParser.init(this, apiCounts, apiEvents, apiExtTap, apiHips, apiMoc, apiModules,
				apiOverlay, apiPanel, apiPlanning, apiPlot, apiView, apiImage, apiAlerts, apiSearch,
				apiSession, apiPlayer);

		Log.debug("[Api] Ready!!");
		
	}
	
	public void displayJPG(String imageURL, String transparency) {
		
		AladinLiteWrapper.getAladinLite().displayJPG(imageURL, transparency, 1.0);
	}
	
	public void noSuchEventError(String event, JavaScriptObject widget) {
		sendBackErrorMsgToWidget(ApiConstants.ERROR_MISSING_EVENT + event, widget);
	}

}
