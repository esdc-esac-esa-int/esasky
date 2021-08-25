package esac.archive.esasky.cl.web.client.api;


import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;

import esac.archive.esasky.cl.web.client.Controller;
import esac.archive.esasky.cl.web.client.repository.DescriptorRepository.DescriptorListAdapter;
import esac.archive.esasky.cl.web.client.status.CountObserver;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.ifcs.model.descriptor.BaseDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.CatalogDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.ObservationDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.SpectraDescriptor;

public class ApiCounts extends ApiBase{
	
	
	public ApiCounts(Controller controller) {
		this.controller = controller;
//		NOT DONE
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
