package esac.archive.esasky.cl.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

import esac.archive.esasky.cl.web.client.view.resultspanel.GeneralJavaScriptObject;

public class ShowPublicationSourcesEvent extends GwtEvent<ShowPublicationSourcesEventHandler> {

    public static Type<ShowPublicationSourcesEventHandler> TYPE = new Type<ShowPublicationSourcesEventHandler>();

    public final GeneralJavaScriptObject rowData;
    
    public ShowPublicationSourcesEvent(GeneralJavaScriptObject rowData) {
        this.rowData = rowData;
    }

    @Override
    public final Type<ShowPublicationSourcesEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final ShowPublicationSourcesEventHandler handler) {
        handler.onEvent(this);
    }

}
