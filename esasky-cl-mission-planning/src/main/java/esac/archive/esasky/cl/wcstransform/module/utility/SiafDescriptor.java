package esac.archive.esasky.cl.wcstransform.module.utility;

import com.allen_sauer.gwt.log.client.Log;
import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;

import esac.archive.esasky.ifcs.model.descriptor.SiafEntries;

public class SiafDescriptor {
	private static SiafEntries siafEntries;
	public static final String SIAF_JWST_SERVLET = "/siaf/jwst";
	private static int timeout = 120000;

	public interface SiafDescriptorListMapper extends ObjectMapper<SiafEntries> {
	}

	public SiafDescriptor(String baseUrl) {
		getSiafEntriesFormUrl(baseUrl + SIAF_JWST_SERVLET, timeout);
	}

	private static void getSiafEntriesFormUrl(final String url, int timeoutMillis) {

		Log.debug("[getSiafEntriesFormUrl] Query [" + url + "]");
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
		builder.setTimeoutMillis(timeoutMillis);

		try {
			builder.sendRequest(null, new RequestCallback() {
				@Override
				public void onResponseReceived(final Request request, final Response response) {
					if (200 == response.getStatusCode()) {
						SiafDescriptorListMapper mapper = GWT.create(SiafDescriptorListMapper.class);
						siafEntries = mapper.read(response.getText());
						Log.debug("SiafEntries=" + response.getText());
						InstrumentMapping.getInstance().setSiafEntries(siafEntries);

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
