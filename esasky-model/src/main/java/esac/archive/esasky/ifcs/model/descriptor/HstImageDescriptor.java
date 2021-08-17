package esac.archive.esasky.ifcs.model.descriptor;

import java.util.List;

import com.fasterxml.jackson.annotation.*;

import esac.archive.esasky.ifcs.model.shared.HstImageCoordinateMetadata;
import esac.archive.esasky.ifcs.model.shared.HstLargeImageMetadata;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HstImageDescriptor{
	
	private String id;
	private String title;
	private String description;
	private int priority;
	@JsonProperty("release_date")
	private String releaseDate;
	@JsonProperty("last_modified")
	private String lastModified;
	private String credit;
	
	private String tilesUrl;
	private List<String> tiles;

	@JsonProperty("large")
	private HstLargeImageMetadata largeImageMetaData;
	
	@JsonProperty("coordinate_metadata")
	private HstImageCoordinateMetadata coordinateMetadata;

	
	public String getId() {
		return id;
	}
	public String getTitle() {
		return title;
	}
	public String getDescription() {
		return description;
	}
	public int getPriority() {
		return priority;
	}
	public String getReleaseDate() {
		return releaseDate;
	}
	public String getLastModified() {
		return lastModified;
	}
	public String getCredit() {
		return credit;
	}
	public String getTilesUrl() {
		return tilesUrl;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}
	public void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}
	public void setCredit(String credit) {
		this.credit = credit;
	}
	
	public void setTiles(List<String> tiles) {
		if(tiles.size() > 0) {
			String first = tiles.get(0);
			this.tilesUrl = first.split("TileGroup")[0];
		}else {
			this.tilesUrl = null;
		}
	}
	
	public void setTilesUrl(String tilesUrl) {
		this.tilesUrl = tilesUrl;

	}
	public HstLargeImageMetadata getLargeImageMetaData() {
		return largeImageMetaData;
	}
	public void setLargeImageMetaData(HstLargeImageMetadata largeImageMetaData) {
		this.largeImageMetaData = largeImageMetaData;
	}
	
	public HstImageCoordinateMetadata getCoordinateMetadata() {
		return coordinateMetadata;
	}
	public void setCoordinateMetadata(HstImageCoordinateMetadata coordinateMetadata) {
		this.coordinateMetadata = coordinateMetadata;
	}
	
	public void scaleToCorrectValues() {
		coordinateMetadata.scaleToCorrectValues();
	}
	
}
