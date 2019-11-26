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

public class LongFilterDialogBox extends FilterDialogBox {
    
    private final Resources resources = GWT.create(Resources.class);
    private CssResource style;
    
    private FilterObserver filterObserver;
    private JavaScriptObject slider;
    private boolean hasSliderBeenAddedToDialogBox = false;
    private boolean reRenderingWouldTakeSignificantTime;
    
    private long minValue;
    private long maxValue;
    private long currentLow = minValue;
    private long currentHigh = maxValue;
    private long range;
    
    private final int SLIDER_MAX = 10000;
    private double currentSliderFromFraction = 0;
    private double currentSliderToFraction = 1 * SLIDER_MAX;
    
    private TextBox fromTextBox = new TextBox();
    private TextBox toTextBox = new TextBox();
    
    private final String sliderSelectorContainerId;
    private final String intFilterContainerId;
    
    protected FilterTimer filterTimer = new FilterTimer();
	
    public interface Resources extends ClientBundle {
        @Source("doubleFilterDialogBox.css")
        @CssResource.NotStrict
        CssResource style();
        
		@Source("reset.png")
		ImageResource resetIcon();
    }
    
	public LongFilterDialogBox(String columnName, final String filterButtonId, final FilterObserver filterObserver) {
		super(filterButtonId);
        this.style = this.resources.style();
        this.style.ensureInjected();
        intFilterContainerId = filterButtonId.replaceAll("(\\(|\\)| )", "_") + "intColumn";
        sliderSelectorContainerId = "selectorId_WTIH_NO_TITLE_" + intFilterContainerId;
        this.filterObserver = filterObserver;
        
        HTML columnNameHTML = new HTML(columnName.replaceAll("_", " "));
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
		
		container.getElement().setId(intFilterContainerId);
		setWidget(container);
		
		addStyleName("doubleFilterDialogBox");
	}
	
	@Override
	public void show() {
		super.show();
		if(!hasSliderBeenAddedToDialogBox) {
			hasSliderBeenAddedToDialogBox = true;
			slider = createSliderFilter(this, intFilterContainerId, sliderSelectorContainerId, SLIDER_MAX);
			setTextBoxValues(minValue, maxValue);
			addElementNotAbleToInitiateMoveOperation("slider-" + sliderSelectorContainerId);
		}
	}

	@Override
	public boolean isFilterActive() {
		return hasSliderBeenAddedToDialogBox 
				&& (currentSliderFromFraction > 0 || currentSliderToFraction < SLIDER_MAX)
				&& 	!(Double.isInfinite(minValue) || Double.isInfinite(maxValue));
	}
	
    private native JavaScriptObject createSliderFilter(LongFilterDialogBox instance, String containerId, String sliderSelectorId, int multiple) /*-{
	    var sliderSelector = $wnd.createSliderSelector(sliderSelectorId,
	                                      "",
	                                      0,
	                                      multiple,
	                                      $entry(function (selector) {
	                                      	instance.@esac.archive.esasky.cl.web.client.view.resultspanel.tab.filter.IntegerFilterDialogBox::fireRangeChangedEvent(DD)(selector.fromValue, selector.toValue);
	                                      	}),
	                                      20,
	                                      1e-20);
		$wnd.$("#" + containerId).append(sliderSelector.$html);
		return sliderSelector;
    }-*/;

    private void fireRangeChangedEvent(double fromValue, double toValue) {
    	currentSliderFromFraction = fromValue;
    	currentSliderToFraction = toValue;
    	
    	long fromValueInt = minValue + (long) ( fromValue * range / SLIDER_MAX );
    	long toValueInt = minValue + (long) ( toValue * range / SLIDER_MAX );
    	setTextBoxValues(fromValueInt, toValueInt);
    	filterTimer.setNewRange(fromValueInt, toValueInt);
    }
    
    private void setTextBoxValues(long fromValue, long toValue) {
		if(Double.isNaN(fromValue) || Double.isNaN(toValue)) {
			return;
		}
    	fromTextBox.setText(Long.toString(fromValue));
    	toTextBox.setText(Long.toString(toValue));
    }
    
    public void setRange(long minValue, long maxValue){
    	range = Math.abs(maxValue - minValue);
    	
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
    		long fromValue = Long.parseLong(fromTextBox.getText());
    		long toValue = Long.parseLong(toTextBox.getText());
    		if(toValue < fromValue) {
    			long temp = fromValue;
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
    
    public long getCurrentLow() {
    	return currentLow;
    }
    
    public long getCurrentHigh() {
    	return currentHigh;
    }
    
	private void setSliderValues(long currentLow, long currentHigh) {
		currentSliderFromFraction = (currentLow - minValue) * ( new Double(SLIDER_MAX) / range);
		currentSliderToFraction = (currentHigh - minValue) * ( new Double(SLIDER_MAX) / range);
		setSliderFraction(slider, currentSliderFromFraction, currentSliderToFraction);
    }
    
    private native void setSliderFraction(JavaScriptObject slider, double lowerHandleFraction, double higherHandleFraction) /*-{
    	slider.setValues(lowerHandleFraction, higherHandleFraction);
    }-*/;
    
    public void setReRenderingWouldTakeSignificantTime(boolean wouldTakeSignificantTime) {
    	reRenderingWouldTakeSignificantTime = wouldTakeSignificantTime;
    }
    
	protected class FilterTimer extends Timer{
		
		private long lastLow = currentLow;
		private long lastHigh = currentHigh;
		
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
		
		public void setNewRange(long low, long high) {
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
