package esac.archive.absi.modules.cl.aladinlite.widget.client.event;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.GwtEvent;

public class AladinLiteMOCIpixClickedEvent extends
        GwtEvent<AladinLiteMOCIpixClickedEventHandler> {

    public static Type<AladinLiteMOCIpixClickedEventHandler> TYPE = new Type<AladinLiteMOCIpixClickedEventHandler>();

    private JavaScriptObject object;
    int x;
    int y;

    public AladinLiteMOCIpixClickedEvent(JavaScriptObject object, int x, int y) {
    	this.object = object;
    	this.x = x;
    	this.y  = y;
    }

    @Override
    public Type<AladinLiteMOCIpixClickedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AladinLiteMOCIpixClickedEventHandler handler) {
        handler.onMOCClicked(this);
    }

	public JavaScriptObject getObject() {
		return object;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

}
