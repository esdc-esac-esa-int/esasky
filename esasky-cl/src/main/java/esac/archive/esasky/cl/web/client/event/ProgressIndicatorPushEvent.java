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

package esac.archive.esasky.cl.web.client.event;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Event to record closing results event tab sent by Tab components.
 * @author ESDC team Copyright (c) 2015- European Space Agency
 */
public class ProgressIndicatorPushEvent extends GwtEvent<ProgressIndicatorPushEventHandler> {

    /** message id. */
    private String id;
    /** message to display. */
    private String message;
    
    private boolean isImportant;
    
    private String googleAnalyticsErrorMessage;

    /** Event type. */
    public final static Type<ProgressIndicatorPushEventHandler> TYPE = new Type<ProgressIndicatorPushEventHandler>();

    public ProgressIndicatorPushEvent(final String inputId, final String inputMessage) {
    	this(inputId, inputMessage, "", false);
    }
    
    public ProgressIndicatorPushEvent(final String inputId, final String inputMessage, boolean isImportant) {
    	this(inputId, inputMessage, "", isImportant);
    }
    
    public ProgressIndicatorPushEvent(final String inputId, final String inputMessage, final String googleAnalyticsErrorMessage) {
    	this(inputId, inputMessage, googleAnalyticsErrorMessage, false);
    }
    
    public ProgressIndicatorPushEvent(final String inputId, final String inputMessage, final String googleAnalyticsErrorMessage, boolean isImportant) {
        Log.debug("[ProgressIndicatorPushEvent] Event received!!! [Id]: " + inputId
                + " [inputMessage]: " + inputMessage);
        this.id = inputId;
        this.message = inputMessage;
        this.isImportant = isImportant;
        this.googleAnalyticsErrorMessage = googleAnalyticsErrorMessage;
    }

    @Override
    public final Type<ProgressIndicatorPushEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final ProgressIndicatorPushEventHandler handler) {
        handler.onPushEvent(this);
    }

    public final String getId() {
        return this.id;
    }

    public final String getMessage() {
        return this.message;
    }
    
    public final boolean isImportant() {
    	return this.isImportant;
    }
    
    public final String getGoogleAnalyticsErrorMessage() {
    	return this.googleAnalyticsErrorMessage;
    }
}
