package esac.archive.esasky.ifcs.model.descriptor;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public interface IDescriptor {

    String getMission();
    void setMission(String inputMission);

    String getTapTable();
    void setTapTable(String inputTapTable);

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

    String getAdsAuthorUrl();
    void setAdsAuthorUrl(String adsAuthorURL);

    String getAdsAuthorUrlReplace();
    void setAdsAuthorUrlReplace(String adsAuthorUrlReplace);

    String getAdsAuthorSeparator();
    void setAdsAuthorSeparator(String adsAuthorSeparator);
    
    int getAdsPublicationsMaxRows();
    void setAdsPublicationsMaxRows(int adsPublicationsMaxRows);
    
    String getCountColumn();
    void setCountColumn(String inputCountColumn);

    double getCountFovLimit();
    void setCountFovLimit(double inputCountFovLimit);

    List<MetadataDescriptor> getMetadata();
    void setMetadata(List<MetadataDescriptor> inputMetadata);

    List<WavelenthDescriptor> getWavelengths();
    void setWavelengths(final List<WavelenthDescriptor> wavelengths);
    
    String getCreditedInstitutions();
    void setCreditedInstitutions(final String creditedInstitutions);
    
    void registerColorChangeObservers(ColorChangeObserver observer);
    void unregisterColorChangeObservers(ColorChangeObserver observer);
    
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
    
    String getExtraPopupDetailsByTapName();

    void setExtraPopupDetailsByTapName(String extraPopupDetailsByTapName);
    
    String generateId();
    
    MetadataDescriptor getMetadataDescriptorByTapName(String tapName);
    @JsonIgnoreProperties
    Boolean getSampEnabled();
    
    @JsonIgnoreProperties
    String getTapSTCSColumn();
    
    String getTapQuery(String tapContext, String metadataAdql, String responseFormat);
    
    String getIcon();
    void setIcon(String icon);
    
    String getDescriptorId();
    void setDescriptorId(String descriptorId);
    
    String getSampUrl();
    String getDdBaseURL();
    String getDdProductURI();
    
}