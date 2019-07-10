package esac.archive.esasky.cl.web.client.api;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.allen_sauer.gwt.log.client.Log;
import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONNumber;

import esac.archive.absi.modules.cl.aladinlite.widget.client.model.ColorPalette;
import esac.archive.esasky.ifcs.model.client.HiPS;
import esac.archive.esasky.ifcs.model.client.HiPSCoordsFrame;
import esac.archive.esasky.ifcs.model.client.HipsWavelength;
import esac.archive.esasky.ifcs.model.client.SkiesMenu;
import esac.archive.esasky.ifcs.model.client.SkiesMenuEntry;
import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesConversion;
import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesFrame;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.client.HiPS.HiPSImageFormat;
import esac.archive.esasky.ifcs.model.descriptor.BaseDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.CatalogDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.MetadataDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.ObservationDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.PublicationsDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.SpectraDescriptor;
import esac.archive.esasky.ifcs.model.shared.ColumnType;
import esac.archive.esasky.cl.web.client.Controller;
import esac.archive.esasky.cl.web.client.EsaSkyWeb;
import esac.archive.esasky.cl.web.client.api.model.Footprint;
import esac.archive.esasky.cl.web.client.api.model.FootprintListOverlay;
import esac.archive.esasky.cl.web.client.api.model.GeneralSkyObject;
import esac.archive.esasky.cl.web.client.api.model.FootprintListJSONWrapper;
import esac.archive.esasky.cl.web.client.api.model.MetadataAPI;
import esac.archive.esasky.cl.web.client.api.model.Source;
import esac.archive.esasky.cl.web.client.api.model.SourceListJSONWrapper;
import esac.archive.esasky.cl.web.client.api.model.SourceListOverlay;
import esac.archive.esasky.cl.web.client.model.entities.EntityContext;
import esac.archive.esasky.cl.web.client.repository.DescriptorRepository.DescriptorListAdapter;
import esac.archive.esasky.cl.web.client.status.CountObserver;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.CoordinateUtils;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.planningmenu.PlanObservationPanel;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.selectsky.SelectSkyPanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.AbstractTableObserver;
import esac.archive.esasky.cl.web.client.view.resultspanel.AbstractTablePanel;

public class Api {
	


	public interface CatalogueMapper extends ObjectMapper<SourceListJSONWrapper> {
	}

	public interface FootprintsSetMapper extends ObjectMapper<FootprintListJSONWrapper> {
	}

	public interface CatalogDescriptorMapper extends ObjectMapper<CatalogDescriptor> {
	}

	Map<String, JavaScriptObject> userCatalogues = new HashMap<String, JavaScriptObject>();
	Map<String, JavaScriptObject> setOfFootprints = new HashMap<String, JavaScriptObject>();
	Controller controller;
	
	private String googleAnalyticsCat = GoogleAnalytics.CAT_Pyesasky;
	
	public void setGoogleAnalyticsCatToPython() {
		this.googleAnalyticsCat = GoogleAnalytics.CAT_Pyesasky;
	}

	public void setGoogleAnalyticsCatToAPI() {
		this.googleAnalyticsCat = GoogleAnalytics.CAT_JavaScriptAPI;
	}
	

	public Api(EsaSkyWeb esaSkyWeb) {
		Log.debug("[Api]");

		Api.onJavaApiReady();

		this.controller = esaSkyWeb.getController();

		Api.init(this);

		Log.debug("[Api] Ready!!");
	}

	public static native void onJavaApiReady() /*-{
		$wnd.JavaApiReady();
	}-*/;

	public static native void init(Api instance) /*-{
		
		function handleMessage(e){
			var msg = e.data
			console.log('message recieved');
			console.log(msg);
			
			if('origin' in msg){
				if(msg['origin'] == 'pyesasky'){
					instance.@esac.archive.esasky.cl.web.client.api.Api::setGoogleAnalyticsCatToPython()();
				}
				else{
					instance.@esac.archive.esasky.cl.web.client.api.Api::setGoogleAnalyticsCatToAPI()();
				}
			}else{
					instance.@esac.archive.esasky.cl.web.client.api.Api::setGoogleAnalyticsCatToAPI()();
			}
			
			switch (msg['event']) {
				
				case 'initTest':
					console.log('initTest event captured');
					instance.@esac.archive.esasky.cl.web.client.api.Api::sendInitMessage(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
					break;

				case 'goToTargetName':
					console.log('goToTargetName event captured');
					return instance.@esac.archive.esasky.cl.web.client.api.Api::goToTargetName(Ljava/lang/String;)(msg['targetname']);
				
				case 'setFoV':
					console.log('setFoV event captured');
					return instance.@esac.archive.esasky.cl.web.client.api.Api::setFoV(D)(msg['fovDeg']);
				
				case 'goToRADec':
					console.log('goToRADec event captured!');
					return instance.@esac.archive.esasky.cl.web.client.api.Api::goTo(Ljava/lang/String;Ljava/lang/String;)(msg['ra'], msg['dec']);

				case 'setHiPSColorPalette':
					console.log('setHiPSColorPalette event captured!');
					return instance.@esac.archive.esasky.cl.web.client.api.Api::setHiPSColorPalette(Ljava/lang/String;)(msg['colorPalette']);
	
				case 'changeHiPS':
					console.log('changeHiPS event captured!');
					console.log(msg);
					var callback = instance.@esac.archive.esasky.cl.web.client.api.Api::setHiPS(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(msg['content'],e);
					//e.source.postMessage(callback,e.origin);
					break;
				
				case 'changeHiPSWithParams':
					console.log('changeHiPSWithParams event captured!');
					console.log(msg);
					console.log("HiPS URL "+msg['content']['HiPS']['url']);
					instance.@esac.archive.esasky.cl.web.client.api.Api::setHiPSWithParams(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;)(msg['content']['HiPS']['id'], msg['content']['HiPS']['name'], msg['content']['HiPS']['url'], msg['content']['HiPS']['cooframe'], msg['content']['HiPS']['maxnorder'], msg['content']['HiPS']['imgformat']);
					break
					
				case 'overlayFootprints':
					console.log('overlayFootprints event captured!');
					console.log(msg);
					var footprintSetJSON = JSON.stringify(msg['content']);
					return instance.@esac.archive.esasky.cl.web.client.api.Api::overlayFootprints(Ljava/lang/String;)(footprintSetJSON);
	
				case 'overlayFootprintsWithDetails':
					console.log('overlayFootprintsWithDetails event captured!');
					console.log(msg);
					var footprintSetJSON = JSON.stringify(msg['content']);
					return instance.@esac.archive.esasky.cl.web.client.api.Api::overlayFootprintsWithData(Ljava/lang/String;)(footprintSetJSON);
				
				case 'clearFootprintsOverlay':
					console.log('clearFootprintsOverlay event captured!');
					console.log(msg);
					return instance.@esac.archive.esasky.cl.web.client.api.Api::clearFootprints(Ljava/lang/String;)(msg['content']);
					
				case 'deleteFootprintsOverlay':
					console.log('deleteFootprintsOverlay event captured!');
					console.log(msg);
					return instance.@esac.archive.esasky.cl.web.client.api.Api::deleteFootprints(Ljava/lang/String;)(msg['content']);
	
				case 'overlayCatalogue':
					console.log('overlayCatalogue event captured!');
					console.log(msg);
					var catJSON = JSON.stringify(msg['content']);
					return instance.@esac.archive.esasky.cl.web.client.api.Api::overlayCatalogue(Ljava/lang/String;)(catJSON);
	
				case 'overlayCatalogueWithDetails':
					console.log('overlayCatalogueWithDetails event captured!');
					console.log(msg);
					var userCatalogueJSON = JSON.stringify(msg['content']);
					return instance.@esac.archive.esasky.cl.web.client.api.Api::overlayCatalogueWithData(Ljava/lang/String;)(userCatalogueJSON);
				
				case 'clearCatalogue':
					console.log('clear catalgue event captured!');
					console.log(msg);
					return instance.@esac.archive.esasky.cl.web.client.api.Api::clearCatalogue(Ljava/lang/String;)(msg['content']);
	
				case 'removeCatalogue':
					console.log('remove catalgue event captured!');
					console.log(msg);
					return instance.@esac.archive.esasky.cl.web.client.api.Api::removeCatalogue(Ljava/lang/String;)(msg['content']);
	
				case 'getAvailableHiPS':
					console.log('getAvailableHiPS event captured!');
					console.log(msg);
					var wavelength = msg['wavelength'];
					var hipsMap = instance.@esac.archive.esasky.cl.web.client.api.Api::getAvailableHiPS(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(wavelength,e);
					e.source.postMessage(hipsMap,e.origin);
					break;	
					
				case 'addJwst':
					console.log('addJwst event captured!');
					console.log(msg);
					instance.@esac.archive.esasky.cl.web.client.api.Api::addJwst(Ljava/lang/String;Ljava/lang/String;Z)(msg['instrument'],msg['detector'] , msg['showAllInstruments']);
					break;

				case 'addJwstWithCoordinates':
					console.log('addJwstWithCoordinates event captured!');
					console.log(msg);
					instance.@esac.archive.esasky.cl.web.client.api.Api::addJwstWithCoordinates(Ljava/lang/String;Ljava/lang/String;ZLjava/lang/String;Ljava/lang/String;Ljava/lang/String;)(msg['instrument'],msg['detector'] , msg['showAllInstruments'],msg['ra'], msg['dec'],msg['rotation']);
					break;
					
				case 'closeJwstPanel':
					console.log('closeJwst event captured!');
					console.log(msg);
					instance.@esac.archive.esasky.cl.web.client.api.Api::closeJwstPanel()();
					break;
					
				case 'openJwstPanel':
					console.log('openJwst event captured!');
					console.log(msg);
					instance.@esac.archive.esasky.cl.web.client.api.Api::openJwstPanel()();
					break;
		
				case 'clearJwstAll':
					console.log('clearJwstAll event captured!');
					console.log(msg);
					instance.@esac.archive.esasky.cl.web.client.api.Api::clearJwst()();
					break;
					
				case 'getCenter':
					console.log('getCenter event captured');
					var coors = instance.@esac.archive.esasky.cl.web.client.api.Api::getCenter(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(msg['cooFrame'],e);
					break;	
					
				case 'getObservationsCount':
					console.log('getObservations event captured');
					instance.@esac.archive.esasky.cl.web.client.api.Api::getObservationsCount(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
					break;	
					
				case 'getCataloguesCount':
					console.log('getCataloguesCount event captured');
					instance.@esac.archive.esasky.cl.web.client.api.Api::getCataloguesCount(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
					break;	
					
				case 'getSpectraCount':
					console.log('getSpectraCount event captured');
					instance.@esac.archive.esasky.cl.web.client.api.Api::getSpectraCount(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
					break;	
					
				case 'getPublicationsCount':
					console.log('getPublicationsCount event captured');
					instance.@esac.archive.esasky.cl.web.client.api.Api::getPublicationsCount(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
					break;	

				case 'plotObservations':
					console.log('plotObservations event captured');
					var callbackMessage = instance.@esac.archive.esasky.cl.web.client.api.Api::plotObservations(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(msg['missionId'],e);
					e.source.postMessage(callbackMessage,e.origin);
					break;	
					
				case 'plotCatalogues':
					console.log('plotCatalogues event captured');
					var callbackMessage = instance.@esac.archive.esasky.cl.web.client.api.Api::plotCatalogues(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(msg['missionId'],e);
					e.source.postMessage(callbackMessage,e.origin);
					break;	
					
				case 'plotSpectra':
					console.log('plotSpectra event captured');
					var callbackMessage = instance.@esac.archive.esasky.cl.web.client.api.Api::plotSpectra(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(msg['missionId'],e);
					e.source.postMessage(callbackMessage,e.origin);
					break;	
					
				case 'getResultPanelData':
					console.log('getResultPanelData event captured');
					instance.@esac.archive.esasky.cl.web.client.api.Api::getResultPanelData(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
					break;	
	
				default:
					console.log('No event associated');
			}
		}	
		
		$wnd.addEventListener('message', handleMessage);

		$wnd.ESASkyAPI.goToAPI = function(ra, dec) {
			instance.@esac.archive.esasky.cl.web.client.api.Api::goTo(Ljava/lang/String;Ljava/lang/String;)(ra, dec);
		};

		$wnd.ESASkyAPI.goToWithParamsAPI = function(ra, dec, fovDegrees,
				showTargetPointer, cooFrame) {
			instance.@esac.archive.esasky.cl.web.client.api.Api::goToWithParams(Ljava/lang/String;Ljava/lang/String;DZLjava/lang/String;)(ra, dec, fovDegrees, showTargetPointer, cooFrame);
		};

		$wnd.ESASkyAPI.goToTargetNameAPI = function(targetName) {
			instance.@esac.archive.esasky.cl.web.client.api.Api::goToTargetName(Ljava/lang/String;)(targetName);
		};

		$wnd.ESASkyAPI.goToTargetNameWithFoVAPI = function(targetName, fovDeg) {
			instance.@esac.archive.esasky.cl.web.client.api.Api::goToTargetNameWithFoV(Ljava/lang/String;D)(targetName, fovDeg);
		};

		$wnd.ESASkyAPI.setFoVAPI = function(fov) {
			instance.@esac.archive.esasky.cl.web.client.api.Api::setFoV(D)(fov);
		};

//		$wnd.ESASkyAPI.setHiPSAPI = function(surveyId) {
//			instance.@esac.archive.esasky.cl.web.client.api.Api::setHiPS(Ljava/lang/String;)(surveyId);
//		};

		$wnd.ESASkyAPI.setHiPSWithParamsAPI = function(surveyId, surveyName,
				surveyRootUrl, surveyFrame, maximumNorder, imgFormat) {
			instance.@esac.archive.esasky.cl.web.client.api.Api::setHiPSWithParams(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;)(surveyId, surveyName, surveyRootUrl, surveyFrame, maximumNorder, imgFormat);
		};

		$wnd.ESASkyAPI.setHiPSColorPaletteAPI = function(colorPalette) {
			instance.@esac.archive.esasky.cl.web.client.api.Api::setHiPSColorPalette(Ljava/lang/String;)(colorPalette);
		};

		$wnd.ESASkyAPI.overlayCatalogueAPI = function(catalogueJSON) {
			instance.@esac.archive.esasky.cl.web.client.api.Api::overlayCatalogue(Ljava/lang/String;)(catalogueJSON);
		};

		$wnd.ESASkyAPI.overlayCatalogueWithDataAPI = function(userCatalogueJSON) {
			instance.@esac.archive.esasky.cl.web.client.api.Api::overlayCatalogueWithData(Ljava/lang/String;)(userCatalogueJSON);
		};

		$wnd.ESASkyAPI.clearCatalogueAPI = function(catalogueName) {
			instance.@esac.archive.esasky.cl.web.client.api.Api::clearCatalogue(Ljava/lang/String;)(catalogueName);
		};

		$wnd.ESASkyAPI.removeCatalogueAPI = function(catalogueName) {
			instance.@esac.archive.esasky.cl.web.client.api.Api::removeCatalogue(Ljava/lang/String;)(catalogueName);
		};

		$wnd.ESASkyAPI.overlayFootprintsAPI = function(footprintsSetJSON) {
			instance.@esac.archive.esasky.cl.web.client.api.Api::overlayFootprints(Ljava/lang/String;)(footprintsSetJSON);
		};

		$wnd.ESASkyAPI.overlayFootprintsWithDataAPI = function(
				footprintsSetJSON) {
			instance.@esac.archive.esasky.cl.web.client.api.Api::overlayFootprintsWithData(Ljava/lang/String;)(footprintsSetJSON);
		};

		$wnd.ESASkyAPI.clearFootprintsAPI = function(overlayName) {
			instance.@esac.archive.esasky.cl.web.client.api.Api::clearFootprints(Ljava/lang/String;)(overlayName);
		};

		$wnd.ESASkyAPI.deleteFootprintsAPI = function(overlayName) {
			instance.@esac.archive.esasky.cl.web.client.api.Api::deleteFootprints(Ljava/lang/String;)(overlayName);
		};
		
//		$wnd.ESASkyAPI.getAvailableHiPSAPI = function(wavelength) {
//			return instance.@esac.archive.esasky.cl.web.client.api.Api::getAvailableHiPS(Ljava/lang/String;)(wavelength);
//		};
		
		$wnd.ESASkyAPI.addJwstWithCoordinatesAPI = function(instrument, detector, allInstruments, ra, dec, rotation) {
			return instance.@esac.archive.esasky.cl.web.client.api.Api::addJwstWithCoordinates(Ljava/lang/String;Ljava/lang/String;ZLjava/lang/String;Ljava/lang/String;Ljava/lang/String;)(instrument, detector, allInstruments, ra, dec, rotation);
		};
		
		$wnd.ESASkyAPI.addJwstAPI = function(instrument, detector, allInstruments) {
			return instance.@esac.archive.esasky.cl.web.client.api.Api::addJwst(Ljava/lang/String;Ljava/lang/String;Z)(instrument, detector, allInstruments);
		};
		
		$wnd.ESASkyAPI.closeJwstPanelAPI = function(){
			return instance.@esac.archive.esasky.cl.web.client.api.Api::closeJwstPanel()();
		};
		
		$wnd.ESASkyAPI.openJwstPanelAPI = function(){
			return instance.@esac.archive.esasky.cl.web.client.api.Api::openJwstPanel()();
		};
		
		$wnd.ESASkyAPI.clearJwstAPI = function(){
			return instance.@esac.archive.esasky.cl.web.client.api.Api::clearJwst()();
		};

	}-*/;
	
	
	public void getResultPanelData(final JavaScriptObject msg) {
		GoogleAnalytics.sendEventWithURL(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_getResultPanelData);
		final AbstractTablePanel tablePanel = controller.getRootPresenter().getResultsPresenter().getTabPanel().getSelectedWidget();
		JSONObject callback = tablePanel.exportAsJSON();
		if(callback.size() == 0) {
			tablePanel.registerObserver( new AbstractTableObserver() {

				@Override
				public void numberOfShownRowsChanged(int numberOfShownRows) {
					JSONObject callback = tablePanel.exportAsJSON();
					sendBackToWidget(callback, msg);
					tablePanel.unregisterObserver(this);
				}
				
			});
		}else {
			sendBackToWidget(callback, msg);
		}
	}
	
	public void plotObservations(String missionId, JavaScriptObject widget) {
		GoogleAnalytics.sendEventWithURL(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_plotObservations, missionId);
		DescriptorListAdapter<ObservationDescriptor> descriptors = controller.getRootPresenter().getDescriptorRepository().getObsDescriptors();
		EntityContext context = EntityContext.ASTRO_IMAGING;
		ObservationDescriptor currObs  = descriptors.getDescriptorByMissionNameCaseInsensitive(missionId);
		
		if(currObs != null ) {
			controller.getRootPresenter().getRelatedMetadata(currObs,context);
			JSONObject callbackMessage = new JSONObject();
			callbackMessage.put("message", new JSONString("Image observations from missionId: " + missionId + " displayed in the ESASky"));
			sendBackToWidget(null, callbackMessage, widget);
		}
		else {
			JSONObject callbackMessage = new JSONObject();
			callbackMessage.put("message", new JSONString("Unknown mission: " + missionId + "\n Check getObservationCount() for available mission names"));
			sendBackToWidget(null, callbackMessage, widget);
		}
	}
	
	public void plotCatalogues(String missionId, JavaScriptObject widget) {
		GoogleAnalytics.sendEventWithURL(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_plotCatalogues, missionId);
		DescriptorListAdapter<CatalogDescriptor> descriptors = controller.getRootPresenter().getDescriptorRepository().getCatDescriptors();
		EntityContext context = EntityContext.ASTRO_CATALOGUE;
		CatalogDescriptor currObs  = descriptors.getDescriptorByMissionNameCaseInsensitive(missionId);
		
		if(currObs != null ) {
			controller.getRootPresenter().getRelatedMetadata(currObs,context);
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
		GoogleAnalytics.sendEventWithURL(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_plotSpectra, missionId);
		DescriptorListAdapter<SpectraDescriptor> descriptors = controller.getRootPresenter().getDescriptorRepository().getSpectraDescriptors();
		EntityContext context = EntityContext.ASTRO_SPECTRA;
		SpectraDescriptor currObs  = descriptors.getDescriptorByMissionNameCaseInsensitive(missionId);
		
		if(currObs != null ) {
			controller.getRootPresenter().getRelatedMetadata(currObs,context);
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
	
	public void getCenter(String cooFrame, JavaScriptObject widget){
		SkyViewPosition skyViewPosition;
		if(cooFrame.equals("J2000")) {
			skyViewPosition = CoordinateUtils.getCenterCoordinateInJ2000();
		}else {
			skyViewPosition = CoordinateUtils.getCenterCoordinateInGalactic();
		}
		
		JSONObject coors = new  JSONObject();
		coors.put("ra", new JSONNumber(skyViewPosition.getCoordinate().ra));
		coors.put("dec", new JSONNumber(skyViewPosition.getCoordinate().dec));
		coors.put("fov", new JSONNumber(skyViewPosition.getFov()));
		
		GoogleAnalytics.sendEventWithURL(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_getCenter, "Cooframe: " + cooFrame + " returned: " + coors.toString() );
		sendBackToWidget(coors, widget);
	}
	
	public void getObservationsCount(JavaScriptObject widget) {
		GoogleAnalytics.sendEventWithURL(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_getObservationsCount);
		DescriptorListAdapter<ObservationDescriptor> descriptors = controller.getRootPresenter().getDescriptorRepository().getObsDescriptors();
		getCounts(descriptors, widget);
	}
	
	public void getCataloguesCount(JavaScriptObject widget) {
		GoogleAnalytics.sendEventWithURL(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_getCataloguesCount);
		DescriptorListAdapter<CatalogDescriptor> descriptors = controller.getRootPresenter().getDescriptorRepository().getCatDescriptors();
		getCounts(descriptors, widget);
	}
	
	public void getSpectraCount(JavaScriptObject widget) {
		GoogleAnalytics.sendEventWithURL(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_getSpectraCount);
		DescriptorListAdapter<SpectraDescriptor> descriptors = controller.getRootPresenter().getDescriptorRepository().getSpectraDescriptors();
		getCounts(descriptors, widget);
	}
	
	public void getPublicationsCount(JavaScriptObject widget) {
		GoogleAnalytics.sendEventWithURL(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_getPublicationsCount);
		DescriptorListAdapter<PublicationsDescriptor> descriptors = controller.getRootPresenter().getDescriptorRepository().getPublicationsDescriptors();
		getCounts(descriptors, widget);
	}
	
	private void getCounts(final DescriptorListAdapter<? extends BaseDescriptor> descriptors, final JavaScriptObject widget) {
		final CountStatus countStatus = descriptors.getCountStatus();
		if(checkCountUpdated(descriptors)) {
			JSONObject obsCount = new  JSONObject();
			
			for (BaseDescriptor currObs : descriptors.getDescriptors()) {
				int c = countStatus.getDetailsByKey(currObs.getMission()).getCount();
				obsCount.put(currObs.getMission(), new JSONNumber(c));
			}
			
			obsCount.put("Total", new JSONNumber(countStatus.getTotalCount()));		
			GoogleAnalytics.sendEventWithURL(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_count, obsCount.toString());
			sendBackToWidget(obsCount, widget);
			
		}else {
			countStatus.registerObserver(new CountObserver() {
				@Override
				public void onCountUpdate(int newCount) {
					JSONObject obsCount = new  JSONObject();
					
					for (BaseDescriptor currObs : descriptors.getDescriptors()) {
						int c = countStatus.getDetailsByKey(currObs.getMission()).getCount();
						obsCount.put(currObs.getMission(), new JSONNumber(c));
					}
					
					obsCount.put("Total", new JSONNumber(countStatus.getTotalCount()));		
					GoogleAnalytics.sendEventWithURL(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_count, obsCount.toString());
					sendBackToWidget(obsCount, widget);
					countStatus.unregisterObserver(this);
				}
			});
		}
	}
	
	private Boolean checkCountUpdated(DescriptorListAdapter<? extends BaseDescriptor> descriptors) {
		if(descriptors != null) {
			CountStatus countStatus = descriptors.getCountStatus();
			String firstMissionId = descriptors.getDescriptors().get(0).getMission();
			return !countStatus.hasMoved(firstMissionId);
		}
		return false;
	}
	
	private void sendInitMessage( JavaScriptObject widget) {
		JSONObject msg = new JSONObject();
		msg.put("Text", new JSONString("Initialised"));
		sendBackToWidget(msg, widget);
	}
	
	
	private void sendBackToWidget(JSONObject values, JavaScriptObject widget) {
		sendBackToWidget(values,null,widget);
	}
	
	private native void sendBackToWidget(JSONObject values, JSONObject extras, JavaScriptObject widget) /*-{
		// Javascript adds a wrapper object around the values and extras which we remove
		var msg = {}
		if(values != null){
			msg["values"] = Object.values(values)[0];
		}
		if(extras != null){
			msg["extras"] = Object.values(extras)[0];
		}
		msg["msgId"] = widget.data.msgId;
		widget.source.postMessage(JSON.stringify(msg), widget.origin);
	}-*/;

	
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
	
	public void addJwstWithCoordinates(String instrument, String detector,boolean allInstruments, String ra, String dec, String rotation) {
		String allInfo = instrument + ";" + detector + ";" + Boolean.toString(allInstruments) + ";" + ra + ";" + dec + ";" + rotation;
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_addJwstWithCoordinates, allInfo);
		PlanObservationPanel planObservationPanel = PlanObservationPanel.getInstance();
		planObservationPanel.show();
		planObservationPanel.addInstrumentRowWithCoordinatesAPI(instrument, detector, allInstruments, ra, dec, rotation);
	}
	
	public void addJwst(String instrument, String detector, boolean allInstruments) {	
		String allInfo = instrument + ";" + detector + ";" + Boolean.toString(allInstruments);
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_addJwst, allInfo);
		PlanObservationPanel planObservationPanel = PlanObservationPanel.getInstance();
		planObservationPanel.show();
		planObservationPanel.addInstrumentRowAPI(instrument, detector, allInstruments);
	}

	public void goTo(String RA, String Dec) {
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_goToRADec, "ra: " + RA + "dec: " + Dec);
		AladinLiteWrapper.getInstance().goToObject(RA + " " + Dec, false);
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

	public void setHiPS(String wantedHiPSName, JavaScriptObject widget) {
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_changeHiPS, wantedHiPSName);
		JSONObject callbackMessage = new JSONObject();
		if (!SelectSkyPanel.getSelectedSky().setSelectHips(wantedHiPSName, true, false)) {
			String text =  "No HiPS called: " + wantedHiPSName + " found."
					+ " Try getAvailableHiPS() for existing HiPS names";
			callbackMessage.put("message",new JSONString(text));
			sendBackToWidget(null, callbackMessage, widget);
		}
		callbackMessage.put("message",new JSONString("Success"));
		sendBackToWidget(null, callbackMessage, widget);
	}

	public void setHiPSWithParams(String surveyId, String surveyName, String surveyRootUrl, String surveyFrame,
			int maximumNorder, String imgFormat) {
		
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_changeHiPSWithParams, surveyRootUrl);

		HiPS hips = new HiPS();
		hips.setSurveyId(surveyId);
		hips.setSurveyName(surveyName);
		hips.setSurveyRootUrl(surveyRootUrl);
		HiPSCoordsFrame surveyFrameEnum = HiPSCoordsFrame.GALACTIC.name().toLowerCase()
				.contains(surveyFrame.toLowerCase()) ? HiPSCoordsFrame.GALACTIC : HiPSCoordsFrame.EQUATORIAL;
		hips.setSurveyFrame(surveyFrameEnum);
		hips.setMaximumNorder(maximumNorder);
		HiPSImageFormat hipsImageFormatEnum = HiPSImageFormat.png.name().toLowerCase().contains(imgFormat.toLowerCase())
				? HiPSImageFormat.png : HiPSImageFormat.jpg;
		hips.setImgFormat(hipsImageFormatEnum);
		SelectSkyPanel.setHiPSFromAPI(hips, true);
	}

	public void setHiPSColorPalette(String colorPalette) {
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_setHiPSColorPalette, colorPalette);
		// Possible values: Native,EOSB, Planck, Rainbow, Greyscale, Cubehelix;
		ColorPalette colorPaletteEnum = ColorPalette.valueOf(colorPalette);
		AladinLiteWrapper.getInstance().setColorPalette(colorPaletteEnum);
	}

	public void overlayFootprints(String footprintsSetJSON) {

		FootprintsSetMapper mapper = GWT.create(FootprintsSetMapper.class);

		FootprintListJSONWrapper footprintsSet = (FootprintListJSONWrapper) mapper.read(footprintsSetJSON);

		JavaScriptObject overlay;
		FootprintListOverlay fooprintList = (FootprintListOverlay) footprintsSet.getOverlaySet();
		
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_overlayFootprints, fooprintList.getOverlayName());

		if (setOfFootprints.containsKey(fooprintList.getOverlayName())) {
			overlay = setOfFootprints.get(fooprintList.getOverlayName());
		} else {
			overlay = AladinLiteWrapper.getAladinLite().createOverlay(fooprintList.getOverlayName(),
					fooprintList.getColor());
		}
		for (Object currSkyObj : fooprintList.getSkyObjectList()) {
			Footprint currFoot = (Footprint) currSkyObj;
			JavaScriptObject footprintJS = AladinLiteWrapper.getAladinLite().createFootprintFromSTCS(currFoot.getStcs(),
					currFoot.getId());
			AladinLiteWrapper.getAladinLite().addFootprintToOverlay(overlay, footprintJS);
		}
		AladinLiteWrapper.getAladinLite().goToRaDec(((Footprint) fooprintList.getSkyObjectList().get(0)).getRa_deg(),
				((Footprint) fooprintList.getSkyObjectList().get(0)).getDec_deg());
	}

	public void overlayFootprintsWithData(String footprintsSetJSON) {

		FootprintsSetMapper mapper = GWT.create(FootprintsSetMapper.class);

		try {
			FootprintListJSONWrapper footprintsSet = (FootprintListJSONWrapper) mapper.read(footprintsSetJSON);

			GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_overlayFootprintsWithDetails, footprintsSet.getOverlaySet().getOverlayName());

			List<MetadataDescriptor> metadata = new LinkedList<MetadataDescriptor>();

			MetadataDescriptor mName = new MetadataDescriptor();
			mName.setIndex(0);
			mName.setLabel(APIMetadataConstants.OBS_NAME);
			mName.setMaxDecimalDigits(null);
			mName.setTapName(APIMetadataConstants.OBS_NAME);
			mName.setType(ColumnType.STRING);
			mName.setVisible(true);
			metadata.add(mName);

			MetadataDescriptor mId = new MetadataDescriptor();
			mId.setIndex(0);
			mId.setLabel(APIMetadataConstants.ID);
			mId.setMaxDecimalDigits(null);
			mId.setTapName(APIMetadataConstants.ID);
			mId.setType(ColumnType.STRING);
			mId.setVisible(false);
			metadata.add(mId);

			MetadataDescriptor mStcs = new MetadataDescriptor();
			mStcs.setIndex(0);
			mStcs.setLabel(APIMetadataConstants.FOOTPRINT_STCS);
			mStcs.setMaxDecimalDigits(null);
			mStcs.setTapName(APIMetadataConstants.FOOTPRINT_STCS);
			mStcs.setType(ColumnType.STRING);
			mStcs.setVisible(false);
			metadata.add(mStcs);

			MetadataDescriptor mRa = new MetadataDescriptor();
			mRa.setIndex(0);
			mRa.setLabel(APIMetadataConstants.CENTER_RA_DEG);
			mRa.setMaxDecimalDigits(null);
			mRa.setTapName(APIMetadataConstants.CENTER_RA_DEG);
			mRa.setType(ColumnType.RA);
			mRa.setVisible(false);
			metadata.add(mRa);

			MetadataDescriptor mDec = new MetadataDescriptor();
			mDec.setIndex(0);
			mDec.setLabel(APIMetadataConstants.CENTER_DEC_DEG);
			mDec.setMaxDecimalDigits(null);
			mDec.setTapName(APIMetadataConstants.CENTER_DEC_DEG);
			mDec.setType(ColumnType.DEC);
			mDec.setVisible(false);
			metadata.add(mDec);

			FootprintListOverlay fooprintList = (FootprintListOverlay) footprintsSet.getOverlaySet();

			GeneralSkyObject generalSkyObject = (GeneralSkyObject) fooprintList.getSkyObjectList().get(0);

			for (MetadataAPI currMetadata : generalSkyObject.getData()) {
				MetadataDescriptor m = new MetadataDescriptor();
				m.setIndex(0);
				m.setLabel(currMetadata.getName());
				m.setMaxDecimalDigits(null);
				m.setTapName(currMetadata.getName());
				m.setType(ColumnType.STRING);
				m.setVisible(true);
				metadata.add(m);
			}

			IDescriptor descriptor = controller.getRootPresenter().getDescriptorRepository()
					.initUserDescriptor(metadata, footprintsSet);

			controller.getRootPresenter().showUserRelatedMetadata(descriptor, footprintsSet, null);

			AladinLiteWrapper.getAladinLite().goToRaDec(
					((Footprint) fooprintList.getSkyObjectList().get(0)).getRa_deg(),
					((Footprint) fooprintList.getSkyObjectList().get(0)).getDec_deg());
		} catch (Exception ex) {
			Log.error(ex.getMessage());
		}

	}

	public void clearFootprints(String overlayName) {
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_clearFootprintsOverlay, overlayName);
		JavaScriptObject overlay;
		if (setOfFootprints.containsKey(overlayName)) {
			overlay = setOfFootprints.get(overlayName);
			AladinLiteWrapper.getAladinLite().removeAllFootprintsFromOverlay(overlay);
		}
	}

	public void deleteFootprints(String overlayName) {
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_deleteFootprintsOverlay, overlayName);
		JavaScriptObject overlay;
		if (setOfFootprints.containsKey(overlayName)) {
			overlay = setOfFootprints.get(overlayName);
			AladinLiteWrapper.getAladinLite().removeAllFootprintsFromOverlay(overlay);
			overlay = null;
			setOfFootprints.remove(overlayName);
		}
	}

	public void overlayCatalogueWithData(String userCatalogueJSON) {


		CatalogueMapper mapper = GWT.create(CatalogueMapper.class);
		try {
			SourceListJSONWrapper userCatalogue = (SourceListJSONWrapper) mapper.read(userCatalogueJSON);
			GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_overlayCatalogueWithDetails, userCatalogue.getOverlaySet().getOverlayName());

			CoordinatesFrame convertToFrame = null;

			if (!userCatalogue.getOverlaySet().getCooframe()
					.equals(AladinLiteWrapper.getCoordinatesFrame().getValue())) {
				if (AladinLiteWrapper.getCoordinatesFrame() == CoordinatesFrame.GALACTIC) {
					convertToFrame = CoordinatesFrame.GALACTIC;
				} else {
					convertToFrame = CoordinatesFrame.J2000;
				}
			} else if (userCatalogue.getOverlaySet().getCooframe()
					.equals(AladinLiteWrapper.getCoordinatesFrame().getValue())
					&& userCatalogue.getOverlaySet().getCooframe().equals(CoordinatesFrame.GALACTIC.getValue())) {
				convertToFrame = CoordinatesFrame.J2000;
			}

			List<MetadataDescriptor> metadata = new LinkedList<MetadataDescriptor>();
			
			MetadataDescriptor mName = new MetadataDescriptor();
			mName.setIndex(0);
			mName.setLabel(APIMetadataConstants.CAT_NAME);
			mName.setMaxDecimalDigits(null);
			mName.setTapName(APIMetadataConstants.CAT_NAME);
			mName.setType(ColumnType.STRING);
			mName.setVisible(true);
			metadata.add(mName);

			MetadataDescriptor mId = new MetadataDescriptor();
			mId.setIndex(0);
			mId.setLabel(APIMetadataConstants.ID);
			mId.setMaxDecimalDigits(null);
			mId.setTapName(APIMetadataConstants.ID);
			mId.setType(ColumnType.STRING);
			mId.setVisible(false);
			metadata.add(mId);

			MetadataDescriptor mRa = new MetadataDescriptor();
			mRa.setIndex(0);
			mRa.setLabel(APIMetadataConstants.CENTER_RA_DEG);
			mRa.setMaxDecimalDigits(null);
			mRa.setTapName(APIMetadataConstants.CENTER_RA_DEG);
			mRa.setType(ColumnType.RA);
			mRa.setVisible(true);
			metadata.add(mRa);

			MetadataDescriptor mDec = new MetadataDescriptor();
			mDec.setIndex(0);
			mDec.setLabel(APIMetadataConstants.CENTER_DEC_DEG);
			mDec.setMaxDecimalDigits(null);
			mDec.setTapName(APIMetadataConstants.CENTER_DEC_DEG);
			mDec.setType(ColumnType.DEC);
			mDec.setVisible(true);
			metadata.add(mDec);

			SourceListOverlay sourceList = (SourceListOverlay) userCatalogue.getOverlaySet();
			GeneralSkyObject generalSkyObject = (GeneralSkyObject) sourceList.getSkyObjectList().get(0);

			for (MetadataAPI currMetadata : generalSkyObject.getData()) {
				MetadataDescriptor m = new MetadataDescriptor();
				m.setIndex(0);
				m.setLabel(currMetadata.getName());
				m.setMaxDecimalDigits(null);
				m.setTapName(currMetadata.getName());
				m.setType(ColumnType.STRING);
				m.setVisible(true);
				metadata.add(m);
			}

			IDescriptor descriptor = controller.getRootPresenter().getDescriptorRepository()
					.initUserDescriptor(metadata, userCatalogue);
			controller.getRootPresenter().showUserRelatedMetadata(descriptor, userCatalogue, convertToFrame);

			centerAladinLite(((Source) sourceList.getSkyObjectList().get(0)).getRa_deg(),
					((Source) sourceList.getSkyObjectList().get(0)).getDec_deg(), convertToFrame);

		} catch (Exception ex) {
			Log.error(ex.getMessage());
		}
	}

	private void centerAladinLite(String raDeg, String decDeg, CoordinatesFrame convertToFrame) {

		Double[] raDecConverted;
		if (convertToFrame == CoordinatesFrame.GALACTIC) {
			raDecConverted = CoordinatesConversion.convertPointEquatorialToGalactic(Double.parseDouble(raDeg),
					Double.parseDouble(decDeg));
			raDeg = String.valueOf(raDecConverted[0]);
			decDeg = String.valueOf(raDecConverted[1]);
		} else if (convertToFrame == CoordinatesFrame.J2000) {
			raDecConverted = CoordinatesConversion.convertPointGalacticToJ2000(Double.parseDouble(raDeg),
					Double.parseDouble(decDeg));
			raDeg = String.valueOf(raDecConverted[0]);
			decDeg = String.valueOf(raDecConverted[1]);
		}

		AladinLiteWrapper.getAladinLite().goToRaDec(raDeg, decDeg);
	}

	public void overlayCatalogue(String catalogueJSON) {

		CatalogueMapper mapper = GWT.create(CatalogueMapper.class);
		SourceListJSONWrapper userCatalogue = (SourceListJSONWrapper) mapper.read(catalogueJSON);

		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_overlayCatalogue, userCatalogue.getOverlaySet().getOverlayName());

		CoordinatesFrame convertToFrame = null;

		// TODO TO BE REVIEWED. this is not te linewidth but the square size
		int sourceSize = userCatalogue.getOverlaySet().getLineWidth();
		if (sourceSize < 6) {
			sourceSize = 6;
		}

		if ("".equals(userCatalogue.getOverlaySet().getOverlayName())
				|| userCatalogue.getOverlaySet().getOverlayName() == null) {
			userCatalogue.getOverlaySet().setOverlayName("user catalogue");
		}

		if (!userCatalogue.getOverlaySet().getCooframe().equals(AladinLiteWrapper.getCoordinatesFrame().getValue())) {
			if (AladinLiteWrapper.getCoordinatesFrame() == CoordinatesFrame.GALACTIC) {
				convertToFrame = CoordinatesFrame.GALACTIC;
			} else {
				convertToFrame = CoordinatesFrame.J2000;
			}
		} else if (userCatalogue.getOverlaySet().getCooframe()
				.equals(AladinLiteWrapper.getCoordinatesFrame().getValue())
				&& userCatalogue.getOverlaySet().getCooframe().equals(CoordinatesFrame.GALACTIC.getValue())) {
			convertToFrame = CoordinatesFrame.J2000;
		}

		// check if the catalogue already exists
		JavaScriptObject catalogueOverlay;
		if (userCatalogues.containsKey(userCatalogue.getOverlaySet().getOverlayName())) {
			catalogueOverlay = userCatalogues.get(userCatalogue.getOverlaySet().getOverlayName());
		} else {
			catalogueOverlay = AladinLiteWrapper.getAladinLite().createCatalog(
					userCatalogue.getOverlaySet().getOverlayName(), userCatalogue.getOverlaySet().getColor(),
					sourceSize);
		}

		// loop over SourceAPI obj and add them to the created Aladin Catalogue
		Map<String, String> details = new HashMap<String, String>();

		for (Object currSkyObj : userCatalogue.getOverlaySet().getSkyObjectList()) {

			Source currSource = (Source) currSkyObj;

			details = new HashMap<String, String>();
			details.put("name", currSource.getName());

			JavaScriptObject source = AladinLiteWrapper.getAladinLite().newApi_createSourceJSObj(currSource.getRa(),
					currSource.getDec(), details);
			AladinLiteWrapper.getAladinLite().newApi_addSourceToCatalogue(catalogueOverlay, source);
		}
		userCatalogues.put(userCatalogue.getOverlaySet().getOverlayName(), catalogueOverlay);

		SourceListOverlay sourceList = (SourceListOverlay) userCatalogue.getOverlaySet();
		Source firstSource = ((Source) sourceList.getSkyObjectList().get(0));

		centerAladinLite(firstSource.getRa(), firstSource.getDec(), convertToFrame);
	}

	public void clearCatalogue(String catalogueName) {
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_clearCatalogue, catalogueName);
		// remove all sources from the catalogue but not the catalogue itself
		AladinLiteWrapper.getAladinLite().removeAllSourcesFromCatalog(userCatalogues.get(catalogueName));
	}

	public void removeCatalogue(String catalogueName) {
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_removeCatalogue, catalogueName);
		AladinLiteWrapper.getInstance();
		// Remove the catalogue from the map
		AladinLiteWrapper.getAladinLite().removeAllSourcesFromCatalog(userCatalogues.get(catalogueName));
		userCatalogues.remove(catalogueName);
	}

	public void getAvailableHiPS(String wavelength, JavaScriptObject widget) {
		//TODO Looks in skiesMenu which doesn't contain user added HiPS
		
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_getAvailableHiPS, wavelength);
		
		SkiesMenu skiesMenu = controller.getRootPresenter().getCtrlTBPresenter().getSelectSkyPresenter().getSkiesMenu();
		
		HipsWavelength hipsWavelength;
		try {
			hipsWavelength = HipsWavelength.valueOf(wavelength);	
		}catch(Exception e) {
			hipsWavelength = null;
		}
		if (null == hipsWavelength) {
			JSONObject wavelengthMap = new  JSONObject();
			for (SkiesMenuEntry currSkiesMenuEntry : skiesMenu.getMenuEntries()) {
				HipsWavelength currWavelength = currSkiesMenuEntry.getWavelength();
				wavelengthMap.put(currWavelength.name(),getHiPSByWavelength(currWavelength));
			}
			sendBackToWidget(wavelengthMap, null, widget);
		} else {
			sendBackToWidget(getHiPSByWavelength(hipsWavelength), null, widget);
		}
	}

	private JSONObject getHiPSByWavelength(HipsWavelength wavelength) {

		JSONObject hiPSMap = new JSONObject();
		SkiesMenu skiesMenu = controller.getRootPresenter().getCtrlTBPresenter().getSelectSkyPresenter().getSkiesMenu();
		for (SkiesMenuEntry currSkiesMenuEntry : skiesMenu.getMenuEntries()) {
			if (currSkiesMenuEntry.getWavelength() == wavelength) {
				for (HiPS currHiPS : currSkiesMenuEntry.getHips()) {
					JSONObject currHiPSJSON = new JSONObject();
					currHiPSJSON.put("HiPS label", new JSONString(currHiPS.getSurveyId()));
					currHiPSJSON.put("HiPS URL", new JSONString(currHiPS.getSurveyRootUrl()));
					currHiPSJSON.put("hips_frame", new JSONString(currHiPS.getSurveyFrame().getName()));
					currHiPSJSON.put("maxOrder", new JSONString(Integer.toString(currHiPS.getMaximumNorder())));
					currHiPSJSON.put("format", new JSONString(currHiPS.getImgFormat().name()));
					hiPSMap.put(currHiPS.getSurveyId(),currHiPSJSON);
				}
			}
		}
		return hiPSMap;
	}
}
