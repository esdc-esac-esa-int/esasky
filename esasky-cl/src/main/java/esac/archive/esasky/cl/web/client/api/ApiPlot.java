package esac.archive.esasky.cl.web.client.api;


import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

import esac.archive.esasky.cl.web.client.Controller;
import esac.archive.esasky.cl.web.client.model.DescriptorCountAdapter;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.ifcs.model.coordinatesutils.Coordinate;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;

public class ApiPlot extends ApiBase{

	public ApiPlot(Controller controller) {
		this.controller = controller;
	}

	
	public void plotObservations(String missionId, JavaScriptObject widget) {
		GoogleAnalytics.sendEventWithURL(googleAnalyticsCat, GoogleAnalytics.ACT_PYESASKY_PLOTOBSERVATIONS, missionId);
		DescriptorCountAdapter descriptors = controller.getRootPresenter().getDescriptorRepository().getDescriptorCountAdapter(EsaSkyWebConstants.CATEGORY_OBSERVATIONS);
		CommonTapDescriptor currObs  = descriptors.getDescriptorByMission(missionId);

		if(currObs != null ) {
			controller.getRootPresenter().getRelatedMetadataWithoutMOC(currObs);
			sendBackMessageToWidget("Image observations from missionId: " + missionId + " displayed in the ESASky", widget);
		}
		else {
			JSONObject callbackMessage = new JSONObject();
			callbackMessage.put("message", new JSONString("Unknown mission: " + missionId + "\n Check getObservationsCount() for available mission names"));
			sendBackToWidget(null, callbackMessage, widget);
		}
	}
	
	public void coneSearchObservations(String missionId, double ra, double dec, double radius, JavaScriptObject widget) {
		GoogleAnalytics.sendEventWithURL(googleAnalyticsCat, GoogleAnalytics.ACT_PYESASKY_PLOTOBSERVATIONS, missionId);
		DescriptorCountAdapter descriptors = controller.getRootPresenter().getDescriptorRepository().getDescriptorCountAdapter(EsaSkyWebConstants.CATEGORY_OBSERVATIONS);
		CommonTapDescriptor currObs  = descriptors.getDescriptorByMission(missionId);

		if(currObs != null ) {
			SkyViewPosition conePos = new SkyViewPosition(new Coordinate(ra, dec), 2 * radius);
			controller.getRootPresenter().coneSearch(currObs, conePos);
			sendBackMessageToWidget("Image observations from missionId: " + missionId + " displayed in the ESASky", widget);
		}
		else {
			JSONObject callbackMessage = new JSONObject();
			callbackMessage.put("message", new JSONString("Unknown mission: " + missionId + "\n Check getObservationsCount() for available mission names"));
			sendBackToWidget(null, callbackMessage, widget);
		}
	}
	
	public void plotCatalogues(String missionId, JavaScriptObject widget) {
		GoogleAnalytics.sendEventWithURL(googleAnalyticsCat, GoogleAnalytics.ACT_PYESASKY_PLOTCATALOGUES, missionId);
		DescriptorCountAdapter descriptors = controller.getRootPresenter().getDescriptorRepository().getDescriptorCountAdapter(EsaSkyWebConstants.CATEGORY_CATALOGUES);
		CommonTapDescriptor currObs  = descriptors.getDescriptorByMission(missionId);
		
		if(currObs != null ) {
			controller.getRootPresenter().getRelatedMetadataWithoutMOC(currObs);
			JSONObject callbackMessage = new JSONObject();
			callbackMessage.put("message", new JSONString("Catalogs from missionId: " + missionId + " displayed in the ESASky"));
			sendBackToWidget(null, callbackMessage, widget);
		}
		else {
			JSONObject callbackMessage = new JSONObject();
			callbackMessage.put("message", new JSONString("Unknown mission: " + missionId + "\n Check getCataloguesCount() for available mission names"));
			sendBackToWidget(null, callbackMessage, widget);
		}
	}
	
	public void coneSearchCatalogues(String missionId, double ra, double dec, double radius, JavaScriptObject widget) {
		DescriptorCountAdapter descriptors = controller.getRootPresenter().getDescriptorRepository().getDescriptorCountAdapter(EsaSkyWebConstants.CATEGORY_CATALOGUES);
		CommonTapDescriptor currObs  = descriptors.getDescriptorByMission(missionId);
		
		if(currObs != null ) {
			SkyViewPosition conePos = new SkyViewPosition(new Coordinate(ra, dec), 2 * radius);
			controller.getRootPresenter().coneSearch(currObs, conePos);
			JSONObject callbackMessage = new JSONObject();
			callbackMessage.put("message", new JSONString("Catalogs from missionId: " + missionId + " displayed in the ESASky"));
			sendBackToWidget(null, callbackMessage, widget);
		}
		else {
			JSONObject callbackMessage = new JSONObject();
			callbackMessage.put("message", new JSONString("Unknown mission: " + missionId + "\n Check getCataloguesCount() for available mission names"));
			sendBackToWidget(null, callbackMessage, widget);
		}
	}
	
	public void plotSpectra(String missionId, JavaScriptObject widget) {
		GoogleAnalytics.sendEventWithURL(googleAnalyticsCat, GoogleAnalytics.ACT_PYESASKY_PLOTSPECTRA, missionId);
		DescriptorCountAdapter descriptors = controller.getRootPresenter().getDescriptorRepository().getSpectraDescriptors();
		CommonTapDescriptor currObs  = descriptors.getDescriptorByMission(missionId);
		
		if(currObs != null ) {
			controller.getRootPresenter().getRelatedMetadataWithoutMOC(currObs);
			JSONObject callbackMessage = new JSONObject();
			callbackMessage.put("message", new JSONString("Spectra from missionId: " + missionId + " displayed in the ESASky"));
			sendBackToWidget(null, callbackMessage, widget);
		}
		else {
			JSONObject callbackMessage = new JSONObject();
			callbackMessage.put("message", new JSONString("Unknown mission: " + missionId + "\n Check getSpectraCount() for available mission names"));
			sendBackToWidget(null, callbackMessage, widget);
		}
	}
	
	public void coneSearchSpectra(String missionId, double ra, double dec, double radius, JavaScriptObject widget) {
		DescriptorCountAdapter descriptors = controller.getRootPresenter().getDescriptorRepository().getSpectraDescriptors();
		CommonTapDescriptor currObs  = descriptors.getDescriptorByMission(missionId);
		
		if(currObs != null ) {
			SkyViewPosition conePos = new SkyViewPosition(new Coordinate(ra, dec), 2 * radius);
			controller.getRootPresenter().coneSearch(currObs, conePos);
			String message = "Catalogs from missionId: " + missionId + " displayed in the ESASky";
			sendBackMessageToWidget(message, widget);
		}
		else {
			String message = "Unknown mission: " + missionId + "\n Check getSpectraCount() for available mission names";
			sendBackMessageToWidget(message, widget);
		}
	}
	
	public void plotPublications(JavaScriptObject widget) {
		GoogleAnalytics.sendEventWithURL(googleAnalyticsCat, GoogleAnalytics.ACT_PYESASKY_PLOTPUBLICATIONS);
		controller.getRootPresenter().getCtrlTBPresenter().getPublicationPresenter().getPublications();
	}

	public void coneSearchPublications(double ra, double dec, double radius, JavaScriptObject widget) {
		SkyViewPosition conePos = new SkyViewPosition(new Coordinate(ra, dec), 2 * radius);
		controller.getRootPresenter().getCtrlTBPresenter().getPublicationPresenter().getPublications(conePos);

	}
}
