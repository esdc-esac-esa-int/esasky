package esac.archive.esasky.ifcs.model.descriptor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import esac.archive.esasky.ifcs.model.shared.ESASkyColors;

public abstract class BaseDescriptor implements IDescriptor {

    /** the name of the mission (e.g. XMM). */
    private String mission;

    /** DB table name. */
    private String tapTable;

    /** Name of corresponding count column in the aggregated count table. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String countColumn;

    /** label that will be used on the GUI. */
    private String guiShortName;

    private String guiLongName;

    /** color that will be used on the GUI for histograms. */
    private String histoColor;

    /** Limit of the FOV to use the aggregated count (degrees). */
    @JsonIgnoreProperties(ignoreUnknown = true)
    private double countFovLimit;

    /** Fov limit. */
    private Double fovLimit;

    /** Archive related base URL */
    private String archiveURL;

    /** Archive related URL parameter */
    private String archiveProductURI;

    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private List<ColorChangeObserver> colorObservers = new LinkedList<ColorChangeObserver>();
    
    /** ADS Author search related URL, must have a adsAuthorUrlReplace string value to replace by author name */
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String adsAuthorUrl;

    /** ADS Author search value to replace in adsAuthorUrl for author name */
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String adsAuthorUrlReplace;
    
    /** ADS names separator value for authors in authors field for publications by source response */
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String adsAuthorSeparator;

    /** ADS max returned rows for publications by source response */
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private int adsPublicationsMaxRows;
    
    /** List of MetadataDescriptors. */
    protected List<MetadataDescriptor> metadata = new LinkedList<MetadataDescriptor>();

    @JsonIgnoreProperties(ignoreUnknown = true)
    private List<WavelenthDescriptor> wavelengths = new LinkedList<WavelenthDescriptor>();
   
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String creditedInstitutions;
    
    private int tabCount = 0;

    @Override
    public final String getMission() {
        return mission;
    }

    @Override
    public final void setMission(final String inputMission) {
        this.mission = inputMission;
    }

    @Override
    public final String getTapTable() {
        return tapTable;
    }

    @Override
    public final void setTapTable(final String inputTapTable) {
        this.tapTable = inputTapTable;
    }

    @Override
    public final String getGuiShortName() {
        return guiShortName;
    }

    @Override
    public final void setGuiShortName(final String inputGuiShortName) {
        this.guiShortName = inputGuiShortName;
    }

    @Override
    public final String getGuiLongName() {
        return guiLongName;
    }

    @Override
    public final void setGuiLongName(final String inputGuiLongName) {
        this.guiLongName = inputGuiLongName;
    }

    @Override
    public final String getHistoColor() {
        return histoColor;
    }

    @Override
    public final void setHistoColor(final String inputHistoColor) {
//        this.histoColor = inputHistoColor;
//        for (ColorChangeObserver observer : colorObservers) {
//        	observer.onColorChange(this, inputHistoColor);
//        }
    }
    
    @Override
    public void registerColorChangeObservers(ColorChangeObserver observer) {
    	colorObservers.add(observer);
    }
    
    @Override
    public void unregisterColorChangeObservers(ColorChangeObserver observer) {
    	colorObservers.remove(observer);
    }
    
    @Override
    public final Double getFovLimit() {
        return fovLimit;
    }

    @Override
    public final void setFovLimit(final Double inputFovLimit) {
        this.fovLimit = inputFovLimit;
    }

    @Override
    public String getArchiveURL() {
        return archiveURL;
    }

    @Override
    public void setArchiveURL(String ddURL) {
        this.archiveURL = ddURL;
    }

    @Override
    public String getArchiveProductURI() {
        return archiveProductURI;
    }

    @Override
    public void setArchiveProductURI(String archiveProductURI) {
        this.archiveProductURI = archiveProductURI;
    }
    
    @Override
    public String getAdsAuthorUrl() {
        return adsAuthorUrl;
    }

    @Override
    public void setAdsAuthorUrl(String adsAuthorURL) {
        this.adsAuthorUrl = adsAuthorURL;
    }

    @Override
    public String getAdsAuthorUrlReplace() {
        return adsAuthorUrlReplace;
    }

    @Override
    public void setAdsAuthorUrlReplace(String adsAuthorUrlReplace) {
        this.adsAuthorUrlReplace = adsAuthorUrlReplace;
    }

    @Override
    public String getAdsAuthorSeparator(){
        return adsAuthorSeparator;
    }

    @Override
    public void setAdsAuthorSeparator(String adsAuthorSeparator){
        this.adsAuthorSeparator = adsAuthorSeparator;
    }
    
    @Override
    public int getAdsPublicationsMaxRows(){
        return adsPublicationsMaxRows;
    }

    @Override
    public void setAdsPublicationsMaxRows(int adsPublicationsMaxRows){
        this.adsPublicationsMaxRows = adsPublicationsMaxRows;
    }
    
    @Override
    public final String getCountColumn() {
        return countColumn;
    }

    @Override
    public final void setCountColumn(final String inputCountColumn) {
        this.countColumn = inputCountColumn;
    }

    @Override
    public final double getCountFovLimit() {
        return countFovLimit;
    }

    @Override
    public final void setCountFovLimit(final double inputCountFovLimit) {
        this.countFovLimit = inputCountFovLimit;
    }

    @Override
    public List<MetadataDescriptor> getMetadata() {
        return metadata;
    }

    @Override
    public void setMetadata(final List<MetadataDescriptor> inputMetadata) {
        this.metadata = inputMetadata;
    }

    @Override
    public MetadataDescriptor getMetadataDescriptorByTapName(String tapName) {
        for (MetadataDescriptor cmd : getMetadata()) {
            if (cmd.getTapName().equals(tapName)) {
                return cmd;
            }
        }
        return null;
    }

    protected int generateNextTabCount() {
        tabCount += 1;
        return tabCount;
    }

    @Override
    public int getTabCount() {
        return tabCount;
    }

    @Override
    public void setTabCount(int count) {
        tabCount = count;
    };

    @Override
    public final List<WavelenthDescriptor> getWavelengths() {
        return wavelengths;
    }

    @Override
    public final void setWavelengths(final List<WavelenthDescriptor> wavelengths) {
        this.wavelengths = wavelengths;
        double minWavelength = 100;
        double maxWavelength = 0;
        for(WavelenthDescriptor w : wavelengths) {
        	ArrayList<Double> range = w.getRange();
    		minWavelength = Math.min(range.get(0), minWavelength);
    		maxWavelength = Math.max(range.get(1), maxWavelength);
        }
        double mean = (maxWavelength + minWavelength) / 2;
        
        this.histoColor = ESASkyColors.getColorFromWavelength(mean);
        for (ColorChangeObserver observer : colorObservers) {
        	observer.onColorChange(this, this.histoColor);
        }
    }
    
	@Override
    public final String getCreditedInstitutions() {
    	return creditedInstitutions;
    }
    
    @Override
    public final void setCreditedInstitutions(final String creditedInstitutions) {
    	this.creditedInstitutions = creditedInstitutions;
    }
}
