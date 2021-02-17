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
