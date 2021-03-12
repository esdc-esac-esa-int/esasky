package esac.archive.esasky.ifcs.model.descriptor;

/**
 * @author ESDC team Copyright (c) 2017 - European Space Agency
 */
public class UserCatalogueDescriptor extends CatalogDescriptor {
    
    @Override
    public String getDescriptorId() {
        if(descriptorId == null || descriptorId.isEmpty()) {
            return getMission();
        }
        return descriptorId;
    }
    
    @Override
    public String generateId() {
         return getDescriptorId();
    }
}
