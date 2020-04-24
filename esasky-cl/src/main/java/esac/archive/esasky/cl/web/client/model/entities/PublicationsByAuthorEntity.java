package esac.archive.esasky.cl.web.client.model.entities;

import java.util.Set;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.Image;

import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.PublicationsDescriptor;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.AladinShape;
import esac.archive.esasky.cl.web.client.model.ShapeId;
import esac.archive.esasky.cl.web.client.model.TapRowList;
import esac.archive.esasky.cl.web.client.query.TAPPublicationsService;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.view.resultspanel.GeneralJavaScriptObject;
import esac.archive.esasky.cl.web.client.view.resultspanel.ITablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.PublicationsTablePanel;

public class PublicationsByAuthorEntity extends ExtTapEntity {

    public PublicationsByAuthorEntity(PublicationsDescriptor descriptor,
            CountStatus countStatus, SkyViewPosition skyViewPosition,
            String esaSkyUniqId) {
        super(descriptor, countStatus, skyViewPosition, esaSkyUniqId, TAPPublicationsService.getInstance());
    }

    @Override
    public ITablePanel createTablePanel() {
        tablePanel = new PublicationsTablePanel(getTabLabel(), getEsaSkyUniqId(), this);
        return tablePanel;
    }
    
    @Override
    public void fetchData() {
        Scheduler.get().scheduleFinally(new ScheduledCommand() {
            
            @Override
            public void execute() {
                tablePanel.insertData(null, EsaSkyWebConstants.PUBLICATIONS_BY_AUTHOR_URL + "?AUTHOR=" + URL.encodeQueryString(getEsaSkyUniqId()) 
                + "&ROWS=" + getDescriptor().getAdsPublicationsMaxRows()); 
            }
        });
    }
    
    @Override
    public boolean isCustomizable() {
    	return false;
    }
    
    @Override
    public String getTabLabel() {
    	return getEsaSkyUniqId();
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
    public void addShapes(TapRowList rowList, GeneralJavaScriptObject javaScriptObject) {
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
    public void selectShapes(Set<ShapeId> shapes) {
    }
  
    @Override
    public void onShapeHover(AladinShape shape) {
    }
    
    @Override
    public void onShapeUnhover(AladinShape shape) {
    }
    
}
