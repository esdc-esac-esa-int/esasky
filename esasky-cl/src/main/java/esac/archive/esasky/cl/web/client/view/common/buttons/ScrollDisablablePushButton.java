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
