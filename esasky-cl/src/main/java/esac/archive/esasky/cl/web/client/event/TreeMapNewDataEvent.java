package esac.archive.esasky.cl.web.client.event;

import java.util.Collection;
import java.util.List;

import com.google.gwt.event.shared.GwtEvent;

import esac.archive.esasky.cl.web.client.model.DescriptorCountAdapter;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;

public class TreeMapNewDataEvent extends GwtEvent<TreeMapNewDataEventHandler> {

    public final static Type<TreeMapNewDataEventHandler> TYPE = new Type<>();

    private final Collection<DescriptorCountAdapter> descriptors;

    public TreeMapNewDataEvent(Collection<DescriptorCountAdapter> countAdapters) {
        this.descriptors = countAdapters;
    }

    @Override
    public final Type<TreeMapNewDataEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final TreeMapNewDataEventHandler handler) {
        handler.onNewDataEvent(this);;
    }

    public Collection<DescriptorCountAdapter> getCountAdapterList(){
        return descriptors;
    }

}