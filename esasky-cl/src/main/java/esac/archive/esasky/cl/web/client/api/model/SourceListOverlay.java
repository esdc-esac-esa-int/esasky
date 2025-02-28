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

import esac.archive.esasky.cl.web.client.api.DefaultValues;

public class SourceListOverlay implements IOverlay {

	String catalogueName;
	String cooframe;
	String color;
	Integer lineWidth;
	List<Source> skyObjectList = new LinkedList<Source>();

	Boolean refreshable = false;

	@Override
	public String getOverlayName() {
		return catalogueName;
	}

	@Override
	public void setOverlayName(String catalogueName) {
		this.catalogueName = catalogueName;
		if ("".equals(catalogueName) || null == catalogueName) {
			this.catalogueName = DefaultValues.CATALOGUE_DEFAULT_NAME;
		}
	}

	@Override
	public String getCooframe() {
		return cooframe;
	}

	@Override
	public void setCooframe(String cooframe) {
		this.cooframe = cooframe;
	}

	@Override
	public String getColor() {
		return color;
	}

	@Override
	public void setColor(String color) {
		this.color = color;
		if ("".equals(color) || null == color) {
			this.color = DefaultValues.CATALOGUE_DEFAULT_COLOR;
		}
	}

	@Override
	public List<Source> getSkyObjectList() {
		return skyObjectList;
	}

	public void setSkyObjectList(List<Source> skyObjects) {
		this.skyObjectList = skyObjects;
	}

	@Override
	public int getLineWidth() {
		return lineWidth;
	}

	@Override
	public void setLineWidth(Integer lineWidth) {
		this.lineWidth = lineWidth;
		if (null == lineWidth) {
			this.lineWidth = DefaultValues.CATALOGUE_DEFAULT_LINEWIDTH;
		}
	}

	@Override
	public boolean getRefreshable() {
		return this.refreshable;
	}

	@Override
	public void setRefreshable(boolean refreshable) {
		this.refreshable=refreshable;
	}

}