package esac.archive.esasky.cl.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class ExtTapFovEvent extends GwtEvent<ExtTapFovEventHandler> {

    public static Type<ExtTapFovEventHandler> TYPE = new Type<ExtTapFovEventHandler>();

    private double fov;
    
    public ExtTapFovEvent(double fov) {
    	this.fov = fov;
    }

    @Override
    public final Type<ExtTapFovEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final ExtTapFovEventHandler handler) {
        handler.onFovChanged(this);
    }

	public double getFov() {
		return fov;
	}

}