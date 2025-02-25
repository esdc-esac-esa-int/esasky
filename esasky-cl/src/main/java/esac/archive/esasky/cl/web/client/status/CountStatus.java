/*
ESASky
Copyright (C) 2025 Henrik Norman

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

package esac.archive.esasky.cl.web.client.status;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import esac.archive.absi.modules.cl.aladinlite.widget.client.model.CoordinatesObject;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.SearchArea;
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

    public void setCountDetails(CommonTapDescriptor descriptor, Integer count, Long updateTime, SkyViewPosition skyViewPosition, SearchArea searchArea) {
        setCount(descriptor, count);
        countStatus.get(descriptor.getId()).setUpdateTime(updateTime);
        countStatus.get(descriptor.getId()).setSkyViewPosition(skyViewPosition);
        countStatus.get(descriptor.getId()).setSearchArea(searchArea);
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
        if (countStatus.containsKey(descriptor.getId())) {
            return countStatus.get(descriptor.getId()).getSkyViewPosition();
        } else {
            return CoordinateUtils.getCenterCoordinateInJ2000();
        }
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

    public boolean countStillValid(CommonTapDescriptor descriptor) {
        if (!countStatus.containsKey(descriptor.getId())) {
            return false;
        }
        SkyViewPosition currPos = CoordinateUtils.getCenterCoordinateInJ2000();
        SkyViewPosition missionPos = getSkyViewPosition(descriptor);

        if (!currPos.compare(missionPos, 0.01)) {
            return false;
        }
        SearchArea currentSearchArea = descriptor.getSearchArea();
        SearchArea previousSearchArea =  countStatus.get(descriptor.getId()).getSearchArea();
        if (currentSearchArea == null && previousSearchArea == null) {
            return true;
        }
        if (currentSearchArea == null || previousSearchArea == null) {
            return false;
        }
        CoordinatesObject[] currentCoordinates = currentSearchArea.getJ2000Coordinates();
        CoordinatesObject[] previousCoordinates = previousSearchArea.getJ2000Coordinates();
        if (currentCoordinates.length != previousCoordinates.length) {
            return false;
        }
        for (int i = 0; i < currentCoordinates.length; i++) {
            if (!currentCoordinates[i].equals(previousCoordinates[i])) {
                return false;
            }
        }
        return true;
    }
}
