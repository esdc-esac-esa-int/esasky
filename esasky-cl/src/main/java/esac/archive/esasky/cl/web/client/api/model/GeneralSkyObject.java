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
