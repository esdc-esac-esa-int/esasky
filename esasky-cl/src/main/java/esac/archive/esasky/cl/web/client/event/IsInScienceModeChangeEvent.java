package esac.archive.esasky.cl.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class IsInScienceModeChangeEvent extends GwtEvent<IsInScienceModeEventHandler> {

    public static Type<IsInScienceModeEventHandler> TYPE = new Type<IsInScienceModeEventHandler>();

    public IsInScienceModeChangeEvent() {
    }

    @Override
    public final Type<IsInScienceModeEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final IsInScienceModeEventHandler handler) {
        handler.onIsInScienceModeChanged();
    }

}
