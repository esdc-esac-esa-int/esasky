package esac.archive.esasky.cl.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class ShowImageListEvent extends GwtEvent<ShowImageListEventHandler> {

    /** Event type. */
    public final static Type<ShowImageListEventHandler> TYPE = new Type<ShowImageListEventHandler>();

    public ShowImageListEvent() {
        super();
    }

    @Override
    public final Type<ShowImageListEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final ShowImageListEventHandler handler) {
        handler.onImageListSelected(this);
    }

}
