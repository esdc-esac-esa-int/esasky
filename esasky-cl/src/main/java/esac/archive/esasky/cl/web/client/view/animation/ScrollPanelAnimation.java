package esac.archive.esasky.cl.web.client.view.animation;

import com.google.gwt.user.client.ui.ScrollPanel;

public class ScrollPanelAnimation extends EsaSkyAnimation {

    private final ScrollPanel scrollPanel;
    
    public ScrollPanelAnimation(ScrollPanel scrollPanel)
    {
        this.scrollPanel = scrollPanel;
    }
 
    @Override
	protected Double getCurrentPosition() {
        return (double) scrollPanel.getVerticalScrollPosition();
	}
    
    @Override
	protected void setCurrentPosition(double newPosition){
        scrollPanel.setVerticalScrollPosition((int)newPosition);
	}
}
