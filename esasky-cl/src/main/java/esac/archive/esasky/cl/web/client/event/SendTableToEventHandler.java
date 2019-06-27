package esac.archive.esasky.cl.web.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handler interface for sending results table to any third-Party application, e.g. SAMP
 * @author María H. Sarmiento Copyright (c) 2016- European Space Agency
 */
public interface SendTableToEventHandler extends EventHandler {

    /**
     * onSendTableClick().
     * @param clickEvent Input SentTableToEvent.
     */
    void onSendTableClick(SendTableToEvent clickEvent);
}
