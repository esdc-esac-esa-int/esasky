package esac.archive.esasky.ifcs.model.coordinatesutils;


public class Coordinate{
	public double ra;
	public double dec;
	
	public Coordinate(double ra, double dec){
		this.ra = ra;
		this.dec = dec;
	}
	
	public boolean compare(Coordinate comparison) {
		return this.compare(comparison, 0.0);
	}

	public boolean compare(Coordinate comparison, double precision) {
		if(Math.abs(this.ra-comparison.ra)<=precision) {
			if(Math.abs(this.dec-comparison.dec)<=precision) {
				return true;
			}
		}
		return false;
	}
}
