package esac.archive.esasky.cl.web.client.model;

import com.google.gwt.user.client.ui.Image;

public class ToggleImage extends Image{
	private Image defaultImage;
	private Image toggledImage;
	private boolean isSelected = true;
	public ToggleImage (Image defaultImage, Image selectedImage){
	    changeImageResources(defaultImage, selectedImage);
	}
	
	public void setDefault(){
		setUrl(defaultImage.getUrl());
		isSelected = false;
	}
	
	public void setToggled(){
		setUrl(toggledImage.getUrl());
		isSelected = true;
	}
	
	public void changeImageResources(Image defaultImage, Image toggledImage) {
    	this.defaultImage = defaultImage;
    	this.toggledImage =  toggledImage;
    	if (isSelected) {
    	    setToggled();
    	} else {
    	    setDefault();
    	}
	}
}
