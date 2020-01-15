package esac.archive.esasky.cl.web.client.view.ctrltoolbar.treemap;

import org.moxieapps.gwt.highcharts.client.Point;

public class GhostPoint extends Point{

	private boolean removed;
	private String loadingText;
	private String noResultsText;
	private String notInRangeText;
	
	public GhostPoint(String loadingText, String noResultsText, String notInRangeText) {
		super(loadingText, 0.2);
		this.loadingText = loadingText;
		this.noResultsText = noResultsText;
		this.notInRangeText = notInRangeText;
		setColor("#000");
	}
	
	public void setLoading(){
		setName(loadingText);
	}
	
	public void setNoResults(){
		setName(noResultsText);
	}
	
	public void setNotInRange(){
		setName(notInRangeText);
	}
	
	public boolean isRemoved(){
		return removed;
	}

	public void setRemoved(boolean removed) {
		this.removed = removed;
	}

	public String getLoadingText() {
		return loadingText;
	}

	public String getNoResultsText() {
		return noResultsText;
	}

	public String getNotInRangeText() {
		return notInRangeText;
	}
	
	
}
