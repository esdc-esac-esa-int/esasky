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

/**
 * Event to record closing results event tab sent by Tab components.
 */
public class MouseMoveEvent extends GwtEvent<MouseMoveEventHandler> {

    /** Event type. */
    public final static Type<MouseMoveEventHandler> TYPE = new Type<MouseMoveEventHandler>();

    private double raDeg;

    private double decDeg;

    private int mouseX;

    private int mouseY;

    public MouseMoveEvent(final double raDeg, final double decDeg) {
        this.raDeg = raDeg;
        this.decDeg = decDeg;
    }

    public MouseMoveEvent(final double raDeg, final double decDeg, final int mouseX,
            final int mouseY) {
        this.raDeg = raDeg;
        this.decDeg = decDeg;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }

    @Override
    public final Type<MouseMoveEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final MouseMoveEventHandler handler) {
        handler.onMouseMoveEvent(this);
    }

    public double getRaDeg() {
        return raDeg;
    }

    public double getDecDeg() {
        return decDeg;
    }

    public double getMouseX() {
        return mouseX;
    }

    public double getMouseY() {
        return mouseY;
    }

}
