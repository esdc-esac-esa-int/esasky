package esac.archive.esasky.cl.web.client.view.common;


import java.util.LinkedList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
    
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;


public class ESASkyMultiRangeSlider extends FlowPanel {

    private Resources resources;
    private final CssResource style;
    private final String CSS_SLIDER_CONTAINER_ID= "esaSkySliderContainer";
    private final String CSS_SLIDER_ID = "esaSkySlider";
    private final int SLIDERMAX = 10;
    
    private static int ID;
    private String sliderID = "esaskySlider";
    private String sliderContainerID = "esaskySliderContainer";
    private double minValue;
    private double maxValue;
    private double currentValue1;
    private double currentValue2;
    private long lastSentGoogleAnalyticsTime = 0;
    private GeneralJavaScriptObject sliderSelector;

    public static interface Resources extends ClientBundle {

        @Source("esaSkySlider.css")
        @CssResource.NotStrict
        CssResource style();
    }

    public ESASkyMultiRangeSlider(double min, double max, int width) {
    	ID++;
    	sliderID += ID;
    	sliderContainerID += ID;
    	
        this.resources = GWT.create(Resources.class);
        this.style = this.resources.style();
        this.style.ensureInjected();
        this.addStyleName(CSS_SLIDER_CONTAINER_ID);

    	this.minValue = min;
    	this.maxValue = max;
    	this.currentValue1 = min;
    	this.currentValue2 = max;
    	initView(width);
    }

    private void initView(int width) {
    	this.getElement().setId(sliderContainerID);
    }
    
    public native void initSlider(Element element, String sliderID,int minValue,int maxValue) /*-{
    	$wnd.jQuery(element).slider({
	        range: true,
	        min: minValue,
	        max: maxValue,
	        values: [minValue, maxValue],
	        slide: function (event, ui) {
	            var value1 = ui.values[0];
	            var value2 = ui.values[1];
	            $wnd.jQuery(element).find(".ui-slider-handle:first").text(value1);
	            $wnd.jQuery(element).find(".ui-slider-handle:last").text(value2);
	            if(element.onValueChange != null){
	            	element.onValueChange(value1, value2);
	            }
	        }
    	});
    }-*/;
    
	public void firstOpening() {
		sliderSelector = createSliderFilter(this, sliderContainerID, sliderID, minValue, maxValue);
	}
	
	public void updateSize(int width) {
		this.setPixelSize(width, this.getOffsetHeight());
	}
    
    private native GeneralJavaScriptObject createSliderFilter(ESASkyMultiRangeSlider instance, String containerId, String sliderSelectorId, double min, double max) /*-{
    var sliderSelector = $wnd.createSliderSelectorWithoutBoxes(sliderSelectorId,
                                      "",
                                      min,
                                      max,
                                      $entry(function (selector) {
                                      	instance.@esac.archive.esasky.cl.web.client.view.common.ESASkyMultiRangeSlider::fireSliderChangedEvent(DD)(selector.fromValue, selector.toValue);
                                      	}),
                                      20,
                                      .01);
		$wnd.$("#" + containerId).append(sliderSelector.$html);
		return sliderSelector;
	}-*/;
    
	private native void addSliderListener(ESASkyMultiRangeSlider instance, Element slider) /*-{
		slider.onValueChange = function(value1, value2) {
			instance.@esac.archive.esasky.cl.web.client.view.common.ESASkyMultiRangeSlider::fireSliderChangedEvent(DD)(value1, value2);
		}
	}-*/;
	
	public void setSliderValue(double value1, double value2) {
		currentValue1 = value1;
		currentValue2 = value2;
		setSliderValueJs(value1, value2, sliderSelector);
		notifyObservers();
	}
	
	
	
	private native void setSliderValueJs(double value1, double value2, JavaScriptObject sliderSelector) /*-{
		sliderSelector.setValues(value1,value2);
	}-*/;
	
	private void fireSliderChangedEvent(double value1, double value2) {
		currentValue1 = value1;
		currentValue2 = value2;
		
		if(System.currentTimeMillis() - lastSentGoogleAnalyticsTime > 1000) {
			GoogleAnalytics.sendEventWithURL(GoogleAnalytics.CAT_SLIDER, GoogleAnalytics.ACT_SLIDER_MOVED,
					"val1: " + Double.toString(value1) + ", val2: " + Double.toString(value2));
			lastSentGoogleAnalyticsTime = System.currentTimeMillis();
		}
		
		notifyObservers();
	}
    
    private LinkedList<EsaSkyMultiRangeSliderObserver> observers = new LinkedList<EsaSkyMultiRangeSliderObserver>();
    
    public void registerValueChangeObserver(EsaSkyMultiRangeSliderObserver observer) {
 	   observers.add(observer);
    }
    
    private void notifyObservers() {
 	   for(EsaSkyMultiRangeSliderObserver observer : observers) {
 		   observer.onValueChange(currentValue1, currentValue2);
 	   }
    }
    
    public void resetSlider() {
    	currentValue1 = minValue;
    	currentValue2 = maxValue;
    	setSliderValue(minValue, maxValue);
    	notifyObservers();
    }

	public double getMinValue() {
		return minValue;
	}

	public void setMinValue(double minValue) {
		this.minValue = minValue;
	}

	public double getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}
	
	public Element getSliderUiHeader() {
		Element el = getElement().getFirstChildElement();
		int i = 0;
		while(!el.hasClassName("ui-widget-header")) {
			el = el.getFirstChildElement();
			i++;
			if(i > 5) {
				break;
			}
		}
		
		if(i < 6) {
			return el;
		}
		return null;
	}
	
	public void setSliderColor(String color) {
		Element header = getSliderUiHeader();
		if(header != null) {
			setSliderColorJs(color, header);
		}
	};
	
	public native void setSliderColorJs(String color, JavaScriptObject sliderUiHeader) /*-{
		sliderUiHeader.style.background = color;
	}-*/;

}
