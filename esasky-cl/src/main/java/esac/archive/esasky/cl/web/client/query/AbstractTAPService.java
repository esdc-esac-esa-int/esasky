package esac.archive.esasky.cl.web.client.query;

import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesConversion;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.*;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.http.client.URL;

import esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteWidget;
import esac.archive.esasky.cl.web.client.Modules;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.DeviceUtils;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;

public abstract class AbstractTAPService {

    public abstract String getMetadataAdql(CommonTapDescriptor descriptor);
    public abstract String getMetadataAdql(CommonTapDescriptor descriptor, String filter);
    public abstract String getMetadataAdqlRadial(CommonTapDescriptor descriptor, SkyViewPosition conePos);
    
  
    public String getRequestUrl(CommonTapDescriptor descriptor) {
        return EsaSkyWebConstants.TAP_CONTEXT;
    }

//    public String getUploadUrl() {
//        return EsaSkyWebConstants.EXT_TAP_UPLOAD_URL;
//    }
    
    public String getCount(final AladinLiteWidget aladinLite, CommonTapDescriptor descriptor) {
        final String tapTable = descriptor.getTableName();
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
    
    public String getMetadataFromMOCPixelsADQL(CommonTapDescriptor descriptor, String whereADQL) {
    	
    	StringBuilder adql;
    	if(Modules.getModule(EsaSkyWebConstants.MODULE_TOGGLE_COLUMNS)) {
    	    adql = new StringBuilder("select top " + DeviceUtils.getDeviceShapeLimit(descriptor) + " *");
    	} else {
    	       adql = new StringBuilder("select top " + DeviceUtils.getDeviceShapeLimit(descriptor) + " ");
    	        
    	        for (TapMetadataDescriptor currentMetadata : descriptor.getMetadata()) {
    	            if (descriptor.getDecColumn().equals(currentMetadata.getName())) {
    	                adql.append(" ").append(currentMetadata.getName()).append(" as ").append(descriptor.getDecColumn()).append(", ");
    	            } else if (descriptor.getRaColumn().equals(currentMetadata.getName())) {
    	                adql.append(" ").append(currentMetadata.getName()).append(" as ").append(descriptor.getRaColumn()).append(", ");
    	            } else {
    	                adql.append(" ").append(currentMetadata.getName());
    	                adql.append(", ");
    	            }
    	        }
    	        
    	        adql = new StringBuilder(adql.substring(0, adql.indexOf(",", adql.length() - 2)));
    	}
    	
    	adql.append(" from ").append(descriptor.getTableName()).append(whereADQL);
    	
    	Log.debug("[TAPQueryBuilder/getMetadata4Sources()] ADQL " + adql);
    	
    	return adql.toString();
    }

    protected String getGeometricConstraint(CommonTapDescriptor descriptor) {
        final String debugPrefix = "[TAPService.getGeometricConstraint]";
        String containsOrIntersect;

        if (AladinLiteWrapper.getInstance().getFovDeg() > 180 && !descriptor.hasSearchArea()) {
            return "1=1";
        }

        if (descriptor.useIntersectsPolygon()) {
            containsOrIntersect = "1=INTERSECTS(fov,";
        } else {
            containsOrIntersect = "1=CONTAINS(POINT('ICRS',"
                    + descriptor.getRaColumn() + ", " + descriptor.getDecColumn() + "), ";
        }

        String shape;
        double fovDeg = AladinLiteWrapper.getAladinLite().getFovDeg();

        if (descriptor.hasSearchArea()) {
            shape = descriptor.getSearchAreaShape();
        } else if (AladinLiteWrapper.isCornersInsideHips()) {
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

}