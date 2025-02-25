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

package esac.archive.esasky.cl.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class TargetDescriptionEvent extends GwtEvent<TargetDescriptionEventHandler> {

    /** Event type. */
    public static final Type<TargetDescriptionEventHandler> TYPE = new Type<>();

    private String targetName;
    private String targetDescription;
    private boolean rightSide;

    public TargetDescriptionEvent(final String targetName, final String targetDescription, boolean rightSide) {
        this.targetName = targetName;
        this.targetDescription = targetDescription;
        this.rightSide = rightSide;
    }

    @Override
    public final Type<TargetDescriptionEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final TargetDescriptionEventHandler handler) {
        handler.onEvent(this);
    }

    public final String getTargetName() {
        return this.targetName;
    }
    
    public final String getTargetDescription() {
    	return this.targetDescription;
    }

    public final boolean isRightSide() {
    	return this.rightSide;
    }
}
