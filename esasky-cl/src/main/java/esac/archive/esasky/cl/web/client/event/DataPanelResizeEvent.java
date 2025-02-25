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

public class DataPanelResizeEvent extends GwtEvent<DataPanelResizeEventHandler> {

    public final static Type<DataPanelResizeEventHandler> TYPE = new Type<DataPanelResizeEventHandler>();

    private final int height;
    
    public DataPanelResizeEvent(int height) {
    	this.height = height;
    }

    @Override
    public final Type<DataPanelResizeEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final DataPanelResizeEventHandler handler) {
        handler.onDataPanelResize(this);
    }
    
    public int getNewHeight() {
    	return height;
    }

}
