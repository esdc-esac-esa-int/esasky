package esac.archive.esasky.cl.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class ShowEvaEvent extends GwtEvent<ShowEvaEventHandler> {

    /** Event type. */
    public static final Type<ShowEvaEventHandler> TYPE = new Type<>();

    public ShowEvaEvent() {
        super();
    }

    @Override
    public final Type<ShowEvaEventHandler> getAssociatedType() {
        return TYPE;
    }

	@Override
	protected void dispatch(ShowEvaEventHandler handler) {
		handler.onShowEva(this);
	}

}
