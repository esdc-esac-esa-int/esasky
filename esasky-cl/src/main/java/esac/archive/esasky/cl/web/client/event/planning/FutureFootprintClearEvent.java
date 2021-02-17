package esac.archive.esasky.cl.web.client.event.planning;

import com.google.gwt.event.shared.GwtEvent;

import esac.archive.esasky.cl.web.client.view.ctrltoolbar.planningmenu.FutureFootprintRow;

/**
 * Event to record Select Observation events sent by Table components.
 */
public class FutureFootprintClearEvent extends GwtEvent<FutureFootprintClearEventHandler> {

    /** evnet type. */
    public final static Type<FutureFootprintClearEventHandler> TYPE = new Type<FutureFootprintClearEventHandler>();

    private FutureFootprintRow futureFootprintRow;

    public FutureFootprintClearEvent(final FutureFootprintRow futureFootprintRow) {
        this.futureFootprintRow = futureFootprintRow;
    }

    @Override
    public final Type<FutureFootprintClearEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final FutureFootprintClearEventHandler handler) {
        handler.clearPlanningFootprint(this);
    }

    public FutureFootprintRow getFutureFootprintRow() {
        return futureFootprintRow;
    }

}
