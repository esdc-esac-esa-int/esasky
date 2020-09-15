package esac.archive.esasky.ifcs.model.descriptor;

import java.util.List;

public class CustomTreeMapDescriptor{
	
	private List<IDescriptor> missionDescriptors;
	private String description;
	private String name;
	private String iconText;
	private OnMissionClicked onMissionClicked;
	
	public interface OnMissionClicked{
		public void onMissionClicked(String mission);
	}
	
	public CustomTreeMapDescriptor(String name, String description, String iconText, List<IDescriptor> missionDescriptors) {
		this.name = name;
		this.description = description;
		this.iconText = iconText;
		this.missionDescriptors = missionDescriptors;
	}
	
	public List<IDescriptor> getMissionDescriptors() {
		return missionDescriptors;
	}
	public void setMissionDescriptors(List<IDescriptor> missionDescriptors) {
		this.missionDescriptors = missionDescriptors;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getIconText() {
		return iconText;
	}
	public void setIconText(String iconText) {
		this.iconText = iconText;
	}

	public OnMissionClicked getOnMissionClicked() {
		return onMissionClicked;
	}

	public void setOnMissionClicked(OnMissionClicked onMissionClicked) {
		this.onMissionClicked = onMissionClicked;
	}
	
}
