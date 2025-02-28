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

package esac.archive.esasky.cl.web.client.view.header;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.utility.DownloadUtils;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.AutoHidingMovablePanel;
import esac.archive.esasky.cl.web.client.view.common.buttons.CloseButton;
import esac.archive.esasky.cl.web.client.view.common.icons.Icons;

public class ScreenshotDialogBox extends AutoHidingMovablePanel {
    private final Resources resources = GWT.create(Resources.class);
    private CssResource style;
    
    private final CloseButton closeButton;
    private final Image image;
    private FocusPanel imageHoverLayer = new FocusPanel();
    
    public interface Resources extends ClientBundle {
        @Source("screenshotDialogBox.css")
        @CssResource.NotStrict
        CssResource style();
    }       
    
	public ScreenshotDialogBox(String url, final JavaScriptObject imageCanvas) {
		super(GoogleAnalytics.CAT_SCREENSHOT);
		this.style = this.resources.style();
		this.style.ensureInjected();
		
		setStyleName("screenshotDialogBox");
		
		Label label = new Label(TextMgr.getInstance().getText("screenShotDialogBox_screenshot"));
		label.setHeight("30px");
		label.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		add(label);
		
		FlowPanel imageAndCloseButton = new FlowPanel();

		closeButton = new CloseButton();
		closeButton.addStyleName("closeScreenshotButton");
		closeButton.addClickHandler(new ClickHandler() {
		    public void onClick(final ClickEvent event) {
		    	    hide();
		    }
		 });
		imageAndCloseButton.add(closeButton);
		
		image = new Image(url);
		image.addLoadHandler(new LoadHandler() {
			
			@Override
			public void onLoad(LoadEvent arg0) {
				setSuggestedPositionCenter();
			}
		});
		
		imageAndCloseButton.add(image);
		
		imageHoverLayer.addStyleName("imageHoverLayer");
		
		Image downloadImage = new Image(Icons.getDownloadIcon());
		downloadImage.addStyleName("imageHoverLayerIcon");
		imageHoverLayer.add(downloadImage);
		
		imageHoverLayer.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent arg0) {
				DownloadUtils.downloadCanvas("ESASky Screenshot.png", imageCanvas);
			}
		});
		imageAndCloseButton.add(imageHoverLayer);
		addElementNotAbleToInitiateMoveOperation(imageAndCloseButton.getElement());
		add(imageAndCloseButton);
		
		MainLayoutPanel.addMainAreaResizeHandler(new ResizeHandler() {
			
			@Override
			public void onResize(ResizeEvent arg0) {
				setSize();
			}
		});
		setSuggestedPositionCenter();
	}
	
	@Override 
	public void show() {
		super.show();
		setSize();
	}

	private void setSize() {
		resizeImage();
	}
	
	private void resizeImage() {
        	double windowWidth = MainLayoutPanel.getMainAreaWidth();
        	double windowHeight = MainLayoutPanel.getMainAreaHeight();
        	image.setHeight("auto");
        	image.setWidth("auto");
        	
        	double imageRatio;
        	if(image.getHeight() == 0) {
        		imageRatio = 0;
        	} else {
        		imageRatio = (double)image.getWidth() / (double)image.getHeight();
        	}
        	
        	int newWidth = (int) Math.round(windowWidth * 0.8);
        	int newHeight= (int) Math.round(windowHeight * 0.8);
        	if(newHeight * imageRatio < newWidth) {
        		image.setWidth("auto");
        		image.setHeight(newHeight + "px");    		
        	} else {
        		image.setHeight("auto");
        		image.setWidth(newWidth + "px");
        	}
        	
        	int imageHoverWidth = image.getWidth();
        	int imageHoverHeight = image.getHeight();
        	if(image.getWidth() == 0) {
        		imageHoverWidth = newWidth;
        		imageHoverHeight = newHeight;
        	}
        	imageHoverLayer.setSize(imageHoverWidth + "px", imageHoverHeight + "px");
	}
}
