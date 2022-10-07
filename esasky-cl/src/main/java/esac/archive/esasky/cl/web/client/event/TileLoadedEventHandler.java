package esac.archive.esasky.cl.web.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface TileLoadedEventHandler extends EventHandler {
    void onTileLoaded(TileLoadedEvent event);
}
