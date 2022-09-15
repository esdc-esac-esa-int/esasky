package esac.archive.esasky.cl.web.client.event.registry;

import com.google.gwt.event.shared.GwtEvent;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;

public class TapRegistrySelectEvent extends GwtEvent<TapRegistrySelectEventHandler> {
    public static final Type<TapRegistrySelectEventHandler> TYPE = new Type<>();
    private final IDescriptor descriptor;

    public TapRegistrySelectEvent() {
        this.descriptor = null;
    }
    public TapRegistrySelectEvent(IDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    public IDescriptor getDescriptor() {
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
