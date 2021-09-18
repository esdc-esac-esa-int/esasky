package esac.archive.esasky.ifcs.model.descriptor;

import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.shared.ColumnType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


public class MetadataDescriptor {

    /* DB column name */
    private String tapName;

    /* Label used into the GUI */
    private String label;

    /* Visibility into the resultPanel */
    private Boolean visible;

    private ColumnType type;
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String description;

    /* Appearing order within the ResultPanel */
    private Integer index;
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    private Integer maxDecimalDigits;
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    private Double defaultMin;
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    private Double defaultMax;

    public Integer getMaxDecimalDigits() {
        return maxDecimalDigits;
    }

    public void setMaxDecimalDigits(Integer maxDecimalDigits) {
        this.maxDecimalDigits = maxDecimalDigits;
    }

    public String getTapName() {
        return tapName;
    }

    public void setTapName(String tapName) {
        this.tapName = tapName;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public ColumnType getType() {
        return type;
    }

    public void setType(ColumnType type) {
        this.type = type;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getDefaultMin() {
		return defaultMin;
	}

	public void setDefaultMin(Double defaultMin) {
		this.defaultMin = defaultMin;
	}

	public Double getDefaultMax() {
		return defaultMax;
	}

	public void setDefaultMax(Double defaultMax) {
		this.defaultMax = defaultMax;
	}
	
	public GeneralJavaScriptObject toJSONObject() {
		String jsonString = "{";
		jsonString  += "\"tapName\":\"" + tapName + "\"";
		jsonString  += ",\"label\":\"" + label + "\"";
		if(visible != null) {
			jsonString  += ",\"visible\":" + visible.toString();
		}
		jsonString  += ",\"description\":\"" + description + "\"";
		if(maxDecimalDigits != null) {
			jsonString  += ",\"maxDecimalDigits\":" + maxDecimalDigits.toString();
		}
		if(type != null) {
			jsonString  += ",\"type\":\"" + type.toString() + "\"";
		}
		
		jsonString += "}";
		
		return GeneralJavaScriptObject.createJsonObject(jsonString);
	}
	
}
