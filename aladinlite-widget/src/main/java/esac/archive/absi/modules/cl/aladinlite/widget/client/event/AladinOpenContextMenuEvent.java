package esac.archive.absi.modules.cl.aladinlite.widget.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.Event;

public class AladinOpenContextMenuEvent extends GwtEvent<AladinOpenContextMenuEventHandler> {
    public static Type<AladinOpenContextMenuEventHandler> TYPE = new Type<>();

    final private Event event;

    public AladinOpenContextMenuEvent(Event event) {
        this.event = event;
    }

    @Override
    public Type<AladinOpenContextMenuEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AladinOpenContextMenuEventHandler handler) {
        handler.onOpenContextMenu(this);
    }

    public Event getEvent() {
        return event;
    }
}
