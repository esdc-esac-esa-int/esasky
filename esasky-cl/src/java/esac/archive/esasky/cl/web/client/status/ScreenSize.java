package esac.archive.esasky.cl.web.client.status;

public class ScreenSize {
	private ScreenWidth width;
	private ScreenHeight height;
	
	public ScreenSize(ScreenWidth width, ScreenHeight height) {
		this.width = width;
		this.height = height;
	}
	
	public ScreenWidth getWidth() {
		return width;
	}
	
	public ScreenHeight getHeight() {
		return height;
	}
}
