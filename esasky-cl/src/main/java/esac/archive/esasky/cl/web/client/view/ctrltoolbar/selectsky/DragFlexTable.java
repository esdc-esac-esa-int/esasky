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
