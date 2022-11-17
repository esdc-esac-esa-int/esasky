package esac.archive.esasky.ifcs.model.descriptor;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.LinkedList;
import java.util.List;

public class CommonTapDescriptorList  {

    @JsonProperty("descriptors")
    private List<CommonTapDescriptor> descriptors = new LinkedList<>();

    public List<CommonTapDescriptor> getDescriptors() {
        return descriptors;
    }

    public void setDescriptors(List<CommonTapDescriptor> descriptors) {
        this.descriptors = descriptors;
    }

    public int getTotal() {
        return descriptors.size();
    }
}
