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

package esac.archive.esasky.cl.web.client.view.common;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.OpenEvent;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;

public abstract class MovableResizablePanel<T> extends MovablePanel implements Hidable<T> {

    private boolean isShowing = false;

    public MovableResizablePanel(String googleEventCategory, boolean isSuggestedPositionCenter) {
        super(googleEventCategory, isSuggestedPositionCenter);
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        getResizeElement().getStyle().setProperty("resize", "both");
        getResizeElement().getStyle().setProperty("overflow", "auto");
        String resizableElementId = getResizeElement().getId();
        if("".equals(resizableElementId)) {
        	Log.error("Resizable Elements needs an Id");
        }
        addResizeHandler(resizableElementId);

        addSingleElementAbleToInitiateMoveOperation(getMovableElement());
        setSnapping(false);
    }

    @Override
    public void show() {
        isShowing = true;
        this.removeStyleName("displayNone");
        updateHandlers();
        setMaxSize();
        ensureDialogFitsInsideWindow();
        OpenEvent.fire(this, null);
    }

    @Override
    public void hide() {
        isShowing = false;
        this.addStyleName("displayNone");
        this.removeHandlers();
        CloseEvent.fire(this, null);
    }

    @Override
    public void toggle() {
        if (isShowing) {
            hide();
        } else {
            show();
        }
    }

    @Override
    public boolean isShowing() {
        return isShowing;
    }

    protected abstract Element getMovableElement();

    protected void onResize() {
        Style elementStyle = this.getElement().getStyle();
        int maxWidth = MainLayoutPanel.getMainAreaWidth() + MainLayoutPanel.getMainAreaAbsoluteLeft() - this.getAbsoluteLeft() - 15;
        int maxHeight = MainLayoutPanel.getMainAreaHeight() + MainLayoutPanel.getMainAreaAbsoluteTop() - this.getAbsoluteTop() - 15;
        elementStyle.setPropertyPx("maxWidth", maxWidth);
        elementStyle.setPropertyPx("maxHeight", maxHeight);
    }

    protected abstract Element getResizeElement();

    private native void addResizeHandler(String id) /*-{
        var movableResizablePanel = this;
        new $wnd.ResizeSensor($doc.getElementById(id), function() {
            movableResizablePanel.@esac.archive.esasky.cl.web.client.view.common.MovableResizablePanel::onResize()();
        });
    }-*/;
}
