package esac.archive.esasky.cl.web.client.query;

import com.allen_sauer.gwt.log.client.Log;

import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;

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

    public static String getTAPMocQuery(final String center, final String stcs, final String tableName) {

        // Get System time call
        Long timecall = System.currentTimeMillis();

        Log.debug("[TAPUtils/getTAPQuery()] timecall " + timecall);
        return EsaSkyWebConstants.TAP_CONTEXT + "/tap/mocs?center=" + center +
                "&stcs="+ stcs + "&tablename="+ tableName + "&timecall=" + timecall + "&client=esasky-web-client";
    }
}
