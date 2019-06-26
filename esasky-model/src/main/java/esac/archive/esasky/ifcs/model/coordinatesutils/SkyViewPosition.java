package esac.archive.esasky.ifcs.model.coordinatesutils;

public class SkyViewPosition{
	private double fov;
	private Coordinate coordinate;
	
	public SkyViewPosition(Coordinate coordinate, Double fov) {
		this.coordinate = coordinate;
		this.fov = fov;
	}

	public double getFov() {
		return fov;
	}

	public void setFov(double fov) {
		this.fov = fov;
	}

	public Coordinate getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(Coordinate coordinate) {
		this.coordinate = coordinate;
	}
}
