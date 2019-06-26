package esac.archive.esasky.cl.web.client.view.common.buttons;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public class CloseButton extends ChangeableIconButton{

    private static Resources resources = GWT.create(Resources.class);
	
    public static interface Resources extends ClientBundle {

        @Source("close-light.png")
        ImageResource closeIconLight();
        
        @Source("close-dark.png")
        ImageResource closeIconDark();
    }
	
	public CloseButton(){
		super(resources.closeIconLight(), resources.closeIconDark());
	}
}
