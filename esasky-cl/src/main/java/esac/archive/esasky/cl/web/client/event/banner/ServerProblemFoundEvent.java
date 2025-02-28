/*
ESASky
Copyright (C) 2025 European Space Agency

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

package esac.archive.esasky.cl.web.client.event.banner;

import com.google.gwt.event.shared.GwtEvent;

public class ServerProblemFoundEvent extends GwtEvent<ServerProblemFoundEventHandler> {

    public final static Type<ServerProblemFoundEventHandler> TYPE = new Type<ServerProblemFoundEventHandler>();

    public ServerProblemFoundEvent() {
    }

    @Override
    public final Type<ServerProblemFoundEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final ServerProblemFoundEventHandler handler) {
        handler.onEvent(this);
    }

}
