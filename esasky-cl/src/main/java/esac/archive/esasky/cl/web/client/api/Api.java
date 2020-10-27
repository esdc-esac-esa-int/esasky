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

import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteFoVChangedEvent;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteShapeSelectedEvent;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteShapeSelectedEventHandler;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.ColorPalette;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.client.HiPS;
import esac.archive.esasky.ifcs.model.client.HiPSCoordsFrame;
import esac.archive.esasky.ifcs.model.client.HipsWavelength;
import esac.archive.esasky.ifcs.model.client.SkiesMenu;
import esac.archive.esasky.ifcs.model.client.SkiesMenuEntry;
import esac.archive.esasky.ifcs.model.coordinatesutils.Coordinate;
import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesConversion;
import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesFrame;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.client.HiPS.HiPSImageFormat;
import esac.archive.esasky.ifcs.model.descriptor.BaseDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.CatalogDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.CustomTreeMapDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.ExtTapDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.MetadataDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.ObservationDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.SpectraDescriptor;
import esac.archive.esasky.ifcs.model.shared.ColumnType;
import esac.archive.esasky.ifcs.model.shared.ESASkyColors;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.Controller;
import esac.archive.esasky.cl.web.client.EsaSkyWeb;
import esac.archive.esasky.cl.web.client.Modules;
import esac.archive.esasky.cl.web.client.api.model.Footprint;
import esac.archive.esasky.cl.web.client.api.model.FootprintListOverlay;
import esac.archive.esasky.cl.web.client.api.model.GeneralSkyObject;
import esac.archive.esasky.cl.web.client.api.model.FootprintListJSONWrapper;
import esac.archive.esasky.cl.web.client.api.model.MetadataAPI;
import esac.archive.esasky.cl.web.client.api.model.SourceListJSONWrapper;
import esac.archive.esasky.cl.web.client.api.model.SourceListOverlay;
import esac.archive.esasky.cl.web.client.model.LineStyle;
import esac.archive.esasky.cl.web.client.model.SourceShapeType;
import esac.archive.esasky.cl.web.client.model.entities.GeneralEntityInterface;
import esac.archive.esasky.cl.web.client.model.entities.MOCEntity;
import esac.archive.esasky.cl.web.client.query.TAPExtTapService;
import esac.archive.esasky.cl.web.client.repository.DescriptorRepository;
import esac.archive.esasky.cl.web.client.repository.DescriptorRepository.DescriptorListAdapter;
import esac.archive.esasky.cl.web.client.repository.EntityRepository;
import esac.archive.esasky.cl.web.client.repository.MocRepository;
import esac.archive.esasky.cl.web.client.status.CountObserver;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.CoordinateUtils;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.planningmenu.PlanObservationPanel;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.selectsky.SelectSkyPanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.TableObserver;
import esac.archive.esasky.cl.web.client.view.resultspanel.ITablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.ResultsPanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.tab.MissionTabButtons;

public class Api {
	

	public interface CatalogueMapper extends ObjectMapper<SourceListJSONWrapper> {}

	public interface FootprintsSetMapper extends ObjectMapper<FootprintListJSONWrapper> {}

	public interface CatalogDescriptorMapper extends ObjectMapper<CatalogDescriptor> {}

	Map<String, JavaScriptObject> userCatalogues = new HashMap<String, JavaScriptObject>();
	Map<String, JavaScriptObject> userMocs = new HashMap<String, JavaScriptObject>();
	Map<String, JavaScriptObject> setOfFootprints = new HashMap<String, JavaScriptObject>();
	Controller controller;
	
	private static Api instance = null;
	
	private String googleAnalyticsCat = GoogleAnalytics.CAT_Pyesasky;
	
	private long lastGASliderSent = 0;
	
	public void setGoogleAnalyticsCatToPython() {
		this.googleAnalyticsCat = GoogleAnalytics.CAT_Pyesasky;
	}

	public void setGoogleAnalyticsCatToAPI() {
		this.googleAnalyticsCat = GoogleAnalytics.CAT_JavaScriptAPI;
	}
	
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

		ApiMessageParser.init(this);

		Log.debug("[Api] Ready!!");
		
	}

	public void registerShapeSelectionCallback(final JavaScriptObject widget) {
		
		 CommonEventBus.getEventBus().addHandler(AladinLiteShapeSelectedEvent.TYPE,
	                new AladinLiteShapeSelectedEventHandler() {

	                    @Override
	                    public void onShapeSelectionEvent(AladinLiteShapeSelectedEvent selectEvent) {
	                    	GeneralJavaScriptObject shape = (GeneralJavaScriptObject) selectEvent.getShape().cast();
	                    	String overlayName = selectEvent.getOverlayName();
	                    	String name = shape.getStringProperty("name");
	                    	if(name == null) {name="";};
                			String id = shape.getStringProperty("id");
                			if(id == null) {id="";};
	                		JSONObject result = new JSONObject();
	                		result.put("overlay", new JSONString(overlayName));
	                		result.put("name", new JSONString(name));
	                		result.put("id", new JSONString(id));
	                        sendBackToWidget(result, widget);
	                    }
	                });
	}
		
	public void addMocOld(String name, String options, String mocData) {
		if(userMocs.containsKey(name)) {
			AladinLiteWrapper.getAladinLite().removeMOC(userMocs.get(name));
			userMocs.remove(name);
		}
		JavaScriptObject moc = AladinLiteWrapper.getAladinLite().createMOC(options);
		userMocs.put(name,moc);
		AladinLiteWrapper.getAladinLite().addMOCData(moc, mocData);
		AladinLiteWrapper.getAladinLite().addMOC(moc);
		
	}

	public void addMOC(String name, GeneralJavaScriptObject options, GeneralJavaScriptObject mocData) {
		MOCEntity old = MocRepository.getInstance().getEntity(name);
		if(old != null) {
			old.closeFromAPI();
			MocRepository.getInstance().removeEntity(old);
		}
		
		IDescriptor descriptor = controller.getRootPresenter().getDescriptorRepository()
				.initUserDescriptor4MOC(name, options);
		MOCEntity entity = new MOCEntity(descriptor);
		
		if(options.hasProperty("lineStyle")) {
			entity.setLineStyle(options.getStringProperty("lineStyle"));
		}else {
			entity.setLineStyle(LineStyle.SOLID.getName());
		}

		if(options.hasProperty("opacity")) {
			entity.setSizeRatio(options.getDoubleProperty("opacity"));
		}
		
		if(!options.hasProperty("mode")) {
			options.setProperty("mode", "healpix");
		}
		
		if(options.hasProperty("addTab") && GeneralJavaScriptObject.convertToBoolean(options.getProperty("addTab"))) {
			ITablePanel panel = controller.getRootPresenter().getResultsPresenter().addResultsTab(entity);
			entity.setTablePanel(panel);
			panel.setEmptyTable("Showing coverage of " + name);
		}
		entity.addJSON(mocData, options);
		
	}
	
	public void removeMOC(String name) {
		if(userMocs.containsKey(name)) {
			AladinLiteWrapper.getAladinLite().removeMOC(userMocs.get(name));
			userMocs.remove(name);
		}
		
		MOCEntity entity = MocRepository.getInstance().getEntity(name);
		if(entity != null) {
			entity.closeFromAPI();
			MocRepository.getInstance().removeEntity(entity);
		}
	}
	
	public void addQ3CMOC(String options, String mocData) {
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
		ExtTapDescriptor desc  = descriptors.getDescriptorByMissionNameCaseInsensitive(missionId);
		
		if(desc != null ) {
			controller.getRootPresenter().getRelatedMetadata(desc);
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
	
	public void plotExtTapWithDetails(String name, String tapUrl, boolean dataOnlyInView, String adql, String color, int limit, GeneralJavaScriptObject options) {
		ExtTapDescriptor descriptor = controller.getRootPresenter().getDescriptorRepository().addExtTapDescriptorFromAPI(name, tapUrl, dataOnlyInView, adql);
		if(color == "") {
			color = ESASkyColors.getNext();
		}
		if(limit == -1) {
			limit = 3000;
		}
		descriptor.setPrimaryColor(color);
		descriptor.setShapeLimit(limit);
		
		if(options.hasProperty("STCSColumn")) {
			descriptor.setTapSTCSColumn(options.getStringProperty("STCSColumn"));
		}
		
		if(options.hasProperty("IntersectColumn")) {
			descriptor.setIntersectColumn(options.getStringProperty("IntersectColumn"));
		}
		
		
		controller.getRootPresenter().getEntityRepository().createEntity(descriptor);
		controller.getRootPresenter().getRelatedMetadata(descriptor);
	}
	
	public void showCoordinateGrid(boolean show) {
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_showCoordinateGrid,Boolean.toString(show));
		controller.getRootPresenter().getHeaderPresenter().toggleGrid(show);
	}
	
	public void getResultPanelData(final JavaScriptObject msg) {
		GoogleAnalytics.sendEventWithURL(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_getResultPanelData);
		final ITablePanel tablePanel = controller.getRootPresenter().getResultsPresenter().getTabPanel().getSelectedWidget();
		JSONObject callback = tablePanel.exportAsJSON();

		if(callback.size() == 0) {
			tablePanel.registerObserver( new TableObserver() {

				@Override
				public void numberOfShownRowsChanged(int numberOfShownRows) {
					JSONObject callback = tablePanel.exportAsJSON();
					JSONObject res = new JSONObject();
					res.put("value", callback);
					sendBackToWidget(res, msg);
					tablePanel.unregisterObserver(this);
				}

                @Override
                public void onSelection(ITablePanel selectedTablePanel) {
                }

                @Override
                public void onUpdateStyle(ITablePanel panel) {
                    // TODO Auto-generated method stub
                    
                }
			});
		}else {
			JSONObject res = new JSONObject();
			res.put("value", callback);
			sendBackToWidget(res, msg);
		}
	}
	
	public void hideResultPanel() {
		ResultsPanel.closeDataPanel();
	}
	
	public void showResultPanel() {
		ResultsPanel.openDataPanel();
	}
	
	public void setDataPanelHidden(boolean input) {
		ResultsPanel.shouldBeHidden(input);
	}
	
	public void closeResultPanelTab(int index) {
		final ITablePanel tablePanel;
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
	
	public void closeAllResultPanelTabs() {
		try {
			while(true) {
				ITablePanel tablePanel = controller.getRootPresenter().getResultsPresenter().getTabPanel().getSelectedWidget();
				tablePanel.closeTablePanel();
				String id = tablePanel.getEntity().getEsaSkyUniqId();
				MissionTabButtons tab = controller.getRootPresenter().getResultsPresenter().getTabPanel().getTabFromId(id);
				controller.getRootPresenter().getResultsPresenter().getTabPanel().removeTab(tab);
			}
		}catch(Exception e) {
			//Runs until it comes here when no tablePanel exists
		}
	}
	
	public void plotObservations(String missionId, JavaScriptObject widget) {
		GoogleAnalytics.sendEventWithURL(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_plotObservations, missionId);
		DescriptorListAdapter<ObservationDescriptor> descriptors = controller.getRootPresenter().getDescriptorRepository().getObsDescriptors();
		ObservationDescriptor currObs  = descriptors.getDescriptorByMissionNameCaseInsensitive(missionId);
		
		if(currObs != null ) {
			controller.getRootPresenter().getRelatedMetadataWithoutMOC(currObs);
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
	
	public void coneSearchObservations(String missionId, double ra, double dec, double radius, JavaScriptObject widget) {
		GoogleAnalytics.sendEventWithURL(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_plotObservations, missionId);
		DescriptorListAdapter<ObservationDescriptor> descriptors = controller.getRootPresenter().getDescriptorRepository().getObsDescriptors();
		ObservationDescriptor currObs  = descriptors.getDescriptorByMissionNameCaseInsensitive(missionId);
		
		if(currObs != null ) {
			SkyViewPosition conePos = new SkyViewPosition(new Coordinate(ra, dec), 2 * radius);
			controller.getRootPresenter().coneSearch(currObs, conePos);
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
		CatalogDescriptor currObs  = descriptors.getDescriptorByMissionNameCaseInsensitive(missionId);
		
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
		DescriptorListAdapter<CatalogDescriptor> descriptors = controller.getRootPresenter().getDescriptorRepository().getCatDescriptors();
		CatalogDescriptor currObs  = descriptors.getDescriptorByMissionNameCaseInsensitive(missionId);
		
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
		GoogleAnalytics.sendEventWithURL(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_plotSpectra, missionId);
		DescriptorListAdapter<SpectraDescriptor> descriptors = controller.getRootPresenter().getDescriptorRepository().getSpectraDescriptors();
		SpectraDescriptor currObs  = descriptors.getDescriptorByMissionNameCaseInsensitive(missionId);
		
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
		DescriptorListAdapter<SpectraDescriptor> descriptors = controller.getRootPresenter().getDescriptorRepository().getSpectraDescriptors();
		SpectraDescriptor currObs  = descriptors.getDescriptorByMissionNameCaseInsensitive(missionId);
		
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
		GoogleAnalytics.sendEventWithURL(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_plotPublications);
		controller.getRootPresenter().getCtrlTBPresenter().getPublicationPresenter().getPublications();
	}

	public void coneSearchPublications(double ra, double dec, double radius, JavaScriptObject widget) {
		SkyViewPosition conePos = new SkyViewPosition(new Coordinate(ra, dec), 2 * radius);
		controller.getRootPresenter().getCtrlTBPresenter().getPublicationPresenter().getPublications(conePos);

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

	private void getExtTapCount(final ExtTapDescriptor parent, final JavaScriptObject widget) {
		final CountStatus countStatus = controller.getRootPresenter().getDescriptorRepository().getExtTapDescriptors().getCountStatus();
		if(!countStatus.hasMoved(parent)) {
			JSONObject obsCount = new  JSONObject();
			
			for(ExtTapDescriptor desc : DescriptorRepository.getInstance().getExtTapDescriptors().getDescriptors()) {
				if(desc.hasParent(parent)) {
					if(countStatus.containsDescriptor(desc)) {
						obsCount.put(desc.getMission(), new JSONNumber(countStatus.getCount(desc)));
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
					
					for(ExtTapDescriptor desc : DescriptorRepository.getInstance().getExtTapDescriptors().getDescriptors()) {
						if(desc.hasParent(parent)) {
							if(countStatus.containsDescriptor(desc)) {
								obsCount.put(desc.getMission(), new JSONNumber(countStatus.getCount(desc)));
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
				obsCount.put(currObs.getMission(), new JSONNumber(countStatus.getCount(currObs)));
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
						obsCount.put(currObs.getMission(), new JSONNumber(countStatus.getCount(currObs)));
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
			return !countStatus.hasMoved(descriptors.getDescriptors().get(0));
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
	
	private void sendBackMessageToWidget(String message, JavaScriptObject widget) {
		JSONObject callbackMessage = new JSONObject();
		callbackMessage.put("message", new JSONString(message));
		sendBackToWidget(null, callbackMessage, widget);
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
	
	private native void sendBackToWidget(JavaScriptObject values, JavaScriptObject widget) /*-{
		var msg = {}
		if(values != null){
			msg["values"] = values;
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
	
	public void addJwstWithCoordinates(String instrument, String detector,boolean allInstruments, String ra, String dec, String rotation, JavaScriptObject widget) {
		String allInfo = instrument + ";" + detector + ";" + Boolean.toString(allInstruments) + ";" + ra + ";" + dec + ";" + rotation;
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_addJwstWithCoordinates, allInfo);
		PlanObservationPanel planObservationPanel = PlanObservationPanel.getInstance();
		planObservationPanel.show();
		String message = planObservationPanel.addInstrumentRowWithCoordinatesAPI(instrument, detector, allInstruments, ra, dec, rotation);
		if(message.length() > 0 ) {
			sendBackMessageToWidget(message, widget);
		}
	}
	
	public void addJwst(String instrument, String detector, boolean allInstruments, JavaScriptObject widget) {	
		String allInfo = instrument + ";" + detector + ";" + Boolean.toString(allInstruments);
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_addJwst, allInfo);
		PlanObservationPanel planObservationPanel = PlanObservationPanel.getInstance();
		planObservationPanel.show();
		String message = planObservationPanel.addInstrumentRowAPI(instrument, detector, allInstruments);
		if(message.length() > 0 ) {
			sendBackMessageToWidget(message, widget);
		}
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
		String msg = "";
		if(index < 0) {
			for(int i = SelectSkyPanel.getInstance().getNumberOfSkyRows() -1; i > 0 ;i--) {
				SelectSkyPanel.getInstance().removeSky(i);
			}
		}else if(!SelectSkyPanel.getInstance().removeSky(index)) {
			msg = "Index out of bounds. Max number is: " + Integer.toString(SelectSkyPanel.getInstance().getNumberOfSkyRows());
		}
		sendBackMessageToWidget(msg, widget);
	}
	
	public void getNumberOfSkyRows(JavaScriptObject widget) {
		JSONObject countObj = new JSONObject();
		int count = SelectSkyPanel.getInstance().getNumberOfSkyRows();
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_getNumberOfSkyRows, Integer.toString(count));
		countObj.put("Count",new JSONNumber(count));
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
		String text = "";
		if (!SelectSkyPanel.getSelectedSky().setSelectHips(wantedHiPSName, true, false)) {
			text =  "No HiPS called: " + wantedHiPSName + " found."
					+ " Try getAvailableHiPS() for existing HiPS names";

			return false;
		}
		sendBackMessageToWidget(text, widget);
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
	
	public void overlayFootprints(String footprintsSetJSON, boolean shouldBeInTablePanel) {

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

			controller.getRootPresenter().showUserRelatedMetadata(descriptor, GeneralJavaScriptObject.createJsonObject(footprintsSetJSON), shouldBeInTablePanel);

			AladinLiteWrapper.getAladinLite().goToRaDec(
					((Footprint) fooprintList.getSkyObjectList().get(0)).getRa_deg(),
					((Footprint) fooprintList.getSkyObjectList().get(0)).getDec_deg());
		} catch (Exception ex) {
			Log.error(ex.getMessage());
		}

	}

	public void removeOverlay(String overlayName, JavaScriptObject widget) {
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_clearFootprintsOverlay, overlayName);
		GeneralEntityInterface ent = EntityRepository.getInstance().getEntity(overlayName);
		if (ent != null) {
			ent.clearAll();
			EntityRepository.getInstance().removeEntity(ent);
		}
		else{
			JSONObject callbackMessage = new JSONObject();
			callbackMessage.put("message", new JSONString("No overlay with name: " + overlayName + " active:\n Check getActiveOverlays() for available overlays"));
			sendBackToWidget(null, callbackMessage, widget);
			
		}
	}

	public void overlayCatalogue(String userCatalogueJSON, boolean shouldBeInTablePanel) {


		CatalogueMapper mapper = GWT.create(CatalogueMapper.class);
		try {
			
			userCatalogueJSON = userCatalogueJSON.replace("\"ra\":", "\"ra_deg\":");
			userCatalogueJSON = userCatalogueJSON.replace("\"dec\":", "\"dec_deg\":");
			SourceListJSONWrapper userCatalogue = (SourceListJSONWrapper) mapper.read(userCatalogueJSON);
			GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_overlayCatalogueWithDetails, userCatalogue.getOverlaySet().getOverlayName());

			List<MetadataDescriptor> metadata = new LinkedList<MetadataDescriptor>();
			
			MetadataDescriptor mName = new MetadataDescriptor();
			mName.setIndex(0);
			mName.setLabel(APIMetadataConstants.CAT_NAME);
			mName.setMaxDecimalDigits(null);
			mName.setTapName(APIMetadataConstants.CAT_NAME);
			mName.setType(ColumnType.STRING);
			mName.setVisible(true);
			metadata.add(mName);

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
			controller.getRootPresenter().showUserRelatedMetadata(descriptor, GeneralJavaScriptObject.createJsonObject(userCatalogueJSON), shouldBeInTablePanel);

		} catch (Exception ex) {
			Log.error(ex.getMessage());
		}
	}

	private void centerAladinLite(String raDeg, String decDeg, CoordinatesFrame convertToFrame) {

		double[] raDecConverted;
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
	
	public void setOverlayColor(String overlayName, String color,  JavaScriptObject widget) {
		GeneralEntityInterface entity = EntityRepository.getInstance().getEntity(overlayName);
		if(entity != null) {
			entity.setPrimaryColor(color);
		}else {
			JSONObject callbackMessage = new JSONObject();
			callbackMessage.put("message", new JSONString("No overlay with name: " + overlayName + " active:\n Check getActiveOverlays() for available overlays"));
			sendBackToWidget(null, callbackMessage, widget);
		}
	}

	public void setOverlaySize(String overlayName, double size,  JavaScriptObject widget) {
		GeneralEntityInterface entity = EntityRepository.getInstance().getEntity(overlayName);
		if(entity != null) {
			entity.setSizeRatio(size);
		}else {
			JSONObject callbackMessage = new JSONObject();
			callbackMessage.put("message", new JSONString("No overlay with name: " + overlayName + " active:\n Check getActiveOverlays() for available overlays"));
			sendBackToWidget(null, callbackMessage, widget);
		}
	}

	public void setOverlayShape(String overlayName, String shape, JavaScriptObject widget) {
		GeneralEntityInterface entity = EntityRepository.getInstance().getEntity(overlayName);
		if(entity != null) {
			
			LinkedList<String> sourceTypes = new LinkedList<String>();
			boolean found = false;
			for(SourceShapeType s : SourceShapeType.values()) {
				if(shape.equalsIgnoreCase(s.getName())) {
					found = true;
				}else {
					sourceTypes.add(s.getName());
				}
			}
			if(found) {
				entity.setShapeType(shape);
			}else {
				JSONObject callbackMessage = new JSONObject();
				callbackMessage.put("message", new JSONString("No such shape possible. \n Available shapes are " + String.join(",", sourceTypes.toArray(new String[0]))));
				sendBackToWidget(null, callbackMessage, widget);
			}
		}else {
			JSONObject callbackMessage = new JSONObject();
			callbackMessage.put("message", new JSONString("No overlay with name: " + overlayName + " active:\n Check getActiveOverlays() for available overlays"));
			sendBackToWidget(null, callbackMessage, widget);
		}
	}
	
	public void getActiveOverlays(JavaScriptObject widget) {
		List<GeneralEntityInterface> list = EntityRepository.getInstance().getAllEntities();
	
		JSONArray arr = new JSONArray();
		for(GeneralEntityInterface ent : list) {
			arr.set(arr.size(),new JSONString(ent.getEsaSkyUniqId()));
		}
		JSONObject result = new JSONObject();
		result.put("Overlays", arr);
		sendBackToWidget(result, widget);
	}

	public void clearAllOverlays() {
		for(GeneralEntityInterface ent : EntityRepository.getInstance().getAllEntities()) {
			ent.clearAll();
			EntityRepository.getInstance().removeEntity(ent);
		}
	}
	
	public void addCustomTreeMap(GeneralJavaScriptObject input, JavaScriptObject widget) {
		String name = "Custom treeMap";
		if(input.hasProperty("name")) {
			name = input.getStringProperty("name");
		} else {
			sendBackMessageToWidget("ERROR: Missing treeMap property name", widget);
			return;
		}
		
		String description = "";
		if(input.hasProperty("description")) {
			description = input.getStringProperty("description");
		}
		
		String iconText = name;
		if(input.hasProperty("iconText")) {
			iconText = input.getStringProperty("iconText");
		}
		
		GeneralJavaScriptObject descriptorArray = input.getProperty("missions");
		if(descriptorArray == null) {
			sendBackMessageToWidget("ERROR: Missing treeMap missions", widget);
			return;
		}
		List<IDescriptor> descriptors = createTreeMapDescriptors(widget, descriptorArray);
		if(descriptors.isEmpty()) {
		    return;
		}
		CustomTreeMapDescriptor customTreeMapDescriptor = new CustomTreeMapDescriptor(name, description, iconText, descriptors);
		
		customTreeMapDescriptor.setOnMissionClicked(new CustomTreeMapDescriptor.OnMissionClicked() {
			
			@Override
			public void onMissionClicked(String mission) {
				JSONObject result = new JSONObject();
				JSONObject treeMap = new JSONObject();
				JSONObject location = new JSONObject();
				treeMap.put("treemap:" , new JSONString(customTreeMapDescriptor.getName()));
				treeMap.put("mission:" , new JSONString(mission));
				SkyViewPosition pos = CoordinateUtils.getCenterCoordinateInJ2000();
				location.put("ra", new JSONNumber(pos.getCoordinate().ra));
				location.put("dec", new JSONNumber(pos.getCoordinate().dec));
				location.put("fov", new JSONNumber(pos.getFov()));
				result.put("action", new JSONString("treemap_mission_clicked"));
				result.put("info", treeMap);
				result.put("location", location);
                sendBackToWidget(result, widget);
			}
		});
		
		controller.getRootPresenter().getCtrlTBPresenter().addCustomTreeMap(customTreeMapDescriptor);
	}

    private List<IDescriptor> createTreeMapDescriptors(JavaScriptObject widget,
            GeneralJavaScriptObject descriptorArray) {
        List<IDescriptor> descriptors = new LinkedList<>();
		int i = 0;
		while(descriptorArray.hasProperty(Integer.toString(i))) {
			GeneralJavaScriptObject mission = descriptorArray.getProperty(Integer.toString(i));
			BaseDescriptor descriptor = new BaseDescriptor() {
				@Override
				public String getIcon() {
					return null;
				}
			};
			
			String missionName = "";
			if(mission.hasProperty("name")) {
				missionName = mission.getStringProperty("name");
			} else {
				sendBackMessageToWidget("ERROR: Missing mission property \"name\"", widget);
				return descriptors;
			}
			
			String color = "";
			if(mission.hasProperty("color")) {
				color = mission.getStringProperty("color");
			} else {
				sendBackMessageToWidget("ERROR: Missing mission property \"color\"", widget);
				return descriptors;
			}
			
			descriptor.setMission(missionName);
			descriptor.setGuiShortName(missionName);
			descriptor.setGuiLongName(missionName);
			descriptor.setDescriptorId(missionName);
			descriptor.setPrimaryColor(color);
			descriptor.setUniqueIdentifierField(APIMetadataConstants.OBS_NAME);
			descriptor.setTapSTCSColumn("stcs");
			descriptor.setSampEnabled(false);
			descriptor.setFovLimit(360.0);
			descriptor.setTapTable("<not_set>");
			descriptor.setTabCount(0);
			descriptors.add(descriptor);
			i++;
		}
        return descriptors;
    }
	
	public void setModuleVisibility(GeneralJavaScriptObject data, JavaScriptObject widget) {
		try {
			String propertiesString = data.getProperties();
			String[] properties = {propertiesString};
			if(propertiesString.contains(",")) {
				properties = propertiesString.split(",");
			}
			for(String key : properties) {
				Modules.setModule(key, GeneralJavaScriptObject.convertToBoolean(data.getProperty(key)));
			}
			controller.getRootPresenter().updateModuleVisibility();
			
		}catch(Exception e) {
			String message = "Input needs to be on format {key:boolean}. \n Possible keys are: \n";
			for(String module : Modules.getModuleKeys()) {
				message += module + ", ";
			}
			message = message.substring(0, message.length() - 2);
			
			sendBackMessageToWidget(message, widget);
		}
	}
	
	public void registerFoVChangedListener(JavaScriptObject widget) {
	      CommonEventBus.getEventBus().addHandler(AladinLiteFoVChangedEvent.TYPE, 
	              fovEvent -> {
	                JSONObject result = new JSONObject();
	                JSONObject fov = new JSONObject();
	                fov.put("fov", new JSONNumber(fovEvent.getFov()));
	                fov.put("fovRa", new JSONNumber(fovEvent.getFov()));
	                fov.put("fovDec", new JSONNumber(fovEvent.getFovDec()));
	                result.put("action", new JSONString("FoV Changed"));
	                result.put("values", fov);
	                sendBackToWidget(result, widget);
	                }
          );
	}

}
