package esac.archive.absi.modules.cl.aladinlite.widget.client.model;

import com.google.gwt.core.client.JavaScriptObject;

public class SearchArea extends JavaScriptObject {

    protected SearchArea() {
    }

    public final native CoordinatesObject[] getCoordinates() /*-{
        if (this["points"] != undefined) {
            return this["points"];
        } else {
            return null;
        }
    }-*/;

    public final native CoordinatesObject[] getJ2000Coordinates() /*-{
        if (this["points_j2000"] != undefined) {
            return this["points_j2000"];
        } else if (this["points"] != undefined){
            return this["points"];
        } else {
            return null;
        }
    }-*/;

    public final native String getRadius() /*-{
        if (this["radius"] != undefined) {
            return this["radius"].toString();
        } else {
            return null;
        }
    }-*/;

    public final native String getAreaType() /*-{
        if (this["type"] != undefined) {
            return this["type"].toString();
        } else {
            return null;
        }
    }-*/;

    public final native boolean isCircle() /*-{
        if (this["type"] != undefined) {
            return this["type"].toString().toLowerCase() === "circle";
        } else {
            return false;
        }
    }-*/;
}
