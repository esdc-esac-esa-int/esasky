package esac.archive.esasky.cl.web.client.view.animation;

import com.google.gwt.dom.client.Element;

public class OpacityAnimation extends EsaSkyAnimation {

    private final Element element;
    
    public OpacityAnimation(Element element)
    {
        this.element = element;
    }
 
    @Override
	protected Double getCurrentPosition() {
		String opacityString = element.getStyle().getOpacity();
		if (opacityString.equals("")){
			opacityString = "1";
		}
		Double currentPosition = new Double(opacityString);
		return currentPosition;
	}
    
    @Override
	protected void setCurrentPosition(double newPosition){
    	    element.getStyle().setOpacity(newPosition);
	}
}
