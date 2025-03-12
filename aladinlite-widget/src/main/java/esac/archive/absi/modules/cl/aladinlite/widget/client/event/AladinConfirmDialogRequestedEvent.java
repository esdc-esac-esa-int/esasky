package esac.archive.absi.modules.cl.aladinlite.widget.client.event;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.GwtEvent;

public class AladinConfirmDialogRequestedEvent extends GwtEvent<AladinConfirmDialogRequestedEventHandler> {
    public static Type<AladinConfirmDialogRequestedEventHandler> TYPE = new Type<>();

    private final String title;
    private final String message;
    private final String help;
    private final JavaScriptObject callback;

    public AladinConfirmDialogRequestedEvent(String title, String message, String help, JavaScriptObject callback) {
        this.title = title;
        this.message = message;
        this.help = help;
        this.callback = callback;
    }

    @Override
    public Type<AladinConfirmDialogRequestedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AladinConfirmDialogRequestedEventHandler handler) {
        handler.onConfirmDialogRequested(this);
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }
    public String getHelp() {
        return help;
    }


    public void callbackResult(boolean result) {
        callbackResult(callback, result);
    }

    private native void callbackResult(JavaScriptObject callback, boolean result) /*-{
        callback(result);
    }-*/;

}
