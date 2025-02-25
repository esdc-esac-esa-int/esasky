/*
ESASky
Copyright (C) 2025 Henrik Norman

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published
by the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package esac.archive.esasky.cl.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;

public class TableRowSelectedEvent extends GwtEvent<TableRowSelectedEventHandler> {

    public final static Type<TableRowSelectedEventHandler> TYPE = new Type<TableRowSelectedEventHandler>();

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
