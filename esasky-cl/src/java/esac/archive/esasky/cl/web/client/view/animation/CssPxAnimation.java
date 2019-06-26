package esac.archive.esasky.cl.web.client.view.animation;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;

public class CssPxAnimation extends EsaSkyAnimation {

    private final Element element;
    private final String property;
 
    public CssPxAnimation(Element element, String property){
        this.element = element;
        this.property = property;
    }
 
 
    @Override
	protected Double getCurrentPosition() {
		String propertyValue = element.getStyle().getProperty(property);
		if (propertyValue.equals("")){
			propertyValue = "0px";
		}
		//remove suffix "px"
		propertyValue = propertyValue.substring(0, propertyValue.length() - 2);
		Double currentPosition = new Double(propertyValue);
		return currentPosition;
	}
    
    @Override
	protected void setCurrentPosition(double newPosition){
        element.getStyle().setProperty(property, newPosition, Unit.PX);
	}
}
