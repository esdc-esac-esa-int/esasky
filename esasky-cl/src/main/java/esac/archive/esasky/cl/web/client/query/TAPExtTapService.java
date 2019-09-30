package esac.archive.esasky.cl.web.client.query;

import com.allen_sauer.gwt.log.client.Log;

import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesConversion;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.ExtTapDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.CoordinateUtils;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;

public class TAPExtTapService extends AbstractMetadataService {

    private static TAPExtTapService instance = null;

    private TAPExtTapService() {
    }

    public static TAPExtTapService getInstance() {
        if (instance == null) {
            instance = new TAPExtTapService();
        }
        return instance;
    }

    public String getAdql(ExtTapDescriptor descriptor, int top) {
    	 String adql = "select top " + top + " *";

         String parsedAdql = adql;
         parsedAdql += " from " + descriptor.getTapTable() + " WHERE ";

         if(descriptor.getSearchFunction().equals("polygonIntersect")) {
        	 parsedAdql += polygonIntersectSearch(descriptor);
         }else {
        	 parsedAdql += raDecCenterSearch(descriptor);
         }
         
         if(descriptor.getWhereADQL() != null) {
         	parsedAdql += " AND " + descriptor.getWhereADQL();
         }
         Log.debug("[TAPQueryBuilder/getMetadata4ExtTap()] ADQL " + parsedAdql);

         return parsedAdql;
    }
    
    private String polygonIntersectSearch(ExtTapDescriptor descriptor) {
    	String constraint = "1=INTERSECTS(" + descriptor.getTapSTCSColumn() + ",";
        String shape = null;
        double fovDeg = AladinLiteWrapper.getAladinLite().getFovDeg();
        if (AladinLiteWrapper.isCornersInsideHips()) {
            if (fovDeg < 1) {
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
    
    private String raDecCenterSearch(ExtTapDescriptor descriptor) {
        double minRa = 999.0;
        double maxRa = -999.0;
        double minDec = 999.0;
        double maxDec = -999.0;

        if(!AladinLiteWrapper.isCornersInsideHips()) {
        	SkyViewPosition pos = CoordinateUtils.getCenterCoordinateInJ2000();
        	minRa = pos.getCoordinate().ra - pos.getFov()/2;
        	maxRa = pos.getCoordinate().ra + pos.getFov()/2;
        	minDec = pos.getCoordinate().dec - pos.getFov()/2;
        	maxDec = pos.getCoordinate().dec + pos.getFov()/2;
        	
        }else {
        	String[] fovCorners = AladinLiteWrapper.getAladinLite().getFovCorners(2).toString().split(",");
	        for(int i = 0; i < fovCorners.length - 1; i += 2) {
	        	double ra = Double.parseDouble(fovCorners[i]);
	        	double dec = Double.parseDouble(fovCorners[i+1]);
	        	if(ra < minRa) minRa = ra;
	        	if(ra > maxRa) maxRa = ra;
	        	if(dec < minDec) minDec = dec;
	        	if(dec > maxDec) maxDec = dec;
	        }
        }
        
        String adql = descriptor.getTapRaColumn() + " > " + Double.toString(minRa) + " AND " +
        		descriptor.getTapRaColumn() + " < " + Double.toString(maxRa) + "  AND " +
        		descriptor.getTapDecColumn() + " > " + Double.toString(minDec) + "  AND " +
        		descriptor.getTapDecColumn() + " < " + Double.toString(maxDec);
        
        return adql;
    }
    
    @Override
    public String getMetadataAdql(IDescriptor descriptorInput) {
        int top = 3000;
        ExtTapDescriptor descriptor = (ExtTapDescriptor) descriptorInput;
        return getAdql(descriptor, top);       
    }
    
    public String getCountAdql(IDescriptor descriptorInput) {
        int top = 1;
        ExtTapDescriptor descriptor = (ExtTapDescriptor) descriptorInput;
        return getAdql(descriptor, top);       
    }

    @Override
    public String getRetreivingDataTextKey() {
    	return "MetadataCallback_retrievingMissionData";
    }

}