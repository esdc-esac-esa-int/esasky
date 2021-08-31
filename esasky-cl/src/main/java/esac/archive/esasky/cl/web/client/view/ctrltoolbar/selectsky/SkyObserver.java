package esac.archive.esasky.cl.web.client.view.ctrltoolbar.selectsky;

import esac.archive.esasky.cl.web.client.view.common.MenuItem;
import esac.archive.esasky.ifcs.model.client.HiPS;

public interface SkyObserver {
	public void onUpdateSkyEvent(SkyRow sky);
	public void onCloseEvent(SkyRow sky);
	public void onMenuItemRemovalEvent(MenuItem<HiPS> menuItem);

}
