package esac.archive.esasky.ifcs.model.shared;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HstLargeImageMetadata {
	@JsonProperty("ResourceType")
	private String resourceType;
	
	@JsonProperty("MediaType")
	private String mediaType;
	
	@JsonProperty("ProjectionType")
	private String projectionType;
	
	@JsonProperty("URL")
	private String URL;
	
	@JsonProperty("FileSize")
	private int fileSize;
	
	@JsonProperty("Dimensions")
	private List<Integer> dimensions;

	@JsonProperty("Checksum")
	private String checksum;
	
	public String getResourceType() {
		return resourceType;
	}
	public String getMediaType() {
		return mediaType;
	}
	public String getProjectionType() {
		return projectionType;
	}
	public String getURL() {
		return URL;
	}
	public int getFileSize() {
		return fileSize;
	}
	public List<Integer> getDimensions() {
		return dimensions;
	}
	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}
	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}
	public void setProjectionType(String projectionType) {
		this.projectionType = projectionType;
	}
	public void setURL(String uRL) {
		URL = uRL;
	}
	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}
	public void setDimensions(List<Integer> dimensions) {
		this.dimensions = dimensions;
	}
	public String getChecksum() {
		return checksum;
	}
	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}
	
}
