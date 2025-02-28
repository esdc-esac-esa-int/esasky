/*
ESASky
Copyright (C) 2025 European Space Agency

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
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;
import esac.archive.esasky.ifcs.model.shared.EsaSkyMocUtility;
import esac.archive.esasky.ifcs.model.shared.RangeTree;
import esac.archive.esasky.ifcs.model.shared.RangeTree.Interval;

public class TAPMOCService {

    private static TAPMOCService instance = null;

    private TAPMOCService() {
    }

    public static TAPMOCService getInstance() {
        if (instance == null) {
            instance = new TAPMOCService();
        }
        return instance;
    }

    public String getPrecomputedMocConstraint(CommonTapDescriptor descriptor) {
		if (descriptor.hasSearchArea()) {
			return descriptor.getSearchAreaShape();
		} else if (AladinLiteWrapper.getInstance().getFovDeg() > 180) {
			return "";
		} else {
			return getGeometricConstraint().replace("\'", "");
		}
    }
    
    private String getGeometricConstraint() {
    	final String debugPrefix = "[TAPMOCService.getGeometricConstraint]";
        String shape;
        double fovDeg = AladinLiteWrapper.getAladinLite().getFovDeg();

		if (AladinLiteWrapper.isCornersInsideHips()) {
            if (fovDeg < 1) {
                Log.debug(debugPrefix + " FoV < 1d");
                shape = "POLYGON\'(" +  AladinLiteWrapper.getAladinLite().getFovCorners(1).toString() + ")\'";

            } else {
                shape =  "POLYGON\'(" +  AladinLiteWrapper.getAladinLite().getFovCorners(2).toString() + ")\'";
            }
        } else {
			double lon = AladinLiteWrapper.getInstance().getCenterLongitudeDeg();
			double lat = AladinLiteWrapper.getInstance().getCenterLatitudeDeg();
            shape = "CIRCLE\'(" + lon + " " + lat + " " + "90)\'";
        }
        return shape;
    }
    
    public String getWhereQueryFromPixels(CommonTapDescriptor descriptor, GeneralJavaScriptObject pixels, String filter) {
    	String[] orderArray = pixels.getProperties().split(",");
    	RangeTree rangeTree = new RangeTree();
    	
    	for(String orderString : orderArray) {
    		int pixelOrder = Integer.parseInt(orderString);
        	String[] pixelArray = GeneralJavaScriptObject.convertToString(pixels.getProperty(orderString)
        			.invokeFunction("toString")).split(",");
        	for(String pixelString : pixelArray){
        		long pixel = Long.parseLong(pixelString);
        		long start = pixel << (60 - 2 * pixelOrder);
        		long end = (pixel + 1) << (60 - 2 * pixelOrder);
        		rangeTree.add(start, end);
        	}
    	}
    	
    	String whereADQL = " WHERE (";
    	String raColumn = descriptor.getRaColumn();
    	String decColumn = descriptor.getDecColumn();
    	
    	for(Interval i : rangeTree.getTree()) {
    		whereADQL += " q3c_ang2ipix(" + raColumn+ "," + decColumn + ") BETWEEN "+ Long.toString(i.getStart())
				+ " AND " + Long.toString(i.getEnd());
    		whereADQL += " OR ";
    	}
    	
    	whereADQL = whereADQL.substring(0, whereADQL.length() - 3);
    	whereADQL += ")";
    	
    	if(!"".contentEquals(filter)) {
    		whereADQL += " AND " + filter;
    		
    	}
    	
    	return whereADQL;
    }

    public static String mocObjectToString(GeneralJavaScriptObject mocObject) {
    	return EsaSkyMocUtility.objectToAsciiString(mocObject);
    }
    
}