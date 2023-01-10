package esac.archive.esasky.cl.web.client.api;


import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

import esac.archive.esasky.cl.web.client.Controller;
import esac.archive.esasky.cl.web.client.query.TAPExtTapService;
import esac.archive.esasky.cl.web.client.repository.DescriptorRepository;
import esac.archive.esasky.cl.web.client.repository.DescriptorRepository.DescriptorListAdapter;
import esac.archive.esasky.cl.web.client.status.CountObserver;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.descriptor.ExtTapDescriptor;
import esac.archive.esasky.ifcs.model.shared.ESASkyColors;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;

public class ApiExtTap extends ApiBase{
	
	
	public ApiExtTap(Controller controller) {
		this.controller = controller;
	}
	
	public void extTapCount(String missionId, JavaScriptObject widget) {
		DescriptorListAdapter<ExtTapDescriptor> descriptors = controller.getRootPresenter().getDescriptorRepository().getExtTapDescriptors();
		ExtTapDescriptor desc  = descriptors.getDescriptorByMissionNameCaseInsensitive(missionId);
		
		if(desc != null ) {
			controller.getRootPresenter().getDescriptorRepository().updateCount4ExtTap(desc);
			getExtTapCount(desc, widget);
		}else {
			JSONObject error = new JSONObject();
			error.put(ApiConstants.MESSAGE, new JSONString(ApiConstants.EXTTAP_MSG_UNKNOWN + missionId));
			JSONArray available = getAvailableTapServices();
			error.put(ApiConstants.ERROR_AVAILABLE, available);
			sendBackErrorToWidget(error, widget);
		}
	}
	
	private JSONObject countToJSON(final ExtTapDescriptor parent, CountStatus countStatus) {
		JSONObject obsCount = new  JSONObject();
		
		for(ExtTapDescriptor desc : DescriptorRepository.getInstance().getExtTapDescriptors().getDescriptors()) {
			if(desc.hasParent(parent)) {
				if(countStatus.containsDescriptor(desc)) {
					obsCount.put(desc.getMission(), new JSONNumber(countStatus.getCount(desc)));
				}
			}
		}
		
		return obsCount;
	}
	
	private void getExtTapCount(final ExtTapDescriptor parent, final JavaScriptObject widget) {
		final CountStatus countStatus = controller.getRootPresenter().getDescriptorRepository().getExtTapDescriptors().getCountStatus();
		if(!countStatus.hasMoved(parent)) {
			JSONObject obsCount = countToJSON(parent, countStatus);
			
			GoogleAnalytics.sendEventWithURL(googleAnalyticsCat, GoogleAnalytics.ACT_PYESASKY_COUNT, obsCount.toString());
			sendBackValuesToWidget(obsCount, widget);
			
		}else {
			countStatus.registerObserver(new CountObserver() {
				@Override
				public void onCountUpdate(long newCount) {
					JSONObject obsCount = countToJSON(parent, countStatus);
					
					GoogleAnalytics.sendEventWithURL(googleAnalyticsCat, GoogleAnalytics.ACT_PYESASKY_COUNT, obsCount.toString());
					sendBackValuesToWidget(obsCount, widget);
					countStatus.unregisterObserver(this);
				}
			});
		}
	}
	
	public JSONArray getAvailableTapServices() {
		JSONArray tapServices = new JSONArray();
		for(ExtTapDescriptor desc : controller.getRootPresenter().getDescriptorRepository().getExtTapDescriptors().getDescriptors()) {
			if(EsaSkyConstants.TREEMAP_LEVEL_SERVICE == desc.getTreeMapLevel()) {
				tapServices.set(tapServices.size(), new JSONString(desc.getMission()));
			}
		}
		return tapServices;
	}

	public void getAvailableTapServices(JavaScriptObject widget) {
		JSONArray tapServices = getAvailableTapServices();
		sendBackSingleValueToWidget(tapServices, widget);
	}
	
	public JSONObject getAllAvailableTapMissions() {
		JSONObject tapServices = new  JSONObject();
		for(ExtTapDescriptor desc : controller.getRootPresenter().getDescriptorRepository().getExtTapDescriptors().getDescriptors()) {
			
			checkAndPutJSONObject(tapServices, desc);
		}
		return tapServices;
	}
	
	public void getAllAvailableTapMissions(JavaScriptObject widget) {
		JSONObject tapServices = getAllAvailableTapMissions();
		sendBackValuesToWidget(tapServices, widget);
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
			JSONObject error = new JSONObject();
			error.put(ApiConstants.MESSAGE, new JSONString(ApiConstants.EXTTAP_MSG_UNKNOWN  + missionId));
			JSONObject available = getAllAvailableTapMissions();
			error.put(ApiConstants.ERROR_AVAILABLE, available);
			sendBackErrorToWidget(error, widget);
		}
		
	}

	public void plotExtTap(String missionId, JavaScriptObject widget) {
		DescriptorListAdapter<ExtTapDescriptor> descriptors = controller.getRootPresenter().getDescriptorRepository().getExtTapDescriptors();
		ExtTapDescriptor desc  = descriptors.getDescriptorByMissionNameCaseInsensitive(missionId);
		
		if(desc != null ) {
			controller.getRootPresenter().getRelatedMetadata(desc);
			JSONObject callbackMessage = new JSONObject();
			callbackMessage.put(ApiConstants.MESSAGE, new JSONString("Data from TAP: " + missionId + " displayed in the ESASky"));
			sendBackToWidget(null, callbackMessage, widget);
			
		}else {
			JSONObject callbackMessage = new JSONObject();
			JSONObject error = new JSONObject();
			error.put(ApiConstants.MESSAGE, new JSONString(ApiConstants.EXTTAP_MSG_UNKNOWN + missionId));
			
			JSONObject missions = getAllAvailableTapMissions();
			error.put(ApiConstants.ERROR_AVAILABLE, missions);
			
			sendBackToWidget(null, callbackMessage, widget);
		}
	}
	
	public void plotExtTapWithDetails(String name, String tapUrl, boolean dataOnlyInView, String adql, String color, int limit, GeneralJavaScriptObject options) {
		
	    ExtTapDescriptor descriptor = controller.getRootPresenter().getDescriptorRepository().addExtTapDescriptorFromAPI(name, tapUrl, dataOnlyInView, adql);
		
	    if("".equals(color)) {
			color = ESASkyColors.getNext();
		}
		if(limit == -1) {
			limit = 3000;
		}
		descriptor.setPrimaryColor(color);
		descriptor.setShapeLimit(limit);
		
		if(options.hasProperty(ApiConstants.EXTTAP_STCS_COLUMN)) {
			descriptor.setTapSTCSColumn(options.getStringProperty(ApiConstants.EXTTAP_STCS_COLUMN));
		}
		
		if(options.hasProperty(ApiConstants.EXTTAP_RA_COLUMN)) {
		    descriptor.setTapRaColumn(options.getStringProperty(ApiConstants.EXTTAP_RA_COLUMN));
		}
		
		if(options.hasProperty(ApiConstants.EXTTAP_DEC_COLUMN)) {
		    descriptor.setTapDecColumn(options.getStringProperty(ApiConstants.EXTTAP_DEC_COLUMN));
		}
		
		if(options.hasProperty(ApiConstants.EXTTAP_INTERSECT_COLUMN)) {
			descriptor.setIntersectColumn(options.getStringProperty(ApiConstants.EXTTAP_INTERSECT_COLUMN));
		}else {
		    descriptor.setSearchFunction("cointainsPoint");
		}
		
		controller.getRootPresenter().getRelatedMetadata(descriptor);
	}

	public void plotExtTapADQL(GeneralJavaScriptObject options, JavaScriptObject widget) {
		String name;
		if(options.hasProperty(ApiConstants.EXTTAP_NAME_COLUMN)) {
			name = options.getStringProperty(ApiConstants.EXTTAP_NAME_COLUMN);
		}else {
			sendBackErrorMsgToWidget(ApiConstants.EXTTAP_MSG_MISING + ApiConstants.EXTTAP_NAME_COLUMN, widget);
			return;
		}

		String tapUrl;
		if(options.hasProperty(ApiConstants.EXTTAP_TAP_URL_COLUMN)) {
			tapUrl = options.getStringProperty(ApiConstants.EXTTAP_TAP_URL_COLUMN);
		}else {
			sendBackErrorMsgToWidget(ApiConstants.EXTTAP_MSG_MISING + ApiConstants.EXTTAP_TAP_URL_COLUMN, widget);
			return;
		}
		
		String adql;
		if(options.hasProperty(ApiConstants.EXTTAP_ADQL_COLUMN)) {
			adql = options.getStringProperty(ApiConstants.EXTTAP_ADQL_COLUMN);
		}else {
			sendBackMessageToWidget(ApiConstants.EXTTAP_MSG_MISING + ApiConstants.EXTTAP_ADQL_COLUMN, widget);
			return;
		}
		
		ExtTapDescriptor descriptor = controller.getRootPresenter().getDescriptorRepository().addExtTapDescriptorFromAPI(name, tapUrl, false, adql);
		
		if(options.hasProperty(ApiConstants.EXTTAP_COLOR_COLUMN)) {
			descriptor.setPrimaryColor(options.getStringProperty(ApiConstants.EXTTAP_COLOR_COLUMN));
		}else {
			descriptor.setPrimaryColor(ESASkyColors.getNext());
		}

		descriptor.setShapeLimit(100000);
		
		if(options.hasProperty(ApiConstants.EXTTAP_STCS_COLUMN)) {
			descriptor.setTapSTCSColumn(options.getStringProperty(ApiConstants.EXTTAP_STCS_COLUMN));
		}
		
		controller.getRootPresenter().getRelatedMetadata(descriptor, adql);
	}

	public void openExtTapPanel() {
		controller.getRootPresenter().getCtrlTBPresenter().openExtTapPanel();
	}

	public void closeExtTapPanel() {
		controller.getRootPresenter().getCtrlTBPresenter().closeExtTapPanel();
	}
	
}
