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

package esac.archive.esasky.cl.web.client.view.header;


import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.banner.ServerProblemFoundEvent;
import esac.archive.esasky.cl.web.client.event.banner.ServerProblemFoundEventHandler;
import esac.archive.esasky.cl.web.client.event.banner.ServerProblemSolvedEvent;
import esac.archive.esasky.cl.web.client.event.banner.ServerProblemSolvedEventHandler;
import esac.archive.esasky.cl.web.client.presenter.StatusPresenter.View;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.LoadingSpinner;
import esac.archive.esasky.cl.web.client.view.common.icons.Icons;

public class StatusPanel extends Composite implements View{

	private static Resources resources = GWT.create(Resources.class);
	private CssResource style;

	private FlowPanel statusLabel = new FlowPanel();
	private LoadingSpinner loadingSpinner = new LoadingSpinner(false);
	private Image exclamationImage;
	private final int ANIMATION_TIME = 500;
	private StatusTimer timer = new StatusTimer();
	private boolean isImportant;

	public interface Resources extends ClientBundle {

		@Source("statusPanel.css")
		@CssResource.NotStrict
		CssResource style();
	}

	private final Widget statusPanel = createStatusPanel();
	
	public StatusPanel() {
		style = resources.style();
		style.ensureInjected();
		
		initView();
	}
	
	private void initView() {
		MainLayoutPanel.addMainAreaResizeHandler(new ResizeHandler() {
			
			@Override
			public void onResize(ResizeEvent arg0) {
				resize();
			}
		});
		
		CommonEventBus.getEventBus().addHandler(ServerProblemFoundEvent.TYPE, new ServerProblemFoundEventHandler() {
			
			@Override
			public void onEvent(ServerProblemFoundEvent event) {
				resize();
			}
		});
		CommonEventBus.getEventBus().addHandler(ServerProblemSolvedEvent.TYPE, new ServerProblemSolvedEventHandler() {
			
			@Override
			public void onEvent(ServerProblemSolvedEvent event) {
				resize();
			}
		});
		
		initWidget(statusPanel);
	}
	
	@Override
	public void onAttach() {
		resize();
	}

	private void resize() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				int maxAvailableWidth = MainLayoutPanel.getMainAreaWidth() 
						- Document.get().getElementById("coordinateContainer").getOffsetWidth() 
						- Document.get().getElementById("selectedSky").getOffsetWidth()
						- Document.get().getElementById("rightSideHeader").getOffsetWidth()
						- 20;
				if(maxAvailableWidth < 0) {
					maxAvailableWidth = 0;
					loadingSpinner.addStyleName("header__statusPanel__invisibleSpinner");
				} else {
					loadingSpinner.removeStyleName("header__statusPanel__invisibleSpinner");
				}
				statusPanel.setWidth(maxAvailableWidth + "px");
				
				int skyLabelWidth = Document.get().getElementById("selectedSky").getOffsetWidth();
				int skyLabelAndMargin = skyLabelWidth == 0 ? 10 : skyLabelWidth + 20;
				statusPanel.getElement().getStyle().setMarginLeft(
						Document.get().getElementById("coordinateContainer").getOffsetWidth() 
						+ (double) skyLabelAndMargin
						, Unit.PX);
				setResponsiveStyle();
			}
		});
	}
	
	private FlowPanel createStatusPanel() {
		FlowPanel statusPanel = new FlowPanel();
		statusPanel.addStyleName("header__statusPanel");

		loadingSpinner.addStyleName("header__statusPanel__spinnerContainer");
		loadingSpinner.setVisible(false);
		statusPanel.add(loadingSpinner);
		
		exclamationImage = new Image(Icons.getExclamationIcon());
		loadingSpinner.addStyleName("header__statusPanel__exclamationIcon");
		exclamationImage.setVisible(false);
		statusPanel.add(exclamationImage);

		statusLabel.addStyleName("header__statusPanel__label");
		statusPanel.add(statusLabel);

		return statusPanel;
	}


	private void animateInNewMessage(String message) {
		statusLabel.getElement().setInnerHTML(message);
		statusLabel.removeStyleName("statusPanel__label__animate-out");
		statusLabel.addStyleName("statusPanel__label__animate-in");
		if(isImportant) {
			statusLabel.addStyleName("statusPanel__label__important");
		} else {
			statusLabel.removeStyleName("statusPanel__label__important");
		}
	}
	
	@Override
	public void setStatusMessage(final String statusMessage, boolean isImportant) {
		this.isImportant = isImportant;
		if(isImportant) {
			loadingSpinner.setVisible(false);
			exclamationImage.setVisible(true);
		} else {
			exclamationImage.setVisible(false);
			loadingSpinner.setVisible(true);
		}
		if(statusLabel.getElement().getInnerHTML().isEmpty()) {
			animateInNewMessage(statusMessage);
			return;
		}
		statusLabel.removeStyleName("statusPanel__label__animate-in");
		statusLabel.addStyleName("statusPanel__label__animate-out");
		timer.setNewMessage(statusMessage);
	}

	@Override
	public void removeStatusMessage() {
		statusLabel.addStyleName("statusPanel__label__animate-out");
		statusLabel.removeStyleName("statusPanel__label__animate-in");
		loadingSpinner.setVisible(false);
		exclamationImage.setVisible(false);
		timer.setNewMessage("");
	}

	
	private class StatusTimer extends Timer {
		private String newMessage;
		@Override
		public void run() {
			if(newMessage.isEmpty()) {
				statusLabel.getElement().setInnerHTML("");
			} else {
				animateInNewMessage(newMessage);
			}
		}
		
		public void setNewMessage(String newMessage) {
			this.newMessage = newMessage;
			if(!isRunning()) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					
					@Override
					public void execute() {
						StatusTimer.this.schedule(ANIMATION_TIME);
					}
				});
			}
		}
	}

	@Override
	public void recalculateSize() {
		resize();
	};
	
	private void setResponsiveStyle() {
		if(statusPanel.getOffsetWidth() <= 150) {
			statusLabel.setVisible(false);
		} else {
			statusLabel.setVisible(true);
		}
	}
}
