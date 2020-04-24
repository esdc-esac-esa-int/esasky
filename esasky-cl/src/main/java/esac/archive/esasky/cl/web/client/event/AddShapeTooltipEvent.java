package esac.archive.esasky.cl.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

import esac.archive.esasky.cl.web.client.view.allskypanel.Tooltip;

public class AddShapeTooltipEvent extends GwtEvent<AddShapeTooltipEventHandler> {

    public static Type<AddShapeTooltipEventHandler> TYPE = new Type<AddShapeTooltipEventHandler>();

    private Tooltip tooltip;

    public AddShapeTooltipEvent(Tooltip tooltip) {
        this.tooltip = tooltip;
    }

    @Override
    public final Type<AddShapeTooltipEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final AddShapeTooltipEventHandler handler) {
        handler.onEvent(this);
    }

    public Tooltip getTooltip() {
        return tooltip;
    }

}
