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

import esac.archive.esasky.cl.gwidgets.client.util.SaveAllView;

/**
 * Event to record download events sent by Tab components.
 * @author María H. Sarmiento Copyright (c) 2016- European Space Agency
 */
public class ExportVOTableEvent extends GwtEvent<ExportVOTableEventHandler> {

    /** Event type. */
    public final static Type<ExportVOTableEventHandler> TYPE = new Type<ExportVOTableEventHandler>();

    /** ESASkyUniqID. */
    private String esaSkyUniqID;
    /** tab type can be ObservationsTablePanel or SourcesTablePanel. */
    /** SaveAllView local instance. */
    private SaveAllView saveAllView;

    /**
     * Class constructor.
     * @param inputEsaSkyUniqID Input String
     * @param inputTabType Input String
     * @param inputSaveAllView Input SaveAllView
     */
    public ExportVOTableEvent(final String inputEsaSkyUniqID,
            final SaveAllView inputSaveAllView) {
        this.esaSkyUniqID = inputEsaSkyUniqID;
        this.saveAllView = inputSaveAllView;
    }

    @Override
    public final Type<ExportVOTableEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final ExportVOTableEventHandler handler) {
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
