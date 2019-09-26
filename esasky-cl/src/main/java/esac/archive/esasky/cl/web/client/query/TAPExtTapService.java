package esac.archive.esasky.cl.web.client.query;

import com.allen_sauer.gwt.log.client.Log;

import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.ExtTapDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.CoordinateUtils;

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

         parsedAdql += raDecCenterSearch(descriptor);
         
         if(descriptor.getWhereADQL() != null) {
         	parsedAdql += " AND " + descriptor.getWhereADQL();
         }
         Log.debug("[TAPQueryBuilder/getMetadata4ExtTap()] ADQL " + parsedAdql);

         return parsedAdql;
    }
    
    private String raDecCenterSearch(ExtTapDescriptor descriptor) {
        String[] fovCorners = AladinLiteWrapper.getAladinLite().getFovCorners(2).toString().split(",");
        double minRa = 999.0;
        double maxRa = -999.0;
        double minDec = 999.0;
        double maxDec = -999.0;

        if(fovCorners.length < 16) {
        	SkyViewPosition pos = CoordinateUtils.getCenterCoordinateInJ2000();
        	minRa = pos.getCoordinate().ra - pos.getFov()/2;
        	maxRa = pos.getCoordinate().ra + pos.getFov()/2;
        	minDec = pos.getCoordinate().dec - pos.getFov()/2;
        	maxDec = pos.getCoordinate().dec + pos.getFov()/2;
        	
        }else {
	        
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
        int top = 10;
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