package esac.archive.esasky.cl.web.client.event.registry;

import com.google.gwt.event.shared.GwtEvent;
import esac.archive.esasky.ifcs.model.descriptor.TapRegistryDescriptor;

public class TapRegistrySelectEvent extends GwtEvent<TapRegistrySelectEventHandler> {
    public static final Type<TapRegistrySelectEventHandler> TYPE = new Type<>();
    private final TapRegistryDescriptor descriptor;

    public TapRegistrySelectEvent() {
        this.descriptor = null;
    }
    public TapRegistrySelectEvent(TapRegistryDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    public TapRegistryDescriptor getDescriptor() {
        return this.descriptor;
    }

    @Override
    public Type<TapRegistrySelectEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(TapRegistrySelectEventHandler handler) {
        handler.onSelect(this);
    }
}
