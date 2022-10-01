package esac.archive.esasky.cl.web.client.view.common;

import com.google.gwt.event.logical.shared.*;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

public interface Hidable<T> extends HasCloseHandlers<T>, HasOpenHandlers<T> {

    void show();

    void hide();

    boolean isShowing();

    <T extends EventHandler> HandlerRegistration addHandler(final T handler, GwtEvent.Type<T> type);

    default void toggle() {
        if (isShowing()) {
            hide();
        } else {
            show();
        }
    }

    default HandlerRegistration addCloseHandler(CloseHandler<T> handler) {
        return addHandler(handler, CloseEvent.getType());
    }

    default HandlerRegistration addOpenHandler(OpenHandler<T> handler) {
        return addHandler(handler, OpenEvent.getType());
    }
}
