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
        metadataDescriptor.setName(tapDescriptor.getPropertyString("column_name"));
        metadataDescriptor.setDataType(tapDescriptor.getPropertyString("datatype"));
        metadataDescriptor.setArraySize(tapDescriptor.getPropertyString("arraysize"));
        metadataDescriptor.setDescription(tapDescriptor.getPropertyString("description"));
        metadataDescriptor.setUnit(tapDescriptor.getPropertyString("unit"));
        metadataDescriptor.setUcd(tapDescriptor.getPropertyString("ucd"));
        metadataDescriptor.setUtype(tapDescriptor.getPropertyString("utype"));
        metadataDescriptor.setPrincipal(tapDescriptor.getPropertyString("principal"));
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
        this.principal = principal == null ? 1 : principal;
    }

    public void setPrincipal(Boolean principal) {
        this.principal = Boolean.FALSE.equals(principal) ? 0 : 1;
    }

    public void setPrincipal(String principal) {
        setPrincipal(principal == null || principal.equals("1") || principal.equals("true"));
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