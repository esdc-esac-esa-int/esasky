package esac.archive.esasky.cl.web.client.callback;

import com.google.gwt.http.client.Response;
import esac.archive.esasky.cl.web.client.event.CountProgressIndicatorPopEvent;
import esac.archive.esasky.cl.web.client.event.CountProgressIndicatorPushEvent;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPopEvent;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPushEvent;

public class CountRequestCallback extends JsonRequestCallback {
    
    public CountRequestCallback(String progressIndicatorMessage, String googleAnalyticsErrorMessage) {
        super(progressIndicatorMessage, googleAnalyticsErrorMessage);
    }

    @Override
    protected void onSuccess(final Response response) {
    }

    @Override
    protected ProgressIndicatorPushEvent createPushEvent(String id, String progressIndicatorMessage, String googleAnalyticsErrorMessage) {
        return new CountProgressIndicatorPushEvent(id, progressIndicatorMessage, googleAnalyticsErrorMessage);
    }

    @Override
    protected ProgressIndicatorPopEvent createPopEvent(String id) {
        return new CountProgressIndicatorPopEvent(id);
    }
}