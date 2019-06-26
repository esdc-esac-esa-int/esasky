package esac.archive.esasky.cl.web.client.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import esac.archive.esasky.cl.web.client.api.DefaultValues;

public class JSONWrapper {

	JSONDataList overalySet;

	@JsonCreator
	public JSONWrapper(@JsonProperty("type") String type){
		if (type.equals("footprint")){
			this.overalySet = new JSONDataList(DefaultValues.JSON_TYPE_FOOTPRINT);
		}else if (type.equals("source")){
			this.overalySet = new JSONDataList(DefaultValues.JSON_TYPE_CATALOGUE);
		}
	}
	
	
	public JSONDataList getOverlaySet() {
		return overalySet;
	}

	public void setJSONDataList(JSONDataList overalySet) {
		this.overalySet = overalySet;
	}

}
