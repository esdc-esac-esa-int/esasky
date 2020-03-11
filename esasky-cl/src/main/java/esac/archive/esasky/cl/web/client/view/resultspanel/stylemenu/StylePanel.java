package esac.archive.esasky.cl.web.client.view.resultspanel.stylemenu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.SourceShapeType;
import esac.archive.esasky.cl.web.client.utility.DownloadUtils;
import esac.archive.esasky.cl.web.client.view.common.DropDownMenu;
import esac.archive.esasky.cl.web.client.view.common.MenuItem;
import esac.archive.esasky.cl.web.client.view.common.MenuObserver;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.PopupHeader;

public class StylePanel extends DialogBox {

	private final StylePanelCallback stylePanelCallback;
	
    public interface StylePanelCallback {
    	public void onShapeColorChanged (String color);
    	public void onArrowColorChanged (String color);
    	public void onOrbitColorChanged (String color);
    	public void onShapeChanged (String shape);
    	public void onShapeSizeChanged (double value);
    	public void onArrowScaleChanged (double value);
    	public void onOrbitScaleChanged (double value);
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
    private boolean interactiveElementsHaveBeenInitialized = false;
    
    private String srcColorPickerId;
    private String srcSizeId;
    private String srcShapeId;
    
    private String arrowColorPickerId = null;
    private String arrowScaleId = null;
    
    private String orbitColorPickerId = null;
    private String orbitColor = null;
    private String orbitScaleId = null;
    private double orbitScale;
    
    private String srcColor;
    private double srcSizeRatio;
    private String srcShape;
    
    private String arrowColor = null;
    private Double arrowScale = null; // A ratio from 0.01 to 1.0
    private Boolean arrowAvgChecked = null;
    private Boolean useMedianOnAvgChecked = null;
    
    private final String CONTAINER_ID = "styleMenuContainer";
    
    private long timeLastHiddenTime;
    
    
    private CheckBox arrowAvgCheckBox;
    private CheckBox arrowMedianCheckBox;
    
    public StylePanel(String id, String mission, 
                      String srcColor, Double srcSizeRatio, String srcShape,
                      String arrowColor, Double arrowScale, Boolean arrowAvgChecked, Boolean useMedianOnAvgChecked,
                      String orbitColor, Double orbitScale,
                      StylePanelCallback callback) {
        
        super(true, false);
        this.resources = GWT.create(Resources.class);
        this.style = this.resources.style();
        this.style.ensureInjected();
        this.setAutoHideEnabled(false);

        this.stylePanelCallback = callback;
        
        this.id = id;
        this.mission = mission;
        
        final String preparedId = DownloadUtils.getValidFilename(id);
        this.srcColorPickerId = preparedId + "_srcColor";
        this.srcColor = srcColor;
        
        this.srcSizeId = preparedId + "_srcSize";
        this.srcSizeRatio = srcSizeRatio;

        if (srcShape != null) {
            this.srcShape = srcShape;
        }
        
        if (arrowColor != null) {
            this.arrowColorPickerId = preparedId + "_arrowColor";
            this.arrowColor = arrowColor;
            if (arrowScale != null) {
            	this.arrowScaleId = preparedId + "_arrowScale";
            	this.arrowScale = arrowScale;
            }
            
            if (arrowAvgChecked != null) {
            	this.arrowAvgChecked = arrowAvgChecked;
            	this.useMedianOnAvgChecked = useMedianOnAvgChecked;
            }
        }
        
        
        if (orbitColor != null) {
        	this.orbitColorPickerId = preparedId + "_orbitColor";
        	this.orbitColor = orbitColor;
        }
        
        if (orbitScale != null) {
        	this.orbitScaleId = preparedId + "_orbitScale";
        	this.orbitScale = orbitScale;
        }
        
        initView();
    }

    HTML srcSlider;
    HTML arrowSlider;
    HTML orbitSlider;
    HTML srcColorPickerContainer;
    
    private void initView() {
        this.getElement().setId(CONTAINER_ID);
        
        VerticalPanel container = new VerticalPanel();
        
        //Adds the panel header 
        PopupHeader header = new PopupHeader(this,
                TextMgr.getInstance().getText("stylePanel_Title").replace("$MISSION$", mission),
                TextMgr.getInstance().getText("stylePanel_helpMessageText"));
        
        container.add(header);
        
        //Adds the panel container 
        FlowPanel innerContainer = new FlowPanel();
        innerContainer.setStyleName("styleMenuInnerContainer");
        
        FlowPanel shapeContainer = new FlowPanel();
        shapeContainer.addStyleName("styleContainerRow");
        
        //Adds the source shape selector
        if (this.srcShape != null) {
            shapeContainer.add(createSourceShapeDropdown());
        }
        
        //Adds the source color picker
        srcColorPickerContainer = new HTML("<div id='" + srcColorPickerId + "_Container' class='colorPickerContainer'></div>");
        shapeContainer.add(srcColorPickerContainer);
        
        //Adds the source size slider
        srcSlider = new HTML("<input type='range' min='1' max='" + MAX_SLIDER_VALUE + "' value='" + (int)(srcSizeRatio * MAX_SLIDER_VALUE) + "' class='slider srcSlider' id='" + srcSizeId + "'>");
        shapeContainer.add(srcSlider);
        
        innerContainer.add(shapeContainer);
        
        FlowPanel properMotionShapeContainer = new FlowPanel();
        properMotionShapeContainer.addStyleName("styleContainerRow");
        
        //Adds the arrow color picker
        if (arrowColorPickerId != null) {
            
            Image arrowImage = new Image(resources.arrow().getSafeUri());
            arrowImage.addStyleName("arrowImg");
            properMotionShapeContainer.add(arrowImage);
            
            HTML arrowColorPickerContainer = new HTML("<div id='" + arrowColorPickerId + "_Container' class='colorPickerContainer'></div>");
            properMotionShapeContainer.add(arrowColorPickerContainer);
            
            //Adds the arrow scale slider
            if (arrowScaleId != null) {
                
                arrowSlider = new HTML("<input type='range' min='1' max='" + MAX_SLIDER_VALUE + "' value='" + (int)(arrowScale * MAX_SLIDER_VALUE) + "' class='slider arrowSlider' id='" + arrowScaleId + "'>");
                properMotionShapeContainer.add(arrowSlider);
            }
            
            innerContainer.add(properMotionShapeContainer);
            
            
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
                
                innerContainer.add(arrowAvgCheckBox);
                
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
                
                innerContainer.add(arrowMedianCheckBox);
            }
        }
        
        if (orbitColorPickerId != null) {
        	FlowPanel orbitContainer = new FlowPanel();
        	orbitContainer.addStyleName("styleContainerRow");
            HTML orbitColorPickerContainer = new HTML("<div id='" + orbitColorPickerId + "_Container' class='colorPickerContainer'></div>");
            orbitContainer.add(orbitColorPickerContainer);
            
            //Adds the source size slider
            orbitSlider = new HTML("<input type='range' min='1' max='" + MAX_SLIDER_VALUE + "' value='" + (int)(orbitScale * MAX_SLIDER_VALUE) + "' class='slider orbitSlider' id='" + orbitScaleId + "'>");
            orbitContainer.add(orbitSlider);
            
            innerContainer.add(orbitContainer);
        }
        container.add(innerContainer);

        this.removeStyleName("gwt-DialogBox");
        this.add(container);
    }
	
    private void initInteractiveElements() {
        createColorPicker(this, srcColorPickerId, srcColorPickerId + "_Container", srcColor);
        createSlider(this, srcSizeId);
        
        if (arrowColorPickerId != null) {
            createColorPicker(this, arrowColorPickerId, arrowColorPickerId + "_Container", arrowColor);
            if (arrowScaleId != null) {
            	createSlider(this, arrowScaleId);
            }
        }
        
        
        if (orbitScaleId != null) {
        	createSlider(this, orbitScaleId);
        }
        if (orbitColorPickerId != null) {
        	createColorPicker(this, orbitColorPickerId, orbitColorPickerId + "_Container", orbitColor);
        }
        interactiveElementsHaveBeenInitialized = true;
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

        hideColorPicker(srcColorPickerId);
        
        if (arrowColorPickerId != null) {
            hideColorPicker(arrowColorPickerId);
        }
    }
    
    @Override
    public void show(){
    	if(System.currentTimeMillis() - timeLastHiddenTime < 200){
    		CloseEvent.fire(this, null);
    		return;
    	}
    	super.show();
    	if(!interactiveElementsHaveBeenInitialized) {
    		initInteractiveElements();
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
            if (sourceShapeType.getName().equals(srcShape)) {
                selectedItem = sourceShapeType;
            }
            srcShapeDropDown.addMenuItem(dropdownItem);
        }
        
        if (selectedItem != null) {
            srcShapeDropDown.selectObject(selectedItem);
        }
        
        return srcShapeDropDown;
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
    
    private void fireColorChangedEvent(String colorPickerId, String color) {
        if (colorPickerId.equals(srcColorPickerId)) {
        	srcColor = color;
            stylePanelCallback.onShapeColorChanged(color);
        } else if (colorPickerId.equals(arrowColorPickerId)) {
        	arrowColor = color;
            stylePanelCallback.onArrowColorChanged(color);
        } else if (orbitColorPickerId.equals(orbitColorPickerId)) {
        	orbitColor = color;
        	stylePanelCallback.onOrbitColorChanged(color);
        }
    }
    
    public static native void createSlider(StylePanel instance, String sliderId) /*-{
        
        $wnd.document.getElementById(sliderId).oninput = function() {
          instance.@esac.archive.esasky.cl.web.client.view.resultspanel.stylemenu.StylePanel::fireSliderChangedEvent(Ljava/lang/String;Ljava/lang/String;)(this.id,this.value);
        }
        
    }-*/;
    
    private void fireSliderChangedEvent(String sliderId, String value) {
        if (sliderId.equals(srcSizeId)) {
        	srcSizeRatio = ((double)Integer.parseInt(value))/((double)MAX_SLIDER_VALUE);
        	stylePanelCallback.onShapeSizeChanged(srcSizeRatio);
        } else if (sliderId.equals(arrowScaleId)) {
        	arrowScale = ((double)Integer.parseInt(value))/((double)MAX_SLIDER_VALUE);
        	stylePanelCallback.onArrowScaleChanged(arrowScale);
        } else if (sliderId.equals(orbitScaleId)) {
        	orbitScale = ((double)Integer.parseInt(value))/((double)MAX_SLIDER_VALUE);
        	stylePanelCallback.onOrbitScaleChanged(orbitScale);
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
       } else {
            return resources.squareShape();
       }
    }
    
    @Override
    public void removeFromParent() {
    	super.removeFromParent();
        removeColorPicker(srcColorPickerId);
        
        if (arrowColorPickerId != null) {
            removeColorPicker(arrowColorPickerId);
        }
        interactiveElementsHaveBeenInitialized = false;
    }
}
