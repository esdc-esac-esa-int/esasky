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

package esac.archive.esasky.cl.web.client.utility;


import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPopEvent;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.MovablePanel;
import esac.archive.esasky.cl.web.client.view.common.MovablePanel.OnKeyPress;
import esac.archive.esasky.cl.web.client.view.common.buttons.CloseButton;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyButton;

public class MessageDialogBox extends Composite{

	
	private final MovablePanel movablePanel;
    private Label headerText = new Label();
    
    private Resources resources = GWT.create(Resources.class);
    private CssResource style;
    
    public static interface Resources extends ClientBundle {
        @Source("messageDialogBox.css")
        @CssResource.NotStrict
        CssResource style();
    }
    
    private FlowPanel contentPanel = new FlowPanel();
    
    private String dialogId;
    private Widget inputWidget;
    private boolean isShowing = false;
    private String contentText;
    private EsaSkyButton showMoreButton;
    
    public MessageDialogBox(final Widget inputWidget, final String inputHeaderText,
            final String dialogId, String analyticsMoveId) {
        super();
        movablePanel = new MovablePanel(analyticsMoveId, true);
        movablePanel.addHideOnEscapeKeyBehavior(this::hide);
        this.dialogId = dialogId;
        this.inputWidget = inputWidget;
        inputWidget.addStyleName("messageDialogBoxWidget");
        style = resources.style();
        style.ensureInjected();
    
        headerText.setText(inputHeaderText);
        headerText.addStyleName("messageDialogHeaderText");
        FlowPanel header = new FlowPanel();
        header.add(headerText);
        
        
        final CloseButton closeButton = new CloseButton();
        closeButton.addStyleName("closeDialogBox");
        closeButton.addClickHandler(event -> {
            // Remove message from the top progress indicator
            CommonEventBus.getEventBus().fireEvent(
                    new ProgressIndicatorPopEvent(dialogId));
            hide();
        });
        header.add(closeButton);
        contentPanel.add(inputWidget);
        
        movablePanel.add(header);
        movablePanel.add(contentPanel);
        movablePanel.addElementNotAbleToInitiateMoveOperation(inputWidget.getElement());
        
        initView(inputHeaderText);
    }

    private void initView(String inputHeader) {
        movablePanel.getElement().setId(getId());
        movablePanel.addStyleName("messageDialogBox");

        initWidget(movablePanel);
        hide();
    }
    
    public String getId(){
	    return dialogId;
    }

    public final void hide() {
    	if(isShowing) {
    		MainLayoutPanel.removeElementFromMainArea(this);
    		isShowing = false;
    	}
    }
    
    public void show() {
    	if(!isShowing) {
    		MainLayoutPanel.addElementToMainArea(this);
    		movablePanel.setMaxSize();
    		isShowing = true;
    	}
    }
    
    public void updateContent(String content, String title) {
    	updateContent(content, title, false);
    }
    
    public void updateContent(String content, String title, boolean forceLongText) {
    	headerText.setText(title);
    	contentPanel.remove(inputWidget);
    	if(showMoreButton != null) {
    		contentPanel.remove(showMoreButton);
    		showMoreButton = null;
    	}
    	this.contentText = content;
    	int maxTextLength = DeviceUtils.isMobile() ? 200 : 500;
    	if(!forceLongText && content.length() > maxTextLength) {
    		content = content.substring(0, maxTextLength) + "...";
    	}
        inputWidget = new HTML(content);
        inputWidget.setStyleName("messageDialogBoxWidget");
        
        contentPanel.add(inputWidget);
        if(!forceLongText && content.length() > maxTextLength) {
        	showMoreButton = createShowMoreButton();
        	contentPanel.add(showMoreButton);
        }
        movablePanel.addElementNotAbleToInitiateMoveOperation(inputWidget.getElement());
    }
    
    private EsaSkyButton createShowMoreButton() {
    	EsaSkyButton button = new EsaSkyButton(TextMgr.getInstance().getText("show_more"));
		button.setNonTransparentBackground();
		button.setMediumStyle();
		button.addStyleName("showMoreBtn");
		button.setTitle(TextMgr.getInstance().getText("show_more_tooltip"));
		button.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				updateContent(contentText, headerText.getText(), true);
				movablePanel.ensureDialogFitsInsideWindow();
			}
		});
    	
    	return button;
    	
    }
    
    public void setSuggestedPosition(int left, int top) {
    	movablePanel.setSuggestedPosition(left, top);
	}
    
    public boolean isShowing() {
    	return isShowing;
    }
}
