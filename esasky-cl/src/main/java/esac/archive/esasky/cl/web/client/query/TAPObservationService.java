package esac.archive.esasky.cl.web.client.query;

import com.allen_sauer.gwt.log.client.Log;

import esac.archive.esasky.cl.web.client.Modules;
import esac.archive.esasky.cl.web.client.utility.DeviceUtils;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.MetadataDescriptor;

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
    public String getMetadataAdql(IDescriptor descriptorInput) {
    	return getMetadataAdql(descriptorInput, "");
    }
    
    /**
     * getMetadata4Footprints().
     * @param aladinLite Input AladinLiteWidget.
     * @param obsDescriptor Input ObservationDescriptor
     * @return Query in ADQL format.
     */
    @Override
    public String getMetadataAdql(IDescriptor descriptor, String filter) {
        final String debugPrefix = "[TAPObservationService.getMetadata]";

        Log.debug(debugPrefix);
        String adql;
        
        if(Modules.toggleColumns) {
            adql = "SELECT TOP " + DeviceUtils.getDeviceShapeLimit(descriptor)  + " *";
        } else {
            adql = "SELECT TOP " + DeviceUtils.getDeviceShapeLimit(descriptor) + " ";
            for (MetadataDescriptor currMetadata : descriptor.getMetadata()) {
                MetadataDescriptor castMetadata = currMetadata;
                adql += " " + castMetadata.getTapName() + ", ";
            }
    
            adql = adql.substring(0, adql.indexOf(",", adql.length() - 2));
        }
        adql.replace("\\s*,\\s*$", "");
        adql += " FROM " + descriptor.getTapTable() + " WHERE ";

        adql += getGeometricConstraint(descriptor);
        
        if(!"".equals(filter)) {
        	adql += " AND " + filter;
        }
        
        adql += getOrderBy(descriptor);

        Log.debug(debugPrefix + " ADQL " + adql);
        return adql;
    }
    
    public String getMetadataAdqlRadial(IDescriptor descriptor, SkyViewPosition pos) {
    	String adql;
    	
    	int top = DeviceUtils.getDeviceShapeLimit(descriptor);
    	
    	if(Modules.toggleColumns) {
    	    adql = "SELECT top " + top +  " * ";
    	} else {
            adql = "SELECT top " + top + " ";
            for (MetadataDescriptor currMetadata : descriptor.getMetadata()) {
                MetadataDescriptor castMetadata = currMetadata;
                adql += " " + castMetadata.getTapName() + ", ";
            }
            adql = adql.substring(0, adql.indexOf(",", adql.length() - 2));
    	}
        adql.replace("\\s*,\\s*$", "");
        adql += " FROM " + descriptor.getTapTable() + " WHERE "
        		+ "1=INTERSECTS(fov, CIRCLE(\'ICRS\', "
				+ Double.toString(pos.getCoordinate().ra) + ", "  +  Double.toString(pos.getCoordinate().dec) + ", "
				+ Double.toString(pos.getFov()/2) +"))";

        adql += getOrderBy(descriptor);
        
        return adql;
    }
    
}