package esac.archive.esasky.cl.web.client;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasWidgets;

import esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteConstants;
import esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteConstants.CoordinateFrame;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteCoordinatesChangedEvent;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteCoordinatesChangedEventHandler;
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
import esac.archive.esasky.cl.web.client.utility.exceptions.MapKeyException;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.OutreachImagePanel;

/**
 * @author ESDC team Copyright (c) 2015- European Space Agency
 */
public class Controller implements ValueChangeHandler<String> {

    private HasWidgets container;
    private MainPresenter presenter;
    
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
		GUISessionStatus.initiateHipsLocationScheduler();
		
		String toggleColumns = Window.Location.getParameter(EsaSkyWebConstants.URL_PARAM_TOGGLE_COLUMNS);
		if(toggleColumns != null 
		        && isParameterNegative(toggleColumns)){
		    try {
				Modules.setModule(EsaSkyWebConstants.MODULE_TOGGLE_COLUMNS, false);
			} catch (MapKeyException e) {
				Log.debug(e.getMessage(), e);
			}
	    }
		
		setBasicLayoutFromParameters();
		setSciMode();
		OutreachImagePanel.setStartupId(Window.Location.getParameter(EsaSkyWebConstants.URL_PARAM_HST_IMAGE));

		String hideWelcomeString = Window.Location.getParameter(EsaSkyWebConstants.URL_PARAM_HIDE_WELCOME);
		final boolean hideWelcome = hideWelcomeString != null && hideWelcomeString.toLowerCase().contains("true");
		
		if (Window.Location.getParameter(EsaSkyWebConstants.URL_PARAM_HIPS) != null
				|| Window.Location.getParameter(EsaSkyWebConstants.URL_PARAM_FRAME_COORD) != null
				|| Window.Location.getParameter(EsaSkyWebConstants.URL_PARAM_TARGET) != null) {
			startupWithChosenTargetOrHips(hideWelcome);
		} else if (EsaSkyWebConstants.RANDOM_SOURCE_ON_STARTUP && !UrlUtils.urlHasBibcode() && !UrlUtils.urlHasAuthor()) {
			//Retrieves a random source from backend and shows it
			startupWithRandomSource(hideWelcome);
		} else {
			initESASkyWithURLParameters("", "", "", "", hideWelcome);
		}
		
	}

    private void startupWithChosenTargetOrHips(final boolean hideWelcome) {
        final String hiPSName = Window.Location.getParameter(EsaSkyWebConstants.URL_PARAM_HIPS) == null ? "" : Window.Location.getParameter(EsaSkyWebConstants.URL_PARAM_HIPS);
        final String cooFrame = Window.Location.getParameter(EsaSkyWebConstants.URL_PARAM_FRAME_COORD) == null ? AladinLiteConstants.FRAME_J2000 : Window.Location.getParameter(EsaSkyWebConstants.URL_PARAM_FRAME_COORD);
        GUISessionStatus.setShowCoordinatesInDegrees(cooFrame.toLowerCase().contains("gal"));
        String targetFromUrl = Window.Location.getParameter(EsaSkyWebConstants.URL_PARAM_TARGET);
        Log.debug("[Controller] QUERYSTRING: " + Window.Location.getQueryString());


        String target = "";
        if(targetFromUrl != null){
        	target = extractTargetFromUrlParameter(targetFromUrl, target);
        }

        final String fov = Window.Location.getParameter(EsaSkyWebConstants.URL_PARAM_FOV);

        initESASkyWithURLParameters(hiPSName, target, fov, cooFrame, hideWelcome);
    }
    
    private String extractTargetFromUrlParameter(String targetFromUrl, String target) {
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
        return target;
    }

    private void startupWithRandomSource(final boolean hideWelcome) {
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
        					&& !GUISessionStatus.getIsInScienceMode()
        					&& !Modules.getMode().equalsIgnoreCase("clean")) {
        				//Wait until target coordinate and position is found and set
        				CommonEventBus.getEventBus().addHandler(AladinLiteCoordinatesChangedEvent.TYPE, new AladinLiteCoordinatesChangedEventHandler() {
        					
        					boolean isInitialEvent = true;
        					@Override
        					public void onCoordsChanged(AladinLiteCoordinatesChangedEvent coordinateEvent) {
        						if(isInitialEvent) {
        							if(!esaSkyTarget.getTitle().isEmpty() 
        									&& !esaSkyTarget.getDescription().isEmpty()) {
        								CommonEventBus.getEventBus().fireEvent(new TargetDescriptionEvent(esaSkyTarget.getTitle(), esaSkyTarget.getDescription(), false));
        							}
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
    }

    private void setBasicLayoutFromParameters() {
        String mode = Window.Location.getParameter(EsaSkyWebConstants.URL_PARAM_LAYOUT);
		Modules.setMode(mode);
		
		String hideBannerInfoString = Window.Location.getParameter(EsaSkyWebConstants.URL_PARAM_HIDE_BANNER_INFO);
		GUISessionStatus.setShouldHideBannerInfo(hideBannerInfoString != null && hideBannerInfoString.toLowerCase().contains("true"));
        
		String hideSwitchString = Window.Location.getParameter(EsaSkyWebConstants.URL_PARAM_HIDE_SCI);
		GUISessionStatus.sethideSwitch(hideSwitchString != null && hideSwitchString.toLowerCase().contains("true"));
		
		String showEvaString = Window.Location.getParameter(EsaSkyWebConstants.URL_PARAM_SHOW_EVA);
		if(showEvaString != null && showEvaString.toLowerCase().contains("true")) {
			try {
				Modules.setModule(EsaSkyWebConstants.MODULE_EVA, true);
				Modules.setModule(EsaSkyWebConstants.MODULE_EVA_MENU, true);
			} catch (MapKeyException e) {
				Log.error(e.getMessage(), e);
			}
		}
		
    }

    private void setSciMode() {
        String sciMode = Window.Location.getParameter(EsaSkyWebConstants.URL_PARAM_SCI_MODE);
		if(
				shouldEnterSciMode(sciMode)
				&& Modules.getModule(EsaSkyWebConstants.MODULE_SCIENCE_MODE)
		) {
			GUISessionStatus.setInitialIsInScienceMode();
		}
    }

    private boolean shouldEnterSciMode(String sciMode) {
        return isParameterPositive(sciMode)
        || isDesktopWithNoCookieOrSciParameter(sciMode)
        || (hasPositiveCookie() && !isParameterNegative(sciMode));
    }

    private boolean isParameterNegative(String parameter) {
        return parameter != null && (parameter.toLowerCase().contains("false") || parameter.toLowerCase().contains("off"));
    }
    
    private boolean hasPositiveCookie() {
        return Cookies.getCookie(EsaSkyWebConstants.SCI_MODE_COOKIE) != null && "true".equalsIgnoreCase(Cookies.getCookie(EsaSkyWebConstants.SCI_MODE_COOKIE));
    }

    private boolean isDesktopWithNoCookieOrSciParameter(String sciMode) {
        return Cookies.getCookie(EsaSkyWebConstants.SCI_MODE_COOKIE) == null && sciMode == null && !DeviceUtils.isMobileOrTablet();
    }

    private boolean isParameterPositive(String parameter) {
        return parameter != null && (parameter.toLowerCase().contains("on") || parameter.toLowerCase().contains("true"));
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
