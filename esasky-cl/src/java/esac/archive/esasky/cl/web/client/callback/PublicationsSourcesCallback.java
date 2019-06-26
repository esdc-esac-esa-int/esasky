package esac.archive.esasky.cl.web.client.callback;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Response;

import esac.archive.esasky.cl.web.client.model.TapRowList;
import esac.archive.esasky.cl.web.client.model.entities.PublicationsEntity;
import esac.archive.esasky.cl.web.client.presenter.ResultsPresenter.TapRowListMapper;
import esac.archive.esasky.cl.web.client.status.GUISessionStatus;

public class PublicationsSourcesCallback extends JsonRequestCallback {

    private PublicationsEntity entity;
    private final String debugPrefix = "[PublicationsSourcesCallback]";
    private long timecall;
    private static long lastTimecall;
     
    public PublicationsSourcesCallback(PublicationsEntity entity, String progressIndicatorMessage, String url) {
        super(progressIndicatorMessage, url);
        this.entity = entity;
        timecall = System.currentTimeMillis();
        lastTimecall = timecall;
    }

    @Override
    protected void onSuccess(Response response) {
    	Log.debug(debugPrefix + "[onSuccess]");
    	if(timecall == lastTimecall && GUISessionStatus.getIsPublicationsActive()) {
    		TapRowListMapper mapper = GWT.create(TapRowListMapper.class);
    		TapRowList rowList = mapper.read(response.getText());
    		entity.setMetadata(rowList);
    		entity.addShapes(rowList);
    	}
    }
}
