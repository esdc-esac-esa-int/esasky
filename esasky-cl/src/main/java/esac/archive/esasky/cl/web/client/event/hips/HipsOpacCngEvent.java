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

package esac.archive.esasky.cl.web.client.event.hips;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event to record closing results event tab sent by Tab components.
 */
public class HipsOpacCngEvent extends GwtEvent<HipsOpacCngEventHandler> {

    /** event type. */
    public final static Type<HipsOpacCngEventHandler> TYPE = new Type<HipsOpacCngEventHandler>();

    /** opacity. */
    private double opacity;

    /**
     * constructor class.
     * @param inputOpacity Input double value
     */
    public HipsOpacCngEvent(final double inputOpacity) {
        this.opacity = inputOpacity;
    }

    /**
     * getOpacity().
     * @return double value
     */
    public final double getOpacity() {
        return opacity;
    }

    @Override
    public final Type<HipsOpacCngEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final HipsOpacCngEventHandler handler) {
        handler.onChangeEvent(this);
    }
}
