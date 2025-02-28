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

package esac.archive.esasky.ifcs.model.shared;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import esac.archive.esasky.ifcs.model.coordinatesutils.Coordinate;
import esac.archive.esasky.ifcs.model.wcstransform.Point2D;
import esac.archive.esasky.ifcs.model.wcstransform.WCSTransform;
import esac.archive.esasky.ifcs.model.wcstransform.worldpos;

public class HstImageCoordinateMetadata {
	@JsonProperty("CoordinateFrame")
	private String coordinateFrame;
	
	@JsonProperty("Equinox")
	private String equinox;
	
	@JsonProperty("CoordinateSystemProjection")
	private String coordinateSystemProjection;
	
	@JsonProperty("Quality")
	private String quality;
	
	@JsonProperty("Rotation")
	private double rotation;
	
	@JsonProperty("Scale")
	private List<Double> scale;

	private double fov;
	
	@JsonProperty("ReferenceValue")
	private List<Double> referenceValue;
	
	private Coordinate coordinate;
	
	@JsonProperty("ReferenceDimension")
	private List<Integer> imageDimensions;
	
	@JsonProperty("ReferencePixel")
	private List<Double> referencePixels;
	
	@JsonProperty("Stcs")
	private String stcs;
	
	
	public String getCoordinateFrame() {
		return coordinateFrame;
	}
	public String getEquinox() {
		return equinox;
	}
	public String getCoordinateSystemProjection() {
		return coordinateSystemProjection;
	}
	public String getQuality() {
		return quality;
	}
	public double getRotation() {
		return rotation;
	}
	public double getFov() {
		return fov;
	}
	public Coordinate getCoordinate() {
		return coordinate;
	}
	public List<Integer> getImageDimensions() {
		return imageDimensions;
	}
	public List<Double> getReferencePixels() {
		return referencePixels;
	}
	public void setCoordinateFrame(String coordinateFrame) {
		this.coordinateFrame = coordinateFrame;
	}
	public void setEquinox(String equinox) {
		this.equinox = equinox;
	}
	public void setCoordinateSystemProjection(String coordinateSystemProjection) {
		this.coordinateSystemProjection = coordinateSystemProjection;
	}
	public void setQuality(String quality) {
		this.quality = quality;
	}
	public void setRotation(double rotation) {
		this.rotation = rotation;
	}
	@JsonSetter("Scale")
	public void setScale(List<Double> referenceScale) {
		this.scale = referenceScale;
	}
	public void setFov(double fov) {
		this.fov = fov;
	}
	@JsonSetter("ReferenceValue")
	public void setReferenceValue(List<Double> referenceValues) {
		this.referenceValue = referenceValues;
	}
	public void setCoordinate(Coordinate coordinate) {
		this.coordinate = coordinate;
	}
	public void setImageDimensions(List<Integer> imageDimensions) {
		this.imageDimensions = imageDimensions;
	}
	public void setReferencePixels(List<Double> referencePixels) {
		this.referencePixels = referencePixels;
	}
	
	public String getStcs() {
		return stcs;
	}
	public void setStcs(String stcs) {
		this.stcs = stcs;
	}
	public void scaleToCorrectValues() {
		this.coordinate = new Coordinate(referenceValue.get(0), referenceValue.get(1));
		moveReferenceToCenter();
		scaleFoV();
	}
	
	public void scaleFoV() {
		this.fov = Math.abs(scale.get(0)) * imageDimensions.get(0);
	}
	
	public void moveReferenceToCenter() {
		WCSTransform wcs = new WCSTransform(coordinate.getRa(), coordinate.getDec(),
				 scale.get(0) * 3600, scale.get(1) * 3600, referencePixels.get(0), referencePixels.get(1),
				imageDimensions.get(0), imageDimensions.get(1), rotation, 2000, 2000, "-" + coordinateSystemProjection);
		Point2D.Double pos = worldpos.getPosition((double) imageDimensions.get(0) / 2, (double) imageDimensions.get(1) / 2, wcs);
		this.coordinate.setRa(pos.x);
		this.coordinate.setDec(pos.y);
		
		createStcs(wcs);
	}
	
	private void createStcs(WCSTransform wcs) {
		String stcs = "POLYGON ICRS ";
		
		stcs += point2Stcs(wcs, 0.0, 0.0) + " ";
		stcs += point2Stcs(wcs, imageDimensions.get(0), 0.0) + " ";
		stcs += point2Stcs(wcs, imageDimensions.get(0), imageDimensions.get(1)) + " ";
		stcs += point2Stcs(wcs, 0.0, imageDimensions.get(1));
		
		this.stcs = stcs;
	}
	
	private String point2Stcs(WCSTransform wcs, double posX, double posY) {
		Point2D.Double pos = worldpos.getPosition(posX, posY, wcs);
		String stcs = Double.toString(pos.x) + " ";
		stcs += Double.toString(pos.y);
		return stcs;
	}
	
	
}
