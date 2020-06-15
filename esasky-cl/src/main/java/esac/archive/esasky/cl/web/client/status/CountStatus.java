package esac.archive.esasky.cl.web.client.status;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

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
    	return countStatus.containsKey(descriptor.getDescriptorId());
    }
    
    public void addDescriptor(IDescriptor descriptor) {
    	countStatus.put(descriptor.getDescriptorId(), new CountDetails(0));
    }

    public void setCountDetails(IDescriptor descriptor, Integer count, Long updateTime, SkyViewPosition skyViewPosition) {
        setCount(descriptor, count);
        countStatus.get(descriptor.getDescriptorId()).setUpdateTime(updateTime);
        countStatus.get(descriptor.getDescriptorId()).setSkyViewPosition(skyViewPosition);
    }

    public Integer getCount(IDescriptor descriptor) {
        return countStatus.get(descriptor.getDescriptorId()).getCount();
    }
    
    public Integer getTotalCount() {
    	return totalCount;
    }

    public void setCount(IDescriptor descriptor, Integer count) {
        	if(countStatus.get(descriptor.getDescriptorId()) == null){
        		countStatus.put(descriptor.getDescriptorId(), new CountDetails(0));
        	}
        countStatus.get(descriptor.getDescriptorId()).setCount(count);
    }

    public Long getUpdateTime(IDescriptor descriptor) {
        return countStatus.get(descriptor.getDescriptorId()).getUpdateTime();
    }

    public void setUpdateTime(IDescriptor descriptor, Long updateTime) {
        countStatus.get(descriptor.getDescriptorId()).setUpdateTime(updateTime);
    }

    public SkyViewPosition getSkyViewPosition(IDescriptor descriptor) {
        return countStatus.get(descriptor.getDescriptorId()).getSkyViewPosition();
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
    
    public boolean hasMoved(IDescriptor descriptor) {
    	try {
    		SkyViewPosition currPos = CoordinateUtils.getCenterCoordinateInJ2000();
    		SkyViewPosition missionPos = getSkyViewPosition(descriptor);
        	return !currPos.compare(missionPos, 0.01);
    	} catch(Exception e) {
    		// Handles if the we haven't received any data yet i.e. no missionPos exists.
    		return true; 
    	}
	}
}
