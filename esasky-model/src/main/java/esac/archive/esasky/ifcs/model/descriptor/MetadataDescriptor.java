package esac.archive.esasky.ifcs.model.descriptor;

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

    /* Appearing order within the ResultPanel */
    private Integer index;
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    private Integer maxDecimalDigits;

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
}
