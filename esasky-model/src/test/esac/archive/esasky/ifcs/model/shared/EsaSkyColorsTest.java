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


import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class EsaSkyColorsTest {

	
	 @Test
		    public void testWavelengthToIndex() {
		 	assertEquals(ESASkyColors.wavelengthToIndex(0), 22);
	        assertEquals(ESASkyColors.wavelengthToIndex(1.1), 21);
	        assertEquals(ESASkyColors.wavelengthToIndex(-1.1), 21);
	        assertEquals(ESASkyColors.wavelengthToIndex(6.5), 10);
	        assertEquals(ESASkyColors.wavelengthToIndex(7), 8);
	    }
	 
	 @Test
	    public void testIndexToWavelength() {
		 	assertThat(ESASkyColors.indexToWaveLength(11), is(closeTo(6.3, 0.01)));
		 	assertThat(ESASkyColors.valueToWaveLength(11.5), is(closeTo(6.25, 0.01)));
		 	assertThat(ESASkyColors.indexToWaveLength(16), is(closeTo(5, 0.1)));
	    }
}
