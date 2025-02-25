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

package esac.archive.esasky.cl.web.client.view.ctrltoolbar.selectsky;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;

public class DragFlexTable extends FlexTable {

    public DragFlexTable() {
        super();
        this.getElement().setId("skies");
    }

    @Override
    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);
    }

    public void insertItem(Widget widget) {
        this.setWidget(getRowCount(), 0, widget);
    }

    private static Node findNearestParentNodeByType(Node node, String nodeType) {
        while ((node != null)) {
            if (Element.is(node)) {
                Element elem = Element.as(node);

                String tagName = elem.getTagName();

                if (nodeType.equalsIgnoreCase(tagName)) {
                    return elem.cast();
                }

            }
            node = node.getParentNode();
        }
        return null;
    }

    public void clearTable() {
        this.removeAllRows();
    }

    public void removeSky(SkyRow sky) {
        TableRowElement tableRow = (TableRowElement) findNearestParentNodeByType(sky.getElement(), "tr");
        if(tableRow != null) {
        	tableRow.removeFromParent();
        }
        
        if(sky.isSelected()){
            for (int i = 0; i < this.getRowCount(); i++) {
                Widget widget = this.getWidget(i, 0);
                if (widget instanceof SkyRow) {
                    ((SkyRow) widget).setSelected();
                    return;
                }
            }
        }
    }
    
}
