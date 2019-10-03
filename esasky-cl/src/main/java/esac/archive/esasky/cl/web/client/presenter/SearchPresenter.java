package esac.archive.esasky.cl.web.client.presenter;

import java.rmi.ServerError;

import com.allen_sauer.gwt.log.client.Log;
import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.TextBox;

import esac.archive.esasky.ifcs.model.coordinatesutils.ClientRegexClass;
import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinateValidator;
import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinateValidator.SearchInputType;
import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesConversion;
import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesParser;
import esac.archive.esasky.ifcs.model.shared.ESASkyGeneralResultList;
import esac.archive.esasky.ifcs.model.shared.ESASkySSOSearchResultList;
import esac.archive.esasky.ifcs.model.shared.ESASkySearchResult;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPopEvent;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPushEvent;
import esac.archive.esasky.cl.web.client.event.banner.CheckForServerMessagesEvent;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.status.ScreenSizeObserver;
import esac.archive.esasky.cl.web.client.status.ScreenSizeService;
import esac.archive.esasky.cl.web.client.status.ScreenWidth;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.allskypanel.AllSkyFocusPanel;
import esac.archive.esasky.cl.web.client.view.allskypanel.AllSkyFocusPanel.AllSkyFocusPanelObserver;
import esac.archive.esasky.cl.web.client.view.common.buttons.CloseButton;

/**
 * @author ESDC team Copyright (c) 2015- European Space Agency
 */
public class SearchPresenter {
	
    /** local instance of view. */
    private View view;
    
    private boolean isTextBoxFocused;

    /** Interface ESASkySearchResult mapper. */
    public interface ESASkySearchResultMapper extends ObjectMapper<ESASkySearchResult> {
    }

    /** Interface ESASkySearchResult mapper. */
    public interface ESASkyGeneralSearchResultMapper extends ObjectMapper<ESASkyGeneralResultList> {
    }

    /** Interface ESASkySSOSearchResult mapper. */
    public interface ESASkySSOSearchResultListMapper extends
            ObjectMapper<ESASkySSOSearchResultList> {
    }

    /**
     * View interface.
     */
    public interface View {

        TextBox getSearchTextBox();

		FocusPanel getTooltip();

        FocusPanel getSearchTextBoxError();

        void showCoordsInputErrorMessage();

        void showTargetNotFoundMessage();

        void showBackendMessage(String message);

        void showSSOTargetNotFoundMessage();

        void showGeneralTargetResultsPanel(ESASkyGeneralResultList resultList);

        FocusPanel getSearchResultsListPanel();
        
        void setFullSize(boolean fullSize);

		CloseButton getClearTextButton();

    }

    /**
     * Class Constructor.
     * @param inputView Input View.
     * @param aladinLiteWrapper
     */
    public SearchPresenter(final View inputView) {
        this.view = inputView;
        bind();
    }

    /**
     * Bind view with presenter.
     */
    private void bind() {

        // Search for a target by name or coordinates within the Search input text

        this.view.getSearchTextBox().addKeyDownHandler(new KeyDownHandler() {
           @Override
            public void onKeyDown(final KeyDownEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    SearchPresenter.this.view.getSearchTextBoxError().setVisible(false);
                    SearchPresenter.this.view.getTooltip().setVisible(false);

                    String userInput = SearchPresenter.this.view.getSearchTextBox().getValue()
                            .trim();

                    CoordinateValidator.SearchInputType inputType = CoordinateValidator
                            .checkInputType(new ClientRegexClass(), userInput, AladinLiteWrapper.getCoordinatesFrame());

                    if (inputType == CoordinateValidator.SearchInputType.NOT_VALID) {
                        SearchPresenter.this.view.showCoordsInputErrorMessage();
                        Log.debug("ERROR FOR WRONG INPUT FORMAT");
                        
                        GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_Search, GoogleAnalytics.ACT_Search_SearchWrongCoords, userInput);
                    } else {
                    	GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_Search, GoogleAnalytics.ACT_Search_SearchQuery, userInput);
                    	if (inputType == CoordinateValidator.SearchInputType.TARGET) {
                    		doSearch4Target();
                    	} else {
                    		doSearchByCoords(inputType);
                    	}
                    }
                }
            }
        });

        this.view.getSearchTextBox().addMouseDownHandler(new MouseDownHandler() {

            @Override
            public void onMouseDown(final MouseDownEvent arg0) {
                openSearchExtras();
            }
        });
        
        this.view.getSearchTextBox().addMouseOutHandler(new MouseOutHandler() {
        	
            	@Override
            	public void onMouseOut(final MouseOutEvent arg0) {
            		closeSearchExtras();
            	}
        });
        
        // When user hovers over tooltip coords the element remains visible
        this.view.getTooltip().addMouseOverHandler(new MouseOverHandler() {

            @Override
            public void onMouseOver(final MouseOverEvent arg0) {
            	    openSearchExtras();
            }
        });
        
        // When user hovers out the tooltip coords the element is hidden
        this.view.getTooltip().addMouseOutHandler(new MouseOutHandler() {

            @Override
            public void onMouseOut(final MouseOutEvent arg0) {
                closeSearchExtras();
                Log.debug("[TargetPresenter/getTooltip()]:onMouseOut");
            }
        });
        
        this.view.getSearchTextBox().addMouseOverHandler(new MouseOverHandler() {
			
			@Override
			public void onMouseOver(MouseOverEvent event) {
				openSearchExtras();
			}
		});
        
        this.view.getClearTextButton().addMouseOverHandler(new MouseOverHandler() {
			
			@Override
			public void onMouseOver(MouseOverEvent event) {
				openSearchExtras();
			}
		});
        
        this.view.getSearchResultsListPanel().addMouseOverHandler(new MouseOverHandler() {
			
			@Override
			public void onMouseOver(MouseOverEvent event) {
				openSearchExtras();
			}
		});
        
        this.view.getSearchTextBoxError().addMouseOverHandler(new MouseOverHandler() {
			
			@Override
			public void onMouseOver(MouseOverEvent event) {
				openSearchExtras();
			}
		});
        
        view.getSearchTextBox().addFocusHandler(new FocusHandler() {
			
			@Override
			public void onFocus(FocusEvent arg0) {
				isTextBoxFocused = true;
			}
		});
        
        view.getSearchTextBox().addBlurHandler(new BlurHandler() {
			
			@Override
			public void onBlur(BlurEvent arg0) {
				isTextBoxFocused = false;
			}
		});
        
        ScreenSizeService.getInstance().registerObserver(new ScreenSizeObserver() {
			
			@Override
			public void onScreenSizeChange() {
				setCorrectBoxSize();
			}
		});
        setCorrectBoxSize();
        
		AllSkyFocusPanel.getInstance().registerObserver(new AllSkyFocusPanelObserver() {
			
			@Override
			public void onAladinInteraction() {
				closeSearchExtras();
			}
		});
		
		MainLayoutPanel.addMainAreaResizeHandler(new ResizeHandler() {
			
			@Override
			public void onResize(ResizeEvent event) {
				setMaxHeight();
			}
		});

		setMaxHeight();

    }
    
    private void setCorrectBoxSize() {
	    ScreenWidth screenWidth = ScreenSizeService.getInstance().getScreenSize().getWidth();
		if(screenWidth.getPxSize() <= ScreenWidth.SMALL.getPxSize() && !isTextBoxFocused) {
			SearchPresenter.this.view.setFullSize(false);
		} else {
			SearchPresenter.this.view.setFullSize(true);
		}
    }
    
    private void closeSearchExtras() {
        setCorrectBoxSize();
		SearchPresenter.this.view.getTooltip().setVisible(false);
		SearchPresenter.this.view.getSearchTextBoxError().setVisible(false);
    }
    
    private void openSearchExtras() {
        AladinLiteWrapper.getInstance().removeSearchtargetPointer();
        SearchPresenter.this.view.setFullSize(true);
        SearchPresenter.this.view.getSearchTextBoxError().setVisible(false);
        if(!SearchPresenter.this.view.getSearchResultsListPanel().isVisible()) {
        	    SearchPresenter.this.view.getTooltip().setVisible(true);
        }
        setMaxHeight();
    }
    
    private void setMaxHeight() {
    	view.getSearchResultsListPanel().getElement().getStyle().setProperty("maxHeight", MainLayoutPanel.getMainAreaHeight() + MainLayoutPanel.getMainAreaAbsoluteTop() - view.getSearchResultsListPanel().getAbsoluteTop() + "px");
    	view.getTooltip().getElement().getStyle().setProperty("maxHeight", MainLayoutPanel.getMainAreaHeight() + MainLayoutPanel.getMainAreaAbsoluteTop() - view.getTooltip().getAbsoluteTop() + "px");
    	view.getSearchTextBoxError().getElement().getStyle().setProperty("maxHeight", MainLayoutPanel.getMainAreaHeight() + MainLayoutPanel.getMainAreaAbsoluteTop() - view.getSearchTextBoxError().getAbsoluteTop() + "px");
    }
    /**
     * doSearchByCoords().
     * @param inputType
     */
    private void doSearchByCoords(SearchInputType inputType) {

        final String debugPrefix = "[doSearchByCoords]";

        final String userInput = this.view.getSearchTextBox().getValue();

        Log.debug(debugPrefix + "Coordinates as they have been introduced by the user: "
                + userInput);

        String raString = CoordinatesConversion.getRaFromCoords(userInput).trim();
        String decString = CoordinatesConversion.getDecFromCoords(userInput).trim();
        if (null == raString || null == decString) {
            return;
        }
        Double raDeg = null;
        Double decDeg = null;
        Double[] raDecDeg = CoordinatesParser.convertCoordsToDegrees(new ClientRegexClass(), userInput,
                AladinLiteWrapper.getCoordinatesFrame(), AladinLiteWrapper.getCoordinatesFrame());

        Log.debug("RA " + raDeg);
        Log.debug("DEC " + decDeg);
        Log.debug("RA " + raDecDeg[0].toString());
        Log.debug("DEC " + raDecDeg[1].toString());

        AladinLiteWrapper.getInstance().goToObject(
                Double.toString(raDecDeg[0]) + " " + Double.toString(raDecDeg[1]), false);

    }

    private static long latestTimecall = System.currentTimeMillis();
    private void doSearch4Target() {
        final String debugPrefix = "[doSearchTarget]";
        String targetName = this.view.getSearchTextBox().getValue();
        if(targetName.trim().isEmpty()) {
        	return;
        }

        final String url = URL.encode(
                EsaSkyWebConstants.GENERAL_RESOLVER_URL + "?action=bytarget&target=" + targetName)
                .replace("+", "%2B");

        Log.debug(debugPrefix + "Query [" + url + "]");
        final String id = "target-" + targetName;
        // TODO change to a generic one and in case of error in the response of one of the search
        // services, just show the error related to it
        CommonEventBus.getEventBus().fireEvent(
                new ProgressIndicatorPushEvent(id, TextMgr.getInstance().getText("searchPresenter_resolvingTarget").replace("$NAME$", targetName)));

        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
        final long timecall = System.currentTimeMillis();
        latestTimecall = timecall;
        try {
            builder.sendRequest(null, new RequestCallback() {

                @Override
                public void onResponseReceived(final Request request, final Response response) {
                	try {
                		CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPopEvent(id));
                		if(timecall != latestTimecall) {
                			Log.warn("discarding search response, since there are newer searches");
                		}
                		if (200 == response.getStatusCode()) {
                			
                			ESASkyGeneralSearchResultMapper mapper = GWT
                					.create(ESASkyGeneralSearchResultMapper.class);
                			ESASkyGeneralResultList result = mapper.read(response.getText());
                			if (result == null) {
                				SearchPresenter.this.view.showTargetNotFoundMessage();
                				GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_Search, GoogleAnalytics.ACT_Search_SearchTargetNotFound, "SIMBAD: " + view.getSearchTextBox().getValue());
                				
                			} else {
                				SearchPresenter.this.view.showGeneralTargetResultsPanel(result);
                			}
                			setMaxHeight();
                			
                		} else {
                			Log.error(debugPrefix + "Couldn't retrieve data from the " + url
                					+ " StatusText: (" + response.getStatusText() + ")");
                			onError(request, new Throwable());
                		}
                		CommonEventBus.getEventBus().fireEvent(new CheckForServerMessagesEvent());
                	} catch (Exception e) {
                		onError(request, e);
                	}
                }

                @Override
                public void onError(final Request request, final Throwable exception) {
                	CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPopEvent(id));
                    Log.error(exception.getMessage());
                    Log.error(debugPrefix + "Error calling " + url);
                }
            });
        } catch (RequestException e) {
        	CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPopEvent(id));
            Log.error(e.getMessage());
            Log.error(debugPrefix + "Error calling " + url);
        }
    }

}
