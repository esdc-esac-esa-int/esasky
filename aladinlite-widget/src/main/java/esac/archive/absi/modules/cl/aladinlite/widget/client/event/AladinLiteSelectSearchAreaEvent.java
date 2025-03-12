package esac.archive.absi.modules.cl.aladinlite.widget.client.event;

import com.google.gwt.event.shared.GwtEvent;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.SearchArea;

public class AladinLiteSelectSearchAreaEvent extends GwtEvent<AladinLiteSelectSearchAreaEventHandler> {
    public static Type<AladinLiteSelectSearchAreaEventHandler> TYPE = new Type<>();
    private final SearchArea searchArea;

    public AladinLiteSelectSearchAreaEvent(SearchArea searchArea) {
        this.searchArea = searchArea;
    }

    @Override
    public GwtEvent.Type<AladinLiteSelectSearchAreaEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AladinLiteSelectSearchAreaEventHandler handler) {
        handler.onSelectSearchAreaEvent(this);
    }

    public SearchArea getSearchArea() {
        return this.searchArea;
    }

}