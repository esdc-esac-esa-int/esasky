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
	
    public static native String getValidFilename(String filename) /*-{
    return filename.replace(/([^a-z0-9]+)/gi, '_')
 }-*/;

}
