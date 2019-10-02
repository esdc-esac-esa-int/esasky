package esac.archive.esasky.cl.web.client.event.hips;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event to record closing results event tab sent by Tab components.
 */
public class HipsNameChangeEvent extends GwtEvent<HipsNameChangeEventHandler> {

    /** Event type. */
    public static Type<HipsNameChangeEventHandler> TYPE = new Type<HipsNameChangeEventHandler>();

    private String hipsName;

    public HipsNameChangeEvent(final String hipsName) {
        this.hipsName = hipsName;
    }

    public final String getHiPSName() {
        return hipsName;
    }
    

    @Override
    public final Type<HipsNameChangeEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final HipsNameChangeEventHandler handler) {
        handler.onChangeEvent(this);
    }
}
