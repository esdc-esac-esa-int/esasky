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

public class TileLoadedEvent extends GwtEvent<TileLoadedEventHandler> {

    public static final Type<TileLoadedEventHandler> TYPE = new Type<>();
    private final boolean success;
    private final String message;

    public TileLoadedEvent(boolean success) {
        this(success, "");
    }

    public TileLoadedEvent(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    @Override
    public Type<TileLoadedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(TileLoadedEventHandler handler) {
        handler.onTileLoaded(this);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}