package esac.archive.esasky.cl.web.client.query;

import com.allen_sauer.gwt.log.client.Log;

import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesConversion;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.CommonObservationDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.MetadataDescriptor;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;

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

        String adql = "SELECT DISTINCT";
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

        Log.debug(debugPrefix + " ADQL " + parsedAdql);
        return parsedAdql;
    }
    
    @Override
	protected String getGeometricConstraint(IDescriptor descriptor) {
    	final String debugPrefix = "[TAPObservationService.getGeometricConstraint]";
    	String constraint = "1=INTERSECTS(fov,";
        String shape = null;
        double fovDeg = AladinLiteWrapper.getAladinLite().getFovDeg();
        if (AladinLiteWrapper.isCornersInsideHips()) {
            if (fovDeg < 1) {
                Log.debug(debugPrefix + " FoV < 1d");
                shape = "POLYGON('ICRS', "
                        + AladinLiteWrapper.getAladinLite().getFovCorners(1).toString() + ")";

            } else {
                shape = "POLYGON('ICRS', "
                        + AladinLiteWrapper.getAladinLite().getFovCorners(2).toString() + ")";
            }
        } else {

            String cooFrame = AladinLiteWrapper.getAladinLite().getCooFrame();
            if (EsaSkyWebConstants.ALADIN_GALACTIC_COOFRAME.equalsIgnoreCase(cooFrame)) {
                // convert to J2000
                Double[] ccInJ2000 = CoordinatesConversion.convertPointGalacticToJ2000(
                        AladinLiteWrapper.getAladinLite().getCenterLongitudeDeg(),
                        AladinLiteWrapper.getAladinLite().getCenterLatitudeDeg());
                shape = "CIRCLE('ICRS', " + ccInJ2000[0] + "," + ccInJ2000[1] + ",90)";
            } else {
                shape = "CIRCLE('ICRS', "
                        + AladinLiteWrapper.getAladinLite().getCenterLongitudeDeg() + ","
                        + AladinLiteWrapper.getAladinLite().getCenterLatitudeDeg() + ",90)";
            }

        }
        return constraint + shape + ")";
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

        return parsedAdql;
    }

}