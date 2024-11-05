package esac.archive.esasky.cl.web.client.event;

import com.google.gwt.event.shared.GwtEvent;


public class CloseImageListEvent extends GwtEvent<CloseImageListEventHandler> {

    /** Event type. */
    public static final Type<CloseImageListEventHandler> TYPE = new Type<>();

    public final Sender sender;

    public CloseImageListEvent(Sender sender) {
        super();
        this.sender = sender;
    }

    @Override
    public final Type<CloseImageListEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final CloseImageListEventHandler handler) {
        handler.onImageListClose(this);
    }

    public enum Sender {
        HST,
        JWST,
        EUCLID,
        ALL
    }

}