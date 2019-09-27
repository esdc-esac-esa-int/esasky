package esac.archive.esasky.cl.web.client.view.resultspanel.table;

import java.util.UUID;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.CustomScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.DataPanelAnimationCompleteEvent;
import esac.archive.esasky.cl.web.client.event.DataPanelAnimationCompleteEventHandler;

public class ESASkyCustomScrollPanel extends CustomScrollPanel {

	private String id = UUID.randomUUID().toString();
	
	public ESASkyCustomScrollPanel(Widget child) {
		super(child);
		getElement().setId(id);
		
		CommonEventBus.getEventBus().addHandler(DataPanelAnimationCompleteEvent.TYPE, new DataPanelAnimationCompleteEventHandler() {
			
			@Override
			public void onDataPanelAnimationComplete(DataPanelAnimationCompleteEvent event) {
				setScrollbarHeight();
			}
		});
	}

	@Override
	public void onResize() {
		super.onResize();
		setScrollbarHeight();
	}

	public void setScrollbarHeight() {
		final Element scrollParent = Document.get().getElementById(id);
		if (scrollParent != null && scrollParent.getAbsoluteTop() != 0) {
			final Element scrollFixed = ((Element)scrollParent.getChild(scrollParent.getChildCount() - 1 ));
			if(!((Element)scrollFixed.getChild(0)).getStyle().getTop().equals(scrollParent.getAbsoluteTop() + "px")) {
				((Element)scrollFixed.getChild(0)).getStyle().setTop(scrollParent.getAbsoluteTop(), Unit.PX);
				scrollFixed.getStyle().setPosition(Position.FIXED);
			}
		}
	}
}
