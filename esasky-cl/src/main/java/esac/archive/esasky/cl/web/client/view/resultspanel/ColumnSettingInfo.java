package esac.archive.esasky.cl.web.client.view.resultspanel;
import com.google.gwt.core.client.JavaScriptObject;

public class ColumnSettingInfo extends JavaScriptObject {
	
	protected ColumnSettingInfo() {
	}
	
    public static native ColumnSettingInfo createColumnSetting(boolean isVisible, int initialIndex, String label, String description) /*-{
    	var object = {};
//		object.isVisible = isVisible;
//		object.initialIndex = initialIndex;
//		object.label = label;
//		object.description = description;

//		if (this["data"] != undefined) {
//			if (this["data"]["keys"] != undefined) {
//				return this["data"]["keys"].toString();
//			}
//		}
		return object;
    }-*/;

    public static native ColumnSettingInfo createColumnSetting() /*-{
    	var object = {};
//		object.isVisible = isVisible;
//		object.initialIndex = initialIndex;
//		object.label = label;
//		object.description = description;

//		if (this["data"] != undefined) {
//			if (this["data"]["keys"] != undefined) {
//				return this["data"]["keys"].toString();
//			}
//		}
		return object;
    }-*/;
    
    public final native void setBooleanProperty(String propertyName, boolean property)/*-{
    	this[propertyName] = property;
	}-*/;

    public final native void setStringProperty(String propertyName, String property)/*-{
    	this[propertyName] = property;
	}-*/;
    
    public final native String getStringProperty(String propertyName)/*-{
    	return this._row.data[propertyName];
	}-*/;

    public final native String getStringProperty2(String propertyName)/*-{
    	return this[propertyName];
	}-*/;

    public final native void setIntegerProperty(String propertyName, int property)/*-{
    	this[propertyName] = property;
	}-*/;
}

