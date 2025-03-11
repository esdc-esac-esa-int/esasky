package esac.archive.absi.modules.cl.aladinlite.widget.client;

import com.google.gwt.core.client.JavaScriptObject;

public class AladinLiteUtils {
    public static native String increaseBrightness(String hex, int percent) /*-{
    	if(!hex) {
    		return hex;
    	}
        // strip the leading # if it's there
        hex = hex.replace(/^\s*#|\s*$/g, '');

        // convert 3 char codes --> 6, e.g. `E0F` --> `EE00FF`
        if(hex.length == 3){
            hex = hex.replace(/(.)/g, '$1$1');
        }

        var r = parseInt(hex.substr(0, 2), 16),
            g = parseInt(hex.substr(2, 2), 16),
            b = parseInt(hex.substr(4, 2), 16);

        return '#' +
            ((0|(1<<8) + r + (256 - r) * percent / 100).toString(16)).substr(1) +
            ((0|(1<<8) + g + (256 - g) * percent / 100).toString(16)).substr(1) +
            ((0|(1<<8) + b + (256 - b) * percent / 100).toString(16)).substr(1);
    }-*/;


    public static native String getShapeColor(JavaScriptObject object) /*-{
        var baseColor = object.color;
        if (! baseColor && object.overlay) {
            baseColor = object.overlay.color;
        }

        if (!baseColor && object.catalog) {
            baseColor = object.catalog.color;
        }

        if (! baseColor) {
            baseColor = '#ff0000';
        }

        return baseColor
    }-*/;
}
