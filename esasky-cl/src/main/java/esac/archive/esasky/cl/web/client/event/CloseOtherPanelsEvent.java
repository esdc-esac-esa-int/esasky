package esac.archive.esasky.cl.web.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Widget;

public class CloseOtherPanelsEvent extends GwtEvent<CloseOtherPanelsEventHandler> {

    /** Event type. */
    public final static Type<CloseOtherPanelsEventHandler> TYPE = new Type<CloseOtherPanelsEventHandler>();

    private Widget widgetNotToClose;

    public CloseOtherPanelsEvent(Widget widgetNotToClose) {
        super();
        this.widgetNotToClose = widgetNotToClose;
    }

    @Override
    public final Type<CloseOtherPanelsEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final CloseOtherPanelsEventHandler handler) {
        handler.onCloseEvent(this);
    }

    public Widget getWidgetNotToClose() {
        return widgetNotToClose;
    }

}
