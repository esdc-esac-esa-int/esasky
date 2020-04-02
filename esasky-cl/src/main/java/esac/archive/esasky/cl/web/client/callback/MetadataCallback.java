package esac.archive.esasky.cl.web.client.callback;

import java.util.HashMap;
import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.http.client.Response;

import esac.archive.esasky.cl.web.client.model.TapRowList;
import esac.archive.esasky.cl.web.client.model.entities.GeneralEntityInterface;
import esac.archive.esasky.cl.web.client.presenter.ResultsPresenter.TapRowListMapper;
import esac.archive.esasky.cl.web.client.view.resultspanel.ITablePanel;

public class MetadataCallback extends JsonRequestCallback {

	private GeneralEntityInterface entity;
	private ITablePanel tablePanel;
	private static HashMap<String, Long> latestUpdates = new HashMap<String, Long>();
	private long timecall;
	private OnComplete onComplete;
	long startTime;

	public interface OnComplete{
		public void onComplete();
	}

	public MetadataCallback(ITablePanel tablePanel, String adql,
			String progressIndicatorMessage, OnComplete onComplete) {
		this(tablePanel, adql, progressIndicatorMessage);
		this.onComplete = onComplete;
	}
	
	public MetadataCallback(ITablePanel tablePanel, String adql,
			String progressIndicatorMessage) {
		super(progressIndicatorMessage, adql);
		this.entity = tablePanel.getEntity();
		this.tablePanel = tablePanel;
		timecall = System.currentTimeMillis();
		latestUpdates.put(tablePanel.getEsaSkyUniqID(), timecall);
		startTime = System.currentTimeMillis();
	}

	@Override
	protected void onSuccess(final Response response) {
		Scheduler.get().scheduleFinally(new ScheduledCommand() {

			@Override
			public void execute() {
				if (timecall < latestUpdates.get(tablePanel.getEsaSkyUniqID())) {
					Log.warn(this.getClass().getSimpleName() + " discarded server answer with timecall="
							+ timecall + " , dif:" + (latestUpdates.get(tablePanel.getEsaSkyUniqID()) - timecall));
					return;
				}
				if(tablePanel.hasBeenClosed()) {
					return;
				}

				TapRowListMapper mapper = GWT.create(TapRowListMapper.class);
				TapRowList rowList = mapper.read(response.getText());

				entity.addShapes(rowList, null);
				entity.setMetadata(rowList);
//				
//				if(entity.getContext() == EntityContext.EXT_TAP) {
//					
//					((ExtTapEntity) entity).setDescriptorMetaData();
//					
//					if(((ExtTapEntity) entity).hasReachedFovLimit()){
//			
//						if(onComplete != null) {
//							onComplete.onComplete();
//						}
//						return;
//					}
//				}
//
//				List<TableRow> tabRowList = TapToMmiDataConverter.convertTapToMMIData(rowList,
//						entity.getDescriptor());
//				long receivedTime = System.currentTimeMillis();
//				Log.debug("Receive time " + Long.toString(receivedTime - startTime ));
////				
//				tablePanel.insertData(tabRowList, null);
////				Log.debug("Insert time " + Long.toString(System.currentTimeMillis() - receivedTime ));
//				
//				// used by download CSV, VOTABLE
//				tablePanel.setADQLQueryUrl(adql);
//				
				if(onComplete != null) {
					onComplete.onComplete();
				}
			}
		});
	}
}
