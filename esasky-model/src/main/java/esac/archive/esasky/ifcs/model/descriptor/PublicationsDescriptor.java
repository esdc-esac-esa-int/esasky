package esac.archive.esasky.ifcs.model.descriptor;

/**
 * @author ESDC team Copyright (c) 2017 - European Space Agency
 */
public class PublicationsDescriptor extends CatalogDescriptor {

    @Override
    public String getDescriptorId() {
        if(descriptorId == null || descriptorId.isEmpty()) {
            return "PUBLICATIONS_" + getMission();
        }
        return descriptorId;
    }
}
