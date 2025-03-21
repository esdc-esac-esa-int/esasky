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

package esac.archive.esasky.cl.web.client.view.common.buttons;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;

public class EsaSkyStringButton extends Composite{
	protected Image enabledImage;
	
    private Resources resources = GWT.create(Resources.class);
    private CssResource style;
    
    protected PushButton button;

    public static interface Resources extends ClientBundle {

        @Source("esaSkyStringButton.css")
        @CssResource.NotStrict
        CssResource style();
    }
	
	public EsaSkyStringButton(String buttonText){
		FlowPanel container = new FlowPanel();
		button = new PushButton(buttonText);
		container.add(button);
		initWidget(container);
		
        this.style = this.resources.style();
        this.style.ensureInjected();
		button.addStyleName("defaultEsaSkyStringButton");
		setLightStyle();
		setSmallStyle();
		setSquaredStyle();
	}

	public PushButton getPushButton() {
		return this.button;
	}
	
	public void setDarkStyle(){
		button.removeStyleName("lightStringStyle");
		button.addStyleName("darkStringStyle");
	}
	public void setLightStyle(){
		button.addStyleName("lightStringStyle");
		button.removeStyleName("darkStringStyle");
	}
	
	public void setSmallStyle(){
		button.addStyleName("smallStringButton");
		button.removeStyleName("mediumStringButton");
		button.removeStyleName("bigStringButton");
	}
	
	public void setMediumStyle(){
		button.addStyleName("mediumStringButton");
		button.removeStyleName("smallStringButton");
		button.removeStyleName("bigStringButton");
	}
	
	public void setBigStyle(){
		button.addStyleName("bigStringButton");
		button.removeStyleName("smallStringButton");
		button.removeStyleName("mediumStringButton");
	}
	
	public void setSquaredStyle() {
		button.addStyleName("squaredStringButton");
	}
	public void setSidePadding(int left, int right) {
	    button.getElement().getStyle().setPaddingLeft(left, Unit.PX);
	    button.getElement().getStyle().setPaddingRight(right, Unit.PX);
	}
	
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		stopPropagationOfMouseDownAndTouchStartToParentElements();
		return button.addClickHandler(handler);
	}
	
	public void setText(String text) {
		if(text.equals(button.getText())) {
			return;
		}
		button.getUpFace().setText(text);
	}
	
	private void stopPropagationOfMouseDownAndTouchStartToParentElements() {
		button.addMouseDownHandler(new MouseDownHandler() {
			
			@Override
			public void onMouseDown(MouseDownEvent event) {
				event.stopPropagation();
			}
		});
		button.addTouchStartHandler(new TouchStartHandler() {
			
			@Override
			public void onTouchStart(TouchStartEvent event) {
				event.stopPropagation();
			}
		});
	}
}
