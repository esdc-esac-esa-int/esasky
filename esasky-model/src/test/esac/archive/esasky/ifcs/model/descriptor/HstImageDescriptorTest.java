package esac.archive.esasky.ifcs.model.descriptor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HstImageDescriptorTest {

	String jsonString = "[{\"id\":\"heic1810d\",\"title\":\"Pockets of star formation in DDO 68\",\"description\":\"The dwarf galaxy DDO 68, also known as UGC 5340, lies about 40 million light-years away from Earth. Due to its proximity it became one of the 50 targets of LEGUS.\\r\\nIn UGC 5340, a pocket of rapid star birth appears in the lower right corner. This region of star formation was probably triggered by a gravitational interaction with an unseen companion galaxy. But star formation is present across the entire body of UGC 5340, and the relatively young stars are responsible for the galaxy’s blue-white colour.\\r\\nAn image of this galaxy was already released back in 2014 (heic1421). This newly-processed image now also shows ultraviolet radiation Hubble captured from the galaxy.\",\"priority\":70,\"release_date\":\"2018-05-17T17:00:00Z\",\"last_modified\":\"2019-10-07T11:40:36.565508\",\"coordinate_metadata\":{\"CoordinateFrame\":\"ICRS\",\"Equinox\":\"J2000\",\"ReferenceValue\":[149.2047,28.8185],\"ReferenceDimension\":[2696.0,1926.0],\"ReferencePixel\":[1187.27716716,2008.34895266],\"Scale\":[-1.099233e-05,1.099233e-05],\"Rotation\":-117.47382405183,\"CoordinateSystemProjection\":\"TAN\",\"Quality\":\"Full\"},\"credit\":\"NASA, ESA, and the LEGUS team\",\"tiles\":[\"https://cdn.spacetelescope.org/archives/images/zoomable/heic1810d/TileGroup0\"],\"large\":{\"ResourceType\":\"Large\",\"MediaType\":\"Image\",\"URL\":\"https://cdn.spacetelescope.org/archives/images/large/heic1810d.jpg\",\"FileSize\":3188230,\"Dimensions\":[2696,1926],\"ProjectionType\":\"Observation\"}},"
			+ "{\"id\":\"heic2004a\",\"title\":\"A massive laboratory\",\"description\":\"This image shows a region of space called LHA 120-N150. It is a substructure of the gigantic Tarantula Nebula. The latter is the largest known stellar nursery in the local Universe. The nebula is situated more than 160 000 light-years away in the Large Magellanic Cloud, a neighbouring dwarf irregular galaxy that orbits the Milky Way.\",\"priority\":0,\"release_date\":\"2020-03-18T09:00:00Z\",\"last_modified\":\"2021-04-08T20:36:09.533863\",\"coordinate_metadata\":{\"CoordinateFrame\":\"ICRS\",\"Equinox\":\"J2000\",\"ReferenceValue\":[83.4262772356,-68.7663000658],\"ReferenceDimension\":[4066.0,3518.0],\"ReferencePixel\":[2033.0,1759.0],\"Scale\":[-1.1014203069e-05,1.1014203069e-05],\"Rotation\":179.42000000000016,\"CoordinateSystemProjection\":\"TAN\",\"Quality\":\"Full\"},\"credit\":\"ESA/Hubble, NASA, I. Stephens\",\"tiles\":[\"https://cdn.spacetelescope.org/archives/images/zoomable/heic2004a/TileGroup1\",\"https://cdn.spacetelescope.org/archives/images/zoomable/heic2004a/TileGroup0\"],\"large\":{\"ResourceType\":\"Large\",\"MediaType\":\"Image\",\"URL\":\"https://cdn.spacetelescope.org/archives/images/large/heic2004a.jpg\",\"FileSize\":6058696,\"Dimensions\":[4066,3518],\"ProjectionType\":\"Observation\"}}]";
	
	String jsonString2 = "[{\"id\":\"heic1608a\",\"title\":\"The Bubble Nebula\",\"description\":\"The Bubble Nebula, also known as NGC 7635, is an emission nebula located 8 000 light-years away. This stunning new image was observed by the NASA/ESA Hubble Space Telescope to celebrate its 26th year in space.\",\"priority\":94,\"release_date\":\"2016-04-21T15:00:00Z\",\"last_modified\":\"2019-10-07T11:40:21.640125\",\"coordinate_metadata\":{\"CoordinateFrame\":\"ICRS\",\"Equinox\":\"J2000\",\"ReferenceValue\":[350.175386125,61.1912206849],\"ReferenceDimension\":[7857.0,7462.0],\"ReferencePixel\":[3928.5,3731.0],\"Scale\":[-1.10108269708e-05,1.10108269708e-05],\"Rotation\":33.58000000000003,\"CoordinateSystemProjection\":\"TAN\",\"Quality\":\"Full\"},\"credit\":\"NASA, ESA, Hubble Heritage Team\",\"tiles\":[\"https://cdn.spacetelescope.org/archives/images/zoomable/heic1608a/TileGroup1\",\"https://cdn.spacetelescope.org/archives/images/zoomable/heic1608a/TileGroup0\",\"https://cdn.spacetelescope.org/archives/images/zoomable/heic1608a/TileGroup2\",\"https://cdn.spacetelescope.org/archives/images/zoomable/heic1608a/TileGroup3\",\"https://cdn.spacetelescope.org/archives/images/zoomable/heic1608a/TileGroup4\"],\"large\":\"https://cdn.spacetelescope.org/archives/images/large/heic1608a.jpg\"}]";
	
	String jsonString3 = "[{\"id\":\"heic1810d\",\"title\":\"Pockets of star formation in DDO 68\",\"description\":\"The dwarf galaxy DDO 68, also known as UGC 5340, lies about 40 million light-years away from Earth. Due to its proximity it became one of the 50 targets of LEGUS.\\r\\nIn UGC 5340, a pocket of rapid star birth appears in the lower right corner. This region of star formation was probably triggered by a gravitational interaction with an unseen companion galaxy. But star formation is present across the entire body of UGC 5340, and the relatively young stars are responsible for the galaxy’s blue-white colour.\\r\\nAn image of this galaxy was already released back in 2014 (heic1421). This newly-processed image now also shows ultraviolet radiation Hubble captured from the galaxy.\",\"priority\":70,\"release_date\":\"2018-05-17T17:00:00Z\",\"last_modified\":\"2019-10-07T11:40:36.565508\",\"coordinate_metadata\":{\"CoordinateFrame\":\"ICRS\",\"Equinox\":\"J2000\",\"ReferenceValue\":[149.2047,28.8185],\"ReferenceDimension\":[2696.0,1926.0],\"ReferencePixel\":[1187.27716716,2008.34895266],\"Scale\":[-1.099233e-05,1.099233e-05],\"Rotation\":-117.47382405183,\"CoordinateSystemProjection\":\"TAN\",\"Quality\":\"Full\"},\"credit\":\"NASA, ESA, and the LEGUS team\",\"tiles\":[\"https://cdn.spacetelescope.org/archives/images/zoomable/heic1810d/TileGroup0\"],\"large\":\"https://cdn.spacetelescope.org/archives/images/large/heic1810d.jpg\"},"
			+ "{\"id\":\"heic2004a\",\"title\":\"A massive laboratory\",\"description\":\"This image shows a region of space called LHA 120-N150. It is a substructure of the gigantic Tarantula Nebula. The latter is the largest known stellar nursery in the local Universe. The nebula is situated more than 160 000 light-years away in the Large Magellanic Cloud, a neighbouring dwarf irregular galaxy that orbits the Milky Way.\",\"priority\":0,\"release_date\":\"2020-03-18T09:00:00Z\",\"last_modified\":\"2021-04-08T20:36:09.533863\",\"coordinate_metadata\":{\"CoordinateFrame\":\"ICRS\",\"Equinox\":\"J2000\",\"ReferenceValue\":[83.4262772356,-68.7663000658],\"ReferenceDimension\":[4066.0,3518.0],\"ReferencePixel\":[2033.0,1759.0],\"Scale\":[-1.1014203069e-05,1.1014203069e-05],\"Rotation\":179.42000000000016,\"CoordinateSystemProjection\":\"TAN\",\"Quality\":\"Full\"},\"credit\":\"ESA/Hubble, NASA, I. Stephens\",\"tiles\":[\"https://cdn.spacetelescope.org/archives/images/zoomable/heic2004a/TileGroup1\",\"https://cdn.spacetelescope.org/archives/images/zoomable/heic2004a/TileGroup0\"],\"large\":\"https://cdn.spacetelescope.org/archives/images/large/heic2004a.jpg\"}]";
	
	private HstImageDescriptor[] imageArray;
	
	@BeforeEach
	public void readJson() throws JsonMappingException, JsonProcessingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		imageArray = mapper.readValue(jsonString3, HstImageDescriptor[].class);
		for(HstImageDescriptor image : imageArray) {
			image.scaleToCorrectValues();
		}
	}
	
	@Test
	public void testParsingNewJson() throws JsonMappingException, JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		imageArray = mapper.readValue(jsonString2, HstImageDescriptor[].class);
		for(HstImageDescriptor image : imageArray) {
			image.scaleToCorrectValues();
		}
		
		HstImageDescriptor image = imageArray[0];
		assertEquals("heic1608a", image.getId());
	}
	
	@Test
	public void testParsingHeic1810d() {
		//Has noncentered reference so will use a number of the values to move it around
		
		HstImageDescriptor image = imageArray[0];
		assertEquals("heic1810d", image.getId());
		assertEquals(70, image.getPriority());
		assertEquals(149.19399355864303, image.getCoordinateMetadata().getCoordinate().ra);
		assertEquals(28.825368261784188, image.getCoordinateMetadata().getCoordinate().dec);
		assertEquals(0.02963532168, image.getCoordinateMetadata().getFov());
	}
	
	@Test
	public void testParsingHeic2004a() {
		HstImageDescriptor image = imageArray[1];
		assertEquals("heic2004a", image.getId());
		assertEquals("https://cdn.spacetelescope.org/archives/images/zoomable/heic2004a/", image.getTilesUrl());
		assertEquals(0, image.getPriority());
		assertEquals(83.4262772356, image.getCoordinateMetadata().getCoordinate().ra);
		assertEquals(-68.7663000658, image.getCoordinateMetadata().getCoordinate().dec);
		assertEquals(0.044783749678554, image.getCoordinateMetadata().getFov());
	}

	@Test
	public void testWritingAndParsingOnCl() throws JsonProcessingException, IOException {
		HstImageDescriptor slDesc = imageArray[1];
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		String newJson = mapper.writer().writeValueAsString(slDesc);
		
		HstImageDescriptor clDesc = mapper.readValue(newJson, HstImageDescriptor.class);
		
		assertEquals("heic2004a", clDesc.getId());
		assertEquals("https://cdn.spacetelescope.org/archives/images/zoomable/heic2004a/", clDesc.getTilesUrl());
		assertEquals(0, clDesc.getPriority());
		assertEquals(83.4262772356, clDesc.getCoordinateMetadata().getCoordinate().ra);
		assertEquals(-68.7663000658, clDesc.getCoordinateMetadata().getCoordinate().dec);
		assertEquals(0.044783749678554, clDesc.getCoordinateMetadata().getFov());
	}
	
	
}
