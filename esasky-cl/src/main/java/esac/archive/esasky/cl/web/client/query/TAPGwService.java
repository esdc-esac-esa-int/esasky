package esac.archive.esasky.cl.web.client.query;

import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.ITapDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.TapDescriptor;

public class TAPGwService extends AbstractTAPService {

    private static TAPGwService instance = null;

    private TAPGwService() {
    }

    public static TAPGwService getInstance() {
        if (instance == null) {
            instance = new TAPGwService();
        }
        return instance;
    }


    @Override
    public String getMetadataAdql(CommonTapDescriptor descriptorInput) {
        return getMetadataAdql(descriptorInput, "");
    }

    @Override
    public String getMetadataAdql(CommonTapDescriptor descriptor, String filter) {
        return "SELECT * from " + descriptor.getTableName() + " order by iso_time desc";
    }

    @Override
    public String getMetadataAdqlRadial(CommonTapDescriptor descriptor, SkyViewPosition conePos) {
        return null;
    }

}