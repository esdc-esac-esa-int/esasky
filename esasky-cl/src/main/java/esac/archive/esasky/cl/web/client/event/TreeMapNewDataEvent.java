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

import java.util.Collection;

import com.google.gwt.event.shared.GwtEvent;

import esac.archive.esasky.cl.web.client.model.DescriptorCountAdapter;

public class TreeMapNewDataEvent extends GwtEvent<TreeMapNewDataEventHandler> {

    public static final Type<TreeMapNewDataEventHandler> TYPE = new Type<>();

    private final Collection<DescriptorCountAdapter> descriptors;
    private final boolean clearData;
    private String clearCategory;

    public TreeMapNewDataEvent(Collection<DescriptorCountAdapter> countAdapters) {
        this(countAdapters, false, "");
    }

    public TreeMapNewDataEvent(Collection<DescriptorCountAdapter> countAdapters, boolean clearData, String category) {
        this.descriptors = countAdapters;
        this.clearData = clearData;
        this.clearCategory = category;
    }

    @Override
    public final Type<TreeMapNewDataEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final TreeMapNewDataEventHandler handler) {
        handler.onNewDataEvent(this);;
    }

    public Collection<DescriptorCountAdapter> getCountAdapterList(){
        return descriptors;
    }

    public boolean clearData() {
        return clearData;
    }

    public String getClearCategory() {
        return clearCategory;
    }
}