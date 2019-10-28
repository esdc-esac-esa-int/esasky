package esac.archive.esasky.cl.web.client.callback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.http.client.Response;

import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.TreeMapNewDataEvent;
import esac.archive.esasky.cl.web.client.model.TapRowList;
import esac.archive.esasky.cl.web.client.presenter.ResultsPresenter.TapRowListMapper;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.cl.web.client.utility.CoordinateUtils;
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
	
	private ExtTapDescriptor createTypeDescriptor(ExtTapDescriptor parent, String typeName) {
		ExtTapDescriptor typeDescriptor = new ExtTapDescriptor();
		typeDescriptor.copyParentValues(parent);
		typeDescriptor.setTreeMapType(EsaSkyConstants.TREEMAP_TYPE_DATAPRODUCT);

		typeDescriptor.setGuiShortName(typeName);
		typeDescriptor.setGuiLongName(typeDescriptor.getGuiLongName() + "-" + typeName);
		
		typeDescriptor.setMission(typeDescriptor.getMission() + "-" + typeName);
		
		String whereADQL = typeDescriptor.getWhereADQL();
		whereADQL += " AND " + EsaSkyConstants.OBSCORE_DATAPRODUCT + " = \'" + typeName + "\'";
		typeDescriptor.setWhereADQL(whereADQL);
		
		typeDescriptor.setSelectADQL("SELECT TOP 2000 *");
		
		return typeDescriptor;
	}
	
	private ExtTapDescriptor createCollectionDescriptor(String facilityName) {
		ExtTapDescriptor collectionDescriptor = new ExtTapDescriptor();
		collectionDescriptor.copyParentValues((ExtTapDescriptor) descriptor);
		collectionDescriptor.setMission(facilityName);
		collectionDescriptor.setTreeMapType(EsaSkyConstants.TREEMAP_TYPE_SUBCOLLECTION);
		
		collectionDescriptor.setGuiShortName(facilityName);
		collectionDescriptor.setGuiLongName(collectionDescriptor.getGuiLongName() + "-" + facilityName);
		collectionDescriptor.setMission(collectionDescriptor.getMission() + "-" + facilityName);
		
		String whereADQL = collectionDescriptor.getWhereADQL();
		if(whereADQL != null) {
			whereADQL += " AND ";
		}else {
			whereADQL = "";
		}
		
		whereADQL +=  EsaSkyConstants.OBSCORE_COLLECTION + " IN (";
		for(String collectionName : descriptor.getCollections().get(facilityName)) {
			whereADQL += "\'" + collectionName + "\', ";
		}
		//Remove last "," 
		whereADQL = whereADQL.substring(0, whereADQL.length() - 2);
		whereADQL += ")";
		
		collectionDescriptor.setWhereADQL(whereADQL);
		return collectionDescriptor;
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
		        
		        countStatus.setUpdateTime(descriptor.getMission(), timecall);
				TapRowListMapper mapper = GWT.create(TapRowListMapper.class);
				TapRowList rowList = mapper.read(response.getText());
	
				int totalCount = rowList.getData().size();
				SkyViewPosition skyViewPosition = CoordinateUtils.getCenterCoordinateInJ2000();
	
		        countStatus.setCountDetails(descriptor.getMission(), totalCount, System.currentTimeMillis(),
		        		skyViewPosition);
		        Log.debug(this.getClass().getSimpleName() + " " + descriptor.getMission() + ": [" + totalCount
		                + "] results found");
		        countStatus.updateCount();
	        	List<IDescriptor> descriptors = new LinkedList<IDescriptor>();
	        	descriptors.add(descriptor);
	        	List<Integer> counts = new LinkedList<Integer>();
	        	counts.add(countStatus.getCount(descriptor.getMission()));
	        	
	        	if(totalCount > 0) {
	        		HashMap<String, ExtTapDescriptor> descMap = new HashMap<>();
	        		LinkedList<String> productList = new LinkedList<String>();
	        		
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
	        					ExtTapDescriptor collectionDesc;
	        					if(!descMap.containsKey(facilityName)) {
	        						collectionDesc = createCollectionDescriptor(facilityName);
	        						descMap.put(facilityName, collectionDesc);
	        						descriptors.add(collectionDesc);
	        						counts.add(1);
	        					}else {
	        						collectionDesc = descMap.get(facilityName);
	        					}
	        					
	        					String combinedName = facilityName + "-" + productType;
	        					if(!productList.contains(combinedName)) {
	        						ExtTapDescriptor typeDesc = createTypeDescriptor(collectionDesc, productType);
	        						descriptors.add(typeDesc);
	        						counts.add(1);
	        						productList.add(combinedName);
	        					}
	        				}else {
	        					//TODO: GA Event
	        				}
	        			}
	        		}
	        	}
	        	
		        CommonEventBus.getEventBus().fireEvent(new TreeMapNewDataEvent(descriptors, counts));
			}
		});
	}
}
