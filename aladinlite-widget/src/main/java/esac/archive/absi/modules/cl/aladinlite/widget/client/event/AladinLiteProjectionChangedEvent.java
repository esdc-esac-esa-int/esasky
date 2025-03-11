package esac.archive.absi.modules.cl.aladinlite.widget.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class AladinLiteProjectionChangedEvent extends GwtEvent<AladinLiteProjectionChangedEventHandler>{
    public static GwtEvent.Type<AladinLiteProjectionChangedEventHandler> TYPE = new GwtEvent.Type<>();

    private final String projection;

    public AladinLiteProjectionChangedEvent(String projection) {
        this.projection = projection;
    }

    @Override
    public GwtEvent.Type<AladinLiteProjectionChangedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AladinLiteProjectionChangedEventHandler handler) {
        handler.onProjectionChanged(this);
    }

    public String getProjection() {
        return projection;
    }
}
