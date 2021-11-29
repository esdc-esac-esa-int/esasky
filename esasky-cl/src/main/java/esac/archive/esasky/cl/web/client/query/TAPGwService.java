package esac.archive.esasky.cl.web.client.query;

import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;

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
    public String getMetadataAdql(IDescriptor descriptorInput) {
        return getMetadataAdql(descriptorInput, "");
    }

    @Override
    public String getMetadataAdql(IDescriptor descriptor, String filter) {
        return "SELECT * from " + descriptor.getTapTable() + " order by iso_time desc";
    }

    @Override
    public String getMetadataAdqlRadial(IDescriptor descriptor, SkyViewPosition conePos) {
        return null;
    }

}