package esac.archive.esasky.cl.web.client.event.banner;

import com.google.gwt.event.shared.GwtEvent;

public class ToggleServerProblemBannerEvent extends GwtEvent<ToggleServerProblemBannerEventHandler> {

    public final static Type<ToggleServerProblemBannerEventHandler> TYPE = new Type<ToggleServerProblemBannerEventHandler>();

    public ToggleServerProblemBannerEvent() {
    }

    @Override
    public final Type<ToggleServerProblemBannerEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final ToggleServerProblemBannerEventHandler handler) {
        handler.onEvent(this);
    }

}
