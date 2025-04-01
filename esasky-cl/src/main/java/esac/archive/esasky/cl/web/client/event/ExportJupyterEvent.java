package esac.archive.esasky.cl.web.client.event;

import com.google.gwt.event.shared.GwtEvent;
import esac.archive.esasky.cl.gwidgets.client.util.SaveAllView;

public class ExportJupyterEvent extends GwtEvent<ExportJupyterEventHandler> {

    public final static Type<ExportJupyterEventHandler> TYPE = new Type<ExportJupyterEventHandler>();

    private final String esaSkyUniqID;
    private final SaveAllView saveAllView;

    public ExportJupyterEvent(final String inputEsaSkyUniqID,
                              final SaveAllView inputSaveAllView) {
        this.esaSkyUniqID = inputEsaSkyUniqID;
        this.saveAllView = inputSaveAllView;
    }

    @Override
    public final Type<ExportJupyterEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final ExportJupyterEventHandler handler) {
        handler.onExportClick(this);
    }

    public final String getEsaSkyUniqID() {
        return this.esaSkyUniqID;
    }

    public final SaveAllView getSaveAllView() {
        return this.saveAllView;
    }
}
