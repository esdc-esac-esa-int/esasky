package esac.archive.esasky.ifcs.model.coordinatesutils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Coordinate{
	public double ra;
	public double dec;
	
	@JsonCreator
	public Coordinate(@JsonProperty("ra") double ra, @JsonProperty("dec")double dec){
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
