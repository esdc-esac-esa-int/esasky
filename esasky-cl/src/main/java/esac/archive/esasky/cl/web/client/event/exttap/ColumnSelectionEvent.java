package esac.archive.esasky.cl.web.client.event.exttap;

import com.google.gwt.event.shared.GwtEvent;

public class ColumnSelectionEvent extends GwtEvent<ColumnSelectionEventHandler>  {
    public static final Type<ColumnSelectionEventHandler> TYPE = new Type<>();
    private final boolean isRegionQuery;
    private final String raColumn;
    private final String decColumn;
    private final String regionColumn;

    public ColumnSelectionEvent(boolean isRegionQuery, String raColumn, String decColumn, String regionColumn) {
        this.isRegionQuery = isRegionQuery;
        this.raColumn = raColumn;
        this.decColumn = decColumn;
        this.regionColumn = regionColumn;
    }

    public boolean isRegionQuery() {
        return isRegionQuery;
    }

    public String getRaColumn() {
        return raColumn;
    }

    public String getDecColumn() {
        return decColumn;
    }

    public String getRegionColumn() {
        return regionColumn;
    }

    @Override
    public Type<ColumnSelectionEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ColumnSelectionEventHandler handler) {
        handler.onColumnSelection(this);
    }
}