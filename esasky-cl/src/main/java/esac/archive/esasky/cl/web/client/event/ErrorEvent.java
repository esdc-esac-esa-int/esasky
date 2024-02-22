package esac.archive.esasky.cl.web.client.event;

import com.google.gwt.event.shared.GwtEvent;


public class ErrorEvent extends GwtEvent<ErrorEventHandler> {

    public static final Type<ErrorEventHandler> TYPE = new Type<>();
    public static final int FRONTEND_ERROR = 0;
    private final int status;
    private final String message;
    private final String details;
    public ErrorEvent(String message, Exception exception) {
        this(message, exception.getMessage());
    }

    public ErrorEvent(String message, String details) {
        this(FRONTEND_ERROR, message, details);
    }

    public ErrorEvent(int status, String message, String details) {
        this.status = status;
        this.message = message;
        this.details = details;
    }

    @Override
    public final Type<ErrorEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final ErrorEventHandler handler) {
        handler.onEvent(this);
    }

    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }

    public int getStatus() {
        return status;
    }
}
