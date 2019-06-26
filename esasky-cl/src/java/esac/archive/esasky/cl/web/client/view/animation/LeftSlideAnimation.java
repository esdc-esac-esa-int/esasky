package esac.archive.esasky.cl.web.client.view.animation;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;

public class LeftSlideAnimation extends EsaSkyAnimation {

    private final Element element;
    
    public LeftSlideAnimation(Element element)
    {
        this.element = element;
    }
 
    @Override
	protected Double getCurrentPosition() {
		String leftString = element.getStyle().getLeft();
		if (leftString.equals("")){
			leftString = "0px";
		}
		//remove suffix "px"
		leftString = leftString.substring(0, leftString.length()-2);
		Double currentPosition = new Double(leftString);
		return currentPosition;
	}
    
    @Override
	protected void setCurrentPosition(double newPosition){
      this.element.getStyle().setLeft(newPosition, Style.Unit.PX);
	}
}
