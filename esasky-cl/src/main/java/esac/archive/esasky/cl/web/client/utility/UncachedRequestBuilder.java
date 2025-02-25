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

package esac.archive.esasky.cl.web.client.utility;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.RequestBuilder.Method;


public class UncachedRequestBuilder {
	private RequestBuilder handler;

	public UncachedRequestBuilder(Method httpMethod, String url) {
		handler = new RequestBuilder(httpMethod, url);
		handler.setHeader("If-Modified-Since", "01 Jan 1970 00:00:00 GMT");
	}

	public Request sendRequest(String requestData, RequestCallback callback) throws RequestException {
		return handler.sendRequest(requestData, callback);
	}

	public void setHeader(String header, String value) {
		handler.setHeader(header, value);
	}
}

