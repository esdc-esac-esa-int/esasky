package esac.archive.esasky.ifcs.model.descriptor;

import java.util.List;

public interface ITapDescriptorList<T extends ITapDescriptor> {
    List<T> getDescriptors();
    int getTotal();
}
