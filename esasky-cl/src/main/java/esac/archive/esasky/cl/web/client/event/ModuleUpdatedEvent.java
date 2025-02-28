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

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.shared.GwtEvent;


/**
 * Event involved in the handler to distribute files via SAMP (copied from Gaia).
 *
 * @author María H Sarmiento - Copyright (c) 2015 - ESA/ESAC.
 */
public class ModuleUpdatedEvent extends GwtEvent<ModuleUpdatedEventHandler> {

    /** Defining event type. */
    public static final Type<ModuleUpdatedEventHandler> TYPE = new Type<>();

    private String key;
    private boolean value;


    public ModuleUpdatedEvent(String key, boolean value) {
        super();
        this.key = key;
        this.value = value;
    }

    @Override
    public final Type<ModuleUpdatedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final ModuleUpdatedEventHandler handler) {
        Log.debug("[ModuleUpdatedEvent] Into dispatch module updated event... ");
        handler.onEvent(this);
    }

	public String getKey() {
		return key;
	}

	public boolean getValue() {
		return value;
	}
    
}
