package esac.archive.esasky.cl.web.client.query;

import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesConversion;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.MetadataDescriptor;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.http.client.URL;

import esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteWidget;
import esac.archive.esasky.cl.web.client.Modules;
import esac.archive.esasky.cl.web.client.model.MOCInfo;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.DeviceUtils;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
public abstract class AbstractTAPService {

    public abstract String getMetadataAdql(IDescriptor descriptor);
    public abstract String getMetadataAdql(IDescriptor descriptor, String filter);
    public abstract String getMetadataAdqlRadial(IDescriptor descriptor, SkyViewPosition conePos);
    
  
    public String getRequestUrl() {
        return EsaSkyWebConstants.TAP_CONTEXT;
    }
    
    public String getCount(final AladinLiteWidget aladinLite, IDescriptor descriptor) {
        final String tapTable = descriptor.getTapTable();
        String url = null;
        String shape = null;
        String adqlQuery = "";

        if (AladinLiteWrapper.isCornersInsideHips()) {
            shape = "POLYGON('ICRS'," + aladinLite.getFovCorners(2).toString() + ")";
            adqlQuery = "SELECT esasky_general_dynamic_count_q3c_poly_singletable('" + tapTable + "', " + shape
                    + ",   '{" + aladinLite.getFovCorners(2).toString()
                    + "}') as esasky_dynamic_count from dual";
        } else {// not accurate search based on a circle
            String cooFrame = aladinLite.getCooFrame();
            double[] ccInJ2000 = { aladinLite.getCenterLongitudeDeg(),
                    aladinLite.getCenterLatitudeDeg() };
            if (EsaSkyWebConstants.ALADIN_GALACTIC_COOFRAME.equalsIgnoreCase(cooFrame)) {
                // convert to J2000
                ccInJ2000 = CoordinatesConversion.convertPointGalacticToJ2000(
                        aladinLite.getCenterLongitudeDeg(), aladinLite.getCenterLatitudeDeg());
            }
            adqlQuery = "SELECT esasky_general_dynamic_count_q3c_circle_singletable("
                    // TAP table name
                    + "'" + tapTable + "', "
                    // centre RA in degrees [J2000]
                    + "'" + ccInJ2000[0] + "', "
                    // centre DEC in degrees [J2000]
                    + "'" + ccInJ2000[1] + "',"
                    // radius in degrees
                    + "'90') " + "as esasky_dynamic_count from dual";
        }

        Log.debug("[TAPQueryBuilder/FastCountQuery()] Fast count ADQL " + adqlQuery);
        url = TAPUtils.getTAPQuery(URL.encodeQueryString(adqlQuery), EsaSkyConstants.JSON);
        return url;
    }
    
    public String getMetadataFromMOCPixelsADQL(IDescriptor descriptor, String whereADQL) {
    	
    	String adql;
    	if(Modules.getModule(EsaSkyWebConstants.MODULE_TOGGLE_COLUMNS)) {
    	    adql = "select top " + DeviceUtils.getDeviceShapeLimit(descriptor) + " *";
    	} else {
    	       adql = "select top " + DeviceUtils.getDeviceShapeLimit(descriptor) + " ";
    	        
    	        for (MetadataDescriptor currentMetadata : descriptor.getMetadata()) {
    	            if (descriptor.getTapDecColumn().equals(currentMetadata.getTapName())) {
    	                adql += " " + currentMetadata.getTapName() + " as "
    	                        + descriptor.getTapDecColumn() + ", ";
    	            } else if (descriptor.getTapRaColumn().equals(currentMetadata.getTapName())) {
    	                adql += " " + currentMetadata.getTapName() + " as "
    	                        + descriptor.getTapRaColumn() + ", ";
    	            } else {
    	                adql += " " + currentMetadata.getTapName();
    	                adql += ", ";
    	            }
    	        }
    	        
    	        adql = adql.substring(0, adql.indexOf(",", adql.length() - 2));
    	}
    	
    	adql += " from " + descriptor.getTapTable() + whereADQL;
    	
    	Log.debug("[TAPQueryBuilder/getMetadata4Sources()] ADQL " + adql);
    	
    	return adql;
    }

    public String getMetadataFromMOCPixel(IDescriptor descriptor, MOCInfo mocInfo) {
    	
    	String adql;
    	if(Modules.getModule(EsaSkyWebConstants.MODULE_TOGGLE_COLUMNS)) {
    		adql = "select top " + DeviceUtils.getDeviceShapeLimit(descriptor) + " *";
    	} else {
    		adql = "select top " + DeviceUtils.getDeviceShapeLimit(descriptor) + " ";
    		
    		for (MetadataDescriptor currentMetadata : descriptor.getMetadata()) {
    			if (descriptor.getTapDecColumn().equals(currentMetadata.getTapName())) {
    				adql += " " + currentMetadata.getTapName() + " as "
    						+ descriptor.getTapDecColumn() + ", ";
    			} else if (descriptor.getTapRaColumn().equals(currentMetadata.getTapName())) {
    				adql += " " + currentMetadata.getTapName() + " as "
    						+ descriptor.getTapRaColumn() + ", ";
    			} else {
    				adql += " " + currentMetadata.getTapName();
    				adql += ", ";
    			}
    		}
    		
    		adql = adql.substring(0, adql.indexOf(",", adql.length() - 2));
    	}
    	
    	adql += " from " + descriptor.getTapTable();
    	
    	String raColumn = descriptor.getTapRaColumn();
    	String decColumn = descriptor.getTapDecColumn();
    	
    	String whereADQL = " WHERE q3c_ang2ipix(" + raColumn+ "," + decColumn + ") BETWEEN "+ Long.toString(new Long(mocInfo.ipix) << (60 - 2 * mocInfo.order))
		+ " AND " + Long.toString(new Long((mocInfo.ipix + 1)) << (60 - 2 * mocInfo.order));
    	
    	adql += whereADQL;
    	
    	Log.debug("[TAPQueryBuilder/getMetadata4Sources()] ADQL " + adql);
    	
    	return adql;
    }
    
    protected String getGeometricConstraint(IDescriptor descriptor) {
        final String debugPrefix = "[TAPService.getGeometricConstraint]";
        String containsOrIntersect;
        if(descriptor.getUseIntersectPolygonInsteadOfContainsPoint()) {
            containsOrIntersect = "1=INTERSECTS(fov,";
        } else {
            containsOrIntersect = "1=CONTAINS(POINT('ICRS',"
                    + descriptor.getTapRaColumn() + ", " + descriptor.getTapDecColumn() + "), ";
        }
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
        return containsOrIntersect + shape + ")";
    }
    
    protected String getOrderBy(IDescriptor descriptor) {
    	if(descriptor.getOrderBy() != null) {
    		return " ORDER BY " + descriptor.getOrderBy();
    	}
    	return "";
    }
}