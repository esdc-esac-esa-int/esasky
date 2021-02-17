package esac.archive.esasky.cl.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class BibcodeSearchEvent extends GwtEvent<BibcodeSearchEventHandler> {

    /** Event type. */
    public final static Type<BibcodeSearchEventHandler> TYPE = new Type<BibcodeSearchEventHandler>();

    String bibcode;

    public BibcodeSearchEvent(String bibcode) {
        super();
        this.bibcode = bibcode;

    }

    @Override
    public final Type<BibcodeSearchEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final BibcodeSearchEventHandler handler) {
        handler.onBibcodeSelected(this);
    }

    public String getBibcode() {
        return bibcode;
    }

}
