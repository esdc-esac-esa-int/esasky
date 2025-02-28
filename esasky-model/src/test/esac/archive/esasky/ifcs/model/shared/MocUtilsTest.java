/*
ESASky
Copyright (C) 2025 European Space Agency

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

package esac.archive.esasky.ifcs.model.shared;


import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class MocUtilsTest {

	
	@Test
    public void testAsciiStringToJsonString() {
		String asciiString = "3/3-6 10 12 4/24-28 120 5/1 3";
	 	String jsonString = EsaSkyMocUtility.asciiStringToJsonString(asciiString);
	 	assertEquals("{3:[3,4,5,6,10,12],4:[24,25,26,27,28,120],5:[1,3]}", jsonString);
	 }
	
	@Test
	public void testAsciiStringToRangeTree() {
		String asciiString = "3/3-6 10 12 4/24-28 120 5/1 3";
		RangeTree tree = EsaSkyMocUtility.asciiStringToRangeTree(asciiString, 5);
		assertEquals("1,3,48-115,160-175,192-207,480-483", tree.toString());
	}

}
