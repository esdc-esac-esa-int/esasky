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

package esac.archive.esasky.cl.web.client.view.common;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.animation.EsaSkyAnimation;
import esac.archive.esasky.cl.web.client.view.animation.RotateAnimation;
import esac.archive.esasky.cl.web.client.view.common.icons.Icons;

public class DropDownMenu<T> extends Composite {

	private List<MenuObserver> observers = new LinkedList<MenuObserver>();
	private SelectedArea selectedArea;
	private ScrollPanel menuBar;
	private DropDownPopupPanel menuBarContainer;
	private VerticalPanel menuBarPanel;
	private int width;
	private int maxHeight = 320;
	private int SELECTED_AREA_PADDING = 4;
	private List<MenuItem<T>> menuItems = new LinkedList<MenuItem<T>>();
	private final String CSS_DBOXLABEL_ID = "dboxLabel";
	private final String CSS_DBOXMENU_ID = "dboxMenu";
	private boolean isImageDropDown = false;
	private final String id;
	private int numberOfMenuItems = 0;
	private Resources resources;

	private T selectedObject;

	private CssResource style;

	private String startingString;

	public interface Resources extends ClientBundle {

		@Source("dropdownMenu.css")
		@CssResource.NotStrict
		CssResource style();

	}

	public class SelectedArea extends FlowPanel implements HasClickHandlers {
		private HorizontalPanel hp; 
		private Label text;
		private Image image;
		private EsaSkyAnimation arrowAnimation;

		public SelectedArea(int pxSize, boolean showImage){

			hp = new HorizontalPanel();
			Image downArrow = new Image(Icons.getDownArrowIcon());

			if (!showImage) {
				text = new Label();
				text.addStyleName(CSS_DBOXLABEL_ID);
				text.getElement().getStyle().setProperty("maxWidth", (pxSize - downArrow.getWidth() - SELECTED_AREA_PADDING*2) + "px");
				hp.add(text);
			} else {
				image = new Image();
				image.addStyleName(CSS_DBOXLABEL_ID);
				final int size = pxSize - downArrow.getWidth() - 12;
				image.getElement().getStyle().setProperty("maxWidth", size + "px");
				image.getElement().getStyle().setProperty("maxHeight", size + "px");
				image.getElement().getStyle().setDisplay(Display.BLOCK);
				hp.add(image);
			}

			arrowAnimation = new RotateAnimation(downArrow.getElement());
			hp.add(downArrow);

			hp.getElement().setId("dboxLabelContent");
			if (!showImage) {
				hp.setCellHorizontalAlignment(text, HasHorizontalAlignment.ALIGN_LEFT);
			} else {
				hp.setCellHorizontalAlignment(image, HasHorizontalAlignment.ALIGN_LEFT);
			}
			hp.setCellHorizontalAlignment(downArrow, HasHorizontalAlignment.ALIGN_RIGHT);
			add(hp);
			addStyleName("selectedArea");
			setWidth(pxSize + "px");            
		}

		@Override
		public HandlerRegistration addClickHandler(ClickHandler handler) {
			return addDomHandler(handler, ClickEvent.getType());
		}

		public void setText(String text){
			this.text.setText(text);
		}

		public void setImage(ImageResource imageResource){
			this.image.setResource(imageResource);
		}

		public void close(){
			arrowAnimation.animateTo(0, 500);
		}

		public void open(){
			arrowAnimation.animateTo(180, 500);
		}

	}

	private class DropDownPopupPanel extends Composite{
		private DropDownPopupPanel(Widget widget){
			initWidget(widget);
			addAutoHidePartner(selectedArea.getElement());
		}

		public void hide(){
			hide(false);
		}

		public void hide(boolean autoClosed){
			isShowing = false;
			MainLayoutPanel.removeElementFromMainArea(this);
			selectedArea.removeStyleName("dropdown__openBorderRadius");
			selectedArea.close();
			updateHandlers();
		}

		public void show() {
			isShowing = true;
			MainLayoutPanel.addElementToMainArea(this);
			selectedArea.addStyleName("dropdown__openBorderRadius");
			updateHandlers();
		}

		private boolean isShowing;

		public void setPopupPosition(int left, int top) {
			getElement().getStyle().setLeft(left, Unit.PX);
			getElement().getStyle().setTop(top, Unit.PX);
			getElement().getStyle().setPosition(Position.ABSOLUTE);
		}


		private void previewNativeEvent(NativePreviewEvent event) {
			// If the event has been canceled or consumed, ignore it
			if (event.isCanceled() || (event.isConsumed())) {
				// We need to ensure that we cancel the event even if its been consumed so
				// that popups lower on the stack do not auto hide
				return;
			}

			// If the event targets the popup or the partner, consume it
			Event nativeEvent = Event.as(event.getNativeEvent());
			boolean eventTargetsPopupOrPartner = eventTargetsPopup(nativeEvent)
					|| eventTargetsPartner(nativeEvent);
			if (eventTargetsPopupOrPartner) {
				event.consume();
			}

			// Switch on the event type
			int type = nativeEvent.getTypeInt();
			switch (type) {
			case Event.ONMOUSEDOWN:
			case Event.ONTOUCHSTART:
				// Don't eat events if event capture is enabled, as this can
				// interfere with dialog dragging, for example.
				if (DOM.getCaptureElement() != null) {
					event.consume();
					return;
				}

				if (!eventTargetsPopupOrPartner) {
					hide(true);
					return;
				}
				break;
			}
		}
		private boolean eventTargetsPopup(NativeEvent event) {
			EventTarget target = event.getEventTarget();
			if (Element.is(target)) {
				return getElement().isOrHasChild(Element.as(target));
			}
			return false;
		}
		private boolean eventTargetsPartner(NativeEvent event) {
			if (autoHidePartners == null) {
				return false;
			}

			EventTarget target = event.getEventTarget();
			if (Element.is(target)) {
				for (Element elem : autoHidePartners) {
					if (elem.isOrHasChild(Element.as(target))) {
						return true;
					}
				}
			}
			return false;
		}
		private HandlerRegistration nativePreviewHandlerRegistration;

		private void updateHandlers() {
			// Remove any existing handlers.
			if (nativePreviewHandlerRegistration != null) {
				nativePreviewHandlerRegistration.removeHandler();
				nativePreviewHandlerRegistration = null;
			}

			// Create handlers if showing.
			if (isShowing) {
				nativePreviewHandlerRegistration = Event.addNativePreviewHandler(new NativePreviewHandler() {
					public void onPreviewNativeEvent(NativePreviewEvent event) {
						previewNativeEvent(event);
					}
				});
			}
		}

		private List<Element> autoHidePartners;
		/**
		 * Mouse events that occur within an autoHide partner will not hide a panel
		 * set to autoHide.
		 *
		 * @param partner the auto hide partner to add
		 */
		public void addAutoHidePartner(Element partner) {
			assert partner != null : "partner cannot be null";
			if (autoHidePartners == null) {
				autoHidePartners = new ArrayList<Element>();
			}
			autoHidePartners.add(partner);
		}

	}

	public DropDownMenu(final String startingString, final String title, int pxSize, String id) {
		this(startingString, title, pxSize, false, id);
	}
	
	public DropDownMenu(final String startingString, final String title, int pxSize, boolean isImageDropdown, String id) {
		this.resources = GWT.create(Resources.class);
		this.style = this.resources.style();
		this.style.ensureInjected();

		this.width = pxSize;
		this.isImageDropDown = isImageDropdown;
		this.id = id;
		
		selectedArea = new SelectedArea(pxSize, this.isImageDropDown);
		selectedArea.getElement().getStyle().setPadding(SELECTED_AREA_PADDING, Unit.PX);
		
		if(!isImageDropdown) {
			selectedArea.setText(startingString);
		}
		menuBarPanel = new VerticalPanel();
		menuBar = new ScrollPanel();
		menuBar.setWidth(width + "px");
		menuBar.add(menuBarPanel);

		menuBarContainer = new DropDownPopupPanel(menuBar);


		this.startingString = startingString;

		init(title);
	}

	//For image DropDownMenu
	public DropDownMenu(int pxSize, String id) {
		this("", "", pxSize, true, id);
	}

	private void init(String title) {
		menuBarContainer.setStyleName(CSS_DBOXMENU_ID);

		selectedArea.setTitle(title);
		selectedArea.getElement().setId(id);

		selectedArea.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				toggleMenuBar();
			}
		});

		MainLayoutPanel.addMainAreaResizeHandler(new ResizeHandler() {

			@Override
			public void onResize(ResizeEvent event) {
				setMaxHeight();
			}
		});
		initWidget(selectedArea);
	}

	private void setMaxHeight() {
		menuBar.getElement().getStyle().setProperty("maxHeight", Math.min(maxHeight, MainLayoutPanel.getMainAreaAbsoluteTop() + MainLayoutPanel.getMainAreaHeight() - menuBarContainer.getAbsoluteTop() - 5) + "px");
	}

	public void selectObject(T object) {
		selectObject(object, true);
	}

	public void selectObject(T object, boolean notifyObservers) {
		for(MenuItem<T> menuItem : menuItems){
			if(menuItem.getItem() == object){
				selectItem(menuItem, notifyObservers);
				break;
			}
		}
	}

	public T getSelectedObject() {
		return this.selectedObject;
	}

	public List<MenuItem<T>> getMenuItems(){
		return menuItems;
	}

	public void addMenuItem(final MenuItem<T> item){
		item.getElement().setId(id + "-option-" + numberOfMenuItems++);
		menuBarPanel.add(item);
		menuItems.add(item);

		item.addClickHandler(event -> selectItem(item));

	}

	private void setScrollBarVisibleStyle() {
		for(MenuItem<T> item: menuItems){
			item.setMaximumWidth(width - 21); //21 px is size of scroll bar
		}
	}

	private void setScrollBarNotVisibleStyle() {
		for(MenuItem<T> item: menuItems){
			item.setMaximumWidth(width);
		}
	}

	private void selectItem(MenuItem<T> selectedItem) {
		selectItem(selectedItem, true);
	}

	private void selectItem(MenuItem<T> selectedItem, boolean notifyObservers) {
		hideMenuBar();
		if(selectedObject != selectedItem.getItem()){
			selectedObject = selectedItem.getItem();
			if (notifyObservers) {
				notifyObservers();
			}
		}

		for(MenuItem<T> menuItem : menuItems){
			if(menuItem == selectedItem){
				menuItem.select();
			} else {
				menuItem.unSelect();
			}
		}

		if (!isImageDropDown) {
			selectedArea.setText(selectedItem.getText());
		} else {
			selectedArea.setImage(selectedItem.getImage());
		}
	}

	public void removeMenuItem(MenuItem<T> item){
		menuBarPanel.remove(item);
		menuItems.remove(item);
	}

	public VerticalPanel getMenuBar() {
		return menuBarPanel;
	}

	public void hideMenuBar() {
		selectedArea.close();
		menuBarContainer.hide();
	}

	public void toggleMenuBar() {
		if(menuBarContainer.isShowing) {
			hideMenuBar();
		} else {

			selectedArea.open();
			if(menuBar.getElement().getScrollHeight() > menuBar.getElement().getClientHeight()){
				setScrollBarVisibleStyle();
			} else {
				setScrollBarNotVisibleStyle();
			}
			menuBarContainer.setPopupPosition(selectedArea.getAbsoluteLeft() - MainLayoutPanel.getMainAreaAbsoluteLeft(), 
					selectedArea.getAbsoluteTop() + selectedArea.getOffsetHeight() - MainLayoutPanel.getMainAreaAbsoluteTop());
			
			menuBarContainer.show();
			menuBarContainer.getElement().addClassName(id);
			setMaxHeight();
			MainLayoutPanel.addMainAreaResizeHandler(new ResizeHandler() {
				
				@Override
				public void onResize(ResizeEvent event) {
					menuBarContainer.setPopupPosition(selectedArea.getAbsoluteLeft() - MainLayoutPanel.getMainAreaAbsoluteLeft(), 
							selectedArea.getAbsoluteTop() + selectedArea.getOffsetHeight() - MainLayoutPanel.getMainAreaAbsoluteTop());
				}
			});
		}
	}

	public void clearItems() {
		menuBarPanel.clear();
		menuItems.clear();
	}

	public void clearSelection() {
		selectedArea.setText(this.startingString);
		selectedObject = null;
		for(MenuItem<T> menuItem : menuItems){
			menuItem.unSelect();
		}
	}

	public boolean containsItem(T item) {
		return menuItems.stream()
				.map(MenuItem::getItem)
				.anyMatch(item::equals);
	}

	public void registerObserver(MenuObserver observer){
		observers.add(observer);
	}

	private void notifyObservers(){
		for(MenuObserver observer: observers){
			observer.onSelectedChange();
		}
	}
}
