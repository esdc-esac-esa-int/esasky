package esac.archive.esasky.cl.web.client.api;



import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JavaScriptObject;

import esac.archive.esasky.cl.web.client.EsaSkyWeb;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;

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
		
		
		ApiMessageParser.init(this, apiCounts, apiEvents, apiExtTap, apiHips, apiMoc, apiModules,
				apiOverlay, apiPanel, apiPlanning, apiPlot, apiView);

		Log.debug("[Api] Ready!!");
		
	}
	
	public void displayJPG(String imageURL, String transparency) {
		
		AladinLiteWrapper.getAladinLite().displayJPG(imageURL, transparency, 1.0);
	}
	
	public void noSuchEventError(String event, JavaScriptObject widget) {
		sendBackErrorMsgToWidget(ApiConstants.ERROR_MISSING_EVENT + event, widget);
	}

}
