package esac.archive.absi.modules.cl.aladinlite.widget.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class AladinLiteFoVChangedEvent extends GwtEvent<AladinLiteFoVChangedEventHandler> {

    public static Type<AladinLiteFoVChangedEventHandler> TYPE = new Type<AladinLiteFoVChangedEventHandler>();

    private double fov;
    private double fovDec;

    public AladinLiteFoVChangedEvent(double fovRa, double fovDec) {
        this.fov = fovRa;
        this.fovDec = fovDec;
    }

    @Override
    public Type<AladinLiteFoVChangedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AladinLiteFoVChangedEventHandler handler) {
        handler.onChangeEvent(this);
    }

    public double getFov() {
        return this.fov;
    }
    
    public double getFovDec() {
    	return this.fovDec;
    }
}
