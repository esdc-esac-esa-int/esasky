package esac.archive.absi.modules.cl.aladinlite.widget.client.model;

import com.google.gwt.core.client.JavaScriptObject;

public class ColorCfg extends JavaScriptObject  {

    protected ColorCfg() {
    }

    public final native double getAdditiveBlending() /*-{
        if (this.additiveBlending) {
            return this.additiveBlending;
        }
        return null;
    }-*/;

    public final native String getColormap() /*-{
        if (this.colormap) {
            return this.colormap;
        }
        return null;
    }-*/;


    public final native double getKBrightness() /*-{
        if (this.kBrightness) {
            return this.kBrightness;
        }
        return null;
    }-*/;

    public final native double getKContrast() /*-{
        if (this.kContrast) {
            return this.kContrast;
        }
        return null;
    }-*/;

    public final native double getKGamma() /*-{
        if (this.kGamma) {
            return this.kGamma;
        }
        return null;
    }-*/;

    public final native double getKSaturation() /*-{
        if (this.kSaturation) {
            return this.kSaturation;
        }
        return null;
    }-*/;

    public final native double getMaxCut() /*-{
        if (this.maxCut) {
            return this.maxCut;
        }
        return null;
    }-*/;

    public final native double getMinCut() /*-{
        if (this.minCut) {
            return this.minCut;
        }
        return null;
    }-*/;

    public final native double getOpacity() /*-{
        if (this.opacity) {
            return this.opacity;
        }
        return null;
    }-*/;

    public final native boolean getReversed() /*-{
        if (this.reversed) {
            return this.reversed;
        }
        return null;
    }-*/;

    public final native String getStretch() /*-{
        if (this.stretch) {
            return this.stretch;
        }
        return null;
    }-*/;

}
