package esac.archive.esasky.cl.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class TileLoadedEvent extends GwtEvent<TileLoadedEventHandler> {

    public static final Type<TileLoadedEventHandler> TYPE = new Type<>();
    private final boolean success;
    private final String message;

    public TileLoadedEvent(boolean success) {
        this(success, "");
    }

    public TileLoadedEvent(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    @Override
    public Type<TileLoadedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(TileLoadedEventHandler handler) {
        handler.onTileLoaded(this);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}