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

package esac.archive.esasky.cl.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class UserIdleEvent extends GwtEvent<UserIdleEventHandler> {
    public static final Type<UserIdleEventHandler> TYPE = new Type<>();
    private final boolean isIdle;

    public UserIdleEvent(boolean isIdle) {
        this.isIdle = isIdle;
    }

    @Override
    public final Type<UserIdleEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final UserIdleEventHandler handler) {
        handler.onIdleStatusChanged(this);
    }

    public boolean isUserIdle() {
        return isIdle;
    }
}
