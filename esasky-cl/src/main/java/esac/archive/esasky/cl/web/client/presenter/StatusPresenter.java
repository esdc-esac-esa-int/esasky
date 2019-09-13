package esac.archive.esasky.cl.web.client.presenter;


import java.util.LinkedList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.Timer;

import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPopEvent;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPopEventHandler;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPushEvent;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPushEventHandler;
import esac.archive.esasky.cl.web.client.event.hips.HipsChangeEvent;
import esac.archive.esasky.cl.web.client.event.hips.HipsChangeEventHandler;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.utility.UrlUtils;

public class StatusPresenter {

	private View view;

	private final List<Status> currentStatuses = new LinkedList<Status>();
	private Status statusCurrentyShowing;

	private final int DELAY_UNTIL_NEXT_MESSAGE_IS_SHOWN_IN_MILLIS = 2000;
	private final int POP_TIMEOUT_DELAY_IN_MILLIS = 120000;
	private StatusTimer statusTimer = new StatusTimer();

	private class StatusTimer extends Timer{

		@Override
		public void run() {
			goToNextMessage();
		}
	}

	public interface View {
		void setStatusMessage(String statusMessage, boolean isImportant);
		void removeStatusMessage();
		void recalculateSize();
	}

	public StatusPresenter(final View inputView) {
		this.view = inputView;
		bind();
	}

	private void bind() {

		CommonEventBus.getEventBus().addHandler(ProgressIndicatorPushEvent.TYPE,
				new ProgressIndicatorPushEventHandler() {

			@Override
			public void onPushEvent(final ProgressIndicatorPushEvent pushEvent) {
				removeMessage(pushEvent.getId());
				final Status newStatus = new Status(pushEvent.getId(), pushEvent.getMessage(), pushEvent.isImportant());
				currentStatuses.add(newStatus);
				statusTimer.run();
				statusTimer.scheduleRepeating(DELAY_UNTIL_NEXT_MESSAGE_IS_SHOWN_IN_MILLIS);
				new Timer() {

					@Override
					public void run() {
						if(currentStatuses.contains(newStatus)) {
							currentStatuses.remove(newStatus);
							String errorMessage = "Did not receive pop event for status: " + newStatus.id 
									+ " with message: " + newStatus.message
									+ " after " + POP_TIMEOUT_DELAY_IN_MILLIS + " milliseconds. Current url is: " + UrlUtils.getUrlForCurrentState();
							
							if(!pushEvent.getGoogleAnalyticsErrorMessage().isEmpty()) {
								errorMessage += " More information: " + pushEvent.getGoogleAnalyticsErrorMessage();
							}
							Log.error(errorMessage);
							GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_Header_Status, GoogleAnalytics.ACT_Header_Status_Error, errorMessage);
						}
					}
				}.schedule(POP_TIMEOUT_DELAY_IN_MILLIS);;
			}

		});

		CommonEventBus.getEventBus().addHandler(ProgressIndicatorPopEvent.TYPE,
				new ProgressIndicatorPopEventHandler() {

			@Override
			public void onPopEvent(final ProgressIndicatorPopEvent popEvent) {
				removeMessage(popEvent.getId());
			}
		});
		
		CommonEventBus.getEventBus().addHandler(HipsChangeEvent.TYPE, new HipsChangeEventHandler() {

			@Override
			public void onChangeEvent(final HipsChangeEvent changeEvent) {
				view.recalculateSize();
			}
		});
	}

	private void removeMessage(String id) {
		for(Status status: currentStatuses) {
			if(status.id.equals(id)) {
				currentStatuses.remove(status);
				if(status.equals(statusCurrentyShowing)) {
					statusCurrentyShowing = null;
					goToNextMessage();
				}
				break;
			}
		}
	}

	private void goToNextMessage() {
		if(currentStatuses.size() == 0) {
			view.removeStatusMessage();
			statusTimer.cancel();
			return;
		}
		if(statusCurrentyShowing == null) {
			setMessageAsCurrent(currentStatuses.get(0));
			return;
		}
		if(!findNextStatus().id.equals(statusCurrentyShowing.id)) {
			setMessageAsCurrent(findNextStatus());
		}
	}

	private void setMessageAsCurrent(Status status) {
		view.setStatusMessage(status.message, status.isImportant);
		statusCurrentyShowing = status;
	}

	private Status findNextStatus() {
		int indexOfCurrentlyShownStatus = currentStatuses.indexOf(statusCurrentyShowing);
		int indexOfNextStatus = (indexOfCurrentlyShownStatus + 1) % currentStatuses.size();
		return currentStatuses.get(indexOfNextStatus);
	}
	
	private class Status{
		public final String id;
		private final String message;
		private final boolean isImportant;
		private Status(String id, String message, boolean isImportant) {
			this.id = id;
			this.message = message;
			this.isImportant = isImportant;
		}
	}
}
