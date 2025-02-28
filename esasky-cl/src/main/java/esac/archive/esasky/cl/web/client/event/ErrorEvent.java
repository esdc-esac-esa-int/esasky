/*
ESASky
Copyright (C) 2025 European Space Agency

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

package esac.archive.esasky.cl.web.client.event;

import com.google.gwt.event.shared.GwtEvent;


public class ErrorEvent extends GwtEvent<ErrorEventHandler> {

    public static final Type<ErrorEventHandler> TYPE = new Type<>();
    public static final int FRONTEND_ERROR = 0;
    private final int status;
    private final String message;
    private final String details;
    public ErrorEvent(String message, Exception exception) {
        this(message, exception.getMessage());
    }

    public ErrorEvent(String message, String details) {
        this(FRONTEND_ERROR, message, details);
    }

    public ErrorEvent(int status, String message, String details) {
        this.status = status;
        this.message = message;
        this.details = details;
    }

    @Override
    public final Type<ErrorEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final ErrorEventHandler handler) {
        handler.onEvent(this);
    }

    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }

    public int getStatus() {
        return status;
    }
}
