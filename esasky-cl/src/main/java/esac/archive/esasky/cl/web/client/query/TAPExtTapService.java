package esac.archive.esasky.cl.web.client.query;

import java.util.ArrayList;

import com.allen_sauer.gwt.log.client.Log;

import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesConversion;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.ExtTapDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.CoordinateUtils;
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
        return shape + ")";
    }
    
    private String polygonIntersectSearch(ExtTapDescriptor descriptor) {
    	String constraint = "1=INTERSECTS(" + descriptor.getTapSTCSColumn() + ",";
    	return constraint + screenPolygon(descriptor);
    }
    
    private String npixSearch( int order) {
    	ArrayList<Integer> list = AladinLiteWrapper.getInstance().getVisibleNpix(order);
    	if(list.size()>0) {
    		String constraint = "healpix_order = " + Integer.toString(order);
    		constraint += " AND healpix_index IN (";
    		for(int ipix : list) {
    			constraint += Integer.toString(ipix) + ",";
    		}
    		constraint = constraint.substring(0,constraint.length()-1) + ")";
    		return constraint;
    	}
    	
    	return null;
    }

    
    private String cointainsPointSearch(ExtTapDescriptor descriptor) {
    	String constraint = "1=CONTAINS( POINT(\'ICRS\', " + descriptor.getTapRaColumn() + ", " + descriptor.getTapDecColumn() + "), ";
    	return constraint + screenPolygon(descriptor);
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
    public String getMocAdql(IDescriptor descriptorInput, String filter) {
        ExtTapDescriptor descriptor = (ExtTapDescriptor) descriptorInput;
        String adql = "SELECT " + EsaSkyConstants.HEALPIX_ORDER + ", " + EsaSkyConstants.HEALPIX_IPIX;
//            		+ ", " + EsaSkyConstants.HEALPIX_COUNT + " ";
    	
    	adql += " from " + descriptor.getIngestedTable();
    	adql += " WHERE ";
        adql += npixSearch(getNorderFromFov());
        
    	if(descriptor.getWhereADQL() != null) {
    		adql += " AND " + descriptor.getWhereADQL();
    	}
    	Log.debug("[TAPQueryBuilder/getMetadata4ExtTap()] ADQL " + adql);
    	
    	return adql;
    }
    
    @Override
    public String getMetadataAdql(IDescriptor descriptorInput) {
        ExtTapDescriptor descriptor = (ExtTapDescriptor) descriptorInput;
        String selectADQL = "SELECT TOP " + Integer.toString(descriptor.getShapeLimit()) + " * ";
        
        if(descriptor.isInBackend()) {
        	return getAdql(descriptor, selectADQL);       
        } else {
        	return getAdqlNewService(descriptor);
        }
    }
    
    @Override
    public String getHeaderAdql(IDescriptor descriptorInput) {
    	return "SELECT TOP 0 * FROM " + descriptorInput.getTapTable();
    }
    
    public String getCountAdql(IDescriptor descriptorInput, boolean MOC) {
    	if(!MOC) {
    		return getCountAdql(descriptorInput);
    		
    	}else {
            ExtTapDescriptor descriptor = (ExtTapDescriptor) descriptorInput;
            String adql = "SELECT DISTINCT " + EsaSkyConstants.OBSCORE_COLLECTION + ", " + EsaSkyConstants.OBSCORE_DATAPRODUCT;
        	
        	adql += " from " + descriptor.getIngestedTable() + " WHERE ";
            adql += npixSearch(getNorderFromFov());
            
        	if(descriptor.getWhereADQL() != null) {
        		adql += " AND " + descriptor.getWhereADQL();
        	}
        	Log.debug("[TAPQueryBuilder/getMetadata4ExtTap()] ADQL " + adql);
        	
        	return adql;
    	}
    }
    
    public String getCountAdql(IDescriptor descriptorInput) {
        String selectADQL = "SELECT DISTINCT " + EsaSkyConstants.OBSCORE_COLLECTION + ", " + EsaSkyConstants.OBSCORE_DATAPRODUCT;
        ExtTapDescriptor descriptor = (ExtTapDescriptor) descriptorInput;
        if(descriptor.isInBackend()) {
        	return getAdql(descriptor, selectADQL);       
        }else {
        	return getAdqlNewService(descriptor);
        }  
    }
    
    @Override
    public String getRetreivingDataTextKey() {
    	return "MetadataCallback_retrievingMissionData";
    }

	@Override
	public String getMetadataAdqlRadial(IDescriptor descriptor, SkyViewPosition conePos) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private int getNorderFromFov() {
		double fov = CoordinateUtils.getCenterCoordinateInJ2000().getFov();
		if(fov > 60) {
			return 3;
		}
		else if(fov > 40) {
			return 4;
		}
		else if(fov > 20) {
			return 5;
		}
		else if(fov > 10) {
			return 6;
		}
		else if(fov > 5) {
			return 7;
		}
		else {
			return 8;
		}
	}

	@Override
	public String getMetadataAdql(IDescriptor descriptor, String filter) {
		// TODO proper filtering
		return getMetadataAdql(descriptor);
	}

}