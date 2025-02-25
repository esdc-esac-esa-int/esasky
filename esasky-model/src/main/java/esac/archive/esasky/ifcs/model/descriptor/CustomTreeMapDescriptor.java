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

package esac.archive.esasky.ifcs.model.descriptor;

import java.util.List;

public class CustomTreeMapDescriptor{
	
	private List<CommonTapDescriptor> missionDescriptors;
	private String description;
	private String name;
	private String iconText;
	private OnMissionClicked onMissionClicked;
	
	public interface OnMissionClicked{
		public void onMissionClicked(String mission);
	}
	
	public CustomTreeMapDescriptor(String name, String description, String iconText, List<CommonTapDescriptor> missionDescriptors) {
		this.name = name;
		this.description = description;
		this.iconText = iconText;
		this.missionDescriptors = missionDescriptors;
	}
	
	public List<CommonTapDescriptor> getMissionDescriptors() {
		return missionDescriptors;
	}
	public void setMissionDescriptors(List<CommonTapDescriptor> missionDescriptors) {
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
