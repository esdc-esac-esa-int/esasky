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

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public interface IGlass{
    default void addGlassStyle(Widget glassPanel) {
        glassPanel.getElement().setClassName("gwt-PopupPanelGlass");
        glassPanel.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
        glassPanel.getElement().getStyle().setLeft(0, Style.Unit.PX);
        glassPanel.getElement().getStyle().setTop(0, Style.Unit.PX);
        glassPanel.getElement().getStyle().setWidth(100, Style.Unit.PCT);
        glassPanel.getElement().getStyle().setHeight(100, Style.Unit.PCT);
        glassPanel.getElement().getStyle().setZIndex(1199);
    }

    default void showGlass() {
        getGlass().removeStyleName("displayNone");

    }

    default void hideGlass() {
        getGlass().addStyleName("displayNone");
    }

    Panel getGlass();

}
