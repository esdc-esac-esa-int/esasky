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

    public String getAdql(ExtTapDescriptor descriptor, String selectADQL) {
    	String adql = selectADQL;
    	
    	adql += " from " + descriptor.getTapTable() + " WHERE ";
    	
    	if(descriptor.getSearchFunction().equals("polygonIntersect")) {
    		adql += polygonIntersectSearch(descriptor);
    	}else {
    		adql += raDecCenterSearch(descriptor);
    	}
    	
    	if(descriptor.getWhereADQL() != null) {
    		adql += " AND " + descriptor.getWhereADQL();
    	}
    	Log.debug("[TAPQueryBuilder/getMetadata4ExtTap()] ADQL " + adql);
    	
    	return adql;
    }
    
    public String getAdqlNewService(ExtTapDescriptor descriptor) {
    	 String adql = descriptor.getSelectADQL();

         return getAdql(descriptor, adql);
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
    
    private String npixSearch( int norder) {
    	ArrayList<Integer> list = AladinLiteWrapper.getInstance().getVisibleNpix(norder);
    	if(list.size()>0) {
    		String constraint = "npix IN (";
    		for(int npix : list) {
    			constraint += "\'" + Integer.toString(npix) + "\',";
    		}
    		constraint = constraint.substring(0,constraint.length()-1) + ")";
    		return constraint;
    	}
    	
    	return null;
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
    
    public String getMetadataAdql(IDescriptor descriptorInput, boolean MOC) {
    	if(!MOC) {
    		return getMetadataAdql(descriptorInput);
    		
    	}else {
            ExtTapDescriptor descriptor = (ExtTapDescriptor) descriptorInput;
            String adql = "SELECT " + descriptor.getTapSTCSColumn() + " ";
        	
        	adql += " from " + descriptor.getIngestedTable();
//        	+ " WHERE ";
//            adql += npixSearch(3);
            
        	if(descriptor.getWhereADQL() != null) {
        		adql += " WHERE " + descriptor.getWhereADQL();
        	}
        	Log.debug("[TAPQueryBuilder/getMetadata4ExtTap()] ADQL " + adql);
        	
        	return adql;
    	}
    }
    
    @Override
    public String getMetadataAdql(IDescriptor descriptorInput) {
        String selectADQL = "SELECT TOP 2000 * ";

        ExtTapDescriptor descriptor = (ExtTapDescriptor) descriptorInput;
        
        if(descriptor.isInBackend()) {
        	return getAdql(descriptor, selectADQL);       
        }else {
        	return getAdqlNewService(descriptor);
        }
    }
    
    public String getCountAdql(IDescriptor descriptorInput, boolean MOC) {
    	if(!MOC) {
    		return getCountAdql(descriptorInput);
    		
    	}else {
            ExtTapDescriptor descriptor = (ExtTapDescriptor) descriptorInput;
            String adql = "SELECT DISTINCT " + EsaSkyConstants.OBSCORE_COLLECTION + ", " + EsaSkyConstants.OBSCORE_DATAPRODUCT;
        	
        	adql += " from " + descriptor.getIngestedTable() + " WHERE ";
            adql += npixSearch(3);
            
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

}