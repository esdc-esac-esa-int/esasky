package esac.archive.esasky.cl.web.client.view.common;


import java.util.LinkedList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;


public class ESASkyMultiRangeSlider extends FlowPanel {

    private Resources resources;
    private final CssResource style;
    private final String CSS_SLIDER_CONTAINER_ID= "esaSkySliderContainer";
    private final String CSS_SLIDER_ID = "esaSkySlider";
    private final int SLIDERMAX = 10;
    
    private static int ID;
    private String sliderID = "esaskySlider";
    private String sliderContainerID = "esaskySliderContainer";
    private HTML slider;
    private double minValue;
    private double maxValue;
    private double currentValue1;
    private double currentValue2;
    private long lastSentGoogleAnalyticsTime = 0;

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
//    	JavaScriptObject slider = createSliderFilter(this, sliderContainerID, sliderID, SLIDERMAX);

//    	this.slider = new HTML("<div id=\"" + sliderID +"\"></div>");
    	//initSlider(slider.getElement(), sliderID, 0, SLIDERMAX);
//    	this.add(slider);
//    	addSliderListener(this, slider.getElement());
    }
    
    //Jquery slider
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
		createSliderFilter(this, sliderContainerID, sliderID, minValue, maxValue);
	}
	
	public void updateSize(int width) {
		this.setPixelSize(width, this.getOffsetHeight());
	}
    
    private native JavaScriptObject createSliderFilter(ESASkyMultiRangeSlider instance, String containerId, String sliderSelectorId, double min, double max) /*-{
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
	
	private void setSliderValue(double value1, double value2) {
		setSliderValue(value1, value2, slider.getElement());
	}
	
	public void addClassName(String className) {
		slider.getElement().addClassName(className);
	}
	
	private native void setSliderValue(double value1, double value2, Element slider) /*-{
		slider.values[0] = value1;
		slider.values[1] = value2;
	}-*/;
	
	private void fireSliderChangedEvent(double value1, double value2) {
		currentValue1 = value1;
		currentValue2 = value2;
		
		if(System.currentTimeMillis() - lastSentGoogleAnalyticsTime > 1000) {
			GoogleAnalytics.sendEventWithURL(GoogleAnalytics.CAT_Slider, GoogleAnalytics.ACT_Slider_Moved,
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

}
