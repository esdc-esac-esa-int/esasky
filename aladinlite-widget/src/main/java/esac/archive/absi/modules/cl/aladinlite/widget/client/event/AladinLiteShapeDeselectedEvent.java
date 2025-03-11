package esac.archive.absi.modules.cl.aladinlite.widget.client.event;

import com.google.gwt.event.shared.GwtEvent;

import esac.archive.absi.modules.cl.aladinlite.widget.client.model.AladinShape;

/**
 * Event to record select events sent by AladinLite.
 */
public class AladinLiteShapeDeselectedEvent extends GwtEvent<AladinLiteShapeDeselectedEventHandler> {

    public static Type<AladinLiteShapeDeselectedEventHandler> TYPE = new Type<AladinLiteShapeDeselectedEventHandler>();

    private int shapeId;
    private String overlayName;
    private AladinShape shapeobj;

    public AladinLiteShapeDeselectedEvent(int shapeId, String overlayName, AladinShape shapeobj) {
        this.shapeId = shapeId;
        this.overlayName = overlayName;
        this.shapeobj = shapeobj;
    }

    @Override
    public Type<AladinLiteShapeDeselectedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AladinLiteShapeDeselectedEventHandler handler) {
        handler.onShapeDeselectionEvent(this);
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
