package esac.archive.esasky.cl.web.client.model;

import esac.archive.esasky.ifcs.model.shared.ColumnType;

public class TableElement {

    private String label;
    private String value;
    private ColumnType type;
    private Integer maxDecimalDigits = null;
    private Boolean visible;
    private Integer columnIndex;
    private String tapName;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

	public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ColumnType getType() {
        return type;
    }

    public Integer getMaxDecimalDigits() {
        return maxDecimalDigits;
    }

    public void setMaxDecimalDigits(Integer maxDecimalDigits) {
        this.maxDecimalDigits = maxDecimalDigits;
    }

    public void setType(ColumnType type) {
        this.type = type;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public Integer getColumnIndex() {
        return columnIndex;
    }

    public void setColumnIndex(Integer columnIndex) {
        this.columnIndex = columnIndex;
    }

    public String getTapName() {
        return tapName;
    }

    public void setTapName(String tapName) {
        this.tapName = tapName;
    }

    @Override
    public String toString() {
        return "TableRow [label=" + label + ", value=" + value + ", type=" + type + ", visible="
                + visible + ", columnIndex=" + columnIndex + "]";
    }

}
