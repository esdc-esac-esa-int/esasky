package esac.archive.esasky.cl.web.client.event.hips;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event to record closing results event tab sent by Tab components.
 */
public class HipsOpacCngEvent extends GwtEvent<HipsOpacCngEventHandler> {

    /** event type. */
    public final static Type<HipsOpacCngEventHandler> TYPE = new Type<HipsOpacCngEventHandler>();

    /** opacity. */
    private double opacity;

    /**
     * constructor class.
     * @param inputOpacity Input double value
     */
    public HipsOpacCngEvent(final double inputOpacity) {
        this.opacity = inputOpacity;
    }

    /**
     * getOpacity().
     * @return double value
     */
    public final double getOpacity() {
        return opacity;
    }

    @Override
    public final Type<HipsOpacCngEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final HipsOpacCngEventHandler handler) {
        handler.onChangeEvent(this);
    }
}
