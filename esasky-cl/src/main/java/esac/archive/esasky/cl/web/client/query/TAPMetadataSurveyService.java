package esac.archive.esasky.cl.web.client.query;

import com.allen_sauer.gwt.log.client.Log;

import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesConversion;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.MetadataDescriptor;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;

public class TAPMetadataSurveyService extends AbstractMetadataService {

    private static TAPMetadataSurveyService instance = null;

    private TAPMetadataSurveyService() {
    }

    public static TAPMetadataSurveyService getInstance() {
        if (instance == null) {
            instance = new TAPMetadataSurveyService();
        }
        return instance;
    }

    /**
     * getMetadata4Footprints().
     * @param aladinLite Input AladinLiteWidget.
     * @param obsDescriptor Input ObservationDescriptor
     * @return Query in ADQL format.
     */
    @Override
    public String getMetadataAdql(IDescriptor descriptor) {
        String adql = "SELECT DISTINCT";
        for (MetadataDescriptor currentMetadata : descriptor.getMetadata()) {
            adql += " " + currentMetadata.getTapName() + ", ";
        }

        String parsedAdql = adql.substring(0, adql.indexOf(",", adql.length() - 2));
        parsedAdql.replace("\\s*,\\s*$", "");
        parsedAdql += " FROM " + descriptor.getTapTable() + " WHERE 1=CONTAINS(pos,";

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
        parsedAdql += shape + ")";

        Log.debug("[TAPQueryBuilder/getMetadata4Footprints()] ADQL " + parsedAdql);
        return parsedAdql;
    }

	@Override
	public String getMetadataAdqlRadial(IDescriptor descriptor, SkyViewPosition conePos) {
		// TODO Auto-generated method stub
		return null;
	}
}