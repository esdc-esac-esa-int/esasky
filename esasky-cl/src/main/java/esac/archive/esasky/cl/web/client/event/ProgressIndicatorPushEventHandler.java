package esac.archive.esasky.cl.web.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * ProgressIndicatorPushEventHandler.
 * @author ESDC team Copyright (c) 2015- European Space Agency
 */
public interface ProgressIndicatorPushEventHandler extends EventHandler {

    /**
     * onPushEvent().
     * @param pushEvent Input ProgressIndicatorPushEvent
     */
    void onPushEvent(ProgressIndicatorPushEvent pushEvent);

}
