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

package esac.archive.esasky.cl.web.client.model.entities;


import com.google.gwt.core.client.Scheduler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.Image;

import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.AladinShape;
import esac.archive.esasky.cl.web.client.query.TAPPublicationsService;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.cl.web.client.view.resultspanel.ITablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.PublicationsTablePanel;

public class PublicationsByAuthorEntity extends EsaSkyEntity {

    private final String authorId;
    public PublicationsByAuthorEntity(CommonTapDescriptor descriptor,
                                      CountStatus countStatus, SkyViewPosition skyViewPosition,
                                      String authorId) {
        super(descriptor, countStatus, skyViewPosition, authorId, TAPPublicationsService.getInstance());
        this.authorId = authorId;
    }

    @Override
    public ITablePanel createTablePanel() {
        tablePanel = new PublicationsTablePanel(getTabLabel(), authorId, this);
        return tablePanel;
    }
    
    @Override
    public void fetchData() {
        Scheduler.get().scheduleFinally(() -> tablePanel.insertData(EsaSkyWebConstants.PUBLICATIONS_BY_AUTHOR_URL
                + "?AUTHOR=" + URL.encodeQueryString(authorId) + "&ROWS=" + 50000));
    }

    @Override
    public String getId() {
        return authorId;
    }


    @Override
    public boolean isCustomizable() {
    	return false;
    }
    
    @Override
    public String getTabLabel() {
    	return authorId;
    }
    
    @Override
    public boolean isRefreshable() {
    	return false;
    }
    
    @Override
    public Image getTypeLogo() {
    	return new Image("images/cds.png");
    }
    
    @Override
    public void addShapes(GeneralJavaScriptObject javaScriptObject, GeneralJavaScriptObject metadata) {
        // Not needed for this entity
    }
    
    @Override
    public void onShapeSelection(AladinShape shape) {
    }
    
    @Override
    public void onShapeDeselection(AladinShape shape) {
    }
    
    @Override
    public void deselectAllShapes() {
    }
    
    @Override
    public void selectShapes(int shapeId) {
    }
  
    @Override
    public void onShapeHover(AladinShape shape) {
    }
    
    @Override
    public void onShapeUnhover(AladinShape shape) {
    }
    
}
