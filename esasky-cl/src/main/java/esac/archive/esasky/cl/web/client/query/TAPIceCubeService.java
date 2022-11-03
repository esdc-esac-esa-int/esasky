package esac.archive.esasky.cl.web.client.query;

import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.ITapDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.TapDescriptor;

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
    public String getMetadataAdql(CommonTapDescriptor descriptorInput) {
        return getMetadataAdql(descriptorInput, "");
    }

    @Override
    public String getMetadataAdql(CommonTapDescriptor descriptor, String filter) {
        return "SELECT * from " + descriptor.getTableName() + " order by discovery_timestamp desc";
    }

    @Override
    public String getMetadataAdqlRadial(CommonTapDescriptor descriptor, SkyViewPosition conePos) {
        return null;
    }

}