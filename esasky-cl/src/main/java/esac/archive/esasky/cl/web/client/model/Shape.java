/*
ESASky
Copyright (C) 2025 Henrik Norman

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

package esac.archive.esasky.cl.web.client.model;


import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;

public class Shape {

    private String ra;
    private String dec;
    private String shapeName;
    private int shapeId;
    private GeneralJavaScriptObject jsObject;

    public String getRa() {
        return ra;
    }

    public void setRa(String ra) {
        this.ra = ra;
    }

    public String getDec() {
        return dec;
    }

    public void setDec(String dec) {
        this.dec = dec;
    }

    public int getShapeId() {
        return shapeId;
    }

    public void setShapeId(int rowId) {
        this.shapeId = rowId;
        if(jsObject != null) {
        	jsObject.setProperty("id", rowId);
        }
    }
    public String getShapeName() {
    	return shapeName;
    }
    
    public void setShapeName(String shapeName) {
    	this.shapeName = shapeName;
    	if(jsObject != null) {
    		setShapeName(jsObject, shapeName);
    	}
    }
    
    public native void setShapeName(GeneralJavaScriptObject obj, String shapeName) /*-{
    	
    	if(obj.length){
    		for(var i = 0; i < obj.length;i++){
    			obj[i].name = shapeName;
    		}
    	}
    	obj.name = shapeName;
    	
     }-*/;

    public GeneralJavaScriptObject getJsObject() {
        return jsObject;
    }

    public void setJsObject(GeneralJavaScriptObject jsObject) {
    	jsObject.setProperty("id", this.shapeId);
    	jsObject.setProperty("name", this.shapeName);
        this.jsObject = jsObject;
    }
}
