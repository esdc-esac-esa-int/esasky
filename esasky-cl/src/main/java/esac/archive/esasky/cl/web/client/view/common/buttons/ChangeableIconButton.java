package esac.archive.esasky.cl.web.client.view.common.buttons;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;

public class ChangeableIconButton extends EsaSkyButton {

    private Image lightImage;
    private Image darkImage;

    public ChangeableIconButton(ImageResource lightImage, ImageResource darkImage) {
        super(lightImage);
        this.lightImage = new Image(lightImage);
        this.darkImage = new Image(darkImage);
		this.lightImage.addStyleName("fillParent");
		this.darkImage.addStyleName("fillParent");
    }

    public void setLightIcon() {
    	button.getDownFace().setImage(lightImage);
    	button.getUpFace().setImage(lightImage);
    	button.getUpHoveringFace().setImage(lightImage);
    	button.getDownHoveringFace().setImage(lightImage);
    }

    public void setDarkIcon() {
    	button.getDownFace().setImage(darkImage);
    	button.getUpFace().setImage(darkImage);
    	button.getUpHoveringFace().setImage(darkImage);
    	button.getDownHoveringFace().setImage(darkImage);
    }
}
