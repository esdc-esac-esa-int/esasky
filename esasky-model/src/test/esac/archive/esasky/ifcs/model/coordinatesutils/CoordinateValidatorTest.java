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

package esac.archive.esasky.ifcs.model.coordinatesutils;

import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinateValidator.SearchInputType;

public class CoordinateValidatorTest {

    @Test
    public void checkInputType_J2000() {
    	assertInputType("20 54 05.689 +37", SearchInputType.SPACE_RA_HHMMSS_DEC_DEG, CoordinatesFrame.J2000);
    	assertInputType("13:10:12.1 -88:12:1.123", SearchInputType.COLUMN_RA_HHMMSS_DEC_DDMMSS, CoordinatesFrame.J2000);
    	assertInputType("13:10:12.1 -88.123", SearchInputType.COLUMN_RA_HHMMSS_DEC_DEG, CoordinatesFrame.J2000);
    	assertInputType("223:10:12.1 -88 12 1.123", SearchInputType.NOT_VALID, CoordinatesFrame.J2000);
    	assertInputType("44 12 12 +120 12 12", SearchInputType.NOT_VALID, CoordinatesFrame.J2000);
    	assertInputType("223.10 -88.123", SearchInputType.RA_DEG_DEC_DEG, CoordinatesFrame.J2000);
    	assertInputType("12 10 12.1 -88.123", SearchInputType.SPACE_RA_HHMMSS_DEC_DEG, CoordinatesFrame.J2000);
    	assertInputType("12 10 12.1 -88 12 1.123", SearchInputType.SPACE_RA_HHMMSS_DEC_DDMMSS, CoordinatesFrame.J2000);
    	assertInputType("004535-731903", SearchInputType.NO_SPACE_RA_HHMMSS_DEC_DDMMSS, CoordinatesFrame.J2000);
    }
    
//    @Test
//    public void checkInputType_Galactic() {
//    	assertInputType("223:10:12.1 -88:12:1.123", SearchInputType.COLUMN_RAdddmmssDECddmmss, CoordinatesFrame.GALACTIC);
//    	assertInputType("223:10:12.1 -88.123", SearchInputType.COLUMN_RAdddmmssDECdeg, CoordinatesFrame.GALACTIC);
//    	assertInputType("223.10 -88:12:1.123", SearchInputType.COLUMN_RAdegDECddmmss, CoordinatesFrame.GALACTIC);
//    	assertInputType("444 12 12 +12 12 12", SearchInputType.NOT_VALID, CoordinatesFrame.GALACTIC);
//    	assertInputType("223.10 -88.123", SearchInputType.RAdegDECdeg, CoordinatesFrame.GALACTIC);
//    	assertInputType("223 10 12.1 -88 12 1.123", SearchInputType.SPACE_RAdddmmssDECddmmss, CoordinatesFrame.GALACTIC);
//    	assertInputType("223 10 12.1 -88.123", SearchInputType.SPACE_RAdddmmssDECdeg, CoordinatesFrame.GALACTIC);
//    	assertInputType("223.10 -88 12 1.123", SearchInputType.SPACE_RAdegDECddmmss, CoordinatesFrame.GALACTIC);
//    }
    
    @Test
    public void checkInputType_Target() {
    	assertInputType("[GMC2001] 10-45", SearchInputType.TARGET, null);
    }

	private void assertInputType(String inputCoords, SearchInputType expected, CoordinatesFrame cooFrame){
		SearchInputType actual = CoordinateValidator.checkInputType(new ServerRegexClass(), inputCoords, cooFrame);
        assertThat(actual, is(equalTo(expected)));
	}
	
    @Test
    public void isRaFormatValid_whenValid_returnsValid() {
        assertThat(CoordinateValidator.isRaFormatValid(new ServerRegexClass(), "223.10"), is(true));
        assertThat(CoordinateValidator.isRaFormatValid(new ServerRegexClass(), "12 10 12.1"), is(true));
        assertThat(CoordinateValidator.isRaFormatValid(new ServerRegexClass(), "12:10:12.1"), is(true));
    }
    
    @Test
    public void isRaFormatValid_whenInvalid_returnsInvalid() {
    	assertThat(CoordinateValidator.isRaFormatValid(new ServerRegexClass(), "44 12 12.123"), is(false));
    	assertThat(CoordinateValidator.isRaFormatValid(new ServerRegexClass(), "44:12:12.123"), is(false));
    	assertThat(CoordinateValidator.isRaFormatValid(new ServerRegexClass(), "[GMC2001] 10-45"), is(false));
    	assertThat(CoordinateValidator.isRaFormatValid(new ServerRegexClass(), "44 12:12.123"), is(false));
    	assertThat(CoordinateValidator.isRaFormatValid(new ServerRegexClass(), "444 12 12.123"), is(false));
    }
    
    @Test
    public void isDecFormatValid_whenValid_returnsValid() {
        assertThat(CoordinateValidator.isDecFormatValid(new ServerRegexClass(), "12 10 12.1"), is(true));
        assertThat(CoordinateValidator.isDecFormatValid(new ServerRegexClass(), "12:10:12.1"), is(true));
        assertThat(CoordinateValidator.isDecFormatValid(new ServerRegexClass(), "44 12 12.123"), is(true));
        assertThat(CoordinateValidator.isDecFormatValid(new ServerRegexClass(), "44:12:12.123"), is(true));
        assertThat(CoordinateValidator.isDecFormatValid(new ServerRegexClass(), "89 12 12.123"), is(true));
        assertThat(CoordinateValidator.isDecFormatValid(new ServerRegexClass(), "44 12 12.123"), is(true));
    }
     
    @Test
    public void isDecFormatValid_whenInvalid_returnsInvalid() {
        assertThat(CoordinateValidator.isDecFormatValid(new ServerRegexClass(), "223.10"), is(false));
        assertThat(CoordinateValidator.isDecFormatValid(new ServerRegexClass(), "[GMC2001] 10-45"), is(false));
        assertThat(CoordinateValidator.isDecFormatValid(new ServerRegexClass(), "91 12 12.123"), is(false));
        assertThat(CoordinateValidator.isDecFormatValid(new ServerRegexClass(), "444 12 12.123"), is(false));
    }

    @Test
    public void comparison_returns_correctly() {
    	SkyViewPosition base = new SkyViewPosition(new Coordinate(10.0, 1.0), 1.0);
    	SkyViewPosition comparison = new SkyViewPosition(new Coordinate(10.0, 1.0), 1.0);
    	assertThat(base.compare(comparison), is(true));
    	comparison = new SkyViewPosition(new Coordinate(10.1, 1.0), 1.0);
    	assertThat(base.compare(comparison), is(false));
    	comparison = new SkyViewPosition(new Coordinate(10.0, 1.1), 1.0);
    	assertThat(base.compare(comparison), is(false));
    	comparison = new SkyViewPosition(new Coordinate(10.0, 1.0), 1.1);
    	assertThat(base.compare(comparison), is(false));

    	base = new SkyViewPosition(new Coordinate(10.0, 1.0), 10.0);
    	comparison = new SkyViewPosition(new Coordinate(10.1, 1.0), 10.0);
    	assertThat(base.compare(comparison,0.02), is(true));
    	comparison = new SkyViewPosition(new Coordinate(10.3, 1.0), 10.0);
    	assertThat(base.compare(comparison,0.02), is(false));
    	comparison = new SkyViewPosition(new Coordinate(10.0, 1.0), 10.1);
    	assertThat(base.compare(comparison,0.02), is(true));
    	comparison = new SkyViewPosition(new Coordinate(10.0, 1.0), 10.5);
    	assertThat(base.compare(comparison,0.01), is(false));
    }

	@Test
	public void distance_returns_correctly() {
		Coordinate base = new  Coordinate(10.0, 1.0);

		Coordinate comparison = new Coordinate(10.0, 5.0);
		assertThat(base.distance(comparison), is(4.0));

		comparison = new Coordinate(5.0, 1.0);
		assertThat(base.distance(comparison), is(5.0));

		comparison = new Coordinate(5.0, 5.0);
		assertThat(base.distance(comparison), is(6.4031242374328485));

		comparison = new Coordinate(10.0, -1.0);
		assertThat(base.distance(comparison), is(2.0));

		comparison = new Coordinate(-10.0, -1.0);
		assertThat(base.distance(comparison), is(20.09975124224178));
	}
}
