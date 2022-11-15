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
		} else {
			String constraint = getGeometricConstraint().replace("\'", "");
			if (!constraint.equals("")) {
				constraint = "POLYGON" + constraint;
			}

			return constraint;
		}
    }
    
    private String getGeometricConstraint() {
    	final String debugPrefix = "[TAPMOCService.getGeometricConstraint]";
        String shape;
        double fovDeg = AladinLiteWrapper.getAladinLite().getFovDeg();
        if (AladinLiteWrapper.isCornersInsideHips()) {
            if (fovDeg < 1) {
                Log.debug(debugPrefix + " FoV < 1d");
                shape = "\'(" +  AladinLiteWrapper.getAladinLite().getFovCorners(1).toString() + ")\'";

            } else {
                shape =  "\'(" +  AladinLiteWrapper.getAladinLite().getFovCorners(2).toString() + ")\'";
            }
        } else {

            shape = "\'\'";
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