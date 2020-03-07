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