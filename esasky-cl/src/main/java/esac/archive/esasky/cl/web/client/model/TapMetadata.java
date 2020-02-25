package esac.archive.esasky.cl.web.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TapMetadata {

    private String name;
    private String datatype;
    private String arraysize;
    private String ucd;
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public String getArraysize() {
        return arraysize;
    }

    public void setArraysize(String arraysize) {
        this.arraysize = arraysize;
    }

    public String getUcd() {
        return ucd;
    }

    public void setUcd(String ucd) {
        this.ucd = ucd;
    }

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
    
}
