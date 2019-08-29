package esac.archive.esasky.cl.web.client.view.common;


import java.util.LinkedList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;


public class ESASkySlider extends FlowPanel {

    private Resources resources;
    private final CssResource style;
    private final String CSS_SLIDER_CONTAINER_ID= "esaSkySliderContainer";
    private final String CSS_SLIDER_ID = "esaSkySlider";
    private final int SLIDERMAX = 1000;
    
    private HTML slider;
    private double minValue;
    private double maxValue;
    private double currentValue;

    /**
     * A ClientBundle that provides images for this widget.
     */
    public static interface Resources extends ClientBundle {

        @Source("esaSkySlider.css")
        @CssResource.NotStrict
        CssResource style();
    }

    public ESASkySlider(double min, double max, int width) {
        this.resources = GWT.create(Resources.class);
        this.style = this.resources.style();
        this.style.ensureInjected();
        this.addStyleName(CSS_SLIDER_CONTAINER_ID);

    	this.minValue = min;
    	this.maxValue = max;
    	this.currentValue = max;
    	initView(width);
    }

    private void initView(int width) {
    	String maxVal = Integer.toString(SLIDERMAX);
    	this.slider = new HTML(
				"<input type=\"range\" min=\"0\" max=\"" + maxVal + "\" "
						+ "value=\"" + maxVal + "\" class=\"" + CSS_SLIDER_ID
						+ " \" >");
    	
    	this.add(slider);
    	addSliderListener(this, slider.getElement());
    }
    
	private native void addSliderListener(ESASkySlider instance, Element slider) /*-{
		slider.oninput = function() {
			instance.@esac.archive.esasky.cl.web.client.view.common.ESASkySlider::fireSliderChangedEvent(D)(this.children[0].value);
		}
	}-*/;
	
	private void setSliderValue(double value) {
		setSliderValue(value, slider.getElement());
	}
	
	private native void setSliderValue(double value, Element slider) /*-{
		slider.children[0].value = value;
	}-*/;
	
	private void fireSliderChangedEvent(double newValue) {
		double scrollPercentage = ((double) newValue) / SLIDERMAX;
		if(scrollPercentage < 0.001 || scrollPercentage > 0.999) {
			scrollPercentage = Math.round(scrollPercentage);
		}
		changeValueFromFraction(scrollPercentage);
	}
    
    private LinkedList<EsaSkySliderObserver> observers = new LinkedList<EsaSkySliderObserver>();
    
    public void registerValueChangeObserver(EsaSkySliderObserver observer) {
 	   observers.add(observer);
    }
    
    private void notifyObservers() {
 	   for(EsaSkySliderObserver observer : observers) {
 		   observer.onValueChange(currentValue);
 	   }
    }
    
    private void changeValueFromFraction(double scrollFraction) {
		double value = (maxValue - minValue) * scrollFraction + minValue;
		setValue(value);
    }

    public void setValue(double value) {
    	if(value > maxValue) {
    		currentValue = maxValue;
		}else if(value < minValue) {
			currentValue = minValue;
		}else {
			currentValue = value;
		}
    	double newPos = currentValue / (maxValue - minValue) * SLIDERMAX;
    	setSliderValue(newPos);
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

	public double getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(double currentValue) {
		this.currentValue = currentValue;
	}

}
