package esac.archive.esasky.cl.web.client.view.ctrltoolbar.uploadtargetlist;

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
