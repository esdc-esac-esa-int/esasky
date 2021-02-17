package esac.archive.esasky.cl.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.cl.web.client.model.entities.EntityContext;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.treemap.PointInformation;

public class TreeMapSelectionEvent extends GwtEvent<TreeMapSelectionEventHandler> {

    public final static Type<TreeMapSelectionEventHandler> TYPE = new Type<TreeMapSelectionEventHandler>();

    private PointInformation pointInformation;
    
    public TreeMapSelectionEvent(PointInformation pointInformation) {
        super();
        this.pointInformation = pointInformation;
    }

    @Override
    public final Type<TreeMapSelectionEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final TreeMapSelectionEventHandler handler) {
        handler.onSelection(this);
    }
    
    public PointInformation getPointInformation(){
    	return pointInformation;
    }
    
    public EntityContext getContext(){
    	return pointInformation.context;
    }
    
    public IDescriptor getDescriptor(){
    	return pointInformation.descriptor;
    }
}
