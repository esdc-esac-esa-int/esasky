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
    
    public String getCountStcs(AladinLiteWidget aladinLite) {
          String shape;

          if (aladinLite.getFovDeg() > 180) {
              return "";
          }
          
          if (AladinLiteWrapper.isCornersInsideHips()) {
              if (AladinLiteWrapper.isCornersValid()) {
                  shape = "POLYGON('ICRS'," + aladinLite.getFovCorners(2).toString() + ")";
              } else {
                  SkyViewPosition pos = CoordinateUtils.getCenterCoordinateInJ2000();
                  shape = "CIRCLE('ICRS'," + pos.getCoordinate().getRa() + "," + pos.getCoordinate().getDec() + ", " + aladinLite.getFovDeg() + ")" ;
              }

          } else {
        	  SkyViewPosition pos = CoordinateUtils.getCenterCoordinateInJ2000();
        	  shape = "CIRCLE('ICRS'," + pos.getCoordinate().getRa() + "," + pos.getCoordinate().getDec() + ", 90.0)" ;
          }

          return shape;
    }

    public String getCountSearchArea(SearchArea searchArea) {
        
        if (searchArea.isCircle()) {
            CoordinatesObject coordinate = searchArea.getJ2000Coordinates()[0];
            return  "CIRCLE('ICRS'," + coordinate.getRaDeg() + "," + coordinate.getDecDeg() + "," + searchArea.getRadius();
        } else {
            CoordinatesObject[] coordinates = searchArea.getJ2000Coordinates();
            String coordinateStr = Arrays.stream(coordinates)
                    .map(point -> point.getRaDeg() + "," + point.getDecDeg())
                    .collect(Collectors.joining(","));

            return "POLYGON('ICRS'," + coordinateStr + ")";
        }

    }

}
