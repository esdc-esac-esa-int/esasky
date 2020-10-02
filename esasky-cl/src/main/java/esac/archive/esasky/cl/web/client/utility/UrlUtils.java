package esac.archive.esasky.cl.web.client.utility;

import java.util.AbstractMap.SimpleEntry;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Navigator;

import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;
import esac.archive.esasky.cl.web.client.Modules;
import esac.archive.esasky.cl.web.client.status.GUISessionStatus;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.selectsky.SelectSkyPanel;

public final class UrlUtils {

	public static String getUrlForCurrentState() {
	    
		String encodedRaDeg = URL.encodeQueryString(new Double(AladinLiteWrapper.getCenterRaDeg()).toString());
		String encodedDecDeg = URL.encodeQueryString(new Double(AladinLiteWrapper.getCenterDecDeg()).toString());
		String encodedFov = URL.encodeQueryString(new Double(AladinLiteWrapper.getAladinLite().getFovDeg()).toString());
		String encodedHips = URL.encodeQueryString(SelectSkyPanel.getNameOfSelectedHips());
		String encodedCooFrame = URL.encodeQueryString(AladinLiteWrapper.getCoordinatesFrame().toString());
		String hostName = Window.Location.getHost();
		String baseUrl = Window.Location.getPath();
		
		String bibcodeOrAuthor = "";
        if(urlHasBibcode()){
            bibcodeOrAuthor = "&" + EsaSkyWebConstants.PUBLICATIONS_BIBCODE_URL_PARAM + "=" + URL.encodeQueryString(Window.Location.getParameterMap().get(EsaSkyWebConstants.PUBLICATIONS_BIBCODE_URL_PARAM).get(0));
        } else if(urlHasAuthor()){
            bibcodeOrAuthor = "&" + EsaSkyWebConstants.PUBLICATIONS_AUTHOR_URL_PARAM + "=" + URL.encodeQueryString(Window.Location.getParameterMap().get(EsaSkyWebConstants.PUBLICATIONS_AUTHOR_URL_PARAM).get(0));
        }
        
		String codeServer = "";
		String userAgent = Navigator.getUserAgent();
		if (hostName.contains("localhost") && userAgent.contains("Firefox") && new Double(userAgent.substring( (userAgent.indexOf("Firefox") + 8) )) < 30) {
			codeServer = "&gwt.codesvr=127.0.0.1:9997";
		}
		
		String logLevel = "";
		if(Window.Location.getParameterMap().containsKey("log_level")){
			logLevel = "&log_level=" + Window.Location.getParameterMap().get("log_level").get(0);
		}
		
		String language = "";
		if(Modules.internationalization) {
			language = "&" + EsaSkyConstants.INTERNATIONALIZATION_LANGCODE_URL_PARAM + "=" + GUISessionStatus.getCurrentLanguage();
		}
		String toggleColumns = "";
		if(Window.Location.getParameterMap().containsKey(EsaSkyWebConstants.URL_PARAM_TOGGLE_COLUMNS)) {
		    toggleColumns = "&" + EsaSkyWebConstants.URL_PARAM_TOGGLE_COLUMNS + "=" + Window.Location.getParameterMap().get(EsaSkyWebConstants.URL_PARAM_TOGGLE_COLUMNS).get(0);
		}
		String layout = "";
		if(Modules.mode != null && Modules.mode != "") {
			layout = "&" + EsaSkyWebConstants.URL_PARAM_LAYOUT + "=" + Modules.mode;
		}
		
		String bookmarkUrl = baseUrl 
				+ "?" + EsaSkyWebConstants.URL_PARAM_TARGET + "=" + encodedRaDeg + "%20" + encodedDecDeg 
				+ "&" + EsaSkyWebConstants.URL_PARAM_HIPS + "=" + encodedHips 
				+ "&" + EsaSkyWebConstants.URL_PARAM_FOV + "=" + encodedFov
				+ "&" + EsaSkyWebConstants.URL_PARAM_FRAME_COORD + "=" + encodedCooFrame
				+ "&" + EsaSkyWebConstants.URL_PARAM_SCI_MODE + "=" + GUISessionStatus.getIsInScienceMode()
				+ language
				+ bibcodeOrAuthor 
				+ codeServer 
				+ toggleColumns
				+ logLevel
				+ layout;
		return bookmarkUrl;
	}
	
	public static native void updateURLWithoutReloadingJS(String newUrl) /*-{
		$wnd.history.replaceState(newUrl, "", newUrl);
	}-*/;
	
    public static boolean urlHasBibcode() {
        return Window.Location.getParameterMap().containsKey(EsaSkyWebConstants.PUBLICATIONS_BIBCODE_URL_PARAM);
    }
    
    public static boolean urlHasAuthor() {
        return Window.Location.getParameterMap().containsKey(EsaSkyWebConstants.PUBLICATIONS_AUTHOR_URL_PARAM);
    }
    
    public static String getUrlLangCode() {
        if (Window.Location.getParameterMap().containsKey(EsaSkyConstants.INTERNATIONALIZATION_LANGCODE_URL_PARAM)) {
            final String langCode = Window.Location.getParameter(EsaSkyConstants.INTERNATIONALIZATION_LANGCODE_URL_PARAM).toLowerCase();
    		for(SimpleEntry<String, String> entry : EsaSkyConstants.AVAILABLE_LANGUAGES) {
    			if(entry.getKey().equalsIgnoreCase(langCode)) {
    				return langCode;
    			}
    		}
        }
        Log.debug("Lang code empty");
        return "";
    }
}
