package esac.archive.esasky.cl.web.client.query;

import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.ITapDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.TapDescriptor;

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
    public String getMetadataAdql(CommonTapDescriptor descriptor) {
        return "";
    }

    @Override
    public String getMetadataAdql(CommonTapDescriptor descriptor, String filter) {
        return "";
    }

    @Override
    public String getMetadataAdqlRadial(CommonTapDescriptor descriptor, SkyViewPosition conePos) {
        return "";
    }
}
