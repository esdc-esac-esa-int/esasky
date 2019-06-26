package esac.archive.esasky.cl.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class IsTrackingSSOEvent extends GwtEvent<IsTrackingSSOEventHandler> {

    public static Type<IsTrackingSSOEventHandler> TYPE = new Type<IsTrackingSSOEventHandler>();

    public IsTrackingSSOEvent() {
    }

    @Override
    public final Type<IsTrackingSSOEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final IsTrackingSSOEventHandler handler) {
        handler.onIsTrackingSSOEventChanged();
    }

}
