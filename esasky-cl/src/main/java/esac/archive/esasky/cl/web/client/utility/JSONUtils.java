package esac.archive.esasky.cl.web.client.utility;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;

/**
 * @author ESDC team Copyright (c) 2015- European Space Agency
 */
public class JSONUtils {
    
    private static final int DEFAULT_JSON_TIMEOUT = 25000;

    public interface IJSONRequestCallback {
        void onSuccess(final String responseText);
        default void onError(final String errorCause) {}
        default void onError(final int statusCode, final String errorCause) {
            onError(errorCause);
        }
        default void whenComplete(){}
    }
    
    /** Prevents Utility class calls. */
    protected JSONUtils() {
        // prevents calls from subclass
        throw new UnsupportedOperationException();
    }

    /**
     * EvalJsonGetData().
     * @param json Input String
     * @return JsArray<JavaScriptObject>
     */
    public static final native JsArray<JavaScriptObject> evalJsonGetData(final String json) /*-{
		return eval(json).data;
    }-*/;

    /** Request a JSON String from url passed as parameter. */
    public static void getJSONFromUrl(final String url, final IJSONRequestCallback callback) {
        getJSONFromUrl(url, callback, DEFAULT_JSON_TIMEOUT, false);
    }

    public static void getJSONFromUrl(final String url, final IJSONRequestCallback callback, boolean handleServerError) {
        getJSONFromUrl(url, callback, DEFAULT_JSON_TIMEOUT, handleServerError);
    }

    public static void getJSONFromUrl(final String url, final IJSONRequestCallback callback, int timeoutMillis) {
        getJSONFromUrl(url, callback, timeoutMillis, false);
    }

    public static void getJSONFromUrl(final String url, final IJSONRequestCallback callback, int timeoutMillis, boolean handleServerError) {
        
        Log.debug("[getJSONFromUrl] Query [" + url + "]");
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
        builder.setTimeoutMillis(timeoutMillis);
        
        try {
            builder.sendRequest(null, new RequestCallback() {

                @Override
                public void onResponseReceived(final Request request, final Response response) {
                    if (200 == response.getStatusCode()) {
                        callback.onSuccess(response.getText());
                    } else {
                        Log.error("[getJSONFromUrl] Couldn't retrieve JSON ("
                                + response.getStatusText() + ") from " + url);

                        if (handleServerError) {
                            callback.onError(response.getStatusCode(), response.getText());
                        } else {
                            callback.onError(response.getStatusCode(), "Couldn't retrieve JSON: " + response.getStatusText());
                        }


                    }

                    callback.whenComplete();
                }

                @Override
                public void onError(final Request request, final Throwable e) {
                    Log.error(e.getMessage());
                    Log.error("[getJSONFromUrl] Error fetching JSON data from server");
                    callback.onError(e.getMessage());
                    callback.whenComplete();
                }
            });
            
        } catch (RequestException e) {
            Log.error(e.getMessage());
            Log.error("[getJSONFromUrl] Error fetching JSON data from server");
            callback.onError(e.getMessage());
            callback.whenComplete();
        }
    }
    
    public static void getJSONFromUrl(final String url, final RequestCallback callback) {
    	getJSONFromUrl(url, callback, false);
    }
    
    public static void getJSONFromUrl(final String url, final RequestCallback callback, boolean includeCredentials) {
        Log.debug("[getJSONFromUrl] Query [" + url + "]");
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
        builder.setIncludeCredentials(includeCredentials);
        try {
            builder.sendRequest(null, callback);
        } catch (RequestException e) {
            Log.error(e.getMessage());
            Log.error("[getJSONFromUrl] Error fetching JSON data from server");
        }
    }
}
