package esac.archive.esasky.cl.gsamp.client.event;

import com.google.gwt.event.shared.GwtEvent;

import esac.archive.esasky.cl.gsamp.client.model.SampMessage;

/**
 * Event to record samp messages received by our samp client.
 * @author ileon Copyright (c) 2011- European Space Agency
 */
public class SampEvent extends GwtEvent<SampEventHandler> {

    /** Samp Event Type. */
    public static final Type<SampEventHandler> TYPE = new Type<SampEventHandler>();

    /** local var sampMessage. */
    private SampMessage sampMessage;

    /**
     * SampEvent().
     * @param inputSampMessage Input SampMessage
     */
    public SampEvent(final SampMessage inputSampMessage) {
        this.sampMessage = inputSampMessage;
    }

    @Override
    public final Type<SampEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final SampEventHandler handler) {
        handler.onSampEvent(this);
    }

    /**
     * getSampMessage().
     * @return SampMessage
     */
    public final SampMessage getSampMessage() {
        return this.sampMessage;
    }
}
