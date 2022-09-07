package esac.archive.esasky.cl.web.client.event;

import com.google.gwt.event.shared.GwtEvent;
import esac.archive.esasky.cl.web.client.model.entities.ImageListEntity;

public class ImageListSelectedEvent extends GwtEvent<ImageListSelectedEventHandler>{
    public static final Type<ImageListSelectedEventHandler> TYPE = new Type<>();

    private final ImageListEntity selectedEntity;

    public ImageListSelectedEvent(ImageListEntity selectedEntity) {
        this.selectedEntity = selectedEntity;
    }

    public ImageListEntity getSelectedEntity() { return selectedEntity; }

    @Override
    public Type<ImageListSelectedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ImageListSelectedEventHandler handler) {
        handler.onSelected(this);
    }
}
