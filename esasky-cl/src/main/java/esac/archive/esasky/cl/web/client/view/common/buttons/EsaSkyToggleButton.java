package esac.archive.esasky.cl.web.client.view.common.buttons;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

public class EsaSkyToggleButton extends EsaSkyButton{
	
    private Resources resources = GWT.create(Resources.class);
    private CssResource style;
    
	private boolean toggleStatus = false;
	
    public static interface Resources extends ClientBundle {

        @Source("esaSkyToggleButton.css")
        @CssResource.NotStrict
        CssResource style();

    }
	
	public EsaSkyToggleButton(ImageResource image){
		this(image, null);
	}

	public EsaSkyToggleButton(String text){
		super(text);
		
		this.style = this.resources.style();
		this.style.ensureInjected();
		
		button.addStyleName("smallStringToggleButton");

		
		addClickHandler(event -> toggle());
	}

	public EsaSkyToggleButton(ImageResource image, String label){
		super(image, label);

		this.style = this.resources.style();
		this.style.ensureInjected();

		button.addStyleName("toggleButtonOff");

		addClickHandler(event -> toggle());

	}

    public void setToggleStatus(boolean toggleStatus) {
        this.toggleStatus = toggleStatus;
        if (toggleStatus) {
        	button.removeStyleName("toggleButtonOff");
        	button.addStyleName("toggleButtonOn");
        } else {
        	button.removeStyleName("toggleButtonOn");
        	button.addStyleName("toggleButtonOff");
        }
    }
    
    public boolean getToggleStatus() {
        return this.toggleStatus;
    }
    
    public void toggle() {
    	setToggleStatus(!toggleStatus);
    }
}
