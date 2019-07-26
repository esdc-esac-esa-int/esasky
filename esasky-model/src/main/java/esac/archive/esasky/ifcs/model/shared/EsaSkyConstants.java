package esac.archive.esasky.ifcs.model.shared;

import esac.archive.absi.modules.cl.aladinlite.widget.client.model.ColorPalette;
import esac.archive.esasky.ifcs.model.client.HiPS.HiPSImageFormat;
import esac.archive.esasky.ifcs.model.client.HipsWavelength;

/**
 * @author ESDC team Copyright (c) 2015- European Space Agency
 */
public class EsaSkyConstants {

    /** Prevents Utility class calls. */
    protected EsaSkyConstants() {
        // prevents calls from subclass
        throw new UnsupportedOperationException();
    }

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
                                                                "/servlet/tiny-url-action?");

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

    /*************************************************************************/
    /** DEFAULT INIT VALUES **/
    /*************************************************************************/
    public enum ReturnType {
        JSON("json"), VOTABLE("vot"), CSV("csv"), ASCII("ascii");

    	private String name;
    	private ReturnType(String name) {
    		this.name = name;
    	}
		@Override
		public String toString() {
			return name;
		}
        
        
    }

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

    /**
     * Model related constants
     */
    public static final String SOURCE_TAP_RA = "ra";
    public static final String SOURCE_TAP_DEC = "dec";
    public static final String SOURCE_TAP_NAME = "name";

    public static final String OBS_TAP_RA = "ra_deg";
    public static final String OBS_TAP_DEC = "dec_deg";
    public static final String OBS_TAP_NAME = "observation_id";

    
    /*************************************************************************/
    /** INTERNATIONALIZATION VALUES **/
    /*************************************************************************/
    public static final String DEFAULT_LANGCODE = "en";
    public static final String[] AVAILABLE_LANGCODES = {"en", "es"};
    
}
