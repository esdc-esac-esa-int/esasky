package esac.archive.esasky.cl.web.client.callback;

import java.util.HashMap;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Response;

import esac.archive.esasky.cl.web.client.model.DynamicCountObject;
import esac.archive.esasky.cl.web.client.model.entities.GeneralEntityInterface;
import esac.archive.esasky.cl.web.client.utility.JSONUtils;
import esac.archive.esasky.cl.web.client.view.resultspanel.AbstractTablePanel;

public class GetMissionDataCountRequestCallback extends JsonRequestCallback {
    
    private GeneralEntityInterface entity;
    private AbstractTablePanel tablePanel;
    private static HashMap<String, Long> latestUpdates = new HashMap<String, Long>();
    private long timecall;

    public GetMissionDataCountRequestCallback(GeneralEntityInterface entity, AbstractTablePanel tablePanel,
            String progressIndicatorMessage, String url) {
        super(progressIndicatorMessage, url);
        this.entity = entity;
        this.tablePanel = tablePanel;
        timecall = System.currentTimeMillis();
        latestUpdates.put(tablePanel.getEsaSkyUniqID(), timecall);
    }

    @Override
    protected void onSuccess(Response response) {
        if (timecall < latestUpdates.get(tablePanel.getEsaSkyUniqID())) {
            Log.warn(this.getClass().getSimpleName() + " discarded server answer with timecall="
                    + timecall + " , dif:" + (latestUpdates.get(tablePanel.getEsaSkyUniqID()) - timecall));
            return;
        }
        if(tablePanel.hasBeenClosed()) {
            return;
        }
        
        JsArray<JavaScriptObject> array = JSONUtils.evalJsonGetData("(" + response.getText() + ")");
        DynamicCountObject countObject = JsonUtils.<DynamicCountObject> safeEval(array.get(0).toString());

        entity.getCountStatus().setCount(entity.getDescriptor().getMission(), countObject.getCount());
        entity.getCountStatus().updateCount();
        entity.fetchData(tablePanel);
    }
}
