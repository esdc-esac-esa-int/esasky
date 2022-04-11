package esac.archive.esasky.cl.web.client.view.searchpanel;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.DataResource.MimeType;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteConstants;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.Modules;
import esac.archive.esasky.cl.web.client.event.AuthorSearchEvent;
import esac.archive.esasky.cl.web.client.event.BibcodeSearchEvent;
import esac.archive.esasky.cl.web.client.event.CloseOtherPanelsEvent;
import esac.archive.esasky.cl.web.client.event.sso.SSOCrossMatchEvent;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.presenter.SearchPresenter;
import esac.archive.esasky.cl.web.client.status.GUISessionStatus;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.allskypanel.SearchToolPanel;
import esac.archive.esasky.cl.web.client.view.animation.AnimationObserver;
import esac.archive.esasky.cl.web.client.view.animation.CssPxAnimation;
import esac.archive.esasky.cl.web.client.view.common.buttons.CloseButton;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyButton;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyToggleButton;
import esac.archive.esasky.cl.web.client.view.common.icons.Icons;
import esac.archive.esasky.cl.web.client.view.searchpanel.targetlist.TargetListPanel;
import esac.archive.esasky.ifcs.model.shared.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ESDC team Copyright (c) 2015- European Space Agency
 */
public class SearchPanel extends Composite implements SearchPresenter.View {

    private FlowPanel container;
    private final TextBox searchTextBox = new TextBox();

    private FocusPanel tooltip;
    private FocusPanel searchTextBoxError;
    private Label searchBoxErrorLabel = new Label();
    
    private FocusPanel searchResultsFocusPanel;
    private FlowPanel resultsList;
    private CloseButton clearTextButton;
    private EsaSkyToggleButton targetListButton;
    private TargetListPanel targetListPanel;
    private Image searchIcon;
    private Image ssoDNetLogo;
    private Image simbadLogo;

    private SearchToolPanel searchToolPanel;
    private EsaSkyToggleButton searchToolBoxButton;
    
    private boolean foundInSimbad = false;
    private boolean foundAuthorInSimbad = false;
    private boolean foundExactMatchAuthorInSimbad = false;
    private boolean foundBibcodeInSimbad = false;
    private boolean foundExactMatchBibcodeInSimbad = false;
    private boolean foundInSSODnet = false;

    private Map<String, Widget> authorList = new HashMap<String, Widget>(100);
    private Map<String, Widget> bibcodeList = new HashMap<String, Widget>(100);
    private Map<String, Widget> ssoList = new HashMap<String, Widget>(100);
    
    private CssPxAnimation searchWidthAnimation;
    private CssPxAnimation searchPaddingAnimation;
    
    private boolean resultPanelWasOpen = false;
    private boolean isFullSize = true;
    
    private Resources resources = GWT.create(Resources.class);
    private CssResource style;
    
    private final String SEARCH_RESULT_ENTRY = "searchResultEntry";

    public static interface Resources extends ClientBundle {

		@Source("search.png")
		ImageResource searchIcon();
		
		@Source("simbad.png")
		ImageResource simbadLogo();

		@Source("logo_IMCCE_web_ssodnet.svg")
		@MimeType("image/svg+xml")
		DataResource ssoDNetLogo();
		
        @Source("searchPanel.css")
        @CssResource.NotStrict
        CssResource style();
    }

    public SearchPanel() {
        this.style = this.resources.style();
        this.style.ensureInjected();
        
        initView();
    }
    
    private void initView() {

        FlowPanel searchPanel = new FlowPanel();
        searchPanel.getElement().setId("allSkySearchPanel");
        searchPanel.getElement().getStyle().setPosition(Position.ABSOLUTE);
        
        searchIcon = new Image(this.resources.searchIcon());
        searchIcon.addStyleName("searchIcon");
        searchPanel.add(searchIcon);

        clearTextButton = new CloseButton();
        clearTextButton.getElement().getStyle().setTop(5, Unit.PX); 
        clearTextButton.setDarkStyle();
        clearTextButton.setSecondaryIcon();
        clearTextButton.addStyleName("clearSearchTextBtn");
        clearTextButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                GUISessionStatus.setIsTrackingSSO(false);
                searchTextBox.setValue("");
                searchResultsFocusPanel.setVisible(false);
                ensureClearTextButtonVisibilty();
                searchTextBox.setFocus(true);
                if (!searchToolBoxButton.getToggleStatus()) {
                    AladinLiteWrapper.getAladinLite().clearSearchArea();
                }
            }
        });
        FlowPanel textBoxHolder = new FlowPanel();
        textBoxHolder.getElement().getStyle().setPosition(Position.RELATIVE);
        textBoxHolder.getElement().setId("textBoxHolder");
        this.searchTextBox.getElement().setAttribute("autocomplete", "off");
        this.searchTextBox.getElement().setId("allSkySearchTextBox");
        this.searchTextBox.addStyleName("searchTextBox");
        this.searchTextBox.getElement().setPropertyString("placeholder", TextMgr.getInstance().getText("searchPanel_search"));
        searchTextBox.addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                ensureClearTextButtonVisibilty();
                GUISessionStatus.setIsTrackingSSO(false);
            }
        });
        searchTextBox.addKeyPressHandler(new KeyPressHandler() {

            @Override
            public void onKeyPress(KeyPressEvent event) {
                ensureClearTextButtonVisibilty();
                GUISessionStatus.setIsTrackingSSO(false);
            }
        });
        searchTextBox.addKeyUpHandler(new KeyUpHandler() {

            @Override
            public void onKeyUp(KeyUpEvent event) {
                ensureClearTextButtonVisibilty();
            }
        });

        searchTextBox.getElement().getStyle().setPropertyPx("width", 150);
        searchWidthAnimation = new CssPxAnimation(searchTextBox.getElement(), "width");
        searchPaddingAnimation = new CssPxAnimation(searchTextBox.getElement(), "paddingRight");
        
        searchWidthAnimation.addObserver(new AnimationObserver() {
			
			@Override
			public void onComplete(double currentPosition) {
				setSearchIconVisibility();
                if(!isFullSize) {
                    searchToolPanel.setVisible(false);
                }
			}
		});

        this.tooltip = new FocusPanel();
        this.tooltip.getElement().setId("tooltipCoords");
        this.tooltip
                .getElement()
                .setInnerHTML(
                        "<p class=\"coords\"><b>" + TextMgr.getInstance().getText("searchPanel_inputByTargetName") + "</b><br/>"
                        + "Antares<br/>"
                        + "M31<br/>"
                        + "PKS2357-326<br/>"
                        + "crab<br/>"
                        + "IRC +10216 </p>"
                        + "<hr/>"
                        + "<p class=\"coords\"><b>" + TextMgr.getInstance().getText("searchPanel_solarSystemObjects") + "</b><br/>"
                        + "<u>" + TextMgr.getInstance().getText("searchPanel_inputByName") + "</u><br/>"
                        + "Churyumov-Gerasimenko<br/>"
                        + "Saturn<br/>"
                        + "<u>" + TextMgr.getInstance().getText("searchPanel_inputByObjectNumber") + "</u><br/>"
                        + "67P<br/>"
                        + "6<br/>"
                        + "<hr/>"
                        + "<p class=\"coords\"><b>" + TextMgr.getInstance().getText("searchPanel_inputByCoordinates") + "</b><br/>"
                        + "14 05 27.29 +28 32 04.0<br/>" 
                        +"10:12:45.3 -45:17:50<br/>" 
                        +"102.036991 +59.771411<br/>"
                        + "</p>"
                        + "<hr/>"
                        + "<p class=\"coords\"><b>" + TextMgr.getInstance().getText("searchPanel_inputByAuthor") + "</b><br/>"
                        + "Messier C.<br/>" 
                        + "Hubble E.<br/>" 
                        + "Rubin V.<br/>" 
                        + "<hr/>"
                        + "<p class=\"coords\"><b>" + TextMgr.getInstance().getText("searchPanel_inputByBibcode") + "</b><br/>"
						+ "1850CDT..1784..227M<br/>" 
						+ "1953ApJ...118..353H<br/>" 
						+ "1968Natur.217..709H<br/>" 
                        + "<p class=\"coords\"><a href=\"//www.cosmos.esa.int/web/esdc/esasky-search-formats\" target=\"_blank\">" + TextMgr.getInstance().getText("searchPanel_seeMoreExamples") + "</a></p>");
        
        
        this.tooltip.setVisible(false);

        searchToolPanel = new SearchToolPanel();
        searchToolPanel.getElement().getStyle().setTop(30, Unit.PX);
        searchToolPanel.getElement().getStyle().setLeft(30, Unit.PX);
        searchToolPanel.getElement().getStyle().setZIndex(203);
        searchToolPanel.getElement().getStyle().setPosition(Position.ABSOLUTE);

        this.searchTextBoxError = new FocusPanel();
        this.searchTextBoxError.getElement().setId("searchTextBoxError");
        searchBoxErrorLabel.addStyleName("searchBoxLabel");
        this.searchTextBoxError.add(searchBoxErrorLabel);
        this.searchTextBoxError.setVisible(false);

        searchResultsFocusPanel = new FocusPanel();
        searchResultsFocusPanel.getElement().setId("searchResultsListPanel");
        searchResultsFocusPanel.setVisible(false);
        
        resultsList = new FlowPanel();
        searchResultsFocusPanel.add(resultsList);

        textBoxHolder.add(this.searchTextBox);
        textBoxHolder.add(createTargetListBtn());
        textBoxHolder.add(createSearchToolboxBtn());
        MainLayoutPanel.addElementToMainArea(targetListPanel);
		targetListPanel.hide();
        textBoxHolder.add(clearTextButton);
        searchPanel.add(textBoxHolder);
        searchPanel.add(this.tooltip);
        searchPanel.add(this.searchTextBoxError);
        searchPanel.add(this.searchResultsFocusPanel);
        searchPanel.add(this.searchToolPanel);

        this.container = new FlowPanel();

        this.container.add(searchPanel);

        this.container.getElement().setId("allSkySearchPanelContainer");
            
        initWidget(this.container);
        ensureClearTextButtonVisibilty();
        
        final Timer afterLoadTimer = new Timer() {
    		
    		@Override
    		public void run() {
    	        ensureClearTextButtonVisibilty();
				cancel();
    		}
    	};
    	afterLoadTimer.schedule(1000);
    	simbadLogo = new Image(resources.simbadLogo());
    	simbadLogo.addStyleName("searchPanel__logo");
    	ssoDNetLogo = new Image(resources.ssoDNetLogo().getSafeUri());
    	ssoDNetLogo.addStyleName("searchPanel__logo");
    }

    @Override
    protected void onLoad() {
        int left = MainLayoutPanel.getMainAreaWidth() - targetListPanel.getOffsetWidth() - 30;
        targetListPanel.setSuggestedPosition(left, 75);
        targetListPanel.definePositionFromTopAndLeft();
    }

    private Widget createSearchToolboxBtn() {

        FlowPanel selectionContainer = new FlowPanel();

        searchToolBoxButton = new EsaSkyToggleButton(Icons.getConeIcon());
        searchToolBoxButton.getElement().setId("searchPanelImg");
        searchToolBoxButton.setMediumStyle();
        searchToolBoxButton.setDarkStyle();
        searchToolBoxButton.addStyleName("searchPanel__selectionToolButton");
        searchToolBoxButton.setTitle(TextMgr.getInstance().getText("searchToolbox_title"));

        searchToolBoxButton.addClickHandler(event -> {
            SearchPanel.this.searchToolPanel.toggleToolbox();
            CommonEventBus.getEventBus().fireEvent(new CloseOtherPanelsEvent(searchToolBoxButton));
        });

        selectionContainer.add(searchToolBoxButton);

        if (!Modules.getModule(EsaSkyWebConstants.MODULE_SEARCH_TOOL)) {
            searchToolBoxButton.getElement().getStyle().setDisplay(Display.NONE);
        }

        return selectionContainer;
    }

	private EsaSkyButton createTargetListBtn() {
		targetListPanel = new TargetListPanel();

		targetListButton = new EsaSkyToggleButton(Icons.getTargetListIcon());
		targetListButton.getElement().setId("targetListImg");
		targetListButton.setMediumStyle();
		targetListButton.setDarkStyle();
		targetListButton.addStyleName("searchPanel__targetListButton");
		targetListButton.setTitle(TextMgr.getInstance().getText("webConstants_uploadTargetList"));
        targetListButton.getElement().getStyle().setTop(1, Unit.PX);

		targetListButton.addClickHandler(event -> {
			SearchPanel.this.targetListPanel.toggle();
			CommonEventBus.getEventBus().fireEvent(new CloseOtherPanelsEvent(targetListButton));

			//To keep relevant history of events, the old name of ctrlToolBar is kept
			GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_CTRLTOOLBAR, GoogleAnalytics.ACT_CTRLTOOLBAR_TARGETLIST, "");
		});
		
		targetListPanel.addCloseHandler(event -> targetListButton.setToggleStatus(false));

		return targetListButton;
	}
    
    private void ensureClearTextButtonVisibilty() {
        if (searchTextBox.getValue().isEmpty() ) {
            clearTextButton.addStyleName("collapse");
            targetListButton.removeStyleName("collapse");
            searchToolBoxButton.removeStyleName("collapse");

        } else {
        	targetListButton.addStyleName("collapse");
            clearTextButton.removeStyleName("collapse");
            searchToolBoxButton.addStyleName("collapse");
        }
    }
    
    private void setSearchIconVisibility() {
    	if (isFullSize) {
    		searchIcon.setVisible(false);
    	} else {
    		searchIcon.setVisible(true);
		} 
    }

    @Override
    public void showCoordsInputErrorMessage() {
        this.searchBoxErrorLabel.setText(TextMgr.getInstance().getText("searchPanel_wrongCoordsInput"));
        this.searchTextBoxError.setVisible(true);
        this.searchResultsFocusPanel.setVisible(false);
    }

    @Override
    public void showTargetNotFoundMessage() {
        this.searchBoxErrorLabel.setText(TextMgr.getInstance().getText("searchPanel_targetNotFound"));
        this.searchTextBoxError.setVisible(true);
        this.searchResultsFocusPanel.setVisible(false);
    }

    @Override
    public void showSSOTargetNotFoundMessage() {
        this.searchBoxErrorLabel.setText(TextMgr.getInstance().getText("searchPanel_ssoTargetNotFound"));
        this.searchTextBoxError.setVisible(true);
        this.searchResultsFocusPanel.setVisible(false);
    }

    
    @Override
    public void showGeneralTargetResultsPanel(ESASkyGeneralResultList resultList, String input, boolean unrecognizedInput) {
        this.resultsList.clear();
        if(searchToolBoxButton.getToggleStatus()) {
        	searchToolBoxButton.toggle();
        	searchToolPanel.toggleToolbox();
        }
        
        final ESASkySearchResult simbadResult = resultList.getSimbadResult();

        foundInSimbad = false;
        foundAuthorInSimbad = false;
        foundBibcodeInSimbad = false;
        foundInSSODnet = false;
        foundExactMatchAuthorInSimbad = false;
        foundExactMatchBibcodeInSimbad = false;

        if (simbadResult != null && simbadResult.getServerMessage().isEmpty()) {
            foundInSimbad = true;
    		FocusPanel menuEntry = new FocusPanel();
    		menuEntry.getElement().setId(SEARCH_RESULT_ENTRY);
    		menuEntry.addClickHandler(new ClickHandler() {
    			
    			@Override
    			public void onClick(ClickEvent event) {
	                AladinLiteWrapper.getInstance().goToTarget(simbadResult.getSimbadRaDeg(),
	                        simbadResult.getSimbadDecDeg(), simbadResult.getFoVDeg(), false,
	                        AladinLiteConstants.FRAME_J2000);
	                SearchPanel.this.searchResultsFocusPanel.setVisible(false);
	                
	                GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_SEARCH, GoogleAnalytics.ACT_SEARCH_SEARCHRESULTCLICK, "SIMBAD: " + simbadResult.getSimbadMainId());
    			}
    		});
    		
    		FlowPanel container = new FlowPanel();
    		container.addStyleName("searchPanel__firstResultOfItsKindContainer");
    		Label typeLabel = new Label("[" + TextMgr.getInstance().getText("searchPanel_target") + "]");
    		typeLabel.addStyleName("searchPanel__resultType");
    		container.add(typeLabel);
    		
    		Label name = new Label(simbadResult.getSimbadMainId());
    		name.addStyleName("searchPanel__resultName");
    		container.add(name);
    		menuEntry.add(container);
//			name.addStyleName("searchPanel__firstResultOfItsKindLabel");
			container.add(simbadLogo);
    		this.resultsList.add(menuEntry);
        }
        
        authorList.clear();
        addAuthorEntries(resultList.getSimbadAuthorResultExact());
        if(authorList.size() > 0 ) {
        	foundExactMatchAuthorInSimbad = true;
        	Log.debug("Exact simbad author name found");
        } 
        addAuthorEntries(resultList.getSimbadAuthorResultWithWildcards());
        
        bibcodeList.clear();
        addBibcodeEntries(resultList.getSimbadBibcodeResultExact());
        if(bibcodeList.size() > 0 ) {
        	foundExactMatchBibcodeInSimbad = true;
        	Log.debug("Exact simbad bibcode name found");
        } 
        addBibcodeEntries(resultList.getSimbadBibcodeResultWithWildcards());
        

        ssoList.clear();
        final ESASkySSOSearchResultList ssodnetResult = resultList.getSsoDnetResults();
        if (ssodnetResult != null) {

            for (final ESASkySSOSearchResult currSSO : ssodnetResult.getResults()) {
                Log.debug("ITERATING SSO");
                
        		FocusPanel menuEntry = new FocusPanel();
        		menuEntry.getElement().setId(SEARCH_RESULT_ENTRY);
        		
        		boolean isLinkToESAArchive = false;
        		
        		FlowPanel container = new FlowPanel();
        		container.addStyleName("searchPanel__firstResultOfItsKindContainer");
        		Label ssoTypeLabel = new Label("[" + TextMgr.getInstance().getText(currSSO.getSsoObjType().name()) + "]");
        		ssoTypeLabel.addStyleName("searchPanel__resultType");
        		container.add(ssoTypeLabel);
        		
        		Label ssoName = new Label();
        		ssoName.addStyleName("searchPanel__resultName");
        		if("Earth".equals(currSSO.getName())) {
        		    isLinkToESAArchive = true;
        		    ssoName.setText("ESA Earth Online");
                    addLinkToEsaArchive(menuEntry, ssoTypeLabel, "https://earth.esa.int/eogateway/", ssoName.getText());
        		} else if("Sun".equals(currSSO.getName())) {
        		    ssoName.setText("Solar Orbiter Archive");
        		    isLinkToESAArchive = true;
        		    addLinkToEsaArchive(menuEntry, ssoTypeLabel, "http://soar.esac.esa.int/", "Solar Orbiter Archive");
                } else if("Moon".equals(currSSO.getName())) {
                    isLinkToESAArchive = true;
                    container.getElement().getStyle().setPropertyPx("marginLeft", -1);
                    container.getElement().getStyle().setPropertyPx("marginRight", -3);
                    ssoName.setText("Moon data from the Planetary Science Archive");
                    addLinkToEsaArchive(menuEntry, ssoTypeLabel, "https://archives.esac.esa.int/psa/#!Table%20View/Moon=target", "PSA Moon");
                } else {
        		    ssoName.setText(currSSO.getName());
        		    menuEntry.addClickHandler((ClickEvent event)-> {
        		            CommonEventBus.getEventBus().fireEvent(
        		                    new SSOCrossMatchEvent(currSSO.getName(), currSSO.getSsoObjType()));
        		            SearchPanel.this.searchResultsFocusPanel.setVisible(false);
        		            
        		            GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_SEARCH, GoogleAnalytics.ACT_SEARCH_SEARCHRESULTCLICK, "SSO: " + currSSO.getName());
    		        });
        		}
        		container.add(ssoName);
        		menuEntry.add(container);
        		if(!foundInSSODnet) {
        			foundInSSODnet = true;
    				if(!isLinkToESAArchive) {
    				    ssoName.addStyleName("searchPanel__firstResultOfItsKindLabel");
    				    container.add(ssoDNetLogo);
    				}
    				if(foundInSimbad || foundAuthorInSimbad || foundBibcodeInSimbad) {
    					menuEntry.addStyleName("searchPanel__firstResultOfItsKind");
    				}
        		}
        		this.resultsList.add(menuEntry);
                ssoList.put(currSSO.getName(), menuEntry);
                if(ssoList.size() > 3) {
                	menuEntry.setVisible(false);
                }
        		
            }
            
			if(ssoList.size() > 3) {
				final Label showMoreSsoLabel = new Label();
				showMoreSsoLabel.getElement().setId("searchPanel__showMoreLabel");
				showMoreSsoLabel.setText(TextMgr.getInstance().getText("searchPanel_showMoreSso"));
				showMoreSsoLabel.setTitle(TextMgr.getInstance().getText("searchPanel_showMoreSsoTooltip"));
				showMoreSsoLabel.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						for(Widget label : ssoList.values()) {
							label.setVisible(true);
						}
						showMoreSsoLabel.setVisible(false);
						GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_SEARCH, GoogleAnalytics.ACT_SEARCH_SEARCHSSORESULTSHOWMORECLICK, "User input: " 
						+ ssodnetResult.getUserInput() + " All SSOs: " + ssoList.values());
					}
				});
				
				this.resultsList.add(showMoreSsoLabel);
			}
            
        }
        
        if (!foundInSimbad && !foundInSSODnet && !foundAuthorInSimbad && !foundBibcodeInSimbad) {
            this.resultsList.clear();
            if(unrecognizedInput) {
                showCoordsInputErrorMessage();
                Log.debug("ERROR FOR WRONG INPUT FORMAT");
                GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_SEARCH, GoogleAnalytics.ACT_SEARCH_SEARCHWRONGCOORDS, input);                
            } else {
                showTargetNotFoundMessage();
                GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_SEARCH, GoogleAnalytics.ACT_SEARCH_SEARCHTARGETNOTFOUND, searchTextBox.getText());
            }

        } else if (foundInSimbad && !foundInSSODnet && !foundAuthorInSimbad && !foundBibcodeInSimbad) {
            this.resultsList.clear();
            this.searchResultsFocusPanel.setVisible(false);
            AladinLiteWrapper.getInstance().goToTarget(simbadResult.getSimbadRaDeg(),
                    simbadResult.getSimbadDecDeg(), simbadResult.getFoVDeg(), false,
                    AladinLiteConstants.FRAME_J2000);
            GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_SEARCH, GoogleAnalytics.ACT_SEARCH_SEARCHRESULTAUTO, "SIMBAD: " + simbadResult.getSimbadMainId());
            
        } else {
            this.searchResultsFocusPanel.setVisible(true);
        }

    }

    private void addLinkToEsaArchive(FocusPanel menuEntry, Label ssoTypeLabel, String link, String eventName) {
        ssoTypeLabel.setVisible(false);
        menuEntry.addClickHandler((ClickEvent event) -> {
                GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_SEARCH, GoogleAnalytics.ACT_SEARCH_SEARCHRESULTCLICK, eventName);
                Window.open(link, "_blank", "");
                SearchPanel.this.searchResultsFocusPanel.setVisible(false);
        });
    }

    public TextBox getSearchTextBox() {
        return this.searchTextBox;
    }

    @Override
    public FocusPanel getTooltip() {
        return this.tooltip;
    }

    @Override
    public FocusPanel getSearchTextBoxError() {
        return this.searchTextBoxError;
    }

    @Override
    public FocusPanel getSearchResultsListPanel() {
        return this.searchResultsFocusPanel;
    }
    
    @Override
    public CloseButton getClearTextButton() {
        return this.clearTextButton;
    }
    
    @Override
    public EsaSkyButton getTargetListButton() {
    	return this.targetListButton;
    }

    @Override
    public EsaSkyToggleButton getSearchToolButton() {
        return searchToolBoxButton;
    }

    @Override
    public SearchToolPanel getSearchToolPanel() {
        return searchToolPanel;
    }

    @Override
    public void showBackendMessage(String message) {
        this.searchBoxErrorLabel.setText(message);
        this.searchTextBoxError.setVisible(true);
        this.searchResultsFocusPanel.setVisible(false);
    }
    
	@Override
	public void setFullSize(boolean fullSize) {
		isFullSize = fullSize;
		if(isFullSize) {
            this.searchToolPanel.setVisible(true);
			searchWidthAnimation.animateTo(150, 500);
			searchPaddingAnimation.animateTo(35, 500);
			if(resultPanelWasOpen && resultsList.getWidgetCount() > 0) {
				searchResultsFocusPanel.setVisible(true);
			}
			searchIcon.setVisible(false);

		} else {
			searchWidthAnimation.animateTo(0, 500);
			searchPaddingAnimation.animateTo(15, 500);
			
			if(searchResultsFocusPanel.isVisible()) {
				resultPanelWasOpen = true;
			}
			searchResultsFocusPanel.setVisible(false);
			tooltip.setVisible(false);
			searchTextBoxError.setVisible(false);
			
			searchTextBox.setFocus(false);
		}
	}
	
	@Override
	public void closeAllOtherPanels(Widget widgetNotToClose){
		if(!widgetNotToClose.equals(targetListButton)) {
			targetListPanel.hide();
		} else if (!widgetNotToClose.equals(searchToolBoxButton)) {
            searchToolPanel.hideToolbox();
            searchToolBoxButton.setToggleStatus(false);
        }
	}
	
	private void addAuthorEntries(final ESASkyPublicationSearchResultList simbadAuthorResult) {
        if (simbadAuthorResult != null && (simbadAuthorResult.getErrorMessage() == null || simbadAuthorResult.getErrorMessage().isEmpty())) {
	        for(final ESASkyPublicationSearchResult publicationResult : simbadAuthorResult.getResults()) {
	        	addAuthorEntry(publicationResult);
	        }
	        if(authorList.size() > 3 || (foundExactMatchAuthorInSimbad && authorList.size() > 1)) {
	        	final Label showMoreAuthorsLabel = new Label();
	        	showMoreAuthorsLabel.getElement().setId("searchPanel__showMoreLabel");
	        	showMoreAuthorsLabel.setText(TextMgr.getInstance().getText("searchPanel_showMoreAuthors"));
	        	showMoreAuthorsLabel.setTitle(TextMgr.getInstance().getText("searchPanel_showMoreAuthorsTooltip").replace("$COUNT$", "100"));
	        	showMoreAuthorsLabel.addClickHandler(new ClickHandler() {
	        		
	        		@Override
	        		public void onClick(ClickEvent event) {
	        			for(Widget label : authorList.values()) {
	        				label.setVisible(true);
	        			}
	        			showMoreAuthorsLabel.setVisible(false);
	        			GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_SEARCH, GoogleAnalytics.ACT_SEARCH_SEARCHAUTHORRESULTSHOWMORECLICK, "User input: " + simbadAuthorResult.getUserInput() + " All authors: " + authorList.values());
	        		}
	        	});
	        	
	        	this.resultsList.add(showMoreAuthorsLabel);
	        }
        }
	}
	
	private void addAuthorEntry(final ESASkyPublicationSearchResult publicationResult) {
    	FocusPanel menuEntry = new FocusPanel();
    	menuEntry.getElement().setId(SEARCH_RESULT_ENTRY);
    	menuEntry.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                SearchPanel.this.searchResultsFocusPanel.setVisible(false);
                
                CommonEventBus.getEventBus().fireEvent(new AuthorSearchEvent(publicationResult.getName()));
                GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_SEARCH, GoogleAnalytics.ACT_SEARCH_SEARCHAUTHORRESULTCLICK, "SIMBAD: " + publicationResult.getName());
            }
        });

    	
    	FlowPanel container = new FlowPanel();
    	container.addStyleName("searchPanel__firstResultOfItsKindContainer");
    	Label simbadAuthorLabel = new Label("[" + TextMgr.getInstance().getText("searchPanel_author") + "]");
    	simbadAuthorLabel.addStyleName("searchPanel__resultType");
    	container.add(simbadAuthorLabel);
    	
    	Label simbadAuthorName = new Label();
    	simbadAuthorName.addStyleName("searchPanel__resultName");
        simbadAuthorName.setText(publicationResult.getName());
        container.add(simbadAuthorName);
        menuEntry.add(container);
        if(authorList.containsKey(publicationResult.getName())){
        	return;
        }
        authorList.put(publicationResult.getName(), menuEntry);
        if(authorList.size() > 3 || foundExactMatchAuthorInSimbad) {
        	menuEntry.setVisible(false);
        }
        if(!foundAuthorInSimbad) {
        	foundAuthorInSimbad = true;
            if(!foundInSimbad) {
            	simbadAuthorName.addStyleName("searchPanel__firstResultOfItsKindLabel");
            	container.add(simbadLogo);
            } else {
        		menuEntry.addStyleName("searchPanel__firstResultOfItsKind");
			}
        }
    	this.resultsList.add(menuEntry);
	}
	
	private void addBibcodeEntries(final ESASkyPublicationSearchResultList simbadBibcodeResult) {
		if (simbadBibcodeResult != null && (simbadBibcodeResult.getErrorMessage() == null || simbadBibcodeResult.getErrorMessage().isEmpty())) {
			for(final ESASkyPublicationSearchResult publicationResult : simbadBibcodeResult.getResults()) {
				addBibcodeEntry(publicationResult);
			}
			if(bibcodeList.size() > 3 || (foundExactMatchBibcodeInSimbad && bibcodeList.size() > 1)) {
				final Label showMoreBibcodeLabel = new Label();
				showMoreBibcodeLabel.getElement().setId("searchPanel__showMoreLabel");
				showMoreBibcodeLabel.setText(TextMgr.getInstance().getText("searchPanel_showMoreBibcodes"));
				showMoreBibcodeLabel.setTitle(TextMgr.getInstance().getText("searchPanel_showMoreBibcodeTooltip").replace("$COUNT$", "100"));
				showMoreBibcodeLabel.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						for(Widget label : bibcodeList.values()) {
							label.setVisible(true);
						}
						showMoreBibcodeLabel.setVisible(false);
						GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_SEARCH, GoogleAnalytics.ACT_SEARCH_SEARCHBIBCODERESULTSHOWMORECLICK, "User input: " + simbadBibcodeResult.getUserInput() + " All authors: " + authorList.values());
					}
				});
				
				this.resultsList.add(showMoreBibcodeLabel);
			}
		}
	}
	
	private void addBibcodeEntry(final ESASkyPublicationSearchResult publicationResult) {
		FocusPanel menuEntry = new FocusPanel();
		menuEntry.getElement().setId(SEARCH_RESULT_ENTRY);
		menuEntry.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				SearchPanel.this.searchResultsFocusPanel.setVisible(false);
				
				CommonEventBus.getEventBus().fireEvent(new BibcodeSearchEvent(publicationResult.getName()));
				GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_SEARCH, GoogleAnalytics.ACT_SEARCH_SEARCHBIBCODERESULTCLICK, "SIMBAD: " + publicationResult.getName());
			}
		});
		
		
		FlowPanel container = new FlowPanel();
		container.addStyleName("searchPanel__firstResultOfItsKindContainer");
		Label simbadBibcodeLabel = new Label("[" + TextMgr.getInstance().getText("searchPanel_bibcode") + "]");
		simbadBibcodeLabel.addStyleName("searchPanel__resultType");
		container.add(simbadBibcodeLabel);
		
		Label simbadBibcodeName = new Label();
		simbadBibcodeName.addStyleName("searchPanel__resultName");
		simbadBibcodeName.setText(publicationResult.getName());
		container.add(simbadBibcodeName);
		menuEntry.add(container);
		if(bibcodeList.containsKey(publicationResult.getName())){
			return;
		}
		bibcodeList.put(publicationResult.getName(), menuEntry);
		if(bibcodeList.size() > 3 || foundExactMatchBibcodeInSimbad) {
			menuEntry.setVisible(false);
		}
		if(!foundBibcodeInSimbad) {
			foundBibcodeInSimbad = true;
			if(!foundInSimbad && !foundAuthorInSimbad) {
				simbadBibcodeName.addStyleName("searchPanel__firstResultOfItsKindLabel");
				container.add(simbadLogo);
			} else {
				menuEntry.addStyleName("searchPanel__firstResultOfItsKind");
			}
		}
		this.resultsList.add(menuEntry);
	}
	
	public void updateModuleVisibility() {
		if(Modules.getModule(EsaSkyWebConstants.MODULE_TARGETLIST)) {
			targetListButton.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		}else {
			targetListButton.getElement().getStyle().setDisplay(Display.NONE);
		}
	}

    @Override
    public TargetListPanel getTargetListPanel() {
        return this.targetListPanel;
    }

    @Override
    public void showTargetList() {
        targetListPanel.show();
        targetListButton.setToggleStatus(true);
        CommonEventBus.getEventBus().fireEvent(new CloseOtherPanelsEvent(targetListButton));
    }

    @Override
    public void showTargetList(String targetList) {
        targetListPanel.setSelectedFile(targetList);
        showTargetList();
    }

    @Override
    public void closeTargetList() {
        targetListPanel.hide();
        targetListButton.setToggleStatus(false);
    }

    @Override
    public void toggleTargetList() {
        targetListPanel.toggle();
        targetListButton.setToggleStatus(targetListPanel.isShowing());
    }

    @Override
    public void showSearchTool() {
        searchToolBoxButton.setToggleStatus(true);
        searchToolPanel.showToolbox();
    }

    @Override
    public void closeSearchTool() {
        searchToolBoxButton.setToggleStatus(false);
        searchToolPanel.hideToolbox();
    }

    @Override
    public boolean setConeSearchArea(String ra, String dec, String radius) {
        searchToolBoxButton.setToggleStatus(true);
        searchToolPanel.showWithConeDetails();
        return searchToolPanel.createConicalSearchArea(ra, dec, radius);
    }

    @Override
    public boolean setPolygonSearchArea(String stcs) {
        searchToolBoxButton.setToggleStatus(true);
        searchToolPanel.showWithPolyDetails();
        return searchToolPanel.createPolygonSearchArea(stcs);
    }

    @Override
    public void clearSearchArea() {
        AladinLiteWrapper.getAladinLite().clearSearchArea();
    }

    @Override
    public void showSearchResultsOnTargetList(List<ESASkySearchResult> searchResults, String title) {
        targetListPanel.show();
        targetListButton.setToggleStatus(true);
        CommonEventBus.getEventBus().fireEvent(new CloseOtherPanelsEvent(targetListButton));
        targetListPanel.setTargetsTableData(searchResults, title);
    }
}
