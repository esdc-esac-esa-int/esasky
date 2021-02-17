package esac.archive.esasky.cl.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author María H. Sarmiento Copyright (c) 2016- European Space Agency
 */
public class SendTableToEvent extends GwtEvent<SendTableToEventHandler> {

    /** Event type. */
    public final static Type<SendTableToEventHandler> TYPE = new Type<SendTableToEventHandler>();

    /** ESASkyUniqID. */
    private String esaSkyUniqID;
    /** tab type can be ObservationsTablePanel or SourcesTablePanel. */

    /**
     * Class constructor.
     * @param inputEsaSkyUniqID Input String
     * @param inputTabType Input String
     */
    public SendTableToEvent(final String inputEsaSkyUniqID) {
        this.esaSkyUniqID = inputEsaSkyUniqID;
    }

    @Override
    public final Type<SendTableToEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final SendTableToEventHandler handler) {
        handler.onSendTableClick(this);
    }

    public final String getEsaSkyUniqID() {
        return esaSkyUniqID;
    }

}
