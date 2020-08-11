package esac.archive.esasky.ifcs.model.descriptor;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class WavelengthDescriptor {

    private String shortName;
    private String longName;
    private String prefix;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private ArrayList<Double> range = new ArrayList<Double>();

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }
    
    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
    
    public ArrayList<Double> getRange() {
		return range;
	}

	public void setRange(ArrayList<Double> range) {
		this.range = range;
	}
}
