package esac.archive.esasky.cl.web.client.view.allskypanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.VerticalPanel;

import esac.archive.absi.modules.cl.aladinlite.widget.client.model.AladinShape;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.DisplayUtils;
import esac.archive.esasky.cl.web.client.utility.DisplayUtils.PreferredDirection;
import esac.archive.esasky.cl.web.client.view.common.AutoHidePanel;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyButton;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyToggleButton;

public class SelectionToolBoxPanel extends AutoHidePanel{

    private int right;
    private int top;
    protected AladinShape source;
    
    private EsaSkyToggleButton boxButton;
    private EsaSkyToggleButton circleButton;
    private EsaSkyToggleButton polyButton;
    
    private Resources resources = GWT.create(Resources.class);
    private CssResource style;
    
    public static interface Resources extends ClientBundle {
        @Source("selectionToolbox.css")
        @CssResource.NotStrict
        CssResource style();
		
		@Source("selection-rectangle-dashed.png")
		ImageResource rect();
		
		@Source("selection-circle-dashed.png")
		ImageResource circle();
		
		@Source("selection-poly-dashed.png")
		ImageResource poly();
    }
    
    public SelectionToolBoxPanel(int right, int top) {
    	style = resources.style();
    	style.ensureInjected();
    	this.right = right;
    	this.top = top;
    	initView();
    	setCorrectToggled();
    	DOM.sinkEvents(getElement(), Event.ONMOUSEWHEEL);
    }
    
    private void toggleOtherButtons(EsaSkyToggleButton buttonPressed) {
    	if(buttonPressed != boxButton) {
    		boxButton.setToggleStatus(false);
    	}
    	if(buttonPressed != circleButton) {
    		circleButton.setToggleStatus(false);
    	}
    	if(buttonPressed != polyButton) {
    		polyButton.setToggleStatus(false);
    	}
    }

    private void setCorrectToggled() {
    	String mode = AladinLiteWrapper.getAladinLite().getSelectionMode();
    	if( mode.equals("BOX")) {
    		boxButton.setToggleStatus(true);
    		toggleOtherButtons(boxButton);
    	}
    	else if( mode.equals("CIRCLE")) {
    		circleButton.setToggleStatus(true);
    		toggleOtherButtons(circleButton);
    	}
    	else if( mode.equals("POLYGON")) {
    		polyButton.setToggleStatus(true);
    		toggleOtherButtons(polyButton);
    	}
    }
    
    private void initView() {
    	VerticalPanel selectionToolBox = new VerticalPanel();
        selectionToolBox.getElement().setId("selectionToolBoxContent");
        
        boxButton = new EsaSkyToggleButton(resources.rect());
		addCommonButtonStyle(boxButton, TextMgr.getInstance().getText("webConstants_projectFutureObservations"));
		SelectionToolBoxPanel _this = this;
		boxButton.addClickHandler( 
				new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				AladinLiteWrapper.getAladinLite().setSelectionMode("box");
				_this.hide();
			}
		});
		circleButton = new EsaSkyToggleButton(resources.circle());
		addCommonButtonStyle(circleButton, TextMgr.getInstance().getText("webConstants_projectFutureObservations"));
		circleButton.addClickHandler( 
				new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						AladinLiteWrapper.getAladinLite().setSelectionMode("circle");
						_this.hide();
					}
				});
		polyButton = new EsaSkyToggleButton(resources.poly());
		addCommonButtonStyle(polyButton, TextMgr.getInstance().getText("webConstants_projectFutureObservations"));
		polyButton.addClickHandler( 
				new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						AladinLiteWrapper.getAladinLite().setSelectionMode("polygon");
						_this.hide();
					}
				});
        
		selectionToolBox.add(boxButton);
		selectionToolBox.add(circleButton);
		selectionToolBox.add(polyButton);
		
        this.getElement().setId("selectionToolbox");
        this.removeStyleName("gwt-DialogBox");
        super.hide();
        this.add(selectionToolBox);
    }


    private void addCommonButtonStyle(EsaSkyButton button, String tooltip) {
		button.setNonTransparentBackground();
		button.setBigStyle();
		button.addStyleName("selectionToolboxButton");
		button.setTitle(tooltip);
	}
    
    public void show(String a) {
        DisplayUtils.showInsideMainAreaPointingAtPosition(this, right , top, PreferredDirection.SOUTHWEST);
    }
    
	@Override
	public void onBrowserEvent(Event event) {
		if(event.getTypeInt() == Event.ONMOUSEWHEEL) {
			event.stopPropagation();
			AladinLiteWrapper.getAladinLite().triggerMouseWheelEvent(event);
			
		}else {
			super.onBrowserEvent(event);
		}
	}
}
