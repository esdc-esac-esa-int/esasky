package esac.archive.esasky.ifcs.model.descriptor;

public class IceCubeDescriptor extends BaseDescriptor {

    @Override
    public String getIcon() {
        return "galaxy";
    }

    @Override
    public String getDescriptorId() {
        if(descriptorId == null || descriptorId.isEmpty()) {
            return "ASTRO_ICECUBE_" + getMission();
        }
        return descriptorId;
    }
}