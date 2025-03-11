package esac.archive.absi.modules.cl.aladinlite.widget.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class AladinLiteHasLoadedEvent extends
        GwtEvent<AladinLiteHasLoadedEventHandler> {

    public static Type<AladinLiteHasLoadedEventHandler> TYPE = new Type<AladinLiteHasLoadedEventHandler>();

    public AladinLiteHasLoadedEvent() {}

    @Override
    public Type<AladinLiteHasLoadedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AladinLiteHasLoadedEventHandler handler) {
        handler.onLoad(this);
    }
}
