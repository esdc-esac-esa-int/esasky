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

//package esac.archive.esasky.cl.web.client.event;
//
//import com.google.gwt.event.shared.GwtEvent;
//
//import esac.archive.esasky.cl.wcstransform.module.utility.Constants.Instrument;
//
//
//public class DetectorChangeEvent extends GwtEvent<DetectorChangeEventHandler> {
//
//    public static Type<DetectorChangeEventHandler> TYPE = new Type<DetectorChangeEventHandler>();
//
//    private Instrument instrument;
//    private String detector;
//
//    public DetectorChangeEvent(final Instrument instrument, final String detector) {
//        this.instrument = instrument;
//        this.detector = detector;
//    }
//
//    public final String getDetector() {
//    	return this.detector;
//    }
//    
//    public final Instrument getInstrument() {
//    	return this.instrument;
//    }
//
//    @Override
//    public final Type<DetectorChangeEventHandler> getAssociatedType() {
//        return TYPE;
//    }
//
//    @Override
//    protected final void dispatch(final DetectorChangeEventHandler handler) {
//        handler.onChangeEvent(this);
//    }
//
//}
