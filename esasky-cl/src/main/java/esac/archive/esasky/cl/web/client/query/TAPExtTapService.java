package esac.archive.esasky.cl.web.client.query;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.http.client.URL;

import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesConversion;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;
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

    public String getAdql(CommonTapDescriptor descriptor, String selectADQL) {
    	String adql = selectADQL;

		String tapTable = descriptor.getTableName();

		// Handle tables with non-alphanumeric characters (excluding ".")
		if (tapTable.matches("^[a-zA-Z0-9]*$") || tapTable.contains(".")) {
			adql += " FROM " + descriptor.getTableName();
		} else {
			adql += " FROM \"" + tapTable + "\"";
		}

        if (!descriptor.isFovLimitDisabled()) {
            if(descriptor.useIntersectsPolygon()) {
                adql +=  " WHERE " + polygonIntersectSearch(descriptor);
            } else {
                adql += " WHERE " + cointainsPointSearch(descriptor);
            }
        }


    	if(descriptor.getWhereADQL() != null) {
    		if(adql.contains("WHERE")) {
    			adql += " AND ";
    		}else {
    			adql += " WHERE ";
    		}
    		adql += descriptor.getWhereADQL();
    	}
    	Log.debug("[TAPQueryBuilder/getMetadata4ExtTap()] ADQL " + adql);

    	return adql;
    }

    private String screenPolygon(CommonTapDescriptor descriptor) {
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

    private String polygonIntersectSearch(CommonTapDescriptor descriptor) {
    	String constraint = "1=INTERSECTS(" + descriptor.getRegionColumn() + ",";
    	return constraint + screenPolygon(descriptor);
    }

    private String cointainsPointSearch(CommonTapDescriptor descriptor) {
    	String constraint = "1=CONTAINS( POINT('ICRS', " + descriptor.getRaColumn() + ", " + descriptor.getDecColumn() + "), ";
    	return constraint + screenPolygon(descriptor);
    }

    private String heasarcSearch(ExtTapDescriptor descriptor) {
    	SkyViewPosition pos = CoordinateUtils.getCenterCoordinateInJ2000();
    	double fov = pos.getFov();
    	double ra = pos.getCoordinate().getRa();
    	double dec = pos.getCoordinate().getDec();
    	String constraint = " POWER(SIN((radians("+ descriptor.getTapDecColumn() + ") - radians(" + dec + "))/2),2)"
    			+ "+ cos(radians("+ descriptor.getTapDecColumn() + ")) * cos(radians(" + dec + "))"
    			+ "* POWER(SIN((radians(" + descriptor.getTapRaColumn() + ") - radians(" + ra + "))/2),2)"
    			+ "< POWER((radians(" + fov +")/2),2) AND "+ descriptor.getTapDecColumn()
    			+ " BETWEEN " + (dec - fov) + " AND " + (dec + fov);
    	return constraint;
    }

    private String raDecCenterSearch(ExtTapDescriptor descriptor) {
        double minRa = 999.0;
        double maxRa = -999.0;
        double minDec = 999.0;
        double maxDec = -999.0;

        if(!AladinLiteWrapper.isCornersInsideHips()) {
        	SkyViewPosition pos = CoordinateUtils.getCenterCoordinateInJ2000();
        	minRa = pos.getCoordinate().getRa() - pos.getFov()/2;
        	maxRa = pos.getCoordinate().getRa() + pos.getFov()/2;
        	minDec = pos.getCoordinate().getDec() - pos.getFov()/2;
        	maxDec = pos.getCoordinate().getDec() + pos.getFov()/2;

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

        String adql = descriptor.getTapRaColumn() + " > " + minRa + " AND " +
        		descriptor.getTapRaColumn() + " < " + maxRa + "  AND " +
        		descriptor.getTapDecColumn() + " > " + minDec + "  AND " +
        		descriptor.getTapDecColumn() + " < " + maxDec;

        return adql;
    }

    @Override
    public String getMetadataAdql(CommonTapDescriptor descriptor) {
        String selectADQL = "SELECT TOP " + DeviceUtils.getDeviceShapeLimit(descriptor) + " * ";

        if(!descriptor.isExternal()) {
        	return URL.encodeQueryString(getAdql(descriptor, selectADQL));
        } else {
        	return getAdql(descriptor, descriptor.getSelectADQL());
        }
    }

//    public String getCountAdql(IDescriptor descriptorInput) {
//    	ExtTapDescriptor descriptor = (ExtTapDescriptor) descriptorInput;
//		if("heasarc".equals(descriptor.getSearchFunction())) {
//			return URL.encodeQueryString(getHeasarcCountAdql(descriptor));
//		}
//		return URL.encodeQueryString(getDefaultCountAdql(descriptor));
//
//    }

//    public String getObsCoreCountAdql(IDescriptor descriptorInput) {
//
//        String selectADQL = "SELECT DISTINCT " + EsaSkyConstants.OBSCORE_COLLECTION + ", " + EsaSkyConstants.OBSCORE_DATAPRODUCT;
//        ExtTapDescriptor descriptor = (ExtTapDescriptor) descriptorInput;
//        if(descriptor.isInBackend()) {
//        	return getAdql(descriptor, selectADQL);
//        }else {
//        	return getAdqlNewService(descriptor);
//        }
//    }

//    public String getDefaultCountAdql(CommonTapDescriptor descriptor) {
//
//    	ExtTapDescriptor descriptor = (ExtTapDescriptor) descriptorInput;
//    	String selectADQL = "SELECT count(*) as c";
//    	for(String column : descriptor.getLevelColumnNames()) {
//    		selectADQL += ", " + column;
//    	}
//    	String adql;
//    	if(descriptor.isInBackend()) {
//    		adql = getAdql(descriptor, selectADQL);
//    	}else {
//    		adql = getAdqlNewService(descriptor);
//    	}
//    	adql += " group by ";
//    	boolean first = true;
//    	for(String column : descriptor.getLevelColumnNames()) {
//    		if(first) {
//    			first = false;
//    		}else {
//    			adql += ", ";
//    		}
//    			adql += column;
//    	}
//
//      	if(descriptor.getOrderBy() != null) {
//    		adql += " ORDER BY " + descriptor.getOrderBy();
//    	}
//
//    	return adql;
//    }

//    public String getHeasarcCountAdql(IDescriptor descriptorInput) {
//
//    	ExtTapDescriptor descriptor = (ExtTapDescriptor) descriptorInput;
//    	String adql = "SELECT table_name, count(*) ";
//    	adql = getAdql(descriptor, adql);
//    	adql += " group by table_name";
//
//    	String finalAdql = adql + " UNION " + adql.replace("pos_small", "pos_big");
//     	return finalAdql;
//    }

	@Override
	public String getMetadataAdqlRadial(CommonTapDescriptor descriptor, SkyViewPosition conePos) {
		return null;
	}

	@Override
	public String getMetadataAdql(CommonTapDescriptor descriptor, String filter) {
		return getMetadataAdql(descriptor);
	}

}