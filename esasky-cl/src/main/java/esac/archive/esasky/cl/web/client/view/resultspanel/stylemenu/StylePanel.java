package esac.archive.esasky.cl.web.client.view.resultspanel.stylemenu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.VerticalPanel;

import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.LineStyle;
import esac.archive.esasky.cl.web.client.model.SourceShapeType;
import esac.archive.esasky.cl.web.client.utility.DownloadUtils;
import esac.archive.esasky.cl.web.client.view.common.DropDownMenu;
import esac.archive.esasky.cl.web.client.view.common.MenuItem;
import esac.archive.esasky.cl.web.client.view.common.MenuObserver;
import esac.archive.esasky.cl.web.client.view.common.buttons.SignButton;
import esac.archive.esasky.cl.web.client.view.common.buttons.SignButton.SignType;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.PopupHeader;

public class StylePanel extends DialogBox {

	private final StylePanelCallback stylePanelCallback;
	
    public interface StylePanelCallback {
    	public void onShapeColorChanged (String color);
    	public void onSecondaryColorChanged (String color);
    	public void onShapeChanged (String shape);
    	public void onLineStyleChanged (String lineStyle);
    	public void onShapeSizeChanged (double value);
    	public void onSecondaryShapeScaleChanged (double value);
        public void onArrowAvgCheckChanged (boolean checkedOne, boolean checkedTwo);
    }
    
    public interface Resources extends ClientBundle {
        @Source("stylePanel.css")
        @CssResource.NotStrict
        CssResource style();
        
        @Source("circleShape.png")
        ImageResource circleShape();
        
        @Source("crossShape.png")
        ImageResource crossShape();
        
        @Source("plusShape.png")
        ImageResource plusShape();
        
        @Source("rhombShape.png")
        ImageResource rhombShape();
        
        @Source("squareShape.png")
        ImageResource squareShape();
        
        @Source("triangleShape.png")
        ImageResource triangleShape();
        
        @Source("arrow.png")
        ImageResource arrow();
    }
    
    
    private static final int MAX_SLIDER_VALUE = 100;
    
    private Resources resources;
    private CssResource style;

    private String id;
    private String mission;
    private boolean primaryElementsHaveBeenInitialized = false;
    private boolean secondaryElementsHaveBeenInitialized = false;
    
    private String primaryShapeColorPickerId;
    private String primaryShapeSizeId;
    private String primaryShapeId;
    
    private String secondaryShapeColorPickerId = null;
    private String secondaryShapeScaleId = null;
    
    private String primaryColor;
    private double primarySizeRatio;
    private String primaryShapeType;

    private String lineStyle;
    
    private String secondaryColor = null;
    private Double secondaryShapeScale = null; // A ratio from 0.01 to 1.0
    private Boolean arrowAvgChecked = null;
    private Boolean useMedianOnAvgChecked = null;
    
    private final String CONTAINER_ID = "styleMenuContainer";
    
    private long timeLastHiddenTime;
    
    private String preparedId;
    
    
    private CheckBox arrowAvgCheckBox;
    private CheckBox arrowMedianCheckBox;
    
    public StylePanel(String id, String mission, 
                      String primaryColor, Double primarySizeRatio, String primaryShape, String lineStyle,
                      String secondaryColor, Double secondarySizeRatio, Boolean arrowAvgChecked, Boolean useMedianOnAvgChecked,
                      StylePanelCallback callback) {
        
        super(true, false);
        this.resources = GWT.create(Resources.class);
        this.style = this.resources.style();
        this.style.ensureInjected();
        this.setAutoHideEnabled(false);

        this.stylePanelCallback = callback;
        
        this.id = id;
        this.mission = mission;
        
        this.preparedId = DownloadUtils.getValidFilename(id);
        this.primaryShapeColorPickerId = preparedId + "_primaryColor";
        this.primaryColor = primaryColor;
        
        this.primaryShapeSizeId = preparedId + "_primarySize";
        this.primarySizeRatio = primarySizeRatio;

        if (primaryShape != null) {
            this.primaryShapeType = primaryShape;
        }

        if (lineStyle != null) {
        	this.lineStyle = lineStyle;
        }
        
        if (secondaryColor != null) {
            this.secondaryColor = secondaryColor;
            if (secondarySizeRatio != null) {
            	this.secondaryShapeScaleId = preparedId + "_secondaryScale";
            	this.secondaryShapeScale = secondarySizeRatio;
            }
            
            if (arrowAvgChecked != null) {
            	this.arrowAvgChecked = arrowAvgChecked;
            	this.useMedianOnAvgChecked = useMedianOnAvgChecked;
            }
        }
        
        initView();
    }

    HTML primarySlider;
    HTML secondarySlider;
    HTML srcColorPickerContainer;
    
    FlowPanel shapeContainer;
    FlowPanel secondaryContainer;
    FlowPanel innerContainer;
    
    DropDownMenu<LineStyle> lineStyleDropDown;
    DropDownMenu<SourceShapeType> shapeTypeDropDown;
    
    
    private void initView() {
        this.getElement().setId(CONTAINER_ID);
        
        VerticalPanel container = new VerticalPanel();
        
        //Adds the panel header 
        PopupHeader header = new PopupHeader(this,
                TextMgr.getInstance().getText("stylePanel_Title").replace("$MISSION$", mission),
                TextMgr.getInstance().getText("stylePanel_helpMessageText"));
        
        container.add(header);
        
        //Adds the panel container 
        innerContainer = new FlowPanel();
        innerContainer.setStyleName("styleMenuInnerContainer");
        
        FlowPanel shapeContainer = new FlowPanel();
        shapeContainer.addStyleName("styleContainerRow");
        
    	this.lineStyleDropDown = createLineShapeDropdown();
    	shapeContainer.add(lineStyleDropDown);
        
        //Adds the source shape selector
    	this.shapeTypeDropDown = createSourceShapeDropdown();
        shapeContainer.add(this.shapeTypeDropDown);
        
        //Adds the source color picker
        srcColorPickerContainer = new HTML("<div id='" + primaryShapeColorPickerId + "_Container' class='colorPickerContainer'></div>");
        shapeContainer.add(srcColorPickerContainer);
        
        //Adds the source size slider
        primarySlider = new HTML("<input type='range' min='1' max='" + MAX_SLIDER_VALUE + "' value='" + (int)(primarySizeRatio * MAX_SLIDER_VALUE) + "' class='slider primarySlider' id='" + primaryShapeSizeId + "'>");
        shapeContainer.add(primarySlider);
        
        innerContainer.add(shapeContainer);
        if (secondaryShapeColorPickerId != null) {
        	this.secondaryContainer = createSecondaryContainer();
        	innerContainer.add(this.secondaryContainer);
        }

        container.add(innerContainer);

        this.removeStyleName("gwt-DialogBox");
        this.add(container);
    }
	
    private void initPrimaryInteractiveElements() {
        createColorPicker(this, primaryShapeColorPickerId, primaryShapeColorPickerId + "_Container", primaryColor);
        
        addCloseButtonToColorPicker(primaryShapeColorPickerId);
        
        createSlider(this, primaryShapeSizeId);
        primaryElementsHaveBeenInitialized = true;
    }

    private void initSecondaryInteractiveElements() {
    	if (secondaryShapeColorPickerId != null) {
    		createColorPicker(this, secondaryShapeColorPickerId, secondaryShapeColorPickerId + "_Container", secondaryColor);
    		addCloseButtonToColorPicker(secondaryShapeColorPickerId);
    		if (secondaryShapeScaleId != null) {
    			createSlider(this, secondaryShapeScaleId);
    		}
    		secondaryElementsHaveBeenInitialized = true;
    	}
    }
    
    private void addCloseButtonToColorPicker(String colorPickerId) {
        SignButton closePrimaryColorPickerButton = new SignButton(SignType.MINUS);
        closePrimaryColorPickerButton.addStyleName("colorPicker__closeButton");
        closePrimaryColorPickerButton.addClickHandler(new ClickHandler() {
            
            @Override
            public void onClick(ClickEvent event) {
                hideColorPickers();
            }
        });
        
        HTMLPanel.wrap(Document.get().getElementById(colorPickerId)).add(closePrimaryColorPickerButton);
    }
    
	public void toggle() {
		if(isShowing()) {
			hide();
		} else {
			show();
		}
	}
	
    public void hide(boolean autoClosed){
        timeLastHiddenTime = System.currentTimeMillis();
        super.hide(autoClosed);
        hideColorPickers();
    }
    
    private void hideColorPickers() {
        hideColorPicker(primaryShapeColorPickerId);
        
        if (secondaryShapeColorPickerId != null) {
            hideColorPicker(secondaryShapeColorPickerId);
        }
    }
    
    @Override
    public void show(){
    	if(System.currentTimeMillis() - timeLastHiddenTime < 200){
    		CloseEvent.fire(this, null);
    		return;
    	}
    	super.show();
    	if(!primaryElementsHaveBeenInitialized) {
    		initPrimaryInteractiveElements();
    	}
    	if(!secondaryElementsHaveBeenInitialized) {
    		initSecondaryInteractiveElements();
    	}
    }
    
    public String getId() {
        return id;
    }
   
    private DropDownMenu<SourceShapeType> createSourceShapeDropdown() {

        final DropDownMenu<SourceShapeType> srcShapeDropDown = new DropDownMenu<SourceShapeType> (42, "srcShapeDropDown");

        srcShapeDropDown.registerObserver(new MenuObserver() {

            @Override
            public void onSelectedChange() {
            	stylePanelCallback.onShapeChanged(srcShapeDropDown.selectedObject.getName());
            }
        });

        srcShapeDropDown.addStyleName("srcShapeDropDown");
        
        SourceShapeType selectedItem = null;
        for (SourceShapeType sourceShapeType : SourceShapeType.values()) {
            MenuItem<SourceShapeType> dropdownItem = new MenuItem<SourceShapeType>(
                    sourceShapeType, getImageShape(sourceShapeType.getName()));
            dropdownItem.addStyleName("srcShapeDropDownItem");
            if (sourceShapeType.getName().equals(primaryShapeType)) {
                selectedItem = sourceShapeType;
            }
            srcShapeDropDown.addMenuItem(dropdownItem);
        }
        
        if (selectedItem != null) {
            srcShapeDropDown.selectObject(selectedItem);
        }
        
        return srcShapeDropDown;
    }

    private DropDownMenu<LineStyle> createLineShapeDropdown() {
    	
    	final DropDownMenu<LineStyle> lineShapeDropDown = new DropDownMenu<LineStyle> ("", "", 60, false, "lineStyleDropDown");
    	
    	lineShapeDropDown.registerObserver(new MenuObserver() {
    		
    		@Override
    		public void onSelectedChange() {
    			stylePanelCallback.onLineStyleChanged(lineShapeDropDown.selectedObject.getName());
    		}
    	});
    	
    	lineShapeDropDown.addStyleName("lineShapeDropDown");
    	
    	LineStyle selectedItem = null;
    	for (LineStyle lineShapeType : LineStyle.values()) {
    		MenuItem<LineStyle> dropdownItem = new MenuItem<LineStyle>(
    				lineShapeType, lineShapeType.getView(), false);
    		dropdownItem.addStyleName("srcShapeDropDownItem");
    		if (lineShapeType.getName().equals(lineStyle)) {
    			selectedItem = lineShapeType;
    		}
    		lineShapeDropDown.addMenuItem(dropdownItem);
    	}
    	
    	if (selectedItem != null) {
    		lineShapeDropDown.selectObject(selectedItem);
    	}
    	
    	return lineShapeDropDown;
    }
    
    
    private FlowPanel createSecondaryContainer() {
    	 FlowPanel secondaryShapeContainer = new FlowPanel();
         secondaryShapeContainer.addStyleName("styleContainerRow");
         
         FlowPanel secondaryContainer = new FlowPanel();
         secondaryContainer.setStyleName("styleMenuSecondContainer");
         
         if (secondaryColor != null) {
             this.secondaryShapeColorPickerId = preparedId + "_secondaryColor";
             if (this.secondaryShapeScale != null) {
             	this.secondaryShapeScaleId = preparedId + "_secondaryScale";
             }
         }
         
         //Adds the arrow color picker
         if (secondaryShapeColorPickerId != null) {
             if (arrowAvgChecked != null) {
                 Image arrowImage = new Image(resources.arrow().getSafeUri());
                 arrowImage.addStyleName("arrowImg");
                 secondaryShapeContainer.add(arrowImage);
             }
             
             HTML secondaryShapeColorPickerContainer = new HTML("<div id='" + secondaryShapeColorPickerId + "_Container' class='colorPickerContainer'></div>");
             secondaryShapeContainer.add(secondaryShapeColorPickerContainer);
             
             //Adds the arrow scale slider
             if (secondaryShapeScaleId != null) {
                 
                 secondarySlider = new HTML("<input type='range' min='1' max='" + MAX_SLIDER_VALUE + "' value='" + (int)(secondaryShapeScale * MAX_SLIDER_VALUE) + "' class='slider secondarySlider' id='" + secondaryShapeScaleId + "'>");
                 secondaryShapeContainer.add(secondarySlider);
             }
             
             secondaryContainer.add(secondaryShapeContainer);
             
             //Adds the remove proper motion average check box
             if (arrowAvgChecked != null) {
                 
                 arrowAvgCheckBox = new CheckBox(TextMgr.getInstance().getText("stylePanel_removeAvgCheckBox"));
                 arrowAvgCheckBox.addStyleName("arrowAvgCheckBox");
                 arrowAvgCheckBox.setValue(arrowAvgChecked && !useMedianOnAvgChecked);
                 arrowAvgCheckBox.addClickHandler(new ClickHandler() {

                     @Override
                     public void onClick(ClickEvent arg0) {
                         arrowMedianCheckBox.setValue(false);
                         final boolean showAvgPM = arrowAvgCheckBox.getValue() || arrowMedianCheckBox.getValue();
                         stylePanelCallback.onArrowAvgCheckChanged(showAvgPM, arrowMedianCheckBox.getValue());
                     }
                 });
                 
                 secondaryContainer.add(arrowAvgCheckBox);
                 
                 arrowMedianCheckBox = new CheckBox(TextMgr.getInstance().getText("stylePanel_removeMedianCheckBox"));
                 arrowMedianCheckBox.addStyleName("arrowAvgCheckBox");
                 arrowMedianCheckBox.setValue(arrowAvgChecked && useMedianOnAvgChecked);
                 arrowMedianCheckBox.addClickHandler(new ClickHandler() {

                     @Override
                     public void onClick(ClickEvent arg0) {
                         arrowAvgCheckBox.setValue(false);
                         final boolean showAvgPM = arrowAvgCheckBox.getValue() || arrowMedianCheckBox.getValue();
                         stylePanelCallback.onArrowAvgCheckChanged(showAvgPM, arrowMedianCheckBox.getValue());
                     }
                 });
                 
                 secondaryContainer.add(arrowMedianCheckBox);
             }
         }
         
         return secondaryContainer;
         
    }
    
    public static native void createColorPicker(StylePanel instance, String colorPickerId, String colorPickerContainerId, String color) /*-{
    
        $wnd.createColorPicker(colorPickerId, colorPickerContainerId, color, function (color, id) {
            instance.@esac.archive.esasky.cl.web.client.view.resultspanel.stylemenu.StylePanel::fireColorChangedEvent(Ljava/lang/String;Ljava/lang/String;)(id,color);
          });
          
    }-*/;
    
    public static native void removeColorPicker(String colorPickerId) /*-{
    
        $wnd.removeColorPicker(colorPickerId);
          
    }-*/;
    
    public static native void hideColorPicker(String colorPickerId) /*-{
    
        $wnd.hideColorPicker(colorPickerId);
          
    }-*/;

    public static native void setColor(String colorPickerId, String color) /*-{
    
        $wnd.setColorPickerColor(colorPickerId, color);
          
    }-*/;
    
    private void fireColorChangedEvent(String colorPickerId, String color) {
        if (colorPickerId.equals(primaryShapeColorPickerId)) {
        	primaryColor = color;
            shapeColorChangedTimer.onColorChange(color);
        } else if (colorPickerId.equals(secondaryShapeColorPickerId)) {
        	secondaryColor = color;
        	secondaryColorChangedTimer.onColorChange(color);
        }
    }
    
    private ShapeColorChangedTimer shapeColorChangedTimer = new ShapeColorChangedTimer(false);
    private ShapeColorChangedTimer secondaryColorChangedTimer = new ShapeColorChangedTimer(true);
    
    private class ShapeColorChangedTimer extends Timer{
        private boolean isSecondaryColor;
        private String color;
        private long timeAtLastRun;
        
        public ShapeColorChangedTimer(boolean isSecondaryColor) {
            super();
            this.isSecondaryColor = isSecondaryColor;
        }
        @Override
        public void run() {
            if(isSecondaryColor) {
                stylePanelCallback.onSecondaryColorChanged(color);
            } else {
                stylePanelCallback.onShapeColorChanged(color);
            }
            timeAtLastRun = System.currentTimeMillis();
        }
        
        public void onColorChange(String newColor) {
            color = newColor;
            if(System.currentTimeMillis() - timeAtLastRun > 100) {
                run();
            } else {
                schedule(100);
            }
        }
    }
    
    public static native void createSlider(StylePanel instance, String sliderId) /*-{
        
        $wnd.document.getElementById(sliderId).oninput = function() {
          instance.@esac.archive.esasky.cl.web.client.view.resultspanel.stylemenu.StylePanel::fireSliderChangedEvent(Ljava/lang/String;Ljava/lang/String;)(this.id,this.value);
        }
        
    }-*/;
    
    private native void setSliderValue(Element slider, double value ) /*-{
		slider.children[0].value = value;
	}-*/;
    
    private void fireSliderChangedEvent(String sliderId, String value) {
        if (sliderId.equals(primaryShapeSizeId)) {
        	primarySizeRatio = ((double)Integer.parseInt(value))/((double)MAX_SLIDER_VALUE);
        	stylePanelCallback.onShapeSizeChanged(primarySizeRatio);
        } else if (sliderId.equals(secondaryShapeScaleId)) {
        	secondaryShapeScale = ((double)Integer.parseInt(value))/((double)MAX_SLIDER_VALUE);
        	stylePanelCallback.onSecondaryShapeScaleChanged(secondaryShapeScale);
        }
    }
    
    private ImageResource getImageShape (String shape) {
       if (shape.equals("plus")) {
            return resources.plusShape();
       } else if (shape.equals("cross")) {
            return resources.crossShape();
       } else if (shape.equals("rhomb")) {
            return resources.rhombShape();
       } else if (shape.equals("triangle")) {
            return resources.triangleShape();
       } else if (shape.equals("circle")) {
            return resources.circleShape();
       } else if (shape.equals("square")){
            return resources.squareShape();
       } else if (shape.equals("solid")){
    	   return resources.circleShape();
       } else {
    	   return resources.squareShape();
       }
    }
    
    @Override
    public void removeFromParent() {
    	super.removeFromParent();
        removeColorPicker(primaryShapeColorPickerId);
        
        if (secondaryShapeColorPickerId != null) {
            removeColorPicker(secondaryShapeColorPickerId);
        }
        primaryElementsHaveBeenInitialized = false;
        secondaryElementsHaveBeenInitialized = false;
    }
    
    public void showSecondaryContainer(String secondaryColor, Double secondarySizeRatio, Boolean arrowAvgChecked, Boolean useMedianOnAvgChecked) {
    	this.secondaryColor = secondaryColor;
    	this.secondaryShapeScale = secondarySizeRatio;
    	this.arrowAvgChecked = arrowAvgChecked;
    	this.useMedianOnAvgChecked = useMedianOnAvgChecked;
    	
    	if(secondaryContainer != null) {
    		UIObject.setVisible(secondaryContainer.getElement(), true);
    		setColor(secondaryShapeColorPickerId, primaryColor);
        	setSliderValue(secondarySlider.getElement(), primarySizeRatio);

    	}else {
    		this.secondaryContainer = createSecondaryContainer();
    		innerContainer.add(secondaryContainer);
    	}
    }

    public void hideSecondaryContainer() {
    	if(secondaryContainer != null) {
    		UIObject.setVisible(secondaryContainer.getElement(), false);
    	}
    }
    
    public void showLineStyleDropDown(String lineStyle) {
    	this.lineStyle = lineStyle;
    	if(lineStyleDropDown != null) {
    		UIObject.setVisible(lineStyleDropDown.getElement(), true);
    		for(MenuItem<LineStyle> item : lineStyleDropDown.getMenuItems()) {
    			if(item.getItem().getName() == lineStyle) {
    				lineStyleDropDown.selectObject(item.getItem());
    			}
    		}
    	}
    }

    public void hideLineStyleDropDown() {
    	if(lineStyleDropDown != null) {
    		UIObject.setVisible(lineStyleDropDown.getElement(), false);
    	}
    }

    public void showShapeTypeDropDown(String primaryShape) {
    	this.primaryShapeType = primaryShape;
    	if(shapeTypeDropDown != null) {
    		UIObject.setVisible(shapeTypeDropDown.getElement(), true);
    		for(MenuItem<SourceShapeType> item : shapeTypeDropDown.getMenuItems()) {
    			if(item.getItem().getName() == primaryShapeType) {
    				shapeTypeDropDown.selectObject(item.getItem());
    			}
    		}
    	}
    }
    
    public void hideShapeTypeDropDown() {
    	if(shapeTypeDropDown != null) {
    		UIObject.setVisible(shapeTypeDropDown.getElement(), false);
    	}
    }
    
    public void showPrimary(String primaryColor, Double primarySizeRatio) {
    	this.primaryColor = primaryColor;
    	this.primarySizeRatio = primarySizeRatio;
    	setColor(primaryShapeColorPickerId, primaryColor);
    	setSliderValue(primarySlider.getElement(), primarySizeRatio);
    	
    }
    
    public void hidePrimary() {
    	if(shapeTypeDropDown != null) {
    		UIObject.setVisible(shapeTypeDropDown.getElement(), false);
    	}
    }
}
