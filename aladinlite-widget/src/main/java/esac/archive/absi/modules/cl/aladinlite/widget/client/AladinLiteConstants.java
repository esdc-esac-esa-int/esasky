package esac.archive.absi.modules.cl.aladinlite.widget.client;

/** Convenience class to store all aladin lite constants. */
public class AladinLiteConstants {

	public enum CoordinateFrame{
		J2000(IMAGE_SURVEY_FRAME_J2000),
		GALACTIC(IMAGE_SURVEY_FRAME_GALACTIC);

		private String aladinJsName;
		private CoordinateFrame(String aladinJsName) {
			this.aladinJsName = aladinJsName;
		}
		
		@Override
		public String toString() {
			return this.aladinJsName;
		}
		
		public static CoordinateFrame fromString(String coordinateFrame) {
			if(coordinateFrame.toLowerCase().startsWith("j2000") || coordinateFrame.toLowerCase().startsWith("icrs")) {
				return CoordinateFrame.J2000;
			} else if(coordinateFrame.toLowerCase().startsWith("gal")) {
				return CoordinateFrame.GALACTIC;
			}
			throw new IllegalArgumentException("Coordinate Frame not recognized: " + coordinateFrame);
		}
	}
	
	
    /** J2000 frame value. */
    public final static String FRAME_J2000 = "J2000";

    /** Galactic frame value. */
    public final static String FRAME_GALACTIC = "Galactic";

    /** Image survey frame value. */
    public final static String IMAGE_SURVEY_FRAME_J2000 = "j2000";

    /** Image survey frame value. */
    public final static String IMAGE_SURVEY_FRAME_GALACTIC = "galactic";

    /** Image survey frame value. */
    public final static String IMAGE_SURVEY_FRAME_EQUATORIAL = "equatorial";

    /** P/DSS2/color DSS colored */
    public final static String SURVEY_PDSS2COLOR = "P/DSS2/color";
    /** P/Fermi/color Fermi color */
    public final static String SURVEY_PFERMICOLOR = "P/Fermi/color";
    /** P/XMM/EPIC XMM-Newton stacked EPIC images (no phot. normalization) */
    public final static String SURVEY_PXMMEPIC = "P/XMM/EPIC";
    /** P/XMM/PN/color False color X-ray images (Red=0.5-1 Green=1-2 Blue=2-4.5)Kev */
    public final static String SURVEY_PXMMPNcolor = "P/XMM/PN/color";
    /** P/GALEXGR6/AIS/color GALEX Allsky Imaging Survey colored */
    public final static String SURVEY_PGALEXGR6AISCOLOR = "P/GALEXGR6/AIS/color";
    /** P/DSS2/red DSS2 Red (F+R) */
    public final static String SURVEY_PDSS2RED = "P/DSS2/red";
    /** P/SDSS9/color SDSS9 colored */
    public final static String SURVEY_PSDSS9COLOR = "P/SDSS9/color";
    /** P/Mellinger/color Mellinger colored */
    public final static String SURVEY_PMELLINGERCOLOR = "P/Mellinger/color";
    /** P/2MASS/color 2MASS colored */
    public final static String SURVEY_P2MASSCOLOR = "P/2MASS/color";
    /** P/IRIS/color IRIS colored */
    public final static String SURVEY_PIRISCOLOR = "P/IRIS/color";
    /** P/SPITZER/color IRAC color I1,I2,I4 - (GLIMPSE, SAGE, SAGE-SMC, SINGS) */
    public final static String SURVEY_PSPITZERCOLOR = "P/SPITZER/color";
    /** P/Finkbeiner Halpha */
    public final static String SURVEY_PFINKBEINER = "P/Finkbeiner";
    /** P/VTSS/Ha VTSS-Ha */
    public final static String SURVEY_PVTSSHA = "P/VTSS/Ha";

    public final static String SURVEY_SUPERCOSMOS_HA = "P/SuperCOSMOS/Ha";
    public final static String SURVEY_ALLWISE_COLOR = "P/WISE/color/";
    public final static String SURVEY_GLIMPSE_360 = "P/Glimpse360";
}
