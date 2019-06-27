package esac.archive.esasky.cl.web.client.event.hips;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author ESDC team Copyright (c) 2015- European Space Agency
 */
public interface HipsChangeEventHandler extends EventHandler {

    /**
     * onChangeEvent().
     * @param changeEvent Input HipsChangeEvent.
     */
    void onChangeEvent(HipsChangeEvent changeEvent);

}
