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