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

public class RangeFilterDialogBox extends FilterDialogBox {
    
    private final Resources resources = GWT.create(Resources.class);
    private CssResource style;
    
    private FilterObserver filterObserver;
    private JavaScriptObject slider;
    private boolean hasSliderBeenAddedToDialogBox = false;
    private boolean reRenderingWouldTakeSignificantTime;
    
    private double minValue;
    private double maxValue;
    private double currentLow = minValue;
    private double currentHigh = maxValue;
    private double range;
    private double precision;
    private double stepSize;
    
    private final int SLIDER_MAX = 10000;
    private double currentSliderFromFraction = 0;
    private double currentSliderToFraction = 1 * SLIDER_MAX;
    
    private TextBox fromTextBox = new TextBox();
    private TextBox toTextBox = new TextBox();
    
    private final String sliderSelectorContainerId;
    private final String rangeFilterContainerId;
    
    protected FilterTimer filterTimer = new FilterTimer();
	private ValueFormatter valueFormatter;
    
    public interface Resources extends ClientBundle {
        @Source("rangeFilterDialogBox.css")
        @CssResource.NotStrict
        CssResource style();
        
		@Source("reset.png")
		ImageResource resetIcon();
    }
    
	public RangeFilterDialogBox(String tapName, String columnName, ValueFormatter valueFormatter,
	        final String filterButtonId, final FilterObserver filterObserver) {
		super(tapName, filterButtonId);
        this.style = this.resources.style();
        this.style.ensureInjected();
        this.valueFormatter = valueFormatter;
        rangeFilterContainerId = filterButtonId.replaceAll("(\\(|\\)| )", "_") + "rangeColumn";
        sliderSelectorContainerId = "selectorId_WTIH_NO_TITLE_" + rangeFilterContainerId;
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
		resetButton.setTitle(TextMgr.getInstance().getText("rangeFilter_resetFilter"));
		resetButton.addStyleName("resetRangeFilterButton");
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
		
		container.getElement().setId(rangeFilterContainerId);
		setWidget(container);
		
		addStyleName("rangeFilterDialogBox");
		
	}
	
	@Override
	public void show() {
		super.show();
		if(!hasSliderBeenAddedToDialogBox) {
			hasSliderBeenAddedToDialogBox = true;
			stepSize = 1;
			slider = createSliderFilter(this, rangeFilterContainerId, sliderSelectorContainerId, 0, SLIDER_MAX, stepSize);
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
	
    private native JavaScriptObject createSliderFilter(RangeFilterDialogBox instance, String containerId, String sliderSelectorId, int minValue, int maxValue, double fixedStep) /*-{
	    var sliderSelector = $wnd.createSliderSelector(sliderSelectorId,
	                                      "",
	                                      minValue,
	                                      maxValue,
	                                      $entry(function (selector) {
	                                      	instance.@esac.archive.esasky.cl.web.client.view.resultspanel.tab.filter.RangeFilterDialogBox::fireRangeChangedEvent(DD)(selector.fromValue, selector.toValue);
	                                      	}),
	                                      20,
	                                      fixedStep);
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
    	fromTextBox.setText(valueFormatter.formatValue(fromValue));
    	toTextBox.setText(valueFormatter.formatValue(toValue));
    }
    
    public void changeFormatter(ValueFormatter formatter) {
        this.valueFormatter = formatter;
    }
    
    public void setRange(Double minValue, Double maxValue, int precision){
    	range = Math.abs(maxValue - minValue);
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
    		double fromValue = valueFormatter.getValueFromFormat(fromTextBox.getText());
    		double toValue = valueFormatter.getValueFromFormat(toTextBox.getText());
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
				if(isFilterActive()) {
					String filter = "";
					if(currentSliderFromFraction > 0) {
						filter += Double.toString(currentLow);
					}
					filter += ",";
					if((SLIDER_MAX - currentSliderToFraction) > stepSize) {
						filter += Double.toString(currentHigh);
					}
					
					filterObserver.onNewFilter(filter);
				}else {
					filterObserver.onNewFilter("");
				}
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

	@Override
	public String getAdqlForFilterCondition() {
		return "";
	}
}
