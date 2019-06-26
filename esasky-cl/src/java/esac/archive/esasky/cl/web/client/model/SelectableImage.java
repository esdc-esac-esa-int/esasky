package esac.archive.esasky.cl.web.client.model;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;

public class SelectableImage extends Image{
	private Image defaultImage;
	private Image selectedImage;
	public SelectableImage (ImageResource defaultImage, ImageResource selectedImage){
		this.defaultImage = new Image(defaultImage);
		this.selectedImage =  new Image(selectedImage);
		setSelected();
	}
	
	public void setDefault(){
		setUrl(defaultImage.getUrl());
	}
	
	public void setSelected(){
		setUrl(selectedImage.getUrl());
	}
}
