package esac.archive.esasky.cl.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class ExtTapToggleEvent extends GwtEvent<ExtTapToggleEventHandler> {

    public final static Type<ExtTapToggleEventHandler> TYPE = new Type<ExtTapToggleEventHandler>();
    boolean isOpen;
    

    public ExtTapToggleEvent(boolean isOpen) {
    	this.isOpen = isOpen;
    }

    @Override
    public final Type<ExtTapToggleEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final ExtTapToggleEventHandler handler) {
        handler.onToggle(this);
    }
    
    public boolean isOpen() {
    	return isOpen;
    }
}