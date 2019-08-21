package esac.archive.esasky.cl.web.client.view.common;


import java.util.LinkedList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;



public class ESASkySlider extends ScrollPanel {

    private Resources resources;
    private final CssResource style;
    private final String CSS_SLIDER_CONTAINER_ID= "sliderScrollPanel";
    private final String CSS_SLIDER_ID = "sliderSimplePanel";

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
    	initView(width);
    }

    private void initView(int width) {
    	SimplePanel sp = new SimplePanel();
	    sp.setWidth(10*width + "px");
	    sp.setHeight("1px");
	    sp.addStyleName(CSS_SLIDER_ID);
	 
	    this.setWidth(width + "px");
	    this.setWidget(sp);
	    
	    this.addScrollHandler(new ScrollHandler() {
	    	
			@Override
			public void onScroll(ScrollEvent event) {
				double maxVal = event.getRelativeElement().getScrollWidth() - event.getRelativeElement().getClientWidth();
				double scrollPercentage = ((double) event.getRelativeElement().getScrollLeft()) / maxVal;
				if(scrollPercentage < 0.01 || scrollPercentage > 0.99) {
					scrollPercentage = Math.round(scrollPercentage);
				}
				changeValueFomPercentage(scrollPercentage);
				notifyObservers();
			}
		});
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
    
    private void changeValueFomPercentage(double scrollPercentage) {
		double value = (maxValue - minValue)*scrollPercentage;
		setValue(value);
    }

    public void setValue(double value) {
    	currentValue = value;
    	double minPos = this.getMinimumHorizontalScrollPosition();
    	double maxPos = this.getMaximumHorizontalScrollPosition();
    	double newPos = (maxPos - minPos) * value / (maxValue - minValue);
    	this.setHorizontalScrollPosition((int)Math.round(newPos));
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
