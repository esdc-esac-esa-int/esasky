package esac.archive.esasky.cl.web.client.api;


import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

import esac.archive.esasky.cl.wcstransform.module.utility.Constants;
import esac.archive.esasky.cl.web.client.Controller;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.planningmenu.PlanObservationPanel;

public class ApiPlanning extends ApiBase{
	
	
	public ApiPlanning(Controller controller) {
		this.controller = controller;
	}
	
	public void clearJwst() {
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_PYESASKY_CLEARJWSTALL, "");
		PlanObservationPanel planObservationPanel = PlanObservationPanel.getInstance();
		planObservationPanel.clearAllAPI();
	}
	
	public void closeJwstPanel() {
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_PYESASKY_CLOSEJWSTPANEL, "");
		PlanObservationPanel planObservationPanel = PlanObservationPanel.getInstance();
		planObservationPanel.hide();
	}
	
	public void openJwstPanel() {
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_PYESASKY_OPENJWSTPANEL, "");
		PlanObservationPanel planObservationPanel = PlanObservationPanel.getInstance();
		planObservationPanel.show();
	}
	
	public void addJwstWithCoordinates(String instrument, String detector,boolean allInstruments, String ra, String dec, String rotation, JavaScriptObject widget) {
		String allInfo = instrument + ";" + detector + ";" + Boolean.toString(allInstruments) + ";" + ra + ";" + dec + ";" + rotation;
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_PYESASKY_ADDJWSTWITHCOORDINATES, allInfo);
		PlanObservationPanel planObservationPanel = PlanObservationPanel.getInstance();
		planObservationPanel.show();
		JSONValue available = planObservationPanel.addInstrumentRowWithCoordinatesAPI(Constants.PlanningMission.JWST.getMissionName(), instrument, detector, allInstruments, ra, dec, rotation);
		if(available != null) {
			JSONObject error = new JSONObject();
			error.put(ApiConstants.ERROR_AVAILABLE, available);
			sendBackErrorToWidget(error, widget);
			return;
		}
		sendBackSuccessToWidget(widget);
	}
	
	public void addJwst(String instrument, String detector, boolean allInstruments, JavaScriptObject widget) {	
		String allInfo = instrument + ";" + detector + ";" + Boolean.toString(allInstruments);
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_PYESASKY_ADDJWST, allInfo);
		PlanObservationPanel planObservationPanel = PlanObservationPanel.getInstance();
		planObservationPanel.show();
		JSONValue available = planObservationPanel.addInstrumentRowAPI(Constants.PlanningMission.JWST.getMissionName(), instrument, detector, allInstruments);
		if(available != null) {
			JSONObject error = new JSONObject();
			error.put(ApiConstants.ERROR_AVAILABLE, available);
			sendBackErrorToWidget(error, widget);
			return;
		}
		sendBackSuccessToWidget(widget);

	}
}
