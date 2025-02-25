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

package esac.archive.esasky.cl.web.client.view.searchpanel.targetlist;

import java.util.Iterator;

import org.moxieapps.gwt.uploader.client.Uploader;

import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.WidgetCollection;

/**
 * The sole reason this class exists is to fix a bug in the moxieapps uploader
 * (org.moxieapps.gwt.uploader-1.1.0.jar) where it adds a new upload input and
 * button each time its <code>onLoad()</code> method is called, i.e. every time
 * you navigate away from the page and then back to it.
 */
public class EsaSkyUploader extends Uploader {
    @Override
    protected void onLoad() {
        boolean hasFileUploadAlready = false;
        WidgetCollection children = getChildren();
        for (Iterator<Widget> iterator = children.iterator(); iterator.hasNext();) {
            Widget eachWidget = iterator.next();
            if (eachWidget instanceof FileUpload) {
                hasFileUploadAlready = true;
            }
        }
        // Only call the super method if there isn't already a file upload input and button
        if (!hasFileUploadAlready) {
            super.onLoad();
        }
    }
}
