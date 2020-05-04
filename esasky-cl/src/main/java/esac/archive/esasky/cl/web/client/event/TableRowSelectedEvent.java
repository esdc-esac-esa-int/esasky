package esac.archive.esasky.cl.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;

public class TableRowSelectedEvent extends GwtEvent<TableRowSelectedEventHandler> {

    public static Type<TableRowSelectedEventHandler> TYPE = new Type<TableRowSelectedEventHandler>();

    private GeneralJavaScriptObject rowData;

    public TableRowSelectedEvent(GeneralJavaScriptObject rowData) {
        this.rowData = rowData;
    }

    @Override
    public final Type<TableRowSelectedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final TableRowSelectedEventHandler handler) {
        handler.onEvent(this);
    }

    public GeneralJavaScriptObject getRowData() {
        return rowData;
    }

}
