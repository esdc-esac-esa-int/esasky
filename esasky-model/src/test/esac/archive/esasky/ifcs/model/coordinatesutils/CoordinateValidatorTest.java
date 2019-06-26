package esac.archive.esasky.ifcs.model.coordinatesutils;

import static org.junit.Assert.*;
import org.junit.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinateValidator.SearchInputType;

public class CoordinateValidatorTest {

    @Test
    public void checkInputType_J2000() {
    	assertInputType("20 54 05.689 +37", SearchInputType.SPACE_RAhhmmssDECdeg, CoordinatesFrame.J2000);
    	assertInputType("13:10:12.1 -88:12:1.123", SearchInputType.COLUMN_RAhhmmssDECddmmss, CoordinatesFrame.J2000);
    	assertInputType("13:10:12.1 -88.123", SearchInputType.COLUMN_RAhhmmssDECdeg, CoordinatesFrame.J2000);
    	assertInputType("223:10:12.1 -88 12 1.123", SearchInputType.NOT_VALID, CoordinatesFrame.J2000);
    	assertInputType("44 12 12 +120 12 12", SearchInputType.NOT_VALID, CoordinatesFrame.J2000);
    	assertInputType("223.10 -88.123", SearchInputType.RAdegDECdeg, CoordinatesFrame.J2000);
    	assertInputType("12 10 12.1 -88.123", SearchInputType.SPACE_RAhhmmssDECdeg, CoordinatesFrame.J2000);
    	assertInputType("12 10 12.1 -88 12 1.123", SearchInputType.SPACE_RAhhmmssDECddmmss, CoordinatesFrame.J2000);
    }
    
    @Test
    public void checkInputType_Galactic() {
    	assertInputType("223:10:12.1 -88:12:1.123", SearchInputType.COLUMN_RAdddmmssDECddmmss, CoordinatesFrame.GALACTIC);
    	assertInputType("223:10:12.1 -88.123", SearchInputType.COLUMN_RAdddmmssDECdeg, CoordinatesFrame.GALACTIC);
    	assertInputType("223.10 -88:12:1.123", SearchInputType.COLUMN_RAdegDECddmmss, CoordinatesFrame.GALACTIC);
    	assertInputType("444 12 12 +12 12 12", SearchInputType.NOT_VALID, CoordinatesFrame.GALACTIC);
    	assertInputType("223.10 -88.123", SearchInputType.RAdegDECdeg, CoordinatesFrame.GALACTIC);
    	assertInputType("223 10 12.1 -88 12 1.123", SearchInputType.SPACE_RAdddmmssDECddmmss, CoordinatesFrame.GALACTIC);
    	assertInputType("223 10 12.1 -88.123", SearchInputType.SPACE_RAdddmmssDECdeg, CoordinatesFrame.GALACTIC);
    	assertInputType("223.10 -88 12 1.123", SearchInputType.SPACE_RAdegDECddmmss, CoordinatesFrame.GALACTIC);
    }
    
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
        assertThat(CoordinateValidator.isRaFormatValid(new ServerRegexClass(), "44 12 12.123"), is(true));
        assertThat(CoordinateValidator.isRaFormatValid(new ServerRegexClass(), "44:12:12.123"), is(true));
    }
    
    @Test
    public void isRaFormatValid_whenInvalid_returnsInvalid() {
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
}
