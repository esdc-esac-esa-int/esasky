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

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;

public class ChangeableIconButton extends EsaSkyButton {

    private Image primaryImage;
    private Image secondaryImage;

    public ChangeableIconButton(ImageResource primaryImage, ImageResource secondaryImage) {
        super(primaryImage);
        this.primaryImage = new Image(primaryImage);
        this.secondaryImage = new Image(secondaryImage);
		this.primaryImage.addStyleName("fillParent");
		this.secondaryImage.addStyleName("fillParent");
    }

    public void setPrimaryIcon() {
    	button.getDownFace().setImage(primaryImage);
    	button.getUpFace().setImage(primaryImage);
    	button.getUpHoveringFace().setImage(primaryImage);
    	button.getDownHoveringFace().setImage(primaryImage);
    }

    public void setSecondaryIcon() {
    	button.getDownFace().setImage(secondaryImage);
    	button.getUpFace().setImage(secondaryImage);
    	button.getUpHoveringFace().setImage(secondaryImage);
    	button.getDownHoveringFace().setImage(secondaryImage);
    }
}
