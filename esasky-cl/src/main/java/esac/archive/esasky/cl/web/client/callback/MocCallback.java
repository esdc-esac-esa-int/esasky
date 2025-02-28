/*
ESASky
Copyright (C) 2025 European Space Agency

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

package esac.archive.esasky.cl.web.client.callback;

import java.util.HashMap;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.http.client.Response;

import esac.archive.esasky.cl.web.client.model.entities.MOCEntity;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.cl.web.client.view.resultspanel.ITablePanel;

public class MocCallback extends JsonRequestCallback {

	private MOCEntity mocEntity;
	private ITablePanel tablePanel;
	private static HashMap<String, Long> latestUpdates = new HashMap<String, Long>();
	private long timecall;
	private OnComplete onComplete;
	long startTime;

	public interface OnComplete{
		public void onComplete();
	}

	public MocCallback(ITablePanel tablePanel, String adql, MOCEntity mocEntity,
			String progressIndicatorMessage, OnComplete onComplete) {
		this(tablePanel, adql, mocEntity, progressIndicatorMessage);
		this.onComplete = onComplete;
	}
	
	public MocCallback(ITablePanel tablePanel, String adql,  MOCEntity mocEntity,
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
				
				GeneralJavaScriptObject jsonData = GeneralJavaScriptObject.createJsonObject(response.getText());

				mocEntity.addJSON(tablePanel, jsonData);
				
				
				if(onComplete != null) {
					onComplete.onComplete();
				}
			}
		});
	}
}
