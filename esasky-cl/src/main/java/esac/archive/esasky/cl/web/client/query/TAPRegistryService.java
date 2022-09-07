package esac.archive.esasky.cl.web.client.query;

import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;

public class TAPRegistryService extends AbstractTAPService {

    private static TAPRegistryService instance = null;

    private TAPRegistryService() {
    }

    public static TAPRegistryService getInstance() {
        if (instance == null) {
            instance = new TAPRegistryService();
        }
        return instance;
    }

    @Override
    public String getMetadataAdql(IDescriptor descriptor) {
        return "";
    }

    @Override
    public String getMetadataAdql(IDescriptor descriptor, String filter) {
        return "";
    }

    @Override
    public String getMetadataAdqlRadial(IDescriptor descriptor, SkyViewPosition conePos) {
        return "";
    }
}
