package esac.archive.esasky.cl.web.client.api;


import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

import esac.archive.esasky.cl.web.client.Controller;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.planningmenu.PlanObservationPanel;

public class ApiPlanning extends ApiBase{
	
	
	public ApiPlanning(Controller controller) {
		this.controller = controller;
	}
	
	public void clearJwst() {
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_clearJwstAll, "");
		PlanObservationPanel planObservationPanel = PlanObservationPanel.getInstance();
		planObservationPanel.clearAllAPI();
	}
	
	public void closeJwstPanel() {
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_closeJwstPanel, "");
		PlanObservationPanel planObservationPanel = PlanObservationPanel.getInstance();
		planObservationPanel.hide();
	}
	
	public void openJwstPanel() {
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_openJwstPanel, "");
		PlanObservationPanel planObservationPanel = PlanObservationPanel.getInstance();
		planObservationPanel.show();
	}
	
	public void addJwstWithCoordinates(String instrument, String detector,boolean allInstruments, String ra, String dec, String rotation, JavaScriptObject widget) {
		String allInfo = instrument + ";" + detector + ";" + Boolean.toString(allInstruments) + ";" + ra + ";" + dec + ";" + rotation;
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_addJwstWithCoordinates, allInfo);
		PlanObservationPanel planObservationPanel = PlanObservationPanel.getInstance();
		planObservationPanel.show();
		JSONValue available = planObservationPanel.addInstrumentRowWithCoordinatesAPI(instrument, detector, allInstruments, ra, dec, rotation);
		if(available != null) {
			JSONObject error = new JSONObject();
			error.put(ApiConstants.ERROR_AVAILABLE, available);
			sendBackErrorToWidget(error, widget);
		}
	}
	
	public void addJwst(String instrument, String detector, boolean allInstruments, JavaScriptObject widget) {	
		String allInfo = instrument + ";" + detector + ";" + Boolean.toString(allInstruments);
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_addJwst, allInfo);
		PlanObservationPanel planObservationPanel = PlanObservationPanel.getInstance();
		planObservationPanel.show();
		JSONValue available = planObservationPanel.addInstrumentRowAPI(instrument, detector, allInstruments);
		if(available != null) {
			JSONObject error = new JSONObject();
			error.put(ApiConstants.ERROR_AVAILABLE, available);
			sendBackErrorToWidget(error, widget);
		}
	}
}
