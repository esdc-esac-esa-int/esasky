package esac.archive.esasky.cl.web.client.callback;

import java.util.LinkedList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.http.client.Response;

import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.TreeMapNewDataEvent;
import esac.archive.esasky.cl.web.client.model.TapRowList;
import esac.archive.esasky.cl.web.client.presenter.ResultsPresenter.TapRowListMapper;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.cl.web.client.utility.CoordinateUtils;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;

public class ExtTapCheckCallback extends JsonRequestCallback {

	private long timecall;
	private CountStatus countStatus;
	private IDescriptor descriptor;

	
	public ExtTapCheckCallback(String adql, IDescriptor descriptor, CountStatus countStatus,
			String progressIndicatorMessage) {
		super(progressIndicatorMessage, adql);
		timecall = System.currentTimeMillis();
		this.countStatus = countStatus;
		this.descriptor = descriptor;
	}

	@Override
	protected void onSuccess(final Response response) {
		Scheduler.get().scheduleFinally(new ScheduledCommand() {
			
			@Override
			public void execute() {
			 if(countStatus.getUpdateTime(descriptor.getMission()) != null 
		        		&& countStatus.getUpdateTime(descriptor.getMission()) > timecall) {
		        	Log.warn(this.getClass().getSimpleName() + " discarded server answer with timecall="
		        			+ timecall + " , dif:" + (countStatus.getUpdateTime(descriptor.getMission()) - timecall));
		        	return;
		        }
		        
		        countStatus.setUpdateTime(descriptor.getMission(), timecall);
				TapRowListMapper mapper = GWT.create(TapRowListMapper.class);
				TapRowList rowList = mapper.read(response.getText());
	
				int value = rowList.getData().size();
				SkyViewPosition skyViewPosition = CoordinateUtils.getCenterCoordinateInJ2000();
	
		        countStatus.setCountDetails(descriptor.getMission(), value, System.currentTimeMillis(),
		        		skyViewPosition);
		        Log.debug(this.getClass().getSimpleName() + " " + descriptor.getMission() + ": [" + value
		                + "] results found");
		        countStatus.updateCount();
	        	List<IDescriptor> descriptors = new LinkedList<IDescriptor>();
	        	descriptors.add(descriptor);
	        	List<Integer> counts = new LinkedList<Integer>();
	        	counts.add(countStatus.getCount(descriptor.getMission()));
		        CommonEventBus.getEventBus().fireEvent(new TreeMapNewDataEvent(descriptors, counts));
			}
		});
	}
}
