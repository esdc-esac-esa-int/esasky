package esac.archive.esasky.cl.web.client.event;

import com.google.gwt.event.shared.GwtEvent;


public class OpenSeaDragonActiveEvent extends GwtEvent<OpenSeaDragonActiveEventHandler> {

	private boolean isActive;
    public static final Type<OpenSeaDragonActiveEventHandler> TYPE = new Type<>();

    public OpenSeaDragonActiveEvent(boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public final Type<OpenSeaDragonActiveEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final OpenSeaDragonActiveEventHandler handler) {
        handler.onEvent(this);
    }

    public final boolean isActive() {
        return this.isActive;
    }
}
