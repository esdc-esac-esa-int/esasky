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
