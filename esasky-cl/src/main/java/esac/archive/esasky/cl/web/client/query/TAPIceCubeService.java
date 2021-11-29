package esac.archive.esasky.cl.web.client.query;

import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;

public class TAPIceCubeService extends AbstractTAPService {

    private static TAPIceCubeService instance = null;

    private TAPIceCubeService() {
    }

    public static TAPIceCubeService getInstance() {
        if (instance == null) {
            instance = new TAPIceCubeService();
        }
        return instance;
    }


    @Override
    public String getMetadataAdql(IDescriptor descriptorInput) {
        return getMetadataAdql(descriptorInput, "");
    }

    @Override
    public String getMetadataAdql(IDescriptor descriptor, String filter) {
        return "SELECT * from " + descriptor.getTapTable() + " order by discovery_timestamp desc";
    }

    @Override
    public String getMetadataAdqlRadial(IDescriptor descriptor, SkyViewPosition conePos) {
        return null;
    }

}