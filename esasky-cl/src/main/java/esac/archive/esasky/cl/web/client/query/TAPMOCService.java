package esac.archive.esasky.cl.web.client.query;

import com.allen_sauer.gwt.log.client.Log;

import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.CoordinateUtils;
import esac.archive.esasky.ifcs.model.coordinatesutils.Coordinate;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;

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

    public String getPrecomputedMOCAdql(IDescriptor descriptor) {
    	Coordinate pos = CoordinateUtils.getCenterCoordinateInJ2000().getCoordinate();
    	String adql = "SELECT esasky_q3c_moc_query(\'" + descriptor.getTapTable().replace("public", "moc_schema") 
    			+ "\', " + getGeometricConstraint() + ", \'" + Double.toString(pos.ra) + "\', \'" + Double.toString(pos.dec)
    			+ "\') from dual";
    	return adql;
    }
    
    private String getGeometricConstraint() {
    	final String debugPrefix = "[TAPMOCService.getGeometricConstraint]";
        String shape = null;
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
    
}