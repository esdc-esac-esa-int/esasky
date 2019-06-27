package esac.archive.esasky.cl.web.client.view.searchpanel;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.DataResource.MimeType;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

import esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteConstants;
import esac.archive.esasky.ifcs.model.shared.ESASkyGeneralResultList;
import esac.archive.esasky.ifcs.model.shared.ESASkySSOSearchResult;
import esac.archive.esasky.ifcs.model.shared.ESASkySSOSearchResult.ESASkySSOObjType;
import esac.archive.esasky.ifcs.model.shared.ESASkySSOSearchResultList;
import esac.archive.esasky.ifcs.model.shared.ESASkySearchResult;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.sso.SSOCrossMatchEvent;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.presenter.SearchPresenter;
import esac.archive.esasky.cl.web.client.status.GUISessionStatus;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.view.animation.AnimationObserver;
import esac.archive.esasky.cl.web.client.view.animation.CssPxAnimation;
import esac.archive.esasky.cl.web.client.view.common.buttons.CloseButton;

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
    private FlowPanel logoContainer;
    private CloseButton clearTextButton;
    private Image searchIcon;
    private Image ssoDNetLogo;
    private Image simbadLogo;
    

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
        clearTextButton.setDarkStyle();
        clearTextButton.setDarkIcon();
        clearTextButton.addStyleName("clearSearchTextBtn");
        clearTextButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                GUISessionStatus.setIsTrackingSSO(false);
                searchTextBox.setValue("");
                searchResultsFocusPanel.setVisible(false);
                ensureClearTextButtonVisibilty();
                searchTextBox.setFocus(true);
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
				setSeachIconVisibilty();
			}
		});

        this.tooltip = new FocusPanel();
        this.tooltip.getElement().setId("tooltipCoords");
        this.tooltip
                .getElement()
                .setInnerHTML(
                        "<p class=\"coords\"><b>" + TextMgr.getInstance().getText("searchPanel_inputByTargetName ") + "</b><br/>"
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
                        + "<p class=\"coords\"><a href=\"//www.cosmos.esa.int/web/esdc/esasky-search-formats\" target=\"_blank\">" + TextMgr.getInstance().getText("searchPanel_seeMoreExamples") + "</a></p>");
        
        
        this.tooltip.setVisible(false);

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
        textBoxHolder.add(clearTextButton);
        searchPanel.add(textBoxHolder);
        searchPanel.add(this.tooltip);
        searchPanel.add(this.searchTextBoxError);
        searchPanel.add(this.searchResultsFocusPanel);

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
    	
    	logoContainer = new FlowPanel();
    	logoContainer.addStyleName("searchPanel__logoContainer");
    	logoContainer.add(simbadLogo);
    	logoContainer.add(ssoDNetLogo);
    }
    
    private void ensureClearTextButtonVisibilty() {
        if (searchTextBox.getValue().isEmpty() ) {
            clearTextButton.addStyleName("collapse");
        } else {
            clearTextButton.removeStyleName("collapse");
            if(searchTextBox.getOffsetHeight() == 0){
                clearTextButton.getElement().getStyle().setTop(5, Unit.PX); 
            }
            else{
                clearTextButton.getElement().getStyle().setTop((searchTextBox.getOffsetHeight() / 2) - 
                		(clearTextButton.getOffsetHeight() / 2) , Unit.PX);
            }
        }
    }
    
    private void setSeachIconVisibilty() {
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
    public void showGeneralTargetResultsPanel(ESASkyGeneralResultList resultList) {
        this.resultsList.clear();
        final ESASkySearchResult simbadResult = resultList.getSimbadResult();

        boolean foundInSimbad = false;
        boolean foundInSSODnet = false;

        Label simbadLabel = new Label();
        simbadLabel.getElement().setId(SEARCH_RESULT_ENTRY);
        if (simbadResult != null && simbadResult.getServerMessage().isEmpty()) {
            simbadLabel.setText(simbadResult.getSimbadMainId());
            foundInSimbad = true;
	        simbadLabel.addClickHandler(new ClickHandler() {
	
	            @Override
	            public void onClick(ClickEvent event) {
	
	                AladinLiteWrapper.getInstance().goToTarget(simbadResult.getSimbadRaDeg(),
	                        simbadResult.getSimbadDecDeg(), simbadResult.getFoVDeg(), false,
	                        AladinLiteConstants.FRAME_J2000);
	                SearchPanel.this.searchResultsFocusPanel.setVisible(false);
	                
	                GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_Search, GoogleAnalytics.ACT_Search_SearchResultClick, "SIMBAD: " + simbadResult.getSimbadMainId());
	            }
	        });
	        this.resultsList.add(simbadLabel);
        }

        ESASkySSOSearchResultList ssodnetResult = resultList.getSsoDnetResults();

        if (ssodnetResult != null) {
            foundInSSODnet = true;

            for (ESASkySSOSearchResult currSSO : ssodnetResult.getResults()) {
                Log.debug("ITERATING SSO");

                Label currSSOLabel = new Label();
                currSSOLabel.getElement().setId(SEARCH_RESULT_ENTRY);
                currSSOLabel.setText("[" + TextMgr.getInstance().getText(currSSO.getSsoObjType().name()) + "] "
                        + currSSO.getName());

                final String ssoName = currSSO.getName();
                final ESASkySSOObjType ssoType = currSSO.getSsoObjType();
                currSSOLabel.addClickHandler(new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {

                        CommonEventBus.getEventBus().fireEvent(
                                new SSOCrossMatchEvent(ssoName, ssoType));
                        SearchPanel.this.searchResultsFocusPanel.setVisible(false);
                        
                        GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_Search, GoogleAnalytics.ACT_Search_SearchResultClick, "SSO: " + ssoName);
                    }
                });
                this.resultsList.add(currSSOLabel);
            }
        }
        
        if (!foundInSimbad && !foundInSSODnet) {
            this.resultsList.clear();
            showTargetNotFoundMessage();
            GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_Search, GoogleAnalytics.ACT_Search_SearchTargetNotFound, searchTextBox.getText());

        } else if (foundInSimbad && !foundInSSODnet) {
            this.resultsList.clear();
            this.searchResultsFocusPanel.setVisible(false);
            AladinLiteWrapper.getInstance().goToTarget(simbadResult.getSimbadRaDeg(),
                    simbadResult.getSimbadDecDeg(), simbadResult.getFoVDeg(), false,
                    AladinLiteConstants.FRAME_J2000);
            GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_Search, GoogleAnalytics.ACT_Search_SearchResultAuto, "SIMBAD: " + simbadResult.getSimbadMainId());
            
        } else {
        	simbadLogo.setVisible(foundInSimbad);
        	ssoDNetLogo.setVisible(foundInSSODnet);
        	this.resultsList.add(logoContainer);
            this.searchResultsFocusPanel.setVisible(true);
        }

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
    public void showBackendMessage(String message) {
        this.searchBoxErrorLabel.setText(message);
        this.searchTextBoxError.setVisible(true);
        this.searchResultsFocusPanel.setVisible(false);
    }
    
	@Override
	public void setFullSize(boolean fullSize) {
		isFullSize = fullSize;
		if(isFullSize) {
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
}
