package esac.archive.esasky.cl.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class AuthorSearchEvent extends GwtEvent<AuthorSearchEventHandler> {

    /** Event type. */
    public static Type<AuthorSearchEventHandler> TYPE = new Type<AuthorSearchEventHandler>();

    String authorName;

    public AuthorSearchEvent(String authorName) {
        super();
        this.authorName = authorName;

    }

    @Override
    public final Type<AuthorSearchEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final AuthorSearchEventHandler handler) {
        handler.onAuthorSelected(this);
    }

    public String getAuthorName() {
        return authorName;
    }

}
