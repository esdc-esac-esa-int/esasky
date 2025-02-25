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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ExtTapDescriptorTest {
	
	private static final String testMission = "mission";
	private static final String parentMission = "parentMission";
	private static final String baseMission = "baseMission";
	
	private static final String tapContext = "/esasky-tap/ext-taps?ACTION=REQUEST";
	private static final String metaDataAdqlEncoded = "SELECT+TOP+1+*+from+ivoa.ObsCore";
	private static final String responseFormat = "VOTABLE";
	private static final String tapURL = "https://somUrl.com/tap";
	
	private static final String expectedInBackendTapUrl= "/esasky-tap/ext-taps?ACTION=REQUEST&TAP_TARGET=mission";
	private static final String expectedNotInBackendTapUrl = "/esasky-tap/ext-taps?ACTION=REQUEST&TAP_TARGET=mission&TAP_URL=https://somUrl.com/tap/sync&RESPONSE_FORMAT=VOTABLE";
	private static final String expectedTapMeta = "&ADQL=SELECT+TOP+1+*+from+ivoa.ObsCore";
	
	private ExtTapDescriptor descriptor;
	
	@BeforeEach
	public void setup(){
		descriptor = new ExtTapDescriptor();
		descriptor.setMission(testMission);
	}
		
	@Test
  	public void testBaseMission() {
		assertEquals(testMission, descriptor.getBaseMission());
		descriptor.setBaseMission(baseMission);		
		assertEquals(baseMission, descriptor.getBaseMission());
	}
	
	@Test
	public void testCopyingParent() {
		ExtTapDescriptor parent = new ExtTapDescriptor();
		parent.setMission(parentMission);
		assertEquals(false, descriptor.hasParent(parent));
		
		descriptor.copyParentValues(parent);
		assertEquals(true, descriptor.hasParent(parent));
		assertEquals(parentMission, descriptor.getMission());
	}
	
	@Test
	public void testGrandParent() {
		ExtTapDescriptor parent = new ExtTapDescriptor();
		ExtTapDescriptor parent2 = new ExtTapDescriptor();
		parent2.setMission(parentMission);
		parent.copyParentValues(parent2);
		descriptor.copyParentValues(parent);
		assertEquals(parent2, descriptor.getLastParent());
	}
	
	@Test
	public void testGetTapQueryForIsInBackend() {
		descriptor.setInBackend(true);
		String resultUrl = descriptor.getTapQuery(tapContext, metaDataAdqlEncoded, responseFormat);
		assertEquals(expectedInBackendTapUrl + expectedTapMeta, resultUrl);
	}
	
	@Test
	public void testGetTapQueryForNotInBackend() {
		descriptor.setInBackend(false);
		descriptor.setTapUrl(tapURL);
		descriptor.setResponseFormat(responseFormat);
		String resultUrl = descriptor.getTapQuery(tapContext, metaDataAdqlEncoded, responseFormat);
		assertEquals(expectedNotInBackendTapUrl + expectedTapMeta, resultUrl);
	}
	
	
}
