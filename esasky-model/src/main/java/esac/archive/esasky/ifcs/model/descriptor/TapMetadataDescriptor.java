package esac.archive.esasky.ifcs.model.descriptor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TapMetadataDescriptor {

    @JsonProperty("name")
    protected String name;

    @JsonProperty("datatype")
    protected String dataType;

    @JsonProperty("xtype")
    protected String xtype;

    @JsonProperty("arraysize")
    protected String arraySize;

    @JsonProperty("description")
    protected String description;

    @JsonProperty("unit")
    protected String unit;

    @JsonProperty("ucd")
    protected String ucd;

    @JsonProperty("utype")
    protected String utype;

    @JsonProperty("principal")
    protected boolean principal;

    public String getName() {
        return name;
    }

    public String getDataType() {
        return dataType;
    }

    public String getXtype() {
        return xtype;
    }
    public String getArraySize() {
        return arraySize;
    }


    public String getDescription() {
        return description;
    }


    public String getUnit() {
        return unit;
    }


    public String getUcd() {
        return ucd;
    }


    public String getUtype() {
        return utype;
    }

    public boolean isPrincipal() {
        return principal;
    }

    @JsonSetter("principal")
    public void setPrincipal(Boolean principal) {
        if (principal == null) {
            this.principal = true;
        } else {
            this.principal = principal;
        }
    }
}