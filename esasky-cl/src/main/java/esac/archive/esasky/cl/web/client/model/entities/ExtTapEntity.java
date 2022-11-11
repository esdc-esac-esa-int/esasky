package esac.archive.esasky.cl.web.client.model.entities;

import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.*;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.query.AbstractTAPService;
import esac.archive.esasky.cl.web.client.status.CountStatus;

public class ExtTapEntity extends EsaSkyEntity {

    public ExtTapEntity(CommonTapDescriptor descriptor, CountStatus countStatus,
                        SkyViewPosition skyViewPosition, String esaSkyUniqId,
                        AbstractTAPService metadataService) {
    	super(descriptor, countStatus, skyViewPosition, esaSkyUniqId, metadataService);
    }

    @Override
    public void fetchData() {
    	if(hasReachedFovLimit()) {
    		String text = TextMgr.getInstance().getText("treeMap_large_fov");
//    		text = text.replace("$fov_limit$", Double.toString(descriptor.getFovLimit())); // TODO: FIX
    		tablePanel.setEmptyTable(text);
	    } else {
	        super.fetchData();
	    }
    }
    
    public boolean hasReachedFovLimit() {
        return true;
//        return CoordinateUtils.getCenterCoordinateInJ2000().getFov() > descriptor.getFovLimit(); // TODO: FIX
    }
    
    @Override
	public String getHelpText() {
//    	ExtTapDescriptor parent = ((ExtTapDescriptor) getDescriptor()).getLastParent();
//    	return TextMgr.getInstance().getText("resultsPresenter_helpDescription_" + parent.getDescriptorId());
        // TODO: FIX
        return "test";
    }
    
    @Override
    public String getTabLabel() {
        return getDescriptor().getShortNameColumn();
    }
}
