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

package esac.archive.esasky.cl.web.client.view.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;

public class CollapsablePanel extends FlowPanel {

	private int height = 100;
    private boolean isCollapsed;

    private final Resources resources;
    private final CssResource style;
    

    public interface Resources extends ClientBundle {
        @Source("collapsablePanel.css")
        @CssResource.NotStrict
        CssResource style();
    }

    public CollapsablePanel(boolean isCollapsed, int heightInPx) {
        this.resources = GWT.create(Resources.class);
        this.style = this.resources.style();
        this.style.ensureInjected();
        this.height = heightInPx;

        if (isCollapsed) {
            collapse();
        }

        this.isCollapsed = isCollapsed;
        this.getElement().setId("collapsablePanel");

    }

    public void collapse() {
        addStyleName("collapsed");
        removeStyleName("expanded");
        getElement().getStyle().setHeight(0, Unit.PX);
        this.isCollapsed = true;
    }

    public void expand() {
        removeStyleName("collapsed");
        addStyleName("expanded");
        getElement().getStyle().setHeight(this.height, Unit.PX);
        this.isCollapsed = false;
    }


    public void toggle() {
        if (isCollapsed) {
            expand();
        } else {
            collapse();
        }
    }

    public boolean isCollapsed() {
        return isCollapsed;
    }
}
