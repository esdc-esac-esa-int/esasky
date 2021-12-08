package esac.archive.esasky.cl.web.client.query;

import com.allen_sauer.gwt.log.client.Log;
import esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteWidget;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.CoordinatesObject;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.SearchArea;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.CoordinateUtils;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesConversion;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;

import java.util.Arrays;
import java.util.stream.Collectors;

public class TAPSingleCountService {

    private static TAPSingleCountService instance = null;

    private TAPSingleCountService() {
    }

    public static TAPSingleCountService getInstance() {
        if (instance == null) {
            instance = new TAPSingleCountService();
        }
        return instance;
    }

    public String getCount(AladinLiteWidget aladinLite) {

        String cooFrame = aladinLite.getCooFrame();
        double[] ccInJ2000 = { aladinLite.getCenterLongitudeDeg(),
                               aladinLite.getCenterLatitudeDeg() };
        
        if (EsaSkyWebConstants.ALADIN_GALACTIC_COOFRAME.equalsIgnoreCase(cooFrame)) {
            // convert to J2000
            ccInJ2000 = CoordinatesConversion.convertPointGalacticToJ2000(
                            aladinLite.getCenterLongitudeDeg(),
                            aladinLite.getCenterLatitudeDeg());
        }
        
        String shape = "POLYGON('ICRS', 0.0, 0.0, 1.0, 1.0, 2.0, 2.0)";
        String spoly = "";
        
        if (AladinLiteWrapper.isCornersInsideHips()) {
            shape = "POLYGON('ICRS'," + aladinLite.getFovCorners(2).toString() + ")";
            spoly = "{" + aladinLite.getFovCorners(2).toString() + "}";
        }
        
        String adqlQuery = "SELECT esasky_general_dynamic_count_q3c_json(" + shape
                + ",   '" + spoly + "'"
                + ",   '" + ccInJ2000[0] + "', '" + ccInJ2000[1] + "'"
                + ") as esasky_dynamic_count from dual";
        
        Log.debug("[TAPQueryBuilder/SingleFastCountQuery()] Single Fast count ADQL " + adqlQuery);
        
        return adqlQuery;
    }
    
    public String getCountStcs(AladinLiteWidget aladinLite) {
          String shape = "";
          
          if (AladinLiteWrapper.isCornersInsideHips()) {
              shape = "POLYGON('ICRS'," + aladinLite.getFovCorners(2).toString() + ")";
          }else {
        	  SkyViewPosition pos = CoordinateUtils.getCenterCoordinateInJ2000();
        	  shape = "CIRCLE('ICRS'," + pos.getCoordinate().getRa() + "," + pos.getCoordinate().getDec() + ", 90.0)" ;
          }
          
          return shape;
    }

    public String getCountSearchArea(SearchArea searchArea) {
        
        if (searchArea.isCircle()) {
            CoordinatesObject coordinate = searchArea.getCoordinates()[0];
            return  "CIRCLE('ICRS'," + coordinate.getRaDeg() + "," + coordinate.getDecDeg() + "," + searchArea.getRadius();
        } else {
            CoordinatesObject[] coordinates = searchArea.getCoordinates();
            String coordinateStr = Arrays.stream(coordinates)
                    .map(point -> point.getRaDeg() + "," + point.getDecDeg())
                    .collect(Collectors.joining(","));

            return "POLYGON('ICRS'," + coordinateStr + ")";
        }

    }

}
