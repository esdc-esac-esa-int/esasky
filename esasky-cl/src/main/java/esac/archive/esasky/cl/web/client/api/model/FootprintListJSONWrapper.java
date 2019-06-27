package esac.archive.esasky.cl.web.client.api.model;

public class FootprintListJSONWrapper implements IJSONWrapper {

	IOverlay overlaySet = new FootprintListOverlay();

	@Override
	public IOverlay getOverlaySet() {
		return overlaySet;
	}

	@Override
	public void setOverlaySet(IOverlay overalySet) {
		this.overlaySet = (FootprintListOverlay) overalySet;
	}

}
