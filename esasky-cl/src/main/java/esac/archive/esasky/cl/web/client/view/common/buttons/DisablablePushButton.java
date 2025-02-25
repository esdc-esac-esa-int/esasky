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

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;

public class DisablablePushButton extends EsaSkyButton{
	private Image disabledImage;
	
	public DisablablePushButton(ImageResource enabledImage, ImageResource disabledImage){
		super(enabledImage);
		
		this.disabledImage= new Image(disabledImage);
		this.disabledImage.addStyleName("fillParent");
		enableButton();
	}
	
	public void disableButton(){
		button.getUpFace().setImage(disabledImage);
		button.setEnabled(false);
	}
	
	public void enableButton(){
		button.getUpFace().setImage(enabledImage);
		button.setEnabled(true);
	}
	
	public void setDisabled(boolean disabled) {
		if(disabled) {
			disableButton();
		} else {
			enableButton();
		}
	}
}
