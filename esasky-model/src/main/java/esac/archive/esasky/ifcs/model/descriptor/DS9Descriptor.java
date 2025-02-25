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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.LinkedList;
import java.util.List;


@JacksonXmlRootElement(localName = "DS9Descriptor")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DS9Descriptor {
    @JsonProperty("mission")
    private String mission;
    @JsonProperty("instrument")
    private String instrument;
    @JsonProperty("color")
    private String color;

    @JsonProperty("width")
    private String width;

    private List<String> shapes;
    private List<DS9TextDescriptor> texts;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public List<String> getShapes() {
        if (shapes == null) {
            shapes = new LinkedList<>();
        }
        return shapes;
    }

    public void setShapes(List<String> shapes) {
        this.shapes = shapes;
    }

    public String getMission() {
        return mission;
    }

    public void setMission(String mission) {
        this.mission = mission;
    }

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    public List<DS9TextDescriptor> getTexts() {
        if (texts == null) {
            texts = new LinkedList<>();
        }
        return texts;
    }

    public void setTexts(List<DS9TextDescriptor> texts) {
        this.texts = texts;
    }
}
