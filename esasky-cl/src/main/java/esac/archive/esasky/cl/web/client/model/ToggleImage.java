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
