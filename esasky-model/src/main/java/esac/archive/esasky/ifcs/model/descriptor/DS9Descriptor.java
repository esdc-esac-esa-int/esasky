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
}
