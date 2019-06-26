package esac.archive.esasky.cl.web.client.view.ctrltoolbar.treemap;

import org.moxieapps.gwt.highcharts.client.Point;

public class GhostPoint extends Point{

	private boolean removed;
	private String loadingText;
	private String noResultsText;
	
	public GhostPoint(String loadingText, String noResultsText) {
		super(loadingText, 0.2);
		this.loadingText = loadingText;
		this.noResultsText = noResultsText;
		setColor("#000");
	}
	
	public void setLoading(){
		setName(loadingText);
	}
	
	public void setNoResults(){
		setName(noResultsText);
	}
	
	public boolean isRemoved(){
		return removed;
	}

	public void setRemoved(boolean removed) {
		this.removed = removed;
	}
}
