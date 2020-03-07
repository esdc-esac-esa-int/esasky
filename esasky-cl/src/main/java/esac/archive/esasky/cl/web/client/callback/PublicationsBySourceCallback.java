package esac.archive.esasky.cl.web.client.callback;

import java.util.HashMap;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Response;

import esac.archive.esasky.cl.web.client.model.TableRow;
import esac.archive.esasky.cl.web.client.model.TapRowList;
import esac.archive.esasky.cl.web.client.model.converter.TapToMmiDataConverter;
import esac.archive.esasky.cl.web.client.model.entities.PublicationsBySourceEntity;
import esac.archive.esasky.cl.web.client.presenter.ResultsPresenter.TapRowListMapper;
import esac.archive.esasky.cl.web.client.view.resultspanel.ITablePanel;

public class PublicationsBySourceCallback extends JsonRequestCallback {

    private PublicationsBySourceEntity entity;
    private ITablePanel tablePanel;
    private static HashMap<String, Long> latestUpdates = new HashMap<String, Long>();
    private long timecall;
    
    public PublicationsBySourceCallback(PublicationsBySourceEntity entity, ITablePanel tablePanel,
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
    	
        TapRowListMapper mapper = GWT.create(TapRowListMapper.class);
        TapRowList rowList = mapper.read(response.getText().replace("\\u0019", "'"));

        List<TableRow> tabRowList = TapToMmiDataConverter.convertTapToMMIData(rowList, entity.getDescriptor());
        entity.setMetadata(rowList);
        tablePanel.insertData(tabRowList, null);
    }
}
