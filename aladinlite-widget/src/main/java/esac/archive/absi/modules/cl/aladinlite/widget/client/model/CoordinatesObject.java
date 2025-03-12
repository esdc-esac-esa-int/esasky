package esac.archive.absi.modules.cl.aladinlite.widget.client.model;

import com.google.gwt.core.client.JavaScriptObject;

/** Convenience class to store objects returned by getRaDec method in AladinLite. */
public class CoordinatesObject extends JavaScriptObject {

    protected CoordinatesObject() {
    }

    /**
     * Retrieve value for ra or null if it does not exist.
     * @return double value or null.
     */
    public final native double getRaDeg() /*-{
		if (this.ra != undefined) {
			return this.ra;
		}
		return null;
    }-*/;

    /**
     * Retrieve value for dec or null if it does not exist.
     * @return double value or null.
     */
    public final native double getDecDeg() /*-{
		if (this.dec != undefined) {
			return this.dec;
		}
		return null;
    }-*/;

    /**
     * Retrieve value for mouse X or null if it does not exist.
     * @return double value or null.
     */
    public final native double getMouseX() /*-{
		if (this.mousex != undefined) {
			return this.mousex;
		}
		return null;
    }-*/;

    /**
     * Retrieve value for mouse Y or null if it does not exist.
     * @return double value or null.
     */
    public final native double getMouseY() /*-{
		if (this.mousey != undefined) {
			return this.mousey;
		}
		return null;
    }-*/;

}
