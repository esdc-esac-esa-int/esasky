package esac.archive.absi.modules.cl.aladinlite.widget.client.model;

import com.google.gwt.core.client.JavaScriptObject;

/** Convenience class to store objects returned by TAP server. */
public class TapServiceObservationObject extends JavaScriptObject {

    // protected enum ObservationType {
    // IMAGING("imaging"), SPECTRA("spectra");
    //
    // String type;
    //
    // ObservationType(String type) {
    // this.type = type;
    // }
    //
    // public String getTypeValue() {
    // return this.type;
    // }
    //
    // }

    protected TapServiceObservationObject() {
    }

    /**
     * Retrieve value for observation id or null if it does not exist.
     * @return String value or null.
     */
    public final native String getObservationId() /*-{
		if (this[0] != undefined) {
			return this[0].toString();
		}
		return null;
    }-*/;

    /**
     * Retrieve value for instrument or null if it does not exist.
     * @return String value or null.
     */
    public final native String getInstrument() /*-{
		if (this[1] != undefined) {
			return this[1].toString();
		}
		return null;
    }-*/;

    /**
     * Retrieve value for ra or null if it does not exist.
     * @return String value or null.
     */
    public final native String getRa() /*-{
		if (this[2] != undefined) {
			return this[2].toString();
		}
		return null;
    }-*/;

    /**
     * Retrieve value for dec or null if it does not exist.
     * @return String value or null.
     */
    public final native String getDec() /*-{
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
     * Retrieve value for product url or null if it does not exist.
     * @return String value or null.
     */
    public final native String getProductUrl() /*-{
		if (this[5] != undefined) {
			return this[5].toString();
		}
		return null;
    }-*/;

    /**
     * Retrieve value for stc-s coordinates or null if it does not exist.
     * @return String value or null.
     */
    public final native String getStcS() /*-{
		if (this[6] != undefined) {
			return this[6].toString();
		}
		return null;
    }-*/;

    /**
     * Retrieve value for typeor null if it does not exist.
     * @return String value or null.
     */
    public final native String getType() /*-{
		if (this[7] != undefined) {
			return this[7].toString();
		}
		return null;
    }-*/;

    /**
     * Retrieve value for date or null if it does not exist.
     * @return String value or null.
     */
    public final native String getDate() /*-{
		if (this[8] != undefined) {
			return this[8].toString();
		}
		return null;
    }-*/;

    /**
     * Retrieve value for filter or null if it does not exist.
     * @return String value or null.
     */
    public final native String getFilter() /*-{
		if (this[9] != undefined) {
			return this[9].toString();
		}
		return null;
    }-*/;

    /**
     * Retrieve value for metadataURL for retrieving all metadata about the current observation
     * directly from the archive or null if it does not exist.
     * @return String value or null.
     */
    public final native String getArchiveMetadataURL() /*-{
		if (this[10] != undefined) {
			return this[10].toString();
		}
		return null;
    }-*/;

    /**
     * Print object contents.
     * @return Object contents
     */
    public final String toFormattedString() {
        String output = new String();
        output = "[ObservationId [" + getObservationId() + "], ";
        output = output + "Instrument [" + getInstrument() + "], ";
        output = output + "Ra [" + getRa() + "], ";
        output = output + "Dec [" + getDec() + "], ";
        output = output + "Postcard URL [" + getPostcardUrl() + "], ";
        output = output + "Product URL [" + getProductUrl() + "], ";
        output = output + "Stc-S [" + getStcS() + "], ";
        output = output + "]";
        return output;
    }
}
