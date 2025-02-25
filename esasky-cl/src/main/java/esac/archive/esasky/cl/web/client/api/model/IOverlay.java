/*
ESASky
Copyright (C) 2025 Henrik Norman

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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ 
	@JsonSubTypes.Type(value = FootprintListOverlay.class, name = "FootprintListOverlay"),
	@JsonSubTypes.Type(value = SourceListOverlay.class, name = "SourceListOverlay") })
public interface IOverlay {

	public String getOverlayName();

	public void setOverlayName(String overlayName);

	public String getColor();

	public void setColor(String color);

	public String getCooframe();

	public void setCooframe(String cooframe);

	public int getLineWidth();

	public void setLineWidth(Integer lineWidth);

	public boolean getRefreshable();

	public void setRefreshable(boolean refreshable);

	public List<? extends GeneralSkyObject> getSkyObjectList();

}
