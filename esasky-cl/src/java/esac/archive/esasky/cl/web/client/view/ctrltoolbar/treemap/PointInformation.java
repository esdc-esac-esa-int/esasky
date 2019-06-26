package esac.archive.esasky.cl.web.client.view.ctrltoolbar.treemap;

import java.util.List;

import esac.archive.ammi.ifcs.model.descriptor.IDescriptor;
import esac.archive.ammi.ifcs.model.descriptor.WavelenthDescriptor;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;

public class PointInformation {
	public String longName;
	public String missionName;
	public int count;
	public IDescriptor descriptor;
	public String wavelengthShortName;
	public String wavelengthLongName;
	public String credits;
	
	public PointInformation(String longName, String missionName, String credits,  int count, IDescriptor descriptor) {
		this.longName = longName;
		this.missionName = missionName;
		this.credits = credits;
		this.count = count;
		this.descriptor = descriptor;
		
		List<WavelenthDescriptor> wavelengthDescriptors = descriptor.getWavelengths();
		final WavelenthDescriptor firstWlDesc = wavelengthDescriptors.get(0);
		this.wavelengthShortName = TextMgr.getInstance().getText(firstWlDesc.getPrefix() + firstWlDesc.getShortName());
		this.wavelengthLongName = TextMgr.getInstance().getText(firstWlDesc.getPrefix() + firstWlDesc.getLongName());
		if(descriptor.getWavelengths().size() > 1) {
			final WavelenthDescriptor lastWlDesc = wavelengthDescriptors.get(descriptor.getWavelengths().size() - 1);
			final String toText = TextMgr.getInstance().getText("PointInformation_to");
			this.wavelengthShortName = toText.replace("$FROM$", this.wavelengthShortName)
			                                 .replace("$TO$", TextMgr.getInstance().getText(lastWlDesc.getPrefix() + lastWlDesc.getShortName()));
			this.wavelengthLongName = toText.replace("$FROM$", this.wavelengthLongName)
			                                .replace("$TO$", TextMgr.getInstance().getText(lastWlDesc.getPrefix() + lastWlDesc.getLongName()));
		}
	}
}