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

    private CommonTapDescriptor publicationsDescriptor;
    public PublicationsBySourceEntity(CommonTapDescriptor descriptor,
                                      CountStatus countStatus, SkyViewPosition skyViewPosition,
                                      String esaSkyUniqId, double ra, double dec, String bibcount) {
        super(descriptor, countStatus, skyViewPosition, esaSkyUniqId, TAPPublicationsService.getInstance(), 14, AladinLiteWrapper.getAladinLite().createImageMarker("images/publications_shape.png"));
        super.addShapes(getTableShapeInfo(ra, dec, bibcount, getEsaSkyUniqId()), null);
        this.publicationsDescriptor = descriptor;
    }

    @Override
    public ITablePanel createTablePanel() {
        tablePanel = new PublicationsTablePanel(getTabLabel(), getEsaSkyUniqId(), this);
        return tablePanel;
    }
    
    @Override
    public void fetchData() {
        Scheduler.get().scheduleFinally(() -> tablePanel.insertData(EsaSkyWebConstants.PUBLICATIONS_BY_SOURCE_URL + "?SOURCE=" + URL.encodeQueryString(getEsaSkyUniqId())
            + "&ROWS=" + 50000));
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
    public void addShapes(GeneralJavaScriptObject javaScriptObject, GeneralJavaScriptObject metadata) {
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
