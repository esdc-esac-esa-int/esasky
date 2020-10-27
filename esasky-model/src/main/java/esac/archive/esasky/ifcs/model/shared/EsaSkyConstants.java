package esac.archive.esasky.ifcs.model.shared;

import java.util.List;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.LinkedList;

import esac.archive.absi.modules.cl.aladinlite.widget.client.model.ColorPalette;
import esac.archive.esasky.ifcs.model.client.HiPS.HiPSImageFormat;
import esac.archive.esasky.ifcs.model.client.HipsWavelength;

/**
 * @author ESDC team Copyright (c) 2015- European Space Agency
 */
public class EsaSkyConstants {


    /*************************************************************************/
    /** GENERAL CONSTANTS **/
    /*************************************************************************/

    /** Replace pattern. */
    public static final String REPLACE_PATTERN = "@@";

    /** App name. */
    public static final String APP_NAME = "ESASky";

    /** Server Context. */
    public static final String PROP_SERVER_CONTEXT = "server.context";
    /** Tap Context. */
    public static final String PROP_TAP_CONTEXT = "tap.context";

    /** Not target found. */
    public static final String NOT_TARGET_FOUND = "No astronomical object found";

    /** Default FoV. */
    public static final Double DEFAULT_FOV = 0.1; // 6 arcmin
    
    /** Default FoV. */
    public static final Double MIN_ALLOWED_DEFAULT_FOV = 0.09; // < 6 arcmin

    /** JSON format. */
    public static final String JSON = "json";

    /** VOTABLE format. */
    public static final String VOTABLE = "votable";

    public static final String INTERNATIONALIZATION_LANGCODE_URL_PARAM = "lang";
    
    
    

    /*************************************************************************/
    /** DEFAULT INIT VALUES **/
    /*************************************************************************/

    /** AladinLite ID name */
    public static final String ALADIN_DIV_NAME = "aladin-container";

    /** Default Hips map. */
    public static final String ALADIN_DEFAULT_HIPS_MAP = "DSS2 color";
    /** Default survey Id. */
    public static final String ALADIN_DEFAULT_SURVEY_ID = "DSS2 Color";
    /** Default survey name. */
    public static final String ALADIN_DEFAULT_SURVEY_NAME = "DSS2 color";
    /** Default survey name. */
    public static final HipsWavelength DEFAULT_WAVELENGTH = HipsWavelength.OPTICAL;
    /** Default survey URL. */
    public static final String ALADIN_DEFAULT_SURVEY_URL = "//cdn.skies.esac.esa.int/DSSColor";
    /** Default HiPS cooframe. */
    public static final String ALADIN_HiPS_DEFAULT_COO_FRAME = "equatorial";
    /** Default Hips Image format. */
    public static final HiPSImageFormat ALADIN_DEFAULT_IMG_FORMAT = HiPSImageFormat.jpg;
    /** Default color palette. */
    public static final ColorPalette ALADIN_DEFAULT_COLOR_MAP = ColorPalette.NATIVE;
    /** Default norder. */
    public static final Integer ALADIN_DEFAULT_NORDER = 9;
    /** Default target. */
    public static final String ALADIN_DEFAULT_TARGET = "M51";
    /** Default FoV value. */
    public static final double initFoV = 1.0;

    /*************************************************************************/
    /** Patterns used to validate RA/DEC format **/
    /*************************************************************************/

    private static final String BLANK = "\\s*";

    private static final String SIGN = "([+-])?";

    private static final String BEGIN = "^" + BLANK;

    private static final String END = BLANK + "$";

    // Atomic elements
    private static final String HOUR = "([0-2]?[0-9])";

    private static final String MIN = "([0-5]?[0-9])";

    private static final String SEC = "([0-5]?[0-9]\\.?\\d*)";

    private static final String SEP2 = "[\\s\\:\\,]+";

    private static final String DEG = "(\\d{1,2})";

    private static final String HOUR_UNIT = "(?:([0-2]?[0-9])h)?";

    private static final String MIN_UNIT = "(?:([0-5]?[0-9])m)?";

    private static final String SEC_UNIT = "(?:([0-5]?[0-9]\\.?\\d*)s)?";

    private static final String DEG_UNIT = "(?:(\\d{1,2})d)?";

    private static final String AMIN_UNIT = "(?:([0-5]?[0-9])[m\\'])?";

    private static final String ASEC_UNIT = "(?:([0-5]?[0-9]\\.?\\d*)(?:s|\\'{2}))?";

    /** RA format: 1, 2 or 3 numbers without units or any with them. */
    public static final String RA_DECIMAL_PATTERN = BEGIN + "([\\d\\.]+)([dh])?" + END;

    public static final String RA_TWO_NUMBERS_PATTERN = BEGIN + HOUR + SEP2 + MIN + END;

    public static final String RA_THREE_NUMBERS_PATTERN = BEGIN + HOUR + SEP2 + MIN + SEP2 + SEC
            + END;

    public static final String RA_UNITS_PATTERN = BEGIN + HOUR_UNIT + BLANK + MIN_UNIT + BLANK
            + SEC_UNIT + END;

    /** DEC format: 1, 2 or 3 numbers without units or any with them. */
    public static final String DEC_DECIMAL_PATTERN = BEGIN + SIGN + BLANK + "([\\d\\.]+)d?" + END;

    public static final String DEC_TWO_NUMBERS_PATTERN = BEGIN + SIGN + BLANK + DEG + SEP2 + MIN
            + END;

    public static final String DEC_THREE_NUMBERS_PATTERN = BEGIN + SIGN + BLANK + DEG + SEP2 + MIN
            + SEP2 + SEC + END;

    public static final String DEC_UNITS_PATTERN = BEGIN + SIGN + DEG_UNIT + BLANK + AMIN_UNIT
            + BLANK + ASEC_UNIT + END;
    /** String starts with character. */
    public static final String TARGET_NAME = BEGIN + "[a-zA-Z,-]+";

    /*************************************************************************/
    /** View Constants **/
    /*************************************************************************/

    /** Buttons Standard width. */
    public static final String BUTTONS_STANDARD_WIDTH = "145px";

    /** Type of button. */
    public static final String SAVE_BUTTON_TYPE = "save";
    /** Type of button. */
    public static final String SEND_BUTTON_TYPE = "send";
    /** Max catalogue search size. */
    public static final Integer MAX_CATALOG_SEARCH_SIZE = 50000;
    /** FoV multiplier for multi-target player. */
    public static final double MULTITARGET_FOV = 2.0;

    /** Default HiPS cooframe. */
    public static final String SIMBAD_J2000_COO_FRAME = "ICRS";
    /** Default HiPS cooframe. */
    public static final String SIMBAD_GALACTIC_COO_FRAME = "Gal";

    public static final String SIMBAD_OBJECT_NOT_IDENTIFIED = "No astronomical object found";

    /** USER ERROR MESSAGES */
    public static final String ERROR_MSG_TOO_MANY_USERS = "The server is taking too long to answer, sorry. "
            + "Please, either submit again your request or refine your search. "
            + "It could be that in this moment there are many users connected.";

    public static final String ERROR_MSG_UPLOAD_FILE = "The server is taking too long to answer, sorry. "
            + "Please, either try to submit again your file. "
            + "It could be that in this moment there are many users connected.";

    public static final String ERROR_MSG_SIMBAD = "Sorry for the inconvinience, "
            + "but it seems that the connection between our servers and SIMABD "
            + "resolver service has some problems. Please retry your search.";

    public static final String ERROR_MSG_SAMP = "Something went wrong in the comunication between SAMP applications. Please retry your search and if the problem persists, tell us using either the userecho page or the helpdesk.";


    public static final String DATALINK_URL_PARAM = "url";
    public static final String IMAGELOADER_URL_PARAM = "url";
    
    /*************************************************************************/
    /** INTERNATIONALIZATION VALUES **/
    /*************************************************************************/
    public static final String DEFAULT_LANGCODE = "en"; 
    public static final List<SimpleEntry<String, String>> AVAILABLE_LANGUAGES = new LinkedList<SimpleEntry<String, String>>(
    		Arrays.asList(
    				new SimpleEntry<String, String>("en", "En"),
    				new SimpleEntry<String, String>("es", "Es")
    				,new SimpleEntry<String, String>("zh", "中文")
    				)
    		);
    
    /*************************************************************************/
    /** COMMUNICATION CONSTANTS **/
    /*************************************************************************/
    
    public static final String EXT_TAP_ACTION_FLAG = "ACTION";
    public static final String EXT_TAP_TARGET_FLAG = "TAP_TARGET";
    public static final String EXT_TAP_ADQL_FLAG = "ADQL";
    public static final String EXT_TAP_ACTION_DESCRIPTORS = "DESCRIPTORS";
    public static final String EXT_TAP_ACTION_RESET = "RESET";
    public static final String EXT_TAP_ACTION_REQUEST = "REQUEST";
    public static final String EXT_TAP_URL = "TAP_URL";
    public static final String EXT_TAP_RESPONSE_FORMAT = "RESPONSE_FORMAT";
    
    /*************************************************************************/
    /** IVOA OBSCORE **/
    /*************************************************************************/
    
    public static final String OBSCORE_COLLECTION = "obs_collection";
    public static final String OBSCORE_FACILITY = "facility_name";
    public static final String OBSCORE_DATAPRODUCT = "dataproduct_type";
    public static final String OBSCORE_FOV = "s_fov";
    public static final String OBSCORE_SREGION = "s_region";
    public static final String CATALOGUE = "catalogue";
    public static final String TABLE_NAME = "table_name";
    public static final String HEASARC_TABLE = "table_name";
    
    /*************************************************************************/
    /** TREE MAP **/
    /*************************************************************************/
    
    public static final String TREEMAP_TYPE_MISSION = "mission";
    public static final String TREEMAP_TYPE_SERVICE = "service";
    public static final String TREEMAP_TYPE_SUBCOLLECTION= "collection";
    public static final String TREEMAP_TYPE_DATAPRODUCT = "dataproduct";
    
    /*************************************************************************/
    /** MOC **/
    /*************************************************************************/
    
    public static final String HEALPIX_IPIX = "healpix_index";
    public static final String HEALPIX_ORDER = "healpix_order";
    public static final String HEALPIX_COUNT = "healpix_count";
    
    public static final String Q3C_IPIX = "moc_ipix";
    public static final String Q3C_ORDER = "moc_order";
    public static final String Q3C_COUNT = "moc_count";
    
    /*************************************************************************/
    /** JWST Instruments **/
    /*************************************************************************/
    
    public enum JWSTInstrument {
        FGS1("FGS", "FGS1_FULL"),
        FGS2("FGS", "FGS2_FULL"), 
        NIRISS_CEN("NIRISS","NIS_CEN"),
        NIRSPEC_MSA("NIRSpec","NRS_FULL_MSA"),
        NIRSPEC_MSA1("NIRSpec","NRS_FULL_MSA1"),
        NIRSPEC_MSA2("NIRSpec","NRS_FULL_MSA2"),
        NIRSPEC_MSA3("NIRSpec","NRS_FULL_MSA3"),
        NIRSPEC_MSA4("NIRSpec","NRS_FULL_MSA4"),
        NIRSPEC_IFU("NIRSpec","NRS_FULL_IFU"),
        NIRSPEC_SLIT1("NIRSpec","NRS_S200A1_SLIT"),
        NIRSPEC_SLIT2("NIRSpec","NRS_S200A2_SLIT"),
        NIRSPEC_SLIT3("NIRSpec","NRS_S200B1_SLIT"),
        NIRSPEC_SLIT4("NIRSpec","NRS_S400A1_SLIT"),
        NIRSPEC_SLIT5("NIRSpec","NRS_S1600A1_SLIT"),
        NIRCAFULL("NIRCam","NRCALL_FULL"),
        NIRCA1("NIRCam","NRCA1_FULL"),
        NIRCA2("NIRCam","NRCA2_FULL"),
        NIRCA3("NIRCam","NRCA3_FULL"),
        NIRCA4("NIRCam","NRCA4_FULL"),
        NIRCA5("NIRCam","NRCA5_FULL_OSS"),
        NIRCB1("NIRCam","NRCB1_FULL"),
        NIRCB2("NIRCam","NRCB2_FULL"),
        NIRCB3("NIRCam","NRCB3_FULL"),
        NIRCB4("NIRCam","NRCB4_FULL"),
        NIRCB5("NIRCam","NIRCB5_FULL"),
        NIRCA2_MASK210R("NIRCam","NRCA2_MASK210R"),
        NIRCA5_MASK335R("NIRCam","NRCA5_MASK335R"),
        NIRCA5_MASK430R("NIRCam","NRCA5_MASK430R"),
        NIRCA4_MASKSWB("NIRCam","NRCA4_MASKSWB"),
        NIRCA5_MASKLWB("NIRCam","NRCA5_MASKLWB"),
        NIRCB1_MASK210R("NIRCam","NRCB1_MASK210R"),
        NIRCB5_MASK335R("NIRCam","NRCB5_MASK335R"),
        NIRCB5_MASK430R("NIRCam","NRCB5_MASK430R"),
        NIRCB3_MASKSWB("NIRCam","NRCB3_MASKSWB"),
        NIRCB5_MASKLWB("NIRCam","NRCB5_MASKLWB"),
        MIRIM_FULL("MIRI","MIRIM_FULL"),     
        MIRIM_MASK1065("MIRI","MIRIM_MASK1065"),
        MIRIM_ILLUM("MIRI","MIRIM_ILLUM"),
        MIRIM_FP1MIMF("MIRI","MIRIM_FP1MIMF"),
        MIRIM_MASK1140("MIRI","MIRIM_MASK1140"),
        MIRIM_MASK1550("MIRI","MIRIM_MASK1550"),
        MIRIM_MASKLYOT("MIRI","MIRIM_MASKLYOT"),
        MIRIM_CHANNEL1A("MIRI","MIRIFU_CHANNEL1A");

    	private String instrName;
    	private String aperName;
    	private JWSTInstrument(String instrName, String aperName) {
    		this.instrName = instrName;
    		this.aperName = aperName;
    	}
		@Override
		public String toString() {
			return instrName;
		}
		
		public String getAperName() {
			return aperName;
		}
        
        
    }
    
    /*************************************************************************/
    /** DEFAULT INIT VALUES **/
    /*************************************************************************/
    public enum ReturnType {
        JSON("json", "application/json"), VOTABLE("vot", "application/x-votable+xml"), CSV("csv", "text/csv"), ASCII("ascii", "");

        private String name;
        private String mimeType;
        private ReturnType(String name, String mimeType) {
            this.name = name;
            this.mimeType = mimeType;
        }
        @Override
        public String toString() {
            return name;
        }
        
        public String getMimeType() {
            return mimeType;
        }
        
        
    }

    /**
     * HTTP GET actions enum.
     * @author mhsarmiento
     *
     */
    public enum HttpServlet {
        DATA_GET_SERVLET("/servlet/data-action?"), METADATA_GET_SERVLET("/servlet/metadata-action?"), METADATA_COUNT_SERVLET(
                "/servlet/metadata-count-action?"), SUGGESTION_SERVLET(
                        "/servlet/suggestion-action?"), RSS_GET_SERVLET("/servlet/rss-action?"), TARGET_NAME_RESOLVER(
                                "/servlet/targetresolver-action?"), FITS_SPECTRA_SERVLET(
                "/servlet/fits-spectra-action?"), FITS_METADATA_SERVLET(
                "/servlet/fits-metadata-action?"), FITS_IMAGE_SERVLET("/servlet/fits-image-action?"), QUERY_RESULTS_DOWNLOAD_SERVLET(
                                                        "/servlet/query-results-download-action?"), TINY_URL_SERVLET(
                                                                "/servlet/tiny-url-action?"), VOTABLE_BUFFER_SERVLET("/servlet/votable-buffer?");

        /** enum attrubite. */
        private String value;

        /**
         * classConstrutor.
         * @param inputValue Input String.
         */
        HttpServlet(final String inputValue) {
            this.value = inputValue;
        }

        @Override
        public String toString() {
            return value;
        }

        /**
         * getValue().
         * @return String
         */
        public String getValue() {
            return value;
        }

    }
    
    /** Prevents Utility class calls. */
    protected EsaSkyConstants() {
        // prevents calls from subclass
        throw new UnsupportedOperationException();
    }
}
