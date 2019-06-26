package esac.archive.ammi.ifcs.model.multiretrievalbean;


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
