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

package esac.archive.esasky.cl.web.client.model;

import esac.archive.esasky.cl.web.client.status.CountObserver;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptorList;

import java.util.List;
import java.util.stream.Collectors;

public class DescriptorCountAdapter {
    private final CountStatus countStatus;
    private final CommonTapDescriptorList tapDescriptorList;

    private final String category;

    public DescriptorCountAdapter(CommonTapDescriptorList tapDescriptorList, String category, CountObserver countObserver) {
        this.countStatus = new CountStatus(tapDescriptorList);
        this.tapDescriptorList = tapDescriptorList;
        this.category = category;

        if (countObserver != null) {
            countStatus.registerObserver(countObserver);
        }
    }

    public CountStatus getCountStatus() {
        return countStatus;
    }

    public CommonTapDescriptorList getTapDescriptorList() {
        return tapDescriptorList;
    }

    public List<CommonTapDescriptor> getDescriptors() {
        return tapDescriptorList != null ? tapDescriptorList.getDescriptors() : null;
    }

    public List<Integer> getCounts() {
        return getDescriptors().stream().map(d -> getCountStatus().getCount(d)).collect(Collectors.toList());
    }

    public Integer getCountSum() {
        return getCounts().stream().reduce(Integer::sum).orElse(null);
    }

    public boolean isZeroCount() {
        Integer sum = getCountSum();
        return sum == null || sum < 1;
    }

    public CommonTapDescriptor getDescriptorByMission(String missionName) {
        for (CommonTapDescriptor currentDesc : this.tapDescriptorList.getDescriptors()) {
            if (currentDesc.getMission().equalsIgnoreCase(missionName)) {
                return currentDesc;
            }
        }
        return null;
    }

    public CommonTapDescriptor getDescriptorByTable(String tableName) {
        for (CommonTapDescriptor currentDesc : this.tapDescriptorList.getDescriptors()) {
            if (currentDesc.getTableName().equalsIgnoreCase(tableName)) {
                return currentDesc;
            }
        }
        return null;
    }


    public String getCategory() {
        return category;
    }
}
