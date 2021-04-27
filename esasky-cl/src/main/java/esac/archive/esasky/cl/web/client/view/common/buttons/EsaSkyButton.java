package esac.archive.esasky.cl.web.client.view.common.buttons;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;

import esac.archive.esasky.cl.web.client.view.animation.AnimationObserver;
import esac.archive.esasky.cl.web.client.view.animation.RotateAnimation;


public class EsaSkyButton extends Composite implements HasClickHandlers{
	protected Image enabledImage;
	
    private Resources resources = GWT.create(Resources.class);
    private CssResource style;
    
	private RotateAnimation rotateAnimation;
    
	protected final PushButtonWithVisibleOnClick button;
	
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
		FlowPanel container = new FlowPanel();
		button = new PushButtonWithVisibleOnClick(new Image(image));
		container.add(button);
		
		initWidget(container);
		
		initStyle();
		
		this.enabledImage = new Image(image);
        this.enabledImage.addStyleName("fillParent");
        button.getUpFace().setImage(enabledImage);
        
        
        rotateAnimation = new RotateAnimation(enabledImage.getElement());
        
        rotateAnimation.addObserver(new AnimationObserver() {
            
            @Override
            public void onComplete(double currentPosition) {
            	button.getUpFace().setImage(enabledImage);
            }
        });
	}
	
	public EsaSkyButton(String text){
		FlowPanel container = new FlowPanel();
		button = new PushButtonWithVisibleOnClick(text);
		button.addStyleName("stringButton");
		container.add(button);
		 
	        
		
		initWidget(container);
		
		initStyle();
		
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

	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		stopPropagationOfMouseDownAndTouchStartToParentElements();
		return button.addClickHandler(handler);
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

}
