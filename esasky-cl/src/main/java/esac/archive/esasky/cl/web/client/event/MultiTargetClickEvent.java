package esac.archive.esasky.cl.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

import esac.archive.esasky.ifcs.model.shared.ESASkySearchResult;

/**
 * Event to record closing results event tab sent by Tab components.
 */
public class MultiTargetClickEvent extends GwtEvent<MultiTargetClickEventHandler> {

    /** Event type. */
    public static Type<MultiTargetClickEventHandler> TYPE = new Type<MultiTargetClickEventHandler>();

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
    public MultiTargetClickEvent(final ESASkySearchResult inputTarget, final int inputIndex,
            final boolean inptShowProgress) {
        this.target = inputTarget;
        this.index = inputIndex;
        this.showProgress = inptShowProgress;
    }

    @Override
    public final Type<MultiTargetClickEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final MultiTargetClickEventHandler handler) {
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
