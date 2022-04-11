package esac.archive.esasky.cl.web.client.view.evapanel;


import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;

import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyButton;

/**
 * @author ESDC team Copyright (c) 2015- European Space Agency
 */
public class EvaPanel extends FocusPanel {

    
    private Resources resources = GWT.create(Resources.class);
    private CssResource style;
    
    private FlowPanel container = new FlowPanel();
    private boolean hasBeenInitialised = false;
    private boolean isShowing = false;

    public static interface Resources extends ClientBundle {

        @Source("evaPanel.css")
        @CssResource.NotStrict
        CssResource style();
    }

    public EvaPanel() {
    	super();
		this.resources = GWT.create(Resources.class);
		this.style = this.resources.style();
		this.style.ensureInjected();
        
        initView();
    }
    
    public void init() {
    	if(!this.hasBeenInitialised) {
    		initJs();
    		hasBeenInitialised = true;
    	}
    }
    
    private void initView() {

    	container.getElement().setClassName("evaContainer");
		FlowPanel controlsPanel = new FlowPanel();
		controlsPanel.addStyleName("evaControlsContainer");
    	EsaSkyButton clearButton = new EsaSkyButton("Clear");
    	clearButton.addClickHandler(event -> clearChat());
    	clearButton.getElement().addClassName("eva-clear-button");
		controlsPanel.add(clearButton);

    	FlowPanel bot = new FlowPanel();
    	bot.getElement().setId("webchat");
    	container.add(controlsPanel);
    	container.add(bot);
    	this.add(container);
    	
    }

    private native void clearChat()/*-{
		$wnd.clearChat();
	}-*/;
    
    
    private native void initJs()/*-{
    	$wnd.initChat();
    }-*/;

	public boolean isShowing() {
		return isShowing;
	}

	public void setShowing(boolean isShowing) {
		this.isShowing = isShowing;
	}

	public boolean hasBeenInitialised() {
		return hasBeenInitialised;
	}

	public void setHasBeenInitialised(boolean hasBeenInitialised) {
		this.hasBeenInitialised = hasBeenInitialised;
	}
	
	
    
}
