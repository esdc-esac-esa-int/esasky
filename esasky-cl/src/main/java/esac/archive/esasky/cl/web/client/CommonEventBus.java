package esac.archive.esasky.cl.web.client;

import com.google.gwt.event.shared.EventBus;

/**
 * @author ESDC team Copyright (c) 2015- European Space Agency
 */
public class CommonEventBus {

    private static EventBus eventBus;

    public static EventBus getEventBus() {
        return eventBus;
    }

    public static void setEventBus(EventBus eventBus) {
        CommonEventBus.eventBus = eventBus;
    }

}
