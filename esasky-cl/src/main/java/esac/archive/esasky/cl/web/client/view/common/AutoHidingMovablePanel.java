package esac.archive.esasky.cl.web.client.view.common;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;

import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.ClosingObserver;

public class AutoHidingMovablePanel extends MovablePanel{
	private boolean isShowing;
	
	public AutoHidingMovablePanel(String googleEventCategoryForMoveOperation) {
		super(googleEventCategoryForMoveOperation, true);
		this.getElement().getStyle().setPosition(Position.ABSOLUTE);
		addHideOnEscapeKeyBehavior(new OnKeyPress() {
            
            @Override
            public void onEscapeKey() {
                hide();
            }
        });
	}
	
	public void show() {
		MainLayoutPanel.addElementToMainArea(this);
		isShowing = true;
		updateHandlersEsaSky();
		ensureDialogFitsInsideWindow();
	}

	public void hide() {
		for(ClosingObserver onClose : observers) {
			onClose.onClose();
		}
		MainLayoutPanel.removeElementFromMainArea(this);
		isShowing = false;
		updateHandlersEsaSky();
	}
	
	public boolean isShowing() {
		return isShowing;
	}
	
	/*Auto hide functionality adapted from source code of
	 * GWT PopupPanel
	 * */
	private void previewNativeEventEsaSky(NativePreviewEvent event) {
		// If the event has been canceled or consumed, ignore it
		if (event.isCanceled() || (event.isConsumed())) {
			// We need to ensure that we cancel the event even if its been consumed so
			// that popups lower on the stack do not auto hide
			return;
		}

		// If the event targets the popup or the partner, consume it
		Event nativeEvent = Event.as(event.getNativeEvent());
		boolean eventTargetsPopupOrPartner = eventTargetsPopupEsaSky(nativeEvent)
				|| eventTargetsPartnerEsaSky(nativeEvent);

		// Switch on the event type
		int type = nativeEvent.getTypeInt();
		switch (type) {
		case Event.ONMOUSEDOWN:
		case Event.ONTOUCHSTART:
			// Don't eat events if event capture is enabled, as this can
			// interfere with dialog dragging, for example.
			if (DOM.getCaptureElement() != null) {
				event.consume();
				return;
			}

			if (!eventTargetsPopupOrPartner) {
				hide();
				return;
			}
			break;
		}
	}
	private boolean eventTargetsPopupEsaSky(NativeEvent event) {
		EventTarget target = event.getEventTarget();
		if (Element.is(target)) {
			return getElement().isOrHasChild(Element.as(target));
		}
		return false;
	}
	private boolean eventTargetsPartnerEsaSky(NativeEvent event) {
		if (autoHidePartners == null) {
			return false;
		}

		EventTarget target = event.getEventTarget();
		if (Element.is(target)) {
			for (Element elem : autoHidePartners) {
				if (elem.isOrHasChild(Element.as(target))) {
					return true;
				}
			}
		}
		return false;
	}
	private HandlerRegistration nativePreviewHandlerRegistration;

	private void updateHandlersEsaSky() {
		// Remove any existing handlers.
		if (nativePreviewHandlerRegistration != null) {
			nativePreviewHandlerRegistration.removeHandler();
			nativePreviewHandlerRegistration = null;
		}

		// Create handlers if showing.
		if (isShowing) {
			nativePreviewHandlerRegistration = Event.addNativePreviewHandler(new NativePreviewHandler() {
				public void onPreviewNativeEvent(NativePreviewEvent event) {
					previewNativeEventEsaSky(event);
				}
			});
		}
	}

	private List<Element> autoHidePartners;
	/**
	 * Mouse events that occur within an autoHide partner will not hide a panel
	 * set to autoHide.
	 *
	 * @param partner the auto hide partner to add
	 */
	public void addAutoHidePartner(Element partner) {
		assert partner != null : "partner cannot be null";
		if (autoHidePartners == null) {
			autoHidePartners = new ArrayList<Element>();
		}
		autoHidePartners.add(partner);
	}
	

}
