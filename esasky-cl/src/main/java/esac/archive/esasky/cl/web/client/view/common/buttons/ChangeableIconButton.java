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
