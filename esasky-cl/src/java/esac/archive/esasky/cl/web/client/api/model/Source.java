package esac.archive.esasky.cl.web.client.api.model;

import com.allen_sauer.gwt.log.client.Log;

public class Source extends GeneralSkyObject {

	String ra = null;
	String dec = null;

	public Source() {
		Log.debug("[Source] ready");
	}

	public String getRa() {
		return ra;
	}

	public void setRa(String ra) {
		this.ra = ra;
	}

	public String getDec() {
		return dec;
	}

	public void setDec(String dec) {
		this.dec = dec;
	}

}
