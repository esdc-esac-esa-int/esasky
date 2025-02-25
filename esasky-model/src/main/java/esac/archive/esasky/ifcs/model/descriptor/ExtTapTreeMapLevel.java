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

import java.util.HashMap;
import java.util.LinkedList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class ExtTapTreeMapLevel{
	
	private HashMap<String, ExtTapTreeMapLevel> subLevels;
	private LinkedList<String> values;
	private LinkedList<String> wavelengthRange;
	String color;
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
	private boolean hasDatalinkArchiveUrl;
	
	
	public HashMap<String, ExtTapTreeMapLevel> getSubLevels() {
		return subLevels;
	}
	public LinkedList<String> getValues() {
		return values;
	}
	public void setSubLevels(HashMap<String, ExtTapTreeMapLevel> subLevels) {
		this.subLevels = subLevels;
	}
	public void setValues(LinkedList<String> values) {
		this.values = values;
	}
	public LinkedList<String> getWavelengthRange() {
		return wavelengthRange;
	}
	public String getColor() {
		return color;
	}
	public boolean getHasDatalinkArchiveUrl() {
	    return hasDatalinkArchiveUrl;
	}
	public void setWavelengthRange(LinkedList<String> wavelengthRange) {
		this.wavelengthRange = wavelengthRange;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public void setHasDatalinkArchiveUrl(boolean hasDatalinkArchiveUrl) {
	    this.hasDatalinkArchiveUrl = hasDatalinkArchiveUrl;
	}
	
	
	
	
}
