package esac.archive.esasky.cl.web.client.view.resultspanel.table;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.CustomScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class ESASkyCustomScrollPanel extends CustomScrollPanel {

	public ESASkyCustomScrollPanel(Widget child) {
		super(child);
	}

	@Override
	public void onResize() {
		super.onResize();
		setScrollbarHeight();
	}

	public static void setScrollbarHeight() {

		final Element[] scrollParents = getElementByClassName("com-google-gwt-user-client-ui-CustomScrollPanel-Style-customScrollPanel");
		for(int i = 0; i < scrollParents.length; i++) {
			final Element scrollParent = scrollParents[i];
			if (scrollParent != null && scrollParent.getAbsoluteTop() != 0) {
				final Element scrollFixed = ((Element)scrollParent.getChild(scrollParent.getChildCount() - 1 ));
				if(!((Element)scrollFixed.getChild(0)).getStyle().getTop().equals(scrollParent.getAbsoluteTop() + "px")) {
					((Element)scrollFixed.getChild(0)).getStyle().setTop(scrollParent.getAbsoluteTop(), Unit.PX);
					scrollFixed.getStyle().setPosition(Position.FIXED);
				}
			}
		}
	}

	private native static Element [] getElementByClassName(String name) /*-{
	    	return $doc.getElementsByClassName(name);
	    }-*/;

}
