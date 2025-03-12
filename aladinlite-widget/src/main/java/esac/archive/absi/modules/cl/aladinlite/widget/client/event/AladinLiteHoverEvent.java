package esac.archive.absi.modules.cl.aladinlite.widget.client.event;

import com.google.gwt.event.shared.GwtEvent;

import esac.archive.absi.modules.cl.aladinlite.widget.client.model.AladinShape;

/**
 * Event to record hover events sent by AladinLite.
 */
public class AladinLiteHoverEvent extends GwtEvent<AladinLiteHoverEventHandler> {

    public static Type<AladinLiteHoverEventHandler> TYPE = new Type<AladinLiteHoverEventHandler>();

    private AladinShape aladinLiteObject;

    public AladinLiteHoverEvent(AladinShape inputObject) {
        this.aladinLiteObject = inputObject;
    }

    @Override
    public Type<AladinLiteHoverEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AladinLiteHoverEventHandler handler) {
        handler.onHoverEvent(this);
    }

    public AladinShape getObject() {
        return this.aladinLiteObject;
    }
}
