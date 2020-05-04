package esac.archive.esasky.cl.web.client.callback;

import java.util.HashMap;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Response;

import esac.archive.esasky.cl.web.client.model.TapRowList;
import esac.archive.esasky.cl.web.client.model.entities.SSOEntity;
import esac.archive.esasky.cl.web.client.presenter.ResultsPresenter.TapRowListMapper;
import esac.archive.esasky.cl.web.client.status.GUISessionStatus;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.ifcs.model.coordinatesutils.Coordinate;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;

public class SSOOrbitMetadataCallback extends JsonRequestCallback {

    private final SSOEntity entity;
    private static final HashMap<String, Long> latestUpdates = new HashMap<String, Long>();
    private long timecall;

    public SSOOrbitMetadataCallback(SSOEntity entity, String progressIndicatorMessage, String url) {
        super(progressIndicatorMessage, url);
        this.entity = entity;
        timecall = System.currentTimeMillis();
        latestUpdates.put(entity.getEsaSkyUniqId(), timecall);
    }

    @Override
    protected void onSuccess(Response response) {
    	if (timecall < latestUpdates.get(entity.getEsaSkyUniqId())) {
    		Log.warn(this.getClass().getSimpleName() + " discarded server answer with timecall="
    				+ timecall + " , dif:" + (latestUpdates.get(entity.getEsaSkyUniqId()) - timecall));
    		return;
    	}
    	if(!GUISessionStatus.getIsTrackingSSO()) {
    		return;
    	}
    	
        TapRowListMapper mapper = GWT.create(TapRowListMapper.class);
        TapRowList rowList = mapper.read(response.getText());
        Log.debug("rowList data size " + rowList.getData().size());
        Log.debug("rowList metadata size " + rowList.getMetadata().size());

        if (rowList.getMetadata().size() > 0 && rowList.getData().size() > 0) {
            String ssoPoints = ((String) rowList.getData().get(0).get(0)).trim();
            String[] raDecTokens = ssoPoints.split("\\s\\s");
            double[] polylinePoints = new double[raDecTokens.length * 2];
            for (int i = 0; i < raDecTokens.length; i++) {
                String[] raAndDec = raDecTokens[i].split("\\s");
                polylinePoints[2 * i] = Double.parseDouble(raAndDec[0]);
                polylinePoints[2 * i + 1] = Double.parseDouble(raAndDec[1]);
            }
            
            this.entity.setOrbitPolyline(polylinePoints);
            
            if(raDecTokens.length > 0) {
            	String[] raAndDec = raDecTokens[0].split("\\s");
            	SkyViewPosition pos = new SkyViewPosition(new Coordinate(Double.valueOf(raAndDec[0]), Double.valueOf(raAndDec[1])), 
                        entity.getSkyViewPosition().getFov());
            	this.entity.setSkyViewPosition(pos);
            	AladinLiteWrapper.getInstance().goToTarget(raAndDec[0], raAndDec[1], pos.getFov(), false, AladinLiteWrapper.getCoordinatesFrame().getValue());
            }
        }
    }
}