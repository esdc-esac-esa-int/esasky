package esac.archive.absi.modules.cl.aladinlite.widget.client.model;

import com.google.gwt.core.client.JavaScriptObject;

public class ImageLayer extends JavaScriptObject {
    protected ImageLayer() {}

    public final native void setCutLimits() /*-{
        if (this && this.getCuts) {
            var cuts = this.getCuts();
            var minCut = cuts[0];
            var maxCut = cuts[1];
            this.minCutLimit = minCut;
            this.maxCutLimit = maxCut;
        } else if  (this){
            this.minCutLimit = 0;
            this.maxCutLimit = 1;
        }
    }-*/;

    public final native String getLayer() /*-{
        if (this.layer) {
            return this.layer;
        }
        return null;
    }-*/;

    public final native String getName() /*-{
        if (this.name) {
            return this.name;
        }
        return null;
    }-*/;

    public final native String getId() /*-{
        if (this.id) {
            return this.id;
        }
        return null;
    }-*/;

    public final native double getRaDeg() /*-{
        if (this.ra) {
            return this.ra;
        }
        return null;
    }-*/;


    public final native double getDecDeg() /*-{
        if (this.dec) {
            return this.dec;
        }
        return null;
    }-*/;


    public final native double getFovDeg() /*-{
        if (this.fov) {
            return this.fov;
        }
        return null;
    }-*/;

    public final native String getImageFormat() /*-{
        if (this.imgFormat) {
            return this.imgFormat;
        }
        return null;
    }-*/;

    public final native String[] getAvailableImageFormats() /*-{
        if (this.acceptedFormats) {
            return this.acceptedFormats;
        }
        return null;
    }-*/;


    public final native ColorCfg getColorCfg() /*-{
        if (this.colorCfg) {
            return this.colorCfg;
        }
        return null;
    }-*/;

    public final native double getMinCutLimit() /*-{
        if (this.minCutLimit) {
            return this.minCutLimit;
        } else {
            return 0;
        }
    }-*/;

    public final native double getMaxCutLimit() /*-{
        if (this.maxCutLimit) {
            return this.maxCutLimit;
        } else {
            return 1;
        }
    }-*/;

    public final native String getStretch()  /*-{
        return this.colorCfg.stretch;
    }-*/;


    public final native double getMinCut()  /*-{
        var cuts = this.getCuts();
        if (cuts && cuts.length > 0) {
            return cuts[0];
        } else {
            return 0;
        }
    }-*/;

    public final native double getMaxCut()  /*-{
        var cuts = this.getCuts();
        if (cuts && cuts.length > 1) {
            return cuts[1];
        } else {
            return 1;
        }
    }-*/;

    public final native void setColorMap(String name) /*-{
        this.setColormap(name);
    }-*/;

    public final native void setImageFormat(String format) /*-{
        // Does not exist for fits files
        if (typeof this.setImageFormat === "function") {
            this.setImageFormat(format);
        }
    }-*/;

    public final native void setColorMapKeepOptions(String name) /*-{
        this.setColormap(name, {reversed: this.colorCfg.reversed, stretch: this.colorCfg.stretch});
    }-*/;

    public final native void setColorMapOptions(boolean reversed, String stretch)  /*-{
        this.setColormap(this.colorCfg.colormap, {reversed: reversed, stretch: stretch});
    }-*/;


    public final native void setCuts(double min, double max)  /*-{
        this.setCuts(min, max)
    }-*/;

    public final native void setCuts(double min, double max, double defaultMin, double defaultMax)  /*-{
        this.setCuts(min, max, defaultMin, defaultMax)
    }-*/;

    public final native void setGamma(double gamma)  /*-{
        this.setGamma(gamma);
    }-*/;

    public final native void setSaturation(double saturation)  /*-{
        this.setSaturation(saturation);
    }-*/;

    public final native void setContrast(double contrast)  /*-{
        this.setContrast(contrast);
    }-*/;

    public final native void setBrightness(double brightness)  /*-{
        this.setBrightness(brightness);
    }-*/;

    public final native void setOpacity(double opacity)  /*-{
        this.setOpacity(opacity);
    }-*/;

    public final native void setBlending(boolean blending)  /*-{
        this.setBlendingConfig(blending);
    }-*/;

}
