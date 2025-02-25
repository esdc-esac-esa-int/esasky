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

import esac.archive.esasky.cl.gwidgets.client.util.SaveAllView;

/**
 * Event to record download events sent by Tab components.
 * @author María H. Sarmiento Copyright (c) 2016- European Space Agency
 */
public class ExportCSVEvent extends GwtEvent<ExportCSVEventHandler> {

    /** Event type. */
    public final static Type<ExportCSVEventHandler> TYPE = new Type<ExportCSVEventHandler>();

    /** ESASkyUniqID. */
    private String esaSkyUniqID;
    /** SaveAllView local instance. */
    private SaveAllView saveAllView;

    /**
     * Class constructor.
     * @param inputEsaSkyUniqID Input String
     * @param inputSaveAllView Input SaveAllView
     */
    public ExportCSVEvent(final String inputEsaSkyUniqID,
            final SaveAllView inputSaveAllView) {
        this.esaSkyUniqID = inputEsaSkyUniqID;
        this.saveAllView = inputSaveAllView;
    }

    @Override
    public final Type<ExportCSVEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final ExportCSVEventHandler handler) {
        handler.onExportClick(this);
    }

    /**
     * getEsaSkyUniqID().
     * @return String
     */
    public final String getEsaSkyUniqID() {
        return esaSkyUniqID;
    }

    /**
     * getSaveAllView().
     * @return SaveAllView
     */
    public final SaveAllView getSaveAllView() {
        return this.saveAllView;
    }
}
