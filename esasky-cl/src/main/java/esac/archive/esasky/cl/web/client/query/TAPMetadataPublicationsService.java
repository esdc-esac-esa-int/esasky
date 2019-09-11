package esac.archive.esasky.cl.web.client.query;

import com.allen_sauer.gwt.log.client.Log;

import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesConversion;
import esac.archive.esasky.ifcs.model.descriptor.PublicationsDescriptor;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;

public class TAPMetadataPublicationsService {

    /**
     * getMetadataAdql().
     * @param descriptor Input PublicationsDescriptor.
     * @param cs Input CountStatus
     * @return Query in ADQL format.
     */
    public static String getMetadataAdqlFromEsaSkyTap(PublicationsDescriptor descriptor, int limit, String orderBy) {
        String adql = "select top " + limit
                + " name, ra, dec, bibcount  from " + descriptor.getTapTable()
                + " where bibcount>0 AND 1=CONTAINS(POINT('ICRS'," + EsaSkyConstants.SOURCE_TAP_RA + ", "
                + EsaSkyConstants.SOURCE_TAP_DEC + "), ";

        String shape = null;
        double fovDeg = AladinLiteWrapper.getAladinLite().getFovDeg();
        if (fovDeg < descriptor.getFovLimit()) {
            if (fovDeg < 1) {
                Log.debug("[TAPMetadataPublicationsService/getMetadataAdql()] FoV < 1d");
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
        adql += shape + ")";
        
        adql += " ORDER BY " + orderBy;

        Log.debug("[TAPMetadataPublicationsService/getMetadataAdql()] ADQL " + adql);

        return adql;
    }
    
    /**
     * getMetadataAdqlforSIMBAD().
     * @param descriptor Input PublicationsDescriptor.
     * @return Query in ADQL format.
     */
    public static String getMetadataAdqlforSIMBAD(PublicationsDescriptor descriptor, int limit, String orderBy) {
        String adql = "select top " + limit 
                + " main_id as name, ra, dec, nbref as bibcount from basic"
                + " where 1=CONTAINS(POINT('ICRS'," + EsaSkyConstants.SOURCE_TAP_RA + ", "
                + EsaSkyConstants.SOURCE_TAP_DEC + "), ";

        String shape = null;
        double fovDeg = AladinLiteWrapper.getAladinLite().getFovDeg();
        if (AladinLiteWrapper.isCornersInsideHips()) {
            if (fovDeg < 1) {
                Log.debug("[TAPMetadataPublicationsService/getMetadataAdqlforSIMBAD()] FoV < 1d");
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
        adql += shape + ") and nbref > 0";

        adql += " ORDER BY " + orderBy;

        Log.debug("[TAPMetadataPublicationsService/getMetadataAdqlforSIMBAD()] ADQL " + adql);

        return adql;
    }
}
