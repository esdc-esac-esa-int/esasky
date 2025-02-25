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
