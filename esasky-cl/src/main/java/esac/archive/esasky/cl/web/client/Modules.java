package esac.archive.esasky.cl.web.client;

import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Dictionary;

import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;

public class Modules {
    
    public static boolean proposalModule = true;
    public static boolean bannersOnAllSides = false;
    public static boolean internationalization = true;
    public static boolean ssoModule = true;
    public static boolean spectraModule = true;
    public static boolean publicationsModule = true;
    public static boolean wwtLink = true;
    public static boolean toggleColumns = false; // set from Controller with toggle_column URL flag
    public static String mode = "";
    
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
    
    private static HashMap<String, Boolean> modules = new HashMap<>();
    
    public static boolean getModule(String key) {
    	if(modules.containsKey(key)) {	
    		return modules.get(key);
    	}else {
    		return false;
    	}
    }

    public static void setModule(String key, boolean value) throws Exception {
    	if(modules.containsKey(key)) {
    		modules.put(key, value);
    		return;
    	}
    	throw new Exception(key);
    }
    
    public static String[] getModuleKeys(){
    	return modules.keySet().toArray(new String [0]);
	}
    
    public static void setMode(String mode) {
    	Modules.mode = mode;
    	
    	if(mode == "JWST") {
    		modules.put(EsaSkyWebConstants.MODULE_SCIENTIFIC, false);
    		modules.put(EsaSkyWebConstants.MODULE_LANGUAGE, false);
    		modules.put(EsaSkyWebConstants.MODULE_COOR_GRID, true);
    		modules.put(EsaSkyWebConstants.MODULE_SCREENSHOT, true);
    		modules.put(EsaSkyWebConstants.MODULE_SHARE, true);
    		modules.put(EsaSkyWebConstants.MODULE_HELP, false);
    		modules.put(EsaSkyWebConstants.MODULE_DROPDOWN, false);
    		modules.put(EsaSkyWebConstants.MODULE_FEEDBACK, false);
    		modules.put(EsaSkyWebConstants.MODULE_SKIESMENU, true);
    		modules.put(EsaSkyWebConstants.MODULE_OBS, false);
    		modules.put(EsaSkyWebConstants.MODULE_CAT, false);
    		modules.put(EsaSkyWebConstants.MODULE_SPE, false);
    		modules.put(EsaSkyWebConstants.MODULE_EXTTAP, false);
    		modules.put(EsaSkyWebConstants.MODULE_SSO, false);
    		modules.put(EsaSkyWebConstants.MODULE_PUBLICATIONS, false);
    		modules.put(EsaSkyWebConstants.MODULE_TARGETLIST, false);
    		modules.put(EsaSkyWebConstants.MODULE_JWST_PLANNING, true);
    		modules.put(EsaSkyWebConstants.MODULE_DICE, false);
    		modules.put(EsaSkyWebConstants.MODULE_SCIENCE, false);
    	}
    	else {
    		modules.put(EsaSkyWebConstants.MODULE_SCIENTIFIC, true);
    		modules.put(EsaSkyWebConstants.MODULE_LANGUAGE, true);
    		modules.put(EsaSkyWebConstants.MODULE_COOR_GRID, true);
    		modules.put(EsaSkyWebConstants.MODULE_SCREENSHOT, true);
    		modules.put(EsaSkyWebConstants.MODULE_SHARE, true);
    		modules.put(EsaSkyWebConstants.MODULE_HELP, true);
    		modules.put(EsaSkyWebConstants.MODULE_DROPDOWN, true);
    		modules.put(EsaSkyWebConstants.MODULE_FEEDBACK, true);
    		modules.put(EsaSkyWebConstants.MODULE_SKIESMENU, true);
    		modules.put(EsaSkyWebConstants.MODULE_OBS, true);
    		modules.put(EsaSkyWebConstants.MODULE_CAT, true);
    		modules.put(EsaSkyWebConstants.MODULE_SPE, true);
    		modules.put(EsaSkyWebConstants.MODULE_EXTTAP, true);
    		modules.put(EsaSkyWebConstants.MODULE_SSO, true);
    		modules.put(EsaSkyWebConstants.MODULE_PUBLICATIONS, true);
    		modules.put(EsaSkyWebConstants.MODULE_TARGETLIST, true);
    		modules.put(EsaSkyWebConstants.MODULE_JWST_PLANNING, true);
    		modules.put(EsaSkyWebConstants.MODULE_DICE, true);
    		modules.put(EsaSkyWebConstants.MODULE_SCIENCE, true);
    	}
    }
    
}
