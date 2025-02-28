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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IceCubeDescriptorTest {

    String jsonString = "{\"descriptors\":[{\"mission\":\"Icecube_events\",\"shapeLimit\":5000,\"useIntersectPolygonInsteadOfContainsPoint\":false,\"tapTable\":\"alerts.mv_v_icecube_event_fdw\",\"guiShortName\":\"Icecube events\",\"guiLongName\":\" Icecube events\",\"primaryColor\":\"#4EB265\",\"archiveURL\":\"\",\"uniqueIdentifierField\":null,\"fovLimit\":0.0,\"tapRaColumn\":\"ra\",\"tapDecColumn\":\"dec\",\"tapSTCSColumn\":\"stc_s\",\"sampEnabled\":true,\"descriptorId\":\"ASTRO_ICECUBE_Icecube_events\",\"metadata\":[{\"tapName\":\"event_num\",\"label\":\"Source Name\",\"visible\":true,\"type\":\"INTEGER\",\"description\":null,\"index\":null,\"maxDecimalDigits\":null,\"defaultMin\":null,\"defaultMax\":null},{\"tapName\":\"notice_type\",\"label\":null,\"visible\":true,\"type\":\"STRING\",\"description\":null,\"index\":null,\"maxDecimalDigits\":null,\"defaultMin\":null,\"defaultMax\":null},{\"tapName\":\"ra\",\"label\":null,\"visible\":true,\"type\":\"RA\",\"description\":null,\"index\":null,\"maxDecimalDigits\":null,\"defaultMin\":null,\"defaultMax\":null},{\"tapName\":\"dec\",\"label\":null,\"visible\":true,\"type\":\"DEC\",\"description\":null,\"index\":null,\"maxDecimalDigits\":null,\"defaultMin\":null,\"defaultMax\":null},{\"tapName\":\"energy\",\"label\":null,\"visible\":true,\"type\":\"DOUBLE\",\"description\":null,\"index\":null,\"maxDecimalDigits\":null,\"defaultMin\":null,\"defaultMax\":null},{\"tapName\":\"signalness\",\"label\":null,\"visible\":true,\"type\":\"DOUBLE\",\"description\":null,\"index\":null,\"maxDecimalDigits\":null,\"defaultMin\":null,\"defaultMax\":null},{\"tapName\":\"stream\",\"label\":null,\"visible\":true,\"type\":\"DOUBLE\",\"description\":null,\"index\":null,\"maxDecimalDigits\":null,\"defaultMin\":null,\"defaultMax\":null},{\"tapName\":\"far\",\"label\":null,\"visible\":true,\"type\":\"DOUBLE\",\"description\":null,\"index\":null,\"maxDecimalDigits\":null,\"defaultMin\":null,\"defaultMax\":null},{\"tapName\":\"discovery_timestamp\",\"label\":null,\"visible\":true,\"type\":\"TIMESTAMP\",\"description\":null,\"index\":null,\"maxDecimalDigits\":null,\"defaultMin\":null,\"defaultMax\":null}],\"wavelengths\":[{\"shortName\":\"Optical\",\"longName\":\"Optical\",\"prefix\":\"\",\"range\":[6.1,6.4]}],\"creditedInstitutions\":\"\",\"tabCount\":0,\"icon\":\"galaxy\"}],\"total\":1}";
    private IceCubeDescriptorList descriptorList;

    @BeforeEach
    public void readJson() throws JsonMappingException, JsonProcessingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        descriptorList = mapper.readValue(jsonString, IceCubeDescriptorList.class);
    }

    @Test
    public void testIceCubeDescriptor() {
        IceCubeDescriptor descriptor = descriptorList.getDescriptors().get(0);
        assertEquals("galaxy", descriptor.getIcon());
        assertEquals("ASTRO_ICECUBE_Icecube_events", descriptor.getDescriptorId());
        assertEquals("alerts.mv_v_icecube_event_fdw", descriptor.getTapTable());

        descriptor.setDescriptorId(null);
        assertEquals("ASTRO_ICECUBE_Icecube_events", descriptor.getDescriptorId());

        descriptor.setDescriptorId("");
        assertEquals("ASTRO_ICECUBE_Icecube_events", descriptor.getDescriptorId());

        descriptor.setDescriptorId("descriptorId");
        assertEquals("descriptorId", descriptor.getDescriptorId());
    }

}
