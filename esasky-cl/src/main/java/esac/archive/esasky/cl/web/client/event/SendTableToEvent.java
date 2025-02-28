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
 * @author María H. Sarmiento Copyright (c) 2016- European Space Agency
 */
public class SendTableToEvent extends GwtEvent<SendTableToEventHandler> {

    /** Event type. */
    public final static Type<SendTableToEventHandler> TYPE = new Type<SendTableToEventHandler>();

    /** ESASkyUniqID. */
    private String esaSkyUniqID;
    /** tab type can be ObservationsTablePanel or SourcesTablePanel. */

    /**
     * Class constructor.
     * @param inputEsaSkyUniqID Input String
     * @param inputTabType Input String
     */
    public SendTableToEvent(final String inputEsaSkyUniqID) {
        this.esaSkyUniqID = inputEsaSkyUniqID;
    }

    @Override
    public final Type<SendTableToEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final SendTableToEventHandler handler) {
        handler.onSendTableClick(this);
    }

    public final String getEsaSkyUniqID() {
        return esaSkyUniqID;
    }

}
