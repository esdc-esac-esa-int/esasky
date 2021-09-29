package esac.archive.esasky.ifcs.model.descriptor;


public class GwDescriptor extends BaseDescriptor {

    @Override
    public String getIcon() {
        return "galaxy";
    }
    
    @Override
    public String getDescriptorId() {
        if(descriptorId == null || descriptorId.isEmpty()) {
            return "ASTRO_GW_" + getMission();
        }
        return descriptorId;
    }
}
