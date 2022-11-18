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
    protected int principal;



    /**
     * Creates a TapMetadataDescriptor from a TapDescriptor.
     * This is a useful conversion when performing metadata queries against tap_schema.* tables.
     *
     * @param tapDescriptor the tapDescriptor
     * @return the TapMetadataDescriptor
     */
    public static TapMetadataDescriptor fromTapDescriptor(TapDescriptor tapDescriptor) {
        TapMetadataDescriptor metadataDescriptor = new TapMetadataDescriptor();
        metadataDescriptor.setName(tapDescriptor.getProperty("column_name").toString());
        metadataDescriptor.setDataType(tapDescriptor.getProperty("datatype").toString());
        metadataDescriptor.setArraySize(tapDescriptor.getProperty("arraysize").toString());
        metadataDescriptor.setDescription(tapDescriptor.getProperty("description").toString());
        metadataDescriptor.setUnit(tapDescriptor.getProperty("unit").toString());
        metadataDescriptor.setUcd(tapDescriptor.getProperty("ucd").toString());
        metadataDescriptor.setUtype(tapDescriptor.getProperty("utype").toString());
        metadataDescriptor.setPrincipal(tapDescriptor.getProperty("principal").toString());
        return metadataDescriptor;
    }

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


    @JsonSetter("principal")
    public void setPrincipal(Integer principal) {
        this.principal = principal == null ? 0 : principal;
    }

    public void setPrincipal(Boolean principal) {
        this.principal = Boolean.TRUE.equals(principal) ? 1 : 0;
    }

    public void setPrincipal(String principal) {
        setPrincipal(principal.equals("1") || principal.equals("true"));
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public void setXtype(String xtype) {
        this.xtype = xtype;
    }

    public void setArraySize(String arraySize) {
        this.arraySize = arraySize;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setUcd(String ucd) {
        this.ucd = ucd;
    }

    public void setUtype(String utype) {
        this.utype = utype;
    }

    public boolean isPrincipal() {
        return principal == 1;
    }
}