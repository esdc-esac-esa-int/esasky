package esac.archive.esasky.cl.web.client.query;


import com.google.gwt.http.client.URL;
import com.google.gwt.safehtml.shared.UriUtils;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.JSONUtils;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;

import java.util.Objects;

public final class TAPDescriptorService {
    private static TAPDescriptorService instance = null;

    private TAPDescriptorService() {}

    public static TAPDescriptorService getInstance() {
        if (instance == null) {
            instance = new TAPDescriptorService();
        }
        return instance;
    }

    public void fetchDescriptors(String schema, String category, JSONUtils.IJSONRequestCallback callback) {
        String url = EsaSkyWebConstants.TAP_DESCRIPTOR_URL + "?schema=" + schema + "&category=" + category;
        JSONUtils.getJSONFromUrl(url, callback);
    }

    public void initializeColumns(CommonTapDescriptor descriptor, JSONUtils.IJSONRequestCallback callback) {
        String url;
        if (Objects.equals(descriptor.getCategory(), EsaSkyWebConstants.CATEGORY_PUBLICATIONS)) {
            String query = "SELECT TOP 1 main_id as name, ra, dec, nbref as bibcount FROM basic";
            url = TAPUtils.getSIMBADTAPQuery("pub_sources", URL.encodeQueryString(query), null);
        } else {
            String query = "SELECT TOP 1 * FROM " + descriptor.getTableName();
            url = createSyncUrl(EsaSkyWebConstants.TAP_SYNC_URL, query);
        }

        JSONUtils.getJSONFromUrl(url, callback);
    }

    private String createSyncUrl(String url, String query) {
        String args = "request=doQuery&lang=ADQL&format=json";
        return url + "?" + args + "&" + "query=" + UriUtils.encode(query);
    }

}
