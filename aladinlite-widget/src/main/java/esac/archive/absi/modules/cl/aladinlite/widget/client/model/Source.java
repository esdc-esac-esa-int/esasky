package esac.archive.absi.modules.cl.aladinlite.widget.client.model;

public class Source extends AladinShape {

    protected Source() {
    }

    public final native String getRaDeg() /*-{
		if (this["ra"] != undefined) {
			return this["ra"].toString();
		}
		return null;
    }-*/;

    /**
     * Retrieve value for ["data"]["dec"] or null if it does not exist.
     * @return String value or null.
     */
    public final native String getDecDeg() /*-{
		if (this["dec"] != undefined) {
			return this["dec"].toString();
		}
		return null;
    }-*/;

    public final native String getCatalogueName() /*-{
		if (this["catalog"] != undefined) {
			return this["catalog"].toString();
		}
		return null;
    }-*/;

    public final native String isShowing() /*-{
		if (this["isShowing"] != undefined) {
			return this["isShowing"].toString();
		}
		return null;
    }-*/;

    public final native String isSelected() /*-{
		if (this["isSelected"] != undefined) {
			return this["isSelected"].toString();
		}
		return null;
    }-*/;

    public final native String getMarker() /*-{
		if (this["marker"] != undefined) {
			return this["marker"].toString();
		}
		return null;
    }-*/;


}
