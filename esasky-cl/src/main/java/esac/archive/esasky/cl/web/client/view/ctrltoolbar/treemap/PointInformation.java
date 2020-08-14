package esac.archive.esasky.cl.web.client.view.ctrltoolbar.treemap;

import esac.archive.esasky.ifcs.model.descriptor.ExtTapDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;
import esac.archive.esasky.cl.web.client.model.entities.EntityContext;
import esac.archive.esasky.cl.web.client.utility.WavelengthUtils;

public class PointInformation {
    public String longName;
    public String missionName;
    public int count;
    public IDescriptor descriptor;
    public String credits;
    public EntityContext context;
    
    private String wavelengthShortName;
    private String wavelengthLongName;
    private String type;
    private String collectionName;
    
    private String parentColor;
    
    
    public PointInformation(String longName, String missionName, String credits,
            int count, IDescriptor descriptor, EntityContext context) {
        this.longName = longName;
        this.missionName = missionName;
		this.credits = credits;
		this.count = count;
		this.descriptor = descriptor;
		this.context = context;
		
		if(descriptor.getWavelengths() != null && descriptor.getWavelengths().size() > 0) {
			this.wavelengthShortName = WavelengthUtils.getShortName(descriptor);
			this.wavelengthLongName = WavelengthUtils.getLongName(descriptor);
		}
		
		if(context == EntityContext.EXT_TAP) {
			String type = ((ExtTapDescriptor) descriptor).getTreeMapType();
			if(type != null) {
				this.type = type;
			}else {
				this.type = EsaSkyConstants.TREEMAP_TYPE_SERVICE;
			}
		}else {
			this.type = EsaSkyConstants.TREEMAP_TYPE_MISSION;
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

	public String getType() {
		if(type != null) {
			return type;
		}
		return "";
	}

	public void setType(String type) {
		this.type = type;
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
	
}