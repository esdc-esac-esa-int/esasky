package esac.archive.esasky.cl.web.client.view.resultspanel;

import com.google.gwt.core.client.JavaScriptObject;

class DatalinkMetadata extends JavaScriptObject {
  protected DatalinkMetadata() {}

  public final native String getName() /*-{ return this.name; }-*/;
  public final native String getDescription() /*-{ return this.description; }-*/;
  public final native String getUtype() /*-{ return this.utype; }-*/;
  public final native String getUcd() /*-{ return this.ucd; }-*/;
  public final native String getDatatype() /*-{ return this.datatype; }-*/;
}