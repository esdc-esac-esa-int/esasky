package esac.archive.esasky.cl.web.client.event.banner;

import com.google.gwt.event.shared.GwtEvent;

public class ServerProblemSolvedEvent extends GwtEvent<ServerProblemSolvedEventHandler> {

    public static Type<ServerProblemSolvedEventHandler> TYPE = new Type<ServerProblemSolvedEventHandler>();

    public ServerProblemSolvedEvent() {
    }

    @Override
    public final Type<ServerProblemSolvedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final ServerProblemSolvedEventHandler handler) {
        handler.onEvent(this);
    }

}
