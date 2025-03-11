	package esac.archive.absi.modules.cl.aladinlite.widget.client.event;

import com.google.gwt.event.shared.GwtEvent;

import esac.archive.absi.modules.cl.aladinlite.widget.client.model.AladinShape;

/**
 * Event to record select events sent by AladinLite.
 */
public class AladinLiteShapeHoverStopEvent extends
        GwtEvent<AladinLiteShapeHoverStopEventHandler> {

    public static Type<AladinLiteShapeHoverStopEventHandler> TYPE = new Type<AladinLiteShapeHoverStopEventHandler>();

    private int shapeId;
    private String overlayName;
    private AladinShape shapeobj;

    public AladinLiteShapeHoverStopEvent(int shapeId, String overlayName, AladinShape shapeobj) {
        this.shapeId = shapeId;
        this.overlayName = overlayName;
        this.shapeobj = shapeobj;
    }

    @Override
    public Type<AladinLiteShapeHoverStopEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AladinLiteShapeHoverStopEventHandler handler) {
        handler.onShapeHoverStopEvent(this);
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
