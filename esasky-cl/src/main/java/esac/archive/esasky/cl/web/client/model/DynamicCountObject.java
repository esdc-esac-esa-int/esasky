package esac.archive.esasky.cl.web.client.model;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Javascript object which looks like: "{"count":1331,"aprox":true}"
 * @author Raul Gutierrez
 *
 */
public class DynamicCountObject extends JavaScriptObject {

	  protected DynamicCountObject() {}

	  public final native int getCount() /*-{ return this.count; }-*/; 
	  public final native boolean isAprox() /*-{ return this.aprox; }-*/;
}
