package esac.archive.esasky.cl.web.client.view.common.buttons;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

public class ScrollDisablablePushButton extends DisablablePushButton{
	
    private Resources resources = GWT.create(Resources.class);
    private CssResource style;

    public static interface Resources extends ClientBundle {

        @Source("scrollDisablablePushButton.css")
        @CssResource.NotStrict
        CssResource style();
    }
	
	public ScrollDisablablePushButton(ImageResource enabledImage, ImageResource disabledImage){
		super(enabledImage, disabledImage);

		this.style = this.resources.style();
        this.style.ensureInjected();
	}

	public void setVisible() {
		removeStyleName("displayNone");
	}
	
	public void setCollapsed() {
		addStyleName("displayNone");
	}
}
