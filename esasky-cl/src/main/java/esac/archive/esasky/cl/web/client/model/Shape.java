package esac.archive.esasky.cl.web.client.model;

import com.google.gwt.core.client.JavaScriptObject;

public class Shape {

    private String ra;
    private String dec;
    private int shapeId;
    private JavaScriptObject jsObject;

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
    }

    public JavaScriptObject getJsObject() {
        return jsObject;
    }

    public void setJsObject(JavaScriptObject jsObject) {
        this.jsObject = jsObject;
    }
}
