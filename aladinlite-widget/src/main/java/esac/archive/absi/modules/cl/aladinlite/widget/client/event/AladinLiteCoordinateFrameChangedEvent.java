package esac.archive.absi.modules.cl.aladinlite.widget.client.event;

import com.google.gwt.event.shared.GwtEvent;

import esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteConstants.CoordinateFrame;

public class AladinLiteCoordinateFrameChangedEvent extends
        GwtEvent<AladinLiteCoordinateFrameChangedEventHandler> {

    public static Type<AladinLiteCoordinateFrameChangedEventHandler> TYPE = new Type<AladinLiteCoordinateFrameChangedEventHandler>();

    private CoordinateFrame coordinateFrame;

    public AladinLiteCoordinateFrameChangedEvent(CoordinateFrame frame) {
    	this.coordinateFrame = frame;
    }

    @Override
    public Type<AladinLiteCoordinateFrameChangedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AladinLiteCoordinateFrameChangedEventHandler handler) {
        handler.onFrameChanged(this);
    }

    public CoordinateFrame getCoordinateFrame() {
    	return this.coordinateFrame;
    }
}
