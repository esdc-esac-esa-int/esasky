/*
ESASky
Copyright (C) 2025 European Space Agency

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published
by the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

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
    
    public final native String[] getPropertiesArray() /*-{
		return Object.getOwnPropertyNames(this)
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

