package esac.archive.esasky.cl.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class PublicationsClickEvent extends GwtEvent<PublicationsClickEventHandler> {

    public static Type<PublicationsClickEventHandler> TYPE = new Type<PublicationsClickEventHandler>();

    private boolean toggleStatus;

    public PublicationsClickEvent(boolean toggleStatus) {
        super();
        this.toggleStatus = toggleStatus;
    }

    @Override
    public final Type<PublicationsClickEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final PublicationsClickEventHandler handler) {
        handler.onClick(this);
    }
    
    public boolean isToggleStatusON() {
        return toggleStatus;
    }
}
