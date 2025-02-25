/*
ESASky
Copyright (C) 2025 Henrik Norman

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

package esac.archive.esasky.cl.web.client.presenter;


import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;

import esac.archive.esasky.cl.web.client.event.hips.HipsChangeEvent;
import esac.archive.esasky.cl.web.client.view.common.ESASkyPlayerPanel;
import esac.archive.esasky.ifcs.model.client.HiPS;
import esac.archive.esasky.ifcs.model.client.HipsWavelength;
import esac.archive.esasky.ifcs.model.client.SkiesMenu;
import esac.archive.esasky.ifcs.model.client.SkiesMenuEntry;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.hips.HipsAddedEvent;
import esac.archive.esasky.cl.web.client.presenter.CtrlToolBarPresenter.SkiesMenuMapper;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.selectsky.AddSkyButton;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.selectsky.SkyRow;

public class SelectSkyPanelPresenter {

    private View view;
    
    private SkiesMenu skiesMenu;

    public interface View {
    	void fillAllSkyPanelEntries(SkiesMenu skiesMenu);
    	
    	void hide();
    	void toggle();
    	boolean isShowing();
    	void setSkiesMenu(SkiesMenu skiesMenu);
    	SkyRow createSky(boolean sendConvenienveEvent);
    	SkyRow createSky(boolean sendConvenienveEvent, String category, boolean isDefault);
    	boolean select(HiPS hips);
    	
    	void refreshUserDropdowns();
    	
    	AddSkyButton getAddSkyRowButton();
        ESASkyPlayerPanel getPlayerPanel();
    }
    
    public SelectSkyPanelPresenter(final View inputView) {
        this.view = inputView;
        getHiPSMapsList();
        CommonEventBus.getEventBus().addHandler(HipsAddedEvent.TYPE, changeEvent -> {
        	if(changeEvent.getAddIfAlreadyExist()) {
        		if(changeEvent.getHipsWavelength() == HipsWavelength.USER || changeEvent.getHipsWavelength() == HipsWavelength.GW || !HipsWavelength.wavelengthList.contains(changeEvent.getHipsWavelength())) {
        			addUrlHips(changeEvent.getHiPS(), null);
        		} else {
        			SkyRow sky = view.createSky(true);
                    CommonEventBus.getEventBus().fireEvent(
                            new HipsChangeEvent(sky.getRowId(), sky.getSelectedHips(), sky.getSelectedPalette(), sky.isBase(), 1));
        		}
        	} else {
        		if(!view.select(changeEvent.getHiPS())) {
        			addUrlHips(changeEvent.getHiPS(), null);
        		}
        	}
		});
    }

    public SkiesMenu getSkiesMenu(){
    	return skiesMenu;
    }

    public  ESASkyPlayerPanel getPlayerPanel() {
        return view.getPlayerPanel();
    }
    
    public void addUrlHips(HiPS hips, SkyRow skyRow) {
		
		SkiesMenuEntry entry = skiesMenu.getHiPSListByWavelength(hips.getHipsWavelength());
		
		if(entry == null) {
			entry = new SkiesMenuEntry();
			entry.getHips().add(hips);
			entry.setTotal(1);
			if(hips.getHipsWavelength() == null) {
				if (hips.getHipsCategory() == null) {
					hips.setHipsWavelength(HipsWavelength.USER);
				}else {
					hips.setHipsWavelength(hips.getHipsCategory());
				}
				
			}
			entry.setWavelength(hips.getHipsWavelength());
			getSkiesMenu().getMenuEntries().add(entry);
		} else {
            if (!entry.getHips().contains(hips)) {
                entry.getHips().add(hips);
            }
		}
		if(skyRow == null) {
			skyRow = view.createSky(false);
		}
		skyRow.setSelectHips(hips.getSurveyName(), false, hips.getHipsCategory());
		view.refreshUserDropdowns();
    }

    private void getHiPSMapsList() {
        Log.debug("Into SelectSkyPresenter.getHiPSMapsList");
        String url = null;

        url = EsaSkyWebConstants.HIPS_SOURCES_URL;

        Log.debug("Query [" + url + "]");

        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
        try {
            builder.sendRequest(null, new RequestCallback() {

                @Override
                public void onResponseReceived(final Request request, final Response response) {
                    // store start time for logging purposes
                    long startTimeMillis = 0L;
                    if (Log.isDebugEnabled()) {
                        startTimeMillis = System.currentTimeMillis();
                    }
                    if (200 == response.getStatusCode()) {
                        Log.debug("Data retrieved ["
                                + (System.currentTimeMillis() - startTimeMillis) + " ms]");
                        SkiesMenuMapper mapper = GWT.create(SkiesMenuMapper.class);
                        skiesMenu = mapper.read(response.getText());
                        Log.debug("Total skies entries: " + skiesMenu.getTotal().toString());
                        view.fillAllSkyPanelEntries(skiesMenu);

                    } else {
                        Log.error("Couldn't retrieve JSON (" + response.getStatusText() + ")");
                        skiesMenu = new SkiesMenu();
                        view.setSkiesMenu(skiesMenu);
                    }
                }

                @Override
                public void onError(final Request request, final Throwable exception) {
                    Log.error(exception.getMessage());
                    Log.error("Error fetching JSON data from server");
                }
            });
        } catch (RequestException e) {
            Log.error(e.getMessage());
            Log.error("Error fetching JSON data from server");
        }
        Log.debug("End of SelectSkyPresenter.getHiPSMapsList");
    }
    
    public void hide() {
    	view.hide();
    }
    
    public void toggle() {
		view.toggle();
    }
    
    public boolean isShowing() {
    	return view.isShowing();
    }	
}
