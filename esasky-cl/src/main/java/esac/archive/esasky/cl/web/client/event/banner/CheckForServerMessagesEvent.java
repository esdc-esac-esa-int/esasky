package esac.archive.esasky.cl.web.client.event.banner;

import com.google.gwt.event.shared.GwtEvent;

public class CheckForServerMessagesEvent extends GwtEvent<CheckForServerMessagesEventHandler> {

    public final static Type<CheckForServerMessagesEventHandler> TYPE = new Type<CheckForServerMessagesEventHandler>();

    public CheckForServerMessagesEvent() {
    }

    @Override
    public final Type<CheckForServerMessagesEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final CheckForServerMessagesEventHandler handler) {
        handler.onEvent(this);
    }

}
