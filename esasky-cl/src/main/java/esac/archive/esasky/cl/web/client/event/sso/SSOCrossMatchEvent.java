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

package esac.archive.esasky.cl.web.client.event.sso;

import com.google.gwt.event.shared.GwtEvent;

import esac.archive.esasky.ifcs.model.shared.ESASkySSOSearchResult.ESASkySSOObjType;

public class SSOCrossMatchEvent extends GwtEvent<SSOCrossMatchEventHandler> {

    /** Event type. */
    public final static Type<SSOCrossMatchEventHandler> TYPE = new Type<SSOCrossMatchEventHandler>();

    String ssoName;
    ESASkySSOObjType ssoType;

    /**
     * Constructor class.
     * @param inputCount Input integer
     * @param inputEntity Input Entity
     */
    public SSOCrossMatchEvent(String ssoName, ESASkySSOObjType ssoType) {
        super();
        this.ssoName = ssoName;
        this.ssoType = ssoType;

    }

    @Override
    public final Type<SSOCrossMatchEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final SSOCrossMatchEventHandler handler) {
        handler.newSsoSelected(this);
    }

    public String getSsoName() {
        return ssoName;
    }

    public ESASkySSOObjType getSsoType() {
        return ssoType;
    }

}
