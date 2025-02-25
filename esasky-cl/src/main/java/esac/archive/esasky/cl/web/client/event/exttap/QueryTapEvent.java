/*
ESASky
Copyright (C) 2025 Henrik Norman

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published
by the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

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
