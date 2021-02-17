package esac.archive.esasky.cl.web.client;

import java.util.HashMap;

import com.google.gwt.i18n.client.Dictionary;

import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.exceptions.MapKeyException;

public class Modules {
    
    private static String mode = "";
    
    private static HashMap<String, Boolean> currentModuleMap = new HashMap<>();
    private static HashMap<String, HashMap<String, Boolean>> allModuleMaps = new HashMap<>();
    
    private Modules() {
    	
    }
    
    public static void Init() {

        allModuleMaps.put(EsaSkyWebConstants.MODULE_MODE_CLEAN, createCleanMap());
        allModuleMaps.put(EsaSkyWebConstants.MODULE_MODE_ESASKY, createEsaskyMap());
        allModuleMaps.put(EsaSkyWebConstants.MODULE_MODE_JWST, createJWSTMap());
        
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
    		return;
    	}
    	throw new MapKeyException(key);
    }
    
    public static String[] getModuleKeys(){
    	return currentModuleMap.keySet().toArray(new String [0]);
	}
    
    public static void setMode(String mode) {
    	Modules.mode = mode;
    	if(mode != null && allModuleMaps.containsKey(mode.toUpperCase())) {
    		currentModuleMap = allModuleMaps.get(mode.toUpperCase());
    	}else {
    		currentModuleMap = allModuleMaps.get(EsaSkyWebConstants.MODULE_MODE_ESASKY);
    	}
    }

	public static String getMode() {
		return mode;
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
        map.put(EsaSkyWebConstants.MODULE_SKIESMENU, true);
        map.put(EsaSkyWebConstants.MODULE_OBS, false);
        map.put(EsaSkyWebConstants.MODULE_CAT, false);
        map.put(EsaSkyWebConstants.MODULE_SPE, false);
        map.put(EsaSkyWebConstants.MODULE_EXTTAP, false);
        map.put(EsaSkyWebConstants.MODULE_SSO, false);
        map.put(EsaSkyWebConstants.MODULE_PUBLICATIONS, false);
        map.put(EsaSkyWebConstants.MODULE_TARGETLIST, false);
        map.put(EsaSkyWebConstants.MODULE_JWST_PLANNING, false);
        map.put(EsaSkyWebConstants.MODULE_DICE, false);
        map.put(EsaSkyWebConstants.MODULE_SCIENCE_MODE, false);

        
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
		map.put(EsaSkyWebConstants.MODULE_SKIESMENU, true);
		map.put(EsaSkyWebConstants.MODULE_OBS, true);
		map.put(EsaSkyWebConstants.MODULE_CAT, false);
		map.put(EsaSkyWebConstants.MODULE_SPE, false);
		map.put(EsaSkyWebConstants.MODULE_EXTTAP, false);
		map.put(EsaSkyWebConstants.MODULE_SSO, false);
		map.put(EsaSkyWebConstants.MODULE_PUBLICATIONS, false);
		map.put(EsaSkyWebConstants.MODULE_TARGETLIST, false);
		map.put(EsaSkyWebConstants.MODULE_JWST_PLANNING, true);
		map.put(EsaSkyWebConstants.MODULE_DICE, false);
		map.put(EsaSkyWebConstants.MODULE_SCIENCE_MODE, true);

		
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
		map.put(EsaSkyWebConstants.MODULE_SKIESMENU, true);
		map.put(EsaSkyWebConstants.MODULE_OBS, true);
		map.put(EsaSkyWebConstants.MODULE_CAT, true);
		map.put(EsaSkyWebConstants.MODULE_SPE, true);
		map.put(EsaSkyWebConstants.MODULE_EXTTAP, Boolean.parseBoolean(Dictionary.getDictionary("serverProperties").get("showExtTap")));
		map.put(EsaSkyWebConstants.MODULE_SSO, true);
		map.put(EsaSkyWebConstants.MODULE_PUBLICATIONS, true);
		map.put(EsaSkyWebConstants.MODULE_TARGETLIST, true);
		map.put(EsaSkyWebConstants.MODULE_JWST_PLANNING, true);
		map.put(EsaSkyWebConstants.MODULE_DICE, true);
		map.put(EsaSkyWebConstants.MODULE_SCIENCE_MODE, true);
		
		return map;
	}
	
	private static HashMap<String, Boolean> createDefaultMap() {
		HashMap<String, Boolean> map = new HashMap<>();
		
		map.put(EsaSkyWebConstants.MODULE_WWT_LINK, true);
		map.put(EsaSkyWebConstants.MODULE_INTERNATIONALIZATION, true);
		map.put(EsaSkyWebConstants.MODULE_BANNERS_ALL_SIDE, false);
		map.put(EsaSkyWebConstants.MODULE_TOGGLE_COLUMNS, true);
		
		return map;
	}
    
}
