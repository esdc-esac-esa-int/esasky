package esac.archive.esasky.cl.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

import esac.archive.esasky.cl.web.client.model.entities.GeneralEntityInterface;

public class AddTableEvent extends GwtEvent<AddTableEventHandler> {

    public final static Type<AddTableEventHandler> TYPE = new Type<AddTableEventHandler>();

    private GeneralEntityInterface entity;

    public AddTableEvent(GeneralEntityInterface entity) {
        this.entity = entity;
    }

    @Override
    public final Type<AddTableEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final AddTableEventHandler handler) {
        handler.onEvent(this);
    }

    public GeneralEntityInterface getEntity() {
        return entity;
    }

}
