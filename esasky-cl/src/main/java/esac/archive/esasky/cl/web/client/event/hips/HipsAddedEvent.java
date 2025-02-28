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
import esac.archive.esasky.ifcs.model.client.HiPS;
import esac.archive.esasky.ifcs.model.client.HipsWavelength;

public class HipsAddedEvent extends GwtEvent<HipsAddedEventHandler> {

    public static final Type<HipsAddedEventHandler> TYPE = new Type<>();

    private HiPS hips;
    private String hipsWavelength;
    private boolean addIfAlreadyExist;

    public HipsAddedEvent(final HiPS inputHips, final String hipsWavelength) {
    	this(inputHips, hipsWavelength, true);
    }

    public HipsAddedEvent(final HiPS inputHips, final String hipsWavelength, final boolean addIfAlreadyExist) {
    	this.hips = inputHips;
    	this.hipsWavelength = hipsWavelength;
    	if(this.hips != null) {
    		this.hips.setHipsWavelength(hipsWavelength);
    	}
    	this.addIfAlreadyExist = addIfAlreadyExist;
    }

    public final HiPS getHiPS() {
        return hips;
    }
    
    public final String getHipsWavelength() {
    	return hipsWavelength;
    }

    public final boolean getAddIfAlreadyExist() {
    	return addIfAlreadyExist;
    }

    @Override
    public final Type<HipsAddedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final HipsAddedEventHandler handler) {
        handler.onEvent(this);
    }
}
