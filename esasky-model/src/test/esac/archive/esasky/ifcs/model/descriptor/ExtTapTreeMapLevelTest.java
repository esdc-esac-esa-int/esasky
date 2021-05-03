package esac.archive.esasky.ifcs.model.descriptor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.LinkedList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ExtTapTreeMapLevelTest {
	
	private ExtTapTreeMapLevel level;
	
	@BeforeEach
	public void setup(){
		level = new ExtTapTreeMapLevel();
	}
		
	@Test
  	public void testColorGetSet() {
		String color = "blue";
		level.setColor(color);
		assertEquals(color, level.getColor());
	}
	
	@Test
	public void testSublevelGetSet() {
		HashMap<String, ExtTapTreeMapLevel> subLevelMap = new HashMap<>();
		level.setSubLevels(subLevelMap);
		assertEquals(subLevelMap, level.getSubLevels());
	}
	
	@Test
	public void testValuesGetSet() {
		LinkedList<String> values = new LinkedList<>();
		level.setValues(values);
		assertEquals(values, level.getValues());
	}
	
	@Test
	public void testWavelengthRangeGetSet() {
		LinkedList<String> wavelengthRange = new LinkedList<>();
		level.setWavelengthRange(wavelengthRange);
		assertEquals(wavelengthRange, level.getWavelengthRange());
	}
	
}
