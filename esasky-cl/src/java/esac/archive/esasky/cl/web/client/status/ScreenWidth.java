package esac.archive.esasky.cl.web.client.status;

public enum ScreenWidth{
	FULL_SIZE(2160), LARGE(1050), MEDIUM(850), SMALL(660), MINI(398);
	private final int pxSize;
	
	private ScreenWidth(final int pxSize) {
		this.pxSize = pxSize;
	}
	
	public int getPxSize() {
		return pxSize;
	}
}