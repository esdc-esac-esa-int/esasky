package esac.archive.esasky.cl.web.client.query;

import com.allen_sauer.gwt.log.client.Log;

import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesConversion;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.MetadataDescriptor;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;

public class TAPSurveyService extends AbstractTAPService {

    private static TAPSurveyService instance = null;

    private TAPSurveyService() {
    }

    public static TAPSurveyService getInstance() {
        if (instance == null) {
            instance = new TAPSurveyService();
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
        String adql = "SELECT DISTINCT";
        for (MetadataDescriptor currentMetadata : descriptor.getMetadata()) {
            adql += " " + currentMetadata.getTapName() + ", ";
        }

        String parsedAdql = adql.substring(0, adql.indexOf(",", adql.length() - 2));
        parsedAdql.replace("\\s*,\\s*$", "");
        parsedAdql += " FROM " + descriptor.getTapTable() + " WHERE " + getGeometricConstraint(descriptor);
        
        parsedAdql += filter;

        parsedAdql += getOrderBy(descriptor);

        Log.debug("[TAPQueryBuilder/getMetadata4Footprints()] ADQL " + parsedAdql);
        return parsedAdql;
    }

	@Override
	public String getMetadataAdqlRadial(IDescriptor descriptor, SkyViewPosition conePos) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getGeometricConstraint(IDescriptor descriptor) {
		String adql= "1=CONTAINS(pos,";

        String shape = null;
        double fovDeg = AladinLiteWrapper.getAladinLite().getFovDeg();
        if (AladinLiteWrapper.isCornersInsideHips()) {
            if (fovDeg < 1) {
                Log.debug("[TAPQueryBuilder/getMetadata4Footprints()] FoV < 1d");
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
                double[] ccInJ2000 = CoordinatesConversion.convertPointGalacticToJ2000(
                        AladinLiteWrapper.getAladinLite().getCenterLongitudeDeg(),
                        AladinLiteWrapper.getAladinLite().getCenterLatitudeDeg());
                shape = "CIRCLE('ICRS', " + ccInJ2000[0] + "," + ccInJ2000[1] + ",90)";
            } else {
                shape = "CIRCLE('ICRS', "
                        + AladinLiteWrapper.getAladinLite().getCenterLongitudeDeg() + ","
                        + AladinLiteWrapper.getAladinLite().getCenterLatitudeDeg() + ",90)";
            }

        }
        adql += shape + ")";
        return adql;
	}
}