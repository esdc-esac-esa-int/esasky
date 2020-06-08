package esac.archive.esasky.cl.web.client.api.model;

import java.util.LinkedList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;

import esac.archive.esasky.cl.web.client.api.DefaultValues;

public class FootprintListOverlay implements IOverlay {

	String overlayName;
	String color;
	String cooframe;
	Integer lineWidth;
	List<Footprint> skyObjectList = new LinkedList<Footprint>();

	public FootprintListOverlay() {
		Log.debug("[FootprintListOverlay] Ready!!");
	}

	@Override
	public String getOverlayName() {
		return overlayName;
	}

	@Override
	public void setOverlayName(String overlayName) {
		this.overlayName = overlayName;
		if ("".equals(overlayName) || null == overlayName) {
			this.overlayName = DefaultValues.FOOTPRINT_DEFAULT_NAME;
		}
	}

	@Override
	public String getColor() {
		return color;
	}

	@Override
	public void setColor(String color) {
		this.color = color;
		if ("".equals(color) || null == color) {
			this.color = DefaultValues.FOOTPRINT_DEFAULT_COLOR;
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
	public List<Footprint> getSkyObjectList() {
		return skyObjectList;
	}

	public void setSkyObjectList(List<Footprint> skyObjects) {
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
			this.lineWidth = DefaultValues.FOOTPRINT_DEFAULT_LINEWIDTH;
		}
	}

}
