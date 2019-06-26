package esac.archive.ammi.ifcs.model.descriptor;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public interface IDescriptor {

    /**
     * getMission().
     * @return string with the name of the mission.
     */
    String getMission();

    /**
     * set Mission name.
     * @param inputMission Input String
     */
    void setMission(String inputMission);

    /**
     * getTapTable().
     * @return String
     */
    String getTapTable();

    /**
     * setTapTable().
     * @param inputTapTable Input string.
     */
    void setTapTable(String inputTapTable);

    String getGuiShortName();

    void setGuiShortName(String inputGuiShortName);
    
    String getGuiLongName();
    
    void setGuiLongName(String inputGuiLongName);

    /**
     * getHistoColor().
     * @return String
     */
    String getHistoColor();

    /**
     * setHistoColor().
     * @param inputHistoColor Input String
     */
    void setHistoColor(String inputHistoColor);

    /**
     * getFovLimit().
     * @return Double
     */
    Double getFovLimit();

    /**
     * inputFovLimit().
     * @param inputFovLimit Input Double
     */
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
    
    /**
     * getCountColumn().
     * @return String
     */
    String getCountColumn();

    /**
     * setCountColumn().
     * @param inputCountColumn input String
     */
    void setCountColumn(String inputCountColumn);

    /**
     * getCountFovLimit().
     * @return double
     */
    double getCountFovLimit();

    /**
     * setCountFovLimit().
     * @param inputCountFovLimit Input double.
     */
    void setCountFovLimit(double inputCountFovLimit);

    /**
     * getMetadata().
     * @return List<MetadataDescriptor>
     */
    List<MetadataDescriptor> getMetadata();

    /**
     * setMetadata().
     * @param inputMetadata Input List<MetadataDescriptor>
     */
    void setMetadata(List<MetadataDescriptor> inputMetadata);

    /**
     * getMetadataDescriptorByTapName().
     * @param tapName Input String
     * @return ObservationMetadataDescriptor
     */
    MetadataDescriptor getMetadataDescriptorByTapName(String tapName);

    List<WavelenthDescriptor> getWavelengths();

    void setWavelengths(final List<WavelenthDescriptor> wavelengths);
    
    String getCreditedInstitutions();
    
    void setCreditedInstitutions(final String creditedInstitutions);

    String generateId();
    
    void registerColorChangeObservers(ColorChangeObserver observer);
    void unregisterColorChangeObservers(ColorChangeObserver observer);
    
    @JsonIgnoreProperties
    String getUniqueIdentifierField();

    void setUniqueIdentifierField(String field);
    
    @JsonIgnoreProperties
    int getTabCount();

    void setTabCount(int count);

}