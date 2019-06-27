	package esac.archive.esasky.cl.web.client.view.resultspanel.tab.filter;


import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.dom.client.Element;

import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.FilterObserver;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyButton;

public class DoubleFilterDialogBox extends FilterDialogBox {
    
    private final Resources resources = GWT.create(Resources.class);
    private CssResource style;
    
    private FilterObserver filterObserver;
    private NumberFormat numberFormat;
    private JavaScriptObject slider;
    private boolean hasSliderBeenAddedToDialogBox = false;
    private boolean reRenderingWouldTakeSignificantTime;
    
    private double minValue;
    private double maxValue;
    private double currentLow = minValue;
    private double currentHigh = maxValue;
    private double range;
    private double precision;
    
    private final int SLIDER_MAX = 10000;
    private double currentSliderFromFraction = 0;
    private double currentSliderToFraction = 1 * SLIDER_MAX;
    
    private TextBox fromTextBox = new TextBox();
    private TextBox toTextBox = new TextBox();
    
    private final String sliderSelectorContainerId;
    private final String doubleFilterContainerId;
    
    protected FilterTimer filterTimer = new FilterTimer();
	
    public interface Resources extends ClientBundle {
        @Source("doubleFilterDialogBox.css")
        @CssResource.NotStrict
        CssResource style();
        
		@Source("reset.png")
		ImageResource resetIcon();
    }
    
	public DoubleFilterDialogBox(String columnName, final String filterButtonId, final FilterObserver filterObserver) {
		super(filterButtonId);
        this.style = this.resources.style();
        this.style.ensureInjected();
        doubleFilterContainerId = filterButtonId.replaceAll("(\\(|\\)| )", "_") + "doubleColumn";
        sliderSelectorContainerId = "selectorId_WTIH_NO_TITLE_" + doubleFilterContainerId;
        this.filterObserver = filterObserver;
        
        HTML columnNameHTML = new HTML(columnName);
        columnNameHTML.addStyleName("filterColumnName");

        fromTextBox.addStyleName("sliderTextBox");
        fromTextBox.addBlurHandler(new BlurHandler() {
			
			@Override
			public void onBlur(BlurEvent event) {
				onChangeFromTextBox();
			}
		});
        
        fromTextBox.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					onChangeFromTextBox();
				}
			}
		});
        addElementNotAbleToInitiateMoveOperation(fromTextBox.getElement());
        toTextBox.addBlurHandler(new BlurHandler() {
        	
        	@Override
        	public void onBlur(BlurEvent event) {
        		onChangeFromTextBox();
        	}
        });
        toTextBox.addKeyUpHandler(new KeyUpHandler() {
        	
        	@Override
        	public void onKeyUp(KeyUpEvent event) {
        		if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
        			onChangeFromTextBox();
        		}
        	}
        });
        
        toTextBox.addStyleName("sliderTextBox");
        addElementNotAbleToInitiateMoveOperation(toTextBox.getElement());
        
        FlowPanel container = new FlowPanel();
        container.add(columnNameHTML);
		container.add(fromTextBox);
        
		EsaSkyButton resetButton = new EsaSkyButton(this.resources.resetIcon());
		resetButton.setTitle(TextMgr.getInstance().getText("doubleFilter_resetFilter"));
		resetButton.addStyleName("resetDoubleFilterButton");
		resetButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				currentSliderFromFraction = 0;
				currentSliderToFraction = SLIDER_MAX;
				setSliderFraction(slider, 0, SLIDER_MAX);
				setTextBoxValues(minValue, maxValue);
				filterTimer.setNewRange(minValue, maxValue);
			}
		});
		container.add(resetButton);
		container.add(toTextBox);
		
		container.getElement().setId(doubleFilterContainerId);
		setWidget(container);
		
		addStyleName("doubleFilterDialogBox");
	}
	
	@Override
	public void show() {
		super.show();
		if(!hasSliderBeenAddedToDialogBox) {
			hasSliderBeenAddedToDialogBox = true;
			slider = createSliderFilter(this, doubleFilterContainerId, sliderSelectorContainerId, SLIDER_MAX);
			setTextBoxValues(minValue, maxValue);
			addElementNotAbleToInitiateMoveOperation("slider-" + sliderSelectorContainerId);
		}
	}

	@Override
	protected boolean isFilterActive() {
		return hasSliderBeenAddedToDialogBox 
				&& (currentSliderFromFraction > 0 || currentSliderToFraction < SLIDER_MAX)
				&& 	!(Double.isInfinite(minValue) || Double.isInfinite(maxValue));
	}
	
    private native JavaScriptObject createSliderFilter(DoubleFilterDialogBox instance, String containerId, String sliderSelectorId, int multiple) /*-{
	    var sliderSelector = $wnd.createSliderSelector(sliderSelectorId,
	                                      "",
	                                      0,
	                                      multiple,
	                                      $entry(function (selector) {
	                                      	instance.@esac.archive.esasky.cl.web.client.view.resultspanel.tab.filter.DoubleFilterDialogBox::fireRangeChangedEvent(DD)(selector.fromValue, selector.toValue);
	                                      	}),
	                                      20,
	                                      1e-20);
		$wnd.$("#" + containerId).append(sliderSelector.$html);
		return sliderSelector;
    }-*/;

    private void fireRangeChangedEvent(double fromValue, double toValue) {
    	currentSliderFromFraction = fromValue;
    	currentSliderToFraction = toValue;
    	
    	fromValue = minValue + fromValue * range / SLIDER_MAX;
    	toValue = minValue + toValue * range / SLIDER_MAX;
    	setTextBoxValues(fromValue, toValue);
    	filterTimer.setNewRange(fromValue, toValue);
    }
    
    private void setTextBoxValues(double fromValue, double toValue) {
		if(Double.isNaN(fromValue) || Double.isNaN(toValue)) {
			return;
		}
    	fromTextBox.setText(numberFormat.format(fromValue));
    	toTextBox.setText(numberFormat.format(toValue));
    	if(Double.isInfinite(fromValue) || Double.isInfinite(toValue)) {
    		return;
    	}
		if(Math.abs(Double.parseDouble(fromTextBox.getText())) < (1 / Math.pow(10, precision))) {
			fromTextBox.setText(NumberFormat.getFormat(numberFormat.getPattern() + "E0").format(fromValue));
			if(fromTextBox.getText().equals("0E0")){
				fromTextBox.setText("0");
			}
		}
		if(Math.abs(Double.parseDouble(toTextBox.getText())) < (1 / Math.pow(10, precision))) {
			toTextBox.setText(NumberFormat.getFormat(numberFormat.getPattern() + "E0").format(toValue));
			if(toTextBox.getText().equals("0E0")){
				toTextBox.setText("0");
			}
		}
    }
    
    public void setRange(Double minValue, Double maxValue, NumberFormat numberFormat, int precision){
    	range = Math.abs(maxValue - minValue);
    	this.numberFormat = numberFormat;
    	this.precision = precision;
    	
    	boolean filterWasActive = isFilterActive();
    	
    	if(!filterWasActive) {
    		this.currentLow = minValue;
    		this.currentHigh = maxValue;
    	}
    	this.minValue = minValue;
    	this.maxValue = maxValue;
    	if(hasSliderBeenAddedToDialogBox) {
    		if(currentLow < minValue) {
    			currentLow = minValue;
    		}
    		if(currentHigh > maxValue) {
    			currentHigh = maxValue;
    		}
    		if(filterWasActive) {
    			setSliderValues(currentLow, currentHigh);
    		}
    		setTextBoxValues(currentLow, currentHigh);
    	}
    	ensureCorrectFilterButtonStyle();
    };
    
    private void onChangeFromTextBox() {
    	try {
    		double fromValue = Double.parseDouble(fromTextBox.getText());
    		double toValue = Double.parseDouble(toTextBox.getText());
    		if(toValue < fromValue) {
    			double temp = fromValue;
    			fromValue = toValue;
    			toValue = temp;
    		}
    		
    		if(fromValue < minValue) {
    			fromValue = minValue;
    		}
    		if(toValue > maxValue) {
    			toValue = maxValue;
    		}
    		
    		setSliderValues(fromValue, toValue);
    		
    		setTextBoxValues(fromValue, toValue);
    		filterTimer.setNewRange(fromValue, toValue);
    		filterTimer.run();
    		
    	} catch (NumberFormatException exception) {
    		setTextBoxValues(currentLow, currentHigh);
    	}
    }
    
    public double getCurrentLow() {
    	return currentLow;
    }
    
    public double getCurrentHigh() {
    	return currentHigh;
    }
    
	private void setSliderValues(double currentLow, double currentHigh) {
		currentSliderFromFraction = (currentLow - minValue) / range * SLIDER_MAX;
		currentSliderToFraction = (currentHigh - minValue) / range * SLIDER_MAX;
		setSliderFraction(slider, currentSliderFromFraction, currentSliderToFraction);
    }
    
    private native void setSliderFraction(JavaScriptObject slider, double lowerHandleFraction, double higherHandleFraction) /*-{
    	slider.setValues(lowerHandleFraction, higherHandleFraction);
    }-*/;
    
    public void setReRenderingWouldTakeSignificantTime(boolean wouldTakeSignificantTime) {
    	reRenderingWouldTakeSignificantTime = wouldTakeSignificantTime;
    }
    
	protected class FilterTimer extends Timer{
		
		private double lastLow = currentLow;
		private double lastHigh = currentHigh;
		
		@Override
		public void run() {
			if(isUserStillDragging() && reRenderingWouldTakeSignificantTime) {
				schedule(200);
				return;
			}
			if(!(Math.abs(lastLow - currentLow) < range * 10e-6 && Math.abs(lastHigh - currentHigh) < range * 10e-6)) {
				lastLow = currentLow;
				lastHigh = currentHigh;
				filterObserver.onNewFilter();
			}
		}
		
		public void setNewRange(double low, double high) {
			currentLow = low;
			currentHigh = high;
			ensureCorrectFilterButtonStyle();
			schedule(500);
		}
		
		private boolean isUserStillDragging() {
			Element sliderSelector = Document.get().getElementById("slider-" + sliderSelectorContainerId);
			
			Element sliderSelectorChild = sliderSelector.getFirstChildElement();
			do {
				if(isActiveHandle(sliderSelectorChild.getClassName())) {
					return true;
				}
				sliderSelectorChild = sliderSelectorChild.getNextSiblingElement();
			} while(sliderSelectorChild != null);
			
			return false;
		}
		
		private boolean isActiveHandle(String className) {
			return className.contains("ui-slider-handle") && className.contains("ui-state-active");
		}
	}
}
