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
