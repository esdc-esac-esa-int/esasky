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
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;

public class SignButton extends ChangeableIconButton{

    public enum SignType { MINUS, PLUS };
        
    private static Resources plusResources = GWT.create(Resources.class);
    private static Resources minusResources = GWT.create(MinusResources.class);
	
    public static interface Resources extends ClientBundle {

        @Source("plus-sign-light.png")
        ImageResource signLight();
        
        @Source("plus-sign-dark.png")
        ImageResource signDark();
        
        @Source("plus-sign-light-outline.png")
        ImageResource signLightOutline();
        
        @Source("plus-sign-dark-outline.png")
        ImageResource signDarkOutline();
    }
    
    public static interface MinusResources extends Resources {

        @Source("minus-sign-light.png")
        ImageResource signLight();
        
        @Source("minus-sign-dark.png")
        ImageResource signDark();
        
        @Source("minus-sign-light-outline.png")
        ImageResource signLightOutline();
        
        @Source("minus-sign-dark-outline.png")
        ImageResource signDarkOutline();
    }
	
    private static Resources getResources(SignType signType) {
        if (signType.equals(SignType.MINUS)) {
            return minusResources;
        } else {
            return plusResources;
        }
    }
    
    private boolean dark = false;
    private boolean outline = false;
    private SignType signType;
    
	public SignButton(SignType signType){
		super(getResources(signType).signLight(), getResources(signType).signDark());
		this.signType = signType;
	}
	
	public void setOutline() {
		outline = true;
		Image image;
		if(dark) {
			image = new Image(getResources(signType).signDarkOutline());
		} else {
			image = new Image(getResources(signType).signLightOutline());
		}
		updateImage(image);
	}
	
	@Override
    public void setPrimaryIcon() {
		dark = false;
		Image image;
		if(outline) {
			image = new Image(getResources(signType).signLightOutline());
		} else {
			image = new Image(getResources(signType).signLight());
		}
		updateImage(image);
	}
	
	@Override
    public void setSecondaryIcon() {
		dark = true;
		Image image;
		if(outline) {
			image = new Image(getResources(signType).signDarkOutline());
		} else {
			image = new Image(getResources(signType).signDark());
		}
		updateImage(image);
    }
	
	private void updateImage(Image image) {
		image.addStyleName("fillParent");
		image.addStyleName("fillParent");
		button.getDownFace().setImage(image);
		button.getUpFace().setImage(image);
		button.getUpHoveringFace().setImage(image);
		button.getDownHoveringFace().setImage(image);
	}
}
