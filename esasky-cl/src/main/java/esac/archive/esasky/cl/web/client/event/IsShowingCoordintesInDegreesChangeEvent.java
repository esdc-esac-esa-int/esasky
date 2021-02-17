package esac.archive.esasky.cl.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class IsShowingCoordintesInDegreesChangeEvent extends GwtEvent<IsShowingCoordintesInDegreesChangeEventHandler> {

    public final static Type<IsShowingCoordintesInDegreesChangeEventHandler> TYPE = new Type<IsShowingCoordintesInDegreesChangeEventHandler>();

    public IsShowingCoordintesInDegreesChangeEvent() {
    }

    @Override
    public final Type<IsShowingCoordintesInDegreesChangeEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final IsShowingCoordintesInDegreesChangeEventHandler handler) {
        handler.onEvent();
    }

}
