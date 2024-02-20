package esac.archive.esasky.cl.web.client.utility;

import com.google.gwt.i18n.client.Dictionary;

import esac.archive.esasky.ifcs.model.client.HiPS;
import esac.archive.esasky.ifcs.model.client.HiPS.HiPSImageFormat;
import esac.archive.esasky.ifcs.model.client.HiPSCoordsFrame;
import esac.archive.esasky.ifcs.model.shared.ColorPalette;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;

/**
 * @author ESDC team Copyright (c) 2015- European Space Agency
 */
public class EsaSkyWebConstants {

    /*************************************************************************/
    /** GENERAL CONSTANTS **/
    /*************************************************************************/
    /** Server context Path. */
    public static final String SERVER_CONTEXT = Dictionary.getDictionary("serverProperties").get(
            "contextPath");
    /** Tap Context Path. */
    public static final String TAP_CONTEXT = Dictionary.getDictionary("serverProperties").get(
            "tapContext");

    /*************************************************************************/
    /** SERVLET URLS **/
    /*************************************************************************/
    public static final String BACKEND_CONTEXT = TAP_CONTEXT;
    public static final String FILE_RESOLVER_URL = BACKEND_CONTEXT + "/file-resolver";
    public static final String FILE_RESOLVER_GALACTIC_URL = BACKEND_CONTEXT + "/file-resolver-gal";
    public static final String FILE_RESOLVER_J2000_URL = BACKEND_CONTEXT + "/file-resolver-j2000";
    public static final String RESOLVER_URL = BACKEND_CONTEXT + "/resolver";
    public static final String GENERAL_RESOLVER_URL = BACKEND_CONTEXT + "/generalresolver";
    public static final String SSO_RESOLVER_URL = BACKEND_CONTEXT + "/ssoresolver";
    public static final String DATA_REQUEST_URL = BACKEND_CONTEXT + "/data-request";
    public static final String TAP_SYNC_URL = BACKEND_CONTEXT + "/tap/sync";
    public static final String TAP_DESCRIPTOR_URL = BACKEND_CONTEXT + "/tap/descriptors";
    public static final String TAP_USERTABLES_URL = BACKEND_CONTEXT + "/tap/usertables";
    public static final String TAP_USERLAYOUTS_URL = BACKEND_CONTEXT + "/tap/layouts";
    public static final String TAP_USERSESSIONS_URL = BACKEND_CONTEXT + "/tap/sessions";
    public static final String IMAGES_URL = BACKEND_CONTEXT + "/outreach-image";
    public static final String SSO_URL = BACKEND_CONTEXT + "/sso";
    public static final String SPECTRA_URL = BACKEND_CONTEXT + "/spectra";
    public static final String CATALOGS_URL = BACKEND_CONTEXT + "/catalogs";
    public static final String EXT_TAP_URL = BACKEND_CONTEXT + "/ext-taps";
    public static final String UPLOAD_TABLE_URL = BACKEND_CONTEXT + "/Upload";
    public static final String EXT_TAP_REQUEST_URL = EXT_TAP_URL + "?" + EsaSkyConstants.EXT_TAP_ACTION_FLAG + "=" + EsaSkyConstants.EXT_TAP_ACTION_REQUEST;
    public static final String EXT_TAP_GET_TAPS_URL = EXT_TAP_URL + "?" + EsaSkyConstants.EXT_TAP_ACTION_FLAG +  "=" + EsaSkyConstants.EXT_TAP_ACTION_DESCRIPTORS;
    public static final String PUBLICATIONS_URL = BACKEND_CONTEXT + "/publications";
    public static final String PUBLICATIONS_BY_SOURCE_URL = BACKEND_CONTEXT + "/publications-by-source";
    public static final String PUBLICATIONS_BY_AUTHOR_URL = BACKEND_CONTEXT + "/publications-by-author";
    public static final String PUBLICATIONS_SOURCES_BY_BIBCODE_URL = BACKEND_CONTEXT + "/sources-from-bibcode";
    public static final String PUBLICATIONS_SOURCES_BY_AUTHOR_URL = BACKEND_CONTEXT + "/sources-from-author";
    public static final String PUBLICATIONS_DETAILS_URL = BACKEND_CONTEXT + "/publication-details";
    public static final String GW_URL = BACKEND_CONTEXT + "/gw";
    public static final String ICECUBE_URL = BACKEND_CONTEXT + "/icecube";
    public static final String HIPS_SOURCES_URL = BACKEND_CONTEXT + "/hips-sources";
    public static final String RANDOM_SOURCE_URL = BACKEND_CONTEXT + "/random-source";
    public static final String SIMBAD_TAP_URL = BACKEND_CONTEXT + "/simbad-tap";
    public static final String BANNER_MESSAGE_URL = BACKEND_CONTEXT + "/banner-message";
    public static final String HIPS_STORAGE_URL = BACKEND_CONTEXT + "/location";
    public static final String DATALINK_URL = BACKEND_CONTEXT + "/datalink-url";
    public static final String IMAGE_LOADER_URL = BACKEND_CONTEXT + "/image-loader";
    public static final String HIPSLIST_URL = BACKEND_CONTEXT + "/global-hipslist";
    public static final String TAPREGISTRY_URL = BACKEND_CONTEXT + "/tap-registry";

    public static final String URL_PARAM_TARGET = "target";
    public static final String URL_PARAM_HIPS = "hips";
    public static final String URL_PARAM_FOV = "fov";

    public static final String URL_PARAM_HST_IMAGE = "hst_image";
    public static final String URL_PARAM_JWST_IMAGE = "jwst_image";
    public static final String URL_PARAM_EUCLID_IMAGE = "euclid_image";
    
    public static final String URL_PARAM_FRAME_COORD = "cooframe";
    
    public static final String URL_PARAM_SCI_MODE = "sci";
    public static final String URL_PARAM_TOGGLE_COLUMNS = "toggle_columns";
    
    public static final String URL_PARAM_HIDE_WELCOME = "hide_welcome";
    public static final String URL_PARAM_HIDE_SCI = "hide_sci_switch";
    public static final String URL_PARAM_LAYOUT = "layout";
    public static final String URL_PARAM_HIDE_BANNER_INFO = "hide_banner_info";
    public static final String URL_PARAM_SHOW_EVA = "show_eva";
    public static final String URL_PARAM_HIDE_FOOTPRINTS = "hide_footprints";
    public static final String SCI_MODE_COOKIE = "IsInSciMode";
    
    public static final boolean SINGLE_COUNT_ENABLED = true; // If enabled the Dynamic count will go against the single fast-count, else one request per mission
    
    public static final boolean RANDOM_SOURCE_ON_STARTUP = true; // Show a random source given by server on startup
    public static final int RANDOM_SOURCE_CALL_TIMEOUT = 1500; //Timeout on ms to abort random source call and show default source
    
    public static final boolean PUBLICATIONS_UPDATE_ON_FOV_CHANGE = false; // Automatically update sources with publications in all sky whe fov changes
    public static final boolean PUBLICATIONS_RETRIEVE_DATA_FROM_SIMBAD = true; // If true, the source by fov queries will be against SIMBAD, else against EsaSkyTAP
    public static final boolean PUBLICATIONS_RETRIEVE_PUB_COUNT_FROM_SIMBAD = false; // If true and not SINGLE_COUNT_ENABLED, the publications count in FOV will be against SIMBAD, else against EsaSkyTAP
    public static final String PUBLICATIONS_SHOW_ALL_AUTHORS_TEXT = "et al."; // Text to show on publication's resultsPanel row if there are more than PUBLICATIONS_MAX_AUTHORS authors
    public static final int PUBLICATIONS_MAX_AUTHORS = 3; // Max authors per resultPanel row, if there are more then PUBLICATIONS_SHOW_ALL_AUTHORS_TEXT will be shown
    public static final String PUBLICATIONS_BIBCODE_URL_PARAM = "bibcode"; //Bibcode URL parameter, if passed EsaSky must show the sources linked to this publication bibcode
    public static final String PUBLICATIONS_AUTHOR_URL_PARAM = "simbad_author"; //Author URL parameter, if passed EsaSky must show the sources linked to this simbad author

    public static final int MAX_SOURCES_IN_TARGETLIST = 3000;
    public static final int MAX_SHAPES_FOR_MOBILE = 200;
    
    public static final double EXTTAP_FOV_LIMIT = 5.0;  

    /** ESA_SKY URLS **/
    public static final String ESA_SKY_RELEASE_NOTES_URL = "//www.cosmos.esa.int/web/esdc/esasky-release-notes";
    public static final String ESA_SKY_HELP_PAGES_URL = "//www.cosmos.esa.int/web/esdc/esasky-how-to";
    public static final String ESA_SKY_NEWSLETTER_URL = "//www.cosmos.esa.int/web/esdc/newsletter";
    public static final String ESA_SKY_USER_ECHO = "https://esdc.userecho.com/communities/1-esasky";
    public static final String ESA_SKY_ABOUTUS_URL = "//www.cosmos.esa.int/web/esdc/esasky-credits";
    public static final String COOKIE_POLICY_URL = "//www.cosmos.esa.int/web/esdc/esasky-help#cookies";
    public static final String ESASKY_INFO_EMAIL = "esaskyinfo@sciops.esa.int";
    public static final String ESA_SKY_ACKNOWLEDGE_URL = "//www.cosmos.esa.int/web/esdc/esasky-credits#CITE";
    
    
    /**
     * AladinLite Galactic string representation. It is one of the String returned by
     * getAladinLite().getCooFrame()
     */
    public static final String ALADIN_GALACTIC_COOFRAME = "galactic";
    public static final String ALADIN_J2000_COOFRAME = "J2000";

    public static final String INTERNET_EXPLORER_APPNAME = "Microsoft Internet Explorer";
    
    public static final int MOC_FILTER_LIMIT = 10000000;
    public static final int MOC_GLOBAL_MINMAX_LIMIT = 3000000;
    

    /*************************************************************************/
    /** MODULES **/
    /*************************************************************************/
    public static final String MODULE_MODE_ESASKY = "ESASKY";
    public static final String MODULE_MODE_JWST = "JWST";
    public static final String MODULE_MODE_EUCLID = "EUCLID";
    public static final String MODULE_MODE_CLEAN = "CLEAN";
    public static final String MODULE_MODE_KIOSK = "KIOSK";
    public static final String MODULE_MODE_USER = "USER";

    
    public static final String MODULE_SCIENTIFIC_BUTTON = "scientific_toggle_button";
    public static final String MODULE_LANGUAGE = "language_button";
    public static final String MODULE_COOR_GRID = "coordinate_grid_button";
    public static final String MODULE_SCREENSHOT = "screenshot_button";
    public static final String MODULE_SHARE = "share_button";
    public static final String MODULE_HELP = "help_button";
    public static final String MODULE_DROPDOWN = "dropdown_menu";
    public static final String MODULE_SKIESMENU = "skies_menu";
    public static final String MODULE_FEEDBACK = "feedback_button";
    public static final String MODULE_LOGIN = "login_button";
    public static final String MODULE_OBS = "observations_button";
    public static final String MODULE_CAT = "catalogues_button";
    public static final String MODULE_SPE = "spectra_button";
    public static final String MODULE_EXTTAP = "exttap_button";
    public static final String MODULE_GW = "gw_button";
    public static final String MODULE_OUTREACH_IMAGE = "outreach_button";
    public static final String MODULE_OUTREACH_JWST = "outreach_jwst_button";
    public static final String MODULE_OUTREACH_EUCLID = "outreach_euclid_button";
    public static final String MODULE_SSO = "sso_button";
    public static final String MODULE_PUBLICATIONS = "publications_button";
    public static final String MODULE_TARGETLIST = "target_list_button";
    public static final String MODULE_TARGETLIST_UPLOAD = "target_list_upload";
    public static final String MODULE_JWST_PLANNING = "jwst_planning_button";
    public static final String MODULE_DICE = "dice_button";
    public static final String MODULE_SCIENCE_MODE = "science_mode";
    public static final String MODULE_SESSION = "session";
    public static final String MODULE_EVA_MENU = "eva_menu";
    public static final String MODULE_EVA = "eva";
    public static final String MODULE_SEARCH_BOX = "search_box";
    public static final String MODULE_SEARCH_TOOL = "search_tool";
    public static final String MODULE_SEARCH_IN_MENU = "search_in";
    public static final String MODULE_KIOSK_BUTTONS = "kiosk_buttons";
    public static final String MODULE_WELCOME_DIALOG = "welcome_dialog";
    
    // DEFAULT ONES
    public static final String MODULE_WWT_LINK = "wwtLink";
    public static final String MODULE_BANNERS_ALL_SIDE = "bannersOnAllSides";
    public static final String MODULE_TOGGLE_COLUMNS = "toggleColumns";
    public static final String MODULE_INTERNATIONALIZATION = "internationalization";
    public static final String MODULE_SHOW_MISSING_TRANSLATIONS = "showMissingTranslations";
    
    
    
    /*************************************************************************/
    /** STRING CONSTANTS **/
    /*************************************************************************/
    
    public static final String S_RA = "s_ra";
    public static final String S_DEC = "s_dec";
    public static final String S_REGION = "s_region";

    public static final String HST_MISSION = "HST";
    public static final String JWST_MISSION = "JWST";
    public static final String EUCLID_MISSION = "EUCLID";
    public static final String HEASARC_MISSION = "HEASARC";
    public static final String SCHEMA_OBSERVATIONS = "observations";
    public static final String SCHEMA_CATALOGUES = "catalogues";
    public static final String SCHEMA_ALERTS = "alerts";
    public static final String SCHEMA_PUBLIC = "public";
    public static final String SCHEMA_IMAGES = "images";
    public static final String SCHEMA_EXTERNAL = "external";
    public static final String CATEGORY_OBSERVATIONS = "observations";
    public static final String CATEGORY_SPECTRA = "spectra";
    public static final String CATEGORY_CATALOGUES = "catalogues";
    public static final String CATEGORY_SSO = "sso";
    public static final String CATEGORY_GRAVITATIONAL_WAVES = "gravitational_waves";
    public static final String CATEGORY_NEUTRINOS = "neutrinos";
    public static final String CATEGORY_PUBLICATIONS = "publications";
    public static final String CATEGORY_IMAGES = "images";
    public static final String CATEGORY_EXTERNAL = "external";



    /** Prevents Utility class calls. */
    protected EsaSkyWebConstants() {
        // prevents calls from subclass
        throw new UnsupportedOperationException();
    }
    
    public static HiPS getInitialHiPS() {
        HiPS hips = new HiPS();
        hips.setMission("Digitized Sky Survey");
        hips.setMissionURL("//archive.stsci.edu/dss/");
        hips.setInstrument("POSS-II, AAO and SERC plates");
        hips.setCreator("Centre de Données astronomiques de Strasbourg");
        hips.setCreatorURL("//cdsweb.u-strasbg.fr");
        hips.setCreationDate("2010-05-01T19:05Z");
        hips.setMoreInfoURL("//alasky.u-strasbg.fr/DSS/DSSColor/properties");
        hips.setSurveyId("DSS2 color");
        hips.setSurveyName("DSS2 color");
        hips.setSurveyRootUrl("//cdn.skies.esac.esa.int/DSSColor/");
        hips.setSurveyFrame(HiPSCoordsFrame.EQUATORIAL);
        hips.setMaximumNorder(9);
        hips.setImgFormat(HiPSImageFormat.jpg);
        hips.setIsDefault(true);
        hips.setColorPalette(ColorPalette.NATIVE);
        return hips;
    }
    
    
    /*************************************************************************/
    /** SESSION SAVE CONSTANTS **/
    /*************************************************************************/

    public static final String SESSION_LOCATION = "location";
    public static final String SESSION_RA = "ra";
    public static final String SESSION_DEC = "dec";
    public static final String SESSION_ROT = "rot";
    public static final String SESSION_FOV = "fov";
    public static final String SESSION_FRAME = "coo_frame";
    
    public static final String SESSION_HIPS = "hips";
    public static final String SESSION_HIPS_ARRAY = "stack";
    public static final String SESSION_HIPS_SLIDER = "current";
    public static final String SESSION_HIPS_NAME = "name";
    public static final String SESSION_HIPS_URL = "url";
    public static final String SESSION_HIPS_WAVELENGTH = "wavelength";
    public static final String SESSION_HIPS_COLORPALETTE = "colorpalette";
    public static final String SESSION_HIPS_CATEGORY = "category";
    public static final String SESSION_HIPS_DEFAULT = "default";

    public static final String SESSION_DATA = "data";
    public static final String SESSION_DATA_MISSION = "mission";
    public static final String SESSION_DATA_CATEGORY = "category";
    public static final String SESSION_DATA_TABLE = "table";
    public static final String SESSION_DATA_ISMOC = "is_moc";
    public static final String SESSION_DATA_HAS_PANEL = "has_panel";
    public static final String SESSION_DATA_ADQL = "adql";
    public static final String SESSION_DATA_FILTERS = "filters";
    public static final String SESSION_DATA_COLOR_MAIN = "color";
    public static final String SESSION_DATA_COLOR_SECOND = "color_secondary";
    public static final String SESSION_DATA_SIZE = "size";
    public static final String SESSION_DATA_LINESTYLE = "line_style";
    public static final String SESSION_DATA_SOURCE_STYLE = "source_style";
    public static final String SESSION_DATA_TAPURL = "tap_url";
    public static final String SESSION_SHOWING = "showing";

    public static final String SESSION_ICECUBE = "icecube";
    public static final String SESSION_MME = "multi_messenger_events";
    public static final String SESSION_GW_ID = "gw_id";

    public static final String SESSION_PLANNING = "planning";
    public static final String SESSION_PLANNING_APERTURE = "aperture";
    public static final String SESSION_PLANNING_INSTRUMENT = "instrument";
    public static final String SESSION_PLANNING_ALL = "all";
    public static final String SESSION_PLANNING_MISSION = "mission";

    public static final String SESSION_OUTREACH = "outreach_image";
    public static final String SESSION_OUTREACH_IMAGE_ID = "id";
    public static final String SESSION_OUTREACH_IMAGE_TELESCOPE = "telescope";
    public static final String SESSION_OUTREACH_IMAGE_OPACITY = "opacity";
    public static final String SESSION_OUTREACH_IMAGE_FOOTPRINT_SHOWING = "footprints_showing";
    public static final String SESSION_OUTREACH_IMAGE_PANEL_OPEN = "panel_open";

    public static final String SESSION_PUB = "publications";
    public static final String SESSION_PUB_TYPE = "type";
    public static final String SESSION_PUB_TYPE_AREA = "area";
    public static final String SESSION_PUB_TYPE_AUTHOR = "author";
    public static final String SESSION_PUB_TYPE_SOURCE = "source";
    public static final String SESSION_PUB_URL = "url";
    public static final String SESSION_PUB_AUTHOR = "author";
    public static final String SESSION_PUB_SOURCE = "author";
    public static final String SESSION_PUB_MAX_ROWS = "n_rows";
    public static final String SESSION_PUB_BIBCOUNT = "bibcount";

    public static final String SESSION_SETTINGS = "settings";
    public static final String SESSION_SETTINGS_GRID = "coo_grid";
    public static final String SESSION_SETTINGS_SEARCH = "search_area";

    public static final String SESSION_TREEMAP = "treemap";
    public static final String SESSION_TREEMAP_LOW = "low";
    public static final String SESSION_TREEMAP_HIGH = "high";
    public static final String SESSION_EXTERNAL_DATA_CENTERS = "external_data_centers";


}
