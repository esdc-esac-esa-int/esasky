package esac.archive.esasky.cl.web.client.status;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Timer;

import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteCoordinatesOrFoVChangedEvent;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.IsInScienceModeChangeEvent;
import esac.archive.esasky.cl.web.client.event.IsTrackingSSOEvent;
import esac.archive.esasky.cl.web.client.model.TrackedSso;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.utility.JSONUtils;
import esac.archive.esasky.cl.web.client.utility.JSONUtils.IJSONRequestCallback;
import esac.archive.esasky.cl.web.client.utility.UrlUtils;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;

/**
 * This class is intended to store some global variables related to status of MMI GUI during the
 * user's session. E.g: User's screen size, which perspective is open, etc.
 * @author ESDC team Copyright (c) 2015- European Space Agency
 */
public class GUISessionStatus {

	private static boolean isTrackingSSO = false;
	private static TrackedSso trackedSso;
	private static boolean isInScienceMode = false;
	private static boolean doCountOnEnteringScienceMode = false;
	private static boolean hideSwitch = false;
	
	private static String currentLanguage;

	/** User's screen height. */
	private static int userScreenHeight = 0;
	/** User's screen width. */
	private static int userScreenWidth = 0;
	
	private static int currentHeightForExpandedDataPanel = 300;

	/** current count for each catalogs. Key is the value of JSON mission CatalogDescriptor */
	private static Map<String, Integer> catalogsCountMap = new HashMap<String, Integer>();
	/**
	 * current count for each observations. The Map Key is the value of JSON mission
	 * ObservationDescriptor
	 */
	private static Map<String, Integer> observationsCountMap = new HashMap<String, Integer>();

	/**
	 * last timestamp related to the last call for each catalogs. Key is the value of JSON mission
	 * CatalogDescriptor
	 */
	private static Map<String, Long> lastCatCountTimestampMap = new HashMap<String, Long>();
	/**
	 * last timestamp related to the last call for each observation. Key is the value of JSON
	 * mission ObservationDescriptor
	 */
	private static Map<String, Long> lastObsCountTimestampMap = new HashMap<String, Long>();

	private static boolean dataPanelOpen = false;

	/** Prevents Utility class calls. */
	protected GUISessionStatus() {
		// prevents calls from subclass
		throw new UnsupportedOperationException();
	}

	/**
	 * setUserScreenHeight().
	 * @param inputUserScreenHeight Input Release Object.
	 */
	public static void setUserScreenHeight(final int inputUserScreenHeight) {
		GUISessionStatus.userScreenHeight = inputUserScreenHeight;
	}

	/**
	 * getUserScreenHeight().
	 * @return userScreenHeight.
	 */
	public static int getUserScreenHeight() {
		return GUISessionStatus.userScreenHeight;
	}

	/**
	 * setUserScreenHeight().
	 * @param inputUserScreenWidth Input Release Object.
	 */
	public static void setUserScreenWidth(final int inputUserScreenWidth) {
		GUISessionStatus.userScreenWidth = inputUserScreenWidth;
	}

	/**
	 * getUserScreenHeight().
	 * @return userScreenWidth.
	 */
	public static int getUserScreenWidth() {
		return GUISessionStatus.userScreenWidth;
	}
	
	public static int getCurrentHeightForExpandedDataPanel() {
		int height = currentHeightForExpandedDataPanel;
		if(height > MainLayoutPanel.getMainAreaHeight() - 80) {
			height = MainLayoutPanel.getMainAreaHeight() - 80;
		}
		if(height < 100) {
			height = 100;
		}
		return height;
	}
	
	public static void setCurrentHeightForExpandedDataPanel(int currentHeightForExpandedDataPanel) {
		GUISessionStatus.currentHeightForExpandedDataPanel = currentHeightForExpandedDataPanel;
	}
	
	/**
	 * @return the catalogsCountMap
	 */
	public static Map<String, Integer> getCatalogsCountMap() {
		return catalogsCountMap;
	}

	/**
	 * @return the observationsCountMap
	 */
	public static Map<String, Integer> getObservationsCountMap() {
		return observationsCountMap;
	}

	/**
	 * @return the lastCatCountTimestampMap
	 */
	public static Map<String, Long> getLastCatCountTimestampMap() {
		return lastCatCountTimestampMap;
	}

	/**
	 * @return the lastObsCountTimestampMap
	 */
	public static Map<String, Long> getLastObsCountTimestampMap() {
		return lastObsCountTimestampMap;
	}

	public static boolean isDataPanelOpen() {
		return GUISessionStatus.dataPanelOpen;
	}

	public static void setDataPanelOpen(boolean dataPanelOpen) {
		GUISessionStatus.dataPanelOpen = dataPanelOpen;
	}

	public static boolean getIsTrackingSSO(){
		return GUISessionStatus.isTrackingSSO;
	}
	
	public static void setTrackedSSO(TrackedSso trackedSso){
		GUISessionStatus.trackedSso = trackedSso;
		setIsTrackingSSO(true);
	}
	
	public static TrackedSso getTrackedSso() {
		return isTrackingSSO ? trackedSso : null;
	}
	
	public static String getTrackedSsoName() {
	    return isTrackingSSO ? trackedSso.name : null;
	}
	
	public static void setIsTrackingSSO(boolean isTrackingSso){
		if(GUISessionStatus.isTrackingSSO != isTrackingSso){
			GUISessionStatus.isTrackingSSO = isTrackingSso;
			CommonEventBus.getEventBus().fireEvent(new IsTrackingSSOEvent());
			if(isTrackingSso) {
				GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_CtrlToolbar, 
						"SSO", GUISessionStatus.trackedSso.name + "(" + GUISessionStatus.trackedSso.type + ")");
			}
		}
	}

	public static void sethideSwitch(boolean hideSwitch){
		GUISessionStatus.hideSwitch = hideSwitch;
	}
	public static boolean isHidingSwitch(){
		return GUISessionStatus.hideSwitch;
	}
	
	public static boolean getIsInScienceMode(){
		return GUISessionStatus.isInScienceMode;
	}
	
	public static void setInitialIsInScienceMode() {
		isInScienceMode = true;
	}

	public static void setIsInScienceMode(boolean isInScienceMode){
		if(GUISessionStatus.isInScienceMode != isInScienceMode){
			GUISessionStatus.isInScienceMode = isInScienceMode;
			GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_Header, GoogleAnalytics.ACT_Header_SciMode, Boolean.toString(isInScienceMode));
			CommonEventBus.getEventBus().fireEvent(new IsInScienceModeChangeEvent());
			if(isInScienceMode && doCountOnEnteringScienceMode) {
				CommonEventBus.getEventBus().fireEvent(new AladinLiteCoordinatesOrFoVChangedEvent());
				doCountOnEnteringScienceMode = false;
			}
			UrlUtils.updateURLWithoutReloadingJS(UrlUtils.getUrlForCurrentState());
			Date expires = new Date();
			long milliseconds = 120 * 24 * 60 * 60 * 1000;
			expires.setTime(expires.getTime() + milliseconds);
			Cookies.setCookie(EsaSkyWebConstants.SCI_MODE_COOKIE, Boolean.toString(isInScienceMode), expires);
		}
		
	}

	public static void setDoCountOnEnteringScienceMode() {
		doCountOnEnteringScienceMode = true;
	}
	
	public static void setCurrentLanguage(String language) {
		currentLanguage = language;
	}
	
	public static String getCurrentLanguage() {
		return currentLanguage;
	}
	
	public static void initiateHipsLocationScheduler() {
		checkHipsServerLocation();
		new Timer() {
			
			@Override
			public void run() {
				if(ActivityStatus.getInstance().anyActivityDuringTheLastMinute()) {
					checkHipsServerLocation();
				}
			}
		}.scheduleRepeating(1000 * 60);
		
		new Timer() {
			
			@Override
			public void run() {
				checkHipsServerLocation();
			}
		}.schedule(1000 * 10);
	}
	
	public static void checkHipsServerLocation() {
		JSONUtils.getJSONFromUrl(EsaSkyWebConstants.HIPS_STORAGE_URL, new IJSONRequestCallback() {
			
			@Override
			public void onSuccess(String responseText) {
				if(responseText.equalsIgnoreCase("//skies.esac.esa.int")
						|| responseText.equalsIgnoreCase("\"//skies.esac.esa.int\"")) {
					try {
						AladinLiteWrapper.getInstance().setLoadHipsFromCDN(false);
					} catch (AssertionError exception) {
						//AladinLite not initialized
						AladinLiteWrapper.loadInitialHipsFromEsac = true;
						AladinLiteWrapper.loadHipsFromCDN = false;
					}
				} else {
					try {
						AladinLiteWrapper.getInstance().setLoadHipsFromCDN(true);
					} catch (AssertionError exception) {
						//AladinLite not initialized
						AladinLiteWrapper.loadInitialHipsFromEsac = false;
						AladinLiteWrapper.loadHipsFromCDN = true;
					}
				}
			}
			
			@Override
			public void onError(String errorCause) {
				Log.debug(errorCause);
			}
		});

	}
	
}