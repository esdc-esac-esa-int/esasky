package esac.archive.esasky.ifcs.model.descriptor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TapDescriptorTest {

    private final TapDescriptor emptyTapDescriptor = new TapDescriptor();
    private TapDescriptor tapDescriptor = new TapDescriptor();
    private static final String rawResponseJson = "{\"metadata\":[{\"name\":\"archive_url\",\"description\":null,\"utype\":null,\"ucd\":null,\"unit\":null,\"datatype\":\"char\"},{\"name\":\"dec_deg\",\"description\":null,\"utype\":\"obscore:Char.SpatialAxis.Coverage.Location.Coord.Position2D.Value2.C23\",\"ucd\":\"pos.eq.dec;meta.main\",\"unit\":\"deg\",\"datatype\":\"double\"},{\"name\":\"end_time\",\"description\":null,\"utype\":null,\"ucd\":null,\"unit\":null,\"datatype\":\"char\"},{\"name\":\"fov\",\"description\":null,\"utype\":null,\"ucd\":\"pos.outline;meta.pgsphere\",\"unit\":null,\"datatype\":\"char\"},{\"name\":\"identifier\",\"description\":null,\"utype\":null,\"ucd\":\"meta.id;meta.main\",\"unit\":null,\"datatype\":\"char\"},{\"name\":\"instrument\",\"description\":null,\"utype\":null,\"ucd\":null,\"unit\":null,\"datatype\":\"char\"},{\"name\":\"max_wavelength\",\"description\":null,\"utype\":null,\"ucd\":null,\"unit\":null,\"datatype\":\"double\"},{\"name\":\"min_wavelength\",\"description\":null,\"utype\":null,\"ucd\":null,\"unit\":null,\"datatype\":\"double\"},{\"name\":\"nida_spectra_oid\",\"description\":null,\"utype\":null,\"ucd\":\"meta.oid\",\"unit\":null,\"datatype\":\"int\"},{\"name\":\"object_type\",\"description\":null,\"utype\":null,\"ucd\":null,\"unit\":null,\"datatype\":\"char\"},{\"name\":\"postcard_url\",\"description\":\"The URL to download the postcard preview\",\"utype\":\"obscore:Access.Reference\",\"ucd\":null,\"unit\":null,\"datatype\":\"char\"},{\"name\":\"product_url\",\"description\":null,\"utype\":null,\"ucd\":null,\"unit\":null,\"datatype\":\"char\"},{\"name\":\"ra_deg\",\"description\":null,\"utype\":\"obscore:Char.SpatialAxis.Coverage.Location.Coord.Position2D.Value2.C1\",\"ucd\":\"pos.eq.ra;meta.main\",\"unit\":\"deg\",\"datatype\":\"double\"},{\"name\":\"scientific_flag\",\"description\":null,\"utype\":null,\"ucd\":null,\"unit\":null,\"datatype\":\"char\"},{\"name\":\"start_time\",\"description\":null,\"utype\":null,\"ucd\":null,\"unit\":null,\"datatype\":\"char\"},{\"name\":\"stc_s\",\"description\":\"Footprint of the observation in STC string format\",\"utype\":\"obscore:Char.SpatialAxis.Coverage.Support.Area\",\"ucd\":null,\"unit\":null,\"datatype\":\"char\"},{\"name\":\"target\",\"description\":null,\"utype\":null,\"ucd\":null,\"unit\":null,\"datatype\":\"char\"},{\"name\":\"wavelengths\",\"description\":null,\"utype\":null,\"ucd\":null,\"unit\":null,\"datatype\":\"char\"}],\"data\":[[\"http://nida.esac.esa.int/nida-cl-web/?ACTION=OBSERVATION&ID=2003805\",\"58.97661\",\"1995-12-07 21:02:22\",\"{(4.03574472613996 , 1.02930284047422),(4.03562169788099 , 1.02927840586469),(4.03555404891918 , 1.02936888547844),(4.03567655357938 , 1.02939332008797)}\",\"02003805\",\"S02\",\"40.236\",\"2.854\",\"9\",\"Variable Star\",\"http://nida.esac.esa.int/nida-sl-tap/data?RETRIEVAL_TYPE=POSTCARD&tdt=2003805\",\"http://nida.esac.esa.int/nida-sl-tap/data?RETRIEVAL_TYPE=STANDALONE&tdt=2003805\",\"231.22569999\",\"Scientifically Validated\",\"1995-12-07 20:52:46\",\"POLYGON 'ICRS' 231.23114 58.9747086 231.224091 58.9733086 231.220215 58.9784927 231.227234 58.9798927\",\"IOT-DRA-7W\",\"2.85-2.88 3.07-3.10 4.64-4.69 7.64-7.73 13.91-14.07 16.92-17.07 39.77-40.24\"]]}";
    private static final String KEY1 = "key1";
    private static final String KEY2 = "key2";
    private static final String VAL1 = "val1";
    private static final String VAL2 = "val2";

    @Test
    void setPropertiesTest() {
        emptyTapDescriptor.setProperties(null);
        assertNotNull(emptyTapDescriptor.getProperties());
        assertTrue(emptyTapDescriptor.getProperties().isEmpty());

        emptyTapDescriptor.setProperties(KEY1, VAL1);
        assertEquals(1, emptyTapDescriptor.getProperties().size());

        Map<String, Object> properties = new HashMap<>();
        properties.put(KEY1, VAL1);
        properties.put(KEY2, VAL2);
        emptyTapDescriptor.setProperties(properties);
        assertEquals(2, emptyTapDescriptor.getProperties().size());
        assertEquals(VAL1, emptyTapDescriptor.getProperty(KEY1));
    }

    @Test
    void getPropertyTest() {
        emptyTapDescriptor.setProperties(KEY1, VAL1);
        assertEquals(VAL1, emptyTapDescriptor.getProperty(KEY1));
        assertEquals(VAL1, emptyTapDescriptor.getPropertyString(KEY1));

    }


    @Test
    void getMetadataTest() throws JsonProcessingException  {
        ObjectMapper mapper = new ObjectMapper();
        tapDescriptor = mapper.readValue(rawResponseJson, TapDescriptor.class);

        assertEquals("ra_deg", tapDescriptor.getRaColumn());
        assertEquals("dec_deg", tapDescriptor.getDecColumn());
        assertEquals("stc_s", tapDescriptor.getRegionColumn());
        assertEquals("identifier", tapDescriptor.getIdColumn());
    }

    @Test
    void setAdqlTest() {
        emptyTapDescriptor.setProperties(null);
        assertNotNull(emptyTapDescriptor.getProperties());
        assertTrue(emptyTapDescriptor.getProperties().isEmpty());

        emptyTapDescriptor.setProperties(KEY1, VAL1);
        assertEquals(1, emptyTapDescriptor.getProperties().size());

        Map<String, Object> properties = new HashMap<>();
        properties.put(KEY1, VAL1);
        properties.put(KEY2, VAL2);
        emptyTapDescriptor.setProperties(properties);
        assertEquals(2, emptyTapDescriptor.getProperties().size());
        assertEquals(VAL1, emptyTapDescriptor.getProperty(KEY1));

        emptyTapDescriptor.setOrderByADQL("test");
        assertEquals(emptyTapDescriptor.getOrderByADQL(), "test");
    }
}
