package esac.archive.absi.modules.cl.aladinlite.widget.client.event;

import com.google.gwt.event.shared.GwtEvent;

import esac.archive.absi.modules.cl.aladinlite.widget.client.model.AladinShape;

/**
 * Event to record select events sent by AladinLite.
 */
public class AladinLiteSelectEvent extends GwtEvent<AladinLiteSelectEventHandler> {

    public static Type<AladinLiteSelectEventHandler> TYPE = new Type<AladinLiteSelectEventHandler>();

    private AladinShape aladinLiteObject;

    public AladinLiteSelectEvent(AladinShape inputObject) {
        this.aladinLiteObject = inputObject;
    }

    @Override
    public Type<AladinLiteSelectEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AladinLiteSelectEventHandler handler) {
        handler.onSelectEvent(this);
    }

    public AladinShape getObject() {
        return this.aladinLiteObject;
    }
}
