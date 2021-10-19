package esac.archive.esasky.ifcs.model.client;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;

public class GeneralJavaScriptObject extends JavaScriptObject {
	
	protected GeneralJavaScriptObject() {
	}
	
   
	public final native GeneralJavaScriptObject setProperty(String propertyName, String propertyValue)/*-{
    	return this[propertyName] = propertyValue;
	}-*/;

	public final native GeneralJavaScriptObject setProperty(String propertyName, Object propertyValue)/*-{
    	return this[propertyName] = propertyValue;
	}-*/;

	public final native GeneralJavaScriptObject setProperty(String propertyName, boolean propertyValue)/*-{
    	return this[propertyName] = propertyValue;
	}-*/;
	
	public final native GeneralJavaScriptObject getProperty(String propertyName)/*-{
    	return this[propertyName];
	}-*/;
	
    public final native String getStringProperty(String propertyName)/*-{
        return this[propertyName] && this[propertyName].toString ? this[propertyName].toString() : this[propertyName];
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
    
    public final native String getProperties() /*-{
		return Object.getOwnPropertyNames(this).toString()
	}-*/;
    
    
    public final native GeneralJavaScriptObject invokeSelf(Object... parameters)/*-{
    	return this.apply(this, parameters);
	}-*/;
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
    
    public final native boolean hasProperty(String propertyName)/*-{
		return this.hasOwnProperty(propertyName) && this[propertyName] != null;
	}-*/;
    
    public final native GeneralJavaScriptObject wrapInArray()/*-{
		return [this];
	}-*/;
    
    public final native String toJSONString()/*-{
		return JSON.stringify(this);
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
    public static native double convertToDouble(GeneralJavaScriptObject javaScriptObject)/*-{
    	return javaScriptObject;
    }-*/;
}

