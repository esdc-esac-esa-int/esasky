package esac.archive.esasky.cl.web.client.callback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import esac.archive.esasky.ifcs.model.descriptor.ExtTapTreeMapLevel;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;

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
	 	
	 	GoogleAnalytics.sendEventWithURL(GoogleAnalytics.CAT_EXTERNALTAPS, GoogleAnalytics.ACT_EXTTAP_COUNT,
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
	
	private String findLevelName(HashMap<String, ExtTapTreeMapLevel> levels, String value) {

		for(Map.Entry<String, ExtTapTreeMapLevel> entry : levels.entrySet()) {
			if(entry.getValue().getValues().contains(value)) {
				return entry.getKey();
			}
		}
		
		return null;
	}
	
	private void logMissingProductType(String collection, String productType) {
		String extra = descriptor.getCreditedInstitutions() + "-" + collection + "-" + productType;
		GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_EXTERNALTAPS,
				GoogleAnalytics.ACT_EXTTAP_MISSINGPRODUCTTYPE, extra);
	}
	private void logMissingCollection(String collection) {
		String extra = descriptor.getCreditedInstitutions() + "-" + collection;
		GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_EXTERNALTAPS,
				GoogleAnalytics.ACT_EXTTAP_MISSINGCOLLECTION, extra);
	}
	
	private ExtTapDescriptor getLevelDesc(ExtTapDescriptor parent, int levelNumber, String levelName, ExtTapTreeMapLevel level) {
		ExtTapDescriptor levelDesc = extTapDescriptors.getDescriptorByMissionNameCaseInsensitive(
				parent.getGuiLongName() + "-" + levelName);
		
		if(levelDesc == null) {
			String colName;
			if(levelNumber > EsaSkyConstants.TREEMAP_LEVEL_SERVICE && levelNumber <= parent.getLevelColumnNames().size()) {
				colName = parent.getLevelColumnNames().get(levelNumber - 1); 
				levelDesc = ExtTapUtils.createLevelDescriptor(parent, levelNumber, levelName, colName, level);
			}
		}
		
		if(!descriptors.contains(levelDesc)) {
			descriptors.add(levelDesc);
			counts.add(1);
			SkyViewPosition skyViewPosition = CoordinateUtils.getCenterCoordinateInJ2000();
			countStatus.setCountDetails(levelDesc, 1, System.currentTimeMillis(), skyViewPosition);
		}
		
		return levelDesc;
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

		        
		        if(EsaSkyConstants.TREEMAP_LEVEL_SERVICE == descriptor.getTreeMapLevel()) {
		        	
		        	if(totalCount > 0) {
		        		extTapDescriptors = DescriptorRepository.getInstance().getExtTapDescriptors();
		        		
		        		int level1Index = rowList.getColumnIndex(descriptor.getLevelColumnNames().get(0));
		        		int level2Index = rowList.getColumnIndex(descriptor.getLevelColumnNames().get(1));
		        		
		        		
		        		for(ArrayList<Object> row : rowList.getData()) {
		        			createDescriptors(level1Index, level2Index, row);
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
	
	private void createDescriptors(int level1Index, int level2Index, ArrayList<Object> row) {
		String level1Value = (String) row.get(level1Index);
		String level2Value = (String) row.get(level2Index);
		String level1Name = findLevelName(descriptor.getSubLevels(), level1Value);
		
		if(level1Name != null) {
			ExtTapDescriptor descLevel1 = getLevelDesc(descriptor, 1,  level1Name, descriptor.getSubLevels().get(level1Name));
			if(descLevel1 != null) {
			String level2Name = findLevelName(descLevel1.getSubLevels(), level2Value);
				if(level2Name != null) {
					getLevelDesc(descLevel1, 2,  level2Name, descLevel1.getSubLevels().get(level2Name));
				
				}else {
					logMissingProductType(level1Value, level2Value);
				}
			}
			
		}else {
			logMissingCollection(level1Value);
		}
	}
	
}
