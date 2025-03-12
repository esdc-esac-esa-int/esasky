package esac.archive.absi.modules.cl.aladinlite.widget.client.model;

import com.google.gwt.core.client.JavaScriptObject;

public class TapServicesMOCObject extends JavaScriptObject {

    protected TapServicesMOCObject() {
    }

    /**
     * Retrieve value for stc-s coordinates or null if it does not exist.
     * @return String value or null.
     */
    public final native String getAladinLitePolygon() /*-{
		if (this[0] != undefined) {
			return this[0].toString();
		}
		return null;
    }-*/;

    // /**
    // * Retrieve value for stc-s coordinates or null if it does not exist.
    // * @return String value or null.
    // */
    // public final native String getStcS() /*-{
    // if (this[0] != undefined) {
    // return this[1].toString();
    // }
    // return null;
    // }-*/;

}
