package esac.archive.esasky.cl.web.client.utility;

import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.NumberFormat;

import esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteConstants;
import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesConversion;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;

public class ExternalServices {

    private static final String VIZIER_PHOTOMETRY_URL = "http://cdsportal.u-strasbg.fr/gadgets/ifr?";
    private static final String VIZIER_PHOTOMETRY_SERVICE_PARAMETER = "url";
    private static final String VIZIER_PLOTTER = "http://cdsportal.u-strasbg.fr/widgets/SED_plotter.xml";
    private static final String VIZIER_SERVICE_SED_PLOT_RADIUS = "SED_plot_radius";
    private static final String VIZIER_DEFAULT_SED_PLOT_RADIUS_ARCSEC = "5";
    private static final String VIZIER_SERVICE_SED_PLOT_OBJECT = "SED_plot_object";
    
    private static final String VIZIER_URL = "//vizier.u-strasbg.fr/viz-bin/VizieR?";
    private static final String VIZIER_CENTER_PARAMETER = "-c=";
    private static final String VIZIER_RADIUS_PARAMETER = "-c.rm=";
    private static final int VIZIER_DEFAULT_RADIUS_ARCMIN = 2;

    private static final String NED_URL = "//ned.ipac.caltech.edu/cgi-bin/objsearch?";
    private static final String NED_SERVICE_CSYS = "in_csys";
    private static final String NED_GALACTIC_CSYS = "Galactic";
    private static final String NED_SERVICE_EQUINOX = "in_equinox";
    private static final String NED_DEFAULT_EQUINOX = "J2000.0";
    private static final String NED_SERVICE_RADIUS = "radius";
    private static final String NED_DEFAULT_RADIUS_ARCMIN = "2.0";
    private static final String NED_SERVICE_HCONST = "hconst";
    private static final String NED_DEFAULT_HCONST = "73";
    private static final String NED_SERVICE_OMEGAM = "omegam";
    private static final String NED_DEFAULT_OMEGAM = "0.27";
    private static final String NED_SERVICE_OMEGAV = "omegav";
    private static final String NED_DEFAULT_OMEGAV = "0.73";
    private static final String NED_SERVICE_CORR_Z = "corr_z";
    private static final String NED_DEFAULT_CORR_Z = "1";
    private static final String NED_SERVICE_Z_CONSTRAINT = "z_constraint";
    private static final String NED_DEFAULT_Z_CONSTRAINT = "Unconstrained";
    private static final String NED_SERVICE_Z_VALUE1 = "z_value1";
    private static final String NED_DEFAULT_Z_VALUE1 = "";
    private static final String NED_SERVICE_Z_VALUE2 = "z_value2";
    private static final String NED_DEFAULT_Z_VALUE2 = "";
    private static final String NED_SERVICE_Z_UNIT = "z_unit";
    private static final String NED_DEFAULT_Z_UNIT = "z";
    private static final String NED_SERVICE_OT_INCLUDE = "ot_include";
    private static final String NED_DEFAULT_OT_INCLUDE = "ANY";
    private static final String NED_SERVICE_NMP_OP = "nmp_op";
    private static final String NED_DEFAULT_NMP_OP = "ANY";
    private static final String NED_SERVICE_OUT_CSYS = "out_csys";
    private static final String NED_EQUATORIAL_OUT_CSYS = "Equatorial";
    private static final String NED_GALACTIC_OUT_CSYS = "Galactic";
    private static final String NED_SERVICE_OUT_EQUINOX = "out_equinox";
    private static final String NED_DEFAULT_OUT_EQUINOX = "J2000.0";
    private static final String NED_SERVICE_OBJ_SORT = "obj_sort";
    private static final String NED_DEFAULT_OBJ_SORT = "Distance+to+search+center";
    private static final String NED_SERVICE_OF = "of";
    private static final String NED_DEFAULT_OF = "pre_text";
    private static final String NED_SERVICE_ZV_BREAKER = "zv_breaker";
    private static final String NED_DEFAULT_ZV_BREAKER = "30000.0";
    private static final String NED_SERVICE_LIST_LIMIT = "list_limit";
    private static final String NED_DEFAULT_LIST_LIMIT = "5";
    private static final String NED_SERVICE_IMG_STAMP = "img_stamp";
    private static final String NED_DEFAULT_IMG_STAMP = "YES";
    private static final String NED_SERVICE_SEARCH_TYPE = "search_type";
    private static final String NED_DEFAULT_SEARCH_TYPE = "Near+Position+Search";
    private static final String NED_SERVICE_LON = "lon";
    private static final String NED_SERVICE_LAT = "lat";

    private static final String SIMBAD_COO_URL = "//simbad.u-strasbg.fr/simbad/sim-coo?";
    private static final String SIMBAD_SERVICE_PARAM_COORD = "Coord";
    private static final String SIMBAD_SERVICE_PARAM_COORD_FRAME = "CooFrame";
    private static final String SIMBAD_SERVICE_PARAM_RADIUS = "Radius";
    private static final String SIMBAD_SERVICE_PARAM_RADIUS_UNIT = "Radius.unit";
    private static final String SIMBAD_DEFAULT_RADIUS_ARCMIN = "2";
    private static final String SIMBAD_DEFAULT_RADIUS_UNIT = "arcmin";
    /** Default HiPS cooframe. */
    public static final String SIMBAD_J2000_COO_FRAME = "ICRS";
    /** Default HiPS cooframe. */
    public static final String SIMBAD_GALACTIC_COO_FRAME = "Gal";

    public static String buildVizierPhotometryURL(double raDeg, double decDeg, String cooFrame) {
        double finalRa = raDeg;
        double finalDec = decDeg;

        // Convert Galactic to Equatorial
        if (AladinLiteConstants.FRAME_GALACTIC.toLowerCase().equalsIgnoreCase(cooFrame)) {
            Double[] ccInJ2000 = CoordinatesConversion.convertPointGalacticToJ2000(raDeg,
                    decDeg);
            finalRa = ccInJ2000[0];
            finalDec = ccInJ2000[1];
        }

        String formattedRa = "";
        if (finalRa < 0) {
            finalRa = Math.abs(finalRa);
            formattedRa += "-";
        }
        // conversion to hours (with decimal)
        Double conversion2Hours = finalRa / 15.0;
        String conversion2HoursString = Double.toString(conversion2Hours);
        String[] conversion2HoursStringToken = conversion2HoursString.split("\\.");
        // hours (whole number)
        Integer hours = Integer.parseInt(conversion2HoursStringToken[0]);
        // conversion to minutes (with decimal)
        Double decimalPart4Minutes = (conversion2Hours - hours);
        Double conversion2Minutes = decimalPart4Minutes * 60;
        // minutes (whole number)
        Integer minutes = Integer.parseInt((Double.toString(conversion2Minutes)).split("\\.")[0]);

        // seconds
        Double decimalPart4Seconds = (conversion2Minutes - minutes);
        Double seconds = decimalPart4Seconds * 60;

        formattedRa += NumberFormat.getFormat("00").format(hours) + " ";
        formattedRa += NumberFormat.getFormat("00").format(minutes) + " ";
        formattedRa += NumberFormat.getFormat("00.00").format(seconds) + " ";

        return VIZIER_PHOTOMETRY_URL + VIZIER_PHOTOMETRY_SERVICE_PARAMETER + "=" + VIZIER_PLOTTER + "&"
                + VIZIER_SERVICE_SED_PLOT_RADIUS + "=" + VIZIER_DEFAULT_SED_PLOT_RADIUS_ARCSEC + "&"
                + VIZIER_SERVICE_SED_PLOT_OBJECT + "=" + formattedRa + " " + finalDec;
    }        
    public static String buildVizierURL(double raDeg, double decDeg, String cooFrame) {
    	double finalRa = raDeg;
    	double finalDec = decDeg;
    	
    	if (AladinLiteConstants.FRAME_GALACTIC.toLowerCase().equalsIgnoreCase(cooFrame)) {
    		Double[] ccInJ2000 = CoordinatesConversion.convertPointGalacticToJ2000(raDeg,
    				decDeg);
    		finalRa = ccInJ2000[0];
    		finalDec = ccInJ2000[1];
    	}
    	
    	return VIZIER_URL + VIZIER_CENTER_PARAMETER + finalRa + " " + finalDec
    			+ "&" + VIZIER_RADIUS_PARAMETER + VIZIER_DEFAULT_RADIUS_ARCMIN;
    }

    public static String buildSimbadURLWithRaDec(double raDeg, double decDeg, String cooFrame) {
        String formattedCoords = NumberFormat.getFormat("###.###").format(raDeg) + "d"
                + NumberFormat.getFormat("###.###").format(decDeg) + "d";
        String url = SIMBAD_COO_URL + "&";
        url += SIMBAD_SERVICE_PARAM_COORD + "=" + formattedCoords + "&";
        if (AladinLiteConstants.FRAME_GALACTIC.toLowerCase().equalsIgnoreCase(cooFrame)) {
            url += SIMBAD_SERVICE_PARAM_COORD_FRAME + "=Gal&";
        } else if (AladinLiteConstants.FRAME_J2000.equals(cooFrame)) {
            url += SIMBAD_SERVICE_PARAM_COORD_FRAME + "=FK5&";
        }
        url += SIMBAD_SERVICE_PARAM_RADIUS + "=" + SIMBAD_DEFAULT_RADIUS_ARCMIN + "&";
        url += SIMBAD_SERVICE_PARAM_RADIUS_UNIT + "=" + SIMBAD_DEFAULT_RADIUS_UNIT;

        return url;
    }

    public static String buildNedURL(double raDeg, double decDeg, String cooFrame) {

        String url = NED_URL;
        // Galactic / J2000 check!!!
        if (AladinLiteConstants.FRAME_GALACTIC.toLowerCase().equalsIgnoreCase(cooFrame)) {
            url += NED_SERVICE_CSYS + "=" + NED_GALACTIC_CSYS + "&";
            url += NED_SERVICE_OUT_CSYS + "=" + NED_GALACTIC_OUT_CSYS + "&";
        } else if (AladinLiteConstants.FRAME_J2000.equals(cooFrame)) {
            url += NED_SERVICE_OUT_CSYS + "=" + NED_EQUATORIAL_OUT_CSYS + "&";
        }
        String formattedRa = "";
        if (raDeg < 0) {
            raDeg = Math.abs(raDeg);
            formattedRa += "-";
        }
        // conversion to hours (with decimal)
        Double conversion2Hours = raDeg / 15.0;
        String conversion2HoursString = Double.toString(conversion2Hours);
        String[] conversion2HoursStringToken = conversion2HoursString.split("\\.");
        // hours (whole number)
        Integer hours = Integer.parseInt(conversion2HoursStringToken[0]);

        // conversion to minutes (with decimal)
        Double decimalPart4Minutes = (conversion2Hours - hours);
        Double conversion2Minutes = decimalPart4Minutes * 60;
        // minutes (whole number)
        Integer minutes = Integer.parseInt((Double.toString(conversion2Minutes)).split("\\.")[0]);

        // seconds
        Double decimalPart4Seconds = (conversion2Minutes - minutes);
        Double seconds = decimalPart4Seconds * 60;

        formattedRa += NumberFormat.getFormat("00").format(hours) + "h ";
        formattedRa += NumberFormat.getFormat("00").format(minutes) + "m";
        formattedRa += NumberFormat.getFormat("00.00").format(seconds) + "s";

        return url + NED_SERVICE_EQUINOX + "=" + NED_DEFAULT_EQUINOX + "&" + NED_SERVICE_RADIUS
                + "=" + NED_DEFAULT_RADIUS_ARCMIN + "&" + NED_SERVICE_HCONST + "=" + NED_DEFAULT_HCONST
                + "&" + NED_SERVICE_OMEGAM + "=" + NED_DEFAULT_OMEGAM + "&" + NED_SERVICE_OMEGAV
                + "=" + NED_DEFAULT_OMEGAV + "&" + NED_SERVICE_CORR_Z + "=" + NED_DEFAULT_CORR_Z
                + "&" + NED_SERVICE_Z_CONSTRAINT + "=" + NED_DEFAULT_Z_CONSTRAINT + "&"
                + NED_SERVICE_Z_VALUE1 + "=" + NED_DEFAULT_Z_VALUE1 + "&" + NED_SERVICE_Z_VALUE2
                + "=" + NED_DEFAULT_Z_VALUE2 + "&" + NED_SERVICE_Z_UNIT + "=" + NED_DEFAULT_Z_UNIT
                + "&" + NED_SERVICE_OT_INCLUDE + "=" + NED_DEFAULT_OT_INCLUDE + "&"
                + NED_SERVICE_NMP_OP + "=" + NED_DEFAULT_NMP_OP + "&" + NED_SERVICE_OUT_EQUINOX
                + "=" + NED_DEFAULT_OUT_EQUINOX + "&" + NED_SERVICE_OBJ_SORT + "="
                + NED_DEFAULT_OBJ_SORT + "&" + NED_SERVICE_OF + "=" + NED_DEFAULT_OF + "&"
                + NED_SERVICE_ZV_BREAKER + "=" + NED_DEFAULT_ZV_BREAKER + "&"
                + NED_SERVICE_LIST_LIMIT + "=" + NED_DEFAULT_LIST_LIMIT + "&"
                + NED_SERVICE_IMG_STAMP + "=" + NED_DEFAULT_IMG_STAMP + "&"
                + NED_SERVICE_SEARCH_TYPE + "=" + NED_DEFAULT_SEARCH_TYPE + "&" + NED_SERVICE_LON
                + "=" + formattedRa + "&" + NED_SERVICE_LAT + "=" + decDeg;
    }
    
    public static String buildWwtURL(double raDeg, double decDeg, String cooFrame) {
    	if (AladinLiteConstants.FRAME_GALACTIC.toLowerCase().equalsIgnoreCase(cooFrame)) {
    		Double[] ccInJ2000 = CoordinatesConversion.convertPointGalacticToJ2000(raDeg, decDeg);
    		raDeg = ccInJ2000[0];
    		decDeg = ccInJ2000[1];
    	}
    	
        
    	return buildWwtURLJ2000(raDeg, decDeg);
    }
    public static String buildWwtURLJ2000(double raDegJ2000, double decDegJ2000) {
		String baseWwtUrl = "http://www.worldwidetelescope.org/webclient/default.aspx?wtml=";

		/*
		 * ESASky fov is always horizontal and in unit Degrees
		 * WWT zoom is either vertical or horizontal, whichever is smaller. 
		 * WWT zoom uses unit tens of arcmin, 
		 * i.e. 1 ESASky fov degree is 1 * 60 / 10 = 6 WWT zoom, assuming viewport width is smaller than viewport height.
		 * Otherwise correct with window ratio
		 */
		double windowRatio = (double) MainLayoutPanel.getMainAreaWidth() / (double)MainLayoutPanel.getMainAreaHeight();
		if(windowRatio < 1) {
			windowRatio = 1;
		}
		Double wwtZoom = AladinLiteWrapper.getAladinLite().getFovDeg() / windowRatio * 60 / 10; 
		return baseWwtUrl 
				+ URL.encodeQueryString("http://www.worldwidetelescope.org/wwtweb/goto.aspx?"
						+ "ra=" + raDegJ2000 * 24/360 //WWT uses unit hours
						+ "&dec=" + decDegJ2000
						+ "&zoom=" + wwtZoom 
						+ "&wtml=true");
    }

}
