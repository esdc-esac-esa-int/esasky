package esac.archive.esasky.cl.web.client.query;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.http.client.URL;
import esac.archive.esasky.cl.web.client.utility.*;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.ExtTapDescriptor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TAPExtTapService extends AbstractTAPService {

    private static TAPExtTapService instance = null;

    private static final String FROM = " FROM ";
    private static final String WHERE = " WHERE ";
    private static final String AND = " AND ";
    private static final String HEASARC = EsaSkyWebConstants.HEASARC_MISSION;

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

		// Handle tables with non-alphanumeric characters
        adql += FROM + ExtTapUtils.encapsulateTableName(tapTable);

        boolean isHEASARC = descriptor.getMission().equalsIgnoreCase(HEASARC);
        boolean isChildHEASARC = isHEASARC && descriptor.getParent() != null;
        if (isHEASARC && !isChildHEASARC) {
            adql += " JOIN master_table.indexview on table_name = name";
        }

        if (!descriptor.isFovLimitDisabled()) {
            if(descriptor.useIntersectsPolygon()) {
                adql +=  WHERE + polygonIntersectSearch(descriptor);
            } else if (isHEASARC) {
                adql += WHERE + heasarcSearch();
            } else {
                adql += WHERE + cointainsPointSearch(descriptor);
            }
        }

        if (descriptor.getBlacklist() != null) {
            adql += descriptor.isFovLimitDisabled() ? WHERE  : AND;
            adql += descriptor.getGroupColumn2()
                    + (!isHEASARC ? " NOT IN" : " IN ") // HEASARC blacklist used as whitelist
                    + "(" + Arrays.stream(descriptor.getBlacklist()).map(bl -> "'" + bl + "'").collect(Collectors.joining(", ")) + ")";
        }

    	if(descriptor.getWhereADQL() != null && !isChildHEASARC) {
    		if(adql.contains(WHERE.trim())) {
    			adql += AND;
    		}else {
    			adql += WHERE;
    		}
    		adql += descriptor.getWhereADQL();
    	}
    	Log.debug("[TAPQueryBuilder/getMetadata4ExtTap()] ADQL " + adql);

    	return adql;
    }

    private String screenCircle() {
        double fovDeg = Math.min(90, AladinLiteWrapper.getAladinLite().getFovDeg());
        double centerLong = AladinLiteWrapper.getAladinLite().getCenterLongitudeDeg();
        double centerLat = AladinLiteWrapper.getAladinLite().getCenterLatitudeDeg();
        return "CIRCLE('ICRS', " + centerLong + "," + centerLat + ", " + fovDeg + ")" + ")";
    }

    private String polygonIntersectSearch(CommonTapDescriptor descriptor) {
    	String constraint = "1=INTERSECTS(" + descriptor.getRegionColumn() + ",";
    	return constraint + screenCircle();
    }

    private String cointainsPointSearch(CommonTapDescriptor descriptor) {
    	String constraint = "1=CONTAINS( POINT('ICRS', " + descriptor.getRaColumn() + ", " + descriptor.getDecColumn() + "), ";
    	return constraint + screenCircle();
    }

    private String heasarcSearch() {
    	SkyViewPosition pos = CoordinateUtils.getCenterCoordinateInJ2000();
    	double fov = pos.getFov();
    	double ra = pos.getCoordinate().getRa();
    	double dec = pos.getCoordinate().getDec();
        return " POWER(SIN((radians(dec) - radians(" + dec + "))/2),2)"
                + "+ cos(radians(dec)) * cos(radians(" + dec + "))"
                + "* POWER(SIN((radians(ra) - radians(" + ra + "))/2),2)"
                + "< POWER((radians(" + fov +")/2),2) AND dec"
                + " BETWEEN " + (dec - fov/2) + AND + (dec + fov/2);
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
            List<String> raValues = IntStream.range(0, fovCorners.length).filter(i -> i % 2 == 0).mapToObj(i -> fovCorners[i]).collect(Collectors.toList());
            List<String> decValues = IntStream.range(0, fovCorners.length).filter(i -> i % 2 != 0).mapToObj(i -> fovCorners[i]).collect(Collectors.toList());

            minRa = raValues.stream().mapToDouble(Double::parseDouble).min().orElse(minRa);
            maxRa = raValues.stream().mapToDouble(Double::parseDouble).max().orElse(maxRa);
            minDec = decValues.stream().mapToDouble(Double::parseDouble).min().orElse(minDec);
            maxDec = decValues.stream().mapToDouble(Double::parseDouble).max().orElse(maxDec);
        }

        return descriptor.getTapRaColumn() + " > " + minRa + AND +
        		descriptor.getTapRaColumn() + " < " + maxRa + AND +
        		descriptor.getTapDecColumn() + " > " + minDec + AND +
        		descriptor.getTapDecColumn() + " < " + maxDec;
    }

    @Override
    public String getMetadataAdql(CommonTapDescriptor descriptor) {
        if(!descriptor.isExternal()) {
            String selectADQL = "SELECT TOP " + DeviceUtils.getDeviceShapeLimit(descriptor) + " * ";
        	return URL.encodeQueryString(getAdql(descriptor, selectADQL));
        } else if(descriptor.getUnprocessedADQL() != null) {
            return descriptor.getUnprocessedADQL();
        } else {
            return getAdql(descriptor, descriptor.getSelectADQL());
        }
    }

    public String getCountAdql(CommonTapDescriptor descriptor) {
		if(HEASARC.equalsIgnoreCase(descriptor.getMission())) {
			return URL.encodeQueryString(getHeasarcCountAdql(descriptor));
		}
		return URL.encodeQueryString(getDefaultCountAdql(descriptor));

    }

    public String getDefaultCountAdql(CommonTapDescriptor descriptor) {
    	String selectADQL = "SELECT count(*) as c, MIN(em_min) as em_min, MAX(em_max) as em_max";
        selectADQL += ", " + descriptor.getGroupColumn1() + ", " + descriptor.getGroupColumn2();

        String adql = getAdql(descriptor, selectADQL);
    	adql += " group by " + descriptor.getGroupColumn1() + ", " + descriptor.getGroupColumn2();


    	return adql;
    }

    public String getHeasarcCountAdql(CommonTapDescriptor descriptor) {
    	String adql = "SELECT table_name, description, regime, count(*) as c";
    	adql = getAdql(descriptor, adql);
    	adql += " group by table_name,description,regime";

        return adql + " UNION " + adql.replace("pos_small", "pos_big");
    }

	@Override
	public String getMetadataAdqlRadial(CommonTapDescriptor descriptor, SkyViewPosition conePos) {
		return null;
	}

	@Override
	public String getMetadataAdql(CommonTapDescriptor descriptor, String filter) {
		return getMetadataAdql(descriptor);
	}

}