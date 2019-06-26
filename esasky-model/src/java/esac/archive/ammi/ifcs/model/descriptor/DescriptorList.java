package esac.archive.ammi.ifcs.model.descriptor;

import java.util.LinkedList;
import java.util.List;

public abstract class DescriptorList<T extends IDescriptor> implements IDescriptorList<T> {

    protected List<T> descriptors = new LinkedList<T>();
    private int total;

    @Override
    public List<T> getDescriptors() {
        return descriptors;
    }

    @Override
    public void setDescriptors(List<T> descriptors) {
        this.descriptors = descriptors;
    }

    @Override
    public int getTotal() {
        return total;
    }

    @Override
    public void setTotal(int total) {
        this.total = total;
    }

    @Override
    public T getDescriptorByMissionNameCaseInsensitive(String missionName) {
        for (T currentDesc : this.descriptors) {
            if (currentDesc.getMission().equalsIgnoreCase(missionName)) {
                return currentDesc;
            }
        }
        return null;
    }
}
