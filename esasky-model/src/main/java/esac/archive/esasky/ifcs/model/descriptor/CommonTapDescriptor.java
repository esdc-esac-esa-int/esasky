package esac.archive.esasky.ifcs.model.descriptor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.shared.ESASkyColors;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CommonTapDescriptor extends TapDescriptorBase {

    @JsonProperty("mission")
    private String mission;

    @JsonProperty("name")
    private String longName;

    @JsonProperty("name_short")
    private String shortName;

    @JsonProperty("schema_name")
    private String schemaName;

    @JsonProperty("shape_limit")
    private int shapeLimit;

    @JsonProperty("table_name")
    private String tableName;

    @JsonProperty("archive_base_url")
    private String archiveBaseURL;

    @JsonProperty("archive_product_uri")
    private String archiveProductURI;

    @JsonProperty("samp_base_url")
    private String sampBaseURL;

    @JsonProperty("samp_product_uri")
    private String sampProductURI;

    @JsonProperty("samp_enabled")
    private boolean sampEnabled;

    @JsonProperty("credits")
    private String credits;

    @JsonProperty("wavelength_start")
    private double wavelengthStart;

    @JsonProperty("wavelength_end")
    private double wavelengthEnd;

    @JsonProperty("category")
    private String category;

    @JsonProperty("columns")
    private List<TapMetadataDescriptor> columns;

    @JsonIgnore
    private TapDescriptor tapDescriptor;


    /*********************
     * Getters
     *********************/

    public String getMission() {
        return mission;
    }


    public String getLongNameColumn() {
        return longName;
    }

    public String getShortNameColumn() {
        return shortName;
    }

    @Override
    public String getSchemaName() {
        return schemaName;
    }

    @Override
    public int getShapeLimit() {
        return shapeLimit;
    }

    public String getTableName() {
        return tableName;
    }

    public String getArchiveBaseURL() {
        return archiveBaseURL;
    }

    public String getArchiveProductURI() {
        return archiveProductURI;
    }

    public String getSampBaseURL() {
        return sampBaseURL;
    }

    public String getSampProductURI() {
        return sampProductURI;
    }

    public boolean isSampEnabled() {
        return sampEnabled;
    }

    public String getCredits() {
        return credits;
    }

    public double getWavelengthStart() {
        return wavelengthStart;
    }

    public double getWavelengthEnd() {
        return wavelengthEnd;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public String getId() {
        return "TAP_DESCRIPTOR_" + this.getMission();
    }

    public Double getWavelengthCenter() {
        return (getWavelengthStart() + getWavelengthEnd()) / 2;
    }

    @Override
    public String getColor() {
        if (super.color != null && !super.color.isEmpty()) {
            return color;
        } else {
            return ESASkyColors.getColorFromWavelength((Math.max(getWavelengthStart(), 0) + Math.min(getWavelengthEnd(), 100)) / 2);
        }
    }

    @Override
    public List<TapMetadataDescriptor> getColumnMetadata() {
        return columns;
    }


    /*********************
     * Setters
     *********************/

    public void setMission(String mission) {
        this.mission = mission;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public void setShapeLimit(int shapeLimit) {
        this.shapeLimit = shapeLimit;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setArchiveBaseURL(String archiveBaseURL) {
        this.archiveBaseURL = archiveBaseURL;
    }

    public void setArchiveProductURI(String archiveProductURI) {
        this.archiveProductURI = archiveProductURI;
    }

    public void setSampBaseURL(String sampBaseURL) {
        this.sampBaseURL = sampBaseURL;
    }

    public void setSampProductURI(String sampProductURI) {
        this.sampProductURI = sampProductURI;
    }

    public void setSampEnabled(boolean sampEnabled) {
        this.sampEnabled = sampEnabled;
    }

    public void setCredits(String credits) {
        this.credits = credits;
    }

    public void setWavelengthStart(double wavelengthStart) {
        this.wavelengthStart = wavelengthStart;
    }

    public void setWavelengthEnd(double wavelengthEnd) {
        this.wavelengthEnd = wavelengthEnd;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setColumns(List<TapMetadataDescriptor> columns) {
        this.columns = columns;
    }

    public void setTapDescriptor(TapDescriptor tapDescriptor) {
        this.tapDescriptor = tapDescriptor;
    }

    /*************************
     * TAPDescriptor methods
     *************************/

    public String getRaColumn() {
        return tapDescriptor.getRaColumn();
    }

    public String getDecColumn() {
        return tapDescriptor.getDecColumn();
    }

    public String getRegionColumn() {
        return tapDescriptor.getRegionColumn();
    }

    public String getIdColumn() {
        return tapDescriptor.getIdColumn();
    }

    public String getProperMotionRaColumn() {
        return tapDescriptor.getProperMotionRaColumn();
    }

    public String getProperMotionDecColumn() {
        return tapDescriptor.getProperMotionDecColumn();
    }

    public String getProperMotionColumn() {
        return tapDescriptor.getProperMotionColumn();
    }

    public String getParallaxTrigColumn() {
        return tapDescriptor.getParallaxTrigColumn();
    }

    public String getRadialVelocityColumn() {
        return tapDescriptor.getRadialVelocityColumn();
    }

    public boolean hasProperMotion() {
        return getProperMotionColumn() != null && !getProperMotionColumn().isEmpty();
    }

    public Double getReferenceEpoch() {
        return tapDescriptor.getReferenceEpoch();
    }


    public List<TapMetadataDescriptor> getMetadata() {
        return tapDescriptor.getMetadata();
    }

    public GeneralJavaScriptObject getRawMetadata() {
        return tapDescriptor.getRawMetadata();
    }

}
