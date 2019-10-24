package esac.archive.esasky.cl.web.client.utility;

import com.google.gwt.core.client.JavaScriptObject;

public final class DownloadUtils {
	public static native void downloadCanvas(String fileName, JavaScriptObject imageCanvas) /*-{
		imageCanvas.toBlob(function(blob) {
    		$wnd.saveAs(blob, fileName);
		});
    }-*/;
	public static native void downloadFile(String fileName, String file, String mimeType) /*-{
		$wnd.saveAs(new Blob([file], {type : mimeType}), fileName);
    }-*/;

}
