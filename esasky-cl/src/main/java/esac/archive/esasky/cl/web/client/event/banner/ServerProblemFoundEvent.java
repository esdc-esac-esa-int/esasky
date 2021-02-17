package esac.archive.esasky.cl.web.client.event.banner;

import com.google.gwt.event.shared.GwtEvent;

public class ServerProblemFoundEvent extends GwtEvent<ServerProblemFoundEventHandler> {

    public final static Type<ServerProblemFoundEventHandler> TYPE = new Type<ServerProblemFoundEventHandler>();

    public ServerProblemFoundEvent() {
    }

    @Override
    public final Type<ServerProblemFoundEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final ServerProblemFoundEventHandler handler) {
        handler.onEvent(this);
    }

}
