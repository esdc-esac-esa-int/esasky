package esac.archive.esasky.cl.web.client.query;

import esac.archive.esasky.ifcs.model.coordinatesutils.Coordinate;
import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesConversion;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.MetadataDescriptor;
import esac.archive.esasky.ifcs.model.shared.ColumnType;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.http.client.URL;

import esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteWidget;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.CoordinateUtils;
import esac.archive.esasky.cl.web.client.utility.DeviceUtils;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
public abstract class AbstractTAPService {

    public abstract String getMetadataAdql(IDescriptor descriptor);
    public abstract String getMetadataAdql(IDescriptor descriptor, String filter);
    public abstract String getMetadataAdqlRadial(IDescriptor descriptor, SkyViewPosition conePos);
    
    protected int getResultsLimit(int descriptorLimit){
        
        if (DeviceUtils.isMobile()){
            return EsaSkyWebConstants.MAX_SHAPES_FOR_MOBILE;
        }
         return descriptorLimit;  
        
    }
    
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
    
    public String fetchMinMaxHeaders(IDescriptor descriptor, boolean global) {
    	String adql = "SELECT esasky_q3c_maxmin_query('" + descriptor.getTapTable() + "',";
    	 if (AladinLiteWrapper.isCornersInsideHips()) {
    		 adql += "'{" + AladinLiteWrapper.getAladinLite().getFovCorners(2).toString()+ "}','',''";
    	 }else {
    		 Coordinate coor =  CoordinateUtils.getCenterCoordinateInJ2000().getCoordinate();
    		 adql += "'','" + Double.toString(coor.ra) + "','" + Double.toString(coor.dec) + "'";
    	 }
    	 
    	 adql += ", '" +  global + "') from public.function_dummy";
    	return adql;
    }
    
    public String fetchGlobalMinMaxHeaders2(IDescriptor descriptor) {
    	String tableName = descriptor.getTapTable().replace("public", "moc_schema") + "_maxmin";
    	String adql = "select ";
    	for (MetadataDescriptor currentMetadata : descriptor.getMetadata()) {
         	if(currentMetadata.getType().equals(ColumnType.DOUBLE) || currentMetadata.getType().equals(ColumnType.INTEGER)
         			|| currentMetadata.getType().equals(ColumnType.DATETIME)) {
         		adql += "min(" + currentMetadata.getTapName() + "_minval) as " +
         				currentMetadata.getTapName() + "_min , ";
         		adql += "max(" + currentMetadata.getTapName() + "_maxval) as " +
         				currentMetadata.getTapName() + "_max , ";
         	}
         	else if (descriptor.getTapDecColumn().equals(currentMetadata.getTapName())) {
             	adql += "min(" + currentMetadata.getTapName() + "_minval) as " +
             			descriptor.getTapDecColumn() + "_min , ";
             	adql += "max(" + currentMetadata.getTapName() + "_maxval) as " +
             			descriptor.getTapDecColumn() + "_max , ";
             } 
             else if (descriptor.getTapRaColumn().equals(currentMetadata.getTapName())) {
             	adql += "min(" + currentMetadata.getTapName() + "_minval) as " +
             			descriptor.getTapRaColumn() + "_min , ";
             	adql += "max(" + currentMetadata.getTapName() + "_maxval) as " +
             			descriptor.getTapRaColumn() + "_max , ";
 	        } else {
 	        	adql += "'' as " + currentMetadata.getTapName() + "_str , ";
 	        }
    	}
    	
    	adql += " FROM " + tableName + " WHERE 1 = INTERSECTS(fov,";
    	adql += "POLYGON('ICRS', "
                + AladinLiteWrapper.getAladinLite().getFovCorners(2).toString() + "))";
    	return adql;
    }
    
    public String fetchLocalMinMaxHeaders(IDescriptor descriptor) {

        String adql = "select ";

        for (MetadataDescriptor currentMetadata : descriptor.getMetadata()) {
        	if(currentMetadata.getType().equals(ColumnType.DOUBLE) || currentMetadata.getType().equals(ColumnType.INTEGER)
        			|| currentMetadata.getType().equals(ColumnType.DATETIME)) {
        		adql += "min(" + currentMetadata.getTapName() + ") as " +
        				currentMetadata.getTapName() + "_min , ";
        		adql += "max(" + currentMetadata.getTapName() + ") as " +
        				currentMetadata.getTapName() + "_max , ";
        	}
        	else if (descriptor.getTapDecColumn().equals(currentMetadata.getTapName())) {
            	adql += "min(" + currentMetadata.getTapName() + ") as " +
            			descriptor.getTapDecColumn() + "_min , ";
            	adql += "max(" + currentMetadata.getTapName() + ") as " +
            			descriptor.getTapDecColumn() + "_max , ";
            } 
            else if (descriptor.getTapRaColumn().equals(currentMetadata.getTapName())) {
            	adql += "min(" + currentMetadata.getTapName() + ") as " +
            			descriptor.getTapRaColumn() + "_min , ";
            	adql += "max(" + currentMetadata.getTapName() + ") as " +
            			descriptor.getTapRaColumn() + "_max , ";
//            } else if (descriptor.getPolygonNameTapColumn().equals(currentMetadata.getTapName())) {
//                adql += " " + currentMetadata.getTapName() + " as "
//                        + currentMetadata.getTapName() + ", ";
//            } else if(currentMetadata.getTapName() == "filter"){
//                adql += "ARRAY_AGG(DISTINCT_FUNC(" + currentMetadata.getTapName() +")) as " + currentMetadata.getTapName() + "_lst , ";
	        } else {
	        	adql += "'' as " + currentMetadata.getTapName() + "_str , ";
	        }
        
        }

        String parsedAdql = adql.substring(0, adql.indexOf(",", adql.length() - 2));
//        String parsedAdql = adql;
        parsedAdql.replace("\\s*,\\s*$", "");
        parsedAdql += " from " + descriptor.getTapTable() + " WHERE " + getGeometricConstraint(descriptor);

        return parsedAdql;
    }
    
    public String getMetadataFromMOCPixelsADQL(IDescriptor descriptor, String whereADQL) {
//    	CatalogDescriptor descriptor = (CatalogDescriptor) descriptorInput;
//    	IDescriptor descriptor = descriptorInput;
    	
    	String adql = "select top " + getResultsLimit(descriptor.getShapeLimit()) + " ";
    	
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
    	
    	String parsedAdql = adql.substring(0, adql.indexOf(",", adql.length() - 2));
    	parsedAdql.replace("\\s*,\\s*$", "");
    	parsedAdql += " from " + descriptor.getTapTable() + whereADQL;
    	
    	Log.debug("[TAPQueryBuilder/getMetadata4Sources()] ADQL " + parsedAdql);
    	
    	return parsedAdql;
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