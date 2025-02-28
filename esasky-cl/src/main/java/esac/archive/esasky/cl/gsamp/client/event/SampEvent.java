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

package esac.archive.esasky.cl.gsamp.client.event;

import com.google.gwt.event.shared.GwtEvent;

import esac.archive.esasky.cl.gsamp.client.model.SampMessage;

/**
 * Event to record samp messages received by our samp client.
 * @author ileon Copyright (c) 2011- European Space Agency
 */
public class SampEvent extends GwtEvent<SampEventHandler> {

    /** Samp Event Type. */
    public static final Type<SampEventHandler> TYPE = new Type<SampEventHandler>();

    /** local var sampMessage. */
    private SampMessage sampMessage;

    /**
     * SampEvent().
     * @param inputSampMessage Input SampMessage
     */
    public SampEvent(final SampMessage inputSampMessage) {
        this.sampMessage = inputSampMessage;
    }

    @Override
    public final Type<SampEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final SampEventHandler handler) {
        handler.onSampEvent(this);
    }

    /**
     * getSampMessage().
     * @return SampMessage
     */
    public final SampMessage getSampMessage() {
        return this.sampMessage;
    }
}
