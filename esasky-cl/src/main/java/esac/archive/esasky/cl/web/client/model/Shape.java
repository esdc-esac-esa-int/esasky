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
