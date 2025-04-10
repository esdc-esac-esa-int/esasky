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

package esac.archive.esasky.cl.web.client.event.hips;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event to record closing results event tab sent by Tab components.
 */
public class HipsNameChangeEvent extends GwtEvent<HipsNameChangeEventHandler> {

    /** Event type. */
    public final static Type<HipsNameChangeEventHandler> TYPE = new Type<HipsNameChangeEventHandler>();

    private final String hipsName;
    private final boolean fromPlayback;

    public HipsNameChangeEvent(final String hipsName) {
        this.hipsName = hipsName;
        fromPlayback = false;
    }

    public HipsNameChangeEvent(final String hipsName, final boolean fromPlayback) {
        this.hipsName = hipsName;
        this.fromPlayback = fromPlayback;
    }

    public final String getHiPSName() {
        return hipsName;
    }

    public final boolean isFromPlayback() {
        return fromPlayback;
    }

    @Override
    public final Type<HipsNameChangeEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final HipsNameChangeEventHandler handler) {
        handler.onChangeEvent(this);
    }
}
