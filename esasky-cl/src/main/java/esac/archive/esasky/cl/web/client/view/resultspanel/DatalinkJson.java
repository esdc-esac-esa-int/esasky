package esac.archive.esasky.cl.web.client.view.resultspanel;

import com.google.gwt.core.client.JavaScriptObject;

class DatalinkJson extends JavaScriptObject {
  protected DatalinkJson() {}

  public final native DatalinkMetadata[] getMetadata() /*-{ return this.metadata; }-*/;
  public final native String[][] getData() /*-{ return this.data; }-*/;
}