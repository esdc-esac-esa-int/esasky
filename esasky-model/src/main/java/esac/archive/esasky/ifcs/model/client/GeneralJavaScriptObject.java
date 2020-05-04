package esac.archive.esasky.ifcs.model.client;
import com.google.gwt.core.client.JavaScriptObject;

public class GeneralJavaScriptObject extends JavaScriptObject {
	
	protected GeneralJavaScriptObject() {
	}
	
   
	public final native GeneralJavaScriptObject setProperty(String propertyName, String propertyValue)/*-{
    	return this[propertyName] = propertyValue;
	}-*/;

	public final native GeneralJavaScriptObject setProperty(String propertyName, Object propertyValue)/*-{
    	return this[propertyName] = propertyValue;
	}-*/;
	
	public final native GeneralJavaScriptObject getProperty(String propertyName)/*-{
    	return this[propertyName];
	}-*/;
	
    public final native String getStringProperty(String propertyName)/*-{
    	return this[propertyName];
	}-*/;
    
    public final native double getDoubleProperty(String propertyName)/*-{
    	return this[propertyName] && !isNaN(this[propertyName]) ? this[propertyName]: 0;
	}-*/;
    
    public final Double getDoubleOrNullProperty(String propertyName) {
        try {
           return new Double(getStringProperty(propertyName));
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    public final native GeneralJavaScriptObject invokeFunction(String functionName)/*-{
    	return this[functionName]();
	}-*/;
    
    public final native GeneralJavaScriptObject invokeFunction(String functionName, String parameter)/*-{
    	return this[functionName](parameter);
	}-*/;

    public final native GeneralJavaScriptObject invokeFunction(String functionName, Object... parameters)/*-{
    	return this[functionName].apply(this, parameters);
	}-*/;

    public final native String jsonStringify() /*-{
    	return JSON.stringify(this);
    }-*/;
    
    public static native GeneralJavaScriptObject createJsonObject(String jsonText) /*-{
		return JSON.parse(jsonText);
	}-*/;
    
    
    
    
    
    
    public static native boolean convertToBoolean(GeneralJavaScriptObject javaScriptObject)/*-{
    	return javaScriptObject;
    }-*/;
    public static native GeneralJavaScriptObject[] convertToArray(GeneralJavaScriptObject javaScriptObject)/*-{
    	return javaScriptObject;
    }-*/;
    public static native String convertToString(GeneralJavaScriptObject javaScriptObject)/*-{
    	return javaScriptObject;
    }-*/;
    public static native int convertToInteger(GeneralJavaScriptObject javaScriptObject)/*-{
    	return javaScriptObject;
    }-*/;
}

