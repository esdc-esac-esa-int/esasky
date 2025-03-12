package esac.archive.absi.modules.cl.aladinlite.widget.client.event;

import com.google.gwt.event.shared.GwtEvent;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.ImageLayer;

public class HipsLayerChangedEvent extends GwtEvent<HipsLayerChangedEventHandler> {
    public static Type<HipsLayerChangedEventHandler> TYPE = new Type<>();
    private final ImageLayer layer;

    public HipsLayerChangedEvent(ImageLayer layer) {
        this.layer = layer;
    }

    @Override
    public Type<HipsLayerChangedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(HipsLayerChangedEventHandler handler) {
        handler.onHipsLayerChanged(this);
    }

    public ImageLayer getLayer() {
        return layer;
    }
}

