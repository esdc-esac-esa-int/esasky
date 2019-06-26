package esac.archive.esasky.cl.web.client.view.resultspanel.tab;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;

import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.DataPanelResizeEvent;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPopEvent;
import esac.archive.esasky.cl.web.client.status.GUISessionStatus;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;

public class DataPanelDraggablePanel extends FlowPanel {
	private ResizeDataPanelTimer resizeDataPanelTimer = new ResizeDataPanelTimer();

	private boolean isMouseDown = false;
	private boolean isBeingDragged = false;
	private final int MIN_DISTANCE_FROM_BOTTOM = 40;
	private final int MIN_DISTANCE_FROM_TOP = 80;
	
	public DataPanelDraggablePanel() {
		super();
		addStyleName("dataPanelDraggablePanel");
		DOM.sinkEvents(getElement(), Event.ONMOUSEDOWN | Event.ONMOUSEMOVE
				| Event.ONMOUSEUP | Event.ONMOUSEOVER | Event.ONTOUCHSTART | Event.ONTOUCHMOVE);
		DOM.sinkEvents(RootPanel.get().getElement(), Event.ONMOUSEUP | Event.ONTOUCHEND | Event.ONTOUCHCANCEL);
	}

	@Override
	public void onBrowserEvent(Event event) {
		final int eventType = DOM.eventGetType(event);
		if (Event.ONMOUSEOVER == eventType) {
			getElement().getStyle().setProperty("cursor", "s-resize");
		}
		if (Event.ONMOUSEDOWN == eventType || Event.ONTOUCHSTART == eventType) {
			if (!isMouseDown) {
				isMouseDown = true;
				DOM.setCapture(getElement());
				event.stopPropagation();
				event.preventDefault();
			}
		} else if (Event.ONMOUSEMOVE == eventType || Event.ONTOUCHMOVE == eventType) {
			if (isMouseDown) {
				int positionY = eventType == Event.ONMOUSEMOVE ? event.getClientY() : event.getTargetTouches().get(0).getClientY();
				isBeingDragged = true;
				getElement().getStyle().setProperty("cursor", "s-resize");
				int height = MainLayoutPanel.getMainAreaHeight() + MainLayoutPanel.getMainAreaAbsoluteTop() - positionY + getOffsetHeight() / 2;
				if(height < MIN_DISTANCE_FROM_BOTTOM) {
					height = MIN_DISTANCE_FROM_BOTTOM;
				}
				if(height > MainLayoutPanel.getMainAreaHeight() - MIN_DISTANCE_FROM_TOP) {
					height = MainLayoutPanel.getMainAreaHeight() - MIN_DISTANCE_FROM_TOP;
				}
				resizeDataPanelTimer.setNewHeight(height);
			}
		} else if (Event.ONMOUSEUP == eventType || Event.ONTOUCHEND == eventType || Event.ONTOUCHCANCEL== eventType) {
			DOM.releaseCapture(getElement());
			if (isBeingDragged) {
				GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_Tab_Resize, GoogleAnalytics.ACT_Tab_Resize, "");
				resizeDataPanelTimer.cancel();
				resizeDataPanelTimer.setNewExpandedDataPanelSize = true;
				resizeDataPanelTimer.schedule(1);
			}
			isMouseDown = false;
		}
	}
	
	public boolean isBeingDragged() {
		return isBeingDragged;
	}
	
	public class ResizeDataPanelTimer extends Timer {
		private int height;
		private com.google.gwt.dom.client.Style resultPanelStyle;
		private boolean setNewExpandedDataPanelSize = true;
		
		public ResizeDataPanelTimer() {
			super();
			this.resultPanelStyle = DOM.getElementById("resultPanel").getStyle();
		}
		@Override
		public void run() {
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				
				@Override
				public void execute() {
					if(isGoingToChangeSize()) {
						isBeingDragged = false;
						resultPanelStyle.setHeight(height, Unit.PX);
						CommonEventBus.getEventBus().fireEvent(new DataPanelResizeEvent(height));
					}
					if(setNewExpandedDataPanelSize) {
						GUISessionStatus.setCurrentHeightForExpandedDataPanel(height);
					}
					GUISessionStatus.setDataPanelOpen(height > MIN_DISTANCE_FROM_BOTTOM);
					CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPopEvent("ResizeDataPanel"));
				}
			});
		}
		
		public void setNewHeight(int newHeight) {
			if(this.height != newHeight) {
				this.height = newHeight;
				setNewExpandedDataPanelSize = false;
				run();
			}
		}
		
		public boolean isGoingToChangeSize() {
			return GUISessionStatus.getCurrentHeightForExpandedDataPanel() != height && isBeingDragged;
		}
	}
}