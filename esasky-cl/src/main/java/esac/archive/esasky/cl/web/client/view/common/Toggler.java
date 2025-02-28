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

package esac.archive.esasky.cl.web.client.view.common;

import com.google.gwt.core.client.GWT;
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
	private Label label;
	private boolean isOpen;
	private EsaSkyAnimation arrowAnimation;
	private Widget widgetToToggleVisibility;
	FlowPanel textAndArrowContainer;

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
		
		textAndArrowContainer = new FlowPanel();
		textAndArrowContainer.addStyleName("toggler__advancedContainer");
		container.add(textAndArrowContainer);

		Image downArrow = new Image(Icons.getDownArrowIcon());
		downArrow.addStyleName("toggler__arrow");
		textAndArrowContainer.add(downArrow);
		arrowAnimation = new RotateAnimation(downArrow.getElement());

		container.add(rightLine);

		add(container);
		addStyleName("toggler");

		addClickHandler(event -> {
			this.toggle();
		});
		close();
	}

	public void setText(String text){
		if (this.label == null) {
			this.label = new Label();
			this.textAndArrowContainer.add(this.label);
			this.label.addStyleName("toggler__text");
		}
		this.label.setText(text);
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

	public void setToggleStatus(boolean toggleStatus) {
		if (toggleStatus && !isOpen) {
			open();
		} else if (!toggleStatus && isOpen) {
			close();
		}
	}

	public boolean isOpen() {
		return isOpen;
	}
}
