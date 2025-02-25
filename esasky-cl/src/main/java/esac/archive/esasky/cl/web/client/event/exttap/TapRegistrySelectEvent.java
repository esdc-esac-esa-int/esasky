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

package esac.archive.esasky.cl.web.client.event.exttap;

import com.google.gwt.event.shared.GwtEvent;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;

public class TapRegistrySelectEvent extends GwtEvent<TapRegistrySelectEventHandler> {
    public static final Type<TapRegistrySelectEventHandler> TYPE = new Type<>();
    private final CommonTapDescriptor descriptor;
    private final GeneralJavaScriptObject data;

    public TapRegistrySelectEvent(CommonTapDescriptor descriptor) {
        this(descriptor, null);
    }

    public TapRegistrySelectEvent(CommonTapDescriptor descriptor, GeneralJavaScriptObject data) {
        this.descriptor = descriptor;
        this.data = data;
    }

    public CommonTapDescriptor getDescriptor() {
        return this.descriptor;
    }

    @Override
    public Type<TapRegistrySelectEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(TapRegistrySelectEventHandler handler) {
        handler.onSelect(this);
    }

    public GeneralJavaScriptObject getData() {
        return data;
    }

    public boolean hasData() {
        return getData() != null;
    }
}
