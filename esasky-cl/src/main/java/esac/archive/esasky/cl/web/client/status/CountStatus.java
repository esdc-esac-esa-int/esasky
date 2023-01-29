package esac.archive.esasky.cl.web.client.status;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.*;
import esac.archive.esasky.cl.web.client.utility.CoordinateUtils;

public class CountStatus {

    private final Map<String, CountDetails> countStatus;

    LinkedList<CountObserver> observers = new LinkedList<>();
    private long totalCount = 0;
    
    public CountStatus (CommonTapDescriptorList descriptor) {
        countStatus = new HashMap<>();
        for (CommonTapDescriptor currentDesc : descriptor.getDescriptors()) {
        	addDescriptor(currentDesc);
        }
    }
    
    public boolean containsDescriptor(CommonTapDescriptor descriptor) {
    	return countStatus.containsKey(descriptor.getId());
    }
    
    public void addDescriptor(CommonTapDescriptor descriptor) {
    	countStatus.put(descriptor.getId(), new CountDetails(0));
    }

    public void setCountDetails(CommonTapDescriptor descriptor, Integer count, Long updateTime, SkyViewPosition skyViewPosition) {
        setCount(descriptor, count);
        countStatus.get(descriptor.getId()).setUpdateTime(updateTime);
        countStatus.get(descriptor.getId()).setSkyViewPosition(skyViewPosition);
    }

    public Integer getCount(CommonTapDescriptor descriptor) {
    	if(descriptor != null && countStatus.containsKey(descriptor.getId())){
    		return countStatus.get(descriptor.getId()).getCount();
    	}else {
    		return 0;
    	}
    }
    
    public Long getTotalCount() {
    	return totalCount;
    }

    public void setCount(CommonTapDescriptor descriptor, Integer count) {
        if(countStatus.get(descriptor.getId()) == null){
        	countStatus.put(descriptor.getId(), new CountDetails(0));
        }
        countStatus.get(descriptor.getId()).setCount(count);
    }

    public Long getUpdateTime(CommonTapDescriptor descriptor) {
        return countStatus.get(descriptor.getId()).getUpdateTime();
    }

    public void setUpdateTime(CommonTapDescriptor descriptor, Long updateTime) {
        countStatus.get(descriptor.getId()).setUpdateTime(updateTime);
    }

    public void markForRemoval(CommonTapDescriptor descriptor) {
        if (countStatus.containsKey(descriptor.getId())) {
            countStatus.get(descriptor.getId()).markForRemoval();
        }
    }

    public boolean isMarkedForRemoval(CommonTapDescriptor descriptor) {
        return countStatus.get(descriptor.getId()).isMarkedForRemoval();
    }

    public SkyViewPosition getSkyViewPosition(CommonTapDescriptor descriptor) {
        return countStatus.get(descriptor.getId()).getSkyViewPosition();
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
    
    public boolean hasMoved(CommonTapDescriptor descriptor) {
    	try {
    		SkyViewPosition currPos = CoordinateUtils.getCenterCoordinateInJ2000();
    		SkyViewPosition missionPos = getSkyViewPosition(descriptor);
        	return !currPos.compare(missionPos, 0.01) && !descriptor.hasSearchArea();
    	} catch(Exception e) {
    		// Handles if the we haven't received any data yet i.e. no missionPos exists.
    		return true; 
    	}
	}
}
