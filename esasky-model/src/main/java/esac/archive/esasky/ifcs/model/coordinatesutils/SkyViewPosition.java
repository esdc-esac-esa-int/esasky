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
		if (comparison == null) {
			return false;
		}
		// The idea is to compare fov:s as relative sizes and use that size
		// as an absolute value for the coordinate comparison
		if(Math.abs(this.fov/comparison.fov-1)<=precision) {
			if(this.coordinate.compare(comparison.coordinate, precision*this.fov)){
				return true;
			}
		}
		return false;
	}
}
