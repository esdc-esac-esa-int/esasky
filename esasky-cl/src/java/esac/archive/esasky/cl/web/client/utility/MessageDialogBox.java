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
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.MovablePanel;
import esac.archive.esasky.cl.web.client.view.common.buttons.CloseButton;

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
    
    public MessageDialogBox(final Widget inputWidget, final String inputHeaderText,
            final String dialogId) {
        super();
        movablePanel = new MovablePanel(inputHeaderText, true);
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
        closeButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(final ClickEvent event) {
                // Remove message from the top progress indicator
                CommonEventBus.getEventBus().fireEvent(
                        new ProgressIndicatorPopEvent(dialogId));
                hide();
            }
        });
        header.add(closeButton);
        contentPanel.add(inputWidget);
        
        movablePanel.add(header);
        movablePanel.add(contentPanel);
        
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
    	headerText.setText(title);
    	contentPanel.remove(inputWidget);
        inputWidget = new HTML(content);
        inputWidget.setStyleName("messageDialogBoxWidget");
    	contentPanel.add(inputWidget);
    }
    
    public void setSuggestedPosition(int left, int top) {
    	movablePanel.setSuggestedPosition(left, top);
	}
    
    public boolean isShowing() {
    	return isShowing;
    }
}
