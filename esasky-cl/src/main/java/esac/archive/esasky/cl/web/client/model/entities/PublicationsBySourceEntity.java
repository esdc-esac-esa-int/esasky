package esac.archive.esasky.cl.web.client.model.entities;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.Image;

import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.PublicationsDescriptor;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.AladinShape;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.AddShapeTooltipEvent;
import esac.archive.esasky.cl.web.client.model.TapRowList;
import esac.archive.esasky.cl.web.client.query.TAPPublicationsService;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.view.allskypanel.PublicationTooltip;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.cl.web.client.view.resultspanel.ITablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.PublicationsTablePanel;

public class PublicationsBySourceEntity extends EsaSkyEntity {

    public PublicationsBySourceEntity(PublicationsDescriptor descriptor,
            CountStatus countStatus, SkyViewPosition skyViewPosition,
            String esaSkyUniqId, double ra, double dec, String bibcount) {
        super(descriptor, countStatus, skyViewPosition, esaSkyUniqId, TAPPublicationsService.getInstance(), 14, AladinLiteWrapper.getAladinLite().createImageMarker("images/publications_shape.png"));
        super.addShapes(null, getTableShapeInfo(ra, dec, bibcount, getEsaSkyUniqId()));
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
                tablePanel.insertData(null, EsaSkyWebConstants.PUBLICATIONS_BY_SOURCE_URL + "?SOURCE=" + URL.encodeQueryString(getEsaSkyUniqId()) 
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
