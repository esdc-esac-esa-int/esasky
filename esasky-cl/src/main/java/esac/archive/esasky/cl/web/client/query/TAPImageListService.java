package esac.archive.esasky.cl.web.client.query;

import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;

public class TAPImageListService extends AbstractTAPService {

    private static TAPImageListService instance = null;

    private TAPImageListService() {
    }

    public static TAPImageListService getInstance() {
        if (instance == null) {
            instance = new TAPImageListService();
        }
        return instance;
    }

    
    @Override
    public String getMetadataAdql(CommonTapDescriptor descriptorInput) {
    	return getMetadataAdql(descriptorInput, "");
    }
    
    @Override
    public String getMetadataAdql(CommonTapDescriptor descriptor, String filter) {
    	return "SELECT * from " + descriptor.getTableName() + " order by priority desc";
    }

	@Override
	public String getMetadataAdqlRadial(CommonTapDescriptor descriptor, SkyViewPosition conePos) {
		// TODO Auto-generated method stub
		return null;
	}
	
    public String getImageMetadata(CommonTapDescriptor descriptor, String id) {
    	return "SELECT * from " + descriptor.getTableName() + " WHERE " + EsaSkyConstants.HST_IMAGE_ID_PARAM + " = '" + id + "'";
    }
    
}