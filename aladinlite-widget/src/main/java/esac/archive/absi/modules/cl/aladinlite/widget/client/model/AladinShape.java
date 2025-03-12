package esac.archive.absi.modules.cl.aladinlite.widget.client.model;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Class to use in gwt the aladin lite objects returned from javascript methods.
 *
 * @author ileon
 */
public class AladinShape extends JavaScriptObject {

    protected AladinShape() {
    }

    public final native String getKeys() /*-{
		if (this["data"] != undefined) {
			if (this["data"]["keys"] != undefined) {
				return this["data"]["keys"].toString();
			}
		}
		return null;
    }-*/;

    /**
     * Retrieve value for ["data"]["message"] or null if it does not exist.
     * @return String value or null.
     */
    public final native String getMessage() /*-{
		if (this["data"] != undefined) {
			if (this["data"]["message"] != undefined) {
				return this["data"]["message"].toString();
			}
		}
		return null;
    }-*/;

    /**
     * Retrieve value for ["data"]["id"] or null if it does not exist.
     * @return String value or null.
     */
    public final native String getId() /*-{
		if (this["data"] != undefined) {
			if (this["data"]["id"] != undefined) {
				return this["data"]["id"].toString();
			}
		} else if(this["id"] != undefined) {
		    return this["id"];
	    }
		return null;
    }-*/;

    /**
     * Retrieve value for ["data"]["catalogue"] or null if it does not exist.
     * @return String value or null.
     */
    public final native String getSourceCatalogue() /*-{
		if (this["data"] != undefined) {
			if (this["data"]["catalogue"] != undefined) {
				return this["data"]["catalogue"].toString();
			}
		}
		return null;
    }-*/;

    /**
     * Retrieve value for ["data"]["sourcename"] or null if it does not exist.
     * @return String value or null.
     */
    public final native String getSourceName() /*-{
		if (this["data"] != undefined) {
			if (this["data"]["sourcename"] != undefined) {
				return this["data"]["sourcename"].toString();
			} else if (this["data"]["name"] != undefined) {
				return this["data"]["name"].toString();
			}
		}
		return null;
    }-*/;

    /**
     * Retrieve value for ["ra"] or null if it does not exist.
     * @return String value or null.
     */
    public final native String getRa() /*-{
		if (this["ra"] != undefined) {
			return this["ra"].toString();
		}
		return null;
    }-*/;

    /**
     * Retrieve value for ["dec"] or null if it does not exist.
     * @return String value or null.
     */
    public final native String getDec() /*-{
		if (this["dec"] != undefined) {
			return this["dec"].toString();
		}
		return null;
    }-*/;

    /**
     * Retrieve value for ["catalog"] or null if it does not exist.
     * @return String value or null.
     */
    public final native String getCatalog() /*-{
		if (this["catalog"] != undefined) {
			return this["catalog"].toString();
		}
		return null;
    }-*/;

    /**
     * Retrieve value for ["isShowing"] or null if it does not exist.
     * @return String value or null.
     */
    public final native String getIsShowing() /*-{
		if (this["isShowing"] != undefined) {
			return this["isShowing"].toString();
		}
		return null;
    }-*/;

    /**
     * Retrieve value for ["isSelected"] or null if it does not exist.
     * @return String value or null.
     */
    public final native String getIsSelected() /*-{
		if (this["isSelected"] != undefined) {
			return this["isSelected"].toString();
		}
		return null;
    }-*/;

    /**
     * Retrieve the type of catalog or null if it does not exist. It is used by ESASky to
     * distinguish between Multitarget and Catalogue source
     * @return String value or null.
     */
    public final native String getSourceType() /*-{
		if (this["data"] != undefined) {
			if (this["data"]["type"] != undefined) {
				return this["data"]["type"].toString();
			}
		}
		return null;
    }-*/;

    public final native String getDataDetailsByKey(String key) /*-{
		if (this["data"] != undefined) {
			if (this["data"][key] != undefined) {
				return this["data"][key].toString();
			}
		}
		return null;
    }-*/;

    /**
     * Print object contents.
     * @return Object contents
     */
    public final String toFormattedString() {
        String output = new String();
        output = "[Ra [" + getRa() + "], ";
        output = output + "Dec [" + getDec() + "], ";
        output = output + "Catalog [" + getCatalog() + "], ";
        output = output + "IsShowing? [" + getIsShowing() + "], ";
        output = output + "IsSelected? [" + getIsSelected() + "], ";
        output = output + "DATA -> [Message [" + getMessage() + "], Idx [" + getId() + "]]";
        return output;
    }
}
