package esac.archive.esasky.cl.web.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface ExportCSVEventHandler extends EventHandler {

    /**
     * onDownloadClick().
     * @param clickEvent Input SentTableToEvent.
     */
    void onExportClick(ExportCSVEvent clickEvent);

}
