package esac.archive.esasky.cl.web.client.api.model;

public class SourceListJSONWrapper implements IJSONWrapper {

	SourceListOverlay overlaySet = new SourceListOverlay();

	@Override
	public IOverlay getOverlaySet() {
		return overlaySet;
	}

	@Override
	public void setOverlaySet(IOverlay overlaySet) {
		this.overlaySet = (SourceListOverlay) overlaySet;

	}

}
