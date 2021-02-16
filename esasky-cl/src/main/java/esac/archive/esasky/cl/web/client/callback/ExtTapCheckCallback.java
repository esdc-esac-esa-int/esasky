package esac.archive.esasky.cl.web.client.callback;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.http.client.Response;

import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.TreeMapNewDataEvent;
import esac.archive.esasky.cl.web.client.utility.ExtTapUtils;
import esac.archive.esasky.cl.web.client.model.TapRowList;
import esac.archive.esasky.cl.web.client.presenter.ResultsPresenter.TapRowListMapper;
import esac.archive.esasky.cl.web.client.repository.DescriptorRepository;
import esac.archive.esasky.cl.web.client.repository.DescriptorRepository.DescriptorListAdapter;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.cl.web.client.utility.CoordinateUtils;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.ExtTapDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;
import esac.archive.esasky.ifcs.model.shared.ObsCoreCollection;

public class ExtTapCheckCallback extends JsonRequestCallback {

	private long timecall;
	private CountStatus countStatus;
	private ExtTapDescriptor descriptor;
	private DescriptorListAdapter<ExtTapDescriptor> extTapDescriptors;
    private List<IDescriptor> descriptors = new LinkedList<IDescriptor>();
    private List<Integer> counts = new LinkedList<Integer>();
	

	
	public ExtTapCheckCallback(String adql, ExtTapDescriptor descriptor, CountStatus countStatus,
			String progressIndicatorMessage) {
		super(progressIndicatorMessage, adql);
		timecall = System.currentTimeMillis();
		this.countStatus = countStatus;
		this.descriptor = descriptor;
	}
	
	private boolean checkValidUpdate() {
		if(countStatus.getUpdateTime(descriptor) != null 
        		&& countStatus.getUpdateTime(descriptor) > timecall) {
        	Log.warn(this.getClass().getSimpleName() + " discarded server answer with timecall="
        			+ timecall + " , dif:" + (countStatus.getUpdateTime(descriptor) - timecall));
        	return false;
        }
		if(CoordinateUtils.getCenterCoordinateInJ2000().getFov() > EsaSkyWebConstants.EXTTAP_FOV_LIMIT){
			Log.warn(this.getClass().getSimpleName() + " discarded server answer to too large fov: "
					+ Double.toString(CoordinateUtils.getCenterCoordinateInJ2000().getFov() ));
        	return false;
		}
		return true;
	}
	
	private void logReceived(String logPrefix, int totalCount) {
	 	double timeForReceiving = (double) (System.currentTimeMillis() - timecall)/1000.0;
	 	Log.debug(logPrefix + descriptor.getGuiLongName() + " Time for response (s): "
	 			+ Double.toString(timeForReceiving));
	 	
	 	GoogleAnalytics.sendEventWithURL(GoogleAnalytics.CAT_ExternalTaps, GoogleAnalytics.ACT_ExtTap_count,
	 			descriptor.getGuiLongName() + " Time for response (s): " + Double.toString(timeForReceiving));
        
        Log.debug(logPrefix + this.getClass().getSimpleName() + " " + descriptor.getGuiLongName()
    	+ ": [" + totalCount + "] results found");
    
	}
	
	private void updateCountStatus(int totalCount) {
		SkyViewPosition skyViewPosition = CoordinateUtils.getCenterCoordinateInJ2000();
		countStatus.setUpdateTime(descriptor, timecall);
        countStatus.setCountDetails(descriptor, totalCount, System.currentTimeMillis(),
        		skyViewPosition);
	}
	
	private String findFacility(String collection) {
		String facilityName = null;

		for(String key : descriptor.getCollections().keySet()) {
			if(descriptor.getCollections().get(key).containsKey(EsaSkyConstants.OBSCORE_COLLECTION)
					&& descriptor.getCollections().get(key).get(EsaSkyConstants.OBSCORE_COLLECTION).contains(collection)) {
				facilityName = key;
				break;
			}else if(descriptor.getCollections().get(key).containsKey(EsaSkyConstants.TABLE_NAME) 
					&& descriptor.getCollections().get(key).get(EsaSkyConstants.TABLE_NAME).contains(collection)) {
				facilityName = key;
				break;
			}
		}
		
		return facilityName;
	}
	
	private String getType(ArrayList<Object> row, int typeIndex ){
		if(typeIndex >= 0) {
			return (String) row.get(typeIndex);
		}else {
			return EsaSkyConstants.CATALOGUE;
		}
		
	}
	
	private void logMissingProductType(String collection, String productType) {
		String extra = descriptor.getCreditedInstitutions() + "-" + collection + "-" + productType;
		GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_ExternalTaps,
				GoogleAnalytics.ACT_ExtTap_missingProductType, extra);
	}
	private void logMissingCollection(String collection) {
		String extra = descriptor.getCreditedInstitutions() + "-" + collection;
		GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_ExternalTaps,
				GoogleAnalytics.ACT_ExtTap_missingCollection, extra);
	}
	
	private ExtTapDescriptor addTypeDesc(String productType) {
		ExtTapDescriptor typeDesc = extTapDescriptors.getDescriptorByMissionNameCaseInsensitive(
				descriptor.getMission() + "-" + ObsCoreCollection.get(productType));
		
		if(typeDesc == null) {
			typeDesc = ExtTapUtils.createDataproductDescriptor(descriptor, productType);
		}
		
		if(!descriptors.contains(typeDesc)) {
			descriptors.add(typeDesc);
			counts.add(1);
		}
		
		return typeDesc;
	}
	
	private void addCollectionDesc(ExtTapDescriptor typeDesc, String productType, String facilityName) {
		String combinedName = ObsCoreCollection.get(productType) + "-" + facilityName;

		ExtTapDescriptor collectionDesc = extTapDescriptors.getDescriptorByMissionNameCaseInsensitive(
				descriptor.getMission() + "-" + combinedName);
		
		if(collectionDesc == null) {
			collectionDesc = ExtTapUtils.createCollectionDescriptor(descriptor, typeDesc, facilityName);
			extTapDescriptors.getDescriptors().add(collectionDesc);
		}
		
		if(!descriptors.contains(collectionDesc)) {
			descriptors.add(collectionDesc);
			counts.add(1);
			SkyViewPosition skyViewPosition = CoordinateUtils.getCenterCoordinateInJ2000();
			countStatus.setCountDetails(collectionDesc, 1, System.currentTimeMillis(),
	        		skyViewPosition);
		}
	}
	
	@Override
	protected void onSuccess(final Response response) {
		Scheduler.get().scheduleFinally(new ScheduledCommand() {
			
			@Override
			public void execute() {
				
				if(!checkValidUpdate()) {
					return;
				}
			 
			 	String logPrefix = "[ExtTapCheckCallback]";
			 
			 	
				TapRowListMapper mapper = GWT.create(TapRowListMapper.class);
				TapRowList rowList = mapper.read(response.getText());
	
				int totalCount = rowList.getData().size();
				logReceived(logPrefix, totalCount);
				updateCountStatus(totalCount);

		        
		        if(EsaSkyConstants.TREEMAP_TYPE_SERVICE.equals(descriptor.getTreeMapType())) {
		        	
		        	if(totalCount > 0) {
		        		extTapDescriptors = DescriptorRepository.getInstance().getExtTapDescriptors();
		        		
		        		int collIndex = rowList.getColumnIndex(EsaSkyConstants.OBSCORE_COLLECTION);
		        		if(collIndex == -1) {
		        			collIndex = rowList.getColumnIndex(EsaSkyConstants.HEASARC_TABLE);
		        		}
		        		
		        		int typeIndex = rowList.getColumnIndex(EsaSkyConstants.OBSCORE_DATAPRODUCT);
		        		
		        		for(ArrayList<Object> row : rowList.getData()) {
		        			String collection = (String) row.get(collIndex);
		        			String productType = getType(row, typeIndex);
	        				String facilityName = findFacility(collection);
	        				
	        				if(facilityName != null) {
	        					if(descriptor.getCollections().get(facilityName).get(EsaSkyConstants.OBSCORE_DATAPRODUCT).contains(productType)) {
		        					ExtTapDescriptor typeDesc = addTypeDesc(productType);
		        					addCollectionDesc(typeDesc, productType, facilityName);
		        					
	        					}else {
	        						logMissingProductType(collection, productType);
		        				}
	        					
	        				}else {
	        					logMissingCollection(collection);
		        			}
		        		}
		        	}
		        }
	        	
		        if(descriptors.size() > 0) {
			        descriptors.add(0,descriptor);
			        counts.add(0,totalCount);
		        }
		        countStatus.updateCount();
		        CommonEventBus.getEventBus().fireEvent(new TreeMapNewDataEvent(descriptors, counts));
			}
		});
	}
	
}
