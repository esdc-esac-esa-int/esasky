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
                        Log.debug("Ds9Entries=" + response.getText());
                        InstrumentMapping.getInstance().setDs9Entries(ds9DescriptorList);

                    } else {
                        Log.error("[getDs9EntriesFormUrl] Couldn't retrieve JSON (" + response.getStatusText() + ") from "
                                + url);
                    }
                }

                @Override
                public void onError(final Request request, final Throwable e) {
                    Log.error("[getDs9EntriesFormUrl] Error fetching JSON data from server:" + e.getMessage());
                }
            });

        } catch (RequestException e) {
            Log.error("[getDs9EntriesFormUrl] Error fetching JSON data from server" + e.getMessage());
        }
    }


}
