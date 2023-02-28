package esac.archive.esasky.cl.web.client.query;


import com.google.gwt.http.client.URL;
import com.google.gwt.safehtml.shared.UriUtils;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.JSONUtils;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;

import java.util.List;
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

    public void fetchDescriptors(List<String> schemas, String category, JSONUtils.IJSONRequestCallback callback) {
        String url = EsaSkyWebConstants.TAP_DESCRIPTOR_URL + "?schema=" + String.join(",", schemas) + "&category=" + category;
        JSONUtils.getJSONFromUrl(url, callback);
    }

    public void initializeColumns(CommonTapDescriptor descriptor, JSONUtils.IJSONRequestCallback callback) {
        String url;
        String query = "SELECT * FROM TAP_SCHEMA.columns where table_name='" + descriptor.getTableName() + "'";
        if (Objects.equals(descriptor.getCategory(), EsaSkyWebConstants.CATEGORY_PUBLICATIONS)) {
            query = query.replaceAll("='[A-Za-z0-9._-]+'", "='basic'");
            url = TAPUtils.getSIMBADTAPQuery("pub_meta", URL.encodeQueryString(query), null);
        } else if (Objects.equals(descriptor.getCategory(), EsaSkyWebConstants.CATEGORY_EXTERNAL)) {
            query = "SELECT * FROM " + descriptor.getTableName();
            url = EsaSkyWebConstants.EXT_TAP_URL + "?"
                    + EsaSkyConstants.EXT_TAP_ACTION_FLAG + "=" + EsaSkyConstants.EXT_TAP_ACTION_REQUEST + "&"
                    + EsaSkyConstants.EXT_TAP_ADQL_FLAG + "=" + query + "&"
                    + EsaSkyConstants.EXT_TAP_MAX_REC_FLAG + "=" + "1" + "&"
                    + EsaSkyConstants.EXT_TAP_URL_FLAG + "=" + descriptor.getTapUrl();
        } else {
            url = createSyncUrl(EsaSkyWebConstants.TAP_SYNC_URL, "json", query);
        }

        JSONUtils.getJSONFromUrl(url, callback);
    }

    private String createSyncUrl(String url, String format, String query) {
        String args = "request=doQuery&lang=ADQL&format=" + format;
        return url + "?" + args + "&" + "query=" + UriUtils.encode(query);
    }

}
