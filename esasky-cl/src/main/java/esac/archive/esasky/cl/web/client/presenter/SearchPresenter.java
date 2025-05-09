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

package esac.archive.esasky.cl.web.client.presenter;

import com.allen_sauer.gwt.log.client.Log;
import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.http.client.*;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.CloseOtherPanelsEvent;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPopEvent;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPushEvent;
import esac.archive.esasky.cl.web.client.event.ShowPublicationSourcesEvent;
import esac.archive.esasky.cl.web.client.event.banner.CheckForServerMessagesEvent;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.TapRowList;
import esac.archive.esasky.cl.web.client.presenter.ResultsPresenter.TapRowListMapper;
import esac.archive.esasky.cl.web.client.repository.DescriptorRepository;
import esac.archive.esasky.cl.web.client.status.ScreenSizeObserver;
import esac.archive.esasky.cl.web.client.status.ScreenSizeService;
import esac.archive.esasky.cl.web.client.status.ScreenWidth;
import esac.archive.esasky.cl.web.client.utility.*;
import esac.archive.esasky.cl.web.client.utility.JSONUtils.IJSONRequestCallback;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.allskypanel.AllSkyFocusPanel;
import esac.archive.esasky.cl.web.client.view.allskypanel.AllSkyFocusPanel.AllSkyFocusPanelObserver;
import esac.archive.esasky.cl.web.client.view.allskypanel.SearchToolPanel;
import esac.archive.esasky.cl.web.client.view.common.ESASkyJavaScriptLibrary;
import esac.archive.esasky.cl.web.client.view.common.buttons.CloseButton;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyButton;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyToggleButton;
import esac.archive.esasky.cl.web.client.view.searchpanel.targetlist.TargetListPanel;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.coordinatesutils.ClientRegexClass;
import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinateValidator;
import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinateValidator.SearchInputType;
import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesConversion;
import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesParser;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;
import esac.archive.esasky.ifcs.model.shared.ESASkyGeneralResultList;
import esac.archive.esasky.ifcs.model.shared.ESASkySSOSearchResultList;
import esac.archive.esasky.ifcs.model.shared.ESASkySearchResult;

import java.util.List;
import java.util.UUID;

/**
 * @author ESDC team Copyright (c) 2015- European Space Agency
 */
public class SearchPresenter {
	
    /** local instance of view. */
    private View view;
    
    private boolean isTextBoxFocused;
    private boolean isMouseOverSearchTool;
    private static long latestBibCodeTimeCall;

    private static final String HTML_REPLACE = "$HTML$";
    private static final String JOURNAL_REPLACE = "$JOURNAL$";
    private static final String DATE_REPLACE = "$DATE$";
    private static final String H3_OPEN = "<h3 style='font-size: 0.85em;'>";
    private static final String H3_CLOSE = "</h3>";
    private static final String H4_OPEN = "<h4>";
    private static final String H4_CLOSE = "</h4>";
    private static final String H5_OPEN = "<h5>";
    private static final String H5_CLOSE = "</h5>";

    Timer updateBoxSizeTimer = new Timer() {
        @Override
        public void run() {
            setCorrectBoxSize();
        }

        @Override
        public void schedule(int delayMillis) {
            super.cancel();
            super.schedule(delayMillis);
        }
    };

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
        
        EsaSkyButton getSearchIcon();

		FocusPanel getTooltip();

        FocusPanel getSearchTextBoxError();

        void showCoordsInputErrorMessage();

        void showTargetNotFoundMessage();

        void showBackendMessage(String message);

        void showSSOTargetNotFoundMessage();

        void showGeneralTargetResultsPanel(ESASkyGeneralResultList resultList, String targetName, boolean unrecognizedInput);

        FocusPanel getSearchResultsListPanel();
        
        void setFullSize(boolean fullSize);

		CloseButton getClearTextButton();
		
		EsaSkyButton getTargetListButton();

        EsaSkyToggleButton getSearchToolButton();

        SearchToolPanel getSearchToolPanel();
		
		void closeAllOtherPanels(Widget button);
		
        void showSearchResultsOnTargetList(List<ESASkySearchResult> searchResults, String title);
        
        void updateModuleVisibility();

        TargetListPanel getTargetListPanel();

        void showTargetList();

        void showTargetList(String targetList);

        void closeTargetList();

        void toggleTargetList();

        void showSearchTool();

        void closeSearchTool();

        boolean setConeSearchArea(String ra, String dec, String radius);

        boolean setPolygonSearchArea(String stcs);

        void clearSearchArea();
    }

    /**
     * Class Constructor.
     * @param inputView Input View.
     * @param aladinLiteWrapper
     */
    public SearchPresenter(final View inputView) {
        this.view = inputView;
        bind();
        updateModuleVisibility();
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

                	GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_SEARCH, GoogleAnalytics.ACT_SEARCH_SEARCHQUERY, "UserInput: " + userInput + " CooFrame: " + AladinLiteWrapper.getCoordinatesFrame().getValue());
                	if (inputType == CoordinateValidator.SearchInputType.TARGET
                	        || inputType == CoordinateValidator.SearchInputType.NOT_VALID
                	        ) {
                		doSearch4Target(inputType);
                	} else if (inputType == SearchInputType.SEARCH_SHAPE) {
                        try {
                            AladinLiteWrapper.getAladinLite().createSearchArea(userInput);
                        }catch (Exception ex) {
                            Log.debug(ex.getMessage(), ex);
                            DisplayUtils.showMessageDialogBox(ex.getMessage(), TextMgr.getInstance().getText("error").toUpperCase(), UUID.randomUUID().toString(),
                                    TextMgr.getInstance().getText("error"));
                        }

                    } else {
                		doSearchByCoords(inputType);
                	}
                }
            }
        });

        /* Due to a gwt issue affecting iOS Chrome / Safari users, only one interaction handler (click, mouse over, etc.) may be added to searchIcon. 
         * Otherwise, the iOS keyboard does not open on focus. iOS Safari / Chrome requires that the opening of the keyboard is triggered directly by a user action.
         * It seems like adding multiple interaction handlers causes iOS Safari /Chrome to block the opening of the software keyboard.
         */
        this.view.getSearchIcon().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(final ClickEvent arg0) {
                openSearchExtras();
            }
        });
        
        this.view.getSearchTextBox().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(final ClickEvent arg0) {
                openSearchExtras();
            }
        });
        
        this.view.getSearchTextBox().addMouseOutHandler(new MouseOutHandler() {
        	
            	@Override
            	public void onMouseOut(final MouseOutEvent arg0) {
                    updateBoxSizeTimer.schedule(500);
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
                updateBoxSizeTimer.schedule(500);
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
        
        this.view.getTargetListButton().addMouseOverHandler(event -> {
            updateBoxSizeTimer.cancel();
    		SearchPresenter.this.view.setFullSize(true);
    		SearchPresenter.this.view.getSearchTextBoxError().setVisible(false);
    		SearchPresenter.this.view.getTooltip().setVisible(false);
        });


        this.view.getSearchToolPanel().addDomHandler(event -> {
            this.isMouseOverSearchTool = true;
        }, MouseOverEvent.getType());

        this.view.getSearchToolPanel().addDomHandler(event -> {
            this.isMouseOverSearchTool = false;
        }, MouseOutEvent.getType());


        this.view.getSearchToolButton().addMouseOverHandler(event -> {
            updateBoxSizeTimer.cancel();
            SearchPresenter.this.view.setFullSize(true);
            SearchPresenter.this.view.getSearchTextBoxError().setVisible(false);
            SearchPresenter.this.view.getTooltip().setVisible(false);
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
                updateBoxSizeTimer.schedule(500);
				closeSearchExtras();
			}
		});
		
		MainLayoutPanel.addMainAreaResizeHandler(new ResizeHandler() {
			
			@Override
			public void onResize(ResizeEvent event) {
				setMaxHeight();
			}
		});
		
        CommonEventBus.getEventBus().addHandler(CloseOtherPanelsEvent.TYPE, event ->
			view.closeAllOtherPanels(event.getWidgetNotToClose())
		);
        
        CommonEventBus.getEventBus().addHandler(ShowPublicationSourcesEvent.TYPE, event ->
            showPublicationInfo(event.rowData)
        );

		setMaxHeight();

    }
    
    public void updateModuleVisibility() {
    	view.updateModuleVisibility();
    }
    
    private void setCorrectBoxSize() {
	    ScreenWidth screenWidth = ScreenSizeService.getInstance().getScreenSize().getWidth();
		if(screenWidth.getPxSize() <= ScreenWidth.SMALL.getPxSize() && !isTextBoxFocused && !isMouseOverSearchTool) {
			SearchPresenter.this.view.setFullSize(false);
		} else {
			SearchPresenter.this.view.setFullSize(true);
		}
    }
    
    private void closeSearchExtras() {
		SearchPresenter.this.view.getTooltip().setVisible(false);
		SearchPresenter.this.view.getSearchTextBoxError().setVisible(false);
    }
    
    private void openSearchExtras() {
        AladinLiteWrapper.getInstance().removeSearchtargetPointer();
        SearchPresenter.this.view.setFullSize(true);
        SearchPresenter.this.view.getSearchTextBoxError().setVisible(false);
        if(!SearchPresenter.this.view.getSearchResultsListPanel().isVisible()
            && !SearchPresenter.this.view.getSearchToolButton().getToggleStatus()) {
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
        double[] raDecDeg = CoordinatesParser.convertCoordsToDegrees(new ClientRegexClass(), userInput,
                AladinLiteWrapper.getCoordinatesFrame(), AladinLiteWrapper.getCoordinatesFrame());

        Log.debug("RA " + raDeg);
        Log.debug("DEC " + decDeg);
        Log.debug("RA " + raDecDeg[0]);
        Log.debug("DEC " + raDecDeg[1]);

        AladinLiteWrapper.getInstance().goToObject(
                raDecDeg[0] + " " + raDecDeg[1], false);
        GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_SEARCH, GoogleAnalytics.ACT_SEARCH_SEARCHCOORDSSUCCESS,
                "RA: " + raString + " DEC: " + decString + " CooFrame: " + AladinLiteWrapper.getCoordinatesFrame().getValue());

    }

    private static long latestTimecall = System.currentTimeMillis();
    private void doSearch4Target(final CoordinateValidator.SearchInputType inputType) {
        final String debugPrefix = "[doSearchTarget]";
        final String targetName = this.view.getSearchTextBox().getValue();
        if(targetName.trim().isEmpty()) {
        	return;
        }

        final String url = 
                EsaSkyWebConstants.GENERAL_RESOLVER_URL + "?action=bytarget&target=" + URL.encodeQueryString(targetName);

        Log.debug(debugPrefix + "Query [" + url + "]");
        final String id = "target-" + targetName;
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
                			    if(targetName.equals(view.getSearchTextBox().getValue()) && inputType == SearchInputType.NOT_VALID) {
                                    SearchPresenter.this.view.showCoordsInputErrorMessage();
                                    Log.debug("ERROR FOR WRONG INPUT FORMAT");
                                    GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_SEARCH, GoogleAnalytics.ACT_SEARCH_SEARCHWRONGCOORDS, targetName);
                			    } else {
                			        SearchPresenter.this.view.showTargetNotFoundMessage();
                			        GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_SEARCH, GoogleAnalytics.ACT_SEARCH_SEARCHTARGETNOTFOUND, "SIMBAD: " + view.getSearchTextBox().getValue());
                			    }
                				
                			} else {
                				SearchPresenter.this.view.showGeneralTargetResultsPanel(result, targetName, targetName.equals(view.getSearchTextBox().getValue()) && inputType == SearchInputType.NOT_VALID);
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
    
    public void showPublicationInfo (final GeneralJavaScriptObject rowData) {
        String bibcode = rowData.getStringProperty("bibcode");
        String authors = rowData.getStringProperty("author");
        getPublicationSources(bibcode, rowData.getStringProperty("title"), authors, 
                rowData.getStringProperty("pub"), rowData.getStringProperty("pubdate"));
        
    }
    public void showPublicationInfo (final String bibcode) {
        Log.info("[SearchPresenter] showPublicationInfo BIBCODE received: " + bibcode + " , preparing publication info.");
        final String publicationDetailsId = "Publication Details";
        CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPushEvent(publicationDetailsId, TextMgr.getInstance().getText("ctrlToolBarPresenter_loadPublicationDetails")));

        
        //Retrieves the publication details for this bibcode and checks if publications exists
        JSONUtils.getJSONFromUrl(EsaSkyWebConstants.PUBLICATIONS_DETAILS_URL + "?BIBCODE="
                + URL.encodeQueryString(bibcode), new IJSONRequestCallback() {
            
            @Override
            public void onSuccess(String responseText) {
            	parsePublicationBibcodeResponse(bibcode, publicationDetailsId, responseText);
            }

            @Override
            public void onError(String errorCause) {
                Log.error("[SearchPresenter] showPublicationInfo, error fetching details. ERROR: " + errorCause);
                CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPopEvent(publicationDetailsId));
            }
            
        });
    }
    
	private void parsePublicationBibcodeResponse(final String bibcode, final String publicationDetailsId,
			String responseText) {
		CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPopEvent(publicationDetailsId));
        
        TapRowListMapper mapper = GWT.create(TapRowListMapper.class);
        TapRowList rowList = mapper.read(responseText);
        
        if (!rowList.getData().isEmpty()) {
        	StringBuilder sb = new StringBuilder();
            
            for(int i = 0; i < rowList.getData().size(); i++) {
            	sb.append(rowList.getDataValue("author", i) + "\n");
            }
            sb.setLength(Math.max(0, sb.length() - 1));
            
            getPublicationSources(bibcode, rowList.getDataValue("title", 0), sb.toString(), rowList.getDataValue("pub", 0), rowList.getDataValue("pubdate", 0));
            
        } else {
            Log.warn("[SearchPresenter] showPublicationInfo, no publication details found for bibcode: " + bibcode);
        }
	}

	private static synchronized void updateLatestBibCodeTimeCall(long timecall) {
		latestBibCodeTimeCall = timecall;
	}
	
    private void getPublicationSources(final String bibcode, final String title, final String authors, final String journal, final String date) {
        final String retrievingSourcesId = "Retrieving Sources";
        final int maxSources = (DeviceUtils.isMobile() ? EsaSkyWebConstants.MAX_SHAPES_FOR_MOBILE : EsaSkyWebConstants.MAX_SOURCES_IN_TARGETLIST);
        
        final long timecall = System.currentTimeMillis();
        SearchPresenter.updateLatestBibCodeTimeCall(timecall);
        
        String url = EsaSkyWebConstants.PUBLICATIONS_SOURCES_BY_BIBCODE_URL + "?BIBCODE="
                + URL.encodeQueryString(bibcode) + "&ROWS=" + maxSources;
        CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPushEvent(retrievingSourcesId, 
                TextMgr.getInstance().getText("ctrlToolBarPresenter_retrievingPublicationTargetList"),
                        url));
        //Retrieves the sources for this bibcode and shows the upload panel
        JSONUtils.getJSONFromUrl(url, new IJSONRequestCallback() {
            
            @Override
            public void onSuccess(String responseText) {
                CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPopEvent(retrievingSourcesId));

                if(timecall < latestBibCodeTimeCall) {
                    Log.warn("discarded bibcode " + bibcode + " target list, since there are newer list requests");
                    return;
                }
                parseSourcesByBibcodeResponse(bibcode, title, authors, journal, date, maxSources, responseText);
            }

            @Override
            public void onError(String errorCause) {
                Log.error("[SearchPresenter] showPublicationInfo ERROR: " + errorCause);
                CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPopEvent(retrievingSourcesId));
            }
            
        });
    }

	private void parseSourcesByBibcodeResponse(final String bibcode, final String title, final String authors,
			final String journal, final String date, final int maxSources, String responseText) {
		//Shows the sources for this publication
        final List<ESASkySearchResult> searchResult = ParseUtils.parseJsonSearchResults(responseText);
        final CommonTapDescriptor descriptor = DescriptorRepository.getInstance().getFirstDescriptor(EsaSkyWebConstants.CATEGORY_PUBLICATIONS);
        final String titleHtml = H3_OPEN + title + H3_CLOSE +
                H5_OPEN + TextMgr.getInstance().getText("ctrlToolBarPresenter_bibcode").replace(HTML_REPLACE, getLinkHtml(bibcode, descriptor.getArchiveBaseURL(), descriptor.getArchiveProductURI()).asString()) + H5_CLOSE +
                H5_OPEN + TextMgr.getInstance().getText("ctrlToolBarPresenter_authors").replace(HTML_REPLACE, ESASkyJavaScriptLibrary.createLinkList(authors, 3)) + H5_CLOSE +
                H5_OPEN + TextMgr.getInstance().getText("ctrlToolBarPresenter_journal").replace(JOURNAL_REPLACE, journal).replace(DATE_REPLACE, date) + H5_CLOSE +
                H4_OPEN + TextMgr.getInstance().getText("ctrlToolBarPresenter_pubSources") + H4_CLOSE;
        view.showSearchResultsOnTargetList(searchResult, titleHtml + getNumSourcesText(searchResult.size(), maxSources));
	}
    
    public void showAuthorInfo (final String author, final String splitByString, final String authorsLinkUrl, final String replaceString) {
        
        Log.info("[SearchPresenter] showAuthorInfo AUTHOR received: " + author + " , preparing author info.");
        
        final int maxSources = (DeviceUtils.isMobile() ? EsaSkyWebConstants.MAX_SHAPES_FOR_MOBILE : EsaSkyWebConstants.MAX_SOURCES_IN_TARGETLIST);
        CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPushEvent("LoadingAuthorPublicatoinSources", 
        		TextMgr.getInstance().getText("ctrlToolBarPresenter_loadingAuthorSources").replace("$AUTHOR$", author)));
        //Retrieves the sources for this bibcode and shows the upload panel
        JSONUtils.getJSONFromUrl(EsaSkyWebConstants.PUBLICATIONS_SOURCES_BY_AUTHOR_URL + "?AUTHOR="
                + URL.encodeQueryString(author) + "&ROWS=" + maxSources, new IJSONRequestCallback() {
            
            @Override
            public void onSuccess(String responseText) {
            	try {
            		parseSourcesByAuthorResponse(author, splitByString, authorsLinkUrl, replaceString, maxSources,
							responseText);
            	} catch(Exception e) {
            		Log.error("SearchPresenter", "Could not parse content", e);
            		onError(e.toString());
            	}
            }

            @Override
            public void onError(String errorCause) {
                Log.error("[SearchPresenter] showAuthorInfo ERROR: " + errorCause);
                CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPopEvent("LoadingAuthorPublicatoinSources"));
            }
            
        });
    }
    
	private void parseSourcesByAuthorResponse(final String author, final String splitByString,
			final String authorsLinkUrl, final String replaceString, final int maxSources,
			String responseText) {
		CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPopEvent("LoadingAuthorPublicatoinSources"));
		String authorHtml = getLinkList(author, 
				splitByString,
				authorsLinkUrl,
				replaceString,
				EsaSkyWebConstants.PUBLICATIONS_SHOW_ALL_AUTHORS_TEXT, 
				EsaSkyWebConstants.PUBLICATIONS_MAX_AUTHORS).asString();
		
		final String titleHtml = H3_OPEN + author + H3_CLOSE +
				H5_OPEN + TextMgr.getInstance().getText("ctrlToolBarPresenter_adsSearch").replace(HTML_REPLACE, authorHtml) + H5_CLOSE +
				H4_OPEN + TextMgr.getInstance().getText("ctrlToolBarPresenter_authorSources") + H4_CLOSE;
		
		//Shows the sources for this publication
		final List<ESASkySearchResult> searchResult = ParseUtils.parseJsonSearchResults(responseText);
		view.showSearchResultsOnTargetList(searchResult, titleHtml + getNumSourcesText(searchResult.size(), maxSources));
	}
    
    private SafeHtml getLinkList(String linkListValue, String splitByString, String linkUrl, String replaceString, String showAllString, int maxLinks) {
        SafeHtmlBuilder sb = new SafeHtmlBuilder();

        String[] valueList = linkListValue.split(splitByString);
        String styleStr = "";
        int appendedLinks = 0;
        boolean showAllAppended = false;
        for (String value : valueList) {
            String finalURL = linkUrl.replace(replaceString, replaceLast(value.replaceAll("'", "%27").replaceAll(" ", "%20"), "%20", "%2C%20"));
            final boolean isLastLink = (appendedLinks == valueList.length -1);
            
                sb.appendHtmlConstant("<a href='" + finalURL
                                        + "' onclick=\"esasky.trackOutbound(this)\" target='_blank' " + styleStr + ">"
                                        + value + ((!isLastLink) ? "," : "" ) + "</a>&nbsp;");
                
                if (appendedLinks > maxLinks && !showAllAppended && !isLastLink) {
                
                    sb.appendHtmlConstant("<a href='#' " 
                                    + "onclick=\"$(this).parent().find('a').fadeIn(); $(this).hide(); " 
                                    + "event.stopPropagation(); return false; \" >" 
                                        + showAllString + "</a>");
                    
                styleStr = "style=\"display: none;\" ";
                showAllAppended = true;
            }
            
            appendedLinks ++;
        }
        
        return sb.toSafeHtml();
    }
    
    private String replaceLast(String string, String find, String replace) {
        int lastIndex = string.lastIndexOf(find);
        
        if (lastIndex == -1) {
            return string;
        }
        
        String beginString = string.substring(0, lastIndex);
        String endString = string.substring(lastIndex + find.length());
        
        return beginString + replace + endString;
    }
    
    private String getNumSourcesText(int numSources, int maxSources) {
        if (numSources == maxSources) {
            return "<div style='font-size: 0.7em; color: yellow;'>" + TextMgr.getInstance().getText("ctrlToolBarPresenter_showingMaxSources").replace("$COUNT$", maxSources + "") + "</div>";
        } else {
            return "<div style='font-size: 0.7em; color: #CCCCCC;'>" + TextMgr.getInstance().getText("ctrlToolBarPresenter_showingCountSources").replace("$COUNT$", numSources + "") + "</div>";
        }
    }
    
   private SafeHtml getLinkHtml(String value, String archiveURL, String archiveProductUrl) {
        
        String[] archiveProductURI = archiveProductUrl.split("@@@");

        StringBuilder finalURI = new StringBuilder(archiveURL);
        for (int i = 0; i < archiveProductURI.length; i++) {
            if (i % 2 == 0) {
                finalURI.append(archiveProductURI[i]);
            } else {
                finalURI.append(value);
            }
        }
        
        SafeHtmlBuilder sb = new SafeHtmlBuilder();
        
        sb.appendHtmlConstant("<a href='" + finalURI.toString()
                + "' onclick=\"esasky.trackOutbound(this)\" target='_blank' >"
                + value + "</a>");
     
        return sb.toSafeHtml();
    }

    public TargetListPanel getTargetListPanel() {
        return view.getTargetListPanel();
    }

    public void showTargetList() {
        view.showTargetList();
    }

    public void showTargetList(String targetList) {
        view.showTargetList(targetList);
    }

    public void closeTargetList() {
        view.closeTargetList();
    }

    public void toggleTargetList() {
        view.toggleTargetList();
    }

    public void showSearchTool() {
        view.showSearchTool();
    }

    public void closeSearchTool() {
        view.closeSearchTool();
    }

    public boolean setConeSearchArea(String ra, String dec, String radius) {
        return view.setConeSearchArea(ra, dec, radius);
    }

    public boolean setPolygonSearchArea(String stcs) {
        return view.setPolygonSearchArea(stcs);
    }

    public void clearSearchArea() {
        view.clearSearchArea();
    }
}
