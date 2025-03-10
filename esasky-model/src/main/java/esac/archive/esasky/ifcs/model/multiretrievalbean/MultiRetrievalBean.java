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

package esac.archive.esasky.ifcs.model.multiretrievalbean;


public class MultiRetrievalBean  {
	
	
		public static final String TYPE_OBSERVATIONAL	= "observational";
		public static final String TYPE_CATALOG			= "catalog";
	
		private String type;
		private String mission;
		private String location;
		
		public MultiRetrievalBean(){};
		
		public MultiRetrievalBean(String type, String mission, String location) {
			setType(type);
			setMission(mission);
			setLocation(location);		
		}
				
		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getMission() {
			return mission;
		}

		public void setMission(String mission) {
			this.mission = mission;
		}

		public String getLocation() {
			return location;
		}

		public void setLocation(String location) {
			this.location = location;
		}
		
		public String toString() {
			String returnString = "(mission: " + this.mission + " location: " + location + ")";
			return returnString;
		}
		
}
