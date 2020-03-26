package esac.archive.esasky.cl.web.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Dictionary;

public class Modules {
    
    public static boolean proposalModule = true;
    public static boolean improvedDownload = true;
    public static boolean useTabulator = true;
    public static boolean bannersOnAllSides = false;
    public static boolean internationalization = true;
    public static boolean ssoModule = true;
    public static boolean spectraModule = true;
    public static boolean publicationsModule = true;
    public static boolean wwtLink = true;
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
}
