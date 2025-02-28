/*
ESASky
Copyright (C) 2025 European Space Agency

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published
by the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package esac.archive.esasky.cl.web.client.view.ctrltoolbar.treemap;

import org.moxieapps.gwt.highcharts.client.Point;

public class GhostPoint extends Point{

	private boolean removed;
	private String loadingText;
	private String noResultsText;
	private String notInRangeText;
	private String largeFovText;
	
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
	
	public String getLargeFovText() {
		return largeFovText;
	}
	
	public void setLargeFovText(String largeFovText) {
		this.largeFovText= largeFovText;
	}
	
	
}
