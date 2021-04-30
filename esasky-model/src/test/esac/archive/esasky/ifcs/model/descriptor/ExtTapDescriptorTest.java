package esac.archive.esasky.ifcs.model.descriptor;

import static org.junit.Assert.*;


import org.junit.*;

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
	
	@Before
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
