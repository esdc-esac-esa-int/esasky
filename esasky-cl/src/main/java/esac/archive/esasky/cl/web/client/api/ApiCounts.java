package esac.archive.esasky.cl.web.client.api;


import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

import esac.archive.esasky.cl.web.client.Controller;
import esac.archive.esasky.cl.web.client.repository.DescriptorRepository.DescriptorListAdapter;
import esac.archive.esasky.cl.web.client.status.CountObserver;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.utility.WavelengthUtils;
import esac.archive.esasky.cl.web.client.utility.WavelengthUtils.WavelengthName;
import esac.archive.esasky.ifcs.model.descriptor.*;

import java.util.List;
import java.util.stream.Collectors;

public class ApiCounts extends ApiBase{
	
	
	public ApiCounts(Controller controller) {
		this.controller = controller;
	}
	
	public void getAvailableObservationMissions(JavaScriptObject widget) {
		DescriptorListAdapter<ObservationDescriptor> descriptors = controller.getRootPresenter().getDescriptorRepository().getObsDescriptors();
		getAvailableMissions(descriptors, widget);
	}
	
	public void getAvailableSpectraMissions(JavaScriptObject widget) {
		DescriptorListAdapter<SpectraDescriptor> descriptors = controller.getRootPresenter().getDescriptorRepository().getSpectraDescriptors();
		getAvailableMissions(descriptors, widget);
	}
	
	public void getAvailableCatalogueMissions(JavaScriptObject widget) {
		DescriptorListAdapter<CatalogDescriptor> descriptors = controller.getRootPresenter().getDescriptorRepository().getCatDescriptors();
		getAvailableMissions(descriptors, widget);
	}
	
	public void getAvailableMissions(final DescriptorListAdapter<? extends BaseDescriptor> descriptors, JavaScriptObject widget) {
		JSONObject obsObj = new  JSONObject();

		for (BaseDescriptor currDesc : descriptors.getDescriptors()) {
			double meanWavelength = currDesc.getCenterWavelengthValue();
			List<WavelengthDescriptor> wavelengthDescriptors = currDesc.getWavelengths();

			JSONObject descObj = new JSONObject();
			WavelengthName name = WavelengthUtils.getWavelengthNameFromValue(meanWavelength);
			descObj.put(ApiConstants.WAVELENGTH, new JSONString(name.longName));
			
			double min = 10;
			double max = 0;
			for (WavelengthDescriptor wavelengthDesc : wavelengthDescriptors) {
				min = Math.min(min, wavelengthDesc.getRange().get(0));
				max = Math.max(max, wavelengthDesc.getRange().get(1));
			}
			List<JSONString> names = WavelengthUtils.getWavelengthsNameFromRange(min, max).stream()
					.map(x -> new JSONString(x.longName)).collect(Collectors.toList());
			
			JSONArray namesArr = new JSONArray();
			names.forEach(x -> namesArr.set(namesArr.size(), x));
			descObj.put(ApiConstants.WAVELENGTHS, namesArr);

			obsObj.put(currDesc.getMission(),descObj);
		}
		sendBackValuesToWidget(obsObj, widget);

	}
	
	public void getObservationsCount(JavaScriptObject widget) {
		GoogleAnalytics.sendEventWithURL(googleAnalyticsCat, GoogleAnalytics.ACT_PYESASKY_GETOBSERVATIONSCOUNT);
		DescriptorListAdapter<ObservationDescriptor> descriptors = controller.getRootPresenter().getDescriptorRepository().getObsDescriptors();
		getCounts(descriptors, widget);
	}
	
	public void getCataloguesCount(JavaScriptObject widget) {
		GoogleAnalytics.sendEventWithURL(googleAnalyticsCat, GoogleAnalytics.ACT_PYESASKY_GETCATALOGUESCOUNT);
		DescriptorListAdapter<CatalogDescriptor> descriptors = controller.getRootPresenter().getDescriptorRepository().getCatDescriptors();
		getCounts(descriptors, widget);
	}
	
	public void getSpectraCount(JavaScriptObject widget) {
		GoogleAnalytics.sendEventWithURL(googleAnalyticsCat, GoogleAnalytics.ACT_PYESASKY_GETSPECTRACOUNT);
		DescriptorListAdapter<SpectraDescriptor> descriptors = controller.getRootPresenter().getDescriptorRepository().getSpectraDescriptors();
		getCounts(descriptors, widget);
	}

	private void getCounts(final DescriptorListAdapter<? extends BaseDescriptor> descriptors, final JavaScriptObject widget) {
		final CountStatus countStatus = descriptors.getCountStatus();
		if(checkCountUpdated(descriptors)) {
			JSONObject obsCount = new  JSONObject();
			
			for (BaseDescriptor currObs : descriptors.getDescriptors()) {
				obsCount.put(currObs.getMission(), new JSONNumber(countStatus.getCount(currObs)));
			}
			
			obsCount.put(ApiConstants.COUNT_TOTAL, new JSONNumber(countStatus.getTotalCount()));		
			GoogleAnalytics.sendEventWithURL(googleAnalyticsCat, GoogleAnalytics.ACT_PYESASKY_COUNT, obsCount.toString());
			sendBackValuesToWidget(obsCount, widget);
			
		}else {
			countStatus.registerObserver(new CountObserver() {
				@Override
				public void onCountUpdate(long newCount) {
					JSONObject obsCount = new  JSONObject();
					
					for (BaseDescriptor currObs : descriptors.getDescriptors()) {
						obsCount.put(currObs.getMission(), new JSONNumber(countStatus.getCount(currObs)));
					}
					
					
					obsCount.put(ApiConstants.COUNT_TOTAL, new JSONNumber(countStatus.getTotalCount()));		
					GoogleAnalytics.sendEventWithURL(googleAnalyticsCat, GoogleAnalytics.ACT_PYESASKY_COUNT, obsCount.toString());
					sendBackValuesToWidget(obsCount, widget);
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
	
	
}
