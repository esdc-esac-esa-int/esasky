/*
ESASky
Copyright (C) 2025 European Space Agency

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
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OutreachImageDescriptor {

	@JsonProperty("identifier")
	private String id;
	private String title;
	
	@JsonProperty("stc_s")
	private String stcs;
	
	@JsonProperty("object_name")
	private String objectName;
	private String description;
	private String credit;
	
	@JsonProperty("tiles_url")
	private String tilesUrl;
	
	@JsonProperty("large_url")
	private String largeUrl;
	
	@JsonProperty("ra_deg")
	private double ra;
	@JsonProperty("dec_deg")
	private double dec;
	
	private int priority;
	private double rotation;

	@JsonProperty("fov_size")
	private double fovSize;
	
	@JsonProperty("pixel_size")
	private int[] pixelSize;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getStcs() {
		return stcs;
	}

	public void setStcs(String stcs) {
		this.stcs = stcs;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCredit() {
		return credit;
	}

	public void setCredit(String credit) {
		this.credit = credit;
	}

	public String getTilesUrl() {
		return tilesUrl;
	}

	public void setTilesUrl(String tilesUrl) {
		this.tilesUrl = tilesUrl;
	}

	public String getLargeUrl() {
		return largeUrl;
	}

	public void setLargeUrl(String largeUrl) {
		this.largeUrl = largeUrl;
	}

	public double getRa() {
		return ra;
	}

	public void setRa(double ra) {
		this.ra = ra;
	}

	public double getDec() {
		return dec;
	}

	public void setDec(double dec) {
		this.dec = dec;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public double getRotation() {
		return rotation;
	}

	public void setRotation(double rotation) {
		this.rotation = rotation;
	}

	public double getFovSize() {
		return fovSize;
	}

	public void setFovSize(double fovSize) {
		this.fovSize = fovSize;
	}

	public int[] getPixelSize() {
		return pixelSize;
	}

	public void setPixelSize(int[] pixelSize) {
		this.pixelSize = pixelSize;
	}
	
//	public void setPixelSize(String str) {
//		str = str.trim();
//		String[] strArgs = str.substring(1, str.length() - 1).trim().split("\\s*,\\s*");
//		this.pixelSize = new int[] {Integer.parseInt(strArgs[0]), Integer.parseInt(strArgs[1])};
//	}
	
}
