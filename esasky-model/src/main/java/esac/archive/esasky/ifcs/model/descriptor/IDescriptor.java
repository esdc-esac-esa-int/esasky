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
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.SearchArea;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;

import java.util.List;

public interface IDescriptor {

    String getMission();
    void setMission(String inputMission);

    String getTapTable();
    void setTapTable(String inputTapTable);

    GeneralJavaScriptObject getTapTableMetadata();

    void setTapTableMetadata(GeneralJavaScriptObject metadata);

    String getGuiShortName();
    void setGuiShortName(String inputGuiShortName);
    
    String getGuiLongName();
    void setGuiLongName(String inputGuiLongName);

    String getPrimaryColor();
    void setPrimaryColor(String inputHistoColor);

    Double getFovLimit();
    void setFovLimit(Double inputFovLimit);

    String getArchiveURL();
    void setArchiveURL(String ddURL);

    String getArchiveProductURI();
    void setArchiveProductURI(String archiveProductURI);

    String getArchiveColumn();
    void setArchiveColumn(String archiveColumn);

    List<MetadataDescriptor> getMetadata();
    void setMetadata(List<MetadataDescriptor> inputMetadata);

    List<WavelengthDescriptor> getWavelengths();
    void setWavelengths(List<WavelengthDescriptor> wavelengths);
    double getCenterWavelengthValue();
    
    String getCreditedInstitutions();
    void setCreditedInstitutions(final String creditedInstitutions);
    
    void registerColorChangeObservers(ColorChangeObserver observer);
    void unregisterColorChangeObservers(ColorChangeObserver observer);
    
    void registerMetadataVisibilityObserver(MetadataVisibilityObserver observer);
    void unregisterMetadataVisibilityObserver(MetadataVisibilityObserver observer);
    
    @JsonIgnoreProperties
    String getUniqueIdentifierField();
    void setUniqueIdentifierField(String field);
    
    @JsonIgnoreProperties
    int getTabCount();
    void setTabCount(int count);
    
    String getTapRaColumn();
    void setTapRaColumn(String tapRaColumn);
    
    String getTapDecColumn();
    void setTapDecColumn(String tapDecColumn);
    
    String getSecondaryColor();
    void setSecondaryColor(String secondaryColor);
    
    Boolean getUseIntersectPolygonInsteadOfContainsPoint();
    void setUseIntersectPolygonInsteadOfContainsPoint(boolean useInstersect);
    
    String getExtraPopupDetailsByTapName();

    void setExtraPopupDetailsByTapName(String extraPopupDetailsByTapName);
    
    String generateId();
    
    MetadataDescriptor getMetadataDescriptorByTapName(String tapName);
    @JsonIgnoreProperties
    Boolean getSampEnabled();
    
    @JsonIgnoreProperties
    String getTapSTCSColumn();
    
    @JsonIgnoreProperties
    void setMetadataVisibility(String tapName, boolean visibility);
    
    String getTapQuery(String tapContext, String metadataAdql, String responseFormat);
    
    String getIcon();
    void setIcon(String icon);
    
    String getDescriptorId();
    void setDescriptorId(String descriptorId);
    
    String getSampUrl();
    String getDdBaseURL();
    String getDdProductURI();
    int getShapeLimit();
    
    public GeneralJavaScriptObject getMetaDataJSONObject();
    
	String getOrderBy();
    void setOrderBy(String orderBy);

    void setSearchArea(SearchArea searchArea);
    SearchArea getSearchArea();
    boolean hasSearchArea();
    String getSearchAreaShape();

    boolean useUcd();

    String getUcdColumnName(String ucdName);
}