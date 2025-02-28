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

public class MultiSelectableDataInSkyChangedEvent extends GwtEvent<MultiSelectableDataInSkyChangedEventHandler> {

    public final static Type<MultiSelectableDataInSkyChangedEventHandler> TYPE = new Type<MultiSelectableDataInSkyChangedEventHandler>();

    private final boolean atLeastOneMultiSelectableDatasetInSky;
    
    public MultiSelectableDataInSkyChangedEvent(boolean atLeastOneMultiSelectableDatasetInSky) {
    	this.atLeastOneMultiSelectableDatasetInSky = atLeastOneMultiSelectableDatasetInSky;
    }

    @Override
    public final Type<MultiSelectableDataInSkyChangedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final MultiSelectableDataInSkyChangedEventHandler handler) {
        handler.onChange(this);
    }
    
    public boolean isAtLeastOneMultiSelectableDatasetInSky() {
    	return atLeastOneMultiSelectableDatasetInSky;
    }

}
