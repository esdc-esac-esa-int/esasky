package esac.archive.absi.modules.cl.aladinlite.widget.client.event;

import com.google.gwt.event.shared.GwtEvent;

import esac.archive.absi.modules.cl.aladinlite.widget.client.model.AladinShape;

/**
 * Event to record select events sent by AladinLite.
 */
public class AladinLiteShapeHoverStartEvent extends
        GwtEvent<AladinLiteShapeHoverStartEventHandler> {

    public static Type<AladinLiteShapeHoverStartEventHandler> TYPE = new Type<AladinLiteShapeHoverStartEventHandler>();

    private int shapeId;
    private String overlayName;
    private AladinShape shapeobj;

    public AladinLiteShapeHoverStartEvent(int shapeId, String overlayName, AladinShape shapeobj) {
        this.shapeId = shapeId;
        this.overlayName = overlayName;
        this.shapeobj = shapeobj;
    }

    @Override
    public Type<AladinLiteShapeHoverStartEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AladinLiteShapeHoverStartEventHandler handler) {
        handler.onShapeHoverStartEvent(this);
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
