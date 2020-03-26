package esac.archive.esasky.ifcs.model.descriptor;

/**
 * @author ESDC team Copyright (c) 2017 - European Space Agency
 */
public class SpectraDescriptor extends CommonObservationDescriptor {

    @Override
    public String getIcon() {
        return "spectra";
    }
    
    @Override
    public String getDescriptorId() {
        if(descriptorId == null || descriptorId.isEmpty()) {
            return "ASTRO_SPECTRA_" + getMission();
        }
        return descriptorId;
    }
}
