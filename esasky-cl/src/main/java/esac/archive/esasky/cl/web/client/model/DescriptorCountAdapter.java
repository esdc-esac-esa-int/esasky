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
