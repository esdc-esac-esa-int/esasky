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

package esac.archive.esasky.ifcs.model.descriptor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GwDescriptorTest {

	String jsonString = "{\"descriptors\":[{\"mission\":\"gw\",\"shapeLimit\":15000,\"useIntersectPolygonInsteadOfContainsPoint\":false,\"tapTable\":\"alerts.mv_v_gravitational_waves_fdw\",\"guiShortName\":\"gw\",\"guiLongName\":\"Gravitational Waves\",\"primaryColor\":\"#FF0000\",\"archiveURL\":\"\",\"uniqueIdentifierField\":null,\"fovLimit\":0.0,\"tapRaColumn\":\"ra\",\"tapDecColumn\":\"dec\",\"tapSTCSColumn\":\"stcs90\",\"sampEnabled\":false,\"descriptorId\":\"ASTRO_GW_gw\",\"metadata\":[{\"tapName\":\"event_page\",\"label\":null,\"visible\":true,\"type\":\"LINK2ARCHIVE\",\"description\":null,\"index\":null,\"maxDecimalDigits\":null,\"defaultMin\":null,\"defaultMax\":null},{\"tapName\":\"alert_type\",\"label\":null,\"visible\":false,\"type\":null,\"description\":null,\"index\":null,\"maxDecimalDigits\":null,\"defaultMin\":null,\"defaultMax\":null},{\"tapName\":\"grace_id\",\"label\":\"gw_grace_id\",\"visible\":true,\"type\":null,\"description\":null,\"index\":null,\"maxDecimalDigits\":null,\"defaultMin\":null,\"defaultMax\":null},{\"tapName\":\"iso_time\",\"label\":\"gw_iso_time\",\"visible\":true,\"type\":null,\"description\":null,\"index\":null,\"maxDecimalDigits\":null,\"defaultMin\":null,\"defaultMax\":null},{\"tapName\":\"far\",\"label\":\"gw_far\",\"visible\":false,\"type\":null,\"description\":null,\"index\":null,\"maxDecimalDigits\":null,\"defaultMin\":null,\"defaultMax\":null},{\"tapName\":\"instruments\",\"label\":null,\"visible\":false,\"type\":null,\"description\":null,\"index\":null,\"maxDecimalDigits\":null,\"defaultMin\":null,\"defaultMax\":null},{\"tapName\":\"pipeline\",\"label\":null,\"visible\":false,\"type\":null,\"description\":null,\"index\":null,\"maxDecimalDigits\":null,\"defaultMin\":null,\"defaultMax\":null},{\"tapName\":\"bbh\",\"label\":\"gw_bbh\",\"visible\":false,\"type\":null,\"description\":null,\"index\":null,\"maxDecimalDigits\":null,\"defaultMin\":null,\"defaultMax\":null},{\"tapName\":\"bns\",\"label\":\"gw_bns\",\"visible\":false,\"type\":null,\"description\":null,\"index\":null,\"maxDecimalDigits\":null,\"defaultMin\":null,\"defaultMax\":null},{\"tapName\":\"nsbh\",\"label\":\"gw_nsbh\",\"visible\":false,\"type\":null,\"description\":null,\"index\":null,\"maxDecimalDigits\":null,\"defaultMin\":null,\"defaultMax\":null},{\"tapName\":\"mass_gap\",\"label\":null,\"visible\":false,\"type\":null,\"description\":null,\"index\":null,\"maxDecimalDigits\":null,\"defaultMin\":null,\"defaultMax\":null},{\"tapName\":\"has_ns\",\"label\":\"gw_has_ns\",\"visible\":false,\"type\":null,\"description\":null,\"index\":null,\"maxDecimalDigits\":null,\"defaultMin\":null,\"defaultMax\":null},{\"tapName\":\"has_remnant\",\"label\":null,\"visible\":false,\"type\":null,\"description\":null,\"index\":null,\"maxDecimalDigits\":null,\"defaultMin\":null,\"defaultMax\":null},{\"tapName\":\"terrestrial\",\"label\":null,\"visible\":false,\"type\":null,\"description\":null,\"index\":null,\"maxDecimalDigits\":null,\"defaultMin\":null,\"defaultMax\":null},{\"tapName\":\"fluence\",\"label\":null,\"visible\":false,\"type\":null,\"description\":null,\"index\":null,\"maxDecimalDigits\":null,\"defaultMin\":null,\"defaultMax\":null},{\"tapName\":\"central_freq\",\"label\":null,\"visible\":false,\"type\":null,\"description\":null,\"index\":null,\"maxDecimalDigits\":null,\"defaultMin\":null,\"defaultMax\":null},{\"tapName\":\"duration\",\"label\":null,\"visible\":false,\"type\":null,\"description\":null,\"index\":null,\"maxDecimalDigits\":null,\"defaultMin\":null,\"defaultMax\":null},{\"tapName\":\"gravitational_waves_oid\",\"label\":null,\"visible\":false,\"type\":null,\"description\":null,\"index\":null,\"maxDecimalDigits\":null,\"defaultMin\":null,\"defaultMax\":null},{\"tapName\":\"group_id\",\"label\":null,\"visible\":false,\"type\":null,\"description\":null,\"index\":null,\"maxDecimalDigits\":null,\"defaultMin\":null,\"defaultMax\":null},{\"tapName\":\"hardware_inj\",\"label\":null,\"visible\":false,\"type\":null,\"description\":null,\"index\":null,\"maxDecimalDigits\":null,\"defaultMin\":null,\"defaultMax\":null},{\"tapName\":\"internal\",\"label\":null,\"visible\":false,\"type\":null,\"description\":null,\"index\":null,\"maxDecimalDigits\":null,\"defaultMin\":null,\"defaultMax\":null},{\"tapName\":\"open_alert\",\"label\":null,\"visible\":false,\"type\":null,\"description\":null,\"index\":null,\"maxDecimalDigits\":null,\"defaultMin\":null,\"defaultMax\":null},{\"tapName\":\"packet_type\",\"label\":null,\"visible\":false,\"type\":null,\"description\":null,\"index\":null,\"maxDecimalDigits\":null,\"defaultMin\":null,\"defaultMax\":null},{\"tapName\":\"pkt_ser_num\",\"label\":null,\"visible\":false,\"type\":null,\"description\":null,\"index\":null,\"maxDecimalDigits\":null,\"defaultMin\":null,\"defaultMax\":null},{\"tapName\":\"search\",\"label\":null,\"visible\":false,\"type\":null,\"description\":null,\"index\":null,\"maxDecimalDigits\":null,\"defaultMin\":null,\"defaultMax\":null},{\"tapName\":\"skymap_fits\",\"label\":\"gw_skymap_fits\",\"visible\":false,\"type\":\"LINK\",\"description\":null,\"index\":null,\"maxDecimalDigits\":null,\"defaultMin\":null,\"defaultMax\":null},{\"tapName\":\"stcs50\",\"label\":null,\"visible\":false,\"type\":null,\"description\":null,\"index\":null,\"maxDecimalDigits\":null,\"defaultMin\":null,\"defaultMax\":null},{\"tapName\":\"stcs90\",\"label\":null,\"visible\":false,\"type\":null,\"description\":null,\"index\":null,\"maxDecimalDigits\":null,\"defaultMin\":null,\"defaultMax\":null},{\"tapName\":\"ra\",\"label\":null,\"visible\":false,\"type\":null,\"description\":null,\"index\":null,\"maxDecimalDigits\":null,\"defaultMin\":null,\"defaultMax\":null},{\"tapName\":\"dec\",\"label\":null,\"visible\":false,\"type\":null,\"description\":null,\"index\":null,\"maxDecimalDigits\":null,\"defaultMin\":null,\"defaultMax\":null}],\"wavelengths\":null,\"creditedInstitutions\":null,\"tabCount\":0,\"icon\":\"galaxy\"}],\"total\":1}";
	private GwDescriptorList descriptorList;
	
	@BeforeEach
	public void readJson() throws JsonMappingException, JsonProcessingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		descriptorList = mapper.readValue(jsonString, GwDescriptorList.class);
	}
	
	@Test
	public void testGwDescriptor() {
		GwDescriptor descriptor = descriptorList.getDescriptors().get(0);
		assertEquals("galaxy", descriptor.getIcon());
		assertEquals("ASTRO_GW_gw", descriptor.getDescriptorId());
		assertEquals("alerts.mv_v_gravitational_waves_fdw", descriptor.getTapTable());
		
		descriptor.setDescriptorId(null);
		assertEquals("ASTRO_GW_gw", descriptor.getDescriptorId());

		descriptor.setDescriptorId("");
		assertEquals("ASTRO_GW_gw", descriptor.getDescriptorId());

		descriptor.setDescriptorId("descriptorId");
		assertEquals("descriptorId", descriptor.getDescriptorId());
	}
	
}
