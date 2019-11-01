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
import esac.archive.esasky.cl.web.client.utility.ExtTapHelper;
import esac.archive.esasky.cl.web.client.model.TapRowList;
import esac.archive.esasky.cl.web.client.presenter.ResultsPresenter.TapRowListMapper;
import esac.archive.esasky.cl.web.client.repository.DescriptorRepository;
import esac.archive.esasky.cl.web.client.repository.DescriptorRepository.DescriptorListAdapter;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.cl.web.client.utility.CoordinateUtils;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.ExtTapDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;

public class ExtTapCheckCallback extends JsonRequestCallback {

	private long timecall;
	private CountStatus countStatus;
	private ExtTapDescriptor descriptor;

	
	public ExtTapCheckCallback(String adql, ExtTapDescriptor descriptor, CountStatus countStatus,
			String progressIndicatorMessage) {
		super(progressIndicatorMessage, adql);
		timecall = System.currentTimeMillis();
		this.countStatus = countStatus;
		this.descriptor = descriptor;
	}
	
	@Override
	protected void onSuccess(final Response response) {
		Scheduler.get().scheduleFinally(new ScheduledCommand() {
			
			@Override
			public void execute() {
			 if(countStatus.getUpdateTime(descriptor.getMission()) != null 
		        		&& countStatus.getUpdateTime(descriptor.getMission()) > timecall) {
		        	Log.warn(this.getClass().getSimpleName() + " discarded server answer with timecall="
		        			+ timecall + " , dif:" + (countStatus.getUpdateTime(descriptor.getMission()) - timecall));
		        	return;
		        }
			 
			 	String logPrefix = "[ExtTapCheckCallback]";
			 
			 	double timeForReceiving = (double) (System.currentTimeMillis() - timecall)/1000.0;
			 	Log.debug(logPrefix + descriptor.getGuiLongName() + " Time for response (s): "
			 			+ Double.toString(timeForReceiving));
			 	
			 	GoogleAnalytics.sendEventWithURL(GoogleAnalytics.CAT_ExternalTaps, GoogleAnalytics.ACT_ExtTap_count,
			 			descriptor.getGuiLongName() + " Time for response (s): " + Double.toString(timeForReceiving));
		        
		        countStatus.setUpdateTime(descriptor.getMission(), timecall);
				TapRowListMapper mapper = GWT.create(TapRowListMapper.class);
				TapRowList rowList = mapper.read(response.getText());
	
				int totalCount = rowList.getData().size();
				SkyViewPosition skyViewPosition = CoordinateUtils.getCenterCoordinateInJ2000();
	
		        countStatus.setCountDetails(descriptor.getMission(), totalCount, System.currentTimeMillis(),
		        		skyViewPosition);
		        
		        Log.debug(logPrefix + this.getClass().getSimpleName() + " " + descriptor.getMission()
		        	+ ": [" + totalCount + "] results found");
		        
		        countStatus.updateCount();
		        
		        List<IDescriptor> descriptors = new LinkedList<IDescriptor>();
		        List<Integer> counts = new LinkedList<Integer>();

		        descriptors.add(descriptor);
		        counts.add(totalCount);

		        
		        if(descriptor.getTreeMapType() == EsaSkyConstants.TREEMAP_TYPE_SERVICE) {
		        	
		        	if(totalCount > 0) {
		        		DescriptorListAdapter<ExtTapDescriptor> extTapDescriptors = DescriptorRepository.getInstance().getExtTapDescriptors();
		        		
		        		
		        		int collIndex = rowList.getColumnIndex(EsaSkyConstants.OBSCORE_COLLECTION);
		        		int typeIndex = rowList.getColumnIndex(EsaSkyConstants.OBSCORE_DATAPRODUCT);
		        		for(ArrayList<Object> row : rowList.getData()) {
		        			String collection = (String) row.get(collIndex);
		        			String productType = (String) row.get(typeIndex);
		        			
		        			if(descriptor.getDataProductTypes().contains(productType)) {
		        				
		        				boolean found = false;
		        				String facilityName = "";
		        				for(String key : descriptor.getCollections().keySet()) {
		        					if(descriptor.getCollections().get(key).contains(collection)) {
		        						found = true;
		        						facilityName = key;
		        						break;
		        					}
		        				}
		        				
		        				if(found) {
		        					ExtTapDescriptor collectionDesc = extTapDescriptors.getDescriptorByMissionNameCaseInsensitive(
		        							descriptor.getMission() + "-" + facilityName);
		        					
		        					if(collectionDesc == null) {
		        						collectionDesc = ExtTapHelper.createCollectionDescriptor(descriptor, facilityName);
		        						extTapDescriptors.getDescriptors().add(collectionDesc);
		        					}
		        					
		        					if(!descriptors.contains(collectionDesc)) {
		        						descriptors.add(collectionDesc);
		        						counts.add(1);
		        					}
		        					
		        					String combinedName = facilityName + "-" + productType;
		        					ExtTapDescriptor typeDesc = extTapDescriptors.getDescriptorByMissionNameCaseInsensitive(
		        							descriptor.getMission() + "-" + combinedName);
		        					
		        					if(typeDesc == null) {
		        						typeDesc = ExtTapHelper.createDataproductDescriptor(collectionDesc, productType);
		        					}
		        					
		        					if(!descriptors.contains(typeDesc)) {
		        						descriptors.add(typeDesc);
		        						counts.add(1);
		        					}
		        					
		        				}else {
		        					String extra = descriptor.getCreditedInstitutions() + "-" + collection;
		        					GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_ExternalTaps,
		        							GoogleAnalytics.ACT_ExtTap_missingCollection, extra);
		        				}
		        			}
		        		}
		        	}
		        }
	        	
		        CommonEventBus.getEventBus().fireEvent(new TreeMapNewDataEvent(descriptors, counts));
			}
		});
	}
	
}
