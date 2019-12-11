package esac.archive.esasky.cl.web.client.utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.Widget;

import esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteConstants;
import esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteConstants.CoordinateFrame;
import esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteWidget;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.ColorPalette;
import esac.archive.esasky.ifcs.model.client.HiPS;
import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesConversion;
import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesFrame;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;
import esac.archive.esasky.cl.web.client.CommonEventBus;

/**
 * @author ESDC team Copyright (c) 2015- European Space Agency
 */
public class AladinLiteWrapper {

    /** Instance to ABSI aladinWidget. */
    private static AladinLiteWidget aladinLite;
    
    public static boolean loadInitialHipsFromEsac;
    public static boolean loadHipsFromCDN;
    /** Instance to JavaScriptObject. */
    private JavaScriptObject multiTargetCatalogObject;

    private JavaScriptObject searchedTargetCatalogObject;

    private JavaScriptObject futureCatalogObject;
    private JavaScriptObject futureSelectedDetectorCatalogObject;

    String planningOverlayName = "Planning";
    String planningOverlaySelectedInstrumentName = "PlanningSelectedInstrument";
    String planningOverlaySelectedDetectorName = "PlanningSelectedDetector";

    private JavaScriptObject publicationCatalogue;
    
    JavaScriptObject planningOverlay = null;
    JavaScriptObject planningOverlaySelectedInstrument = null;

    String ssoOverlayName = "SSO";
    JavaScriptObject ssoOverlay = null;

    private Resources resources = GWT.create(Resources.class);
    private CssResource style;

    private static AladinLiteWrapper _instance = null;

    static String dBugPref = "[" + AladinLiteWrapper.class.getSimpleName() + "]";

    public static interface Resources extends ClientBundle {

        @Source("aladinLiteWrapper.css")
        @CssResource.NotStrict
        CssResource style();
    }

    public static void init(final Widget inputParentWidget, final HiPS initialHiPS,
            final String target, final String fovFromUrl, final String coordinateFrame) {
        String dbp = dBugPref + "[init]";
        Log.debug(dbp);
        // Log.debug(dBugPref);
        if (_instance == null) {
            _instance = new AladinLiteWrapper(inputParentWidget, initialHiPS, target, fovFromUrl, coordinateFrame);
            Log.debug("_instance " + _instance);
        }
    }

    public static AladinLiteWrapper getInstance() {
        if (_instance == null) {
            throw new AssertionError("You have to call init first");
        }
        return _instance;
    }

    /**
     * Class constructor.
     * @param inputParentWidget Input Widget.
     * @param initialHiPS Input HiPS
     * @param target Input String
     */
    private AladinLiteWrapper(final Widget inputParentWidget, final HiPS initialHiPS,
            final String target, final String fovFromUrl, final String coordinateFrame) {
        style = resources.style();
        style.ensureInjected();
        initAladinWidget(inputParentWidget, initialHiPS, target, fovFromUrl, coordinateFrame);
    }

    /**
     * initAladinWidget().
     * @param inputParentWidget Input Widget
     * @param initialHiPS Input HiPS
     * @param target Input String.
     */
    private void initAladinWidget(final Widget inputParentWidget, HiPS initialHiPS,
            String target, String fovFromUrl, String coordinateFrame) {

        String prefix = ("[AladinLiteWrapper] ");
        
        if (initialHiPS == null){
            initialHiPS = EsaSkyWebConstants.getInitialHiPS();
        }
        String rootUrl = initialHiPS.getSurveyRootUrl();
        if(loadInitialHipsFromEsac) {
    		if(rootUrl.contains("cdn.skies.esac.esa.int")) {
    			String newRootUrl = rootUrl.replaceFirst("cdn\\.", "");;
    			initialHiPS.setSurveyRootUrl(newRootUrl);
    			Log.debug("Switched to ESAC servers for HiPS loading. New rootUrl is: " + newRootUrl);
    		}
        }
        
        if (target == null || target.equals("")){
        	    target = EsaSkyConstants.ALADIN_DEFAULT_TARGET;
        }
        Double fov = sanitizeFovFromUrl(fovFromUrl);
        if(coordinateFrame != null && coordinateFrame.toLowerCase().contains("gal")) {
        	coordinateFrame = AladinLiteConstants.FRAME_GALACTIC;
        } else {
        	coordinateFrame = AladinLiteConstants.FRAME_J2000;
        }
        Log.debug(prefix + " getSurveyId(): " + initialHiPS.getSurveyId());
        Log.debug(prefix + " getSurveyFrame(): " + initialHiPS.getSurveyFrame().toString());
        Log.debug(prefix + " target: " + target);

        aladinLite = new AladinLiteWidget(CommonEventBus.getEventBus(),
                EsaSkyConstants.ALADIN_DIV_NAME, new Double(1580), new Double(960),
                initialHiPS.getSurveyId(), initialHiPS.getSurveyName(),
                initialHiPS.getSurveyRootUrl(), initialHiPS.getSurveyFrame().getName(),
                initialHiPS.getMaximumNorder(), initialHiPS.getImgFormat().name(),

                coordinateFrame, // cooFrame
                false, // showLayersControl
                false, // showGotoControlBoolean,
                false, // showFullscreenControlBoolean
                false, // showShareControlBoolean
                true, // showReticleBoolean
                false, // showZoomControlBoolean
                true, // showFrameBoolean
                target, // target
                fov, // zoomInteger
                inputParentWidget);
    }

	private Double sanitizeFovFromUrl(String fovFromUrl) {
		Double fov = EsaSkyConstants.initFoV;
        if (fovFromUrl != null && !fovFromUrl.equals("")) {
            	try {        		
            		Double fovFromUrlDouble = Double.valueOf(fovFromUrl);
                	if (fovFromUrlDouble <= 0 || fovFromUrlDouble > 180) {
                        Log.debug("Invalid fov value");
                    } else {
                        fov = fovFromUrlDouble;
                    }
            	} catch (NumberFormatException e){
            		
            	}
        }
		return fov;
	}

    public String getCooFrame() {
        return aladinLite.getCooFrame();
    }
    
    public void setCooFrame(CoordinateFrame coordinateFrame) {
    	aladinLite.setCooFrame(coordinateFrame);
    }

    public static CoordinatesFrame getCoordinatesFrame() {
        return CoordinatesFrame.valueOf(aladinLite.getCooFrame().toUpperCase());
    }

    public JavaScriptObject getSSOOverlay() {
        if (this.ssoOverlay == null) {
            this.ssoOverlay = aladinLite.createOverlay(ssoOverlayName, "red");
        }
        return this.ssoOverlay;
    }

    public void addPlolyline2SSOverlay(JavaScriptObject polyline) {
        cleanSSOOverlay();
        if(polyline != null){
        	getAladinLite().addJ2000PolylineToOverlay(getSSOOverlay(), polyline);
        }
    }
    
    public JavaScriptObject createPolyline(double [] polylinePoints, String color, int lineWidth) {
    	return aladinLite.createJ2000Polyline(polylinePoints, color, lineWidth);
    }

    public void cleanSSOOverlay() {
        getAladinLite().removeAllFootprintsFromOverlay(getSSOOverlay());
    }

    public final JavaScriptObject getFutureCatalog() {
        if (this.futureCatalogObject == null) {
            this.futureCatalogObject = aladinLite.createCatalog("Future Catalog", "#00CC00", 0);
        }
        return this.futureCatalogObject;
    }

    public final void addSourcesToFutureCatalog(String raDeg, String decDeg,
            Map<String, Object> details) {
        JavaScriptObject source = aladinLite.newApi_createSourceJSObj(raDeg, decDeg, details);
        aladinLite.newApi_addSourceToCatalogue(getFutureCatalog(), source);
    }

    public final JavaScriptObject getFutureSelectedDetectorCatalogue() {
        if (this.futureSelectedDetectorCatalogObject == null) {
            this.futureSelectedDetectorCatalogObject = aladinLite.createCatalog(
                    "Future Selected Detector Catalog", "#ff8566", 0);
        }
        return this.futureSelectedDetectorCatalogObject;
    }

    public final void addSourcesToFutureSelectedDetectorCatalogue(String raDeg, String decDeg,
            Map<String, Object> details) {
        JavaScriptObject source = aladinLite.newApi_createSourceJSObj(raDeg, decDeg, details);
        aladinLite.newApi_addSourceToCatalogue(getFutureSelectedDetectorCatalogue(), source);
    }
    
    public JavaScriptObject getPublicationCatalogue() {
        return this.publicationCatalogue;
    }
    
    public JavaScriptObject createPublicationCatalogue(String catalogName, String color, Map<String, Object> details) {
        this.publicationCatalogue = aladinLite.createCatalogWithDetails(
                catalogName, 14, color, details);
        return this.publicationCatalogue;
    }

    public JavaScriptObject getPlanningOverlaySelectedInstrument() {
        if (this.planningOverlaySelectedInstrument == null) {
            this.planningOverlaySelectedInstrument = aladinLite.createOverlay(
                    planningOverlaySelectedInstrumentName, "#ff8566");
        }
        return this.planningOverlaySelectedInstrument;
    }

    public JavaScriptObject getPlanningOverlaySelectedInstrument(String planeColor) {
        if (this.planningOverlaySelectedInstrument == null) {
            this.planningOverlaySelectedInstrument = aladinLite.createOverlay(
                    planningOverlaySelectedInstrumentName, planeColor);
        }
        return this.planningOverlaySelectedInstrument;
    }

    public JavaScriptObject getPlanningOverlay() {
        if (this.planningOverlay == null) {
            this.planningOverlay = aladinLite.createOverlay(planningOverlayName, "#00FF00");
        }
        return this.planningOverlay;
    }

    public JavaScriptObject getPlanningOverlay(String planeColor) {
        if (this.planningOverlay == null) {
            this.planningOverlay = aladinLite.createOverlay(planningOverlayName, planeColor);
        }
        return this.planningOverlay;
    }

    /**
     * getAladinLite().
     * @return AladinLiteWidget
     */
    public static final AladinLiteWidget getAladinLite() {
        return aladinLite;
    }

    /**
     * getFovDeg().
     * @return double
     */
    public final double getFovDeg() {
        return Double.parseDouble(NumberFormat.getFormat("000.000000000000").format(
                aladinLite.getFovDeg()));
    }

    /**
     * getCenterLatitudeDeg().
     * @return double
     */
    public final double getCenterLatitudeDeg() {
        return aladinLite.getCenterLatitudeDeg();
    }

    /**
     * getCenterLongitudeDeg().
     * @return double
     */
    public final double getCenterLongitudeDeg() {
        return aladinLite.getCenterLongitudeDeg();
    }

    /**
     * displayMultiTargetPointer().
     * @param ra Input String
     * @param dec Input String
     */
    public final void displayMultiTargetPointer(final String ra, final String dec) {
        if (this.multiTargetCatalogObject == null) {
            this.multiTargetCatalogObject = aladinLite.createCatalog("Multi Target Catalog",
                    "#00CC00", 6);
        }

        JsArray<JavaScriptObject> jsSources = aladinLite
                .createSingleSourceArray(ra, dec, "message");
        aladinLite.addSourcesToCatalog(this.multiTargetCatalogObject, jsSources);
    }

    public final JavaScriptObject getMultiTargetCatalogue() {
        if (this.multiTargetCatalogObject == null) {
            this.multiTargetCatalogObject = aladinLite.createCatalog("Multi Target Catalog",
                    "#00CC00", 6);
        }
        return this.multiTargetCatalogObject;
    }

    /**
     * displayMultiTargetPointer().
     * @param ra Input String
     * @param dec Input String
     */
    public final void addSourcesToMultiTargetCatalogue(JavaScriptObject source) {
        if (this.multiTargetCatalogObject == null) {
            this.multiTargetCatalogObject = aladinLite.createCatalog("Multi Target Catalog",
                    "#00CC00", 6);
        }

        JsArray<JavaScriptObject> jsSources = aladinLite.getNativeJsArray();
        jsSources.push(jsSources);
        aladinLite.addSourcesToCatalog(this.multiTargetCatalogObject, jsSources);
    }

    /**
     * removeMultitargetPointer().
     */
    public final void removeMultitargetPointer() {
        if (this.multiTargetCatalogObject != null) {
            aladinLite.removeAllSourcesFromCatalog(this.multiTargetCatalogObject);
        }
    }

    /**
     * displaySearchTargetPointer().
     * @param ra Input String
     * @param dec Input String
     * @param cooFrame
     */
    public final void displaySearchedTargetPointer(final String ra, final String dec,
            String cooFrame) {
        if (this.searchedTargetCatalogObject == null) {
            this.searchedTargetCatalogObject = aladinLite.createCatalog("Searched Target Catalog",
                    "#00CC00", 6);
        }
        
        Map<String, String> details = new HashMap<String, String>();

        details.put("cooFrame", AladinLiteConstants.FRAME_J2000);
        details.put("catalogue", "Searched Target Catalog");

        Double[] raDec = { Double.parseDouble(ra), Double.parseDouble(dec) };
        if (EsaSkyWebConstants.ALADIN_GALACTIC_COOFRAME.equalsIgnoreCase(cooFrame)) {
            Log.debug("@@@ CONVERSION TO J2000 ");
            raDec = CoordinatesConversion.convertPointGalacticToJ2000(Double.parseDouble(ra),
                    Double.parseDouble(dec));
        }

        JavaScriptObject jsSource = aladinLite.newApi_createSourceJSObj(Double.toString(raDec[0]),
                Double.toString(raDec[1]), details);
        aladinLite.newApi_addSourceToCatalogue(this.searchedTargetCatalogObject, jsSource);
    }

    /**
     * removeSearchtargetPointer().
     */
    public final void removeSearchtargetPointer() {
        if (this.searchedTargetCatalogObject != null) {
            aladinLite.removeAllSourcesFromCatalog(this.searchedTargetCatalogObject);
        }
    }

    /**
     * openHiPS().
     * @param hips Input HiPS object.
     */
    public final void openHiPS(final HiPS hips) {
        String rootUrl = hips.getSurveyRootUrl();
        if(!loadHipsFromCDN) {
    		if(rootUrl.contains("cdn.skies.esac.esa.int")) {
    			String newRootUrl = rootUrl.replaceFirst("cdn\\.", "");;
    			hips.setSurveyRootUrl(newRootUrl);
    			Log.debug("Changed survey url to ESAC servers for HiPS loading. New rootUrl is: " + newRootUrl);
    		}
        }
        
        aladinLite.createAndSetImageSurveyWithImgFormat(hips.getSurveyId(), hips.getSurveyName(),
                hips.getSurveyRootUrl(), hips.getSurveyFrame().name(), hips.getMaximumNorder(),
                hips.getImgFormat().name());
    }
    
    public void setLoadHipsFromCDN(boolean loadFromCDN) {
    	loadHipsFromCDN = loadFromCDN;
    	String rootUrl = getRootUrl(aladinLite.getCurrentImageSurveyObject());
    	if(loadFromCDN) {
        	if(rootUrl.contains("skies.esac.esa.int") && !rootUrl.contains("cdn.skies.esac.esa.int")) {
        		String newRootUrl = rootUrl.replaceFirst("skies\\.esac\\.esa\\.int", "cdn.skies.esac.esa.int");
        		setRootUrl(aladinLite.getCurrentImageSurveyObject(), newRootUrl);
        		Log.debug("Switched to CDN servers for HiPS loading. New rootUrl is: " + newRootUrl);
        	}
    	} else {
    		if(rootUrl.contains("cdn.skies.esac.esa.int")) {
    			String newRootUrl = rootUrl.replaceFirst("cdn\\.", "");;
    			setRootUrl(aladinLite.getCurrentImageSurveyObject(), newRootUrl);
    			Log.debug("Switched to ESAC servers for HiPS loading. New rootUrl is: " + newRootUrl);
    		}
    	}
    }
    
    private native void setRootUrl(JavaScriptObject currentImageSurvey, String newRootUrl) /*-{
    	currentImageSurvey.rootUrl = newRootUrl;
    }-*/;
    
    private native String getRootUrl(JavaScriptObject currentImageSurvey) /*-{
    	return currentImageSurvey.rootUrl;
    }-*/;
    /**
     * createOverlayMap().
     * @param overlayHiPS Input HiPS object
     * @param opacity Input double
     * @param colorPalette Input ColorPalette object
     */
    public final void createOverlayMap(final HiPS overlayHiPS, final double opacity,  ColorPalette colorPalette) {
        aladinLite.doOverlaySimpleImageLayer(overlayHiPS.getSurveyId(),
                overlayHiPS.getSurveyName(), overlayHiPS.getSurveyRootUrl(), overlayHiPS
                        .getSurveyFrame().name(), overlayHiPS.getMaximumNorder(), overlayHiPS
                        .getImgFormat().name(), opacity);
        aladinLite.setOverlayColorPalette(colorPalette);
    }

    /**
     * changeHiPSOpacity().
     * @param opacity Input double
     */
    public final void changeHiPSOpacity(final double opacity) {
        aladinLite.setImageSurveyAlpha(opacity);
    }

    
    public final void changeOverlayOpacity(final double opacity) {
    	aladinLite.setOverlayImageLayerAlpha(opacity);
    }

    /**
     * setOverlayImageLayerToNull().
     */
    public final void setOverlayImageLayerToNull() {
    	aladinLite.setOverlayImageLayerToNull();
    }
    	
    /**
     * removeAllFootprintsFromOverlay().
     * @param overlay Input JavaScriptObject
     */
    public final void removeAllFootprintsFromOverlay(final JavaScriptObject overlay) {
        getAladinLite().removeAllFootprintsFromOverlay(overlay);
    }

    /**
     * increaseZoom().
     */
    public final void increaseZoom() {
        getAladinLite().increaseZoom();
    }

    /**
     * decreaseZoom().
     */
    public final void decreaseZoom() {
        getAladinLite().decreaseZoom();
    }

    /**
     * setDefaultColorPalette().
     * @param colorPalette Input Enum ColorPalette
     */
    public final void setColorPalette(final ColorPalette colorPalette) {

        aladinLite.setColorPalette(colorPalette);
    }

    /**
     * goToTargetAndSearch().
     * @param ra Input String
     * @param dec Input String
     * @param fovDegrees Input double
     * @param showTargetPointer Input boolean value.
     * @param cooFrame String representing the cooFrame to be used for the marker
     */
    public void goToTarget(final String ra, final String dec, final double fovDegrees,
            final boolean showTargetPointer, final String cooFrame) {
        final String debugPrefix = "[goToTarget]";

        Log.debug(debugPrefix + "Calling AladinLite:goToRaDec(" + ra + "," + dec + ")");
        AladinLiteWrapper.getAladinLite().goToRaDec(ra, dec);
        if (showTargetPointer) {
            this.displaySearchedTargetPointer(ra, dec, cooFrame);
        }
        Log.debug(debugPrefix + "Calling AladinLite:setZoom(" + fovDegrees + ")");
        AladinLiteWrapper.getAladinLite().setZoom(fovDegrees);
    }

    /**
     * goToObject().
     * @param input Input String
     * @param showTargetPointer Input boolean value.
     */
    public void goToObject(final String input, final boolean showObjectPointer) {
        AladinLiteWrapper.getAladinLite().goToObject(input);
        if (showObjectPointer) {
            Double[] raDec = { this.getCenterLongitudeDeg(), this.getCenterLatitudeDeg() };
            this.displaySearchedTargetPointer(Double.toString(raDec[0]), Double.toString(raDec[1]),
                    AladinLiteWrapper.getAladinLite().getCooFrame());
        }
    }

    public static double getCenterRaDeg() {
        return aladinLite.getCenterLongitudeDeg();
    }

    public static double getCenterDecDeg() {
        return aladinLite.getCenterLatitudeDeg();
    }
    
    public static boolean isCornersInsideHips() {
    	try {
    		String [] points = aladinLite.getFovCorners(2).toString().split(",");
    		if(points.length == 16) {
    			for (int i = 0; i < points.length; i++) {
    				new Double(points[i]);
    			}
    			return true;
    		}
    	} catch(Exception e) {
    	}
    	return false;
    }
    
    public ArrayList<Integer> getVisibleNpix(int norder) {
    	String[] cells = aladinLite.getVisibleNpix(norder).toString().split(",");
    	ArrayList<Integer> list = new ArrayList<Integer>();
    	
    	for(String cell : cells) {
    		list.add(Integer.parseInt(cell));
    	}
        return list;
    }
}
