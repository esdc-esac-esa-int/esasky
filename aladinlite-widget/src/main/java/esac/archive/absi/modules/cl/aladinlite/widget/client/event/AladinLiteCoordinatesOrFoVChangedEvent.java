package esac.archive.absi.modules.cl.aladinlite.widget.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event to record click events sent by AladinLite.
 */
public class AladinLiteCoordinatesOrFoVChangedEvent extends
GwtEvent<AladinLiteCoordinatesOrFoVChangedEventHandler> {

    public static Type<AladinLiteCoordinatesOrFoVChangedEventHandler> TYPE = new Type<AladinLiteCoordinatesOrFoVChangedEventHandler>();

    public AladinLiteCoordinatesOrFoVChangedEvent() {
    }

    @Override
    public Type<AladinLiteCoordinatesOrFoVChangedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AladinLiteCoordinatesOrFoVChangedEventHandler handler) {
        handler.onChangeEvent(this);
    }
}
