package esac.archive.esasky.cl.web.client.view.ctrltoolbar.selectsky;

import esac.archive.esasky.cl.web.client.view.common.MenuItem;
import esac.archive.esasky.ifcs.model.client.HiPS;

public interface SkyObserver {
	void onUpdateSkyEvent(SkyRow sky);
	void onCloseEvent(SkyRow sky);
	void onMenuItemRemovalEvent(MenuItem<HiPS> menuItem);
	void onImageSettingsClicked(SkyRow skyRow);

}
