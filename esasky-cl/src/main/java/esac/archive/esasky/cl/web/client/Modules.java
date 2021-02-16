package esac.archive.esasky.cl.web.client;

import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Dictionary;

import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.exceptions.MapKeyException;

public class Modules {
    
    public static boolean proposalModule = true;
    public static boolean bannersOnAllSides = false;
    public static boolean internationalization = true;
    public static boolean ssoModule = true;
    public static boolean spectraModule = true;
    public static boolean publicationsModule = true;
    public static boolean wwtLink = true;
    public static boolean toggleColumns = true; // set from Controller with toggle_column URL flag
    private static String mode = "";
    private static HashMap<String, Boolean> moduleMap = new HashMap<>();
    
    private Modules() {
    	
    }
    
    public static final boolean shouldShowExtTap = Boolean.parseBoolean(Dictionary.getDictionary("serverProperties")
			.get("showExtTap"));

    public static void Init() {

        ContextConstants ctxConstants = GWT.create(ContextConstants.class);
        
        if (null != ctxConstants.proposalModule() && !ctxConstants.proposalModule().isEmpty()){
            proposalModule = !ctxConstants.proposalModule().toLowerCase().equals("false");
        }
        
        if (null != ctxConstants.ssoModule() && !ctxConstants.ssoModule().isEmpty()){
            ssoModule = !ctxConstants.ssoModule().toLowerCase().equals("false");
        }
        
        if (null != ctxConstants.spectraModule() && !ctxConstants.spectraModule().isEmpty()){
            spectraModule = !ctxConstants.spectraModule().toLowerCase().equals("false");
        }
        
        if (null != ctxConstants.publicationsModule() && !ctxConstants.publicationsModule().isEmpty()){
            publicationsModule = !ctxConstants.publicationsModule().toLowerCase().equals("false");
        }
    }
    

    
    public static boolean getModule(String key) {
    	if(moduleMap.containsKey(key)) {	
    		return moduleMap.get(key);
    	}else {
    		return false;
    	}
    }

    public static void setModule(String key, boolean value) throws MapKeyException {
    	if(moduleMap.containsKey(key)) {
    		moduleMap.put(key, value);
    		return;
    	}
    	throw new MapKeyException(key);
    }
    
    public static String[] getModuleKeys(){
    	return moduleMap.keySet().toArray(new String [0]);
	}
    
    public static void setMode(String mode) {
    	Modules.mode = mode;
    	
    	if("JWST".equalsIgnoreCase(mode)) {
    		moduleMap.put(EsaSkyWebConstants.MODULE_SCIENTIFIC, true);
    		moduleMap.put(EsaSkyWebConstants.MODULE_LANGUAGE, false);
    		moduleMap.put(EsaSkyWebConstants.MODULE_COOR_GRID, true);
    		moduleMap.put(EsaSkyWebConstants.MODULE_SCREENSHOT, true);
    		moduleMap.put(EsaSkyWebConstants.MODULE_SHARE, true);
    		moduleMap.put(EsaSkyWebConstants.MODULE_HELP, false);
    		moduleMap.put(EsaSkyWebConstants.MODULE_DROPDOWN, false);
    		moduleMap.put(EsaSkyWebConstants.MODULE_FEEDBACK, false);
    		moduleMap.put(EsaSkyWebConstants.MODULE_SKIESMENU, true);
    		moduleMap.put(EsaSkyWebConstants.MODULE_OBS, true);
    		moduleMap.put(EsaSkyWebConstants.MODULE_CAT, false);
    		moduleMap.put(EsaSkyWebConstants.MODULE_SPE, false);
    		moduleMap.put(EsaSkyWebConstants.MODULE_EXTTAP, false);
    		moduleMap.put(EsaSkyWebConstants.MODULE_SSO, false);
    		moduleMap.put(EsaSkyWebConstants.MODULE_PUBLICATIONS, false);
    		moduleMap.put(EsaSkyWebConstants.MODULE_TARGETLIST, false);
    		moduleMap.put(EsaSkyWebConstants.MODULE_JWST_PLANNING, true);
    		moduleMap.put(EsaSkyWebConstants.MODULE_DICE, false);
    		moduleMap.put(EsaSkyWebConstants.MODULE_SCIENCE_MODE, true);
    	}
    	else {
    		moduleMap.put(EsaSkyWebConstants.MODULE_SCIENTIFIC, true);
    		moduleMap.put(EsaSkyWebConstants.MODULE_LANGUAGE, true);
    		moduleMap.put(EsaSkyWebConstants.MODULE_COOR_GRID, true);
    		moduleMap.put(EsaSkyWebConstants.MODULE_SCREENSHOT, true);
    		moduleMap.put(EsaSkyWebConstants.MODULE_SHARE, true);
    		moduleMap.put(EsaSkyWebConstants.MODULE_HELP, true);
    		moduleMap.put(EsaSkyWebConstants.MODULE_DROPDOWN, true);
    		moduleMap.put(EsaSkyWebConstants.MODULE_FEEDBACK, true);
    		moduleMap.put(EsaSkyWebConstants.MODULE_SKIESMENU, true);
    		moduleMap.put(EsaSkyWebConstants.MODULE_OBS, true);
    		moduleMap.put(EsaSkyWebConstants.MODULE_CAT, true);
    		moduleMap.put(EsaSkyWebConstants.MODULE_SPE, true);
    		moduleMap.put(EsaSkyWebConstants.MODULE_EXTTAP, true);
    		moduleMap.put(EsaSkyWebConstants.MODULE_SSO, true);
    		moduleMap.put(EsaSkyWebConstants.MODULE_PUBLICATIONS, true);
    		moduleMap.put(EsaSkyWebConstants.MODULE_TARGETLIST, true);
    		moduleMap.put(EsaSkyWebConstants.MODULE_JWST_PLANNING, true);
    		moduleMap.put(EsaSkyWebConstants.MODULE_DICE, true);
    		moduleMap.put(EsaSkyWebConstants.MODULE_SCIENCE_MODE, true);
    	}
    }

	public static boolean isProposalModule() {
		return proposalModule;
	}

	public static boolean isBannersOnAllSides() {
		return bannersOnAllSides;
	}

	public static boolean isInternationalization() {
		return internationalization;
	}

	public static boolean isSsoModule() {
		return ssoModule;
	}

	public static boolean isSpectraModule() {
		return spectraModule;
	}

	public static boolean isPublicationsModule() {
		return publicationsModule;
	}

	public static boolean isWwtLink() {
		return wwtLink;
	}

	public static boolean isToggleColumns() {
		return toggleColumns;
	}

	public static String getMode() {
		return mode;
	}

	public static boolean isShouldshowexttap() {
		return shouldShowExtTap;
	}

	public static HashMap<String, Boolean> getModuleMap() {
		return moduleMap;
	}

	public static void setProposalModule(boolean proposalModule) {
		Modules.proposalModule = proposalModule;
	}

	public static void setBannersOnAllSides(boolean bannersOnAllSides) {
		Modules.bannersOnAllSides = bannersOnAllSides;
	}

	public static void setInternationalization(boolean internationalization) {
		Modules.internationalization = internationalization;
	}

	public static void setSsoModule(boolean ssoModule) {
		Modules.ssoModule = ssoModule;
	}

	public static void setSpectraModule(boolean spectraModule) {
		Modules.spectraModule = spectraModule;
	}

	public static void setPublicationsModule(boolean publicationsModule) {
		Modules.publicationsModule = publicationsModule;
	}

	public static void setWwtLink(boolean wwtLink) {
		Modules.wwtLink = wwtLink;
	}

	public static void setToggleColumns(boolean toggleColumns) {
		Modules.toggleColumns = toggleColumns;
	}

	public static void setModuleMap(HashMap<String, Boolean> moduleMap) {
		Modules.moduleMap = moduleMap;
	}
    
}
