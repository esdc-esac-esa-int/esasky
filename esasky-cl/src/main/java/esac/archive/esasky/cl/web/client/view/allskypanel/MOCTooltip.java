package esac.archive.esasky.cl.web.client.view.allskypanel;

import java.util.LinkedList;


public class MOCTooltip extends Tooltip {

	LinkedList<MOCTooltipObserver> observers = new LinkedList<>();
	String text = "";
	
    public MOCTooltip(String text, int x, int y) {
    	super(x,y);
    	this.text = text;
    }
    
    public void registerObserver(MOCTooltipObserver observer) {
    	observers.add(observer);
    }

	@Override
	protected void fillContent(String cooFrame) {
        typeSpecificContent.setHTML(text);
	}
    
}