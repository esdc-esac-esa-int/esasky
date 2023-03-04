package esac.archive.esasky.ifcs.model.descriptor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import esac.archive.esasky.ifcs.model.shared.ESASkyColors;

import java.util.LinkedList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CommonTapDescriptor extends TapDescriptor {

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

    @JsonProperty("external")
    private boolean external;

    @JsonProperty("fov_limit")
    private double fovLimit;

    @JsonProperty("intersect_polygon_query")
    private boolean useIntersectsPolygon;

    @JsonIgnore
    private CommonTapDescriptor parent;

    @JsonIgnore
    private final List<CommonTapDescriptor> children = new LinkedList<>();

    @JsonIgnore
    private String description;

    /*********************
     * Getters
     *********************/


    public String getMission() {
        return mission;
    }

    public String getLongName() {
        return longName;
    }

    public String getShortName() {
        return shortName;
    }


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
    public boolean isExternal() {
        return external;
    }

    public double getFovLimit() {
        return fovLimit;
    }

    public boolean useIntersectsPolygon() {
        return useIntersectsPolygon;
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

    @JsonIgnore
    public List<CommonTapDescriptor> getChildren() {
        return children;
    }

    @JsonIgnore
    public List<CommonTapDescriptor> getAllChildren() {
        List<CommonTapDescriptor> allChildren = new LinkedList<>();
        for (CommonTapDescriptor child : children) {
            allChildren.add(child);
            allChildren.addAll(child.getAllChildren());
        }
        return allChildren;
    }

    @JsonIgnore
    public int getLevel() {
        return parent == null ? 0 : 1 + parent.getLevel();
    }

    @JsonIgnore
    public TapDescriptorBase getOriginalParent() {
        return parent == null ? this : parent.getOriginalParent();
    }

    public String getDescription() {
        return description;
    }

    @JsonIgnore
    public void removeChildren(List<String> idsNotToRemove) {
        for (CommonTapDescriptor child : children) {
            child.removeChildren(idsNotToRemove);

            if (!idsNotToRemove.contains(child.getId())) {
                children.remove(child);
            }
        }
    }

    @JsonIgnore
    public void addChild(CommonTapDescriptor child) {
        this.children.add(child);
        child.setParent(this);
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

    @JsonSetter("columns")
    public void setColumns(List<TapMetadataDescriptor> columns) {
        this.setMetadata(columns);
    }

    public void setIsExternal(boolean external) {
        this.external = external;
    }

    public void setFovLimit(double fovLimit) {
        this.fovLimit = fovLimit;
    }

    public void setUseIntersectsPolygon(boolean useIntersectsPolygon) {
        this.useIntersectsPolygon = useIntersectsPolygon;
    }

    @JsonIgnore
    public CommonTapDescriptor getParent() {
        return parent;
    }

    public void setParent(CommonTapDescriptor parent) {
        this.parent = parent;
    }


    public void setDescription(String description) {
        this.description = description;
    }
}
