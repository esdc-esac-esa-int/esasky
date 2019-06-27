package esac.archive.esasky.cl.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

import esac.archive.esasky.cl.gwidgets.client.util.SaveAllView;

/**
 * @author María H. Sarmiento Copyright (c) 2016- European Space Agency
 */
public class UpdateNumRowsSelectedEvent extends GwtEvent<UpdateNumRowsSelectedEventHandler> {

    /** Event type. */
    public static Type<UpdateNumRowsSelectedEventHandler> TYPE = new Type<UpdateNumRowsSelectedEventHandler>();

    /** ESASkyUniqID. */
    private String esaSkyUniqID;
    /** SaveAllView local instance. */
    private SaveAllView saveAllView;

    /**
     * Class constructor.
     * @param inputEsaSkyUniqID Input String
     * @param inputSaveAllView Input SaveAllView
     */
    public UpdateNumRowsSelectedEvent(final String inputEsaSkyUniqID,
            final SaveAllView inputSaveAllView) {
        this.esaSkyUniqID = inputEsaSkyUniqID;
        this.saveAllView = inputSaveAllView;
    }

    @Override
    public final Type<UpdateNumRowsSelectedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final UpdateNumRowsSelectedEventHandler handler) {
        handler.onUpdateClick(this);
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
