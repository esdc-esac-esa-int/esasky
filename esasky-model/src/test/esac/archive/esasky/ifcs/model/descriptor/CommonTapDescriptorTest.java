package esac.archive.esasky.ifcs.model.descriptor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

class CommonTapDescriptorTest {

    String jsonString = "{\"descriptors\":[{\"shape_limit\":5000,\"columns\":[{\"name\":\"end_utc\",\"unit\":null,\"principal\":1,\"ucd\":\"time.end\",\"utype\":null},{\"name\":\"fov\",\"unit\":null,\"principal\":1,\"ucd\":\"pos.outline;meta.pgsphere\",\"utype\":null},{\"name\":\"position_angle\",\"unit\":null,\"principal\":1,\"ucd\":null,\"utype\":null},{\"name\":\"proprietary_end_date\",\"unit\":null,\"principal\":1,\"ucd\":null,\"utype\":null},{\"name\":\"xmm_om_uv_oid\",\"unit\":null,\"principal\":1,\"ucd\":null,\"utype\":null},{\"name\":\"postcard_url\",\"unit\":null,\"principal\":1,\"ucd\":\"meta.ref.url;meta.preview\",\"utype\":\"obscore:Access.Reference\"},{\"name\":\"product_url\",\"unit\":null,\"principal\":1,\"ucd\":\"meta.dataset;meta.ref.url\",\"utype\":\"obscore:Access.Reference\"},{\"name\":\"observation_id\",\"unit\":null,\"principal\":1,\"ucd\":null,\"utype\":\"obscore:DataID.Creatordid\"},{\"name\":\"instrument\",\"unit\":null,\"principal\":1,\"ucd\":null,\"utype\":\"obscore:Provenance.ObsConfig.Instrument.name\"},{\"name\":\"observation_oid\",\"unit\":null,\"principal\":1,\"ucd\":null,\"utype\":null},{\"name\":\"filter\",\"unit\":null,\"principal\":1,\"ucd\":null,\"utype\":null},{\"name\":\"ra_deg\",\"unit\":\"deg\",\"principal\":1,\"ucd\":\"pos.eq.ra;meta.main\",\"utype\":\"obscore:Char.SpatialAxis.Coverage.Location.Coord.Position2D.Value2.C7\"},{\"name\":\"dec_deg\",\"unit\":\"deg\",\"principal\":1,\"ucd\":\"pos.eq.dec;meta.main\",\"utype\":\"obscore:Char.SpatialAxis.Coverage.Location.Coord.Position2D.Value2.C17\"},{\"name\":\"target\",\"unit\":null,\"principal\":1,\"ucd\":null,\"utype\":null},{\"name\":\"start_utc\",\"unit\":null,\"principal\":1,\"ucd\":\"time.start\",\"utype\":null},{\"name\":\"duration\",\"unit\":\"s\",\"principal\":1,\"ucd\":null,\"utype\":\"obscore:Char.TimeAxis.Coverage.Support.Extent\"},{\"name\":\"stc_s\",\"unit\":null,\"principal\":1,\"ucd\":null,\"utype\":\"obscore:Char.SpatialAxis.Coverage.Support.Area\"}],\"wavelength_end\":6.7,\"schema_name\":\"observations\",\"fov_limit\":0,\"table_name\":\"observations.mv_v_esasky_xmm_om_uv_fdw\",\"mission\":\"XMM-OM-UV\",\"external\":false,\"archive_product_uri\":\"obsid=@@@observation_id@@@\",\"samp_base_url\":null,\"credits\":null,\"intersect_polygon_query\":false,\"archive_base_url\":\"http://nxsa.esac.esa.int/nxsa-web/#\",\"name\":\"XMM-OM\",\"samp_product_uri\":null,\"samp_enabled\":true,\"wavelength_start\":6.5,\"id\":44,\"name_short\":\"XMM-OM\",\"category\":\"observations\"},{\"shape_limit\":5000,\"columns\":[{\"name\":\"end_time\",\"unit\":\"time\",\"principal\":1,\"ucd\":\"time.end\",\"utype\":null},{\"name\":\"fov\",\"unit\":null,\"principal\":1,\"ucd\":\"pos.outline;meta.pgsphere\",\"utype\":null},{\"name\":\"npix\",\"unit\":null,\"principal\":1,\"ucd\":null,\"utype\":null},{\"name\":\"observation_oid\",\"unit\":null,\"principal\":1,\"ucd\":null,\"utype\":null},{\"name\":\"postcard_url\",\"unit\":null,\"principal\":1,\"ucd\":\"meta.ref.url;meta.preview\",\"utype\":\"obscore:Access.Reference\"},{\"name\":\"product_url\",\"unit\":null,\"principal\":1,\"ucd\":\"meta.dataset;meta.ref.url\",\"utype\":\"obscore:Access.Reference\"},{\"name\":\"observation_id\",\"unit\":null,\"principal\":1,\"ucd\":null,\"utype\":\"obscore:DataID.Creatordid\"},{\"name\":\"ra_deg\",\"unit\":\"deg\",\"principal\":1,\"ucd\":\"pos.eq.ra;meta.main\",\"utype\":\"obscore:Char.SpatialAxis.Coverage.Location.Coord.Position2D.Value2.C15\"},{\"name\":\"dec_deg\",\"unit\":\"deg\",\"principal\":1,\"ucd\":\"pos.eq.dec;meta.main\",\"utype\":\"obscore:Char.SpatialAxis.Coverage.Location.Coord.Position2D.Value2.C9\"},{\"name\":\"target_name\",\"unit\":null,\"principal\":1,\"ucd\":null,\"utype\":\"obscore:Target.Name\"},{\"name\":\"instrument_name\",\"unit\":null,\"principal\":1,\"ucd\":null,\"utype\":\"obscore:Provenance.ObsConfig.Instrument.name\"},{\"name\":\"collection\",\"unit\":null,\"principal\":1,\"ucd\":null,\"utype\":\"obscore:DataID.Collection\"},{\"name\":\"obs_type\",\"unit\":null,\"principal\":1,\"ucd\":null,\"utype\":null},{\"name\":\"filter\",\"unit\":null,\"principal\":1,\"ucd\":null,\"utype\":null},{\"name\":\"start_time\",\"unit\":\"time\",\"principal\":1,\"ucd\":\"time.start\",\"utype\":null},{\"name\":\"exposure_duration\",\"unit\":\"seconds\",\"principal\":1,\"ucd\":null,\"utype\":\"obscore:Char.TimeAxis.Coverage.Support.Extent\"},{\"name\":\"stc_s\",\"unit\":null,\"principal\":1,\"ucd\":null,\"utype\":\"obscore:Char.SpatialAxis.Coverage.Support.Area\"}],\"wavelength_end\":6.4,\"schema_name\":\"observations\",\"fov_limit\":0,\"table_name\":\"observations.mv_v_v_hst_mmi_observation_optical_fdw_fdw\",\"mission\":\"HST-OPTICAL\",\"external\":false,\"archive_product_uri\":\"observationid=@@@observation_id@@@\",\"samp_base_url\":null,\"credits\":null,\"intersect_polygon_query\":false,\"archive_base_url\":\"http://archives.esac.esa.int/ehst/#\",\"name\":\"HubbleSpaceTelescope\",\"samp_product_uri\":\"ARTIFACT_ID=@@@observation_id@@@_DRZ\",\"samp_enabled\":false,\"wavelength_start\":6.1,\"id\":46,\"name_short\":\"HST\",\"category\":\"observations\"}]}";


    private CommonTapDescriptorList descriptorList;

    @BeforeEach
    public void readJson() throws JsonMappingException, JsonProcessingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        descriptorList = mapper.readValue(jsonString, CommonTapDescriptorList.class);
    }

    @Test
    void testCommonTapDescriptor() {
        CommonTapDescriptor descriptor = descriptorList.getDescriptors().get(0);
        assertEquals(6.5, descriptor.getWavelengthStart());
        assertEquals(6.7, descriptor.getWavelengthEnd());
        assertEquals("observations", descriptor.getSchemaName());
        assertEquals("observations", descriptor.getCategory());
        assertEquals(0, descriptor.getFovLimit());
        assertEquals("observations.mv_v_esasky_xmm_om_uv_fdw", descriptor.getTableName());
        assertEquals("XMM-OM-UV", descriptor.getMission());
        assertFalse(descriptor.isExternal());
        assertEquals("obsid=@@@observation_id@@@", descriptor.getArchiveProductURI());
        assertEquals("http://nxsa.esac.esa.int/nxsa-web/#", descriptor.getArchiveBaseURL());
        assertEquals("XMM-OM", descriptor.getLongName());
        assertEquals("XMM-OM", descriptor.getShortName());
        assertEquals(5000, descriptor.getShapeLimit());
        assertTrue(descriptor.isSampEnabled());
    }


    @Test
    void testCommonTapDescriptorLevel() {
        CommonTapDescriptor descriptor1 = descriptorList.getDescriptors().get(0);
        CommonTapDescriptor descriptor2 = descriptorList.getDescriptors().get(1);
        assertNull(descriptor1.getParent());
        assertNull(descriptor2.getParent());
        descriptor1.addChild(descriptor2);
        assertEquals(descriptor1, descriptor2.getParent());
        assertEquals(1, descriptor1.getChildren().size());
        assertEquals(1, descriptor1.getAllChildren().size());
        assertEquals(descriptor2, descriptor1.getChildren().get(0));
        assertEquals(descriptor1, descriptor2.getOriginalParent());
        descriptor1.removeChildren(Arrays.asList(descriptor2.getId()));
        assertEquals(1, descriptor1.getChildren().size());
        assertEquals(descriptor1, descriptor2.getParent());
        descriptor1.removeChildren(Arrays.asList(""));
        assertEquals(0, descriptor1.getChildren().size());
    }
}
