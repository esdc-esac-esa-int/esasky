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

import esac.archive.esasky.cl.web.client.model.entities.GeneralEntityInterface;

public class AddTableEvent extends GwtEvent<AddTableEventHandler> {

    public final static Type<AddTableEventHandler> TYPE = new Type<AddTableEventHandler>();

    private GeneralEntityInterface entity;

    public AddTableEvent(GeneralEntityInterface entity) {
        this.entity = entity;
    }

    @Override
    public final Type<AddTableEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final AddTableEventHandler handler) {
        handler.onEvent(this);
    }

    public GeneralEntityInterface getEntity() {
        return entity;
    }

}
