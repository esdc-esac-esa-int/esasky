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


import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import esac.archive.esasky.cl.web.client.Controller;
import esac.archive.esasky.cl.web.client.query.TAPExtTapService;
import esac.archive.esasky.cl.web.client.status.CountObserver;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.ExtTapUtils;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;

public class ApiExtTap extends ApiBase{
	
	
	public ApiExtTap(Controller controller) {
		this.controller = controller;
	}
	
	public void extTapCount(String missionId, JavaScriptObject widget) {;

		CommonTapDescriptor desc =  controller.getRootPresenter().getDescriptorRepository()
				.getDescriptorCountAdapter(EsaSkyWebConstants.CATEGORY_EXTERNAL)
				.getDescriptorByMission(missionId);
		
		if(desc != null ) {
			controller.getRootPresenter().getDescriptorRepository().updateCount4ExtTap(desc, null);
			getExtTapCount(desc, widget);
		}else {
			JSONObject error = new JSONObject();
			error.put(ApiConstants.MESSAGE, new JSONString(ApiConstants.EXTTAP_MSG_UNKNOWN + missionId));
			JSONArray available = getAvailableTapServices();
			error.put(ApiConstants.ERROR_AVAILABLE, available);
			sendBackErrorToWidget(error, widget);
		}
	}
	
	private JSONObject countToJSON(final CommonTapDescriptor parent, CountStatus countStatus) {
		JSONObject obsCount = new  JSONObject();
		String mission = parent.getMission();
		for(CommonTapDescriptor desc : parent.getAllChildren()) {
			if (desc.getChildren().isEmpty()) {
				String key = mission + "-" + desc.getParent().getLongName() + "-" + desc.getLongName();
				obsCount.put(key, new JSONNumber(desc.getCount()));
			}
		}

		return obsCount;

	}
	
	private void getExtTapCount(final CommonTapDescriptor parent, final JavaScriptObject widget) {
		final CountStatus countStatus = controller.getRootPresenter().getDescriptorRepository().getDescriptorCountAdapter(EsaSkyWebConstants.CATEGORY_EXTERNAL).getCountStatus();
		if(countStatus.countStillValid(parent)) {
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

		for(CommonTapDescriptor desc : controller.getRootPresenter().getDescriptorRepository().getDescriptors(EsaSkyWebConstants.CATEGORY_EXTERNAL)) {
			if(EsaSkyConstants.TREEMAP_LEVEL_SERVICE == desc.getLevel()) {
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
		for(CommonTapDescriptor desc : controller.getRootPresenter().getDescriptorRepository().getDescriptors(EsaSkyWebConstants.CATEGORY_EXTERNAL)) {
			checkAndPutJSONObject(tapServices, desc);
		}
		return tapServices;
	}
	
	public void getAllAvailableTapMissions(JavaScriptObject widget) {
		JSONObject tapServices = getAllAvailableTapMissions();
		sendBackValuesToWidget(tapServices, widget);
	}
	
	private JSONObject checkAndPutJSONObject(JSONObject object, CommonTapDescriptor desc) {
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
		CommonTapDescriptor desc = controller.getRootPresenter().getDescriptorRepository().getFirstDescriptor(EsaSkyWebConstants.CATEGORY_EXTERNAL, missionId);
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

		CommonTapDescriptor desc = ExtTapUtils.getLevelDescriptor(missionId);
		
		if(desc != null && desc.getChildren().isEmpty()) {
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
		String bulkDownloadUrl = EsaSkyWebConstants.DATA_REQUEST_URL; 
		if(options.hasProperty(ApiConstants.EXTTAP_BULK_DOWNLOAD_URL)) {
			bulkDownloadUrl = options.getStringProperty(ApiConstants.EXTTAP_BULK_DOWNLOAD_URL);
		}
		
		CommonTapDescriptor descriptor = controller.getRootPresenter().getDescriptorRepository().createCustomExternalTapDescriptor(name, tapUrl, dataOnlyInView, adql, bulkDownloadUrl, options.getStringProperty(ApiConstants.EXTTAP_BULK_DOWNLOAD_ID_COLUMN));

		if(limit == -1) {
			limit = 3000;
		}

		descriptor.setShapeLimit(limit);

		String ra = null;
		String dec = null;
		String region = null;

		if(options.hasProperty(ApiConstants.EXTTAP_STCS_COLUMN)) {
			region = options.getStringProperty(ApiConstants.EXTTAP_STCS_COLUMN);
		}

		if (options.hasProperty(ApiConstants.EXTTAP_INTERSECT_COLUMN)
				|| options.hasProperty(ApiConstants.EXTTAP_INTERSECT_COLUMN_OLD_API)) {
			region = options.getStringProperty(ApiConstants.EXTTAP_STCS_COLUMN);
		}
		
		if(options.hasProperty(ApiConstants.EXTTAP_RA_COLUMN)) {
		    ra = options.getStringProperty(ApiConstants.EXTTAP_RA_COLUMN);
		}
		
		if(options.hasProperty(ApiConstants.EXTTAP_DEC_COLUMN)) {
		    dec = options.getStringProperty(ApiConstants.EXTTAP_DEC_COLUMN);
		}

		descriptor.setMetadata(controller.getRootPresenter().getDescriptorRepository().mockSpatialMetadata(ra, dec, region));
		descriptor.setUseIntersectsPolygon(options.hasProperty(ApiConstants.EXTTAP_INTERSECT_COLUMN)
				|| options.hasProperty(ApiConstants.EXTTAP_INTERSECT_COLUMN_OLD_API));

		descriptor.setFovLimitDisabled(!dataOnlyInView);
		descriptor.setColor(color);

		controller.getRootPresenter().getRelatedMetadata(descriptor);
	}


	public void openExtTapPanelTab(String tab) {
		if(!("".equals(tab))) {
			tab = tab.toUpperCase();
		}
		controller.getRootPresenter().getCtrlTBPresenter().openExternalTapPanel(tab);
	}

	public void closeExtTapPanelTab() {
		controller.getRootPresenter().getCtrlTBPresenter().closeExtTapPanel();
	}
	
}
