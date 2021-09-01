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
