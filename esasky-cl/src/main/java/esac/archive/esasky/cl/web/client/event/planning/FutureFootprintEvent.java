package esac.archive.esasky.cl.web.client.event.planning;

import com.google.gwt.event.shared.GwtEvent;

import esac.archive.esasky.cl.web.client.view.ctrltoolbar.planningmenu.FutureFootprintRow;

/**
 * Event to record Select Observation events sent by Table components.
 */
public class FutureFootprintEvent extends GwtEvent<FutureFootprintEventHandler> {

    /** evnet type. */
    public final static Type<FutureFootprintEventHandler> TYPE = new Type<FutureFootprintEventHandler>();

    private FutureFootprintRow futureFootprintRow;

    public FutureFootprintEvent(final FutureFootprintRow futureFootprintRow) {

        this.futureFootprintRow = futureFootprintRow;
    }

    @Override
    public final Type<FutureFootprintEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final FutureFootprintEventHandler handler) {
        handler.drawPlanningFootprint(this);
    }

    public FutureFootprintRow getFutureFootprintRow() {
        return futureFootprintRow;
    }

}
