package esac.archive.esasky.cl.web.client.view.animation;

import com.google.gwt.dom.client.Element;

public class RotateAnimation extends EsaSkyAnimation {

    private final Element element;
    
    public RotateAnimation(Element element)
    {
        this.element = element;
    }
 
    @Override
	protected Double getCurrentPosition() {
		String transformString = element.getStyle().getProperty("transform");
		if (transformString.equals("")){
			transformString = "rotate(0deg)";
		}
		
		//remove prefix "rotate(" and suffix ")deg"
		transformString = transformString.substring(7, transformString.length()-4);
		Double currentPosition = new Double(transformString);
		return currentPosition;
	}
    
    @Override
	protected void setCurrentPosition(double newPosition){
        	String rotateProperty = "rotate(" + newPosition + "deg)";
        	this.element.getStyle().setProperty("transform", rotateProperty);
	}
}
