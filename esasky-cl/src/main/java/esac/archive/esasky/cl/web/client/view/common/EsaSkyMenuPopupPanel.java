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

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;


public class EsaSkyMenuPopupPanel<T> extends PopupPanel {

    private List<MenuObserver> observers = new LinkedList<MenuObserver>();
    private VerticalPanel menuBarPanel;
    private List<MenuItem<T>> menuItems = new LinkedList<MenuItem<T>>();
    private long timeLastHiddenTime;
    private T selectedObject;
    private boolean notifyOnSelectionOfAlreadySelectedItem;
    
    private Resources resources;
    private CssResource style;
    public interface Resources extends ClientBundle {
        
    	@Source("esaSkyMenuPopupPanel.css")
        @CssResource.NotStrict
        CssResource style();
    }

    public EsaSkyMenuPopupPanel(int pxSize, boolean notifyOnSelectionOfAlreadySelectedItem) {
        	this(pxSize);
        	this.notifyOnSelectionOfAlreadySelectedItem = notifyOnSelectionOfAlreadySelectedItem;
    }
    
    public EsaSkyMenuPopupPanel(int pxSize) {
    	this();
    	setWidth(pxSize + "px");
    }
    
    public EsaSkyMenuPopupPanel() {
        this.resources = GWT.create(Resources.class);
        this.style = this.resources.style();
        this.style.ensureInjected();
        
        setModal(false);
        setAutoHideEnabled(true);
        setAnimationEnabled(true);
        setAnimationType(AnimationType.ROLL_DOWN);
        setStyleName("popupPanel");
        
        menuBarPanel = new VerticalPanel();
        
        add(menuBarPanel);
        
        getContainerElement().getStyle().setOverflow(Overflow.AUTO);
        MainLayoutPanel.addMainAreaResizeHandler(new ResizeHandler() {
			
			@Override
			public void onResize(ResizeEvent event) {
				setMaxHeight();
			}
		});
    }

    public void selectObject(T object) {
        for(MenuItem<T> menuItem : menuItems){
            	if(menuItem.getItem() == object){
            		selectItem(menuItem);
            		break;
            	}
        }
    }

    public void selectIndex(int index) {
        selectItem(menuItems.get(index));
    }

    public List<MenuItem<T>> getMenuItems(){
    	    return menuItems;
    }
    
    public void addMenuItem(final MenuItem<T> item){
        menuBarPanel.add(item);
        	menuItems.add(item);
        	item.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				selectItem(item);
				hide();
			}
		});
    }
    
	private void selectItem(MenuItem<T> selectedItem) {
		if(selectedObject != selectedItem.getItem()
				|| notifyOnSelectionOfAlreadySelectedItem){
			selectedObject = selectedItem.getItem();
			notifyObservers();
		}
		for(MenuItem<T> menuItem : menuItems){
			if(menuItem == selectedItem){
				menuItem.select();
			} else {
				menuItem.unSelect();
			}
		}
	}
	
    public void removeMenuItem(MenuItem<T> item){
        menuBarPanel.remove(item);
    	    menuItems.remove(item);
    }
    
    public void clearItems() {
        menuBarPanel.clear();
        menuItems.clear();
    }
    
    public T getSelectedObject(){
        return selectedObject;
    }
    
    public void hide(boolean autoClosed){
        timeLastHiddenTime = System.currentTimeMillis();
        super.hide(autoClosed);
    }
    
    @Override
    public void show(){
        	if(System.currentTimeMillis() - timeLastHiddenTime < 200){
        		return;
        	}
        	super.show();
        	setMaxHeight();
    }
    
    @Override
    public void setPopupPosition(int left, int top) {
        	super.setPopupPosition(left, top);
        	setMaxHeight();
    }
    

    public void registerObserver(MenuObserver observer){
    	    observers.add(observer);
    }
    
    private void notifyObservers(){
        	for(MenuObserver observer: observers){
        		observer.onSelectedChange();
        	}
    }
    
    private void setMaxHeight() {
    	    getContainerElement().getStyle().setProperty("maxHeight", MainLayoutPanel.getMainAreaAbsoluteTop() + MainLayoutPanel.getMainAreaHeight() - getAbsoluteTop() - 5 + "px");
    }
}
