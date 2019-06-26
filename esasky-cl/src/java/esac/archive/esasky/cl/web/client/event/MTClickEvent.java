package esac.archive.esasky.cl.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

import esac.archive.ammi.ifcs.model.shared.ESASkySearchResult;

/**
 * Event to record closing results event tab sent by Tab components.
 */
public class MTClickEvent extends GwtEvent<MTClickEventHandler> {

    /** Event type. */
    public static Type<MTClickEventHandler> TYPE = new Type<MTClickEventHandler>();

    /** class attribute with type MultiTargetObject. */
    private ESASkySearchResult target;
    // private MultiTargetEntity target;
    // private SIMBADResult target;
    /** index. */
    private int index;
    /** show progress?. */
    private boolean showProgress;

    /**
     * Class Constructor.
     * @param inputTarget Input MultiTargetEntity
     * @param inputIndex Input Integer
     * @param inptShowProgress Input Boolean value
     */
    public MTClickEvent(final ESASkySearchResult inputTarget, final int inputIndex,
            final boolean inptShowProgress) {
        this.target = inputTarget;
        this.index = inputIndex;
        this.showProgress = inptShowProgress;
    }

    @Override
    public final Type<MTClickEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final MTClickEventHandler handler) {
        handler.onClickEvent(this);
    }

    /**
     * getTarget().
     * @return MultTargetObject.
     */
    public final ESASkySearchResult getTarget() {
        return this.target;
    }

    /**
     * getIndex().
     * @return integer.
     */
    public final int getIndex() {
        return this.index;
    }

    /**
     * getShowProgress().
     * @return boolean.
     */
    public final boolean getShowProgress() {
        return this.showProgress;
    }
}
