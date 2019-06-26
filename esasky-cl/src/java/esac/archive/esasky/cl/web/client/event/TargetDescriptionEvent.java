package esac.archive.esasky.cl.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class TargetDescriptionEvent extends GwtEvent<TargetDescriptionEventHandler> {

    /** Event type. */
    public static Type<TargetDescriptionEventHandler> TYPE = new Type<TargetDescriptionEventHandler>();

    private String targetName;
    private String targetDescription;

    public TargetDescriptionEvent(final String targetName, final String targetDescription) {
        this.targetName = targetName;
        this.targetDescription = targetDescription;
    }

    @Override
    public final Type<TargetDescriptionEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final TargetDescriptionEventHandler handler) {
        handler.onEvent(this);
    }

    public final String getTargetName() {
        return this.targetName;
    }
    
    public final String getTargetDescription() {
    	return this.targetDescription;
    }
}
