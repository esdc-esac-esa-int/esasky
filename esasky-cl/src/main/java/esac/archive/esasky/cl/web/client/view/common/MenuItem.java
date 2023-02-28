package esac.archive.esasky.cl.web.client.view.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.view.common.buttons.CloseButton;
import esac.archive.esasky.cl.web.client.view.common.icons.Icons;

public class MenuItem<T> extends FocusPanel{
	private T item;
	
	private boolean isSelected;
	private boolean tickWhenSelected;
	
	private Image selectedImage = new Image();
	private Label label;
	private ImageResource imageRes;
	private Image image;
	private int width;
	private FlowPanel horizontalPanel = new FlowPanel();
	private CloseButton removeRowButton = new CloseButton();
	private boolean isDisabled = false;
	
    private final Resources resources = GWT.create(Resources.class);

    private CssResource style;


	public interface Resources extends ClientBundle {

        @Source("menuItem.css")
        @CssResource.NotStrict
        CssResource style();
    }
	public interface OnRemove<T>{
        void onRemove(MenuItem<T> menuItem);
	}
    public MenuItem(T item, String text, boolean tickWhenSelected, boolean removableItems, OnRemove<T> onRemove){
        this(item, text, tickWhenSelected);
        if(removableItems) {
            removeRowButton.setSmallStyle();
            horizontalPanel.add(removeRowButton);
            removeRowButton.addStyleName("menuItem_removeRow");
            removeRowButton.setTitle(TextMgr.getInstance().getText("menuItem_removeEntry"));
            removeRowButton.setVisible(false);
            removeRowButton.addClickHandler(new ClickHandler() {
                
                @Override
                public void onClick(ClickEvent event) {
                    event.stopPropagation();
                    event.preventDefault();
                    if(onRemove != null) {
                        onRemove.onRemove(MenuItem.this);
                    }
                }
            });
            this.addMouseOverHandler(new MouseOverHandler() {
                
                @Override
                public void onMouseOver(MouseOverEvent event) {
                    removeRowButton.setVisible(true);
                }
            });
            this.addMouseOutHandler(new MouseOutHandler() {
                
                @Override
                public void onMouseOut(MouseOutEvent event) {
                    removeRowButton.setVisible(false);
                }
            });
        }
    }

    public MenuItem(T item, String text, boolean tickWhenSelected){
        this.style = this.resources.style();
        this.style.ensureInjected();
                
		this.item = item;
		this.tickWhenSelected = tickWhenSelected;
		horizontalPanel.addStyleName("fillWidth");
		label = new Label();
        label.setText(text);
        label.addStyleName("dropdownLabel");
		horizontalPanel.add(label);
        add(horizontalPanel);
        if(tickWhenSelected){
            selectedImage = new Image(Icons.getSelectedIcon().getSafeUri());
            horizontalPanel.add(selectedImage);
            selectedImage.addStyleName("selectedIcon");
        }

		addStyleName("dropdownItem");
        
        unSelect();
	}
	
	public MenuItem(T item, String text, String toolTip, boolean tickWhenSelected){
		this(item, text, tickWhenSelected);
		setToolTip(toolTip);
	}
	
	public MenuItem(T item, ImageResource image){
        this.style = this.resources.style();
        this.style.ensureInjected();
        
        this.item = item;
        this.tickWhenSelected = false;
        
        this.imageRes = image;
        this.image = new Image(image.getSafeUri());
        add(this.image);
        addStyleName("dropdownItem");
        
        unSelect();
    }
	
	private void setToolTip(String toolTip) {
		setTitle(toolTip);
	}

	public T getItem(){
		return item;
	}
	
	public boolean getIsSelected(){
		return isSelected;
	}
	
	public void select(){
		if (!isDisabled) {
			isSelected = true;
			if(tickWhenSelected){
				selectedImage.setVisible(true);
			}
			updateMaximumWidth();
		}
	}
	
	public void unSelect(){
		isSelected = false;
		if(tickWhenSelected){
			selectedImage.setVisible(false);
		}
		updateMaximumWidth();
	}
	
	public String getText(){
		return label.getText();
	}
	
    public ImageResource getImage(){
        return this.imageRes;
    }
	
	public void setMaximumWidth(int width){
		this.width = width;
		updateMaximumWidth();
	}
	
	private void updateMaximumWidth(){
		if(width == 0) return;
		
		int newWidth = (getIsSelected() && tickWhenSelected) ? width - selectedImage.getOffsetWidth() - 2 : width;
		if (!hasImage()) {
		    label.getElement().getStyle().setProperty("maxWidth", newWidth + "px");
		} else {
		    image.getElement().getStyle().setProperty("maxWidth", newWidth + "px");
		}
	}
	
	public boolean hasImage () {
	    return this.image != null;
	}

	public boolean isDisabled() {
		return isDisabled;
	}

	public void setDisabled(boolean disabled) {
		isDisabled = disabled;

		if (isDisabled) {
			this.addStyleName("dropdownItemDisabled");
		} else {
			this.removeStyleName("dropdownItemDisabled");
		}
	}
	
}