package esac.archive.esasky.cl.web.client.utility;

/**
 * Constants used in gwidgets.
 * @author Maria Henar Sarmiento Carrion Copyright (c) 2015 - European Space Agency
 */
public class SampConstants {
	
    /** Prevents Utility class calls. */
    protected SampConstants() {
        // prevents calls from subclass
        throw new UnsupportedOperationException();
    }

    /** Samp actions. */
    public enum SampAction {
        SEND_TABLE_TO_SAMP_APP, SEND_PRODUCT_TO_SAMP_APP, SEND_IMAGE, SEND_VOTABLE, UNREGISTER,SEND_FITS_TABLE_TO_SAMP_APP, SEND_REGION_TABLE_TO_SAMP_APP;
    }

    public static final String SAMP_VOTABLE_MESSAGE = "table.load.votable";
    public static final String SAMP_FITS_IMAGE_MESSAGE = "image.load.fits";
    public static final String SAMP_FITS_TABLE_MESSAGE = "table.load.fits";

    /** max num of register tries. */
    public static final int MAX_NUMBER_OF_SAMP_REGISTER_TRIES = 12;
    /** time between tries. */
    public static final int TIME_BETWEEN_TRIES = 2000;

    /** App name Aladin. */
    public static final String ALADIN_APP = "Aladin";
    /** App name SAOImage DS9. */
    public static final String DS9_APP = "SAOImage DS9";
    /** App name VOSpec. */
    public static final String VOSPEC_APP = "VOSpec";
    /** App name Topcat. */
    public static final String TOPCAT_APP = "topcat";
    /** App name Hub. */
    public static final String HUB_APP = "SAMP";

    /** App url. */
    public static final String SAMP_HUB_URL = "//astrojs.github.io/sampjs/hub/webhub.jnlp";
    /** TOPCAT App url. */
    public static final String TOPCAT_JNLP_URL = "//www.star.bristol.ac.uk/~mbt/topcat/topcat-full.jnlp";
    /** Aladin App url. */
    public static final String ALADIN_JNLP_URL = "//aladin.u-strasbg.fr/java/download/aladin.jnlp";
    /** VOSpec App url. */
    public static final String VOSPEC_JNLP_URL = "//esavo.esac.esa.int/webstart/VOSpec.jnlp";
    /** DS9 App url. */
    public static final String DS9_JNLP_URL = "//ds9.si.edu/site/Download.html";

    /**
     * Maximum length of url... de facto limit of 2000 characters.
     * //stackoverflow.com/questions
     * /417142/what-is-the-maximum-length-of-a-url-in-different-browsers
     */
    public static final int MAXIMUM_LENGTH_URL = 2000;

}
