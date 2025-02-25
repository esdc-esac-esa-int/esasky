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

package esac.archive.esasky.cl.web.client.view.resultspanel.tab;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CommonResources;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author ESDC team Copyright (c) 2015- European Space Agency
 */
public class Tab extends SimplePanel {

	private Element inner;
	private DataPanelDraggablePanel draggableArea;


	public Tab(final Widget child) {
		super(Document.get().createDivElement());
		getElement().appendChild(this.inner = Document.get().createDivElement());

		draggableArea = new DataPanelDraggablePanel();
		FlowPanel horizontalLine = new FlowPanel();
		horizontalLine.addStyleName("draggableArea__horizontal");
		draggableArea.add(horizontalLine);
		FlowPanel secondHorizontalLine = new FlowPanel();
		secondHorizontalLine.addStyleName("draggableArea__horizontal-second");
		draggableArea.add(secondHorizontalLine);
		
		FlowPanel tabContainer = new FlowPanel();
		tabContainer.add(draggableArea);
		child.addStyleName("dataPanelTabChild");
		tabContainer.add(child);

		setWidget(tabContainer);
		setStyleName("scrollTabLayoutPanelTab");
		this.inner.setClassName("scrollTabLayoutPanelTabInner");

		getElement().addClassName(CommonResources.getInlineBlockStyle());
	}
	
	public HandlerRegistration addClickHandler(final ClickHandler handler) {
		return addDomHandler(handler, ClickEvent.getType());
	}

	public void setSelected(final boolean selected) {
		if (selected) {
			addStyleDependentName("selected");
		} else {
			removeStyleDependentName("selected");
		}
	}

	public boolean isBeingDragged() {
		return draggableArea.isBeingDragged();
	}
}