package esac.archive.esasky.cl.web.client.callback;

import java.util.HashMap;
import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;

public class StandardCallback extends JsonRequestCallback {

	private static HashMap<String, Long> latestUpdates = new HashMap<String, Long>();
	private long timecall;
	private OnComplete onComplete;
	private OnFailure onFailure;
	private String category;

	public interface OnComplete{
		public void onComplete(String responseText);
	}
	public interface OnFailure{
		public void onFailure();
	}
	public StandardCallback(String category, String progressIndicatorMessage, OnComplete onComplete) {
		this(category, progressIndicatorMessage, onComplete, null);
	}

	/*
	 * Call with same category name will discard all answered that are not the latest*/
	public StandardCallback(String category, String progressIndicatorMessage, OnComplete onComplete, OnFailure onFailure) {
		super(progressIndicatorMessage, category);
		this.category = category;
		timecall = System.currentTimeMillis();
		latestUpdates.put(category, timecall);
		this.onComplete = onComplete;
		this.onFailure = onFailure;
	}
	
	@Override
	protected void onSuccess(final Response response) {
		Scheduler.get().scheduleFinally(new ScheduledCommand() {

			@Override
			public void execute() {
				if (timecall < latestUpdates.get(category)) {
					Log.warn(this.getClass().getSimpleName() + " discarded server answer for " + category + " with timecall="
							+ timecall + " , dif:" + (latestUpdates.get(category) - timecall));
					return;
				}
				if(onComplete != null) {
					onComplete.onComplete(response.getText());
				}
			}
		});
	}
	
	@Override
	public void onError(Request request, Throwable exception) {
		super.onError(request, exception);
		if(onFailure != null) {
			onFailure.onFailure();
		}
	}
}
