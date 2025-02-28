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

import esac.archive.esasky.cl.web.client.model.entities.EntityContext;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.treemap.PointInformation;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;

public class TreeMapSelectionEvent extends GwtEvent<TreeMapSelectionEventHandler> {

    public final static Type<TreeMapSelectionEventHandler> TYPE = new Type<TreeMapSelectionEventHandler>();

    private PointInformation pointInformation;
    
    public TreeMapSelectionEvent(PointInformation pointInformation) {
        super();
        this.pointInformation = pointInformation;
    }

    @Override
    public final Type<TreeMapSelectionEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final TreeMapSelectionEventHandler handler) {
        handler.onSelection(this);
    }
    
    public PointInformation getPointInformation(){
    	return pointInformation;
    }
    
    public EntityContext getContext(){
    	return pointInformation.context;
    }
    
    public CommonTapDescriptor getDescriptor(){
    	return pointInformation.descriptor;
    }
}
