package esac.archive.esasky.cl.web.client.model.entities;

import esac.archive.absi.modules.cl.aladinlite.widget.client.model.SearchArea;
import esac.archive.esasky.cl.web.client.repository.DescriptorRepository;
import esac.archive.esasky.cl.web.client.utility.CoordinateUtils;
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
    		text = text.replace("$fov_limit$", Double.toString(descriptor.getFovLimit()));
    		tablePanel.setEmptyTable(text);
	    } else {
	        super.fetchData();
	    }
    }
    
    public boolean hasReachedFovLimit() {
        if (descriptor.isFovLimitDisabled()) {
            return false;
        }
        double fov = CoordinateUtils.getCenterCoordinateInJ2000().getFov();
        SearchArea searchArea = DescriptorRepository.getInstance().getSearchArea();
        return DescriptorRepository.tapAreaTooLargeForExternal(fov, searchArea, descriptor.getFovLimit());
    }
    
    @Override
	public String getHelpText() {
        if (descriptor.getDescription() != null) {
            return descriptor.getDescription();
        } else {
            return TextMgr.getInstance().getText("resultsPresenter_helpDescription_"
                    + getDescriptor().getCategory() + "_" + getDescriptor().getMission());
        }
    }

    @Override
    public String getHelpTitle() {
        if (descriptor.isCustom()) {
            return descriptor.getMission();
        } else {
            return super.getHelpTitle();
        }
    }

    @Override
    public String getTabLabel() {
        return getDescriptor().getShortName();
    }
}
