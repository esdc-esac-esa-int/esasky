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
