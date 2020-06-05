package esac.archive.esasky.cl.web.client.query;

import com.allen_sauer.gwt.log.client.Log;

import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.CommonObservationDescriptor;
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

        String adql = "SELECT";
        for (MetadataDescriptor currMetadata : descriptor.getMetadata()) {
            MetadataDescriptor castMetadata = currMetadata;
            adql += " " + castMetadata.getTapName() + ", ";
        }

        String parsedAdql = adql.substring(0, adql.indexOf(",", adql.length() - 2));
        parsedAdql.replace("\\s*,\\s*$", "");
        parsedAdql += " FROM " + descriptor.getTapTable() + " WHERE ";

        parsedAdql += getGeometricConstraint(descriptor);
        
        if(filter != "") {
        	parsedAdql += " AND " + filter;
        }
        
        parsedAdql += getOrderBy(descriptor);

        Log.debug(debugPrefix + " ADQL " + parsedAdql);
        return parsedAdql;
    }
    
    public String getMetadataAdqlRadial(IDescriptor descriptorInput, SkyViewPosition pos) {
    	CommonObservationDescriptor descriptor = (CommonObservationDescriptor) descriptorInput;
    	
    	String adql = "SELECT ";

    	for (MetadataDescriptor currMetadata : descriptor.getMetadata()) {
            MetadataDescriptor castMetadata = currMetadata;
            adql += " " + castMetadata.getTapName() + ",";
        }

        String parsedAdql = adql.substring(0, adql.indexOf(",", adql.length() - 1));
        parsedAdql.replace("\\s*,\\s*$", "");
        parsedAdql += " FROM " + descriptor.getTapTable() + " WHERE "
        		+ "1=INTERSECTS(fov, CIRCLE(\'ICRS\', "
				+ Double.toString(pos.getCoordinate().ra) + ", "  +  Double.toString(pos.getCoordinate().dec) + ", "
				+ Double.toString(pos.getFov()/2) +"))";

        parsedAdql += getOrderBy(descriptor);
        
        return parsedAdql;
    }
    
}