package esac.archive.esasky.cl.web.client.status;

import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.RootPanel;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteCoordinatesChangedEvent;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPushEvent;
import esac.archive.esasky.cl.web.client.event.UserIdleEvent;

public class ActivityStatus {
	private static ActivityStatus instance;
	private static final int IDLE_THRESHOLD = 1000 * 60;
	private boolean isUserActive;
	private final Timer activityStoppedTimer = new Timer() {
		
		@Override
		public void run() {
			onUserSleep();
		}
	};

    public ActivityStatus() {
		CommonEventBus.getEventBus().addHandler(ProgressIndicatorPushEvent.TYPE, pushEvent -> resetIdleTimer());
		CommonEventBus.getEventBus().addHandler(AladinLiteCoordinatesChangedEvent.TYPE, changeEvent -> resetIdleTimer());
		RootPanel.get().addDomHandler(event -> resetIdleTimer(), MouseMoveEvent.getType());
		RootPanel.get().addDomHandler(event -> resetIdleTimer(), KeyPressEvent.getType());
		activityStoppedTimer.scheduleRepeating(IDLE_THRESHOLD);
	}
	
	public static ActivityStatus getInstance() {
		if(instance == null) {
			instance = new ActivityStatus();
		}
		return instance;
	}
	
	public boolean isUserActive() {
		return isUserActive;
	}

	private void resetIdleTimer() {
		if (!isUserActive) {
			onUserWakeup();
		}

		isUserActive = true;
		activityStoppedTimer.cancel();
        activityStoppedTimer.scheduleRepeating(IDLE_THRESHOLD);
	}

	private void onUserWakeup() {
		CommonEventBus.getEventBus().fireEvent(new UserIdleEvent(false));
	}

	private void onUserSleep() {
		isUserActive = false;
		CommonEventBus.getEventBus().fireEvent(new UserIdleEvent(true));
		activityStoppedTimer.cancel();
	}

}