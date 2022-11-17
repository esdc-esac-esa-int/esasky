package esac.archive.esasky.cl.web.client.event.exttap;

import com.google.gwt.event.shared.GwtEvent;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;

public class TapRegistrySelectEvent extends GwtEvent<TapRegistrySelectEventHandler> {
    public static final Type<TapRegistrySelectEventHandler> TYPE = new Type<>();
    private final CommonTapDescriptor descriptor;
    private final GeneralJavaScriptObject data;

    public TapRegistrySelectEvent(CommonTapDescriptor descriptor) {
        this(descriptor, null);
    }

    public TapRegistrySelectEvent(CommonTapDescriptor descriptor, GeneralJavaScriptObject data) {
        this.descriptor = descriptor;
        this.data = data;
    }

    public CommonTapDescriptor getDescriptor() {
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

    public GeneralJavaScriptObject getData() {
        return data;
    }

    public boolean hasData() {
        return getData() != null;
    }
}
