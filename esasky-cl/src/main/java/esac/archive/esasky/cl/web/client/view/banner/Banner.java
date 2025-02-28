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

package esac.archive.esasky.cl.web.client.view.banner;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import esac.archive.esasky.cl.web.client.presenter.BannerPresenter.View;
import esac.archive.esasky.cl.web.client.view.animation.AnimationObserver;
import esac.archive.esasky.cl.web.client.view.animation.CssPxAnimation;
import esac.archive.esasky.cl.web.client.view.common.buttons.CloseButton;

public class Banner extends Composite implements View{

	private static Resources resources = GWT.create(Resources.class);
	private CssResource style;

	private final FlowPanel banner = new FlowPanel();
	private HTML bannerText = new HTML();
	private CloseButton closeButton = new CloseButton();
	private CssPxAnimation animation;
	private int animationTimer = 1000;
	private boolean isShowing;
	private Image warningIcon = new Image(resources.warning());
	private Image informationIcon = new Image(resources.information());
	private Side side;
	
	public interface Resources extends ClientBundle {
		@Source("banner.css")
		@CssResource.NotStrict
		CssResource style();
		
        @Source("warning.png")
        ImageResource warning();
        
        @Source("information.png")
        ImageResource information();
	}
	
	public enum Side {TOP, BOTTOM, LEFT, RIGHT}

	public Banner(Side side) {
		style = resources.style();
		style.ensureInjected();
		this.side = side;
		initView();
	}
	
	private void initView() {
		banner.addStyleName("banner__container");
		
		FlowPanel contentContainer = new FlowPanel();
		contentContainer.addStyleName("banner__contentContainer");
		warningIcon.addStyleName("banner__icon");
		contentContainer.add(warningIcon);
		informationIcon.addStyleName("banner__icon");
		contentContainer.add(informationIcon);
		bannerText.addStyleName("banner__text");
		contentContainer.add(bannerText);
		banner.add(contentContainer);
		
		closeButton.addStyleName("banner__closeButton");
		banner.add(closeButton);
		if(side == Side.LEFT) {
			animation = new CssPxAnimation(banner.getElement(), "marginLeft");
			banner.getElement().getStyle().setBackgroundColor("blue");
			contentContainer.getElement().getStyle().setProperty("padding", "10vh 1vw");
		} else if (side == Side.TOP){
			animation = new CssPxAnimation(banner.getElement(), "marginTop");
		} else if (side == Side.RIGHT){
			animation = new CssPxAnimation(banner.getElement(), "marginRight");
			banner.getElement().getStyle().setBackgroundColor("grey");
			contentContainer.getElement().getStyle().setProperty("padding", "10vh 1vw");
		} else if (side == Side.BOTTOM){
			animation = new CssPxAnimation(banner.getElement(), "marginBottom");
			banner.getElement().getStyle().setBackgroundColor("green");
		}
		
		animation.addObserver(currentPosition -> banner.setVisible(isShowing));
		contentContainer.setHeight("100%");
		contentContainer.setWidth("100%");
		banner.setVisible(false);
		
		initWidget(banner);
	}

	@Override
	public void setText(String text) {
		bannerText.setHTML(text);
	}

	@Override
	public void show() {
		banner.setVisible(true);
		isShowing = true;
		animation.animateTo(0, animationTimer);
	}

	@Override
	public void hide() {
		isShowing = false;
		if(side == Side.LEFT || side == Side.RIGHT) {
			animation.animateTo(- banner.getOffsetWidth(), animationTimer);
		} else {
			animation.animateTo(- banner.getOffsetHeight(), animationTimer);
		}
	}

	@Override
	public void addCloseButtonClickHandler(ClickHandler handler) {
		closeButton.addClickHandler(handler);
	}

	@Override
	public String getText() {
		return bannerText.getHTML();
	}

	public void setWidget(Widget widget) {
		for(int i = 0; i < banner.getWidgetCount(); i++) {
			banner.remove(0);
		}
		banner.add(widget);
	}
	
	public void addWidget(Widget widget) {
		banner.add(widget);
	}
	
	public void setSize(int sizeInPixels) {
		if(side == Side.TOP || side == Side.BOTTOM) {
			this.setHeight(Integer.toString(sizeInPixels) + "px");
		}else {
			this.setWidth(Integer.toString(sizeInPixels) + "px");
		}
	}

	@Override
	public boolean isShowing() {
		return isShowing;
	}

	@Override
	public void setIsWarning(boolean isWarning) {
		if(isWarning) {
			banner.getElement().getStyle().setBackgroundColor("rgb(170, 0 , 0)");
		} else {
			banner.getElement().getStyle().setBackgroundColor("#20a4d8");
		}
		warningIcon.setVisible(isWarning);
		informationIcon.setVisible(!isWarning);
	}
	
	public void hideCloseButton() {
		closeButton.setVisible(false);
	}
}
