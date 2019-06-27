package esac.archive.esasky.cl.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class DataPanelResizeEvent extends GwtEvent<DataPanelResizeEventHandler> {

    public static Type<DataPanelResizeEventHandler> TYPE = new Type<DataPanelResizeEventHandler>();

    private final int height;
    
    public DataPanelResizeEvent(int height) {
    	this.height = height;
    }

    @Override
    public final Type<DataPanelResizeEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final DataPanelResizeEventHandler handler) {
        handler.onDataPanelResize(this);
    }
    
    public int getNewHeight() {
    	return height;
    }

}
