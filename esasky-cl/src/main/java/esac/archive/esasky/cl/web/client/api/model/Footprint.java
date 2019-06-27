package esac.archive.esasky.cl.web.client.api.model;

import com.allen_sauer.gwt.log.client.Log;

public class Footprint extends GeneralSkyObject {

	String stcs;

	public Footprint() {
		Log.debug("[Footprint] ready");
	}

	public String getStcs() {
		return stcs;
	}

	public void setStcs(String stc) {
		this.stcs = stc;
	}


}
