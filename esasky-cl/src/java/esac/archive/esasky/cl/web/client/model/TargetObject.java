package esac.archive.esasky.cl.web.client.model;

import com.google.gwt.core.client.JavaScriptObject;

/** Convenience class to store objects returned by TAP server. */
public class TargetObject extends JavaScriptObject {

    protected TargetObject() {
    }

    public final double getFoVDeg() {
        double fovDeg = 1.; // default value
        String majorAxisArcmin = getMajorAxisArcmin();
        if (majorAxisArcmin != null && majorAxisArcmin.trim().length() > 0) {
            fovDeg = Double.parseDouble(majorAxisArcmin) / 60.;
        }
        return fovDeg;
    }

    /**
     * Retrieve value for target name id or null if it does not exist.
     * @return String value or null.
     */
    public final native String getName() /*-{
		if (this["name"] != undefined) {
			return this["name"].toString();
		}
		return null;
    }-*/;

    /**
     * Retrieve value for ra in degrees or null if it does not exist.
     * @return String value or null.
     */
    public final native String getRaDeg() /*-{
		if (this["raDeg"] != undefined) {
			return this["raDeg"].toString();
		}
		return null;
    }-*/;

    /**
     * Retrieve value for dec in degrees or null if it does not exist.
     * @return String value or null.
     */
    public final native String getDecDeg() /*-{
		if (this["decDeg"] != undefined) {
			return this["decDeg"].toString();
		}
		return null;
    }-*/;

    /**
     * Retrieve value for major axis in arcmin or null if it does not exist.
     * @return String value or null.
     */
    public final native String getMajorAxisArcmin() /*-{
		if (this["galdimMajAxis"] != undefined) {
			return this["galdimMajAxis"].toString();
		}
		return null;
    }-*/;

    /**
     * Retrieve value for minor axis in arcmin or null if it does not exist.
     * @return String value or null.
     */
    public final native String getMinorAxisArcmin() /*-{
		if (this["galdimMinAxis"] != undefined) {
			return this["galdimMinAxis"].toString();
		}
		return null;
    }-*/;

    /**
     * Retrieve value for position angle in degrees or null if it does not exist.
     * @return String value or null.
     */
    public final native String getPosAngleDeg() /*-{
		if (this["galdimAngle"] != undefined) {
			return this["galdimAngle"].toString();
		}
		return null;
    }-*/;

    /**
     * Print object contents.
     * @return Object contents
     */
    public final String toFormattedString() {
        String output = new String();
        output = "[Name [" + getName() + "], ";
        output = output + "Ra (deg) [" + getRaDeg() + "], ";
        output = output + "Dec (deg) [" + getDecDeg() + "], ";
        output = output + "Major axis (arcmin) [" + getMajorAxisArcmin() + "], ";
        output = output + "Minor axis (arcmin) [" + getMinorAxisArcmin() + "], ";
        output = output + "Position Angle (deg) [" + getPosAngleDeg() + "], ";
        output = output + "]";
        return output;
    }
}
