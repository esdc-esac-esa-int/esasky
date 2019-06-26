package esac.archive.esasky.cl.web.client.view.animation;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;

public class HeightAnimation extends EsaSkyAnimation {

    private final Element element;
    
    public HeightAnimation(Element element)
    {
        this.element = element;
    }
 
    @Override
	protected Double getCurrentPosition() {
		String heightString = element.getStyle().getHeight();
		if (heightString.equals("")){
			heightString = "0px";
		}
		//remove suffix "px"
		heightString = heightString.substring(0, heightString.length()-2);
		Double currentPosition = new Double(heightString);
		return currentPosition;
	}
    
    @Override
	protected void setCurrentPosition(double newPosition){
        element.getStyle().setHeight(newPosition, Unit.PX);
	}
}
