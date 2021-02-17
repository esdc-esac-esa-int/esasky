package esac.archive.esasky.cl.web.client.event;

import java.util.List;

import com.google.gwt.event.shared.GwtEvent;

import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;

public class TreeMapNewDataEvent extends GwtEvent<TreeMapNewDataEventHandler> {

    public final static Type<TreeMapNewDataEventHandler> TYPE = new Type<TreeMapNewDataEventHandler>();
    
    private List<IDescriptor> descriptors;
    private List<Integer> counts;

    public TreeMapNewDataEvent(List<IDescriptor> descriptors, List<Integer> counts) {
    	this.descriptors = descriptors;
    	this.counts = counts;
    }

    @Override
    public final Type<TreeMapNewDataEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final TreeMapNewDataEventHandler handler) {
        handler.onNewDataEvent(this);;
    }
    
    public List<IDescriptor> getDescriptors(){
    	return descriptors;
    }
    
    public List<Integer> getCounts(){
    	return counts;
    }
}