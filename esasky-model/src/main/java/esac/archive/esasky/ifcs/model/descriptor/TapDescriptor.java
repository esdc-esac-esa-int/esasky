package esac.archive.esasky.ifcs.model.descriptor;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.shared.IContentDescriptor;
import esac.archive.esasky.ifcs.model.shared.ObsCore;
import esac.archive.esasky.ifcs.model.shared.UCD;

import java.util.*;

/**
 * Generalized descriptor that works with all TAP data.
 */
public class TapDescriptor extends TapDescriptorBase {

    private Map<String, Object> properties;

    @JsonIgnore
    private List<TapMetadataDescriptor> metadata;

    @JsonIgnore
    private GeneralJavaScriptObject rawMetadata;

    /**
     * Catch-all setter for JSON properties.
     *
     * This method is not meant to be called directly, instead it is
     * used automatically when mapping a JSON-string to an object of this class.
     *
     * @param propertyKey the key of the value in the map.
     * @param value the value Object in the map.
     */
    @JsonAnySetter
    public void setProperties(String propertyKey, Object value) {
        if (this.properties == null) {
            this.properties = new HashMap<>();
        }
        this.properties.put(propertyKey, value);
    }

    /**
     * Finds the property value corresponding to the key or null if
     * the key was not found.
     *
     * @param key the key of the property value to be retrieved.
     * @return the property or null.
     */
    private Object getProperty(String key) {
        return properties.getOrDefault(key, null);
    }

    /**
     * Finds the property value corresponding to one of the provided content descriptors.
     *
     * @param contentDescriptors UCD values, UTypes etc.
     * @return the property object.
     */
    protected Object getProperty(IContentDescriptor... contentDescriptors) {
        String metadataName = getMetadataName(contentDescriptors);
        return getProperty(metadataName);
    }

    /**
     * Finds the metadata object matching one of the provided UCD values.
     *
     * @param ucdList list of UCD values.
     * @return the metadata object or null.
     */
    protected TapMetadataDescriptor getMetadata(UCD... ucdList) {
        TapMetadataDescriptor descriptor = null;
        for (TapMetadataDescriptor metaDesc : getMetadata()) {
            String ucdStr = metaDesc.getUcd();

            if (ucdStr != null && Arrays.stream(ucdList).anyMatch(u -> u.matches(ucdStr))) {
                descriptor = metaDesc;
                if (UCD.isMain(ucdStr)) {
                    break;
                }
            }
        }
        return descriptor;
    }

    /**
     * Finds the metadata object matching one of the provided ObsCore values.
     *
     * @param obsCoreList list of ObsCore values.
     * @return the metadata object or null.
     */
    protected TapMetadataDescriptor getMetadata(ObsCore... obsCoreList) {
        TapMetadataDescriptor descriptor = null;
        for (TapMetadataDescriptor metaDesc : getMetadata()) {
            String utypeStr = metaDesc.getUtype();

            if (utypeStr != null && Arrays.stream(obsCoreList).anyMatch(u -> u.matches(utypeStr))) {
                descriptor = metaDesc;
                break;
            }
        }
        return descriptor;
    }

    /**
     * Finds the metadata object matching one of the provided content descriptors.
     *
     * @param contentDescriptors UCD values, UTypes etc.
     * @return the metadata object.
     */
    protected TapMetadataDescriptor getMetadata(IContentDescriptor... contentDescriptors) {
        TapMetadataDescriptor descriptor = getMetadata(Arrays.stream(contentDescriptors).filter(cd -> cd instanceof UCD).toArray(UCD[]::new));
        if (descriptor == null) {
            descriptor = getMetadata(Arrays.stream(contentDescriptors).filter(cd -> cd instanceof ObsCore).toArray(ObsCore[]::new));
        }
        return descriptor;
    }

    /**
     * Returns the name of the metadata object. If no metadata object is found
     * it returns null.
     *
     * @param contentDescriptors UCD values, UTypes etc.
     * @return the metadata name as a String or null.
     */
    protected String getMetadataName(IContentDescriptor... contentDescriptors) {
        TapMetadataDescriptor descriptor = getMetadata(contentDescriptors);

        if (descriptor != null) {
            return descriptor.getName();
        } else {
            return null;
        }
    }


    /********************************
     *  Metadata Getters & setters
     ********************************/

    @Override
    public List<TapMetadataDescriptor> getMetadata() {
        if (metadata == null) {
            metadata = new ArrayList<>();
        }
        return metadata;
    }

    @Override
    public GeneralJavaScriptObject getRawMetadata() {
        return rawMetadata;
    }

    public void setMetadata(List<TapMetadataDescriptor> metadata) {
        this.metadata = metadata;
    }

    public void setRawMetadata(GeneralJavaScriptObject metadata) {
        this.rawMetadata = metadata;
    }

    /***************************
    *  Public Helper Methods
    ****************************/
    public String getRaColumn() {
        return getMetadataName(UCD.POS_EQ_RA, ObsCore.S_RA);
    }

    public String getDecColumn() {
        return getMetadataName(UCD.POS_EQ_DEC, ObsCore.S_DEC);
    }

    public String getRegionColumn() {
        return getMetadataName(UCD.POS_OUTLINE, UCD.OBS_FIELD, ObsCore.S_REGION);
    }

    public String getIdColumn() {
        return getMetadataName(UCD.META_ID, ObsCore.OBS_ID, ObsCore.OBS_CREATOR_DID);
    }

    public String getLongName() {
        return getMetadataName(UCD.META_ID, ObsCore.OBS_ID, ObsCore.TARGET_NAME);
    }

    public String getShortName() {
        return getLongName();
    }

    @Override
    public List<TapMetadataDescriptor> getColumnMetadata() {
        return getMetadata();
    }

    public String getMission() {
        return getProperty(UCD.INSTR_OBSTY, ObsCore.INSTRUMENT_NAME).toString();
    }

    public String getTableName() {
        return getProperty(UCD.META_TABLE, ObsCore.ACCESS_URL).toString();
    }
}
