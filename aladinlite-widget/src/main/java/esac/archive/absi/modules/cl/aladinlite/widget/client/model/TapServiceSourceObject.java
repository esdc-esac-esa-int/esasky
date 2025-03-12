package esac.archive.absi.modules.cl.aladinlite.widget.client.model;

import com.google.gwt.core.client.JavaScriptObject;

/** Convenience class to store objects returned by TAP server. */
public class TapServiceSourceObject extends JavaScriptObject {

    protected TapServiceSourceObject() {
    }

    /**
     * Retrieve value for ra or null if it does not exist.
     * @return String value or null.
     */
    public final native String getRa() /*-{
		if (this[0] != undefined) {
			return this[0].toString();
		}
		return null;
    }-*/;

    /**
     * Retrieve value for dec or null if it does not exist.
     * @return String value or null.
     */
    public final native String getDec() /*-{
		if (this[1] != undefined) {
			return this[1].toString();
		}
		return null;
    }-*/;

    /**
     * Retrieve value for iau name or null if it does not exist.
     * @return String value or null.
     */
    public final native String getIauName() /*-{
		if (this[2] != undefined) {
			return this[2].toString();
		}
		return null;
    }-*/;

    /**
     * Retrieve value for total flux or null if it does not exist.
     * @return String value or null.
     */
    public final native String getTotalFlux() /*-{
		if (this[3] != undefined) {
			return this[3].toString();
		}
		return null;
    }-*/;

    /**
     * Retrieve value for postcard url or null if it does not exist.
     * @return String value or null.
     */
    public final native String getPostcardUrl() /*-{
		if (this[4] != undefined) {
			return this[4].toString();
		}
		return null;
    }-*/;

    /**
     * Print object contents.
     * @return Object contents
     */
    public final String toFormattedString() {
        String output = new String();
        output = "[Source :";
        output = output + "Ra [" + getRa() + "], ";
        output = output + "Dec [" + getDec() + "], ";
        output = output + "Iau Name [" + getIauName() + "], ";
        output = output + "Total Flux [" + getTotalFlux() + "], ";
        output = output + "Postcard URL [" + getPostcardUrl() + "], ";
        output = output + "]";
        return output;
    }
}
