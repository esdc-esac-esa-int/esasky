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

import java.util.LinkedList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;

public class GeneralSkyObject implements ISkyObject {

	String name = null;
	String ra_deg = null;
	String dec_deg = null;
	List<MetadataAPI> data = new LinkedList<MetadataAPI>();
	Integer id;

	public GeneralSkyObject() {
		Log.debug("[GeneralSkyObject] ready");
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public String getRa_deg() {
		return ra_deg;
	}

	public void setRa_deg(String ra_deg) {
		this.ra_deg = ra_deg;
	}

	public String getDec_deg() {
		return dec_deg;
	}

	public void setDec_deg(String dec_deg) {
		this.dec_deg = dec_deg;
	}

	@Override
	public List<MetadataAPI> getData() {
		return data;
	}

	@Override
	public void setData(List<MetadataAPI> data) {
		this.data = data;
	}

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

}
