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
		assertEquals("1,3,48-112,160,192,480", tree.toString());
	}

}
