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

package esac.archive.esasky.cl.web.client.query;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.http.client.URL;

import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
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

        String stcQuery = "";
        if (stcs != null && !stcs.isEmpty()) {
            stcQuery = "&stcs=" + stcs;
        }

        return EsaSkyWebConstants.TAP_CONTEXT + "/tap/counts?"
        + stcQuery + "&timecall=" + timecall + "&client=esasky-web-client";
    }

    public static String getTAPMocQuery(final String center, String stcs, final String tableName,
    		int order,  String filters, boolean precomputedMaxmin) {

        // Get System time call
        Long timecall = System.currentTimeMillis();

        Log.debug("[TAPUtils/getTAPQuery()] timecall " + timecall);
        String url =  EsaSkyWebConstants.TAP_CONTEXT + "/tap/mocs?center=" + center + "&order=" + order +
                "&stcs="+ stcs + "&tablename="+ tableName + "&precomputedMaxmin=" + precomputedMaxmin
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
