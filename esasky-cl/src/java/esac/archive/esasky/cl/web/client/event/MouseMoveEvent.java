package esac.archive.esasky.cl.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event to record closing results event tab sent by Tab components.
 */
public class MouseMoveEvent extends GwtEvent<MouseMoveEventHandler> {

    /** Event type. */
    public static Type<MouseMoveEventHandler> TYPE = new Type<MouseMoveEventHandler>();

    private double raDeg;

    private double decDeg;

    private int mouseX;

    private int mouseY;

    public MouseMoveEvent(final double raDeg, final double decDeg) {
        this.raDeg = raDeg;
        this.decDeg = decDeg;
    }

    public MouseMoveEvent(final double raDeg, final double decDeg, final int mouseX,
            final int mouseY) {
        this.raDeg = raDeg;
        this.decDeg = decDeg;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }

    @Override
    public final Type<MouseMoveEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final MouseMoveEventHandler handler) {
        handler.onMouseMoveEvent(this);
    }

    public double getRaDeg() {
        return raDeg;
    }

    public double getDecDeg() {
        return decDeg;
    }

    public double getMouseX() {
        return mouseX;
    }

    public double getMouseY() {
        return mouseY;
    }

}
