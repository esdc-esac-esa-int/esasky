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
