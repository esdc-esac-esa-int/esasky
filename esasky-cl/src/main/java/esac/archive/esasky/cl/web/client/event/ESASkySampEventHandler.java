package esac.archive.esasky.cl.web.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handler interface to distribute files via SAMP (copy from Gaia project).
 *
 * @author María H Sarmiento - Copyright (c) 2015 - ESA/ESAC.
 */
public interface ESASkySampEventHandler extends EventHandler {

    /**
     * onEvent().
     * @param event Input SampEvent.
     */
    void onEvent(ESASkySampEvent event);
}
