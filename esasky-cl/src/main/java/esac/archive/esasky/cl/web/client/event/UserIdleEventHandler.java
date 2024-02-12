package esac.archive.esasky.cl.web.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface UserIdleEventHandler extends EventHandler {
    void onIdleStatusChanged(UserIdleEvent event);
}
