package esac.archive.esasky.cl.web.client.view.common.buttons;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class LabelWithHelpButton extends FlowPanel{

    private static Resources resources = GWT.create(Resources.class);
    private final CssResource style;
    
    private Label label;
    private HelpButton button;
    
	
    public static interface Resources extends ClientBundle {

        	@Source("labelWithHelpButton.css")
        	@CssResource.NotStrict
        	CssResource style();
    }
	
    public LabelWithHelpButton(String labelText, String dialogMessage, String dialogHeader) {
        this(labelText, dialogMessage, dialogHeader, null);
    }
	
	public LabelWithHelpButton(String labelText, String dialogMessage, String dialogHeader, String labelStyleName) {
        this.style = resources.style();
        this.style.ensureInjected();
        
		label = new Label(labelText);
		label.addStyleName(((labelStyleName != null) && !labelStyleName.isEmpty()) ? labelStyleName : "labelWithHelpButtonLabel");
		
		button = new HelpButton(dialogMessage, dialogHeader);
		button.addStyleName("labelHelpButton");
		
		add(label);
		add(button);
		
		addStyleName("displayInlineBlock");
	}
	
	public void setText(String text) {
		label.setText(text);
		button.setHeaderTitle(text);
	}
	
	public String getText() {
        return label.getText();
    }
	
	public void setDialogMessage(String dialogMessage) {
		button.setMessageText(dialogMessage);
	}
	
	public void setButtonDarkIconAndStyle() {
		button.setDarkIcon();
		button.setDarkStyle();
	}
	
	public void setButtonLightIconAndStyle() {
		button.setLightIcon();
		button.setLightStyle();
		
	}
    	
}
