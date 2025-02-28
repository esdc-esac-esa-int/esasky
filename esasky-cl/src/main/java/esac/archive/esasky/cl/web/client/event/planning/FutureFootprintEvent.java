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

package esac.archive.esasky.cl.web.client.event.planning;

import com.google.gwt.event.shared.GwtEvent;

import esac.archive.esasky.cl.web.client.view.ctrltoolbar.planningmenu.FutureFootprintRow;

/**
 * Event to record Select Observation events sent by Table components.
 */
public class FutureFootprintEvent extends GwtEvent<FutureFootprintEventHandler> {

    /** evnet type. */
    public final static Type<FutureFootprintEventHandler> TYPE = new Type<FutureFootprintEventHandler>();

    private FutureFootprintRow futureFootprintRow;

    public FutureFootprintEvent(final FutureFootprintRow futureFootprintRow) {

        this.futureFootprintRow = futureFootprintRow;
    }

    @Override
    public final Type<FutureFootprintEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final FutureFootprintEventHandler handler) {
        handler.drawPlanningFootprint(this);
    }

    public FutureFootprintRow getFutureFootprintRow() {
        return futureFootprintRow;
    }

}
