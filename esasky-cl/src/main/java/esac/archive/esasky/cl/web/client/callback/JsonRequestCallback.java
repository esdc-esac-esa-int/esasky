/*
ESASky
Copyright (C) 2025 European Space Agency

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

package esac.archive.esasky.cl.web.client.callback;

import java.util.UUID;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;

import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPopEvent;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPushEvent;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;

public abstract class JsonRequestCallback implements RequestCallback {

    private String progressIndicatorId;
    private String googleAnalyticsErrorMessage = "";
    private String responseText = "";
    
    public JsonRequestCallback(String progressIndicatorMessage, String googleAnalyticsErrorMessage) {
        this.progressIndicatorId = UUID.randomUUID().toString();
        this.googleAnalyticsErrorMessage = googleAnalyticsErrorMessage;
        addProgressIndicator(progressIndicatorMessage, googleAnalyticsErrorMessage);
    }

    @Override
    public void onResponseReceived(final Request request, final Response response) {
    	this.responseText = response.getText();
        if (200 == response.getStatusCode()) {
        	try {
        		onSuccess(response);
        	} catch (Exception e) {
        		onError(request, e);
        	}
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
        Log.error(this.getClass().getSimpleName() + " Error fetching JSON data from server. " + " Response text = " + responseText + " GA error message = " + googleAnalyticsErrorMessage);
        GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_REQUESTERROR, this.getClass().getSimpleName(), exception.getMessage() + " Error fetching JSON data from server. " + " Response text = " + responseText + " GA error message = " + googleAnalyticsErrorMessage);
        removeProgressIndicator();
    }

    private void addProgressIndicator(String progressIndicatorMessage, String googleAnalyticsErrorMessage) {
        ProgressIndicatorPushEvent event = createPushEvent(progressIndicatorId, progressIndicatorMessage, googleAnalyticsErrorMessage);
        CommonEventBus.getEventBus().fireEvent(event);
    }

    private void removeProgressIndicator() {
        ProgressIndicatorPopEvent event = createPopEvent(progressIndicatorId);
        CommonEventBus.getEventBus().fireEvent(event);
    }

    protected ProgressIndicatorPushEvent createPushEvent(String id, String progressIndicatorMessage, String googleAnalyticsErrorMessage) {
        return new ProgressIndicatorPushEvent(id, progressIndicatorMessage, googleAnalyticsErrorMessage);
    }

    protected ProgressIndicatorPopEvent createPopEvent(String id) {
        return new ProgressIndicatorPopEvent(id);
    }
}