package esac.archive.esasky.cl.web.client.model.entities;


import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.Image;

import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.AladinShape;
import esac.archive.esasky.cl.web.client.query.TAPPublicationsService;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.cl.web.client.view.resultspanel.ITablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.PublicationsTablePanel;

public class PublicationsByAuthorEntity extends EsaSkyEntity {

    private CommonTapDescriptor publicationsDescriptor;
    public PublicationsByAuthorEntity(CommonTapDescriptor descriptor,
                                      CountStatus countStatus, SkyViewPosition skyViewPosition,
                                      String esaSkyUniqId) {
        super(descriptor, countStatus, skyViewPosition, esaSkyUniqId, TAPPublicationsService.getInstance());
        this.publicationsDescriptor = descriptor; 
    }

    @Override
    public ITablePanel createTablePanel() {
        tablePanel = new PublicationsTablePanel(getTabLabel(), getId(), this);
        return tablePanel;
    }
    
    @Override
    public void fetchData() {
        Scheduler.get().scheduleFinally(new ScheduledCommand() {
            
            @Override
            public void execute() {
                // TODO: FIX
//                tablePanel.insertData(EsaSkyWebConstants.PUBLICATIONS_BY_AUTHOR_URL + "?AUTHOR=" + URL.encodeQueryString(getEsaSkyUniqId())
//                + "&ROWS=" + publicationsDescriptor.getAdsPublicationsMaxRows());
            }
        });
    }
    
    @Override
    public boolean isCustomizable() {
    	return false;
    }
    
    @Override
    public String getTabLabel() {
    	return getId();
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
