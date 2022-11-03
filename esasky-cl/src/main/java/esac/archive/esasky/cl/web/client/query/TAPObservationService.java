package esac.archive.esasky.cl.web.client.query;

import com.allen_sauer.gwt.log.client.Log;

import esac.archive.esasky.cl.web.client.Modules;
import esac.archive.esasky.cl.web.client.utility.DeviceUtils;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.*;

public class TAPObservationService extends AbstractTAPService {

    private static TAPObservationService instance = null;

    private TAPObservationService() {
    }

    public static TAPObservationService getInstance() {
        if (instance == null) {
            instance = new TAPObservationService();
        }
        return instance;
    }

    
    @Override
    public String getMetadataAdql(CommonTapDescriptor descriptorInput) {
    	return getMetadataAdql(descriptorInput, "");
    }

    @Override
    public String getMetadataAdql(CommonTapDescriptor descriptor, String filter) {
        final String debugPrefix = "[TAPObservationService.getMetadata]";

        Log.debug(debugPrefix);
        String adql;
        
        if(Modules.getModule(EsaSkyWebConstants.MODULE_TOGGLE_COLUMNS)) {
            adql = "SELECT TOP " + DeviceUtils.getDeviceShapeLimit(descriptor)  + " *";
        } else {
            adql = "SELECT TOP " + DeviceUtils.getDeviceShapeLimit(descriptor) + " ";
            for (TapMetadataDescriptor currMetadata : descriptor.getMetadata()) {
                TapMetadataDescriptor castMetadata = currMetadata;
                adql += " " + castMetadata.getName() + ", ";
            }
    
            adql = adql.substring(0, adql.indexOf(",", adql.length() - 2));
        }
        adql += " FROM " + descriptor.getTableName() + " WHERE ";

        adql += getGeometricConstraint(descriptor);
        
        if(!"".equals(filter)) {
        	adql += " AND " + filter;
        }

        Log.debug(debugPrefix + " ADQL " + adql);
        return adql;
    }

    public String getMetadataAdqlRadial(CommonTapDescriptor descriptor, SkyViewPosition pos) {
    	String adql;

    	int top = DeviceUtils.getDeviceShapeLimit(descriptor);

    	if(Modules.getModule(EsaSkyWebConstants.MODULE_TOGGLE_COLUMNS)) {
    	    adql = "SELECT top " + top +  " * ";
    	} else {
            adql = "SELECT top " + top + " ";
            for (TapMetadataDescriptor currMetadata : descriptor.getMetadata()) {
                TapMetadataDescriptor castMetadata = currMetadata;
                adql += " " + castMetadata.getName() + ", ";
            }
            adql = adql.substring(0, adql.indexOf(",", adql.length() - 2));
    	}
        adql += " FROM " + descriptor.getTableName() + " WHERE "
        		+ "1=INTERSECTS(fov, CIRCLE(\'ICRS\', "
				+ pos.getCoordinate().getRa() + ", "  + pos.getCoordinate().getDec() + ", "
				+ pos.getFov() / 2 +"))";

        return adql;
    }
    
}