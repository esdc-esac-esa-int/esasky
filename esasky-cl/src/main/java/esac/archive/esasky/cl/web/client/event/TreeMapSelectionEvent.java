package esac.archive.esasky.cl.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.cl.web.client.model.entities.EntityContext;

public class TreeMapSelectionEvent extends GwtEvent<TreeMapSelectionEventHandler> {

    public static Type<TreeMapSelectionEventHandler> TYPE = new Type<TreeMapSelectionEventHandler>();

    private EntityContext context;
    
    private IDescriptor descriptor;
    
    public TreeMapSelectionEvent(EntityContext context, IDescriptor descriptor) {
        super();
        this.context = context;
        this.descriptor = descriptor;
    }

    @Override
    public final Type<TreeMapSelectionEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final TreeMapSelectionEventHandler handler) {
        handler.onSelection(this);
    }
    
    public EntityContext getContext(){
    	return context;
    }
    
    public IDescriptor getDescriptor(){
    	return descriptor;
    }
}
