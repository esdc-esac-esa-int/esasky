package esac.archive.esasky.cl.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event to record closing results event tab sent by Tab components.
 * @author ESDC team Copyright (c) 2015- European Space Agency
 */
public class ProgressIndicatorPopEvent extends GwtEvent<ProgressIndicatorPopEventHandler> {

    /** Event id. */
    private String id;

    /** Event Type. */
    public final static Type<ProgressIndicatorPopEventHandler> TYPE = new Type<ProgressIndicatorPopEventHandler>();

    /**
     * Class Constructor.
     * @param inputId Input event id.
     */
    public ProgressIndicatorPopEvent(final String inputId) {
        this.id = inputId;
    }

    @Override
    public final Type<ProgressIndicatorPopEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final ProgressIndicatorPopEventHandler handler) {
        handler.onPopEvent(this);
    }

    /**
     * get event id.
     * @return String
     */
    public final String getId() {
        return this.id;
    }
}
