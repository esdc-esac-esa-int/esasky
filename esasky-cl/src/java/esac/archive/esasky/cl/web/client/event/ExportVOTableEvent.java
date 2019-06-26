package esac.archive.esasky.cl.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

import esac.archive.esasky.cl.gwidgets.client.util.SaveAllView;

/**
 * Event to record download events sent by Tab components.
 * @author María H. Sarmiento Copyright (c) 2016- European Space Agency
 */
public class ExportVOTableEvent extends GwtEvent<ExportVOTableEventHandler> {

    /** Event type. */
    public static Type<ExportVOTableEventHandler> TYPE = new Type<ExportVOTableEventHandler>();

    /** ESASkyUniqID. */
    private String esaSkyUniqID;
    /** tab type can be ObservationsTablePanel or SourcesTablePanel. */
    private String tabType;
    /** SaveAllView local instance. */
    private SaveAllView saveAllView;

    /**
     * Class constructor.
     * @param inputEsaSkyUniqID Input String
     * @param inputTabType Input String
     * @param inputSaveAllView Input SaveAllView
     */
    public ExportVOTableEvent(final String inputEsaSkyUniqID, final String inputTabType,
            final SaveAllView inputSaveAllView) {
        this.esaSkyUniqID = inputEsaSkyUniqID;
        this.tabType = inputTabType;
        this.saveAllView = inputSaveAllView;
    }

    @Override
    public final Type<ExportVOTableEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final ExportVOTableEventHandler handler) {
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
     * getTapType().
     * @return String
     */
    public final String getTabType() {
        return tabType;
    }

    /**
     * getSaveAllView().
     * @return SaveAllView
     */
    public final SaveAllView getSaveAllView() {
        return this.saveAllView;
    }
}
