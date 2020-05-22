package esac.archive.esasky.ifcs.model.descriptor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


public class CatalogDescriptor extends BaseDescriptor {

    private String shapeLimitDescription;

    private String posTapColumn;

    private String polygonNameTapColumn;

    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String drawSourcesFunction;
    
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String pmRaTapColumn;
    
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String pmDecTapColumn;
    
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String finalRaTapColumn;
    
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String finalDecTapColumn;
    
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String pmPlxTapColumn;
    
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String pmNormRadVelTapColumn;
    
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private Double pmOrigEpoch;
    
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private Double pmFinalEpoch;
    
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private Double pmArrowWidth;

    public String getShapeLimitDescription() {
        return shapeLimitDescription;
    }

    public void setShapeLimitDescription(String sourceLimitDescription) {
        this.shapeLimitDescription = sourceLimitDescription;
    }

    public String getPosTapColumn() {
        return posTapColumn;
    }

    public void setPosTapColumn(String posTapColumn) {
        this.posTapColumn = posTapColumn;
    }

    public String getPolygonNameTapColumn() {
        return polygonNameTapColumn;
    }

    public void setPolygonNameTapColumn(String polygonNameTapColumn) {
        this.polygonNameTapColumn = polygonNameTapColumn;
    }

    public String getDrawSourcesFunction() {
        return drawSourcesFunction;
    }

    public void setDrawSourcesFunction(String drawSourcesFunction) {
        this.drawSourcesFunction = drawSourcesFunction;
    }
    
    public String getPmRaTapColumn() {
        return pmRaTapColumn;
    }

    public void setPmRaTapColumn(String pmRaTapColumn) {
        this.pmRaTapColumn = pmRaTapColumn;
    }
    
    public String getPmDecTapColumn() {
        return pmDecTapColumn;
    }

    public void setPmDecTapColumn(String pmDecTapColumn) {
        this.pmDecTapColumn = pmDecTapColumn;
    }
    
    public String getFinalRaTapColumn() {
        return finalRaTapColumn;
    }

    public void setFinalRaTapColumn(String finalRaTapColumn) {
        this.finalRaTapColumn = finalRaTapColumn;
    }
    
    public String getFinalDecTapColumn() {
        return finalDecTapColumn;
    }

    public void setFinalDecTapColumn(String finalDecTapColumn) {
        this.finalDecTapColumn = finalDecTapColumn;
    }
    
    public String getPmPlxTapColumn() {
        return pmPlxTapColumn;
    }

    public void setPmPlxTapColumn(String pmPlxTapColumn) {
        this.pmPlxTapColumn = pmPlxTapColumn;
    }
    
    public String getPmNormRadVelTapColumn() {
        return pmNormRadVelTapColumn;
    }

    public void setPmNormRadVelTapColumn(String pmNormRadVelTapColumn) {
        this.pmNormRadVelTapColumn = pmNormRadVelTapColumn;
    }
    
    public Double getPmOrigEpoch() {
        return pmOrigEpoch;
    }

    public void setPmOrigEpoch(Double pmOrigEpoch) {
        this.pmOrigEpoch = pmOrigEpoch;
    }
    
    public Double getPmFinalEpoch() {
        return pmFinalEpoch;
    }

    public void setPmFinalEpoch(Double pmFinalEpoch) {
        this.pmFinalEpoch = pmFinalEpoch;
    }
    
    public Double getPmArrowWidth() {
        return pmArrowWidth;
    }

    public void setPmArrowWidth(Double pmArrowWidth) {
        this.pmArrowWidth = pmArrowWidth;
    }
    
	@Override
	public String getUniqueIdentifierField() {
		return polygonNameTapColumn;
	}
	
	@Override
	public void setUniqueIdentifierField(String field) {
		polygonNameTapColumn = field;
	}

    @Override
    public String getIcon() {
        return "catalog";
    }

    @Override
    public String getDescriptorId() {
        if(descriptorId == null || descriptorId.isEmpty()) {
            return "ASTRO_CATALOGUE_" + getMission();
        }
        return descriptorId;
    }
}
