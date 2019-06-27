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

	public List<? extends GeneralSkyObject> getSkyObjectList();

	public void setSkyObjectList(List<? extends GeneralSkyObject> skyObjects);

}
