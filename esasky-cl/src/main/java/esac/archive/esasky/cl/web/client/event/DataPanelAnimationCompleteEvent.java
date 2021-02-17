package esac.archive.esasky.cl.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class DataPanelAnimationCompleteEvent extends GwtEvent<DataPanelAnimationCompleteEventHandler> {

    public final static Type<DataPanelAnimationCompleteEventHandler> TYPE = new Type<DataPanelAnimationCompleteEventHandler>();

    public DataPanelAnimationCompleteEvent() {
    }

    @Override
    public final Type<DataPanelAnimationCompleteEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final DataPanelAnimationCompleteEventHandler handler) {
        handler.onDataPanelAnimationComplete(this);
    }
}
