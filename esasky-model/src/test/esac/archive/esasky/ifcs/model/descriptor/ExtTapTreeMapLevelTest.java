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
