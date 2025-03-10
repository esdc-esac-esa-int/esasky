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

package esac.archive.esasky.ifcs.model.coordinatesutils;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CoordinatesConversionTest {

    @Test
    public void convertPointGalacticToJ2000_returnsCorrectCoord() {
        Double raDegGal = 104.85;
        Double decDegGal = 68.561;

        double[] toBeTested = CoordinatesConversion.convertPointGalacticToJ2000(raDegGal,
                decDegGal);
        double[] toBeTestedInRawDouble = { toBeTested[0], toBeTested[1] };

        double[] expected = { 202.470257, 47.194821 };
        assertArrayEquals(expected, toBeTestedInRawDouble, 0.0001);
    }

    @Test
    public void convertPointEquatorialToGalactic() {
        Double myRADegEq = 202.470257;
        Double myDECDegEq = 47.194821;
        double[] toBeTested = CoordinatesConversion.convertPointEquatorialToGalactic(myRADegEq,
                myDECDegEq);
        double[] toBeTestedInRawDouble = { toBeTested[0], toBeTested[1] };
        double[] expecteds = { 104.85, 68.561 };
        assertArrayEquals(expecteds, toBeTestedInRawDouble, 0.0001);
    }

    @Test
    public void convertPointListGalacticToJ2000_whenUsingPointList_returnsCorrectCoord() {
        String input = "0.5000190396762169,0.5000000000000003,359.50198108695514,0.5000000000000003,359.5019812383684,-0.49799994953311816,0.5000188876548284,-0.49799994953311816";
        String actual = CoordinatesConversion.convertPointListGalacticToJ2000(input);
        String expected = "266.21626444268526,-28.248749623725896,266.21626444268526,-28.248749623725896,266.21626444268526,-28.248749623725896,266.21626444268526,-28.248749623725896";
    	assertThat(actual, equalToIgnoringCase(expected));
    }
    
    @Test
    public void getRaFromCoords_whenUsingVariousFormats_returnsCorrectRa() {
    	assertGetRaFromCoords("13 29 52.8 +47 11 41", "13 29 52.8");
    	assertGetRaFromCoords("  13 29 52.8    -47 11 41  ", "13 29 52.8");
    	assertGetRaFromCoords("13:29:52.8 +47:11:41", "13:29:52.8");
    	assertGetRaFromCoords("13:29:52.8 -47:11:41", "13:29:52.8");
    	assertGetRaFromCoords("202.4702 +47.1947", "202.4702");
    	assertGetRaFromCoords("202.4702 -47.1947", "202.4702");
    	assertGetRaFromCoords("+202.4702 -47.1947", "202.4702");
    	assertGetRaFromCoords("  202.4702     -47.1947  ", "202.4702");
    }

    private void assertGetRaFromCoords(String inputCoords, String expected){
    	assertThat(CoordinatesConversion.getRaFromCoords(inputCoords), equalToIgnoringCase(expected));
    }
 
    @Test
    public void getDecFromCoords_whenUsingVariousFormats_returnsCorrectDec() {
    	assertGetDecFromCoords("13 29 52.8 +47 11 41", "+47 11 41");
    	assertGetDecFromCoords("13 29 52.8 -47 11 41", "-47 11 41");
    	assertGetDecFromCoords("  13 29 52.8    -47 11 41  ", "-47 11 41");
    	assertGetDecFromCoords("13:29:52.8 +47:11:41", "+47:11:41");
    	assertGetDecFromCoords("13:29:52.8 -47:11:41", "-47:11:41");
    	assertGetDecFromCoords("202.4702 +47.1947", "+47.1947");
    	assertGetDecFromCoords("202.4702 -47.1947", "-47.1947");
    	assertGetDecFromCoords("+202.4702 -47.1947", "-47.1947");
    	assertGetDecFromCoords("  202.4702     -47.1947  ", "-47.1947");
    }

    private void assertGetDecFromCoords(String inputCoords, String expected){
    	assertThat(CoordinatesConversion.getDecFromCoords(inputCoords), equalToIgnoringCase(expected));
    }
     
    @Test
    public void convertEquatorialRAhhmmssToDecimal_whenUsingVariousFormats_returnsCorrectDecimal() {
    	assertConvertEquatorialRAhhmmssToDecimal("13:29:52.8", 202.47);
    	assertConvertEquatorialRAhhmmssToDecimal("13 29 52.8", 202.47);
    	assertConvertEquatorialRAhhmmssToDecimal("20 54 05.689", 313.5237);
    	assertConvertEquatorialRAhhmmssToDecimal("20", 300.0);
    	assertConvertEquatorialRAhhmmssToDecimal("13:29:52.8", 202.47);
    }
    
    private void assertConvertEquatorialRAhhmmssToDecimal(String inputCoords, Double expected){
    	Double actual = CoordinatesConversion.convertEquatorialRAhhmmssToDecimal(inputCoords);
    	assertThat(actual, is(closeTo(expected, 0.0001)));
    }

//    @Test
//    public void convertGalacticRADMStoDecimal_whenUsingVariousFormats_returnsCorrectDecimal() {
//    	convertGalacticRADMStoDecimal("13:29:52.8", 13.49800000);
//    	convertGalacticRADMStoDecimal("13 29 52.8", 13.49800000);
//    	convertGalacticRADMStoDecimal("20 54 05.689", 20.90158028);
//    	convertGalacticRADMStoDecimal("20", 20.0);
//    	convertGalacticRADMStoDecimal("-20 54 05.689", 339.09841972);
//    }
    
//    private void convertGalacticRADMStoDecimal(String inputCoords, Double expected){
//    	Double actual = CoordinatesConversion.convertGalacticRAdddmmssToDecimal(inputCoords);
//    	assertThat(actual, is(closeTo(expected, 0.0001)));
//    }

    @Test
    public void convertDECDMStoDecimal_whenUsingVariousFormats_returnsCorrectDecimal() {
        assertConvertDECDMStoDecimal("-47:11:41", -47.1947);
        assertConvertDECDMStoDecimal("-47 11 41", -47.1947);
    }
    
    private void assertConvertDECDMStoDecimal(String input, Double expected){
        Double actual = CoordinatesConversion.convertDECddmmssToDecimal(input);
    	assertThat(actual, is(closeTo(expected, 0.0001)));
    }
}
