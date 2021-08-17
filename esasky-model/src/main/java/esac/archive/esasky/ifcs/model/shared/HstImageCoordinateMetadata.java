package esac.archive.esasky.ifcs.model.shared;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import esac.archive.esasky.cl.wcstransform.module.jskycoords.Point2D;
import esac.archive.esasky.cl.wcstransform.module.jskycoords.WCSTransform;
import esac.archive.esasky.cl.wcstransform.module.jskycoords.worldpos;
import esac.archive.esasky.ifcs.model.coordinatesutils.Coordinate;

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
		this.fov = Math.abs(referenceScale.get(0));
	}
	public void setFov(double fov) {
		this.fov = fov;
	}
	@JsonSetter("ReferenceValue")
	public void setReferenceValue(List<Double> referenceValues) {
		this.coordinate = new Coordinate(referenceValues.get(0), referenceValues.get(1));
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
	
	public void scaleToCorrectValues() {
		moveReferenceToCenter();
		scaleFoV();
	}
	
	public void scaleFoV() {
		this.fov = fov * imageDimensions.get(0);
	}
	
	public void moveReferenceToCenter() {
		WCSTransform wcs = new WCSTransform(coordinate.ra, coordinate.dec,
				-this.fov * 3600, this.fov * 3600, referencePixels.get(0), referencePixels.get(1),
				imageDimensions.get(0), imageDimensions.get(1), rotation, 2000, 2000, "-" + coordinateSystemProjection);
		Point2D.Double pos = worldpos.getPosition(imageDimensions.get(0) / 2, imageDimensions.get(1) / 2, wcs);
		this.coordinate.ra = pos.x;
		this.coordinate.dec = pos.y;
		this.referencePixels.set(0, imageDimensions.get(0) / 2.0);
		this.referencePixels.set(1, imageDimensions.get(1) / 2.0);
	}
	
	
}
