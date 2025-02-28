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

package esac.archive.esasky.cl.web.client.api.model;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import esac.archive.esasky.cl.web.client.api.DefaultValues;

public class JSONDataList {

	String overlayName;
	String color;
	String cooframe;
	Integer lineWidth;
	List<?> skyObjectList;
	@JsonIgnoreProperties(ignoreUnknown = true)
	String type;

	public JSONDataList(String type) {
		if (type.equals(DefaultValues.JSON_TYPE_FOOTPRINT)) {
			this.skyObjectList = new LinkedList<Footprint>();
			this.type = "footprint";
		} else if (type.equals(DefaultValues.JSON_TYPE_CATALOGUE)) {
			this.skyObjectList = new LinkedList<Source>();
			this.type = "source";
		}
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getOverlayName() {
		return overlayName;
	}

	public void setOverlayName(String overlayName) {
		this.overlayName = overlayName;

		if (type.equals(DefaultValues.JSON_TYPE_FOOTPRINT) && ("".equals(overlayName) || null == overlayName)) {
			this.overlayName = DefaultValues.FOOTPRINT_DEFAULT_NAME;
		} else if (type.equals(DefaultValues.JSON_TYPE_CATALOGUE) && ("".equals(overlayName) || null == overlayName)) {
			this.overlayName = DefaultValues.CATALOGUE_DEFAULT_NAME;
		}
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;

		if (type.equals(DefaultValues.JSON_TYPE_FOOTPRINT) && ("".equals(color) || null == color)) {
			this.color = DefaultValues.FOOTPRINT_DEFAULT_COLOR;
		} else if (type.equals(DefaultValues.JSON_TYPE_CATALOGUE) && ("".equals(overlayName) || null == overlayName)) {
			this.color = DefaultValues.CATALOGUE_DEFAULT_COLOR;
		}
	}

	public String getCooframe() {
		return cooframe;
	}

	public void setCooframe(String cooframe) {
		this.cooframe = cooframe;
	}

	public int getLineWidth() {
		return lineWidth;
	}

	public void setLineWidth(Integer lineWidth) {
		this.lineWidth = lineWidth;

		if (type.equals(DefaultValues.JSON_TYPE_FOOTPRINT) && null == lineWidth) {
			this.lineWidth = DefaultValues.FOOTPRINT_DEFAULT_LINEWIDTH;
		} else if (type.equals(DefaultValues.JSON_TYPE_CATALOGUE) && null == lineWidth) {
			this.lineWidth = DefaultValues.CATALOGUE_DEFAULT_LINEWIDTH;
		}
	}

	public List<?> getSkyObjectList() {
		return skyObjectList;
	}

	public void setSkyObjectList(List<?> skyObjectList) {
		this.skyObjectList = skyObjectList;
	}
}
