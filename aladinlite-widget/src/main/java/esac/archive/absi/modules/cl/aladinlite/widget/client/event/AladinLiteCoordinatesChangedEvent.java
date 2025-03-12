package esac.archive.absi.modules.cl.aladinlite.widget.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class AladinLiteCoordinatesChangedEvent extends
        GwtEvent<AladinLiteCoordinatesChangedEventHandler> {

    public static Type<AladinLiteCoordinatesChangedEventHandler> TYPE = new Type<AladinLiteCoordinatesChangedEventHandler>();

    private double ra;
    private double dec;
    private boolean isViewCenterPosition;

    public AladinLiteCoordinatesChangedEvent(double ra, double dec, boolean isViewCenterPosition) {
    	this.ra = ra;
    	this.dec = dec;
    	this.isViewCenterPosition = isViewCenterPosition;
    }

    @Override
    public Type<AladinLiteCoordinatesChangedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AladinLiteCoordinatesChangedEventHandler handler) {
        handler.onCoordsChanged(this);
    }

    public double getRa() {
        return this.ra;
    }
    
    public double getDec() {
    	return this.dec;
    }
    
    public boolean getIsViewCenterPosition() {
    	return this.isViewCenterPosition;
    }
}
