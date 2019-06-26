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

