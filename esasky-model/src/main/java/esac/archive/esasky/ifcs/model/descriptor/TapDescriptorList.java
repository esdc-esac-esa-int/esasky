package esac.archive.esasky.ifcs.model.descriptor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.GWT;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TapDescriptorList {
    public interface Mapper extends ObjectMapper<TapDescriptor> { }
    public interface MetadataMapper extends ObjectMapper<TapMetadataDescriptor> { }

    @JsonProperty("metadata")
    protected GeneralJavaScriptObject rawMetadata;

    @JsonProperty("data")
    protected GeneralJavaScriptObject rawData;

    @JsonIgnore
    protected List<TapDescriptor> descriptors;

    @JsonIgnore
    private List<TapMetadataDescriptor> descriptorMetadata;

    public int getTotal() {
        return descriptors != null ? descriptors.size() : 0;
    }

    public List<TapDescriptor> getDescriptors() {
        if (descriptors == null) {
            Mapper mapper = GWT.create(Mapper.class);
            MetadataMapper metadataMapper = GWT.create(MetadataMapper.class);
            return createDescriptors(mapper, metadataMapper);
        } else {
            return this.descriptors;
        }
    }

    public List<TapMetadataDescriptor> getDescriptorMetadata() {
        if (descriptorMetadata == null) {
            MetadataMapper metadataMapper = GWT.create(MetadataMapper.class);
            return createMetadata(metadataMapper);
        } else {
            return this.descriptorMetadata;
        }
    }

    private List<TapMetadataDescriptor> createMetadata(MetadataMapper metadataMapper) {
        descriptorMetadata = new ArrayList<>();
        for (GeneralJavaScriptObject rawMetadataSlice : GeneralJavaScriptObject.convertToArray(rawMetadata)) {
            String json = rawMetadataSlice.toJSONString();
            TapMetadataDescriptor metadataEntry = metadataMapper.read(json);
            descriptorMetadata.add(metadataEntry);
        }

        return descriptorMetadata;
    }

    private List<TapDescriptor> createDescriptors(Mapper dataMapper, MetadataMapper metadataMapper) {
        descriptors = new LinkedList<>();
        GeneralJavaScriptObject[] dataArray = formatData(rawData, rawMetadata);

        List<TapMetadataDescriptor> metadata = createMetadata(metadataMapper);

        for (GeneralJavaScriptObject data : dataArray) {
            String json = data.toJSONString();
            TapDescriptor descriptor = dataMapper.read(json);
            descriptor.setMetadata(metadata);
            descriptor.setRawMetadata(rawMetadata);
            descriptors.add(descriptor);
        }

        return descriptors;
    }

    // Format TAP data as a dictionary with metadata name as key
    protected native GeneralJavaScriptObject[] formatData(GeneralJavaScriptObject data, GeneralJavaScriptObject metadata)/*-{
        var resultItemArr = []
        for (var i = 0; i < data.length; i++) {
            var currentDataItem = data[i];
            var resultItem = {}
            for (var j = 0; j < metadata.length; j++) {
                resultItem[metadata[j].name] = currentDataItem[j]
            }
            resultItemArr.push(resultItem)
        }
        return resultItemArr;
    }-*/;
}