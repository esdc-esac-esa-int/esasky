package esac.archive.absi.modules.cl.aladinlite.widget.client.event;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.GwtEvent;


/**
 * Event to record select events sent by AladinLite.
 */
public class AladinLiteDeselectAreaEvent extends GwtEvent<AladinLiteDeselectAreaEventHandler> {

    public static Type<AladinLiteDeselectAreaEventHandler> TYPE = new Type<AladinLiteDeselectAreaEventHandler>();

    private JavaScriptObject objects;
    private JavaScriptObject area;

    public AladinLiteDeselectAreaEvent(JavaScriptObject objects, JavaScriptObject area) {
        this.objects = objects;
        this.area = area;
    }

    @Override
    public Type<AladinLiteDeselectAreaEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AladinLiteDeselectAreaEventHandler handler) {
        handler.onDeselectionAreaEvent(this);
    }

	public JavaScriptObject getObjects() {
		return objects;
	}

	public JavaScriptObject getArea() {
		return area;
	}

   

}
