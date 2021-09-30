package esac.archive.esasky.cl.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class GridToggledEvent extends GwtEvent<GridToggledEventHandler> {

    public final static Type<GridToggledEventHandler> TYPE = new Type<GridToggledEventHandler>();

	private boolean isGridActice;
    
    public GridToggledEvent(boolean isGridActive) {
    	this.isGridActice = isGridActive;
    }

    @Override
    public final Type<GridToggledEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final GridToggledEventHandler handler) {
        handler.onEvent(this);
    }
    
    public boolean isGridActive() {
    	return isGridActice;
    }

}
