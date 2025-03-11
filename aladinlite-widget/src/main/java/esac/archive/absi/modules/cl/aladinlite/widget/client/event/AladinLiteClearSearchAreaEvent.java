package esac.archive.absi.modules.cl.aladinlite.widget.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class AladinLiteClearSearchAreaEvent extends GwtEvent<AladinLiteClearSearchAreaEventHandler> {

    public static Type<AladinLiteClearSearchAreaEventHandler> TYPE = new Type<>();

    @Override
    public Type<AladinLiteClearSearchAreaEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AladinLiteClearSearchAreaEventHandler handler) {
        handler.onAreaSelectionCleared();
    }
}
