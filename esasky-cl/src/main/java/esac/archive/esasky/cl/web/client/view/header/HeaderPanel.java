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
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

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
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.view.common.EsaSkySwitch;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyButton;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyStringButton;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyToggleButton;

public class HeaderPanel extends Composite implements HeaderPresenter.View {

	private static Resources resources = GWT.create(Resources.class);
	private CssResource style;

	private EsaSkyButton shareButton = new EsaSkyButton(resources.shareIcon());
	private EsaSkyButton helpButton = new EsaSkyButton(resources.helpIcon());
	private EsaSkyButton screenshotButton = new EsaSkyButton(resources.screenshotIcon());
	private EsaSkyToggleButton gridButton = new EsaSkyToggleButton(resources.gridIcon());
	private EsaSkyStringButton feedbackButton = new EsaSkyStringButton(TextMgr.getInstance().getText("header_comunity"));
	private EsaSkyStringButton hipsLabelButton = new EsaSkyStringButton(EsaSkyConstants.ALADIN_DEFAULT_HIPS_MAP);

	private EsaSkySwitch headerScienceModeSwitch;
	private String headerScienceModeSwitchId = Document.get().createUniqueId();
	private StatusPanel statusPanel = new StatusPanel();
	private Label coordinateLabel = new Label();
	private FlowPanel fovPanel = new FlowPanel();
	private final ListBox languageBox = new ListBox();
	private boolean isGridOn = false;

	private Image dropdownIcon = new Image(resources.menuIcon());
	private FocusPanel dropdownContainer = new FocusPanel();
	private FlowPanel dropdownContent;
	private FocusPanel dropdownShareEntry = new FocusPanel(); 
	private Anchor dropdownFeedbackEntry = new Anchor(TextMgr.getInstance().getText("header_comunity"));
	private FocusPanel dropdownScreenshotEntry = new FocusPanel();
	private FocusPanel dropdownGridEntry = new FocusPanel();
	private FocusPanel dropdownHelpEntry = new FocusPanel(); 
	private FocusPanel dropdownViewInWwtEntry = new FocusPanel(); 
	private EsaSkySwitch dropdownScienceModeSwitch; 
	private EsaSkyButton warningButton = new EsaSkyButton(resources.warning());
	private final ListBox dropdownLanguageBox = new ListBox();
	private String dropdownScienceModeSwitchId = Document.get().createUniqueId();
	private Anchor dropdownVideoTutorialsEntry = new Anchor(TextMgr.getInstance().getText("header_videoTutorials"));
	private Anchor dropdownReleaseNotesEntry = new Anchor(TextMgr.getInstance().getText("header_releaseNotes"));
	private Anchor dropdownNewsletterEntry = new Anchor(TextMgr.getInstance().getText("header_newsletter"));
	private Anchor dropdownAboutUsEntry = new Anchor(TextMgr.getInstance().getText("header_aboutUs"));

	private ListBox coordinateFrameFull = new ListBox();
	private ListBox coordinateFrameFirstLetter = new ListBox();
	private FlowPanel coordinateContainer = new FlowPanel();

	public interface Resources extends ClientBundle {

		@Source("headerPanel.css")
		@CssResource.NotStrict
		CssResource style();

		@Source("share.png")
		ImageResource shareIcon();

		@Source("question_mark.png")
		ImageResource helpIcon();

		@Source("screenshot.png")
		ImageResource screenshotIcon();

		@Source("menu.png")
		ImageResource menuIcon();

		@Source("wwt_logo.png")
		ImageResource wwtLogo();
		
        @Source("warning.png")
        ImageResource warning();

        @Source("grid.png")
        ImageResource gridIcon();
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
		if(Modules.internationalization) {
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
				setResponsiveStyle();
			}
		});
		setResponsiveStyle();
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

		if(Modules.internationalization) {
			dropdownLanguageBox.getElement().setId("header__dropdown__language");
			dropdownContent.add(dropdownLanguageBox);
		}

		dropdownContent.add(createScreenshotDropdownEntry());
		dropdownContent.add(createGridDropdownEntry());
		dropdownContent.add(createShareDropdownEntry());
		dropdownContent.add(createHelpDropdownEntry());
		if(Modules.wwtLink) {
			dropdownContent.add(createViewInWWTDropdownEntry());
		}

		dropdownFeedbackEntry.getElement().setId("header__dropdown__feedback");
		dropdownVideoTutorialsEntry.getElement().setId("header__dropdown__tutorials");
		dropdownReleaseNotesEntry.getElement().setId("header__dropdown__releasenotes");
		dropdownNewsletterEntry.getElement().setId("header__dropdown__newsletter");
		dropdownAboutUsEntry.getElement().setId("header__dropdown__aboutus");
		
		dropdownContent.add(dropdownFeedbackEntry);
		dropdownContent.add(dropdownVideoTutorialsEntry);
		dropdownContent.add(dropdownReleaseNotesEntry);
		dropdownContent.add(dropdownNewsletterEntry);
		dropdownContent.add(dropdownAboutUsEntry);

		return dropdownContent;
	}

	private Widget createScreenshotDropdownEntry() {
		FlowPanel dropdownScreenshotContainer = new FlowPanel();

		Image screenshotImage = new Image(resources.screenshotIcon());
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

		Image gridImage = new Image(resources.gridIcon());
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

		Image shareImage = new Image(resources.shareIcon());
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

		Image helpImage = new Image(resources.helpIcon());
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

	private Widget createViewInWWTDropdownEntry() {
		FlowPanel viewInContainer = new FlowPanel();

		Image wwtImage = new Image(resources.wwtLogo());
		wwtImage.addStyleName("header__dropdown__item__icon");
		viewInContainer.add(wwtImage);

		Label wwtLabel = new Label(TextMgr.getInstance().getText("header_viewInWWTAbbr"));
		wwtLabel.addStyleName("header__dropdown__wwt__text");
		viewInContainer.add(wwtLabel);

		dropdownViewInWwtEntry.add(viewInContainer);
		dropdownViewInWwtEntry.getElement().setId("header__dropdown__wwt");
		dropdownViewInWwtEntry.setTitle(TextMgr.getInstance().getText("header_viewInWWTFull"));
		return dropdownViewInWwtEntry;
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
		coordinateFrameFull.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent arg0) {
				coordinateFrameFirstLetter.setSelectedIndex(coordinateFrameFull.getSelectedIndex());
			}
		});
		coordinateFrameFirstLetter.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent arg0) {
				coordinateFrameFull.setSelectedIndex(coordinateFrameFirstLetter.getSelectedIndex());
				changeHandler.onChange(arg0);
			}
		});
	}

	@Override
	public String getSelectedCoordinateFrame() {
		return coordinateFrameFull.getSelectedValue();
	}

	@Override
	public void selectCoordinateFrame(int index) {
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
	public void toggleGrid() {
		isGridOn = !isGridOn;
		
		gridButton.setToggleStatus(isGridOn);
		AladinLiteWrapper.getAladinLite().showGrid(isGridOn);
	}
	
	@Override
	public void toggleGrid(boolean show) {
		if(isGridOn != show) {
			toggleGrid();
		}
	}

	@Override
	public boolean isGridOn() {
		return isGridOn;
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
	
	private void setResponsiveStyle() {
		int pxWidth = ScreenSizeService.getInstance().getScreenSize().getWidth().getPxSize();
		if(pxWidth <= ScreenWidth.LARGE.getPxSize()) {
			hipsLabelButton.setVisible(false);
			languageBox.setVisible(false);
			dropdownLanguageBox.setVisible(true);
		} else {
			hipsLabelButton.setVisible(true);
			languageBox.setVisible(true);
			dropdownLanguageBox.setVisible(false);
		}
		if(pxWidth <= ScreenWidth.MEDIUM.getPxSize()) {
			feedbackButton.setVisible(false);
			dropdownFeedbackEntry.setVisible(true);
		} else {
			feedbackButton.setVisible(true);
			dropdownFeedbackEntry.setVisible(false);
		}
		if(pxWidth <= ScreenWidth.SMALL.getPxSize()) {
			shareButton.setVisible(false);
			helpButton.setVisible(false);
			screenshotButton.setVisible(false);
			gridButton.setVisible(false);
			headerScienceModeSwitch.setVisible(false);
			
			coordinateFrameFull.getElement().getStyle().setMarginRight(3, Unit.PX);
			coordinateFrameFirstLetter.getElement().getStyle().setMarginRight(3, Unit.PX);
			coordinateLabel.getElement().getStyle().setMarginRight(3, Unit.PX);
			coordinateContainer.getElement().getStyle().setMarginRight(0, Unit.PX);
			
			dropdownHelpEntry.setVisible(true);
			dropdownScienceModeSwitch.setVisible(true);
			dropdownShareEntry.setVisible(true);
			dropdownScreenshotEntry.setVisible(true);
			dropdownGridEntry.setVisible(true);
		} else {
			shareButton.setVisible(true);
			helpButton.setVisible(true);
			screenshotButton.setVisible(true);
			gridButton.setVisible(true);
			headerScienceModeSwitch.setVisible(true);
			
			coordinateFrameFull.getElement().getStyle().setMarginRight(5, Unit.PX);
			coordinateFrameFirstLetter.getElement().getStyle().setMarginRight(5, Unit.PX);
			coordinateLabel.getElement().getStyle().setMarginRight(5, Unit.PX);
			coordinateContainer.getElement().getStyle().setMarginRight(0, Unit.PX);
			
			dropdownHelpEntry.setVisible(false);
			dropdownScienceModeSwitch.setVisible(false);
			dropdownShareEntry.setVisible(false);
			dropdownScreenshotEntry.setVisible(false);
			dropdownGridEntry.setVisible(false);
			
		}
		if(pxWidth <= ScreenWidth.MINI.getPxSize()) {
			coordinateContainer.setWidth("305px");
			coordinateFrameFull.setVisible(false);
			coordinateFrameFirstLetter.setVisible(true);
		} else {
			coordinateContainer.setWidth("332px");
			coordinateFrameFull.setVisible(true);
			coordinateFrameFirstLetter.setVisible(false);
		}
	}

}
