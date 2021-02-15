package esac.archive.esasky.cl.web.client.query;

import com.allen_sauer.gwt.log.client.Log;

import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesConversion;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.ExtTapDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.CoordinateUtils;
import esac.archive.esasky.cl.web.client.utility.DeviceUtils;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;

public class TAPExtTapService extends AbstractTAPService {

    private static TAPExtTapService instance = null;

    private TAPExtTapService() {
    }

    public static TAPExtTapService getInstance() {
        if (instance == null) {
            instance = new TAPExtTapService();
        }
        return instance;
    }
    
    @Override
    public String getRequestUrl() {
        return EsaSkyWebConstants.EXT_TAP_REQUEST_URL;
    }

    public String getAdql(ExtTapDescriptor descriptor, String selectADQL) {
    	String adql = selectADQL;
    	
    	adql += " from " + descriptor.getTapTable();
    	
    	if(descriptor.getSearchFunction().equals("polygonIntersect")) {
    		adql +=  " WHERE " + polygonIntersectSearch(descriptor);
    		
    	}else if(descriptor.getSearchFunction().equals("cointainsPoint")){
    		adql += " WHERE " + cointainsPointSearch(descriptor);
    		
	    }else if(descriptor.getSearchFunction().equals("raDecCenterSearch") ){
	    	adql += " WHERE " + raDecCenterSearch(descriptor);
	    
	    }else if(descriptor.getSearchFunction().equals("heasarc") ){
	    	adql += " WHERE " + heasarcSearch(descriptor);
	    }
    	
    	if(descriptor.getWhereADQL() != null) {
    		if(adql.contains("WHERE")) {
    			adql += " AND ";
    		}else {
    			adql += " WHERE ";
    		}
    		adql += descriptor.getWhereADQL();
    	}
    	if(descriptor.getDateADQL() != null) {
    		if(adql.contains("WHERE")) {
    			adql += " AND ";
    		}else {
    			adql += " WHERE ";
    		}
    		adql += descriptor.getDateADQL();
    	}
    	
       	if(descriptor.getOrderBy() != null) {
    		adql += " ORDER BY " + descriptor.getOrderBy();
    	}
    	
    	Log.debug("[TAPQueryBuilder/getMetadata4ExtTap()] ADQL " + adql);
    	
    	return adql;
    }
    
    public String getAdqlNewService(ExtTapDescriptor descriptor) {
    	 String adql = descriptor.getSelectADQL();

         return getAdql(descriptor, adql);
    }
    
    private String screenPolygon(ExtTapDescriptor descriptor) {
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
        return shape + ")";
    }
    
    private String polygonIntersectSearch(ExtTapDescriptor descriptor) {
    	String constraint = "1=INTERSECTS(" + descriptor.getIntersectColumn() + ",";
    	return constraint + screenPolygon(descriptor);
    }
    
    private String cointainsPointSearch(ExtTapDescriptor descriptor) {
    	String constraint = "1=CONTAINS( POINT(\'ICRS\', " + descriptor.getTapRaColumn() + ", " + descriptor.getTapDecColumn() + "), ";
    	return constraint + screenPolygon(descriptor);
    }
    
    private String heasarcSearch(ExtTapDescriptor descriptor) {
    	SkyViewPosition pos = CoordinateUtils.getCenterCoordinateInJ2000();
    	double fov = pos.getFov();
    	double ra = pos.getCoordinate().ra;
    	double dec = pos.getCoordinate().dec;
    	String constraint = " POWER(SIN((radians("+ descriptor.getTapDecColumn() + ") - radians(" + Double.toString(dec) + "))/2),2)"
    			+ "+ cos(radians("+ descriptor.getTapDecColumn() + ")) * cos(radians(" + Double.toString(dec) + "))"
    			+ "* POWER(SIN((radians(" + descriptor.getTapRaColumn() + ") - radians(" + Double.toString(ra) + "))/2),2)"
    			+ "< POWER((radians(" + Double.toString(fov) +")/2),2) AND "+ descriptor.getTapDecColumn()
    			+ " BETWEEN " + Double.toString(dec - fov) + " AND " + Double.toString(dec + fov);
    	return constraint;
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
        ExtTapDescriptor descriptor = (ExtTapDescriptor) descriptorInput;
        String selectADQL = "SELECT TOP " + Integer.toString(DeviceUtils.getDeviceShapeLimit(descriptor)) + " * ";
        
        if(descriptor.isInBackend()) {
        	return getAdql(descriptor, selectADQL);       
        } else {
        	return getAdqlNewService(descriptor);
        }
    }	
    
    public String getCountAdql(IDescriptor descriptorInput) {
    	ExtTapDescriptor descriptor = (ExtTapDescriptor) descriptorInput;
		if("heasarc".equals(descriptor.getSearchFunction())) {
			return getHeasarcCountAdql(descriptor);
		}
		return getObsCoreCountAdql(descriptor);
    		
    }
    
    public String getObsCoreCountAdql(IDescriptor descriptorInput) {
    	
        String selectADQL = "SELECT DISTINCT " + EsaSkyConstants.OBSCORE_COLLECTION + ", " + EsaSkyConstants.OBSCORE_DATAPRODUCT;
        ExtTapDescriptor descriptor = (ExtTapDescriptor) descriptorInput;
        if(descriptor.isInBackend()) {
        	return getAdql(descriptor, selectADQL);       
        }else {
        	return getAdqlNewService(descriptor);
        }  
    }
    
    public String getHeasarcCountAdql(IDescriptor descriptorInput) {
    	
    	ExtTapDescriptor descriptor = (ExtTapDescriptor) descriptorInput;
    	String adql = "SELECT table_name, count(*) ";
    	adql = getAdql(descriptor, adql);
    	adql += " group by table_name";
    	return adql;
    }
    
	@Override
	public String getMetadataAdqlRadial(IDescriptor descriptor, SkyViewPosition conePos) {
		return null;
	}

	@Override
	public String getMetadataAdql(IDescriptor descriptor, String filter) {
		return getMetadataAdql(descriptor);
	}

}