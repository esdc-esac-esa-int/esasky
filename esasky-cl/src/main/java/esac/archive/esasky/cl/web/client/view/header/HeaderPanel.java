package esac.archive.esasky.cl.web.client.view.header;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;
import esac.archive.esasky.cl.web.client.Modules;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.presenter.HeaderPresenter;
import esac.archive.esasky.cl.web.client.presenter.HeaderPresenter.SelectionEntry;
import esac.archive.esasky.cl.web.client.presenter.HeaderPresenter.StringValueSelectionChangedHandler;
import esac.archive.esasky.cl.web.client.presenter.StatusPresenter;
import esac.archive.esasky.cl.web.client.status.GUISessionStatus;
import esac.archive.esasky.cl.web.client.status.ScreenSizeObserver;
import esac.archive.esasky.cl.web.client.status.ScreenSizeService;
import esac.archive.esasky.cl.web.client.status.ScreenWidth;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.view.common.EsaSkySwitch;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyButton;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyStringButton;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyToggleButton;
import esac.archive.esasky.cl.web.client.view.common.icons.Icons;

public class HeaderPanel extends Composite implements HeaderPresenter.View {

	private static Resources resources = GWT.create(Resources.class);
	private CssResource style;

	private EsaSkyButton shareButton = new EsaSkyButton(Icons.getShareIcon());
	private EsaSkyButton helpButton = new EsaSkyButton(Icons.getQuestionMarkIcon());
	private EsaSkyButton screenshotButton = new EsaSkyButton(Icons.getScreenshotIcon());
	private EsaSkyToggleButton gridButton = new EsaSkyToggleButton(Icons.getGridIcon());
	private EsaSkyStringButton feedbackButton = new EsaSkyStringButton(TextMgr.getInstance().getText("header_comunity"));
	private EsaSkyStringButton hipsLabelButton = new EsaSkyStringButton(EsaSkyConstants.ALADIN_DEFAULT_HIPS_MAP);

	private EsaSkySwitch headerScienceModeSwitch;
	private String headerScienceModeSwitchId = Document.get().createUniqueId();
	private StatusPanel statusPanel = new StatusPanel();
	private EsaSkyStringButton coordinateLabel = new EsaSkyStringButton("");
	private FlowPanel fovPanel = new FlowPanel();
	private final ListBox languageBox = new ListBox();

	private Image dropdownIcon = new Image(Icons.getMenuIcon());
	private FocusPanel dropdownContainer = new FocusPanel();
	private FlowPanel dropdownContent;
	private FocusPanel dropdownShareEntry = new FocusPanel(); 
	private Anchor dropdownFeedbackEntry = new Anchor(TextMgr.getInstance().getText("header_comunity"));
	private FocusPanel dropdownScreenshotEntry = new FocusPanel();
	private FocusPanel dropdownGridEntry = new FocusPanel();
	private FocusPanel dropdownHelpEntry = new FocusPanel(); 
	private FocusPanel dropdownEvaEntry = new FocusPanel(); 
	private FocusPanel dropdownViewInWwtEntry = new FocusPanel(); 
	private FocusPanel dropdownSessionSaveEntry = new FocusPanel(); 
	private FocusPanel dropdownSessionRestoreEntry = new FocusPanel(); 
	private FocusPanel hiResDropdown = new FocusPanel();
	private FocusPanel jwstDropdown = new FocusPanel();
	private EsaSkySwitch dropdownScienceModeSwitch; 
	private EsaSkyButton warningButton = new EsaSkyButton(Icons.getWarningIcon());
	private final ListBox dropdownLanguageBox = new ListBox();
	private String dropdownScienceModeSwitchId = Document.get().createUniqueId();
	private Anchor dropdownVideoTutorialsEntry = new Anchor(TextMgr.getInstance().getText("header_videoTutorials"));
	private Anchor dropdownReleaseNotesEntry = new Anchor(TextMgr.getInstance().getText("header_releaseNotes"));
	private Anchor dropdownNewsletterEntry = new Anchor(TextMgr.getInstance().getText("header_newsletter"));
	private Anchor dropdownAboutUsEntry = new Anchor(TextMgr.getInstance().getText("header_aboutUs"));
	private Anchor dropdownAcknowledgeEntry = new Anchor(TextMgr.getInstance().getText("header_acknowledge"));

	private ListBox coordinateFrameFull = new ListBox();
	private ListBox coordinateFrameFirstLetter = new ListBox();
	private FlowPanel coordinateContainer = new FlowPanel();

	public interface Resources extends ClientBundle {

		@Source("headerPanel.css")
		@CssResource.NotStrict
		CssResource style();
	}

	public HeaderPanel() {
		style = resources.style();
		style.ensureInjected();

		initView();
	}

	private void initView() {
		FlowPanel header = new FlowPanel();
		header.addStyleName("header");

		coordinateFrameFull.addStyleName("coordinateFrameFull");
		coordinateFrameFirstLetter.addStyleName("coordinateFrameFirstLetter");

		coordinateLabel.addStyleName("coordinateLabel");
		coordinateLabel.getElement().setId("coordinateLabelButton");

		fovPanel.addStyleName("fovPanel");

		coordinateContainer.getElement().setId("coordinateContainer");
		coordinateContainer.add(coordinateFrameFull);
		coordinateContainer.add(coordinateFrameFirstLetter);
		coordinateContainer.add(coordinateLabel);
		coordinateContainer.add(fovPanel);
		header.add(coordinateContainer);

		hipsLabelButton.getElement().setId("selectedSky");
		header.add(hipsLabelButton);

		header.add(statusPanel);
		
		
		gridButton.setTitle(TextMgr.getInstance().getText("header_gridFull"));
		gridButton.setMediumStyle();
		gridButton.getElement().setId("header__gridButton");
		

		shareButton.setTitle(TextMgr.getInstance().getText("header_getURLCurrentView"));
		shareButton.setMediumStyle();
		shareButton.getElement().setId("headerShareButton");

		helpButton.setTitle(TextMgr.getInstance().getText("header_learnMore"));
		helpButton.setMediumStyle();
		helpButton.getElement().setId("headerHelpButton");

		screenshotButton.setTitle(TextMgr.getInstance().getText("header_takeScreenshotFull"));
		screenshotButton.setMediumStyle();
		screenshotButton.getElement().setId("header__screenshotButton");

		feedbackButton.getElement().setId("feedbackButton");


		FlowPanel rightSideHeader = new FlowPanel();
		rightSideHeader.getElement().setId("rightSideHeader");
		
		warningButton.setMediumStyle();
		warningButton.getElement().setId("header__warningButton");
		hideWarningButton();
		rightSideHeader.add(warningButton);

		headerScienceModeSwitch = new EsaSkySwitch(headerScienceModeSwitchId, GUISessionStatus.getIsInScienceMode(),
				TextMgr.getInstance().getText("header_sciMode"), TextMgr.getInstance().getText("header_sciModeSwitchTooltip"));
		
		headerScienceModeSwitch.getElement().setId("header__scienceMode");
		if(!GUISessionStatus.isHidingSwitch()) {
			rightSideHeader.add(headerScienceModeSwitch);
		}
		if(Modules.getModule(EsaSkyWebConstants.MODULE_INTERNATIONALIZATION)) {
			languageBox.addStyleName("languageSelector");
			rightSideHeader.add(languageBox);
		}
		rightSideHeader.add(gridButton);
		rightSideHeader.add(screenshotButton);
		rightSideHeader.add(shareButton);
		rightSideHeader.add(helpButton);
		rightSideHeader.add(feedbackButton);

		rightSideHeader.add(createHamburgerMenu());

		header.add(rightSideHeader);


		initWidget(header);
		
		ScreenSizeService.getInstance().registerObserver(new ScreenSizeObserver() {
			
			@Override
			public void onScreenSizeChange() {
				updateModuleVisibility();
			}
		});
		updateModuleVisibility();
	}
	
	private FocusPanel createHamburgerMenu() {
		dropdownContainer.addStyleName("header__dropdown__container");
		FlowPanel dropdown = new FlowPanel();
		dropdownContent = createDropdownContent();
		closeDropdownMenu();
		dropdownIcon.getElement().setId("header__dropdown__icon");

		dropdown.add(dropdownIcon);
		dropdown.getElement().setId("header__dropdown");

		dropdown.add(dropdownContent);

		dropdownContainer.add(dropdown);
		return dropdownContainer;
	}

	private FlowPanel createDropdownContent() {
		FlowPanel dropdownContent = new FlowPanel();
		dropdownContent.addStyleName("header__dropdown__content");

		dropdownScienceModeSwitch = new EsaSkySwitch(dropdownScienceModeSwitchId, GUISessionStatus.getIsInScienceMode(),
				TextMgr.getInstance().getText("header_sciMode"), TextMgr.getInstance().getText("header_sciModeSwitchTooltip"));
		
		dropdownScienceModeSwitch.getElement().setId("header__dropdown__science");
		if(!GUISessionStatus.isHidingSwitch()) {
			dropdownContent.add(dropdownScienceModeSwitch);
		}

		if(Modules.getModule(EsaSkyWebConstants.MODULE_INTERNATIONALIZATION)) {
			dropdownLanguageBox.getElement().setId("header__dropdown__language");
			dropdownContent.add(dropdownLanguageBox);
		}

		dropdownContent.add(createScreenshotDropdownEntry());
		dropdownContent.add(createGridDropdownEntry());
		dropdownContent.add(createShareDropdownEntry());
		dropdownContent.add(createHelpDropdownEntry());
		
		
		if(Modules.getModule(EsaSkyWebConstants.MODULE_WWT_LINK)) {
			dropdownContent.add(createViewInWWTDropdownEntry());
		}

		dropdownContent.add(createHiResDropdownEntry());
		dropdownContent.add(createJwstDropdownEntry());

		dropdownFeedbackEntry.getElement().setId("header__dropdown__feedback");
		dropdownVideoTutorialsEntry.getElement().setId("header__dropdown__tutorials");
		dropdownReleaseNotesEntry.getElement().setId("header__dropdown__releasenotes");
		dropdownNewsletterEntry.getElement().setId("header__dropdown__newsletter");
		dropdownAboutUsEntry.getElement().setId("header__dropdown__aboutus");
		dropdownAcknowledgeEntry.getElement().setId("header__dropdown_acknowledge");
		
		dropdownContent.add(dropdownFeedbackEntry);
		dropdownContent.add(dropdownVideoTutorialsEntry);
		dropdownContent.add(dropdownReleaseNotesEntry);
		dropdownContent.add(dropdownNewsletterEntry);
		dropdownContent.add(dropdownAboutUsEntry);
		dropdownContent.add(dropdownAcknowledgeEntry);
		if(Modules.getModule(EsaSkyWebConstants.MODULE_EVA_MENU)) {
			dropdownContent.add(createEvaDropDownEntry());
		}
		dropdownContent.add(createSessionSaveDropdownEntries());
		dropdownContent.add(createSessionRestoreDropdownEntries());


		return dropdownContent;
	}

	private Widget createScreenshotDropdownEntry() {
		FlowPanel dropdownScreenshotContainer = new FlowPanel();

		Image screenshotImage = new Image(Icons.getScreenshotIcon());
		screenshotImage.addStyleName("header__dropdown__item__icon");
		dropdownScreenshotContainer.add(screenshotImage);
		Label screenshotLabel = new Label(TextMgr.getInstance().getText("header_takeScreenshot"));
		screenshotLabel.addStyleName("header__dropdown__screenshot__text");
		dropdownScreenshotContainer.add(screenshotLabel);

		dropdownScreenshotEntry.add(dropdownScreenshotContainer);
		dropdownScreenshotEntry.getElement().setId("header__dropdown__screenshot");
		dropdownScreenshotEntry.setTitle(TextMgr.getInstance().getText("header_takeScreenshotFull"));
		return dropdownScreenshotEntry;
	}
	
	private Widget createGridDropdownEntry() {
		FlowPanel dropdownGridContainer = new FlowPanel();

		Image gridImage = new Image(Icons.getGridIcon());
		gridImage.addStyleName("header__dropdown__item__icon");
		dropdownGridContainer.add(gridImage);
		Label screenshotLabel = new Label(TextMgr.getInstance().getText("header_gridDropdown"));
		screenshotLabel.addStyleName("header__dropdown__grid__text");
		dropdownGridContainer.add(screenshotLabel);

		dropdownGridEntry.add(dropdownGridContainer);
		dropdownGridEntry.getElement().setId("header__dropdown__grid");
		dropdownGridEntry.setTitle(TextMgr.getInstance().getText("header_gridFull"));
		return dropdownGridEntry;
	}

	private Widget createShareDropdownEntry() {
		FlowPanel dropdownShareContainer = new FlowPanel();

		Image shareImage = new Image(Icons.getShareIcon());
		shareImage.addStyleName("header__dropdown__item__icon");
		dropdownShareContainer.add(shareImage);

		Label shareLabel = new Label(TextMgr.getInstance().getText("header_share"));
		shareLabel.addStyleName("header__dropdown__share__text");
		dropdownShareContainer.add(shareLabel);

		dropdownShareEntry.add(dropdownShareContainer);
		dropdownShareEntry.getElement().setId("header__dropdown__share");
		dropdownShareEntry.setTitle(TextMgr.getInstance().getText("header_getURLCurrentView"));
		return dropdownShareEntry;
	}

	private Widget createHelpDropdownEntry() {
		FlowPanel helpContainer = new FlowPanel();

		Image helpImage = new Image(Icons.getQuestionMarkIcon());
		helpImage.addStyleName("header__dropdown__item__icon");
		helpContainer.add(helpImage);

		Label helpLabel = new Label(TextMgr.getInstance().getText("header_help"));
		helpLabel.addStyleName("header__dropdown__help__text");
		helpContainer.add(helpLabel);

		dropdownHelpEntry.add(helpContainer);
		dropdownHelpEntry.getElement().setId("header__dropdown__help");
		dropdownHelpEntry.setTitle(TextMgr.getInstance().getText("header_learnMore"));
		return dropdownHelpEntry;
	}
	
	private Widget createEvaDropDownEntry() {
		Label helpLabel = new Label(TextMgr.getInstance().getText("header_eva"));
		helpLabel.addStyleName("header__dropdown__help__text");
		
		dropdownEvaEntry.add(helpLabel);
		dropdownEvaEntry.getElement().setId("header__dropdown__eva");
		dropdownEvaEntry.setTitle(TextMgr.getInstance().getText("header_eva"));
		return dropdownEvaEntry;
	}

	private Widget createViewInWWTDropdownEntry() {
		FlowPanel viewInContainer = new FlowPanel();

		Image wwtImage = new Image(Icons.getWwtLogoIcon());
		wwtImage.addStyleName("header__dropdown__item__icon");
		viewInContainer.add(wwtImage);

		Label wwtLabel = new Label(TextMgr.getInstance().getText("header_viewInWWTAbbr"));
		wwtLabel.addStyleName("header__dropdown__wwt__text");
		viewInContainer.add(wwtLabel);

		dropdownViewInWwtEntry.add(viewInContainer);
		viewInContainer.getElement().setId("header__dropdown__wwt");
		dropdownViewInWwtEntry.setTitle(TextMgr.getInstance().getText("header_viewInWWTFull"));
		return dropdownViewInWwtEntry;
	}

	private Widget createHiResDropdownEntry() {
		Label hiresLabel = new Label(TextMgr.getInstance().getText("header_highResolutionImages"));
		hiresLabel.addStyleName("header__dropdown__outreach__text");
		hiResDropdown.add(hiresLabel);
		hiResDropdown.getElement().setId("header__dropdown__outreach");
		hiResDropdown.setTitle(TextMgr.getInstance().getText("header_highResolutionImagesTooltip"));
		hiResDropdown.setVisible(Modules.getModule(EsaSkyWebConstants.MODULE_OUTREACH_IMAGE)
				&& GUISessionStatus.getIsInScienceMode());
		return hiResDropdown;
	}

	private Widget createJwstDropdownEntry() {
		Label jwstLabel = new Label(TextMgr.getInstance().getText("header_jwstOutreachImages"));
		jwstLabel.addStyleName("header__dropdown__outreach__text");
		jwstDropdown.add(jwstLabel);
		jwstDropdown.setTitle(TextMgr.getInstance().getText("header_jwstImagesTooltip"));
		jwstDropdown.setVisible(Modules.getModule(EsaSkyWebConstants.MODULE_OUTREACH_JWST)
				&& GUISessionStatus.getIsInScienceMode());
		return jwstDropdown;
	}

	private Widget createSessionSaveDropdownEntries() {
		Label sessionSaveLabel = new Label(TextMgr.getInstance().getText("header_session_save"));
		sessionSaveLabel.addStyleName("header__dropdown__session__save__text");
		dropdownSessionSaveEntry.add(sessionSaveLabel);
		dropdownSessionSaveEntry.getElement().setId("header__dropdown__session__save");
		dropdownSessionSaveEntry.setTitle(TextMgr.getInstance().getText("header_sessionSaveEntryTooltip"));
		dropdownSessionSaveEntry.setVisible(Modules.getModule(EsaSkyWebConstants.MODULE_SESSION));
		return dropdownSessionSaveEntry;
	}
	
	private Widget createSessionRestoreDropdownEntries() {
		Label sessionRestoreLabel = new Label(TextMgr.getInstance().getText("header_session_restore"));
		sessionRestoreLabel.addStyleName("header__dropdown__session__restore__text");
		dropdownSessionRestoreEntry.add(sessionRestoreLabel);
		dropdownSessionRestoreEntry.getElement().setId("header__dropdown__session__save");
		dropdownSessionRestoreEntry.setTitle(TextMgr.getInstance().getText("header_sessionRestoreEntryTooltip"));
		dropdownSessionRestoreEntry.setVisible(Modules.getModule(EsaSkyWebConstants.MODULE_SESSION));
		return dropdownSessionRestoreEntry;
	}

	public void setFov(String fov) {
		fovPanel.getElement().setInnerHTML(fov);
	}

	public void setCoordinate(String coordinate) {
		coordinateLabel.setText(coordinate);
	}

	public void setIsCenterCoordinateStyle() {
		coordinateLabel.addStyleName("isCenterPosition");
	}

	public void setIsNotCenterCoordinateStyle() {
		coordinateLabel.removeStyleName("isCenterPosition");
	}
	
	public void addCoordinateClickHandler(ClickHandler clickHandler) {
		coordinateLabel.addClickHandler(clickHandler);
	}

	@Override
	public void setAvailableCoordinateFrames(List<SelectionEntry> coordinateFrames) {
		coordinateFrameFull.clear();
		coordinateFrameFirstLetter.clear();
		for(SelectionEntry entry : coordinateFrames) {
			coordinateFrameFull.addItem(entry.getText(), entry.getValue());
			coordinateFrameFirstLetter.addItem(entry.getText().substring(0, 1), entry.getValue());
		}
	}

	@Override
	public void addCoordinateFrameChangeHandler(final ChangeHandler changeHandler) {
		coordinateFrameFull.addChangeHandler(changeHandler);
		bindBothCoordinateFramesToEachOther(changeHandler);
	}

	private void bindBothCoordinateFramesToEachOther(final ChangeHandler changeHandler) {
		coordinateFrameFull.addChangeHandler(arg0 -> {
			coordinateFrameFirstLetter.setSelectedIndex(coordinateFrameFull.getSelectedIndex());
			GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_HEADER, GoogleAnalytics.ACT_HEADER_COOFRAMECHANGED,
					AladinLiteWrapper.getCoordinatesFrame().getValue());
		});
		coordinateFrameFirstLetter.addChangeHandler(arg0 -> {
			coordinateFrameFull.setSelectedIndex(coordinateFrameFirstLetter.getSelectedIndex());
			changeHandler.onChange(arg0);
		});
	}

	@Override
	public String getSelectedCoordinateFrame() {
		return coordinateFrameFull.getSelectedValue();
	}

	@Override
	public void selectCoordinateFrame(int index) {
		selectCoordinateFrameNoEvent(index);
		GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_HEADER, GoogleAnalytics.ACT_HEADER_COOFRAMECHANGED,
				coordinateFrameFull.getSelectedValue());
	}
	
	@Override
	public void selectCoordinateFrameNoEvent(int index) {
		coordinateFrameFull.setSelectedIndex(index);
		coordinateFrameFirstLetter.setSelectedIndex(index);
	}

	@Override
	public void setHipsName(String hipsName) {
		hipsLabelButton.setText(hipsName);
	}

	@Override
	public void addHipsNameClickHandler(ClickHandler clickHandler) {
		hipsLabelButton.addClickHandler(clickHandler);
	}
	
	@Override
	public void addShareClickHandler(ClickHandler clickHandler) {
		shareButton.addClickHandler(clickHandler);
		dropdownShareEntry.addClickHandler(clickHandler);
	}

	@Override
	public void addHelpClickHandler(ClickHandler clickHandler) {
		helpButton.addClickHandler(clickHandler);
		dropdownHelpEntry.addClickHandler(clickHandler);
	}

	@Override
	public void addEvaClickHandler(ClickHandler clickHandler) {
		dropdownEvaEntry.addClickHandler(clickHandler);
	}

	@Override
	public void addFeedbackClickHandler(ClickHandler clickHandler) {
		dropdownFeedbackEntry.addClickHandler(clickHandler);
		feedbackButton.addClickHandler(clickHandler);

	}

	@Override
	public void addVideoTutorialsClickHandler(ClickHandler clickHandler) {
		dropdownVideoTutorialsEntry.addClickHandler(clickHandler);
	}

	@Override
	public void addReleaseNotesClickHandler(ClickHandler clickHandler) {
		dropdownReleaseNotesEntry.addClickHandler(clickHandler);
	}

	@Override
	public void addNewsletterClickHandler(ClickHandler clickHandler) {
		dropdownNewsletterEntry.addClickHandler(clickHandler);
	}

	@Override
	public void addAboutUsClickHandler(ClickHandler clickHandler) {
		dropdownAboutUsEntry.addClickHandler(clickHandler);
	}

	@Override
	public void addAcknowledgeClickHandler(ClickHandler clickHandler) {
		dropdownAcknowledgeEntry.addClickHandler(clickHandler);
	}

	@Override
	public void addMenuClickHandler(ClickHandler clickHandler) {
		dropdownIcon.addClickHandler(clickHandler);
	}

	@Override
	public void toggleDropdownMenu() {
		if(dropdownContent.getElement().getStyle().getDisplay().equalsIgnoreCase(Display.NONE.toString())) {
			openDropdownMenu();
		} else {
			closeDropdownMenu();
		}
	}
	
	@Override
	public void addGridButtonClickHandler(ClickHandler handler) {
		gridButton.addClickHandler(handler);
		dropdownGridEntry.addClickHandler(handler);
	}
	
	@Override
	public void setGridButtonToggled(boolean toggled) {
		gridButton.setToggleStatus(toggled);
	}
	
	public boolean getGridButtonToggled() {
		return gridButton.getToggleStatus();
	}
	
	@Override
	public void closeDropdownMenu() {
		dropdownContent.getElement().getStyle().setDisplay(Display.NONE);
	}

	private void openDropdownMenu() {
		dropdownContent.getElement().getStyle().setDisplay(Display.BLOCK);
	}

	@Override
	public void addDropdownContainerBlurHandler(BlurHandler handler) {
		dropdownContainer.addBlurHandler(handler);
	}

	@Override
	public void addDropdownContainerMouseDownHandler(MouseDownHandler handler) {
		dropdownContainer.addMouseDownHandler(handler);
	}

	@Override
	public void addViewInWwtClickHandler(ClickHandler handler) {
		dropdownViewInWwtEntry.addClickHandler(handler);
	}

	@Override
	public void addHiResClickHandler(ClickHandler handler) {
		hiResDropdown.addClickHandler(handler);
	}

	@Override
	public void addJwstClickHandler(ClickHandler handler) {
		jwstDropdown.addClickHandler(handler);
	}
	
	@Override
	public void addSessionSaveClickHandler(ClickHandler handler) {
		dropdownSessionSaveEntry.addClickHandler(handler);
	}
	
	@Override
	public void addSessionRestoreClickHandler(ClickHandler handler) {
		dropdownSessionRestoreEntry.addClickHandler(handler);
	}

	@Override
	public StatusPresenter.View getStatusView() {
		return statusPanel;
	}

	@Override
	public void addScienceModeSwitchClickHandler(ClickHandler handler) {
		headerScienceModeSwitch.addClickHandler(handler);
		dropdownScienceModeSwitch.addClickHandler(handler);
	}

	@Override
	public void setIsInScienceMode(boolean isInScienceMode) {
		dropdownScienceModeSwitch.setChecked(isInScienceMode);
		headerScienceModeSwitch.setChecked(isInScienceMode);
		hiResDropdown.setVisible(Modules.getModule(EsaSkyWebConstants.MODULE_OUTREACH_IMAGE)
				&& isInScienceMode);
		jwstDropdown.setVisible(Modules.getModule(EsaSkyWebConstants.MODULE_OUTREACH_JWST)
				&& isInScienceMode);
	}

	@Override
	public void addScreenshotClickHandler(ClickHandler handler) {
		screenshotButton.addClickHandler(handler);
		dropdownScreenshotEntry.addClickHandler(handler);
	}

	@Override
	public void setAvailableLanguages(List<SimpleEntry<String, String>> languagesDisplayAndValuePairs) {
		for(SimpleEntry<String, String> languagesDisplayAndValuePair : languagesDisplayAndValuePairs) {
			languageBox.addItem(languagesDisplayAndValuePair.getValue(), languagesDisplayAndValuePair.getKey());
			dropdownLanguageBox.addItem(languagesDisplayAndValuePair.getValue(), languagesDisplayAndValuePair.getKey());
		}
	}

	@Override
	public void setSelectedLanguage(int index) {
		languageBox.setSelectedIndex(index);
		dropdownLanguageBox.setSelectedIndex(index);
	}

	@Override
	public void addLanguageSelectionChangeHandler(final StringValueSelectionChangedHandler handler) {
		languageBox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent arg0) {
				handler.onSelectionChanged(languageBox.getSelectedValue(), languageBox.getSelectedIndex());
			}
		});
		dropdownLanguageBox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent arg0) {
				handler.onSelectionChanged(dropdownLanguageBox.getSelectedValue(), dropdownLanguageBox.getSelectedIndex());
			}
		});
		
	}

	@Override
	public void addWarningButtonClickHandler(ClickHandler handler) {
		warningButton.addClickHandler(handler);
	}

	@Override
	public void showWarningButton() {
		warningButton.setVisible(true);
	}

	@Override
	public void hideWarningButton() {
		warningButton.setVisible(false);
	}
	
	public void updateModuleVisibility() {
		int pxWidth = ScreenSizeService.getInstance().getScreenSize().getWidth().getPxSize();
		
		
		if(pxWidth <= ScreenWidth.LARGE.getPxSize()) {
			hipsLabelButton.setVisible(false);
		} else {
			hipsLabelButton.setVisible(true);
		}
		
		if(pxWidth <= ScreenWidth.SMALL.getPxSize()) {
			coordinateLabel.setSidePadding(3, 3);
			coordinateContainer.getElement().getStyle().setMarginRight(0, Unit.PX);
		}else {
			coordinateLabel.setSidePadding(5, 5);
			coordinateContainer.getElement().getStyle().setMarginRight(0, Unit.PX);
		}
		
		if(pxWidth <= ScreenWidth.MINI.getPxSize()) {
			coordinateContainer.setWidth("305px");
			coordinateFrameFull.setVisible(false);
			coordinateFrameFirstLetter.setVisible(true);
		} else {
			coordinateContainer.setWidth("355px");
			coordinateFrameFull.setVisible(true);
			coordinateFrameFirstLetter.setVisible(false);
		}
		
		if(Modules.getModule(EsaSkyWebConstants.MODULE_SCIENTIFIC_BUTTON) && pxWidth > ScreenWidth.SMALL.getPxSize()) {
			headerScienceModeSwitch.setVisible(true);
			dropdownScienceModeSwitch.setVisible(false);
		}else {
			headerScienceModeSwitch.setVisible(false);
			dropdownScienceModeSwitch.setVisible(true);
		}

		if(Modules.getModule(EsaSkyWebConstants.MODULE_LANGUAGE) && pxWidth > ScreenWidth.LARGE.getPxSize()) {
			languageBox.setVisible(true);
			dropdownLanguageBox.setVisible(false);
		}else {
			languageBox.setVisible(false);
			dropdownLanguageBox.setVisible(true);
		}
		
		if(Modules.getModule(EsaSkyWebConstants.MODULE_COOR_GRID) && pxWidth > ScreenWidth.SMALL.getPxSize()) {
			gridButton.setVisible(true);
			dropdownGridEntry.setVisible(false);
		}else {
			gridButton.setVisible(false);
			dropdownGridEntry.setVisible(true);
		}
		
		if(Modules.getModule(EsaSkyWebConstants.MODULE_SCREENSHOT) && pxWidth > ScreenWidth.SMALL.getPxSize()) {
			screenshotButton.setVisible(true);
			dropdownScreenshotEntry.setVisible(false);
		}else {
			screenshotButton.setVisible(false);
			dropdownScreenshotEntry.setVisible(true);
		}
		
		if(Modules.getModule(EsaSkyWebConstants.MODULE_SHARE) && pxWidth > ScreenWidth.SMALL.getPxSize()) {
			shareButton.setVisible(true);
			dropdownShareEntry.setVisible(false);
		}else {
			shareButton.setVisible(false);
			dropdownShareEntry.setVisible(true);
		}
		
		if(Modules.getModule(EsaSkyWebConstants.MODULE_HELP) && pxWidth > ScreenWidth.SMALL.getPxSize()) {
			helpButton.setVisible(true);
			dropdownHelpEntry.setVisible(false);
		}else {
			helpButton.setVisible(false);
			dropdownHelpEntry.setVisible(true);
		}
		
		if(Modules.getModule(EsaSkyWebConstants.MODULE_DROPDOWN)) {
			dropdownContainer.setVisible(true);
		}else {
			dropdownContainer.setVisible(false);
		}
		
		if(Modules.getModule(EsaSkyWebConstants.MODULE_FEEDBACK)&& pxWidth > ScreenWidth.MEDIUM.getPxSize()) {
			feedbackButton.setVisible(true);
			dropdownFeedbackEntry.setVisible(false);
		}else {
			feedbackButton.setVisible(false);
			dropdownFeedbackEntry.setVisible(true);
		}
		
		if(Modules.getModule(EsaSkyWebConstants.MODULE_SCIENCE_MODE)) {
			dropdownSessionSaveEntry.setVisible(true);
			dropdownSessionRestoreEntry.setVisible(true);
		}else {
			dropdownSessionSaveEntry.setVisible(false);
			dropdownSessionRestoreEntry.setVisible(false);
		}
	}

}
