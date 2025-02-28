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

package esac.archive.esasky.cl.web.client.view.common.buttons;

import java.util.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

import esac.archive.esasky.cl.web.client.utility.DisplayUtils;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;

public class HelpButton extends ChangeableIconButton{

    private static Resources resources = GWT.create(Resources.class);
    private final CssResource style;
    
    private final String messageId = UUID.randomUUID().toString();
    
    private String messageText;
    private String headerTitle;
    
    public static interface Resources extends ClientBundle {

        	@Source("help-light-small.png")
    		ImageResource helpLight();
        	
        	@Source("help-dark-small.png")
        	ImageResource helpDark();
        	
        	@Source("helpButton.css")
        	@CssResource.NotStrict
        	CssResource style();
    }
	

	public HelpButton(final String messageText, final String headerTitle){
        super(resources.helpLight(), resources.helpDark());
        this.style = resources.style();
        this.style.ensureInjected();
        
        this.messageText = messageText;
        this.headerTitle = headerTitle;
        button.addStyleName("helpButton");
        setRoundStyle();

        addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                DisplayUtils.showMessageDialogBox(HelpButton.this.messageText, HelpButton.this.headerTitle, messageId, "HelpDialog");
                GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_HELP, HelpButton.this.headerTitle, "");
            }
        });
	}
	
	public void setHeaderTitle(String headerTitle) {
		this.headerTitle = headerTitle;
	}
	
	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}
}
