/*
ESASky
Copyright (C) 2025 Henrik Norman

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
		button.setSecondaryIcon();
		button.setDarkStyle();
	}
	
	public void setButtonLightIconAndStyle() {
		button.setPrimaryIcon();
		button.setLightStyle();
	}
	
	public void setHelpButtonVisibility(boolean visible) {
		button.setVisible(visible);
	}
    	
}
