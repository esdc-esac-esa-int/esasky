package esac.archive.esasky.ifcs.model.descriptor;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "SiafEntries")
@JsonIgnoreProperties
public class SiafEntries {
	
	@JacksonXmlElementWrapper(useWrapping = false)
	
    public List<SiafEntry> SiafEntry = new LinkedList<SiafEntry>();

    public List<SiafEntry> getSiafEntry() {
        return SiafEntry;
    }


    public void setSiafEntry(List<SiafEntry> SiafEntry) {
        this.SiafEntry = SiafEntry;
    }

}
