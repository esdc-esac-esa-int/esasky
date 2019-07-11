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
	
	public boolean compare(SkyViewPosition comparison) {
		return this.compare(comparison, 0.0);
	}

	public boolean compare(SkyViewPosition comparison, double precision) {
		// The idea is to compare fov:s as relative sizes and use that size
		// as an absolute value for the coordinate comparison
		if(Math.abs(this.fov-comparison.fov)<=precision*this.fov) {
			if(this.coordinate.compare(comparison.coordinate, precision*this.fov)){
				return true;
			}
		}
		return false;
	}
}
