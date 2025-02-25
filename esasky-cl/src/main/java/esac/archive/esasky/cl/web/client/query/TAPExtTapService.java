/*
ESASky
Copyright (C) 2025 Henrik Norman

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published
by the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package esac.archive.esasky.cl.web.client.query;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.http.client.URL;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.CoordinatesObject;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.SearchArea;
import esac.archive.esasky.cl.web.client.repository.DescriptorRepository;
import esac.archive.esasky.cl.web.client.utility.*;
import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesConversion;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.ExtTapDescriptor;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TAPExtTapService extends AbstractTAPService {

    private static TAPExtTapService instance = null;

    private static final String FROM = " FROM ";
    private static final String WHERE = " WHERE ";

    private static final String ORDER_BY = " ORDER BY ";
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
    public String getRequestUrl(CommonTapDescriptor descriptor) {
        return descriptor.isUserTable() ? super.getRequestUrl(descriptor) : EsaSkyWebConstants.EXT_TAP_REQUEST_URL;
    }

    public String getAdql(CommonTapDescriptor descriptor, String selectADQL, SearchArea searchArea) {
    	String adql = selectADQL;

		String tapTable = descriptor.getTableName();

		// Handle tables with non-alphanumeric characters
        adql += FROM + ExtTapUtils.encapsulateTableName(tapTable);

        boolean isHEASARC = getIsHEASARC(descriptor);

        boolean isChildHEASARC = getIsChildHEASARC(isHEASARC, descriptor);

        if (isHEASARC && !isChildHEASARC) {
            adql += " JOIN master_table.indexview on table_name = name";
        }

        if (!descriptor.isFovLimitDisabled()) {
            if (descriptor.useIntersectsPolygon()) {
                adql +=  WHERE + polygonIntersectSearch(descriptor, searchArea);
            } else if (isHEASARC) {
                adql += WHERE + heasarcSearch(descriptor, searchArea);
            } else {
                adql += WHERE + cointainsPointSearch(descriptor, searchArea);
            }
        }

        if (descriptor.getBlacklist() != null) {
            adql += getBlacklistAdql(descriptor, isHEASARC);
        }

    	adql = addWhereToAdql(descriptor, isChildHEASARC, adql);

        adql = addOrderByToAdql(descriptor, isChildHEASARC, adql);

    	Log.debug("[TAPQueryBuilder/getMetadata4ExtTap()] ADQL " + adql);
    	return adql;
    }

    private boolean getIsChildHEASARC(boolean isHEASARC, CommonTapDescriptor descriptor) {
        return isHEASARC && descriptor.getParent() != null;
    }

    private boolean getIsHEASARC(CommonTapDescriptor descriptor) {
        return descriptor.getMission().equalsIgnoreCase(HEASARC)
                && descriptor.getOriginalParent() != null
                && !descriptor.getOriginalParent().isCustom();
    }

    private String addWhereToAdql(CommonTapDescriptor descriptor, boolean isChildHEASARC, String adql) {
        if(descriptor.getWhereADQL() != null && !isChildHEASARC) {
            if(adql.contains(WHERE.trim())) {
                adql += AND;
            }else {
                adql += WHERE;
            }
            adql += descriptor.getWhereADQL();
        }
        return adql;
    }

    private String addOrderByToAdql(CommonTapDescriptor descriptor, boolean isChildHEASARC, String adql) {
        if(descriptor.getOrderByADQL() != null && !isChildHEASARC) {
            if(adql.contains(ORDER_BY.trim())) {
                adql += AND;
            }else {
                adql += ORDER_BY;
            }
            adql += descriptor.getOrderByADQL();
        }
        return adql;
    }

    private String getBlacklistAdql(CommonTapDescriptor descriptor, boolean isHEASARC) {
        String adql = descriptor.isFovLimitDisabled() ? WHERE  : AND;
        adql += descriptor.getGroupColumn2()
                + (!isHEASARC ? " NOT IN" : " IN ") // HEASARC blacklist used as whitelist
                + "(" + Arrays.stream(descriptor.getBlacklist()).map(bl -> "'" + bl + "'").collect(Collectors.joining(", ")) + ")";

        return adql;
    }

    private String screenCircle() {
        double fovDeg = Math.min(90, AladinLiteWrapper.getAladinLite().getFovDeg());
        double centerLong = AladinLiteWrapper.getAladinLite().getCenterLongitudeDeg();
        double centerLat = AladinLiteWrapper.getAladinLite().getCenterLatitudeDeg();
        String cooFrame = AladinLiteWrapper.getAladinLite().getCooFrame();
        if (EsaSkyWebConstants.ALADIN_GALACTIC_COOFRAME.equalsIgnoreCase(cooFrame)) {
            double[] ccInJ2000 = CoordinatesConversion.convertPointGalacticToJ2000(centerLong, centerLat);
            centerLong = ccInJ2000[0];
            centerLat = ccInJ2000[1];
        }
        return circleQueryString(centerLong, centerLat, String.valueOf(fovDeg));
    }

    private String circleQueryString(double centerLong, double centerLat, String fovDeg) {
        return "CIRCLE('ICRS', " + centerLong + "," + centerLat + ", " + fovDeg + ")";
    }

    private String polygonIntersectSearch(CommonTapDescriptor descriptor, SearchArea searchArea) {
        String areaString = searchArea == null ? screenCircle() : areaToAdqlString(searchArea);
    	return "1=INTERSECTS(" + descriptor.getRegionColumn() + "," + areaString + ")";
    }

    private String cointainsPointSearch(CommonTapDescriptor descriptor, SearchArea searchArea) {
        String areaString;
        // Disable ESO Polygon search for now. They require the polygon vertices to be in counterclockwise order,
        // i.e. the "left" side is the "inside" where we search for objects. We don't know in which order the vertices
        // come in the SearchArea object.
        if (searchArea == null) {
            areaString = screenCircle();
        } else if (!searchArea.isCircle() && "ESO".equals(descriptor.getMission())) {
            areaString = screenCircle();
        } else {
            areaString = areaToAdqlString(searchArea);
        }

        String containsQuery = "CONTAINS( POINT('ICRS', " + descriptor.getRaColumn() + ", " + descriptor.getDecColumn() + "), " + areaString + ")";
        if (Objects.equals(descriptor.getMission(), "ESO")) {
            return containsQuery + "=1";
        } else {
            return "1=" +containsQuery;
        }
    }

    private String areaToAdqlString(SearchArea searchArea) {
        CoordinatesObject coordinate = searchArea.getJ2000Coordinates()[0];

        if (searchArea.isCircle()) {
            return circleQueryString(coordinate.getRaDeg(), coordinate.getDecDeg(), searchArea.getRadius());
        } else {
            CoordinatesObject[] coordinates = searchArea.getJ2000Coordinates();
            String coordinateString = Arrays.stream(coordinates)
                    .map(point -> point.getRaDeg() + "," + point.getDecDeg())
                    .collect(Collectors.joining(","));
            return "POLYGON('ICRS'," + coordinateString + ")";
        }
    }

    private String heasarcSearch(CommonTapDescriptor descriptor, SearchArea searchArea) {
        double fov, ra, dec;
        // Heasarc supports polygon search, but it's really slow. They are soon(?) releasing an obscore version, which will speed things up.
        if (searchArea != null && searchArea.isCircle()) {
            CoordinatesObject point = searchArea.getJ2000Coordinates()[0];
            ra = point.getRaDeg();
            dec = point.getDecDeg();
            fov = Float.parseFloat(searchArea.getRadius()) * 2;
        } else {
            SkyViewPosition pos = CoordinateUtils.getCenterCoordinateInJ2000();
            fov = pos.getFov();
            ra = pos.getCoordinate().getRa();
            dec = pos.getCoordinate().getDec();
        }
        return " POWER(SIN((radians(dec) - radians(" + dec + "))/2),2)"
                + "+ POWER(SIN((radians(ra) - radians(" + ra + "))/2),2)"
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
        if (descriptor.getUnprocessedADQL() != null) {
            return descriptor.getUnprocessedADQL();
        } else {
            SearchArea searchArea = DescriptorRepository.getInstance().getSearchArea();
            return getAdql(descriptor, descriptor.getSelectADQL(), searchArea);
        }
    }

    public String getCountAdql(CommonTapDescriptor descriptor, SearchArea searchArea) {
		if(HEASARC.equalsIgnoreCase(descriptor.getMission())) {
			return URL.encodeQueryString(getHeasarcCountAdql(descriptor, searchArea));
		}
		return URL.encodeQueryString(getDefaultCountAdql(descriptor, searchArea));

    }

    public String getDefaultCountAdql(CommonTapDescriptor descriptor, SearchArea searchArea) {
    	String selectADQL = "SELECT count(*) as c, MIN(em_min) as em_min, MAX(em_max) as em_max";
        selectADQL += ", " + descriptor.getGroupColumn1() + ", " + descriptor.getGroupColumn2();

        String adql = getAdql(descriptor, selectADQL, searchArea);
    	adql += " group by " + descriptor.getGroupColumn1() + ", " + descriptor.getGroupColumn2();


    	return adql;
    }

    public String getHeasarcCountAdql(CommonTapDescriptor descriptor, SearchArea searchArea) {
    	String adql = "SELECT table_name, description, regime, count(*) as c";
    	adql = getAdql(descriptor, adql, searchArea);
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