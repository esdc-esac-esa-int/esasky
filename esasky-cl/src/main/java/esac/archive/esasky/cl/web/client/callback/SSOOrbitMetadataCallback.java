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
        latestUpdates.put(entity.getId(), timecall);
    }

    @Override
    protected void onSuccess(Response response) {
    	if (timecall < latestUpdates.get(entity.getId())) {
    		Log.warn(this.getClass().getSimpleName() + " discarded server answer with timecall="
    				+ timecall + " , dif:" + (latestUpdates.get(entity.getId()) - timecall));
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