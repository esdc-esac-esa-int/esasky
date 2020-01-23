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
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;

import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteShapeSelectedEvent;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteShapeSelectedEventHandler;
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
import esac.archive.esasky.ifcs.model.descriptor.ExtTapDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.MetadataDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.ObservationDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.PublicationsDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.SpectraDescriptor;
import esac.archive.esasky.ifcs.model.shared.ColumnType;
import esac.archive.esasky.ifcs.model.shared.ESASkyColors;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;
import esac.archive.esasky.cl.web.client.CommonEventBus;
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
import esac.archive.esasky.cl.web.client.query.TAPExtTapService;
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
import esac.archive.esasky.cl.web.client.view.resultspanel.ResultsPanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.tab.MissionTabButtons;

public class Api {
	

	public interface CatalogueMapper extends ObjectMapper<SourceListJSONWrapper> {}

	public interface FootprintsSetMapper extends ObjectMapper<FootprintListJSONWrapper> {}

	public interface CatalogDescriptorMapper extends ObjectMapper<CatalogDescriptor> {}

	Map<String, JavaScriptObject> userCatalogues = new HashMap<String, JavaScriptObject>();
	Map<String, JavaScriptObject> setOfFootprints = new HashMap<String, JavaScriptObject>();
	Controller controller;
	
	private String googleAnalyticsCat = GoogleAnalytics.CAT_Pyesasky;
	
	private long lastGASliderSent = 0;
	
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

		ApiMessageParser.init(this);

		Log.debug("[Api] Ready!!");
	}

	public static native void onJavaApiReady() /*-{
		$wnd.JavaApiReady();
	}-*/;
	
	public void registerShapeSelectionCallback(final JavaScriptObject widget) {
		setDataPanelHidden(true);
		CommonEventBus.getEventBus().addHandler(AladinLiteShapeSelectedEvent.TYPE,
                new AladinLiteShapeSelectedEventHandler() {

            @Override
            public void onShapeSelectionEvent(AladinLiteShapeSelectedEvent selectEvent) {
            	 if (!selectEvent.getOverlayName().equals(EntityContext.PUBLICATIONS.toString())) {

                     // Selects a table row
                     AbstractTablePanel tableContainingShape = controller.getRootPresenter().getResultsPresenter().getTabPanel().getAbstractTablePanelFromId(selectEvent.getOverlayName());
                     
                     String data = tableContainingShape.getUnfilteredRow(selectEvent.getShapeId());
                     JSONObject values = new JSONObject();
                     values.put("data", new JSONString(data));
                     sendBackToWidget(values, widget);
                 }
            }
        });
	}
		
	public void addMOC(String options, String mocData) {
		
		JavaScriptObject moc = AladinLiteWrapper.getAladinLite().createQ3CMOC(options);
		AladinLiteWrapper.getAladinLite().addMOCData(moc, mocData);
		AladinLiteWrapper.getAladinLite().addMOC(moc);
		
	}
	
	public void getVisibleNpix(int norder) {
		JavaScriptObject js = AladinLiteWrapper.getAladinLite().getVisibleNpix(norder);
		Log.debug(js.toString());
	}
	
	public void getAvailableTapServices(JavaScriptObject widget) {
		JSONArray tapServices = new JSONArray();
		for(ExtTapDescriptor desc : controller.getRootPresenter().getDescriptorRepository().getExtTapDescriptors().getDescriptors()) {
			if(desc.getTreeMapType() == EsaSkyConstants.TREEMAP_TYPE_SERVICE) {
				tapServices.set(tapServices.size(), new JSONString(desc.getMission()));
			}
		}
		JSONObject result = new JSONObject();
		result.put("TapServices", tapServices);
		sendBackToWidget(result, null, widget);
	}
	
	public void getAllAvailableTapMissions(JavaScriptObject widget) {
		JSONObject tapServices = new  JSONObject();
		for(ExtTapDescriptor desc : controller.getRootPresenter().getDescriptorRepository().getExtTapDescriptors().getDescriptors()) {
			
			checkAndPutJSONObject(tapServices, desc);
		}
		sendBackToWidget(tapServices, null, widget);
	}
	
	private JSONObject checkAndPutJSONObject(JSONObject object, ExtTapDescriptor desc) {
		if(desc.getParent() == null) {
			if( !object.containsKey(desc.getMission())) {
				JSONObject childList = new JSONObject();
				object.put(desc.getMission(), childList);
				return childList;
			}else {
				return (JSONObject) object.get(desc.getMission());
			}
			
		}else if(desc.getParent().getParent() == null){
			JSONObject list = checkAndPutJSONObject(object,desc.getParent());
			if(!list.containsKey(desc.getMission())) {
				JSONArray array = new JSONArray();
				list.put(desc.getMission(), array);
				return list;
			}else {
				return list;
			}
			
		}else {
			JSONObject list = checkAndPutJSONObject(object,desc.getParent());
			JSONArray array = (JSONArray) list.get(desc.getParent().getMission());
			array.set(array.size(), new JSONString(desc.getMission()));
		}
		return object;
	}
	

	public void getExtTapADQL(String missionId, JavaScriptObject widget) {
		ExtTapDescriptor desc = controller.getRootPresenter().getDescriptorRepository().getExtTapDescriptors().getDescriptorByMissionNameCaseInsensitive(missionId);
		
		if(desc != null) {
			JSONObject callbackMessage = new  JSONObject();
			String adql = TAPExtTapService.getInstance().getMetadataAdql(desc);
			callbackMessage.put(missionId,new JSONString(adql));
			sendBackToWidget(callbackMessage, null, widget);
		}else {
			JSONObject callbackMessage = new JSONObject();
			callbackMessage.put("message", new JSONString("Unknown TAP Service: " + missionId + "\n Check getAllAvailableTapMissions() for available TAP Service"));
			sendBackToWidget(null, callbackMessage, widget);
		}
		
	}

	public void extTapCount(String missionId, JavaScriptObject widget) {
		DescriptorListAdapter<ExtTapDescriptor> descriptors = controller.getRootPresenter().getDescriptorRepository().getExtTapDescriptors();
		ExtTapDescriptor desc  = descriptors.getDescriptorByMissionNameCaseInsensitive(missionId);
		
		if(desc != null ) {
			controller.getRootPresenter().getDescriptorRepository().updateCount4ExtTap(desc);
			getExtTapCount(desc, widget);
		}else {
			JSONObject callbackMessage = new JSONObject();
			callbackMessage.put("message", new JSONString("Unknown TAP Service: " + missionId + "\n Check getAvailableTapServices() for available TAP Service"));
			sendBackToWidget(null, callbackMessage, widget);
		}
	}

	public void plotExtTap(String missionId, JavaScriptObject widget) {
		DescriptorListAdapter<ExtTapDescriptor> descriptors = controller.getRootPresenter().getDescriptorRepository().getExtTapDescriptors();
		EntityContext context = EntityContext.EXT_TAP;
		ExtTapDescriptor desc  = descriptors.getDescriptorByMissionNameCaseInsensitive(missionId);
		
		if(desc != null ) {
			controller.getRootPresenter().getRelatedMetadata(desc,context);
			JSONObject callbackMessage = new JSONObject();
			callbackMessage.put("message", new JSONString("Data from TAP: " + missionId + " displayed in the ESASky"));
			sendBackToWidget(null, callbackMessage, widget);
			
		}else {
			JSONObject callbackMessage = new JSONObject();
			callbackMessage.put("message", new JSONString("Unknown TAP Service: " + missionId + "\n Check getAllAvailableTapMissions()"
					+ " or getTapServiceCount() for available TAP Services"));
			sendBackToWidget(null, callbackMessage, widget);
		}
	}
	
	public void plotExtTapWithDetails(String name, String tapUrl, boolean dataOnlyInView, String adql, String color, int limit) {
		ExtTapDescriptor descriptor = controller.getRootPresenter().getDescriptorRepository().addExtTapDescriptorFromAPI(name, tapUrl, dataOnlyInView, adql);
		if(color == "") {
			color = ESASkyColors.getNext();
		}
		if(limit == -1) {
			limit = 3000;
		}
		descriptor.setHistoColor(color);
		descriptor.setSourceLimit(limit);
		controller.getRootPresenter().getEntityRepository().createExtTapEntity(descriptor, EntityContext.EXT_TAP);
		controller.getRootPresenter().getRelatedMetadata(descriptor, EntityContext.EXT_TAP);
	}
	
	public void showCoordinateGrid(boolean show) {
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_showCoordinateGrid,Boolean.toString(show));
		controller.getRootPresenter().getHeaderPresenter().toggleGrid(show);
	}
	
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
	
	public void closeDataPanel() {
		ResultsPanel.closeDataPanel();
	}
	
	public void setDataPanelHidden(boolean input) {
		ResultsPanel.shouldBeHidden(input);
	}
	
	public void closeResultPanelTab(int index) {
		final AbstractTablePanel tablePanel;
		try {
			if(index == -1) {
				tablePanel = controller.getRootPresenter().getResultsPresenter().getTabPanel().getSelectedWidget();
			}else {
				tablePanel = controller.getRootPresenter().getResultsPresenter().getTabPanel().getWidget(index);
			}
			
			tablePanel.closeTablePanel();
			String id = tablePanel.getEntity().getEsaSkyUniqId();
			MissionTabButtons tab = controller.getRootPresenter().getResultsPresenter().getTabPanel().getTabFromId(id);
			controller.getRootPresenter().getResultsPresenter().getTabPanel().removeTab(tab);
		}catch(IndexOutOfBoundsException e) {
			Log.error(e.toString());
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
			callbackMessage.put("message", new JSONString("Unknown mission: " + missionId + "\n Check getObservationsCount() for available mission names"));
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
	
	private void getExtTapCount(final ExtTapDescriptor descriptor, final JavaScriptObject widget) {
		final CountStatus countStatus = controller.getRootPresenter().getDescriptorRepository().getExtTapDescriptors().getCountStatus();
		if(!countStatus.hasMoved(descriptor.getMission())) {
			JSONObject obsCount = new  JSONObject();
			
			for(String key : countStatus.getKeys()) {
				if(key.toLowerCase().startsWith(descriptor.getMission().toLowerCase())){
					int c = countStatus.getDetailsByKey(key).getCount();
					if(c == 1) {
						obsCount.put(key, new JSONNumber(c));
					}
				}
			}
			
			GoogleAnalytics.sendEventWithURL(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_count, obsCount.toString());
			sendBackToWidget(obsCount, widget);
			
		}else {
			countStatus.registerObserver(new CountObserver() {
				@Override
				public void onCountUpdate(int newCount) {
					JSONObject obsCount = new  JSONObject();
					
					for(String key : countStatus.getKeys()) {
						if(key.toLowerCase().startsWith(descriptor.getMission().toLowerCase())){
							int c = countStatus.getDetailsByKey(key).getCount();
							if(c == 1) {
								obsCount.put(key, new JSONNumber(c));
							}
						}
					}
					
					GoogleAnalytics.sendEventWithURL(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_count, obsCount.toString());
					sendBackToWidget(obsCount, widget);
					countStatus.unregisterObserver(this);
				}
			});
		}
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
		widget.source.postMessage(msg, widget.origin);
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
	
	public void openSkyPanel() {
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_openSkyPanel, "");
		if(!controller.getRootPresenter().getCtrlTBPresenter().getSelectSkyPresenter().isShowing()) {
			controller.getRootPresenter().getCtrlTBPresenter().getSelectSkyPresenter().toggle();
		}
	}

	public void closeSkyPanel() {
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_closeSkyPanel, "");
		if(controller.getRootPresenter().getCtrlTBPresenter().getSelectSkyPresenter().isShowing()) {
			controller.getRootPresenter().getCtrlTBPresenter().getSelectSkyPresenter().toggle();
		}
	}
	
	public void addHiPS(String wantedHiPSName, JavaScriptObject widget) {
		SelectSkyPanel.getInstance().createSky();
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_addHips, wantedHiPSName);
		if(!setHiPS(wantedHiPSName, widget)) {
			SelectSkyPanel.getSelectedSky().notifyClose();
		}
	}
	
	public void addHiPSWithParams(String surveyName, String surveyRootUrl, String surveyFrame,
			int maximumNorder, String imgFormat) {
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_addHips, surveyRootUrl);
		SelectSkyPanel.getInstance().createSky();
		setHiPSWithParams(surveyName, surveyRootUrl, surveyFrame, maximumNorder, imgFormat);
	}
	
	public void removeSkyRow(int index, JavaScriptObject widget) {
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_removeHipsOnIndex, "");
		JSONObject callbackMessage = new JSONObject();
		if(index < 0) {
			for(int i = SelectSkyPanel.getInstance().getNumberOfSkyRows() -1; i > 0 ;i--) {
				SelectSkyPanel.getInstance().removeSky(i);
			}
		}else if(!SelectSkyPanel.getInstance().removeSky(index)) {
			String msg = "Index out of bounds. Max number is: " + Integer.toString(SelectSkyPanel.getInstance().getNumberOfSkyRows());
			callbackMessage.put("message",new JSONString(msg));
		}
		sendBackToWidget(null, callbackMessage, widget);
	}
	
	public void getNumberOfSkyRows(JavaScriptObject widget) {
		JSONObject countObj = new JSONObject();
		int count = SelectSkyPanel.getInstance().getNumberOfSkyRows();
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_getNumberOfSkyRows, Integer.toString(count));
		countObj.put("",new JSONNumber(count));
		sendBackToWidget(countObj, null, widget);
	}
	
	public void setHiPSSliderValue(double value) {
		if(System.currentTimeMillis() - lastGASliderSent > 1000) {
			lastGASliderSent = System.currentTimeMillis();
			GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_setHipsSliderValue, Double.toString(value));
		}
		SelectSkyPanel.getInstance().setSliderValue(value);
	}

	public boolean setHiPS(String wantedHiPSName, JavaScriptObject widget) {
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_changeHiPS, wantedHiPSName);
		JSONObject callbackMessage = new JSONObject();
		if (!SelectSkyPanel.getSelectedSky().setSelectHips(wantedHiPSName, true, false)) {
			String text =  "No HiPS called: " + wantedHiPSName + " found."
					+ " Try getAvailableHiPS() for existing HiPS names";
			callbackMessage.put("message",new JSONString(text));
			sendBackToWidget(null, callbackMessage, widget);
			return false;
		}
		sendBackToWidget(null, callbackMessage, widget);
		return true;
	}

	public void setHiPSWithParams(String surveyName, String surveyRootUrl, String surveyFrame,
			int maximumNorder, String imgFormat) {
		
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_changeHiPSWithParams, surveyRootUrl);

		HiPS hips = new HiPS();
		hips.setSurveyId(surveyName);
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
		SelectSkyPanel.getSelectedSky().setColorPalette(colorPaletteEnum);
		//AladinLiteWrapper.getInstance().setColorPalette(colorPaletteEnum);
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
