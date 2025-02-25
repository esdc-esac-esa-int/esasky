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
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


public class CatalogDescriptor extends BaseDescriptor {

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

    @Override
    public String getTapRaColumn() {
        return tapRaColumn == null ? "ra": tapRaColumn;
    }

    @Override
    public String getTapDecColumn() {
        return tapDecColumn == null ? "dec": tapDecColumn;
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
