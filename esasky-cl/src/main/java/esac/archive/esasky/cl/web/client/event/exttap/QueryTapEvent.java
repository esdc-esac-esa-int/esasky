package esac.archive.esasky.cl.web.client.event.exttap;

import com.google.gwt.event.shared.GwtEvent;

public class QueryTapEvent extends GwtEvent<QueryTapEventHandler> {
    public static final Type<QueryTapEventHandler> TYPE = new Type<>();

    private final String tapUrl;
    private final String tableName;
    private final String description;
    private final String query;
    private final String publisher;

    public QueryTapEvent(String tapUrl, String tableName, String description, String publisher, String query) {
        this.tapUrl = tapUrl;
        this.tableName = tableName;
        this.query = query;
        this.description = description;
        this.publisher = publisher;
    }


    @Override
    public Type<QueryTapEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(QueryTapEventHandler handler) {
        handler.doQuery(this);
    }

    public String getTapUrl() {
        return tapUrl;
    }

    public String getTableName() {
        return tableName;
    }

    public String getQuery() {
        return query;
    }

    public String getDescription() {
        return description;
    }

    public String getPublisher() {
        return publisher;
    }
}
