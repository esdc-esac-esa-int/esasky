package esac.archive.esasky.cl.web.client.query;


import com.google.gwt.safehtml.shared.UriUtils;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.JSONUtils;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;

public final class TAPDescriptorService {
    private static TAPDescriptorService instance = null;
//    private final String args = "request=doQuery&lang=ADQL&format=json";
//    private final String URL = EsaSkyWebConstants.TAP_DESCRIPTOR_URL;

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
        String query = "SELECT TOP 1 * FROM ";
        JSONUtils.getJSONFromUrl(createSyncUrl(query + descriptor.getTableName()), callback);
    }

    private String createSyncUrl(String query) {
        String args = "request=doQuery&lang=ADQL&format=json";
        String url = EsaSkyWebConstants.TAP_SYNC_URL + "?" + args;
        return url + "&" + "query=" + UriUtils.encode(query);
    }

}
