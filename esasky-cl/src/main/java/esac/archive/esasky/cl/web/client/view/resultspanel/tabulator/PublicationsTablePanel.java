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

package esac.archive.esasky.cl.web.client.view.resultspanel.tabulator;

import java.util.List;

import esac.archive.esasky.cl.web.client.model.entities.GeneralEntityInterface;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;

public class PublicationsTablePanel extends TabulatorTablePanel {
    
    public PublicationsTablePanel(String label, String esaSkyUniqID, GeneralEntityInterface entity) {
        super(label, esaSkyUniqID, entity);
        selectTablePanel();
    }
    
    @Override
    public void onRowMouseEnter(int rowId) {
        //Do nothing
    }

    @Override
    public void onRowMouseLeave(int rowId) {
        //Do nothing
    }
    
    @Override
    public void hoverStartRow(int rowId) {
        //Do nothing
    }

    @Override
    public void hoverStopRow(int rowId) {
        //Do nothing
    }
    
    @Override
    public void onRowSelection(final GeneralJavaScriptObject row) {
        //Do nothing
    }
    
    @Override
    public void onRowDeselection(GeneralJavaScriptObject row) {
        //Do nothing
    }
    
    @Override
    public void onDataFiltered(List<Integer> indexArray) {
        this.numberOfShownRows = indexArray.size();
        notifyNumberOfRowsShowingChanged(numberOfShownRows);
    }

	@Override
	public void deselectTablePanel() {
		super.deselectTablePanel();
		entity.deselectAllShapes();
	}
	
	@Override
	public void selectTablePanel() {
		super.selectTablePanel();
		entity.selectShapes(0);
	}
	
}
