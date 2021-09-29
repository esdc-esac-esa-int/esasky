package esac.archive.esasky.cl.web.client.presenter;


import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;

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
    	boolean select(HiPS hips);
    	
    	void refreshUserDropdowns();
    	
    	AddSkyButton getAddSkyRowButton();
    }
    
    public SelectSkyPanelPresenter(final View inputView) {
        this.view = inputView;
        getHiPSMapsList();
        CommonEventBus.getEventBus().addHandler(HipsAddedEvent.TYPE, changeEvent -> {
        	if(changeEvent.getAddIfAlreadyExist()) {
        		if(changeEvent.isUserHips()) {
        			addUrlHips(changeEvent.getHiPS());
        		} else {
        			view.createSky(true);
        		}
        	} else {
        		if(!view.select(changeEvent.getHiPS())) {
        			addUrlHips(changeEvent.getHiPS());
        		}
        	}
		});
    }

    public SkiesMenu getSkiesMenu(){
    	return skiesMenu;
    }
    
    private void addUrlHips(HiPS hips) {
		
		SkiesMenuEntry entry = skiesMenu.getHiPSListByWavelength(HipsWavelength.USER);
		
		if(entry == null) {
			entry = new SkiesMenuEntry();
			entry.getHips().add(hips);
			entry.setTotal(1);
			entry.setWavelength(HipsWavelength.USER);
			getSkiesMenu().getMenuEntries().add(entry);
		} else {
		    entry.getHips().add(hips);
		}
		SkyRow skyRow = view.createSky(false);
		skyRow.setSelectHips(hips.getSurveyName(), false, false);
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
