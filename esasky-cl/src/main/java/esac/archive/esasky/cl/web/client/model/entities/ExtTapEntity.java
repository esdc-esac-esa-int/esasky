package esac.archive.esasky.cl.web.client.model.entities;

import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.query.AbstractTAPService;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.cl.web.client.utility.CoordinateUtils;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;

public class ExtTapEntity extends EsaSkyEntity {

    public ExtTapEntity(IDescriptor descriptor, CountStatus countStatus,
            SkyViewPosition skyViewPosition, String esaSkyUniqId, 
            AbstractTAPService metadataService) {
    	super(descriptor, countStatus, skyViewPosition, esaSkyUniqId, metadataService);
    }

    @Override
    public void fetchData() {
    	if(hasReachedFovLimit()) {
    		String text = TextMgr.getInstance().getText("exttap_too_large_fov");
    		text = text.replace("$fovLimit$", Double.toString(descriptor.getFovLimit()));
    		tablePanel.setPlaceholderText(text);
	    } else {
	        super.fetchData();
	    }
    }
    
    public boolean hasReachedFovLimit() {
        return CoordinateUtils.getCenterCoordinateInJ2000().getFov() > EsaSkyWebConstants.EXTTAP_FOV_LIMIT;
    }
}
