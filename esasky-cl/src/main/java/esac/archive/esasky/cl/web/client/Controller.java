package esac.archive.esasky.cl.web.client;

import com.allen_sauer.gwt.log.client.Log;
import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasWidgets;

import esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteConstants;
import esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteConstants.CoordinateFrame;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteCoordinatesChangedEvent;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteCoordinatesChangedEventHandler;
import esac.archive.esasky.ifcs.model.client.HiPS;
import esac.archive.esasky.ifcs.model.shared.ESASkyTarget;
import esac.archive.esasky.cl.web.client.event.TargetDescriptionEvent;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.presenter.MainPresenter;
import esac.archive.esasky.cl.web.client.status.GUISessionStatus;
import esac.archive.esasky.cl.web.client.utility.DeviceUtils;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.JSONUtils;
import esac.archive.esasky.cl.web.client.utility.JSONUtils.IJSONRequestCallback;
import esac.archive.esasky.cl.web.client.utility.ParseUtils;
import esac.archive.esasky.cl.web.client.utility.UrlUtils;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;

/**
 * @author ESDC team Copyright (c) 2015- European Space Agency
 */
public class Controller implements ValueChangeHandler<String> {

    private HasWidgets container;
    private MainPresenter presenter;
    public interface HiPSMapper extends ObjectMapper<HiPS> {}
    
    public Controller() {
        bind();
    }

    private void bind() {
        History.addValueChangeHandler(this);
    }

    public final void go(final HasWidgets inputContainer) {
        this.container = inputContainer;
        initializePresenter();
        History.fireCurrentHistoryState();
    }

    @Override
    public final void onValueChange(final ValueChangeEvent<String> event) {
        String token = event.getValue();
        if (token != null) {
            // TODO
        }
    }

	private void initializePresenter() {

		String sciMode = Window.Location.getParameter(EsaSkyWebConstants.URL_PARAM_SCI_MODE);
		if(
				(sciMode != null 
				&& (sciMode.toLowerCase().contains("on") || sciMode.toLowerCase().contains("true"))
						)
				|| 
				(!DeviceUtils.isMobileOrTablet() && sciMode == null )) {
			GUISessionStatus.setInitialIsInScienceMode();
		}
		
		String hideWelcomeString = Window.Location.getParameter(EsaSkyWebConstants.URL_PARAM_HIDE_WELCOME);
		final boolean hideWelcome = hideWelcomeString != null && hideWelcomeString.toLowerCase().contains("true") ? true: false;
		String hideSwitchString = Window.Location.getParameter(EsaSkyWebConstants.URL_PARAM_HIDE_SCI);
		GUISessionStatus.sethideSwitch(hideSwitchString != null && hideSwitchString.toLowerCase().contains("true") ? true: false);

		if (Window.Location.getParameter(EsaSkyWebConstants.URL_PARAM_HIPS) != null
				|| Window.Location.getParameter(EsaSkyWebConstants.URL_PARAM_FRAME_COORD) != null
				|| Window.Location.getParameter(EsaSkyWebConstants.URL_PARAM_TARGET) != null) {
					
			final String hiPSName = Window.Location.getParameter(EsaSkyWebConstants.URL_PARAM_HIPS) == null ? "" : Window.Location.getParameter(EsaSkyWebConstants.URL_PARAM_HIPS);
			final String cooFrame = Window.Location.getParameter(EsaSkyWebConstants.URL_PARAM_FRAME_COORD) == null ? AladinLiteConstants.FRAME_J2000 : Window.Location.getParameter(EsaSkyWebConstants.URL_PARAM_FRAME_COORD);
			String targetFromUrl = Window.Location.getParameter(EsaSkyWebConstants.URL_PARAM_TARGET);
			Log.debug("[Controller] QUERYSTRING: " + Window.Location.getQueryString());


			String target = "";
			if(targetFromUrl != null){
				if(targetFromUrl.contains("-") || targetFromUrl.contains("+")){
					target = targetFromUrl;
				} else {
					if(countChar(targetFromUrl, ' ') == 1){
						//Since "+" in the coordinate cant be directly transmitted through the url, it is added here if needed.
						target = targetFromUrl.replaceFirst(" ([0-9])", " +$1");
					} else {
						String[] parts = targetFromUrl.split(" ");
						if(parts.length == 6){
							parts[3] = "+" + parts[3]; 
						}
						for(String part : parts){
							target += part + " ";
						}
						target = target.trim();
					}
				}
			}

			final String fov = Window.Location.getParameter(EsaSkyWebConstants.URL_PARAM_FOV);

			initESASkyWithURLParameters(hiPSName, target, fov, cooFrame, hideWelcome);

		} else {

			if (EsaSkyWebConstants.RANDOM_SOURCE_ON_STARTUP && !UrlUtils.urlHasBibcode() && !UrlUtils.urlHasAuthor()) {
				//Retrieves a random source from backend and shows it
				JSONUtils.getJSONFromUrl(EsaSkyWebConstants.RANDOM_SOURCE_URL + "?lang=" + TextMgr.getInstance().getLangCode(), new IJSONRequestCallback() {

					@Override
					public void onSuccess(String responseText) {
						String target = "";
						String fov = "";
						String cooFrame = "";
						String hiPSName = "";

						try {
							final ESASkyTarget esaSkyTarget = ParseUtils.parseJsonTarget(responseText);
							target = esaSkyTarget.getRa() + " " + esaSkyTarget.getDec();
							fov = esaSkyTarget.getFovDeg();
							cooFrame = esaSkyTarget.getCooFrame() != null ? esaSkyTarget.getCooFrame() : CoordinateFrame.J2000.toString();
							hiPSName = esaSkyTarget.getHipsName();
							initESASkyWithURLParameters(hiPSName, target, fov, cooFrame, hideWelcome);
							if (esaSkyTarget != null
									&& !esaSkyTarget.getTitle().isEmpty()
									&& !esaSkyTarget.getDescription().isEmpty()
									&& !GUISessionStatus.getIsInScienceMode()) {
								//Wait until target coordinate and position is found and set
								CommonEventBus.getEventBus().addHandler(AladinLiteCoordinatesChangedEvent.TYPE, new AladinLiteCoordinatesChangedEventHandler() {
									
									boolean isInitialEvent = true;
									@Override
									public void onCoordsChanged(AladinLiteCoordinatesChangedEvent coordinateEvent) {
										if(isInitialEvent) {
											CommonEventBus.getEventBus().fireEvent(new TargetDescriptionEvent(esaSkyTarget.getTitle(), esaSkyTarget.getDescription()));
										}
										isInitialEvent = false;
									}
								});
							}

						} catch (Exception ex) {
							Log.error("[Controller] getRandomSource onSuccess ERROR: ", ex);
							target = "";
							fov = "";
							cooFrame = "";
							hiPSName = "";
							initESASkyWithURLParameters(hiPSName, target, fov, cooFrame, hideWelcome);
						}
					}

					@Override
					public void onError(String errorCause) {
						Log.error("[Controller] getRandomSource ERROR: " + errorCause);
						initESASkyWithURLParameters("", "", "", "", hideWelcome);
					}

				}, EsaSkyWebConstants.RANDOM_SOURCE_CALL_TIMEOUT);

			} else {
				initESASkyWithURLParameters("", "", "", "", hideWelcome);
			}

		}
	}

	private static int countChar(String str, char c) {
		int start = -1;
		int count = 0;
		while (true) {
			if ((start = str.indexOf(c, start + 1)) == -1)
				return (count);
			count++;
		}
	}

	private void initESASkyWithURLParameters(String HiPSFromURL,
			String targetFromURL, String fov, String coordinateFrameFromUrl, boolean hideWelcome) {
		MainLayoutPanel view = new MainLayoutPanel(HiPSFromURL, targetFromURL, fov, coordinateFrameFromUrl, hideWelcome);
		
		boolean isInitialPositionDescribedInCoordinates = !targetFromURL.isEmpty() && !RegExp.compile("[a-zA-Z]").test(targetFromURL);
		presenter = new MainPresenter(view, coordinateFrameFromUrl, isInitialPositionDescribedInCoordinates);
        presenter.go(Controller.this.container);
	}
	
	public MainPresenter getRootPresenter(){
    	return presenter;
    }
}
