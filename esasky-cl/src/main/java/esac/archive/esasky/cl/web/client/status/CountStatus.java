package esac.archive.esasky.cl.web.client.status;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptorList;
import esac.archive.esasky.cl.web.client.utility.CoordinateUtils;

public class CountStatus {

    private Map<String, CountDetails> countStatus;

    LinkedList<CountObserver> observers = new LinkedList<CountObserver>();
    private int totalCount = 0;
    
    public CountStatus (IDescriptorList<?> descriptor) {
        countStatus = new HashMap<String, CountDetails>();
        for (IDescriptor currentDesc : descriptor.getDescriptors()) {
        	addDescriptor(currentDesc);
        }
    }
    
    public boolean containsDescriptor(IDescriptor descriptor) {
    	return countStatus.containsKey(descriptor.getMission().toLowerCase());
    }
    
    public void addDescriptor(IDescriptor descriptor) {
    	countStatus.put(descriptor.getMission().toLowerCase(), new CountDetails(0));
    }

    public CountDetails getDetailsByKey(String missionId) {
        return countStatus.get(missionId.toLowerCase());
    }

    public Set<String> getKeys() {
    	return countStatus.keySet();
    }

    public void setCountDetails(String missionId, Integer count, Long updateTime, SkyViewPosition skyViewPosition) {
        setCount(missionId, count);
        countStatus.get(missionId.toLowerCase()).setUpdateTime(updateTime);
        countStatus.get(missionId.toLowerCase()).setSkyViewPosition(skyViewPosition);
    }

    public Integer getCount(String missionId) {
        return countStatus.get(missionId.toLowerCase()).getCount();
    }
    
    public Integer getTotalCount() {
    	return totalCount;
    }

    public void setCount(String missionId, Integer count) {
        	if(countStatus.get(missionId.toLowerCase()) == null){
        		countStatus.put(missionId.toLowerCase(), new CountDetails(0));
        	}
        countStatus.get(missionId.toLowerCase()).setCount(count);
//        updateCount();
    }

    public Long getUpdateTime(String missionId) {
        return countStatus.get(missionId.toLowerCase()).getUpdateTime();
    }

    public void setUpdateTime(String missionId, Long updateTime) {
        countStatus.get(missionId.toLowerCase()).setUpdateTime(updateTime);
    }

    public SkyViewPosition getSkyViewPosition(String missionId) {
        return countStatus.get(missionId.toLowerCase()).getSkyViewPosition();
    }

    public void setSkyViewPosition(String missionId, SkyViewPosition skyViewPosition) {
        countStatus.get(missionId.toLowerCase()).setSkyViewPosition(skyViewPosition);
    }
    
    public void updateCount(){
        	totalCount = 0;
        	for(String key : countStatus.keySet()){
        		totalCount += countStatus.get(key).getCount();
        	}
        notifyObservers();
    }
    
    public void registerObserver(CountObserver observer){
        observers.add(observer);
    }

    public void unregisterObserver(CountObserver observer){
    	observers.remove(observer);
    }

    public boolean hasObserver(CountObserver observer){
    	return observers.contains(observer);
    }
    
    public void notifyObservers(){
        	for(CountObserver observer : observers){
        		observer.onCountUpdate(totalCount);
        	}
    }
    
    public boolean hasMoved(String missionId) {
    	try {
    		SkyViewPosition currPos = CoordinateUtils.getCenterCoordinateInJ2000();
    		SkyViewPosition missionPos = getSkyViewPosition(missionId);
        	return !currPos.compare(missionPos, 0.01);
    	} catch(Exception e) {
    		// Handles if the we haven't received any data yet i.e. no missionPos exists.
    		return true; 
    	}
	}
}
