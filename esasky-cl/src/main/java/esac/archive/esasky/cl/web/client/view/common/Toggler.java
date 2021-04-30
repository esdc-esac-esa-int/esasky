package esac.archive.esasky.cl.web.client.view.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import esac.archive.esasky.cl.web.client.view.animation.EsaSkyAnimation;
import esac.archive.esasky.cl.web.client.view.animation.RotateAnimation;
import esac.archive.esasky.cl.web.client.view.common.icons.Icons;

public class Toggler extends FocusPanel {
	private final Resources resources;
	private CssResource style;

	public static interface Resources extends ClientBundle {

		@Source("toggler.css")
		@CssResource.NotStrict
		CssResource style();
	}
	
	private FlowPanel container; 
	private Label text;
	private boolean isOpen;
	private EsaSkyAnimation arrowAnimation;
	private Widget widgetToToggleVisibility;
	
	public Toggler(Widget widgetToToggleVisibility){
		this.resources = GWT.create(Resources.class);
		this.style = this.resources.style();
		this.style.ensureInjected();
		
		this.widgetToToggleVisibility = widgetToToggleVisibility;
		
		container = new FlowPanel();
		container.addStyleName("toggler__container");
		
		FlowPanel leftLine = new FlowPanel();
		leftLine.addStyleName("toggler__line");
		container.add(leftLine);
		
		FlowPanel rightLine = new FlowPanel();
		rightLine.addStyleName("toggler__line");
		
		FlowPanel textAndArrowContainer = new FlowPanel();
		textAndArrowContainer.addStyleName("toggler__advancedContainer");
		container.add(textAndArrowContainer);
		
		text = new Label();
		textAndArrowContainer.add(text);
		text.addStyleName("toggler__text");
		
		Image downArrow = new Image(Icons.getDownArrowIcon());
		downArrow.addStyleName("toggler__arrow");
		textAndArrowContainer.add(downArrow);
		arrowAnimation = new RotateAnimation(downArrow.getElement());

		container.add(rightLine);

		add(container);
		addStyleName("toggler");
		addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				toggle();
				//TODO check max height
			}
		});
		close();
	}

	public void setText(String text){
		this.text.setText(text);
	}

	public void close(){
		arrowAnimation.animateTo(0, 500);
		isOpen = false;
		widgetToToggleVisibility.setVisible(false);
	}

	public void open(){
		arrowAnimation.animateTo(180, 500);
		isOpen = true;
		widgetToToggleVisibility.setVisible(true);
	}
	
	public void toggle(){
		if(isOpen) {
			close();
		} else {
			open();
		}
	}
}
