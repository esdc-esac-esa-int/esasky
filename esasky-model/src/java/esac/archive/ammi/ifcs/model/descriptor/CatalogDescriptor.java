package esac.archive.ammi.ifcs.model.descriptor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


public class CatalogDescriptor extends BaseDescriptor {

    private int sourceLimit;

    private String sourceLimitDescription;

    private String posTapColumn;

    private String polygonRaTapColumn;

    private String polygonDecTapColumn;

    private String polygonNameTapColumn;

    private String orderBy;

    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String extraPopupDetailsByTapName;

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
    private String pmArrowColor;
    
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private Double pmArrowWidth;

    public int getSourceLimit() {
        return sourceLimit;
    }

    public void setSourceLimit(int sourceLimit) {
        this.sourceLimit = sourceLimit;
    }

    public String getSourceLimitDescription() {
        return sourceLimitDescription;
    }

    public void setSourceLimitDescription(String sourceLimitDescription) {
        this.sourceLimitDescription = sourceLimitDescription;
    }

    public String getPosTapColumn() {
        return posTapColumn;
    }

    public void setPosTapColumn(String posTapColumn) {
        this.posTapColumn = posTapColumn;
    }

    public String getPolygonRaTapColumn() {
        return polygonRaTapColumn;
    }

    public void setPolygonRaTapColumn(String polygonRaTapColumn) {
        this.polygonRaTapColumn = polygonRaTapColumn;
    }

    public String getPolygonDecTapColumn() {
        return polygonDecTapColumn;
    }

    public void setPolygonDecTapColumn(String polygonDecTapColumn) {
        this.polygonDecTapColumn = polygonDecTapColumn;
    }

    public String getPolygonNameTapColumn() {
        return polygonNameTapColumn;
    }

    public void setPolygonNameTapColumn(String polygonNameTapColumn) {
        this.polygonNameTapColumn = polygonNameTapColumn;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getExtraPopupDetailsByTapName() {
        return extraPopupDetailsByTapName;
    }

    public void setExtraPopupDetailsByTapName(String extraPopupDetailsByTapName) {
        this.extraPopupDetailsByTapName = extraPopupDetailsByTapName;
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
    
    public String getPmArrowColor() {
        return pmArrowColor;
    }

    public void setPmArrowColor(String pmArrowColor) {
        this.pmArrowColor = pmArrowColor;
    }
    
    public Double getPmArrowWidth() {
        return pmArrowWidth;
    }

    public void setPmArrowWidth(Double pmArrowWidth) {
        this.pmArrowWidth = pmArrowWidth;
    }
    
	@Override
	public String generateId() {
		 return getMission() + " C " + generateNextTabCount();
	}

	@Override
	public String getUniqueIdentifierField() {
		return polygonNameTapColumn;
	}
	
	@Override
	public void setUniqueIdentifierField(String field) {
		polygonNameTapColumn = field;
	}
}
