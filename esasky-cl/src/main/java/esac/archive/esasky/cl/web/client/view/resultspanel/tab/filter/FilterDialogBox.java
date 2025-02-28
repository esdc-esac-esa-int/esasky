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

package esac.archive.esasky.cl.web.client.view.resultspanel.tab.filter;


import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.view.common.AutoHidingMovablePanel;

public abstract class FilterDialogBox extends AutoHidingMovablePanel {
    
    private final Resources resources = GWT.create(Resources.class);
    private CssResource style;

    private final String filterButtonId;
    protected final String tapName;
    
    protected long timeAtLastClose = 0;
    
    public interface Resources extends ClientBundle {
        @Source("filterDialogBox.css")
        @CssResource.NotStrict
        CssResource style();
    }
    
	public FilterDialogBox(String tapName, String filterButtonId) {
		super(GoogleAnalytics.CAT_FILTER);
		this.tapName = tapName;
		this.filterButtonId = filterButtonId;
        this.style = this.resources.style();
        this.style.ensureInjected();
		
		setSize("auto", "auto");
		addStyleName("filterDialogBox");
	}
	
	
	public void ensureCorrectFilterButtonStyle() {
		if(isFilterActive()) {
			Document.get().getElementById(filterButtonId).addClassName("filterActive");
		} else {
			Document.get().getElementById(filterButtonId).removeClassName("filterActive");
		}
	}
	
	public void toggle() {
	    if(!isShowing() && System.currentTimeMillis() - timeAtLastClose > 300) {
	        show();
	    } else {
	        hide();
	    }
	}
	
	@Override
	public void hide() {
	    if(isShowing()) {
	        timeAtLastClose = System.currentTimeMillis();
	    }
	    super.hide();
	}
	
	public abstract boolean isFilterActive();
	public abstract void setValuesFromString(String filterString);
}
