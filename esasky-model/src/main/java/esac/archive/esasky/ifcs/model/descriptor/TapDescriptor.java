package esac.archive.esasky.ifcs.model.descriptor;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.GWT;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.shared.contentdescriptors.IContentDescriptor;
import esac.archive.esasky.ifcs.model.shared.contentdescriptors.Name;
import esac.archive.esasky.ifcs.model.shared.contentdescriptors.ObsCore;
import esac.archive.esasky.ifcs.model.shared.contentdescriptors.UCD;

import java.util.*;

/**
 * Generalized descriptor that works with all TAP data.
 */
public class TapDescriptor extends TapDescriptorBase {

    public interface MetadataListMapper extends ObjectMapper<List<TapMetadataDescriptor>> { }

    private Map<String, Object> properties;


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
     * Set properties map.
     *
     * @param properties the map of properties
     */
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    /**
     * Returns the properties map.
     *
     * @return the properties map object.
     */
    public Map<String, Object> getProperties() {
        if (this.properties == null) {
            this.properties = new HashMap<>();
        }

        return properties;
    }

    /**
     * Finds the property value corresponding to the key or null if
     * the key was not found.
     *
     * @param key the key of the property value to be retrieved.
     * @return the property or null.
     */
    protected Object getProperty(String key) {
        return properties.getOrDefault(key, null);
    }

    /**
     * Finds the property value corresponding to the key and converts it
     * to a String.
     *
     * @param key the key of the property value to be retrieved.
     * @return the property as a String or null.
     */
    protected String getPropertyString(String key) {
        Object property = getProperty(key);
        return property != null ? property.toString() : null;
    }


    /**
     * Finds the property value corresponding to ONE of the provided content descriptors.
     *
     * @param contentDescriptors UCD values, UTypes etc.
     * @return the property object.
     */
    protected Object getPropertyAny(IContentDescriptor... contentDescriptors) {
        String metadataName = getMetadataNameAny(contentDescriptors);
        return getProperty(metadataName);
    }

    /**
     * Finds the property value corresponding to ALL the provided content descriptors.
     *
     * @param contentDescriptors UCD values, UTypes etc.
     * @return the property object.
     */
    protected Object getPropertyAll(IContentDescriptor... contentDescriptors) {
        String metadataName = getMetadataNameAll(contentDescriptors);
        return getProperty(metadataName);
    }

    /**
     * Finds the metadata object matching ONE of the provided UCD values.
     *
     * @param ucdList list of UCD values.
     * @return the metadata object or null.
     */
    protected TapMetadataDescriptor getMetadataAny(UCD... ucdList) {
        if (ucdList.length == 0) {
            return null;
        }

        TapMetadataDescriptor descriptor = null;
        for (TapMetadataDescriptor metaDesc : getMetadata()) {
            String ucdStr = metaDesc.getUcd();

            boolean postiveMatch = Arrays.stream(ucdList).filter(u -> !u.isNegative()).anyMatch(u -> u.matches(ucdStr));
            boolean negativeMatch =  Arrays.stream(ucdList).filter(u -> u.isNegative()).allMatch(u -> u.matches(ucdStr));
            if (ucdStr != null && postiveMatch && negativeMatch) {
                descriptor = metaDesc;
                if (UCD.isMain(ucdStr)) {
                    break;
                }
            }
        }
        return descriptor;
    }

    /**
     * Finds the metadata object matching ALL the provided UCD values.
     *
     * @param ucdList list of UCD values.
     * @return the metadata object or null.
     */
    protected TapMetadataDescriptor getMetadataAll(UCD... ucdList) {
        if (ucdList.length == 0) {
            return null;
        }

        TapMetadataDescriptor descriptor = null;
        for (TapMetadataDescriptor metaDesc : getMetadata()) {
            String ucdStr = metaDesc.getUcd();
            if (ucdStr != null && Arrays.stream(ucdList).allMatch(u -> u.matches(ucdStr))) {
                descriptor = metaDesc;
                if (UCD.isMain(ucdStr)) {
                    break;
                }
            }
        }
        return descriptor;
    }

    /**
     * Finds the metadata object matching ONE of the provided ObsCore values.
     *
     * @param obsCoreList list of ObsCore values.
     * @return the metadata object or null.
     */
    protected TapMetadataDescriptor getMetadataAny(ObsCore... obsCoreList) {
        if (obsCoreList.length == 0) {
            return null;
        }

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
     * Finds the metadata object matching ALL the provided ObsCore values.
     *
     * @param obsCoreList list of ObsCore values.
     * @return the metadata object or null.
     */
    protected TapMetadataDescriptor getMetadataAll(ObsCore... obsCoreList) {
        if (obsCoreList.length == 0) {
            return null;
        }

        TapMetadataDescriptor descriptor = null;
        for (TapMetadataDescriptor metaDesc : getMetadata()) {
            String utypeStr = metaDesc.getUtype();

            if (utypeStr != null && Arrays.stream(obsCoreList).allMatch(u -> u.matches(utypeStr))) {
                descriptor = metaDesc;
                break;
            }
        }
        return descriptor;
    }


    /**
     * Finds the metadata object matching ONE of the provided Name values.
     *
     * @param nameList list of name values.
     * @return the metadata object or null.
     */
    protected TapMetadataDescriptor getMetadataAny(Name... nameList) {
        if (nameList.length == 0) {
            return null;
        }

        TapMetadataDescriptor descriptor = null;
        for (TapMetadataDescriptor metaDesc : getMetadata()) {
            String nameStr = metaDesc.getName();

            if (nameStr != null && Arrays.stream(nameList).anyMatch(u -> u.matches(nameStr))) {
                descriptor = metaDesc;
                break;
            }
        }
        return descriptor;
    }

    /**
     * Finds the metadata object matching ALL the provided Name values.
     *
     * @param nameList list of Name values.
     * @return the metadata object or null.
     */
    protected TapMetadataDescriptor getMetadataAll(Name... nameList) {
        if (nameList.length == 0) {
            return null;
        }

        TapMetadataDescriptor descriptor = null;
        for (TapMetadataDescriptor metaDesc : getMetadata()) {
            String nameStr = metaDesc.getName();

            if (nameStr != null && Arrays.stream(nameList).allMatch(u -> u.matches(nameStr))) {
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
    protected TapMetadataDescriptor getMetadataAny(IContentDescriptor... contentDescriptors) {
        TapMetadataDescriptor descriptor = getMetadataAny(Arrays.stream(contentDescriptors).filter(cd -> cd instanceof ObsCore).toArray(ObsCore[]::new));

        if (descriptor == null) {
            descriptor = getMetadataAny(Arrays.stream(contentDescriptors).filter(cd -> cd instanceof UCD).toArray(UCD[]::new));
        }

        if (descriptor == null) {
            descriptor = getMetadataAny(Arrays.stream(contentDescriptors).filter(cd -> cd instanceof Name).toArray(Name[]::new));
        }

        return descriptor;
    }

    /**
     * Finds the metadata object matching one of the provided content descriptors.
     *
     * @param contentDescriptors UCD values, UTypes etc.
     * @return the metadata object.
     */
    protected TapMetadataDescriptor getMetadataAll(IContentDescriptor... contentDescriptors) {
        TapMetadataDescriptor descriptor = getMetadataAll(Arrays.stream(contentDescriptors).filter(cd -> cd instanceof ObsCore).toArray(ObsCore[]::new));

        if (descriptor == null) {
            descriptor = getMetadataAll(Arrays.stream(contentDescriptors).filter(cd -> cd instanceof UCD).toArray(UCD[]::new));
        }

        if (descriptor == null) {
            descriptor = getMetadataAll(Arrays.stream(contentDescriptors).filter(cd -> cd instanceof Name).toArray(Name[]::new));
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
    protected String getMetadataNameAny(IContentDescriptor... contentDescriptors) {
        TapMetadataDescriptor descriptor = getMetadataAny(contentDescriptors);

        if (descriptor != null) {
            return descriptor.getName();
        } else {
            return null;
        }
    }


    /**
     * Returns the name of the metadata object. If no metadata object is found
     * it returns null.
     *
     * @param contentDescriptors UCD values, UTypes etc.
     * @return the metadata name as a String or null.
     */
    protected String getMetadataNameAll(IContentDescriptor... contentDescriptors) {
        TapMetadataDescriptor descriptor = getMetadataAll(contentDescriptors);

        if (descriptor != null) {
            return descriptor.getName();
        } else {
            return null;
        }
    }


    /********************************
     *  Metadata Getters & setters
     ********************************/

    public List<TapMetadataDescriptor> getMetadata() {
        if (metadata == null) {
            metadata = new ArrayList<>();
        }
        return metadata;
    }

    public GeneralJavaScriptObject getRawMetadata() {
        if (rawMetadata == null && metadata != null) {
            MetadataListMapper metadataMapper = GWT.create(MetadataListMapper.class);
            String json = metadataMapper.write(metadata);
            rawMetadata = GeneralJavaScriptObject.createJsonObject(json);
        }

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
        return getMetadataNameAny(UCD.POS_EQ_RA.positive(), UCD.STAT_ERROR.negative(),
                ObsCore.S_RA,
                Name.RA, Name.S_RA);
    }

    public String getDecColumn() {
        return getMetadataNameAny(UCD.POS_EQ_DEC.positive(), UCD.STAT_ERROR.negative(),
                ObsCore.S_DEC,
                Name.DEC, Name.S_DEC);
    }

    public String getRegionColumn() {
        return getMetadataNameAny(UCD.POS_OUTLINE.positive(), UCD.META_PGSPHERE.negative(),
                ObsCore.S_REGION,
                Name.REGION, Name.S_REGION);
    }

    public String getIdColumn() {
        return getMetadataNameAny(UCD.META_ID.positive(), ObsCore.OBS_ID, ObsCore.OBS_CREATOR_DID);
    }

    public String getProperMotionRaColumn() {
        return getMetadataNameAll(UCD.POS_PM.positive(), UCD.POS_EQ_RA.positive(), UCD.STAT.negative());
    }

    public String getProperMotionDecColumn() {
        return getMetadataNameAll(UCD.POS_PM.positive(), UCD.POS_EQ_DEC.positive(), UCD.STAT.negative());
    }

    public String getProperMotionColumn() {
        return getMetadataNameAny(UCD.POS_PM.positive());
    }

    public boolean hasProperMotion() {
        return getProperMotionColumn() != null && !getProperMotionColumn().isEmpty();
    }

    public String getParallaxTrigColumn() {
        return getMetadataNameAll(UCD.POS_PARALLAX_TRIG.positive(), UCD.STAT.negative());
    }

    public String getRadialVelocityColumn() {
        return getMetadataNameAll(UCD.SPECT_DOPPLERVELOC.positive(), UCD.STAT.negative());
    }

    public String getNameColumn() {
        return getMetadataNameAny(UCD.META_ID.positive(), ObsCore.OBS_ID, ObsCore.TARGET_NAME);
    }

    public String getReferenceEpochColumn() {
        return getMetadataNameAny(UCD.META_REF.positive(), UCD.TIME_EPOCH.positive());
    }

    public String getDatasetSize() {
        return getMetadataNameAll(UCD.PHYS_SIZE.positive(), UCD.META_FILE.positive(), ObsCore.ACCESS_ESTSIZE);
    }

}
