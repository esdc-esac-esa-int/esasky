package esac.archive.esasky.cl.gsamp.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handler interface for SAMP events.
 * @author ileon
 */
public interface SampEventHandler extends EventHandler {

    /**
     * onSampEvent().
     * @param sampEvent Input SampEvent.
     */
    void onSampEvent(SampEvent sampEvent);
}
