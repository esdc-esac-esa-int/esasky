/*
ESASky
Copyright (C) 2025 Henrik Norman

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

package esac.archive.esasky.cl.web.client;

import java.util.HashMap;

import com.google.gwt.i18n.client.Dictionary;

import esac.archive.esasky.cl.web.client.event.ModuleUpdatedEvent;
import esac.archive.esasky.cl.web.client.login.UserLayoutPanel;
import esac.archive.esasky.cl.web.client.presenter.MainPresenter;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.ExtTapUtils;
import esac.archive.esasky.cl.web.client.utility.JSONUtils;
import esac.archive.esasky.cl.web.client.utility.UrlUtils;
import esac.archive.esasky.cl.web.client.utility.exceptions.MapKeyException;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;

public class Modules {
    
    private static String mode = "";
    
    private static HashMap<String, Boolean> currentModuleMap = new HashMap<>();
    private static HashMap<String, HashMap<String, Boolean>> allModuleMaps = new HashMap<>();
    
    private static final String SERVER_PROPERTIES = "serverProperties";
    
    private Modules() {
    	
    }
    
    public static void Init() {

        allModuleMaps.put(EsaSkyWebConstants.MODULE_MODE_CLEAN, createCleanMap());
        allModuleMaps.put(EsaSkyWebConstants.MODULE_MODE_ESASKY, createEsaskyMap());
        allModuleMaps.put(EsaSkyWebConstants.MODULE_MODE_JWST, createJWSTMap());
        allModuleMaps.put(EsaSkyWebConstants.MODULE_MODE_EUCLID, createEuclidMap());
		allModuleMaps.put(EsaSkyWebConstants.MODULE_MODE_KIOSK, createKioskMap());
        allModuleMaps.put(EsaSkyWebConstants.MODULE_MODE_USER, createEsaskyMap());
        currentModuleMap = allModuleMaps.get(EsaSkyWebConstants.MODULE_MODE_ESASKY);
    	
    }
    
    public static boolean getModule(String key) {
    	if(currentModuleMap.containsKey(key)) {	
    		return currentModuleMap.get(key);
    	}else {
    		return false;
    	}
    }

    public static void setModule(String key, boolean value) throws MapKeyException {
    	if(currentModuleMap.containsKey(key)) {
    		currentModuleMap.put(key, value);
    		ModuleUpdatedEvent event = new ModuleUpdatedEvent(key, value);
    		CommonEventBus.getEventBus().fireEvent(event);
    		return;
    	}
    	throw new MapKeyException(key);
    }
    
    public static String[] getModuleKeys(){
    	return currentModuleMap.keySet().toArray(new String [0]);
	}
    
    public static void setMode(String mode) {
    	Modules.mode = mode;
		String mapMode  = mode;

		if (mode != null && mode.startsWith(EsaSkyWebConstants.MODULE_MODE_USER)) {
			String[] modeArr = Modules.mode.split("_");
			mapMode = modeArr[0];
		}

    	if(mapMode != null && allModuleMaps.containsKey(mapMode.toUpperCase())) {
    		currentModuleMap = allModuleMaps.get(mapMode.toUpperCase());
    	}else {
    		currentModuleMap = allModuleMaps.get(EsaSkyWebConstants.MODULE_MODE_ESASKY);
    	}
    }

	public static void setMode(String mode, String layoutId) {
		setMode(mode + "_" + layoutId);
	}

	public static String getLayoutId() {
		if (Modules.mode.startsWith(EsaSkyWebConstants.MODULE_MODE_USER)) {
			String[] mode = Modules.mode.split("_");
			if (mode.length > 1) {
				return mode[1];
			}
		}

		return null;
	}

	public static void activateLayout(String layoutId) {
		if (layoutId != null ) {
			String url = EsaSkyWebConstants.TAP_USERLAYOUTS_URL + "?layout_id=" + layoutId;
			JSONUtils.getJSONFromUrl(url, new JSONUtils.IJSONRequestCallback() {
				@Override
				public void onSuccess(String responseText) {

					GeneralJavaScriptObject obj = GeneralJavaScriptObject.createJsonObject(responseText);
					GeneralJavaScriptObject data = obj.getProperty("data");
					GeneralJavaScriptObject metadata = obj.getProperty("columns");

					GeneralJavaScriptObject metaObj = ExtTapUtils.formatExternalTapMetadata(metadata);
					GeneralJavaScriptObject dataObj = ExtTapUtils.formatExternalTapData(data, metaObj);

					Modules.setMode(EsaSkyWebConstants.MODULE_MODE_USER, layoutId);
					for (GeneralJavaScriptObject obj2 : GeneralJavaScriptObject.convertToArray(dataObj)) {
						String key = obj2.getStringProperty("key");
						Boolean isShown = Boolean.parseBoolean(obj2.getStringProperty("is_shown"));

						try {
							Modules.setModule(key, isShown);
						} catch (MapKeyException e) {
							throw new RuntimeException(e);
						}
					}

					MainPresenter.getInstance().updateModuleVisibility();
					UrlUtils.updateURLWithoutReloadingJS(UrlUtils.getUrlForCurrentState());

				}

				@Override
				public void onError(String errorCause) {
					String test = "";
				}

				@Override
				public void whenComplete() {

				}
			});
		}
	}

	public static String getMode() {
		return mode != null ? mode : "";
	}

	public static HashMap<String, Boolean> getModuleMap() {
		return currentModuleMap;
	}

	public static void setModuleMap(HashMap<String, Boolean> moduleMap) {
		Modules.currentModuleMap = moduleMap;
	}
	
	private static HashMap<String, Boolean> createCleanMap() {
        HashMap<String, Boolean> map = createDefaultMap();

        map.put(EsaSkyWebConstants.MODULE_SCIENTIFIC_BUTTON, false);
        map.put(EsaSkyWebConstants.MODULE_LANGUAGE, false);
        map.put(EsaSkyWebConstants.MODULE_COOR_GRID, true);
        map.put(EsaSkyWebConstants.MODULE_SCREENSHOT, true);
        map.put(EsaSkyWebConstants.MODULE_SHARE, false);
        map.put(EsaSkyWebConstants.MODULE_HELP, false);
        map.put(EsaSkyWebConstants.MODULE_DROPDOWN, true);
        map.put(EsaSkyWebConstants.MODULE_FEEDBACK, false);
		map.put(EsaSkyWebConstants.MODULE_LOGIN, false);
        map.put(EsaSkyWebConstants.MODULE_SKIESMENU, true);
        map.put(EsaSkyWebConstants.MODULE_OBS, false);
        map.put(EsaSkyWebConstants.MODULE_CAT, false);
        map.put(EsaSkyWebConstants.MODULE_SPE, false);
        map.put(EsaSkyWebConstants.MODULE_EXTTAP, false);
        map.put(EsaSkyWebConstants.MODULE_OUTREACH_IMAGE, false);
		map.put(EsaSkyWebConstants.MODULE_OUTREACH_JWST, false);
		map.put(EsaSkyWebConstants.MODULE_OUTREACH_EUCLID, false);
        map.put(EsaSkyWebConstants.MODULE_GW, false);
        map.put(EsaSkyWebConstants.MODULE_SSO, false);
        map.put(EsaSkyWebConstants.MODULE_PUBLICATIONS, false);
        map.put(EsaSkyWebConstants.MODULE_TARGETLIST, false);
        map.put(EsaSkyWebConstants.MODULE_JWST_PLANNING, false);
        map.put(EsaSkyWebConstants.MODULE_DICE, false);
        map.put(EsaSkyWebConstants.MODULE_SCIENCE_MODE, false);
        map.put(EsaSkyWebConstants.MODULE_SESSION, true);
		map.put(EsaSkyWebConstants.MODULE_SEARCH_BOX, true);
		map.put(EsaSkyWebConstants.MODULE_SEARCH_TOOL, false);

		return map;
	}
	
	private static HashMap<String, Boolean> createJWSTMap() {
        HashMap<String, Boolean> map = createDefaultMap();
		
		map.put(EsaSkyWebConstants.MODULE_SCIENTIFIC_BUTTON, false);
		map.put(EsaSkyWebConstants.MODULE_LANGUAGE, false);
		map.put(EsaSkyWebConstants.MODULE_COOR_GRID, true);
		map.put(EsaSkyWebConstants.MODULE_SCREENSHOT, true);
		map.put(EsaSkyWebConstants.MODULE_SHARE, true);
		map.put(EsaSkyWebConstants.MODULE_HELP, false);
		map.put(EsaSkyWebConstants.MODULE_DROPDOWN, false);
		map.put(EsaSkyWebConstants.MODULE_FEEDBACK, false);
		map.put(EsaSkyWebConstants.MODULE_LOGIN, false);
		map.put(EsaSkyWebConstants.MODULE_SKIESMENU, true);
		map.put(EsaSkyWebConstants.MODULE_OBS, true);
		map.put(EsaSkyWebConstants.MODULE_CAT, true);
		map.put(EsaSkyWebConstants.MODULE_SPE, true);
		map.put(EsaSkyWebConstants.MODULE_OUTREACH_IMAGE, false);
		map.put(EsaSkyWebConstants.MODULE_OUTREACH_JWST, false);
		map.put(EsaSkyWebConstants.MODULE_OUTREACH_EUCLID, false);
		map.put(EsaSkyWebConstants.MODULE_EXTTAP, false);
		map.put(EsaSkyWebConstants.MODULE_GW, false);
		map.put(EsaSkyWebConstants.MODULE_SSO, false);
		map.put(EsaSkyWebConstants.MODULE_PUBLICATIONS, false);
		map.put(EsaSkyWebConstants.MODULE_TARGETLIST, true);
		map.put(EsaSkyWebConstants.MODULE_JWST_PLANNING, true);
		map.put(EsaSkyWebConstants.MODULE_DICE, false);
		map.put(EsaSkyWebConstants.MODULE_SCIENCE_MODE, true);
        map.put(EsaSkyWebConstants.MODULE_SESSION, true);
		map.put(EsaSkyWebConstants.MODULE_SEARCH_TOOL, true);
		map.put(EsaSkyWebConstants.MODULE_SEARCH_BOX, true);
		
		return map;
	}
	
	private static HashMap<String, Boolean> createEuclidMap() {
        HashMap<String, Boolean> map = createDefaultMap();
		
		map.put(EsaSkyWebConstants.MODULE_SCIENTIFIC_BUTTON, false);
		map.put(EsaSkyWebConstants.MODULE_LANGUAGE, false);
		map.put(EsaSkyWebConstants.MODULE_COOR_GRID, true);
		map.put(EsaSkyWebConstants.MODULE_SCREENSHOT, true);
		map.put(EsaSkyWebConstants.MODULE_SHARE, true);
		map.put(EsaSkyWebConstants.MODULE_HELP, true);
		map.put(EsaSkyWebConstants.MODULE_DROPDOWN, true);
		map.put(EsaSkyWebConstants.MODULE_FEEDBACK, false);
		map.put(EsaSkyWebConstants.MODULE_LOGIN, false);
		map.put(EsaSkyWebConstants.MODULE_SKIESMENU, true);
		map.put(EsaSkyWebConstants.MODULE_OBS, true);
		map.put(EsaSkyWebConstants.MODULE_CAT, true);
		map.put(EsaSkyWebConstants.MODULE_SPE, true);
		map.put(EsaSkyWebConstants.MODULE_OUTREACH_IMAGE, true);
		map.put(EsaSkyWebConstants.MODULE_OUTREACH_JWST, false);
		map.put(EsaSkyWebConstants.MODULE_OUTREACH_EUCLID, true);
		map.put(EsaSkyWebConstants.MODULE_EXTTAP, true);
		map.put(EsaSkyWebConstants.MODULE_GW, false);
		map.put(EsaSkyWebConstants.MODULE_SSO, false);
		map.put(EsaSkyWebConstants.MODULE_PUBLICATIONS, true);
		map.put(EsaSkyWebConstants.MODULE_TARGETLIST, true);
		map.put(EsaSkyWebConstants.MODULE_JWST_PLANNING, false);
		map.put(EsaSkyWebConstants.MODULE_DICE, false);
		map.put(EsaSkyWebConstants.MODULE_SCIENCE_MODE, true);
        map.put(EsaSkyWebConstants.MODULE_SESSION, true);
		map.put(EsaSkyWebConstants.MODULE_SEARCH_TOOL, true);
		map.put(EsaSkyWebConstants.MODULE_SEARCH_BOX, true);
		
		return map;
	}
	
	private static HashMap<String, Boolean> createEsaskyMap() {
        HashMap<String, Boolean> map = createDefaultMap();
		
		map.put(EsaSkyWebConstants.MODULE_SCIENTIFIC_BUTTON, true);
		map.put(EsaSkyWebConstants.MODULE_LANGUAGE, true);
		map.put(EsaSkyWebConstants.MODULE_COOR_GRID, true);
		map.put(EsaSkyWebConstants.MODULE_SCREENSHOT, true);
		map.put(EsaSkyWebConstants.MODULE_SHARE, true);
		map.put(EsaSkyWebConstants.MODULE_HELP, true);
		map.put(EsaSkyWebConstants.MODULE_DROPDOWN, true);
		map.put(EsaSkyWebConstants.MODULE_FEEDBACK, true);
		map.put(EsaSkyWebConstants.MODULE_LOGIN, true);
		map.put(EsaSkyWebConstants.MODULE_SKIESMENU, true);
		map.put(EsaSkyWebConstants.MODULE_OBS, true);
		map.put(EsaSkyWebConstants.MODULE_CAT, true);
		map.put(EsaSkyWebConstants.MODULE_SPE, true);
		map.put(EsaSkyWebConstants.MODULE_EXTTAP, Boolean.parseBoolean(Dictionary.getDictionary(SERVER_PROPERTIES).get("showExtTap")));
		map.put(EsaSkyWebConstants.MODULE_OUTREACH_IMAGE, true);
		map.put(EsaSkyWebConstants.MODULE_OUTREACH_JWST, true);
		map.put(EsaSkyWebConstants.MODULE_OUTREACH_EUCLID, true);
		map.put(EsaSkyWebConstants.MODULE_GW, true);
		map.put(EsaSkyWebConstants.MODULE_SSO, true);
		map.put(EsaSkyWebConstants.MODULE_PUBLICATIONS, true);
		map.put(EsaSkyWebConstants.MODULE_TARGETLIST, true);
		map.put(EsaSkyWebConstants.MODULE_JWST_PLANNING, true);
		map.put(EsaSkyWebConstants.MODULE_DICE, true);
		map.put(EsaSkyWebConstants.MODULE_SCIENCE_MODE, true);
        map.put(EsaSkyWebConstants.MODULE_SESSION, true);
		map.put(EsaSkyWebConstants.MODULE_SEARCH_TOOL, true);
		map.put(EsaSkyWebConstants.MODULE_SEARCH_BOX, true);

		return map;
	}

	private static HashMap<String, Boolean> createKioskMap() {
		HashMap<String, Boolean> map = createDefaultMap();

		map.put(EsaSkyWebConstants.MODULE_SCIENTIFIC_BUTTON, false);
		map.put(EsaSkyWebConstants.MODULE_LANGUAGE, true);
		map.put(EsaSkyWebConstants.MODULE_COOR_GRID, true);
		map.put(EsaSkyWebConstants.MODULE_SCREENSHOT, false);
		map.put(EsaSkyWebConstants.MODULE_SHARE, false);
		map.put(EsaSkyWebConstants.MODULE_HELP, false);
		map.put(EsaSkyWebConstants.MODULE_FEEDBACK, false);
		map.put(EsaSkyWebConstants.MODULE_LOGIN, false);
		map.put(EsaSkyWebConstants.MODULE_SKIESMENU, true);
		map.put(EsaSkyWebConstants.MODULE_OBS, false);
		map.put(EsaSkyWebConstants.MODULE_CAT, false);
		map.put(EsaSkyWebConstants.MODULE_SPE, false);
		map.put(EsaSkyWebConstants.MODULE_EXTTAP, false);
		map.put(EsaSkyWebConstants.MODULE_OUTREACH_IMAGE, true);
		map.put(EsaSkyWebConstants.MODULE_OUTREACH_JWST, true);
		map.put(EsaSkyWebConstants.MODULE_OUTREACH_EUCLID, true);
		map.put(EsaSkyWebConstants.MODULE_GW, false);
		map.put(EsaSkyWebConstants.MODULE_SSO, false);
		map.put(EsaSkyWebConstants.MODULE_PUBLICATIONS, false);
		map.put(EsaSkyWebConstants.MODULE_TARGETLIST, true);
		map.put(EsaSkyWebConstants.MODULE_JWST_PLANNING, false);
		map.put(EsaSkyWebConstants.MODULE_DICE, true);
		map.put(EsaSkyWebConstants.MODULE_SCIENCE_MODE, false);
		map.put(EsaSkyWebConstants.MODULE_SESSION, true);
		map.put(EsaSkyWebConstants.MODULE_SEARCH_TOOL, false);
		map.put(EsaSkyWebConstants.MODULE_DROPDOWN, false);
		map.put(EsaSkyWebConstants.MODULE_TARGETLIST_UPLOAD, false);
		map.put(EsaSkyWebConstants.MODULE_EVA_MENU, false);
		map.put(EsaSkyWebConstants.MODULE_SEARCH_IN_MENU, false);
		map.put(EsaSkyWebConstants.MODULE_KIOSK_BUTTONS, true);
		map.put(EsaSkyWebConstants.MODULE_WELCOME_DIALOG, false);
		map.put(EsaSkyWebConstants.MODULE_SEARCH_BOX, true);
		return map;
	}
	
	private static HashMap<String, Boolean> createDefaultMap() {
		HashMap<String, Boolean> map = new HashMap<>();
		
		map.put(EsaSkyWebConstants.MODULE_WWT_LINK, true);
		map.put(EsaSkyWebConstants.MODULE_INTERNATIONALIZATION, true);
		map.put(EsaSkyWebConstants.MODULE_BANNERS_ALL_SIDE, false);
		map.put(EsaSkyWebConstants.MODULE_TOGGLE_COLUMNS, true);
		map.put(EsaSkyWebConstants.MODULE_SHOW_MISSING_TRANSLATIONS, Boolean.parseBoolean(Dictionary.getDictionary(SERVER_PROPERTIES).get("showMissingTranslationBox")));
		map.put(EsaSkyWebConstants.MODULE_EVA_MENU, Boolean.parseBoolean(Dictionary.getDictionary(SERVER_PROPERTIES).get("showEva")));
		map.put(EsaSkyWebConstants.MODULE_EVA, false);
		map.put(EsaSkyWebConstants.MODULE_TARGETLIST_UPLOAD, true);
		map.put(EsaSkyWebConstants.MODULE_SEARCH_IN_MENU, true);
		map.put(EsaSkyWebConstants.MODULE_KIOSK_BUTTONS, false);
		map.put(EsaSkyWebConstants.MODULE_WELCOME_DIALOG, true);
		map.put(EsaSkyWebConstants.MODULE_SEARCH_BOX, true);
		return map;
	}
    
}
