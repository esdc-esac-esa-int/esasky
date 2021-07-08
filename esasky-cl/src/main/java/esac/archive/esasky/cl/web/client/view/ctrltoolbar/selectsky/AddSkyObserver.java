package esac.archive.esasky.cl.web.client.view.ctrltoolbar.selectsky;

import esac.archive.esasky.ifcs.model.client.HiPS;

public interface AddSkyObserver {
	public void onSkyAdded();
	public void onSkyAddedWithUrl(HiPS hips);
}
