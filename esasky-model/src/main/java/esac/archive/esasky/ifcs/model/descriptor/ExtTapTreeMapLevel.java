package esac.archive.esasky.ifcs.model.descriptor;

import java.util.HashMap;
import java.util.LinkedList;

public class ExtTapTreeMapLevel{
	
	private HashMap<String, ExtTapTreeMapLevel> subLevels;
	private LinkedList<String> values;
	private LinkedList<String> wavelengthRange;
	String color;
	
	
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
	public void setWavelengthRange(LinkedList<String> wavelengthRange) {
		this.wavelengthRange = wavelengthRange;
	}
	public void setColor(String color) {
		this.color = color;
	}
	
	
	
	
}
