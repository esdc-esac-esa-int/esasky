package esac.archive.esasky.cl.web.client.callback;

import java.util.UUID;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;

import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPopEvent;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPushEvent;

public abstract class JsonRequestCallback implements RequestCallback {

    private String progressIndicatorId;
    
    public JsonRequestCallback(String progressIndicatorMessage, String googleAnalyticsErrorMessage) {
        this.progressIndicatorId = UUID.randomUUID().toString();
        addProgressIndicator(progressIndicatorMessage, googleAnalyticsErrorMessage);
    }

    @Override
    public void onResponseReceived(final Request request, final Response response) {
        if (200 == response.getStatusCode()) {
			onSuccess(response);
        } else {
            onError(request, new Exception(response.getStatusCode() + " ("
                    + response.getStatusText() + ")"));
        }

        removeProgressIndicator();
    }

    protected abstract void onSuccess(final Response response);

    @Override
    public void onError(final Request request, final Throwable exception) {
        Log.error(exception.getMessage());
        Log.error(this.getClass().getSimpleName() + " Error fetching JSON data from server");
        
        removeProgressIndicator();
    }

    private void addProgressIndicator(String progressIndicatorMessage, String googleAnalyticsErrorMessage) {
        CommonEventBus.getEventBus().fireEvent(
                new ProgressIndicatorPushEvent(this.progressIndicatorId, progressIndicatorMessage, googleAnalyticsErrorMessage));
    }

    private void removeProgressIndicator() {
        CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPopEvent(progressIndicatorId));
    }
}