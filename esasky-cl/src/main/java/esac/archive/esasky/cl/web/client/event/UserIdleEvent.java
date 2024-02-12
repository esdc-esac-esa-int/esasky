package esac.archive.esasky.cl.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class UserIdleEvent extends GwtEvent<UserIdleEventHandler> {
    public static final Type<UserIdleEventHandler> TYPE = new Type<>();
    private final boolean isIdle;

    public UserIdleEvent(boolean isIdle) {
        this.isIdle = isIdle;
    }

    @Override
    public final Type<UserIdleEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final UserIdleEventHandler handler) {
        handler.onIdleStatusChanged(this);
    }

    public boolean isUserIdle() {
        return isIdle;
    }
}
