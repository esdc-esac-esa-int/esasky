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

package esac.archive.esasky.cl.web.client.view.ctrltoolbar.treemap;

import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;
import esac.archive.esasky.cl.web.client.model.entities.EntityContext;
import esac.archive.esasky.cl.web.client.utility.WavelengthUtils;

public class PointInformation {
    public final String longName;
    public final String missionName;
    private int count;
    public final CommonTapDescriptor descriptor;
    public final String credits;
    public final EntityContext context;
    
    private String wavelengthShortName;
    private String wavelengthLongName;
    private int treemapLevel;
    private String collectionName;
    
    private String parentColor;
    
    
    public PointInformation(String longName, String missionName, String credits,
							int count, CommonTapDescriptor descriptor, EntityContext context) {
        this.longName = longName;
        this.missionName = missionName;
		this.credits = credits;
		this.count = count;
		this.descriptor = descriptor;
		this.context = context;

		WavelengthUtils.WavelengthName wln = WavelengthUtils.getWavelengthNameFromValue(descriptor.getWavelengthCenter());
		if (wln != null) {
			this.wavelengthShortName = WavelengthUtils.getShortName(descriptor);
			this.wavelengthLongName = WavelengthUtils.getLongName(descriptor);
		}
		
		if(context == EntityContext.EXT_TAP) {
			int treemapLevel = descriptor.getLevel();
			if(treemapLevel != -1) {
				this.treemapLevel = treemapLevel;
			}else {
				this.treemapLevel = EsaSkyConstants.TREEMAP_LEVEL_SERVICE;
			}
		}else {
			this.treemapLevel = EsaSkyConstants.TREEMAP_LEVEL_1;
		}
	}

	public String getWavelengthShortName() {
		if(wavelengthShortName != null) {
			return wavelengthShortName;
		}
		return "";
	}

	public String getWavelengthLongName() {
		if(wavelengthLongName != null) {
			return wavelengthLongName;
		}
		return "";
	}

	public String getCollectionName() {
		if(collectionName != null) {
			return collectionName;
		}
		return "";
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	public String getParentColor() {
		return parentColor;
	}

	public void setParentColor(String parentColor) {
		this.parentColor = parentColor;
	}

	public int getTreemapLevel() {
		return treemapLevel;
	}

	public void setTreemapLevel(int treemapLevel) {
		this.treemapLevel = treemapLevel;
	}
	
	public int getCount() {
	    return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
}