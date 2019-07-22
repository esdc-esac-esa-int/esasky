package esac.archive.esasky.cl.web.client.status;

import com.google.gwt.user.client.Timer;

import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteCoordinatesChangedEvent;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteCoordinatesChangedEventHandler;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPushEvent;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPushEventHandler;

public class ActivityStatus {
	private static ActivityStatus instance;
	private boolean anyActivityDuringTheLastMinute;
	private Timer activityStoppedTimer = new Timer() {
		
		@Override
		public void run() {
			anyActivityDuringTheLastMinute = false;
		}
	};
	
	public ActivityStatus() {
		CommonEventBus.getEventBus().addHandler(ProgressIndicatorPushEvent.TYPE,
				new ProgressIndicatorPushEventHandler() {
			
			@Override
			public void onPushEvent(ProgressIndicatorPushEvent pushEvent) {
				anyActivityDuringTheLastMinute = true;
				activityStoppedTimer.scheduleRepeating(1000*60);
			}
		});
		
		CommonEventBus.getEventBus().addHandler(AladinLiteCoordinatesChangedEvent.TYPE, new AladinLiteCoordinatesChangedEventHandler() {

			@Override
			public void onCoordsChanged(AladinLiteCoordinatesChangedEvent changeEvent) {
				anyActivityDuringTheLastMinute = true;
				activityStoppedTimer.scheduleRepeating(1000*60);
			}

		});
	}
	
	public static ActivityStatus getInstance() {
		if(instance == null) {
			instance = new ActivityStatus();
		}
		return instance;
	}
	
	public boolean anyActivityDuringTheLastMinute() {
		return anyActivityDuringTheLastMinute;
	}
}