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
