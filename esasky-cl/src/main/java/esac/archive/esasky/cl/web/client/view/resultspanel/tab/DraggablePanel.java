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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;

import esac.archive.esasky.cl.web.client.view.banner.Banner.Side;

public class DraggablePanel extends FlowPanel {
	private ResizeTimer resizeTimer = new ResizeTimer();

	private boolean isMouseDown = false;
	private boolean isBeingDragged = false;
	private final int MIN_DISTANCE_FROM_BOTTOM = 40;
	private final int MIN_DISTANCE_FROM_TOP = 80;
	private final Element elementToResize;
	private final Side side;
	
	public DraggablePanel(Element elementToResize, Side side) {
		super();
		this.elementToResize = elementToResize;
		this.side = side;
		DOM.sinkEvents(getElement(), Event.ONMOUSEDOWN | Event.ONMOUSEMOVE
				| Event.ONMOUSEUP | Event.ONMOUSEOVER | Event.ONTOUCHSTART | Event.ONTOUCHMOVE);
		DOM.sinkEvents(RootPanel.get().getElement(), Event.ONMOUSEUP | Event.ONTOUCHEND | Event.ONTOUCHCANCEL);
	}

	@Override
	public void onBrowserEvent(Event event) {
		final int eventType = DOM.eventGetType(event);
		if (Event.ONMOUSEOVER == eventType) {
			if(side == Side.LEFT || side == Side.RIGHT) {
				getElement().getStyle().setProperty("cursor", "e-resize");
			} else {
				getElement().getStyle().setProperty("cursor", "s-resize");
			}
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
				isBeingDragged = true;
				if(side == Side.LEFT) {
					int positionX = eventType == Event.ONMOUSEMOVE ? event.getClientX() : event.getTargetTouches().get(0).getClientX();
					getElement().getStyle().setProperty("cursor", "e-resize");
					int width = positionX;
					if(width < MIN_DISTANCE_FROM_BOTTOM) {
						width = MIN_DISTANCE_FROM_BOTTOM;
					}
					if(width > Window.getClientWidth() - MIN_DISTANCE_FROM_TOP) {
						width = Window.getClientWidth() - MIN_DISTANCE_FROM_TOP;
					}
					resizeTimer.setNewSize(width);
				} else if (side == Side.TOP) {
					int positionY = eventType == Event.ONMOUSEMOVE ? event.getClientY() : event.getTargetTouches().get(0).getClientY();
					getElement().getStyle().setProperty("cursor", "s-resize");
					int height = positionY;
					if(height < MIN_DISTANCE_FROM_BOTTOM) {
						height = MIN_DISTANCE_FROM_BOTTOM;
					}
					if(height > Window.getClientHeight() - MIN_DISTANCE_FROM_TOP) {
						height = Window.getClientHeight() - MIN_DISTANCE_FROM_TOP;
					}
					resizeTimer.setNewSize(height);
				} else if (side == Side.RIGHT) {
					int positionX = eventType == Event.ONMOUSEMOVE ? event.getClientX() : event.getTargetTouches().get(0).getClientX();
					getElement().getStyle().setProperty("cursor", "e-resize");
					int width = Window.getClientWidth() - positionX;
					if(width < MIN_DISTANCE_FROM_BOTTOM) {
						width = MIN_DISTANCE_FROM_BOTTOM;
					}
					if(width > Window.getClientWidth() - MIN_DISTANCE_FROM_TOP) {
						width = Window.getClientWidth() - MIN_DISTANCE_FROM_TOP;
					}
					resizeTimer.setNewSize(width);
				} else if (side == Side.BOTTOM) {
					int positionY = eventType == Event.ONMOUSEMOVE ? event.getClientY() : event.getTargetTouches().get(0).getClientY();
					getElement().getStyle().setProperty("cursor", "s-resize");
					int height = Window.getClientHeight() - positionY;
					if(height < MIN_DISTANCE_FROM_BOTTOM) {
						height = MIN_DISTANCE_FROM_BOTTOM;
					}
					if(height > Window.getClientHeight() - MIN_DISTANCE_FROM_TOP) {
						height = Window.getClientHeight() - MIN_DISTANCE_FROM_TOP;
					}
					resizeTimer.setNewSize(height);
				}
			}
		} else if (Event.ONMOUSEUP == eventType || Event.ONTOUCHEND == eventType || Event.ONTOUCHCANCEL== eventType) {
			DOM.releaseCapture(getElement());
			if (isBeingDragged) {
//				GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_Tab_Resize, GoogleAnalytics.ACT_Tab_Resize, "");
				resizeTimer.schedule(1);
			}
			isMouseDown = false;
		}
	}
	
	public class ResizeTimer extends Timer {
		private int size;
//		private com.google.gwt.dom.client.Style resultPanelStyle;
		
		public ResizeTimer() {
			super();
//			this.resultPanelStyle = DOM.getElementById("resultPanel").getStyle();
		}
		@Override
		public void run() {
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				
				@Override
				public void execute() {
					isBeingDragged = false;
					
//					resultPanelStyle.setHeight(height, Unit.PX);
					if(side == Side.LEFT || side == Side.RIGHT) {
						elementToResize.getStyle().setWidth(size, Unit.PX);
					} else {
						elementToResize.getStyle().setHeight(size, Unit.PX);
					}
				}
			});
		}
		
		public void setNewSize(int newSize) {
			if(this.size != newSize) {
				this.size = newSize;
				if(!isRunning()) {
					schedule(10);
				}
			}
		}
	}
	
}