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
