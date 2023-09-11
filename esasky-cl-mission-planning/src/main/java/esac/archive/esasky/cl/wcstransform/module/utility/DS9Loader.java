package esac.archive.esasky.cl.wcstransform.module.utility;

import com.allen_sauer.gwt.log.client.Log;
import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.*;
import esac.archive.esasky.ifcs.model.descriptor.DS9DescriptorList;

public class DS9Loader {
    private static DS9DescriptorList ds9DescriptorList;
    public static final String DS9_SERVLET = "/ds9";
    private static int timeout = 120000;

    public interface ds9DescriptorListMapper extends ObjectMapper<DS9DescriptorList> {
    }

    public DS9Loader(String baseUrl) {
        getDs9Descriptors(baseUrl + DS9_SERVLET, timeout);
    }

    private static void getDs9Descriptors(final String url, int timeoutMillis) {

        Log.debug("[getSiafEntriesFormUrl] Query [" + url + "]");
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
        builder.setTimeoutMillis(timeoutMillis);

        try {
            builder.sendRequest(null, new RequestCallback() {
                @Override
                public void onResponseReceived(final Request request, final Response response) {
                    if (200 == response.getStatusCode()) {
                        ds9DescriptorListMapper mapper = GWT.create(ds9DescriptorListMapper.class);
                        ds9DescriptorList = mapper.read(response.getText());
                        Log.debug("SiafEntries=" + response.getText());
                        InstrumentMapping.getInstance().setDs9Entries(ds9DescriptorList);

                    } else {
                        Log.error("[getSiafEntriesFormUrl] Couldn't retrieve JSON (" + response.getStatusText() + ") from "
                                + url);
                    }
                }

                @Override
                public void onError(final Request request, final Throwable e) {
                    Log.error("[getSiafEntriesFormUrl] Error fetching JSON data from server:" + e.getMessage());
                }
            });

        } catch (RequestException e) {
            Log.error("[getSiafEntriesFormUrl] Error fetching JSON data from server" + e.getMessage());
        }
    }


}
