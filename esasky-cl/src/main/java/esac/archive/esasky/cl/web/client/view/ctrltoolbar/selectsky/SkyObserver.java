package esac.archive.esasky.cl.web.client.view.ctrltoolbar.selectsky;

public interface SkyObserver {
	public void onUpdateSkyEvent(SkyRow sky);
	public void onCloseEvent(SkyRow sky);

}
