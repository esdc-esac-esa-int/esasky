package esac.archive.esasky.cl.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

import esac.archive.esasky.cl.gwidgets.client.util.SaveAllView;

/**
 * Event to record download events sent by Tab components.
 * @author María H. Sarmiento Copyright (c) 2016- European Space Agency
 */
public class ExportCSVEvent extends GwtEvent<ExportCSVEventHandler> {

    /** Event type. */
    public final static Type<ExportCSVEventHandler> TYPE = new Type<ExportCSVEventHandler>();

    /** ESASkyUniqID. */
    private String esaSkyUniqID;
    /** SaveAllView local instance. */
    private SaveAllView saveAllView;

    /**
     * Class constructor.
     * @param inputEsaSkyUniqID Input String
     * @param inputSaveAllView Input SaveAllView
     */
    public ExportCSVEvent(final String inputEsaSkyUniqID,
            final SaveAllView inputSaveAllView) {
        this.esaSkyUniqID = inputEsaSkyUniqID;
        this.saveAllView = inputSaveAllView;
    }

    @Override
    public final Type<ExportCSVEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final ExportCSVEventHandler handler) {
        handler.onExportClick(this);
    }

    /**
     * getEsaSkyUniqID().
     * @return String
     */
    public final String getEsaSkyUniqID() {
        return esaSkyUniqID;
    }

    /**
     * getSaveAllView().
     * @return SaveAllView
     */
    public final SaveAllView getSaveAllView() {
        return this.saveAllView;
    }
}
