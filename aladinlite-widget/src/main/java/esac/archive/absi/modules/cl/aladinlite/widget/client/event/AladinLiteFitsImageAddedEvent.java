package esac.archive.absi.modules.cl.aladinlite.widget.client.event;

import com.google.gwt.event.shared.GwtEvent;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.ImageLayer;

public class AladinLiteFitsImageAddedEvent extends GwtEvent<AladinLiteFitsImageAddedEventHandler> {

    public static Type<AladinLiteFitsImageAddedEventHandler> TYPE = new Type<>();
    private final ImageLayer imageLayer;

    public AladinLiteFitsImageAddedEvent(ImageLayer imageLayer) {
        this.imageLayer = imageLayer;

    }

    @Override
    public Type<AladinLiteFitsImageAddedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AladinLiteFitsImageAddedEventHandler handler) {
        handler.onFitsImageAdded(this);
    }

    public ImageLayer getImageLayer() {
        return imageLayer;
    }
}
