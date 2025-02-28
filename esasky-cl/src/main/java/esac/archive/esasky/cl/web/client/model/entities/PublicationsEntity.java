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

package esac.archive.esasky.cl.web.client.model.entities;

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
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;

public class PublicationsEntity extends EsaSkyEntity {

    public PublicationsEntity(CommonTapDescriptor descriptor, CountStatus countStatus,
                              SkyViewPosition skyViewPosition, String esaSkyUniqId) {
        super(descriptor, countStatus, skyViewPosition, esaSkyUniqId, TAPPublicationsService.getInstance(), 14, AladinLiteWrapper.getAladinLite().createImageMarker("images/publications_shape.png"));
    }
	
    @Override
    public void onShapeSelection(AladinShape shape) {
        GeneralEntityInterface entity = EntityRepository.getInstance().getEntityByChild(shape.getSourceName());
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
