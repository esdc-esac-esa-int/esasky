package esac.archive.esasky.cl.web.client.view.common;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;

public class LoadingSpinner extends FlowPanel{
	private static Resources resources = GWT.create(Resources.class);
	private CssResource style;

	public interface Resources extends ClientBundle {

		@Source("loadingSpinner.css")
		@CssResource.NotStrict
		CssResource style();
	}
	
	public LoadingSpinner(boolean large) {
		super();
		style = resources.style();
		style.ensureInjected();
		if(large) {
			getElement().setInnerHTML("<div class=\"esasky__spinner__large\"><div></div><div></div><div></div><div></div><div></div></div>");
		} else {
			getElement().setInnerHTML("<div class=\"esasky__spinner\"><div></div><div></div><div></div><div></div><div></div></div>");
		}
	}
	
	public static String getLoadingSpinner() {
	    return new LoadingSpinner(true).getElement().getString();
	}
}

