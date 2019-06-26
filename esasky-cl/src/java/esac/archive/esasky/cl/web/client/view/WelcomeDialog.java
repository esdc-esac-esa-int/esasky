package esac.archive.esasky.cl.web.client.view;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.status.GUISessionStatus;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.view.common.MovablePanel;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyStringButton;
import esac.archive.esasky.cl.web.client.view.common.buttons.LabelWithHelpButton;

public class WelcomeDialog extends Composite {


    public final Resources resources = GWT.create(Resources.class);
    private final CssResource style;
    
    private final String WELCOME_COOKIE_NAME = "esaSkyWelcomeCookie";    
    private final int DIALOG_WIDTH = 475;
    
    private FlowPanel welcomeButtonContainer;
	private EsaSkyStringButton scienceButton;
	private EsaSkyStringButton explorerButton;
	private final CheckBox checkBox = new CheckBox(TextMgr.getInstance().getText("WelcomeDialog_checkbox"));
    
    public interface Resources extends ClientBundle {

        @Source("welcomeDialog.css")
        @CssResource.NotStrict
        CssResource style();
        
        @Source("esa-logo.png")
        ImageResource esaLogo();
    }

    public WelcomeDialog() {

        this.style = this.resources.style();
        this.style.ensureInjected();
        
		Label title = new Label(TextMgr.getInstance().getText("WelcomeDialog_title"));
		title.addStyleName("welcomeTitle");

		Image esaSkyLogo = new Image(this.resources.esaLogo());
		esaSkyLogo.addStyleName("welcomeEsaSkyLogo");
		
		Label descriptionText = new Label(TextMgr.getInstance().getText("WelcomeDialog_description"));
		descriptionText.addStyleName("welcomeDescription");
		
		final MovablePanel welcomeDialogConainer = new MovablePanel(GoogleAnalytics.CAT_Welcome, true);
		welcomeDialogConainer.add(esaSkyLogo);
		welcomeDialogConainer.add(title);
		welcomeDialogConainer.add(descriptionText);
		
		LabelWithHelpButton textWithHelp = new LabelWithHelpButton(
				TextMgr.getInstance().getText("WelcomeDialog_chooseMode"),
				TextMgr.getInstance().getText("WelcomeDialog_modeDescription"),
				TextMgr.getInstance().getText("WelcomeDialog_modeDescriptionHeader"));
		textWithHelp.getElement().setId("welcomeDialog__sciModeInfo");
		welcomeDialogConainer.add(textWithHelp);
		
		scienceButton = new EsaSkyStringButton(TextMgr.getInstance().getText("WelcomeDialog_science"));
		scienceButton.setMediumStyle();
		scienceButton.addStyleName("welcomeButton");
		scienceButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				close();
				GUISessionStatus.setIsInScienceMode(true);
			}
		});
		
		explorerButton = new EsaSkyStringButton(TextMgr.getInstance().getText("WelcomeDialog_explorer"));
		explorerButton.setMediumStyle();
		explorerButton.addStyleName("welcomeButton");
		explorerButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				close();
				GUISessionStatus.setIsInScienceMode(false);
			}
		});
		
		checkBox.addStyleName("welcomeCheckBox");
		
		Anchor cookieInformation = new Anchor(TextMgr.getInstance().getText("WelcomeDialog_cookieInformation"), EsaSkyWebConstants.COOKIE_POLICY_URL, "_blank");
		cookieInformation.addStyleName("cookieInformation");
		
		welcomeButtonContainer = new FlowPanel();
		welcomeButtonContainer.addStyleName("welcomeButtonContainer");
		welcomeButtonContainer.add(scienceButton);
		welcomeButtonContainer.add(explorerButton);
		
		welcomeDialogConainer.add(welcomeButtonContainer);
		
		
		FlowPanel cookieCheckboxAndPolicy = new FlowPanel();
		cookieCheckboxAndPolicy.addStyleName("cookieCheckboxAndPolicy");
		cookieCheckboxAndPolicy.add(checkBox);
		cookieCheckboxAndPolicy.add(cookieInformation);
		
		welcomeDialogConainer.add(cookieCheckboxAndPolicy);
		welcomeDialogConainer.add(createClosingButtons());
		
		initWidget(welcomeDialogConainer);
		getElement().getStyle().setWidth(DIALOG_WIDTH, Unit.PX);
		addStyleName("welcomeDialogBox");
		
		MainLayoutPanel.addMainAreaResizeHandler(new ResizeHandler() {
			
			@Override
			public void onResize(ResizeEvent event) {
				setMaxSize();
				setSciModeButtonPositions();
			}
		});
    }
    
    @Override
    protected void onLoad() {
    	super.onLoad();
    	setSciModeButtonPositions();
    }
    
    private Widget createClosingButtons() {
    	EsaSkyStringButton closeButton = new EsaSkyStringButton(TextMgr.getInstance().getText("WelcomeDialog_closeButton"));
    	closeButton.setMediumStyle();
    	closeButton.addStyleName("welcomeCloseButton");
    	closeButton.addClickHandler(new ClickHandler() {
    		
    		@Override
    		public void onClick(ClickEvent event) {
    			close();
    		}
    	});
    	
    	return closeButton;
    }
    
    private void close() {
    	if(checkBox.getValue()){
			Date expires = new Date();
			long milliseconds = 120 * 24 * 60 * 60 * 1000;
			expires.setTime(expires.getTime() + milliseconds);
			Cookies.setCookie(WELCOME_COOKIE_NAME, "", expires);
		}
		MainLayoutPanel.removeElementFromMainArea(this);
    }
    private void setSciModeButtonPositions() {
    	if(DIALOG_WIDTH - 100 >= getMaxPossibleWidth()) {
    		setSmallScreenButtonLayout();
    	} else {
    		setDefaultButtonLayout();
    	}
    }

	private void setSmallScreenButtonLayout() {
		welcomeButtonContainer.getElement().getStyle().setDisplay(Display.BLOCK);
		scienceButton.getElement().getStyle().setProperty("display", "flex");
		explorerButton.getElement().getStyle().setProperty("display", "flex");
	}

	private void setDefaultButtonLayout() {
		welcomeButtonContainer.getElement().getStyle().setProperty("display", "inline-flex");
		scienceButton.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		explorerButton.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
	}
    
    private void setMaxSize() {
    	    getElement().getStyle().setPropertyPx("maxWidth", getMaxPossibleWidth());
    }
    
    private int getMaxPossibleWidth() {
    	return MainLayoutPanel.getMainAreaWidth();
    }

	private boolean hasPreviouslyClickedDontShowAgain() {
        if(Cookies.getCookie(WELCOME_COOKIE_NAME) != null) {
            return true;
		 }
        return false;
	}
	
    public void show() {
        	if(!hasPreviouslyClickedDontShowAgain()) {
        	    	MainLayoutPanel.addElementToMainArea(this);
        	    	setMaxSize();
        	    	setSciModeButtonPositions();
        	}
    }
}
