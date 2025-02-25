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

package esac.archive.esasky.cl.web.client.callback;

import com.google.gwt.http.client.Response;
import esac.archive.esasky.cl.web.client.event.CountProgressIndicatorPopEvent;
import esac.archive.esasky.cl.web.client.event.CountProgressIndicatorPushEvent;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPopEvent;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPushEvent;

public class CountRequestCallback extends JsonRequestCallback {
    
	public interface IOnSuccess {
	    void onSucess(Response response);
	}
	
	private IOnSuccess onSucess;
    
    public CountRequestCallback(String progressIndicatorMessage, String googleAnalyticsErrorMessage, IOnSuccess onSucess) {
        super(progressIndicatorMessage, googleAnalyticsErrorMessage);
        this.onSucess = onSucess;
    }

    @Override
    protected void onSuccess(final Response response) {
    	this.onSucess.onSucess(response);
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