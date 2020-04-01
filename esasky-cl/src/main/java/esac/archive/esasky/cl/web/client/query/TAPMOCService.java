package esac.archive.esasky.cl.web.client.query;

import com.allen_sauer.gwt.log.client.Log;

import esac.archive.esasky.cl.web.client.repository.MocRepository;
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
    			+ "\')  as moc from dual";
    	return adql;
    }
    
    public String getFilteredCatalogueMOCAdql(IDescriptor descriptor, String filter) {
    	
    	int order = MocRepository.getTargetOrderFromFoV();
    	Coordinate pos = CoordinateUtils.getCenterCoordinateInJ2000().getCoordinate();
    	
    	String adql = "SELECT esasky_q3c_filtered_catalogue_moc_query(\'" + descriptor.getTapTable()
		    	+ "\', " + getGeometricConstraint() + ", \'" + filter + "\',\'" + Double.toString(pos.ra) + "\', \'" + Double.toString(pos.dec)
		    	+ "\',\'" + Integer.toString(order) + "\') as moc from dual";
    	
    	//		String adql = "SELECT " + Integer.toString(order) + " as moc_order,"
//				+ "esasky_q3c_bitshift_right(q3c_ang2ipix(ra,dec), " + Integer.toString(60 - 2 * order) + ") as moc_ipix, count(*) as moc_count"
//				+ " FROM " + descriptor.getTapTable() + " WHERE \'1\' = q3c_radial_query(ra,dec, "
//				+ Double.toString(pos.getCoordinate().ra) + ", "  +  Double.toString(pos.getCoordinate().dec) + ", "
//				+ Double.toString(pos.getFov()/2) + ")" + filter + " GROUP BY moc_ipix";
		
		return adql;
    }
				
	public String getFilteredObservationMOCAdql(IDescriptor descriptor, String filter) {
		
		Coordinate pos = CoordinateUtils.getCenterCoordinateInJ2000().getCoordinate();
		
		String adql = "SELECT esasky_q3c_filtered_obs_moc_query(\'" + descriptor.getTapTable()
		+ "\', " + getGeometricConstraint() + ", \'" + filter + "\',\'" + Double.toString(pos.ra) + "\', \'" + Double.toString(pos.dec)
		+ "\',\'8\') as moc from dual";
		
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