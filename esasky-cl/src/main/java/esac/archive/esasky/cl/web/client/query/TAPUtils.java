package esac.archive.esasky.cl.web.client.query;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.http.client.URL;

import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;

public class TAPUtils {

    /**
     * getTAPQuery().
     * @param adql Input String
     * @param formatResponse Input String containing the values JSON, VOTABLE, CSV or ASCII
     * @return String
     */
    public static String getTAPQuery(final String adql, final String formatResponse) {

        // Get System time call
        Long timecall = System.currentTimeMillis();
        String adqlParameterAndValue = "";
        if(!adql.isEmpty()) {
        	adqlParameterAndValue = "&query=" + adql;
        }

        Log.debug("[TAPUtils/getTAPQuery()] timecall " + timecall);
        return EsaSkyWebConstants.TAP_CONTEXT + "/tap/sync?request=doQuery&lang=ADQL&format="
        + formatResponse + adqlParameterAndValue + "&timecall=" + timecall + "&client=esasky-web-client";
    }
    
    /**
     * getSIMBADTAPQuery().
     * @param adql Input String
     * @return String
     */
    public static String getSIMBADTAPQuery(final String responseType, final String adql, final String dataWrap) {
        return EsaSkyWebConstants.SIMBAD_TAP_URL + "?TYPE=" + responseType + "&QUERY=" + adql + ((dataWrap != null) ? "&DATAWRAP=" + dataWrap : "");
    }
    
    /**
     * getTAPQuery().
     * @param adql Input String
     * @param formatResponse Input String containing the values JSON, VOTABLE, CSV or ASCII
     * @return String
     */
    public static String getTAPCountQuery(final String stcs) {

        // Get System time call
        Long timecall = System.currentTimeMillis();

        Log.debug("[TAPUtils/getTAPQuery()] timecall " + timecall);
        return EsaSkyWebConstants.TAP_CONTEXT + "/tap/counts?stcs="
        + stcs + "&timecall=" + timecall + "&client=esasky-web-client";
    }

    public static String getTAPMocQuery(final String center, final String stcs, final String tableName,
    		int order,  String filters, boolean precomputedMaxmin) {

        // Get System time call
        Long timecall = System.currentTimeMillis();

        Log.debug("[TAPUtils/getTAPQuery()] timecall " + timecall);
        String url =  EsaSkyWebConstants.TAP_CONTEXT + "/tap/mocs?center=" + center + "&order=" + Integer.toString(order) +
                "&stcs="+ stcs + "&tablename="+ tableName + "&precomputedMaxmin=" + Boolean.toString(precomputedMaxmin)
                + "&timecall=" + timecall + "&client=esasky-web-client";
       
        if(filters != null && !"".equals(filters)) {
        	url += "&filter" + filters;
        }
        
        return url;
    }
    
    public static String getTAPMocFilteredQuery(final String tableName, int order, GeneralJavaScriptObject visiblePixels, String filters, boolean includeMaxMin) {
    	
    	// Get System time call
    	Long timecall = System.currentTimeMillis();
    	
    	Log.debug("[TAPUtils/getTAPQuery()] timecall " + timecall);
		String pixelString = TAPMOCService.mocObjectToString(visiblePixels);
    	String url =  EsaSkyWebConstants.TAP_CONTEXT + "/tap/mocs?tablename="+ tableName + "&pixels=" +  URL.encodeQueryString(pixelString) +
    			"&order=" + Integer.toString(order) + "&includeMaxmin=" + Boolean.toString(includeMaxMin) + 
    			"&timecall=" + timecall + "&client=esasky-web-client";
    	
    	if(filters != null && !"".equals(filters)) {
        	url += "&filter=" + URL.encodeQueryString(filters);
        }
    	
    	return url;
    }
}
