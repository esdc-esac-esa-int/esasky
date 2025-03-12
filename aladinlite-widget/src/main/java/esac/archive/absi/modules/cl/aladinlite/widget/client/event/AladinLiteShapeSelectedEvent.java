package esac.archive.absi.modules.cl.aladinlite.widget.client.event;

import com.google.gwt.event.shared.GwtEvent;

import esac.archive.absi.modules.cl.aladinlite.widget.client.model.AladinShape;

/**
 * Event to record select events sent by AladinLite.
 */
public class AladinLiteShapeSelectedEvent extends GwtEvent<AladinLiteShapeSelectedEventHandler> {

    public static Type<AladinLiteShapeSelectedEventHandler> TYPE = new Type<AladinLiteShapeSelectedEventHandler>();

    private int shapeId;
    private String overlayName;
    private AladinShape shapeobj;

    public AladinLiteShapeSelectedEvent(int shapeId, String overlayName, AladinShape shapeobj) {
        this.shapeId = shapeId;
        this.overlayName = overlayName;
        this.shapeobj = shapeobj;
    }

    @Override
    public Type<AladinLiteShapeSelectedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AladinLiteShapeSelectedEventHandler handler) {
        handler.onShapeSelectionEvent(this);
    }

    public int getShapeId() {
        return shapeId;
    }

    public String getOverlayName() {
        return overlayName;
    }

    public AladinShape getShape() {
        return shapeobj;
    }

}
