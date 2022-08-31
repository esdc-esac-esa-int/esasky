package esac.archive.esasky.cl.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class ShowImageListEvent extends GwtEvent<ShowImageListEventHandler> {

    /** Event type. */
    public static final Type<ShowImageListEventHandler> TYPE = new Type<>();

    public final Sender sender;

    public ShowImageListEvent(Sender sender) {
        super();
        this.sender = sender;
    }

    @Override
    public final Type<ShowImageListEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final ShowImageListEventHandler handler) {
        handler.onImageListSelected(this);
    }

    public enum Sender {
        HST,
        JWST
    }

}
