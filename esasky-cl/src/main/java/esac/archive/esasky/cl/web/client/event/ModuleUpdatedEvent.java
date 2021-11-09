package esac.archive.esasky.cl.web.client.event;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.shared.GwtEvent;


/**
 * Event involved in the handler to distribute files via SAMP (copied from Gaia).
 *
 * @author María H Sarmiento - Copyright (c) 2015 - ESA/ESAC.
 */
public class ModuleUpdatedEvent extends GwtEvent<ModuleUpdatedEventHandler> {

    /** Defining event type. */
    public final static Type<ModuleUpdatedEventHandler> TYPE = new Type<ModuleUpdatedEventHandler>();

    private String key;
    private boolean value;


    public ModuleUpdatedEvent(String key, boolean value) {
        super();
        this.key = key;
        this.value = value;
    }

    @Override
    public final Type<ModuleUpdatedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final ModuleUpdatedEventHandler handler) {
        Log.debug("[ModuleUpdatedEvent] Into dispatch module updated event... ");
        handler.onEvent(this);
    }

	public String getKey() {
		return key;
	}

	public boolean getValue() {
		return value;
	}
    
}
