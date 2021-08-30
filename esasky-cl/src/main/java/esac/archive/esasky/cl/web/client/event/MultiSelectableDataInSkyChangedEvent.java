package esac.archive.esasky.cl.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class MultiSelectableDataInSkyChangedEvent extends GwtEvent<MultiSelectableDataInSkyChangedEventHandler> {

    public final static Type<MultiSelectableDataInSkyChangedEventHandler> TYPE = new Type<MultiSelectableDataInSkyChangedEventHandler>();

    private final boolean atLeastOneMultiSelectableDatasetInSky;
    
    public MultiSelectableDataInSkyChangedEvent(boolean atLeastOneMultiSelectableDatasetInSky) {
    	this.atLeastOneMultiSelectableDatasetInSky = atLeastOneMultiSelectableDatasetInSky;
    }

    @Override
    public final Type<MultiSelectableDataInSkyChangedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final MultiSelectableDataInSkyChangedEventHandler handler) {
        handler.onChange(this);
    }
    
    public boolean isAtLeastOneMultiSelectableDatasetInSky() {
    	return atLeastOneMultiSelectableDatasetInSky;
    }

}
