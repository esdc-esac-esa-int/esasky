package esac.archive.esasky.ifcs.model.descriptor;

import java.util.LinkedList;
import java.util.List;

public class DS9DescriptorList  {
    List<DS9Descriptor> descriptorList = new LinkedList<>();

    public List<DS9Descriptor> getDescriptors() {
        return descriptorList;
    }

    public void setDescriptors(List<DS9Descriptor> descriptors) {
        this.descriptorList = descriptors;
    }

}
