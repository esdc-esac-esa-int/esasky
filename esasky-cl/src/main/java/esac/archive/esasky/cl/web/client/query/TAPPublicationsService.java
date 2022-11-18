package esac.archive.esasky.cl.web.client.query;

import com.allen_sauer.gwt.log.client.Log;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesConversion;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.*;

public class TAPPublicationsService extends AbstractTAPService {

    private static TAPPublicationsService instance = null;

    private TAPPublicationsService() {
    }
    
    public static TAPPublicationsService getInstance() {
        if (instance == null) {
            instance = new TAPPublicationsService();
        }
        return instance;
    }

    public static String getMetadataAdqlFromEsaSkyTap(CommonTapDescriptor descriptor, int limit, String orderBy) {
        String adql = "select top " + limit
                + " name, ra, dec, bibcount  from " + descriptor.getTableName()
                + " where bibcount>0 AND " + TAPPublicationsService.getInstance().getGeometricConstraint(descriptor);
        
        adql += " ORDER BY " + orderBy;

        Log.debug("[TAPPublicationsService/getMetadataAdql()] ADQL " + adql);

        return adql;
    }
    
    /**
     * getMetadataAdqlforSIMBAD().
     * @param descriptor Input PublicationsDescriptor.
     * @return Query in ADQL format.
     */
    public static String getMetadataAdqlforSIMBAD(CommonTapDescriptor descriptor, int limit, String orderBy) {
        String adql = "select top " + limit 
                + " main_id as name, ra, dec, nbref as bibcount from basic"
                + " where 1=CONTAINS(POINT('ICRS'," + descriptor.getRaColumn() + ", "
                + descriptor.getDecColumn() + "), ";

        String shape;
        double fovDeg = AladinLiteWrapper.getAladinLite().getFovDeg();
        if (AladinLiteWrapper.isCornersInsideHips()) {
            if (fovDeg < 1) {
                Log.debug("[TAPPublicationsService/getMetadataAdqlforSIMBAD()] FoV < 1d");
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
        adql += shape + ") and nbref > 0";

        adql += " ORDER BY " + orderBy;

        Log.debug("[TAPPublicationsService/getMetadataAdqlforSIMBAD()] ADQL " + adql);

        return adql;
    }
    
    public static String getConeSearchMetadataAdqlforSIMBAD(CommonTapDescriptor descriptor, SkyViewPosition pos, int limit, String orderBy) {
    	String adql = "select top " + limit 
    			+ " main_id as name, ra, dec, nbref as bibcount from basic"
    			+ " where 1=CONTAINS(POINT('ICRS'," + descriptor.getRaColumn() + ", "
    			+ descriptor.getDecColumn() + "), ";
    	
    	String shape = null;
 
		shape = "CIRCLE('ICRS', "
				+ pos.getCoordinate().getRa() + ","
				+ pos.getCoordinate().getDec() + ","
				+ pos.getFov() + ")";

    	adql += shape + ") and nbref > 0";
    	
    	adql += " ORDER BY " + orderBy;
    	
    	Log.debug("[TAPPublicationsService/getMetadataAdqlforSIMBAD()] ADQL " + adql);
    	
    	return adql;
    }

    public static String getSearchAreaMetadataAdqlforSIMBAD(CommonTapDescriptor descriptor, int limit, String orderBy) {
        String adql = "select top " + limit
                + " main_id as name, ra, dec, nbref as bibcount from basic"
                + " where 1=CONTAINS(POINT('ICRS'," + descriptor.getRaColumn() + ", "
                + descriptor.getDecColumn() + "), ";

        adql += descriptor.getSearchAreaShape() + ") and nbref > 0";

        adql += " ORDER BY " + orderBy;

        Log.debug("[TAPPublicationsService/getMetadataAdqlforSIMBAD()] ADQL " + adql);

        return adql;
    }
    
    @Override
    public String getMetadataAdql(CommonTapDescriptor descriptor) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getMetadataAdql(CommonTapDescriptor descriptor, String filter) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getMetadataAdqlRadial(CommonTapDescriptor descriptor, SkyViewPosition conePos) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
	protected String getGeometricConstraint(CommonTapDescriptor descriptor) {
		String adql =  "1=CONTAINS(POINT('ICRS'," + descriptor.getRaColumn() + ", "
	                + descriptor.getDecColumn() + "), ";

	        String shape = null;
	        double fovDeg = AladinLiteWrapper.getAladinLite().getFovDeg();
	        if (fovDeg < descriptor.getFovLimit()) {
	            if (fovDeg < 1) {
	                Log.debug("[TAPPublicationsService/getMetadataAdql()] FoV < 1d");
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
