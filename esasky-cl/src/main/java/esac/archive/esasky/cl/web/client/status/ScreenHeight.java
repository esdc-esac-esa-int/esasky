package esac.archive.esasky.cl.web.client.status;

public enum ScreenHeight{
	FULL_SIZE(2160), SMALL(860), MINI(700);
	private final int pxSize;
	
	private ScreenHeight(final int pxSize) {
		this.pxSize = pxSize;
	}
	
	public int getPxSize() {
		return pxSize;
	}
}