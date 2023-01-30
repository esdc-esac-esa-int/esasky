package esac.archive.esasky.ifcs.model.descriptor;

import com.allen_sauer.gwt.log.client.Log;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.gwt.http.client.URL;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.CoordinatesObject;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.SearchArea;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.shared.ColumnType;
import esac.archive.esasky.ifcs.model.shared.ESASkyColors;
import esac.archive.esasky.ifcs.model.shared.contentdescriptors.UCD;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseDescriptor implements IDescriptor {

    /** the name of the mission (e.g. XMM). */
    private String mission;

    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private int shapeLimit;
    
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    protected Boolean useIntersectPolygonInsteadOfContainsPoint;
    
    /** DB table name. */
    private String tapTable;
    private GeneralJavaScriptObject tapTableMetadata;

    /** Name of corresponding count column in the aggregated count table. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String countColumn;

    /** label that will be used on the GUI. */
    private String guiShortName;

    private String guiLongName;

    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String primaryColor;

    /** Limit of the FOV to use the aggregated count (degrees). */
    @JsonIgnoreProperties(ignoreUnknown = true)
    private double countFovLimit;

    /** Archive related base URL */
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String archiveURL;

    /** Archive related URL parameter */
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String archiveProductURI;

    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String archiveColumn;

    private String uniqueIdentifierField;
    
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private Double fovLimit;
    
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    protected String tapRaColumn;
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    protected String tapDecColumn;
    
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    protected String tapSTCSColumn;
    
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String secondaryColor;

    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private Boolean sampEnabled;
    
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String extraPopupDetailsByTapName;
    
    
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String sampUrl;
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String ddProductURI;

    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String ddBaseURL;
    
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    protected String descriptorId;

    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private List<ColorChangeObserver> colorObservers = new LinkedList<ColorChangeObserver>();
    
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private List<MetadataVisibilityObserver> metadataVisibilityObservers = new LinkedList<MetadataVisibilityObserver>();
    
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String orderBy;
    
    /** List of MetadataDescriptors. */
    protected List<MetadataDescriptor> metadata = new LinkedList<MetadataDescriptor>();

    @JsonIgnoreProperties(ignoreUnknown = true)
    private List<WavelengthDescriptor> wavelengths;
   
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String creditedInstitutions;

    @JsonIgnoreProperties(ignoreUnknown = true)
    private Boolean useUcd = false;
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
    public final GeneralJavaScriptObject getTapTableMetadata() {
        return tapTableMetadata;
    }

    @Override
    public final void setTapTableMetadata(final GeneralJavaScriptObject metadata) {
        this.tapTableMetadata = metadata;
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
    public final String getPrimaryColor() {
        return primaryColor;
    }

    @Override
    public final void setPrimaryColor(final String inputPrimaryColor) {
        this.primaryColor = inputPrimaryColor;
        for (ColorChangeObserver observer : colorObservers) {
        	observer.onColorChange(this, inputPrimaryColor);
        }
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
        if(fovLimit == null) {
            return new Double(0);
        }
        return fovLimit;
    }

    @Override
    public final void setFovLimit(final Double inputFovLimit) {
        this.fovLimit = inputFovLimit;
    }

    @Override
    public String getArchiveURL() {
        return archiveURL == null ? "" : archiveURL;
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
    public String getArchiveColumn() {
        return this.archiveColumn == null ? "" : this.archiveColumn;
    }

    @Override
    public void setArchiveColumn(String archiveColumn) {
        this.archiveColumn = archiveColumn;
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
    
    @Override
    public void setMetadataVisibility(String tapName, boolean visibility) {
        MetadataDescriptor metadataDescriptor = getMetadataDescriptorByTapName(tapName);
        if(metadataDescriptor == null) {
            metadataDescriptor = new MetadataDescriptor();
            metadataDescriptor.setTapName(tapName);
            metadataDescriptor.setType(ColumnType.STRING);
            getMetadata().add(metadataDescriptor);
        }
        if(metadataDescriptor.getVisible() == null || metadataDescriptor.getVisible() != visibility) {
            metadataDescriptor.setVisible(visibility);
            for (MetadataVisibilityObserver observer : metadataVisibilityObservers) {
                observer.onVisibilityChange(tapName, visibility);
            }
        }
    }

    @Override
    public void registerMetadataVisibilityObserver(MetadataVisibilityObserver observer) {
        metadataVisibilityObservers.add(observer);
    }
    
    @Override
    public void unregisterMetadataVisibilityObserver(MetadataVisibilityObserver observer) {
        metadataVisibilityObservers.remove(observer);
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
    public final List<WavelengthDescriptor> getWavelengths() {
        return wavelengths;
    }

    @Override
    public final void setWavelengths(List<WavelengthDescriptor> wavelengths) {
        this.wavelengths = wavelengths;
        if(getPrimaryColor() == null) {
            setPrimaryColor(ESASkyColors.getColorFromWavelength(getCenterWavelengthValue()));
        }
    }
    
    @JsonIgnore
    @Override
    public double getCenterWavelengthValue() {
        double minWavelength = 100;
        double maxWavelength = 0;
	    for(WavelengthDescriptor w : getWavelengths()) {
	        ArrayList<Double> range = w.getRange();
	        minWavelength = Math.min(range.get(0), minWavelength);
	        maxWavelength = Math.max(range.get(1), maxWavelength);
	    }
        
        return (maxWavelength + minWavelength) / 2;
    }
    
	@Override
    public final String getCreditedInstitutions() {
    	return creditedInstitutions;
    }
    
    @Override
    public final void setCreditedInstitutions(final String creditedInstitutions) {
    	this.creditedInstitutions = creditedInstitutions;
    }
    
    @Override
    public String getTapRaColumn() {
        return tapRaColumn;
    }

    @Override
    public void setTapRaColumn(String tapRaColumn) {
        this.tapRaColumn = tapRaColumn;
    }

    @Override
    public String getTapDecColumn() {
        return tapDecColumn;
    }

    @Override
    public void setTapDecColumn(String tapDecColumn) {
        this.tapDecColumn = tapDecColumn;
    }
    
    public final void setSampEnabled(final Boolean inputSampEnabled) {
        this.sampEnabled = inputSampEnabled;
    }

    public final Boolean getSampEnabled() {
        if(sampEnabled == null) {
            return false;
        }
        return this.sampEnabled;
    }
   
    public String getTapSTCSColumn() {
        return tapSTCSColumn == null ? "" : tapSTCSColumn;
    }

    public final void setTapSTCSColumn(final String inputTapSTCSColumn) {
        this.tapSTCSColumn = inputTapSTCSColumn;
    }

    @Override
    public String getTapQuery(String tapContext, String metadataAdql, String responseFormat) {
        Long timecall = System.currentTimeMillis();
        String adqlParameterAndValue = "";
        String adql = URL.encodeQueryString(metadataAdql);
        if(!adql.isEmpty()) {
            adqlParameterAndValue = "&query=" + adql;
        }

        Log.debug("[getTapQuery()] timecall " + timecall);
        return tapContext + "/tap/sync?request=doQuery&lang=ADQL&format="
        + responseFormat + adqlParameterAndValue + "&timecall=" + timecall;
    }
    
    
    public String getUniqueIdentifierField(){
        return uniqueIdentifierField;
    }
    
    public void setUniqueIdentifierField(String field){
        uniqueIdentifierField = field;
    }
    
    public String getSecondaryColor() {
        return secondaryColor;
    }

    public void setSecondaryColor(String secondaryColor) {
        this.secondaryColor = secondaryColor;
    }
    
    public String getExtraPopupDetailsByTapName() {
        return extraPopupDetailsByTapName;
    }

    public void setExtraPopupDetailsByTapName(String extraPopupDetailsByTapName) {
        this.extraPopupDetailsByTapName = extraPopupDetailsByTapName;
    }
    
    public void setIcon(String icon) {}
    

    public String getSampUrl() {
        return sampUrl;
    }

    public void setSampUrl(String sampUrl) {
        this.sampUrl = sampUrl;
    }

    public String getDdProductURI() {
        return ddProductURI;
    }

    public void setDdProductURI(String ddProductURI) {
        this.ddProductURI = ddProductURI;
    }

    public final String getDdBaseURL() {
        return ddBaseURL;
    }

    public final void setDdBaseURL(final String inputDDBaseURL) {
        this.ddBaseURL = inputDDBaseURL;
    }
    
    @Override
    public String getDescriptorId() {
        return descriptorId;
    }

    @Override
    public void setDescriptorId(String descriptorId) {
        this.descriptorId = descriptorId;
    }
    
    @Override
    public String generateId() {
         return getDescriptorId() + generateNextTabCount();
    }
    
    public int getShapeLimit() {
        return shapeLimit;
    }

    public void setShapeLimit(int shapeLimit) {
        this.shapeLimit = shapeLimit;
    }
    
    @Override
    public Boolean getUseIntersectPolygonInsteadOfContainsPoint() {
        return useIntersectPolygonInsteadOfContainsPoint == null ? false: useIntersectPolygonInsteadOfContainsPoint;
    }
    
    public void setUseIntersectPolygonInsteadOfContainsPoint(boolean useInstersect) {
        this.useIntersectPolygonInsteadOfContainsPoint = useInstersect;
    }
    
    @JsonIgnore
    public GeneralJavaScriptObject getMetaDataJSONObject() {
    	
    	GeneralJavaScriptObject object = GeneralJavaScriptObject.createJsonObject("{}");
    	int i = 0;
    	for(MetadataDescriptor md : getMetadata()) {
    		GeneralJavaScriptObject mdObject = md.toJSONObject();
    		mdObject.setProperty("index", Integer.toString(i));
    		object.setProperty(md.getTapName(), mdObject);
    		i++;
    	}
    	
    	return object;
    }

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}


    @JsonIgnore
    private SearchArea searchArea;

    @JsonIgnore
    @Override
    public void setSearchArea(SearchArea searchArea) {
        this.searchArea = searchArea;
    }

    @JsonIgnore
    @Override
    public SearchArea getSearchArea() {
        return this.searchArea;
    }

    @JsonIgnore
    @Override
    public boolean hasSearchArea() {
        return getSearchArea() != null;
    }

    @JsonIgnore
    @Override
    public String getSearchAreaShape() {
        String shape = "";
        if (hasSearchArea()) {
            if (searchArea.isCircle()) {
                CoordinatesObject coordinate = searchArea.getJ2000Coordinates()[0];
                shape =  "CIRCLE('ICRS'," + coordinate.getRaDeg()+ "," + coordinate.getDecDeg() + "," + searchArea.getRadius() + ")";
            } else {
                CoordinatesObject[] coordinates = searchArea.getJ2000Coordinates();
                String coordinateStr = Arrays.stream(coordinates)
                    .map(point -> point.getRaDeg() + "," + point.getDecDeg())
                    .collect(Collectors.joining(","));

                shape = "POLYGON('ICRS'," + coordinateStr + ")";
            }
        }
        return shape;
    }


    @Override
    public  String getUcdColumnName(String ucdName) {
        if (this.tapTableMetadata == null) {
            return "";
        }

        GeneralJavaScriptObject[] metadataColumns = GeneralJavaScriptObject.convertToArray(this.tapTableMetadata);
        final String metaMain = UCD.META_MAIN.getValue();
        String columnName = "";
        for(int i = 0; i < metadataColumns.length; i++) {
            if (metadataColumns[i].hasProperty("ucd") && metadataColumns[i].getStringProperty("ucd").contains(ucdName)) {
                if (columnName.equals("") || metadataColumns[i].getStringProperty("ucd").contains(metaMain)) {
                    columnName =  metadataColumns[i].hasProperty("column_name")
                            ? metadataColumns[i].getStringProperty("column_name")
                            : metadataColumns[i].getStringProperty("name");
                }
            }
        }
        return columnName;
    }

    @JsonIgnore
    @Override
    public boolean useUcd() {
        return useUcd;
    }

    public void setUseUcd(Boolean useUcd) {
        this.useUcd = useUcd;
    }
}
