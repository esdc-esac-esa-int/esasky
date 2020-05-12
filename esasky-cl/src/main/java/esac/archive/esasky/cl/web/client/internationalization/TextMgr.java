package esac.archive.esasky.cl.web.client.internationalization;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.AbstractMap.SimpleEntry;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.user.client.Timer;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;
import esac.archive.esasky.cl.web.client.status.GUISessionStatus;
import esac.archive.esasky.cl.web.client.utility.DisplayUtils;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;

public class TextMgr {

	public interface InitCallback {
		public void onInitialized (boolean success);
	}
	
	private static TextMgr instance;
	private static TextMgr defaultInstance;
	private static InitCallback initCallback;
	
	private HashMap<String, String> texts;
	
	private String baseUrl = GWT.getHostPageBaseURL() + "internationalization/";
	
	private String langCode = EsaSkyConstants.DEFAULT_LANGCODE;
	
	//private ContextConstants ctxConstants;
	
	private static final String LOCALE_URL = Dictionary.getDictionary("serverProperties")
            .get("localeFilesLocation");
	private static final boolean shouldShowMissingTranslationBox = Boolean.parseBoolean(Dictionary.getDictionary("serverProperties")
			.get("showMissingTranslationBox"));
	
	
	
	private TextMgr(String langCode) {
		
	    //ctxConstants = GWT.create(ContextConstants.class);

//	    if (null != ctxConstants.localeFilesLocation() && !ctxConstants.localeFilesLocation().isEmpty()){
//            baseUrl = ctxConstants.localeFilesLocation();
//        }
	    
	    if (null != LOCALE_URL && !LOCALE_URL.isEmpty()){
//            baseUrl = LOCALE_URL;
        }
	    
	    setLangCode(langCode);
		new Timer() {
			
			@Override
			public void run() {
				boolean newMissingTranslationsFound = false;
				String missedTranslationList = "";
				for(String missedTranslation : missingTranslations) {
					if(!reportedMissingTranslations.contains(missedTranslation)) {
						reportedMissingTranslations.add(missedTranslation);
						newMissingTranslationsFound = true;
						if(missedTranslationList.equals("")) {
							missedTranslationList = missedTranslation;
						} else {
							missedTranslationList += ", " + missedTranslation;
						}
						
					}
				}
				if(newMissingTranslationsFound && shouldShowMissingTranslationBox) {
					DisplayUtils.showMessageDialogBox(
							"If you have time, please consider informing us by reporting the following keys to esdc_esasky@sciops.esa.int: "
							+ missedTranslationList, 
							"Missing translations (This dialog is deactivated in operational versions)", UUID.randomUUID().toString());
				}
			}
		}.scheduleRepeating(15000);
	}
	
	public static void Init(String localeLanguage, InitCallback initCallback) {
		Log.debug("TextMgr.Init() langCode: " + localeLanguage);
		TextMgr.initCallback = initCallback;
		instance = new TextMgr(localeLanguage);
		if(instance.getLangCode() == EsaSkyConstants.DEFAULT_LANGCODE) {
			defaultInstance = instance;
		}else {
			defaultInstance = new TextMgr(EsaSkyConstants.DEFAULT_LANGCODE);
		}
		exposeToJavascript(instance, defaultInstance);
	}
	
	public static native void exposeToJavascript(TextMgr instance, TextMgr defaultInstance)/*-{
	    if(!$wnd.esasky) {$wnd.esasky = {};}
        $wnd.esasky.getInternationalizationText = function(text) {
            return instance.@esac.archive.esasky.cl.web.client.internationalization.TextMgr::getText(Ljava/lang/String;)(text);
        }
        $wnd.esasky.getDefaultLanguageText = function(text) {
            return defaultInstance.@esac.archive.esasky.cl.web.client.internationalization.TextMgr::getText(Ljava/lang/String;)(text);
        }
     	$wnd.esasky.getColumnDisplayText = function(displayText){
			displayText = displayText.replace(/_/g," ");
			displayText = displayText.replace( /(^|\s)([a-z])/g , function(m,p1,p2){ return p1+p2.toUpperCase();} );
			return displayText;	
     	}
    }-*/;
	
	public static boolean isInitialized() {
		return instance != null;
	}
	
	public static TextMgr getDefaultInstance() {
		if (!isInitialized()) {
			Log.error("TextMgr not initialized!!"); 
		}
		return instance;
	}

	public static TextMgr getInstance() {
		if (!isInitialized()) {
			Log.error("TextMgr not initialized!!"); 
		}
		return instance;
	}
	
	public String getText (String key) {
	    
        if (key.isEmpty()) {
            return key;
        }
	       
		if (texts != null) {
			if (texts.containsKey(key)) {
				return texts.get(key);	
			}
		}
		
		if(this.langCode != EsaSkyConstants.DEFAULT_LANGCODE) {
			return TextMgr.getDefaultInstance().getText(key);
		}
		
		missingTranslations.add(key);
		GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_Internationalization, GoogleAnalytics.ACT_MissingTranslation + " " + langCode, key);
		Log.warn("TextMgr.getText(), unknown key: " + key);
		return key;
	}
	
	private Set<String> reportedMissingTranslations = new HashSet<String>();
	private Set<String> missingTranslations = new HashSet<String>();
	
	public String getLangCode () {
	    return langCode;
	}
	
	private String getTwoLetterLangCode(String newLangCode) {
		String twoLetterLangCode = newLangCode.substring(0, 2);
		Log.debug(twoLetterLangCode + ":");
		for(SimpleEntry<String, String> entry : EsaSkyConstants.AVAILABLE_LANGUAGES) {
			if(entry.getKey().equalsIgnoreCase(twoLetterLangCode)) {
				return twoLetterLangCode;
			}
		}
		Log.warn("TextMgr.setLangCode() langCode: " + newLangCode + ", IS NOT SUPPORTED");
		return EsaSkyConstants.DEFAULT_LANGCODE;
	}
	
	public void setLangCode(String newLangCode) {
		langCode = getTwoLetterLangCode(newLangCode);
		Log.debug("TextMgr.setLangCode() langCode: " + langCode);
		texts = new HashMap<String, String>();
		
		//Requests the translations file with the "v" parameter to avoid caching issues if EsaSky version changes
		//TODO use build number and not hard coded version number
		readXML(baseUrl + "internationalization_" + langCode + ".xml?v=" + GWT.getModuleName() + "3.2", this);
		if(instance == null || this == getInstance()) {
			GUISessionStatus.setCurrentLanguage(langCode);
		}
	}
	
	private void getTextsFromXML(Document xmlDoc) {

		try {
			if (xmlDoc != null) {
				
				NodeList nodes = xmlDoc.getElementsByTagName("text");
				
				for (int childIdx = 0; childIdx < nodes.getLength(); childIdx++) {
					
					Node textNode = nodes.item(childIdx);
					
					final String key = textNode.getAttributes().getNamedItem("key").getNodeValue();
					final String value = textNode.getFirstChild().getNodeValue().trim();
					texts.put(key, value);
					
				}
			}
			
			onInitialized(texts.size() > 0);
			
		} catch(Exception ex) {
			Log.error("TextMgr.readAllTexts()", ex);
			onInitialized(false);
		}
	}
	
	//TODO: Sure this method is replicated somewhere in the project, move to some utils class
	public static void readXML(String url, final TextMgr intManager) {
	    
	    Log.debug("TextMgr.readXML() from url: " + url);
	    
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
        try {
        	
            builder.sendRequest(null, new RequestCallback() {
                
	    			public void onError(Request request, Throwable ex) {
	    				Log.error("TextMgr.readXML() onError", ex);
	    				intManager.onInitialized(false);
                }

                public void onResponseReceived(Request request, Response response) {
                    	try {
                    		final String result = response.getText();
                    		Document xmlDoc = XMLParser.parse(result); 
                        	intManager.getTextsFromXML(xmlDoc);
                        	
                    	} catch(Exception ex) {
                    		Log.error("TextMgr.readXML().onResponseReceived", ex);
                    		intManager.onInitialized(false);
                    		GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_Internationalization, GoogleAnalytics.ACT_LoadingOfXMLFailed, getInstance().langCode);
                    	}
                }
            });
        } catch (RequestException ex) {
        	    Log.error("TextMgr.readXML()", ex);
        }
	}
	
	private void onInitialized(boolean success) {
		if (TextMgr.initCallback != null) {
			TextMgr.initCallback.onInitialized(success);
			TextMgr.initCallback = null;
		}
	}

}
