package esac.archive.esasky.ifcs.model.coordinatesutils;

import static org.junit.Assert.*;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class CoordinatesParserTest {

	@Test
	public void convertCoordsToDegrees_J2000ToJ2000_returnsCorrectDegrees() {
		assertCoordToDegrees("20 +37 01 17.38", new Double[]{300.0000, 37.0215}, CoordinatesFrame.J2000,
				CoordinatesFrame.J2000);
		assertCoordToDegrees("20 54 05.689 +37", new Double[]{313.52370417, 37.0}, CoordinatesFrame.J2000,
				CoordinatesFrame.J2000);
	}
	
//	@Test
//	public void convertCoordsToDegrees_GalacticToJ2000_returnsCorrectDegrees() {
//		assertCoordToDegrees("20 +37 01 17.38", new Double[]{244.4329, 6.6343}, CoordinatesFrame.GALACTIC,
//				CoordinatesFrame.J2000);
//	}		
//
//	@Test
//	public void convertCoordsToDegrees_J2000ToGalactic_returnsCorrectDegrees() {
//		assertCoordToDegrees("20 +37 01 17.38", new Double[]{73.06300281673, 3.734690125043}, CoordinatesFrame.J2000,
//				CoordinatesFrame.GALACTIC);
//		assertCoordToDegrees("20 54 05.689 +37", new Double[]{79.32998838, -5.05319742}, CoordinatesFrame.J2000,
//				CoordinatesFrame.GALACTIC);
//	}
//	
//	@Test
//	public void convertCoordsToDegrees_GalacticToGalactic_returnsCorrectDegrees() {
//		assertCoordToDegrees("20 +37 01 17.38", new Double[]{20.0, 37.021494}, CoordinatesFrame.GALACTIC,
//				CoordinatesFrame.GALACTIC);
//		assertCoordToDegrees("20 54 05.689 +37", new Double[]{20.90158, 37.0}, CoordinatesFrame.GALACTIC,
//				CoordinatesFrame.GALACTIC);
//	}

	private void assertCoordToDegrees(String inputCoords, Double[] expected, CoordinatesFrame inputCooFrame,
            CoordinatesFrame outputCooFrame){
		double[] actual = CoordinatesParser.convertCoordsToDegrees(new ServerRegexClass(), inputCoords, inputCooFrame, outputCooFrame);

		assertThat(actual[0], is(closeTo(expected[0], 0.0001)));
		assertThat(actual[1], is(closeTo(expected[1], 0.0001)));
	}

	@Test
	public void parseCoords_J2000_returnsCorrectCoords() {
		assertParseCoords("20 +37 01 17.38", new Double[]{300.0000, 37.0215}, CoordinatesFrame.J2000);
		assertParseCoords("20 54 05.689 +37 01 17.38", new Double[]{313.5237, 37.0215}, CoordinatesFrame.J2000);
		assertParseCoords("20 54 05.689 +37", new Double[]{313.5237, 37.0000}, CoordinatesFrame.J2000);
		assertParseCoords("20 54 05.689 +37 01 17.38", new Double[]{313.52370417, 37.02149444}, CoordinatesFrame.J2000);
		assertParseCoords("10-45:17:50", new Double[]{150.00000000, -45.29722222}, CoordinatesFrame.J2000);
		assertParseCoords("10:12:45.3-45:17:50", new Double[]{153.18875000, -45.29722222}, CoordinatesFrame.J2000);
		assertParseCoords("10:12:45.3-45", new Double[]{153.18875000, -45.0}, CoordinatesFrame.J2000);
		assertParseCoords("10:12:45.3-45:17:50.876", new Double[]{153.18875000, -45.29746556}, CoordinatesFrame.J2000);
		assertParseCoords("0.0 +0.0", new Double[]{0.0, 0.0}, CoordinatesFrame.J2000);
		assertParseCoords("214 +24", new Double[]{214.0, 24.0}, CoordinatesFrame.J2000);
		assertParseCoords("350.123456 -17.33333", new Double[]{350.123456, -17.33333}, CoordinatesFrame.J2000);
		assertParseCoords("12h 12m 12s -89d 01m 01s", new Double[]{183.05, -89.01694444}, CoordinatesFrame.J2000);
		assertParseCoords("12h 12' 12'' -89d 01m 01s", new Double[]{183.05, -89.01694444}, CoordinatesFrame.J2000);
		assertParseCoords("12h 12\" -89d", new Double[]{180.05, -89.0}, CoordinatesFrame.J2000);
		assertParseCoords("12h 12' 12'' -89d 01' 01''", new Double[]{183.05, -89.01694444}, CoordinatesFrame.J2000);
		assertParseCoords("12h 12' +89d 01' 01s", new Double[]{183.0, 89.0169444}, CoordinatesFrame.J2000);
		assertParseCoords("12h 12'' +89d 01' 01s", new Double[]{180.05, 89.0169444}, CoordinatesFrame.J2000);
		assertParseCoords("12h +89d", new Double[]{180.0, 89.0}, CoordinatesFrame.J2000);
	}
	
	@Test
	public void parseCoords_Galactic_returnsCorrectCoords() {
//		assertParseCoords("20 +37 01 17.38", new Double[]{244.4329, 6.6343}, CoordinatesFrame.GALACTIC);
//		assertParseCoords("20 54 05.689 +37 01 17.38", new Double[]{244.7835, 7.2645}, CoordinatesFrame.GALACTIC);
//		assertParseCoords("20 54 05.689 +37", new Double[]{244.80255584, 7.25417502}, CoordinatesFrame.GALACTIC);
//		assertParseCoords("20 54 05.689 +37 01 17.38", new Double[]{244.78354254, 7.26448425}, CoordinatesFrame.GALACTIC);
//		assertParseCoords("10-45:17:50", new Double[]{320.93915441, -34.61273199}, CoordinatesFrame.GALACTIC);
//		assertParseCoords("10:12:45.3-45:17:50", new Double[]{320.95576068, -34.46382150}, CoordinatesFrame.GALACTIC);
//		assertParseCoords("10:12:45.3-45", new Double[]{320.59693857, -34.43574051}, CoordinatesFrame.GALACTIC);
//		assertParseCoords("10:12:45.3-45:17:50.876", new Double[]{320.95605454, -34.46384406}, CoordinatesFrame.GALACTIC);
		assertParseCoords("0.0 +0.0", new Double[]{266.40508920, -28.93617470}, CoordinatesFrame.GALACTIC);
		assertParseCoords("214 +24", new Double[]{124.89839505, 9.80584280}, CoordinatesFrame.GALACTIC);
//		assertParseCoords("12d 12m 12s -89d 01m 01s", new Double[]{11.8232, -27.4724}, CoordinatesFrame.GALACTIC);
//		assertParseCoords("12d 12' 12'' -89d 01m 01s", new Double[]{11.8232, -27.4724}, CoordinatesFrame.GALACTIC);
//		assertParseCoords("12d -89d", new Double[]{11.80667, -27.48158}, CoordinatesFrame.GALACTIC);
//		//assertParseCoords("350.123456 -17.33333", new Double[]{280.35462047, -45.49402661}, CoordinatesFrame.GALACTIC);
	}
	
	private void assertParseCoords(String inputCoords, Double[] expected, CoordinatesFrame cooFrame) {
		double[] actual = CoordinatesParser.parseCoords(new ServerRegexClass(), inputCoords, cooFrame);
		
		assertThat(actual[0], is(closeTo(expected[0], 0.0001)));
		assertThat(actual[1], is(closeTo(expected[1], 0.0001)));
	}
}
