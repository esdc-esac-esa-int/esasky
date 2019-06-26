package esac.archive.ammi.ifcs.model.descriptor;

import java.util.List;

public interface IDescriptorList<T extends IDescriptor> {

    public List<T> getDescriptors();

    public void setDescriptors(List<T> descriptors);

    public int getTotal();

    public void setTotal(int total);

    public T getDescriptorByMissionNameCaseInsensitive(String missionName);
}
