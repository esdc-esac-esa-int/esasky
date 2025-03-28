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
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.*;

import esac.archive.esasky.cl.web.client.view.animation.RotateAnimation;


public class EsaSkyButton extends Composite implements HasClickHandlers{
	protected Image enabledImage;

    private Resources resources = GWT.create(Resources.class);
    private CssResource style;
	private RotateAnimation rotateAnimation;

	protected final PushButtonWithVisibleOnClick button;
	protected Label label;

	private static final String DISPLAY_NONE = "displayNone";

	protected interface ClickAction{
		void action();
	}

	private ClickAction onClickAction;
	private ClickAction onClickStartAction;
	private ClickAction onClickCancelAction;

    public static interface Resources extends ClientBundle {

        @Source("esaSkyButton.css")
        @CssResource.NotStrict
        CssResource style();
    }

	public EsaSkyButton(ImageResource image){
		this(image, null);
	}

	public EsaSkyButton(String text){
		FlowPanel container = new FlowPanel();
		button = new PushButtonWithVisibleOnClick(text);
		button.addStyleName("stringButton");

		container.add(button);

		initWidget(container);

		initStyle();

	}

	public EsaSkyButton(ImageResource image, String labelText){
		FlowPanel container = new FlowPanel();
		button = new PushButtonWithVisibleOnClick(new Image(image));
		container.add(button);

		initWidget(container);

		initStyle();

		this.enabledImage = new Image(image);
		this.enabledImage.addStyleName("fillParent");
		button.getUpFace().setImage(enabledImage);

		rotateAnimation = new RotateAnimation(enabledImage.getElement());
		rotateAnimation.addObserver(currentPosition -> button.getUpFace().setImage(enabledImage));

		if (labelText != null) {
			this.addStyleName("imageLabelButton");
			label = new Label(labelText);
			label.addStyleName(DISPLAY_NONE);
			label.addStyleName("imageLabelButtonLabel");
			container.add(label);
		}
	}

	public EsaSkyButton(String color, boolean isSmall){
		FlowPanel container = new FlowPanel();
        button = new PushButtonWithVisibleOnClick();
        container.add(button);
        initWidget(container);

        initStyle();
        if(isSmall) {
        	button.addStyleName("smallColorButton");
        } else {
        	setCircleColor(color);
        }
    }

	private void initStyle() {
	    this.style = this.resources.style();
        this.style.ensureInjected();

        button.addStyleName("defaultEsaSkyButton");
        setLightStyle();
        setSmallStyle();
        setSquaredStyle();
	}

	public void setNonTransparentBackground() {
		this.addStyleName("nonTransparentButton");
	}

	public void setTransparentBackground() {
		this.removeStyleName("nonTransparentButton");
	}

	public void setTextStyle(){
		button.addStyleName("stringButton");
	}

	public void setImageStyle(){
		button.removeStyleName("stringButton");
	}

	public void setDarkStyle(){
		button.removeStyleName("lightStyle");
		button.addStyleName("darkStyle");
	}

	public void setLightStyle(){
		button.addStyleName("lightStyle");
		button.removeStyleName("darkStyle");
	}

	public void setSmallStyle(){
		button.addStyleName("smallButton");
		button.removeStyleName("mediumButton");
		button.removeStyleName("bigButton");
		button.removeStyleName("veryBigButton");
	}

	public void setMediumStyle(){
		button.addStyleName("mediumButton");
		button.removeStyleName("smallButton");
		button.removeStyleName("bigButton");
		button.removeStyleName("veryBigButton");
	}

	public void setBigStyle(){
		button.addStyleName("bigButton");
		button.removeStyleName("smallButton");
		button.removeStyleName("mediumButton");
		button.removeStyleName("veryBigButton");
	}

	public void setVeryBigStyle(){
		button.addStyleName("veryBigButton");
		button.removeStyleName("bigButton");
		button.removeStyleName("smallButton");
		button.removeStyleName("mediumButton");
	}

	public void setRoundStyle() {
		button.addStyleName("roundButton");
		button.removeStyleName("squaredButton");
		addStyleName("roundButton");
		removeStyleName("squaredButton");
	}

	public void setSquaredStyle() {
		button.addStyleName("squaredButton");
		button.removeStyleName("roundButton");
		addStyleName("squaredButton");
		removeStyleName("roundButton");
	}

	public void rotate(double rotation, int milliseconds){
	    if (enabledImage != null) {
	        rotateAnimation.animateTo(rotation, milliseconds);
	    }
	}

	public void rotate(double rotation){
	    if (enabledImage != null) {
	        rotateAnimation.animateTo(rotation, 0);
	    }
	}

	public void keepAspectRatioWidth() {
		button.addStyleName("widthAsMainSizeIndicator");
	}

	public void setCircleColor (String color) {
        String html = "<div class='styleCircle' style='background-color:" + color + ";'></div>";
        button.getUpFace().setHTML(html);
	}

	public void setBackgroundColor(String color) {
		button.getElement().getStyle().setBackgroundColor(color);
	}

	public void setEnabled(boolean enabled) {
		button.setEnabled(enabled);
	}

	public void setFocus(boolean focus) {
		button.setFocus(focus);
	}

	public boolean isEnabled() {
		return button.isEnabled();
	}

	public void setButtonImage(ImageResource imageResource) {
		Image image = new Image(imageResource);
		this.enabledImage = image;
        this.enabledImage.addStyleName("fillParent");
        button.getUpFace().setImage(enabledImage);
	}

	public void setButtonText(String text) {
		button.getUpFace().setText(text);
	}

	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
        stopPropagationOfMouseDownAndTouchStartToParentElements();
		return button.addClickHandler(handler);
	}

	private void stopPropagationOfMouseDownAndTouchStartToParentElements() {
		button.addMouseDownHandler(DomEvent::stopPropagation);
		button.addTouchStartHandler(DomEvent::stopPropagation);
	}

	public void addMouseOverHandler(MouseOverHandler handler) {
		button.addMouseOverHandler(handler);
	}

	protected void setOnClickStart(ClickAction action) {
		onClickStartAction = action;
	}

	protected void setOnClick(ClickAction action) {
		onClickAction = action;
	}
	protected void setOnClickCancel(ClickAction action) {
		onClickCancelAction = action;
	}

	public class PushButtonWithVisibleOnClick extends PushButton{

		public PushButtonWithVisibleOnClick(Image image) {
			super(image);
		}

		public PushButtonWithVisibleOnClick() {
			super();
		}

		public PushButtonWithVisibleOnClick(String text) {
			super(text);
		}

		public void onClick() {
			super.onClick();
			if(onClickAction != null) {
				onClickAction.action();
			}
		}

		public void onClickCancel() {
			super.onClickCancel();
			if(onClickCancelAction != null) {
				onClickCancelAction.action();
			}
		}

		public void onClickStart() {
			super.onClickStart();
			if(onClickStartAction != null) {
				onClickStartAction.action();
			}
		}
	}

	//GWT bug results in hover class not always being removed correctly.
	//Common occurrence when button changes or moves
	public void removeGwtHoverCssClass() {
		button.removeStyleName("gwt-PushButton-down-hovering");
		button.removeStyleName("gwt-PushButton-up-hovering");
	}

    public void click() {
        button.fireEvent(new ClickEvent(){});
    }

	public void showLabel() {
		if (label != null) {
			label.removeStyleName(DISPLAY_NONE);
		}
	}

	public void hideLabel() {
		if (label != null) {
			label.addStyleName(DISPLAY_NONE);
		}
	}

	public boolean isLabelVisible() {
		return label != null && !label.getStyleName().contains(DISPLAY_NONE);
	}
}
