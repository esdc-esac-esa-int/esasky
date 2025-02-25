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

import esac.archive.esasky.ifcs.model.shared.ESASkySearchResult;

/**
 * Event to record closing results event tab sent by Tab components.
 */
public class MultiTargetClickEvent extends GwtEvent<MultiTargetClickEventHandler> {

    /** Event type. */
    public final static Type<MultiTargetClickEventHandler> TYPE = new Type<MultiTargetClickEventHandler>();

    /** class attribute with type MultiTargetObject. */
    private ESASkySearchResult target;
    // private MultiTargetEntity target;
    // private SIMBADResult target;
    /** index. */
    private int index;
    /** show progress?. */
    private boolean showProgress;

    /**
     * Class Constructor.
     * @param inputTarget Input MultiTargetEntity
     * @param inputIndex Input Integer
     * @param inptShowProgress Input Boolean value
     */
    public MultiTargetClickEvent(final ESASkySearchResult inputTarget, final int inputIndex,
            final boolean inptShowProgress) {
        this.target = inputTarget;
        this.index = inputIndex;
        this.showProgress = inptShowProgress;
    }

    @Override
    public final Type<MultiTargetClickEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final MultiTargetClickEventHandler handler) {
        handler.onClickEvent(this);
    }

    /**
     * getTarget().
     * @return MultTargetObject.
     */
    public final ESASkySearchResult getTarget() {
        return this.target;
    }

    /**
     * getIndex().
     * @return integer.
     */
    public final int getIndex() {
        return this.index;
    }

    /**
     * getShowProgress().
     * @return boolean.
     */
    public final boolean getShowProgress() {
        return this.showProgress;
    }
}
