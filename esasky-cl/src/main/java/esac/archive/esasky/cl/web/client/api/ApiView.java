package esac.archive.esasky.cl.web.client.api;


import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;

import esac.archive.esasky.cl.web.client.Controller;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.CoordinateUtils;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;

public class ApiView extends ApiBase{
	
	
	public ApiView(Controller controller) {
		this.controller = controller;
	}
	

	public void getCenter(String cooFrame, JavaScriptObject widget){
		SkyViewPosition skyViewPosition;
		if(cooFrame == null || "".equals(cooFrame)) {
			cooFrame = AladinLiteWrapper.getInstance().getCooFrame();
		}
		if(cooFrame.equalsIgnoreCase(EsaSkyWebConstants.ALADIN_J2000_COOFRAME)) {
			skyViewPosition = CoordinateUtils.getCenterCoordinateInJ2000();
		}else {
			skyViewPosition = CoordinateUtils.getCenterCoordinateInGalactic();
		}
		
		JSONObject coors = new  JSONObject();
		coors.put(ApiConstants.RA, new JSONNumber(skyViewPosition.getCoordinate().ra));
		coors.put(ApiConstants.DEC, new JSONNumber(skyViewPosition.getCoordinate().dec));
		coors.put(ApiConstants.FOV, new JSONNumber(skyViewPosition.getFov()));
		
		GoogleAnalytics.sendEventWithURL(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_getCenter, "Cooframe: " + cooFrame + " returned: " + coors.toString() );
		sendBackValuesToWidget(coors, widget);
	}
	
	public void goTo(String ra, String dec) {
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_goToRADec, "ra: " + ra + "dec: " + dec);
		AladinLiteWrapper.getInstance().goToObject(ra + " " + dec, false);
	}

	public void goToWithParams(String ra, String dec, double fovDegrees, boolean showTargetPointer, String cooFrame) {
		AladinLiteWrapper.getInstance().goToTarget(ra, dec, fovDegrees, showTargetPointer, cooFrame);
	}

	public void goToTargetName(String targetName) {
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_goToTargetName, targetName);
		AladinLiteWrapper.getInstance().goToObject(targetName, false);
	}

	public void goToTargetNameWithFoV(String targetName, double fovDeg) {
		AladinLiteWrapper.getInstance().goToObject(targetName, false);
		AladinLiteWrapper.getAladinLite().setZoom(fovDeg);
	}

	public void setFoV(double fov) {
		GoogleAnalytics.sendEventWithURL(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_setFoV, Double.toString(fov));
		AladinLiteWrapper.getAladinLite().setZoom(fov);
	}
}
