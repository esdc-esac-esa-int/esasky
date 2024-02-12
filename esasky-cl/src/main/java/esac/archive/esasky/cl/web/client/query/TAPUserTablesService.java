package esac.archive.esasky.cl.web.client.query;

import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.JSONUtils;

public class TAPUserTablesService {
    private static TAPUserTablesService instance = null;

    private TAPUserTablesService() {}

    public static TAPUserTablesService getInstance() {
        if (instance == null) {
            instance = new TAPUserTablesService();
        }
        return instance;
    }

    public void fetchUserTables(JSONUtils.IJSONRequestCallback callback) {
        String url = EsaSkyWebConstants.TAP_USERTABLES_URL;
        JSONUtils.getJSONFromUrl(url, callback);
    }
}
