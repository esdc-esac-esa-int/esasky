package esac.archive.esasky.cl.web.client.model.entities;

import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.ITapDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.PublicationsDescriptor;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.AladinShape;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.AddShapeTooltipEvent;
import esac.archive.esasky.cl.web.client.event.AddTableEvent;
import esac.archive.esasky.cl.web.client.query.TAPPublicationsService;
import esac.archive.esasky.cl.web.client.repository.EntityRepository;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.SourceConstant;
import esac.archive.esasky.cl.web.client.view.allskypanel.PublicationTooltip;
import esac.archive.esasky.ifcs.model.descriptor.TapDescriptor;

public class PublicationsEntity extends EsaSkyEntity {

    public PublicationsEntity(CommonTapDescriptor descriptor, CountStatus countStatus,
                              SkyViewPosition skyViewPosition, String esaSkyUniqId) {
        super(descriptor, countStatus, skyViewPosition, esaSkyUniqId, TAPPublicationsService.getInstance(), 14, AladinLiteWrapper.getAladinLite().createImageMarker("images/publications_shape.png"));
    }
	
    @Override
    public void onShapeSelection(AladinShape shape) {
        GeneralEntityInterface entity = EntityRepository.getInstance().getEntity(shape.getSourceName());
        if(entity == null) {
            entity = EntityRepository.getInstance().createPublicationsBySourceEntity(shape.getDataDetailsByKey(SourceConstant.SOURCE_NAME), 
                    new Double(shape.getRa()), new Double(shape.getDec()), shape.getDataDetailsByKey("bibcount"));
            CommonEventBus.getEventBus().fireEvent(new AddTableEvent(entity));
        } else {
            entity.select();
        }
        deselectAllShapes();
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
    
    @Override
    public void onShapeDeselection(AladinShape shape) {}
}
