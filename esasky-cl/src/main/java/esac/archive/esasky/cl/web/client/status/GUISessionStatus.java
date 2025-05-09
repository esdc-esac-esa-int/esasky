/*
ESASky
Copyright (C) 2025 European Space Agency

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published
by the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package esac.archive.esasky.cl.web.client.status;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Timer;

import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteCoordinatesOrFoVChangedEvent;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.Modules;
import esac.archive.esasky.cl.web.client.event.IsInScienceModeChangeEvent;
import esac.archive.esasky.cl.web.client.event.IsShowingCoordintesInDegreesChangeEvent;
import esac.archive.esasky.cl.web.client.event.IsTrackingSSOEvent;
import esac.archive.esasky.cl.web.client.login.UserDetails;
import esac.archive.esasky.cl.web.client.model.TrackedSso;
import esac.archive.esasky.cl.web.client.utility.*;
import esac.archive.esasky.cl.web.client.utility.JSONUtils.IJSONRequestCallback;
import esac.archive.esasky.cl.web.client.utility.exceptions.MapKeyException;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
/**
 * This class is intended to store some global variables related to status of MMI GUI during the
 * user's session. E.g: User's screen size, which perspective is open, etc.
 * @author ESDC team Copyright (c) 2015- European Space Agency
 */
public class GUISessionStatus {

	private static boolean isTrackingSSO = false;
	private static TrackedSso trackedSso;
	private static boolean doCountOnEnteringScienceMode = false;
	private static boolean hideSwitch = false;
	private static boolean showCoordinatesInDegrees = false;
	private static boolean hideBannerInfo = false;

	private static UserDetails userDetails;

	private static String currentLanguage;

	//TOPCAT requires a uniqe ID, otherwise it rejects incoming message
	private static int uniqueSampNumber = 0;

	/** User's screen height. */
	private static int userScreenHeight = 0;
	/** User's screen width. */
	private static int userScreenWidth = 0;

	private static int currentHeightForExpandedDataPanel = 335;

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

	public static void setUserDetails(final UserDetails userDetails) {
		GUISessionStatus.userDetails = userDetails;
	}

	public static UserDetails getUserDetails() {
		return GUISessionStatus.userDetails;
	}

	public static boolean isUserAuthenticated() {
		return Objects.nonNull(GUISessionStatus.getUserDetails());
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
				GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_CTRLTOOLBAR,
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

	public static void toggleShowCoordinatesInDegrees(){
	    GUISessionStatus.setShowCoordinatesInDegrees(!GUISessionStatus.showCoordinatesInDegrees);
	}
	public static void setShowCoordinatesInDegrees(boolean showInDegrees){
	    GUISessionStatus.showCoordinatesInDegrees = showInDegrees;
	    CommonEventBus.getEventBus().fireEvent(new IsShowingCoordintesInDegreesChangeEvent());
	}
	public static boolean isShowingCoordinatesInDegrees(){
	    return GUISessionStatus.showCoordinatesInDegrees;
	}

	public static boolean getIsInScienceMode(){
		return Modules.getModule(EsaSkyWebConstants.MODULE_SCIENCE_MODE);
	}

	public static void setInitialIsInScienceMode() {
		try {
			Modules.setModule(EsaSkyWebConstants.MODULE_SCIENCE_MODE, true);
		} catch (MapKeyException e) {
			Log.error(e.getMessage(), e);
		}
	}

	public static void onScienceModeChanged(boolean isInScienceMode){
		GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_HEADER, GoogleAnalytics.ACT_HEADER_SCIMODE, Boolean.toString(isInScienceMode));
		CommonEventBus.getEventBus().fireEvent(new IsInScienceModeChangeEvent());
		if(isInScienceMode && doCountOnEnteringScienceMode) {
			CommonEventBus.getEventBus().fireEvent(new AladinLiteCoordinatesOrFoVChangedEvent());
			doCountOnEnteringScienceMode = false;
		}
		UrlUtils.updateURLWithoutReloadingJS(UrlUtils.getUrlForCurrentState());
		Date expires = new Date();
		long milliseconds = ((long) 120)  * 24 * 60 * 60 * 1000;
		expires.setTime(expires.getTime() + milliseconds);
		Cookies.setCookie(EsaSkyWebConstants.SCI_MODE_COOKIE, Boolean.toString(isInScienceMode), expires);
	}

	public static void setIsInScienceMode(boolean isInScienceMode){
		if(getIsInScienceMode() != isInScienceMode){
			try {
				Modules.setModule(EsaSkyWebConstants.MODULE_SCIENCE_MODE, isInScienceMode);
			} catch (MapKeyException e) {
				Log.error(e.getMessage(), e);
			}
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

    public static void setShouldHideBannerInfo(boolean shouldHideBannerInfo) {
        hideBannerInfo = shouldHideBannerInfo;
    }

    public static boolean getShouldHideBannerInfo() {
        return hideBannerInfo;
    }

	public static void initiateHipsLocationScheduler() {
		checkHipsServerLocation();
		new Timer() {

			@Override
			public void run() {
				if(ActivityStatus.getInstance().isUserActive()) {
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
					} catch (Exception exception) {
						//AladinLite not initialized
						AladinLiteWrapper.setLoadInitialHipsFromEsac(true);
						AladinLiteWrapper.setLoadHipsFromCDNBeforeAladinInitialization(false);
					}
				} else {
					try {
						AladinLiteWrapper.getInstance().setLoadHipsFromCDN(true);
					} catch (Exception exception) {
						//AladinLite not initialized
					    AladinLiteWrapper.setLoadInitialHipsFromEsac(false);
						AladinLiteWrapper.setLoadHipsFromCDNBeforeAladinInitialization(true);
					}
				}
			}

			@Override
			public void onError(String errorCause) {
				Log.debug(errorCause);
			}
		});

	}

	public static boolean isUserActive() {
		return ActivityStatus.getInstance().isUserActive();
	}

	public static int getNextUniqueSampNumber(){
	    uniqueSampNumber++;
	    return uniqueSampNumber;
	}

}