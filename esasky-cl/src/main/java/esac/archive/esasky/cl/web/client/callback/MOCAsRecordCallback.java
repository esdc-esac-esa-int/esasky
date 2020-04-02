package esac.archive.esasky.cl.web.client.callback;

import java.util.HashMap;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.http.client.Response;

import esac.archive.esasky.cl.web.client.model.TapRowList;
import esac.archive.esasky.cl.web.client.model.entities.MOCEntity;
import esac.archive.esasky.cl.web.client.presenter.ResultsPresenter.TapRowListMapper;
import esac.archive.esasky.cl.web.client.view.resultspanel.ITablePanel;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;

public class MOCAsRecordCallback extends JsonRequestCallback {

	private MOCEntity mocEntity;
	private ITablePanel tablePanel;
	private static HashMap<String, Long> latestUpdates = new HashMap<String, Long>();
	private long timecall;
	private OnComplete onComplete;
	long startTime;

	public interface OnComplete{
		public void onComplete();
	}

	public MOCAsRecordCallback(ITablePanel tablePanel, String adql, MOCEntity mocEntity,
			String progressIndicatorMessage, OnComplete onComplete) {
		this(tablePanel, adql, mocEntity, progressIndicatorMessage);
		this.onComplete = onComplete;
	}
	
	public MOCAsRecordCallback(ITablePanel tablePanel, String adql,  MOCEntity mocEntity,
			String progressIndicatorMessage) {
		super(progressIndicatorMessage, adql);
		this.mocEntity = mocEntity;
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
				
				String text = response.getText();
				text = text.replace("{\"name\":\"moc\",\"datatype\":\"record\"}",""
						+ "{\"name\":\"" + EsaSkyConstants.Q3C_ORDER + "\",\"datatype\":\"INTEGER\"},"
						+ "{\"name\":\"" + EsaSkyConstants.Q3C_IPIX + "\",\"datatype\":\"INTEGER\"},"
						+ "{\"name\":\"" + EsaSkyConstants.Q3C_COUNT + "\",\"datatype\":\"INTEGER\"}");
				text = text.replace("\"(", "");
				text = text.replace(")\"", "");
				
				
				TapRowListMapper mapper = GWT.create(TapRowListMapper.class);
				TapRowList rowList = mapper.read(text);
				rowList.createMOCFromIntegers();
				
				mocEntity.addData(tablePanel, rowList.getMOC());
				
				
				if(onComplete != null) {
					onComplete.onComplete();
				}
			}
		});
	}
}
