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
    
    public interface Resources extends ClientBundle {
        @Source("filterDialogBox.css")
        @CssResource.NotStrict
        CssResource style();
    }
    
	public FilterDialogBox(String tapName, String filterButtonId) {
		super(GoogleAnalytics.CAT_Filter);
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
	
	public abstract boolean isFilterActive();
	
	public abstract String getAdqlForFilterCondition();
}
