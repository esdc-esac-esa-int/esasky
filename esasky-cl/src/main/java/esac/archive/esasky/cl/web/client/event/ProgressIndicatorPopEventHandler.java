package esac.archive.esasky.cl.web.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * ProgressIndicatorPopEventHandler.
 * @author ESDC team Copyright (c) 2015- European Space Agency
 */
public interface ProgressIndicatorPopEventHandler extends EventHandler {

    /**
     * onPopEvent().
     * @param popEvent Input ProgressIndicatorPopEvent
     */
    void onPopEvent(ProgressIndicatorPopEvent popEvent);

}
