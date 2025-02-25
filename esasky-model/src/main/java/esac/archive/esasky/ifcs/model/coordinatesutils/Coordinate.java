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

package esac.archive.esasky.ifcs.model.coordinatesutils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Coordinate{
	private double ra;
	private double dec;

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
	
	public double distance(Coordinate comparison) {
		double raDist = Math.min(Math.abs(this.ra - comparison.ra), Math.abs(360 - this.ra - comparison.ra));
		double decDist = Math.abs(this.dec - comparison.dec);
		return Math.pow(Math.pow(raDist, 2) + Math.pow(decDist, 2), 0.5);
	}
	
	public double getRa() {
	    return ra;
	}
	
	public void setRa(double ra) {
	    this.ra = ra;
	}
	
    public double getDec() {
        return dec;
    }

    public void setDec(double dec) {
        this.dec = dec;
    }
}
