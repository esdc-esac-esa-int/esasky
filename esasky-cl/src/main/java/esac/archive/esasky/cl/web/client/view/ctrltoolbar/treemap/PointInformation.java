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
	
}