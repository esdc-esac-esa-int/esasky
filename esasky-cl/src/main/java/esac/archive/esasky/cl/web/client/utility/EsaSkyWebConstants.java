package esac.archive.esasky.cl.web.client.utility;

import com.google.gwt.i18n.client.Dictionary;

import esac.archive.absi.modules.cl.aladinlite.widget.client.model.ColorPalette;
import esac.archive.esasky.ifcs.model.client.HiPS;
import esac.archive.esasky.ifcs.model.client.HiPS.HiPSImageFormat;
import esac.archive.esasky.ifcs.model.client.HiPSCoordsFrame;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;

/**
 * @author ESDC team Copyright (c) 2015- European Space Agency
 */
public class EsaSkyWebConstants {

    /** Prevents Utility class calls. */
    protected EsaSkyWebConstants() {
        // prevents calls from subclass
        throw new UnsupportedOperationException();
    }

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
    public static final String OBSERVATIONS_URL = BACKEND_CONTEXT + "/observations";
    public static final String SSO_URL = BACKEND_CONTEXT + "/sso";
    public static final String SPECTRA_URL = BACKEND_CONTEXT + "/spectra";
    public static final String CATALOGS_URL = BACKEND_CONTEXT + "/catalogs";
    public static final String EXT_TAP_URL_TABULATOR = BACKEND_CONTEXT + "/tabulator-ext-taps";
    public static final String EXT_TAP_URL = BACKEND_CONTEXT + "/ext-taps";
    public static final String EXT_TAP_REQUEST_URL = EXT_TAP_URL + "?" + EsaSkyConstants.EXT_TAP_ACTION_FLAG + "=" + EsaSkyConstants.EXT_TAP_ACTION_REQUEST;
    public static final String EXT_TAP_REQUEST_URL_TABULATOR = EXT_TAP_URL_TABULATOR + "?" + EsaSkyConstants.EXT_TAP_ACTION_FLAG + "=" + EsaSkyConstants.EXT_TAP_ACTION_REQUEST;
    public static final String EXT_TAP_GET_TAPS_URL = EXT_TAP_URL + "?" + EsaSkyConstants.EXT_TAP_ACTION_FLAG +  "=" + EsaSkyConstants.EXT_TAP_ACTION_DESCRIPTORS;
    public static final String PUBLICATIONS_URL = BACKEND_CONTEXT + "/publications";
    public static final String PUBLICATIONS_BY_SOURCE_URL = BACKEND_CONTEXT + "/publications-by-source";
    public static final String PUBLICATIONS_BY_AUTHOR_URL = BACKEND_CONTEXT + "/publications-by-author";
    public static final String PUBLICATIONS_SOURCES_BY_BIBCODE_URL = BACKEND_CONTEXT + "/sources-from-bibcode";
    public static final String PUBLICATIONS_SOURCES_BY_AUTHOR_URL = BACKEND_CONTEXT + "/sources-from-author";
    public static final String PUBLICATIONS_DETAILS_URL = BACKEND_CONTEXT + "/publication-details";
    public static final String HIPS_SOURCES_URL = BACKEND_CONTEXT + "/hips-sources";
    public static final String RANDOM_SOURCE_URL = BACKEND_CONTEXT + "/random-source";
    public static final String SIMBAD_TAP_URL = BACKEND_CONTEXT + "/simbad-tap";
    public static final String BANNER_MESSAGE_URL = BACKEND_CONTEXT + "/banner-message";
    public static final String HIPS_STORAGE_URL = BACKEND_CONTEXT + "/location";
    public static final String DATALINK_URL = BACKEND_CONTEXT + "/datalink-url";
    public static final String IMAGE_LOADER_URL = BACKEND_CONTEXT + "/image-loader";

    public static final String SOURCE_TYPE = "sourceType";
    
    public static final String URL_PARAM_TARGET = "target";
    public static final String URL_PARAM_HIPS = "hips";
    public static final String URL_PARAM_FOV = "fov";

    public static final String URL_PARAM_FRAME_COORD = "cooframe";
    
    public static final String URL_PARAM_SCI_MODE = "sci";
    
    public static final String URL_PARAM_HIDE_WELCOME = "hide_welcome";
    public static final String URL_PARAM_HIDE_SCI = "hide_sci_switch";
    
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
    public static final int MAX_SOURCES_FOR_MOBILE = 200;
    
    public enum SourceType {
        MULTITARGET("Multitarget"), CATALOGUE("Catalogue"), PUBLICATION("Publication"), SURVEY(
                "Survey"), PLANNING("Planning");

        String sourceType;

        /**
         * Class Constructor.
         * @param value Input String
         */
        private SourceType(final String value) {
            this.sourceType = value;
        }

        public String getSourceType() {
            return this.sourceType;
        }

        @Override
        public String toString() {
            return this.sourceType;
        }
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

    /** ESA_SKY URLS **/
    public static final String ESA_SKY_RELEASE_NOTES_URL = "//www.cosmos.esa.int/web/esdc/esasky-release-notes";
    public static final String ESA_SKY_HELP_PAGES_URL = "//www.cosmos.esa.int/web/esdc/esasky-how-to";
    public static final String ESA_SKY_NEWSLETTER_URL = "//www.cosmos.esa.int/web/esdc/newsletter";
    public static final String ESA_SKY_USER_ECHO = "https://esdc.userecho.com/communities/1-esasky";
    public static final String ESA_SKY_ABOUTUS_URL = "//www.cosmos.esa.int/web/esdc/esasky-credits";
    public static final String COOKIE_POLICY_URL = "//www.cosmos.esa.int/web/esdc/esasky-help#cookies";
    public static final String ESASKY_INFO_EMAIL = "esaskyinfo@sciops.esa.int";
    
    
    /**
     * AladinLite Galactic string representation. It is one of the String returned by
     * getAladinLite().getCooFrame()
     */
    public static final String ALADIN_GALACTIC_COOFRAME = "galactic";

    public static String INTERNET_EXPLORER_APPNAME = "Microsoft Internet Explorer";
}
