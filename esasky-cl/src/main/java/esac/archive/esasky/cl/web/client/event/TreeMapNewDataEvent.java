package esac.archive.esasky.cl.web.client.event;

import java.util.Collection;

import com.google.gwt.event.shared.GwtEvent;

import esac.archive.esasky.cl.web.client.model.DescriptorCountAdapter;

public class TreeMapNewDataEvent extends GwtEvent<TreeMapNewDataEventHandler> {

    public static final Type<TreeMapNewDataEventHandler> TYPE = new Type<>();

    private final Collection<DescriptorCountAdapter> descriptors;
    private final boolean replaceData;

    public TreeMapNewDataEvent(Collection<DescriptorCountAdapter> countAdapters) {
        this(countAdapters, false);
    }

    public TreeMapNewDataEvent(Collection<DescriptorCountAdapter> countAdapters, boolean replaceData) {
        this.descriptors = countAdapters;
        this.replaceData = replaceData;
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

    public boolean replaceData() {
        return replaceData;
    }
}