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
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.AladinShape;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.AddShapeTooltipEvent;
import esac.archive.esasky.cl.web.client.query.TAPPublicationsService;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.view.allskypanel.PublicationTooltip;
import esac.archive.esasky.cl.web.client.view.resultspanel.ITablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.PublicationsTablePanel;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;

public class PublicationsBySourceEntity extends EsaSkyEntity {
    private final String publicationId;
    public PublicationsBySourceEntity(CommonTapDescriptor descriptor,
                                      CountStatus countStatus, SkyViewPosition skyViewPosition,
                                      String publicationId, double ra, double dec, String bibcount) {
        super(descriptor, countStatus, skyViewPosition, publicationId, TAPPublicationsService.getInstance(), 14, AladinLiteWrapper.getAladinLite().createImageMarker("images/publications_shape.png"));
        super.addShapes(getTableShapeInfo(ra, dec, bibcount, publicationId), null);
        this.publicationId = publicationId;
    }

    @Override
    public ITablePanel createTablePanel() {
        tablePanel = new PublicationsTablePanel(getTabLabel(), publicationId, this);
        return tablePanel;
    }

    @Override
    public String getId() {
        return publicationId;
    }
    
    @Override
    public void fetchData() {
        Scheduler.get().scheduleFinally(() -> tablePanel.insertData(EsaSkyWebConstants.PUBLICATIONS_BY_SOURCE_URL + "?SOURCE=" + URL.encodeQueryString(publicationId)
            + "&ROWS=" + 50000));
    }
    
    @Override
    public boolean isCustomizable() {
    	return false;
    }
    
    @Override
    public String getTabLabel() {
    	return publicationId;
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
    
    private native GeneralJavaScriptObject getTableShapeInfo(double ra, double dec, String bibcount, String name)/*-{
        return [{ra: ra,
            dec: dec,
            name: name,
            bibcount: bibcount}];
    }-*/;
    
    @Override
    public void onShapeSelection(AladinShape shape) {
        select();
    }
    
    @Override
    public void onShapeDeselection(AladinShape shape) {
    }
  
    @Override
    public void onShapeHover(AladinShape shape) {
        tooltip = new PublicationTooltip(shape);
        CommonEventBus.getEventBus().fireEvent(new AddShapeTooltipEvent(tooltip));
    }
    
    @Override
    public void onShapeUnhover(AladinShape shape) {
        if(tooltip != null) {
            tooltip.removeFromParent();
            tooltip = null;
        }
    }
    
}
