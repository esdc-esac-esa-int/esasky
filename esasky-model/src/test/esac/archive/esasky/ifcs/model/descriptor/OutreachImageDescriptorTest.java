package esac.archive.esasky.ifcs.model.descriptor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OutreachImageDescriptorTest {

	String jsonString = "{\"credit\":\"NASA, ESA, the Hubble Heritage Team (STScI/AURA), A. Nota (ESA/STScI), and the Westerlund 2 Science Team\",\"dec_deg\":\"-57.759406875299995\",\"description\":\"This image shows the sparkling centerpiece of Hubble's 25th anniversary tribute. Westerlund 2 is a giant cluster of about 3000 stars located 20 000 light-years away in the constellation Carina.\\nHubble's near-infrared imaging camera pierces through the dusty veil enshrouding the stellar nursery, giving astronomers a clear view of the dense concentration of stars in the central cluster.\",\"fov_size\":\"0.0520026622395\",\"hst_outreach_oid\":\"478\",\"id\":\"heic1509c\",\"large_url\":\"https://cdn.spacetelescope.org/archives/images/large/heic1509c.jpg\",\"object_name\":\"Westerlund 2\",\"pixel_size\":[3000, 2000],\"priority\":\"75\",\"ra_deg\":\"156.022352656\",\"rotation\":\"-27.679999999999986\",\"stc_s\":\"POLYGON ICRS 156.0504414464296 -57.78683266339919 155.96409179586007 -57.76266545538848 155.9943064724899 -57.73197488316336 156.08060296713174 -57.75612156866548\",\"tiles_url\":\"https://cdn.spacetelescope.org/archives/images/zoomable/heic1509c/\",\"title\":\"The star cluster Westerlund 2\",\"fov\":\"Polygon 156.05044144642966 -57.78683266339919 155.96409179585982 -57.76266545538854 155.9943064724899 -57.731974883163396 156.08060296713177 -57.7561215686652\",\"npix\":\"597501\"}";
	
	@Test
	public void testParsingHeic1810d() throws JsonMappingException, JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		OutreachImageDescriptor desc = mapper.readValue(jsonString, OutreachImageDescriptor.class);
		
		assertEquals("heic1509c", desc.getId());
		
		assertTrue(desc.getCredit().startsWith("NASA"));
		assertEquals("Westerlund 2", desc.getObjectName());
		assertTrue(desc.getDescription().startsWith("This"));
		assertTrue(desc.getTitle().startsWith("The star cluster Westerlund"));
		
		assertTrue(desc.getStcs().startsWith("POLYGON ICRS 156.0504414464296"));
		
		assertEquals(-57.759406875299995, desc.getDec());
		assertEquals(156.022352656, desc.getRa());
		assertEquals(0.0520026622395, desc.getFovSize());
		assertEquals(-27.679999999999986, desc.getRotation());
		assertEquals(75.0, desc.getPriority());
		assertArrayEquals(new int[]{3000, 2000}, desc.getPixelSize());
		
		assertEquals("https://cdn.spacetelescope.org/archives/images/zoomable/heic1509c/", desc.getTilesUrl());
		assertEquals("https://cdn.spacetelescope.org/archives/images/large/heic1509c.jpg", desc.getLargeUrl());
	}
	
}
