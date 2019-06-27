package esac.archive.esasky.cl.web.client.view.animation;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;

public class MarginLeftSlideAnimation extends EsaSkyAnimation {

    private final Element element;
    
    public MarginLeftSlideAnimation(Element element)
    {
        this.element = element;
    }
 
    @Override
	protected Double getCurrentPosition() {
		String marginLeftString = element.getStyle().getMarginLeft();
		if (marginLeftString.equals("")){
			marginLeftString = "0px";
		}
		//remove suffix "px"
		marginLeftString = marginLeftString.substring(0, marginLeftString.length()-2);
		Double currentPosition = new Double(marginLeftString);
		return currentPosition;
	}
    
    @Override
	protected void setCurrentPosition(double newPosition){
        this.element.getStyle().setMarginLeft(newPosition, Style.Unit.PX);
	}
}
