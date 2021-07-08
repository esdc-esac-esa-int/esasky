package esac.archive.esasky.cl.web.client.utility;

import esac.archive.esasky.ifcs.model.client.HiPS;

public interface HipsParserObserver {
	
	public void onSuccess(HiPS hips);
	public void onError(String errorMsg);

}
