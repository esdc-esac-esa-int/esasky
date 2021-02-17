package esac.archive.esasky.cl.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event to record closing results event tab sent by Tab components.
 */
public class UrlChangedEvent extends GwtEvent<UrlChangedEventHandler> {

    /** Event type. */
    public final static Type<UrlChangedEventHandler> TYPE = new Type<UrlChangedEventHandler>();

    public UrlChangedEvent() {
    }

    @Override
    public final Type<UrlChangedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final UrlChangedEventHandler handler) {
        handler.onUrlChanged(this);
    }

}
