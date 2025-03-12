package esac.archive.absi.modules.cl.aladinlite.widget.client.event;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.GwtEvent;


/**
 * Event to record select events sent by AladinLite.
 */
public class AladinLiteSelectAreaEvent extends GwtEvent<AladinLiteSelectAreaEventHandler> {

    public static Type<AladinLiteSelectAreaEventHandler> TYPE = new Type<AladinLiteSelectAreaEventHandler>();

    private JavaScriptObject objects;
    private JavaScriptObject area;

    public AladinLiteSelectAreaEvent(JavaScriptObject objects, JavaScriptObject area) {
        this.objects = objects;
        this.area = area;
    }

    @Override
    public Type<AladinLiteSelectAreaEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AladinLiteSelectAreaEventHandler handler) {
        handler.onSelectionAreaEvent(this);
    }

	public JavaScriptObject getObjects() {
		return objects;
	}

	public JavaScriptObject getArea() {
		return area;
	}

   

}
