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

/**
 * Event to record closing results event tab sent by Tab components.
 * @author ESDC team Copyright (c) 2015- European Space Agency
 */
public class ProgressIndicatorPopEvent extends GwtEvent<ProgressIndicatorPopEventHandler> {

    /** Event id. */
    private String id;

    /** Event Type. */
    public final static Type<ProgressIndicatorPopEventHandler> TYPE = new Type<ProgressIndicatorPopEventHandler>();

    /**
     * Class Constructor.
     * @param inputId Input event id.
     */
    public ProgressIndicatorPopEvent(final String inputId) {
        this.id = inputId;
    }

    @Override
    public final Type<ProgressIndicatorPopEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final ProgressIndicatorPopEventHandler handler) {
        handler.onPopEvent(this);
    }

    /**
     * get event id.
     * @return String
     */
    public final String getId() {
        return this.id;
    }
}
