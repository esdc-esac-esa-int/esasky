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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class HstImageCoordinateMetadataTest {

	
	
	@Test
	public void testmoveCentralRaDecToReferencePosition() {
		// From https://esahubble.org/images/opo1335a/
		HstImageCoordinateMetadata testObj = new HstImageCoordinateMetadata();
		testObj.setReferenceValue(Arrays.asList(new Double[]{308.556303678, 41.1518765351}));
		testObj.setImageDimensions(Arrays.asList(2938,1892));
		testObj.setReferencePixels(Arrays.asList(2042.05443874,1995.00115791));
		testObj.setCoordinateSystemProjection("TAN");
		testObj.setScale(Arrays.asList(new Double[]{-1.38807e-05, 1.38807e-05}));
		testObj.setRotation(-7.48466377426);
		testObj.scaleToCorrectValues();
		
		assertThat(testObj.getCoordinate().getRa(), is(closeTo(308.5642583, 0.0001)));
		assertThat(testObj.getCoordinate().getDec(), is(closeTo(41.1364, 0.0001)));
		assertThat(testObj.getFov(), is(closeTo(1.38807e-05*2938, 0.0001)));

	}
	 
	@Test
	public void testMoveCentralRaDecToReferencePosition2() {
		// From https://esahubble.org/images/heic0602a/
		HstImageCoordinateMetadata testObj = new HstImageCoordinateMetadata();
		testObj.setReferenceValue(Arrays.asList(new Double[]{210.894624886, 54.3010132646}));
		testObj.setImageDimensions(Arrays.asList(15852,12392));
		testObj.setReferencePixels(Arrays.asList(3685.0,2881.0));
		testObj.setCoordinateSystemProjection("TAN");
		testObj.setScale(Arrays.asList(new Double[]{-1.38767541849e-05, 1.38767541849e-05}));
		testObj.setRotation(-3.5400000000000014);
		
		testObj.scaleToCorrectValues();
		
		assertThat(testObj.getCoordinate().getRa(), is(closeTo(210.7987208, 0.0001)));
		assertThat(testObj.getCoordinate().getDec(), is(closeTo(54.3505611, 0.0001)));
		assertThat(testObj.getFov(), is(closeTo(1.38767541849e-05*15852, 0.0001)));

	}
	
}
