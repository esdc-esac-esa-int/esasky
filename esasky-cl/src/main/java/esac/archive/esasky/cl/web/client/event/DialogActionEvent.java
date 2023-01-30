package esac.archive.esasky.cl.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class DialogActionEvent extends GwtEvent<DialogActionEventHandler> {

    /** Event type. */
    public static final Type<DialogActionEventHandler> TYPE = new Type<>();

    public enum DialogAction { YES, NO, CANCEL }

    DialogAction action;

    public DialogActionEvent(DialogAction action) {
        super();
        this.action = action;
    }

    @Override
    public final Type<DialogActionEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final DialogActionEventHandler handler) {
        handler.onAction(this);
    }

    public DialogAction getAction() {
        return action;
    }

}
