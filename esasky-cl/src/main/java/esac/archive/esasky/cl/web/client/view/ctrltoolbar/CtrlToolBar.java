package esac.archive.esasky.cl.web.client.view.ctrltoolbar;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.*;
import esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteConstants;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteCoordinatesOrFoVChangedEvent;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteCoordinatesOrFoVChangedEventHandler;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.Modules;
import esac.archive.esasky.cl.web.client.event.*;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.entities.EntityContext;
import esac.archive.esasky.cl.web.client.presenter.CtrlToolBarPresenter;
import esac.archive.esasky.cl.web.client.presenter.PublicationPanelPresenter;
import esac.archive.esasky.cl.web.client.presenter.SelectSkyPanelPresenter.View;
import esac.archive.esasky.cl.web.client.status.GUISessionStatus;
import esac.archive.esasky.cl.web.client.utility.*;
import esac.archive.esasky.cl.web.client.utility.JSONUtils.IJSONRequestCallback;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyButton;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyToggleButton;
import esac.archive.esasky.cl.web.client.view.common.icons.Icons;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.planningmenu.PlanObservationPanel;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.publication.PublicationPanel;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.selectsky.SelectSkyPanel;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.treemap.TreeMapChanged;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.treemap.TreeMapContainer;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.descriptor.*;
import esac.archive.esasky.ifcs.model.shared.ESASkyTarget;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * @author ESDC team Copyright (c) 2015- European Space Agency
 */
public class CtrlToolBar extends Composite implements CtrlToolBarPresenter.View {

	private FlowPanel ctrlToolBarPanel;
	private SelectSkyPanel selectSkyPanel;
	private PublicationPanel publicationPanel;
	private PlanObservationPanel planObservationPanel;
	private GwPanel gwPanel;
	private OutreachImagePanel outreachImagePanel;
	private String HiPSFromURL = null;
	private String unwantedRandomTargets = "";
	private final TreeMapContainer observationTreeMapContainer = new TreeMapContainer(EntityContext.ASTRO_IMAGING);
	private final TreeMapContainer catalogTreeMapContainer = new TreeMapContainer(EntityContext.ASTRO_CATALOGUE);
	private final TreeMapContainer spectraTreeMapContainer = new TreeMapContainer(EntityContext.ASTRO_SPECTRA);
	private final TreeMapContainer ssoTreeMapContainer = new TreeMapContainer(EntityContext.SSO);
	private final TreeMapContainer extTapTreeMapContainer = new TreeMapContainer(EntityContext.EXT_TAP);
	
	private HashMap<String, CustomTreeMap> customTreeMaps = new HashMap<String, CustomTreeMap>();
	
	private EsaSkyButton exploreBtn;
	private EsaSkyToggleButton selectSkyButton;
	private EsaSkyToggleButton planObservationButton;
	private BadgeButton observationButton;
	private BadgeButton catalogButton;
	private BadgeButton spectraButton;
	private BadgeButton ssoButton;
	private EsaSkyToggleButton extTapButton;
	private EsaSkyToggleButton gwButton;
	private EsaSkyToggleButton outreachImageButton;
	private EsaSkyToggleButton publicationsButton;


    private final CssResource style;
    private Resources resources = GWT.create(Resources.class);

    private HandlerRegistration latestHandler;

    public static interface Resources extends ClientBundle {

        @Source("ctrlToolBar.css")
        @CssResource.NotStrict
        CssResource style();

    }

    public CtrlToolBar(String hips) {
        this.HiPSFromURL = hips;

        this.style = resources.style();
        this.style.ensureInjected();

        initView();

        CommonEventBus.getEventBus().addHandler(TargetDescriptionEvent.TYPE, new TargetDescriptionEventHandler() {

            @Override
            public void onEvent(TargetDescriptionEvent event) {
                addTargetBox(event.getTargetName(), event.getTargetDescription(), event.isRightSide());
            }
        });

        CommonEventBus.getEventBus().addHandler(ShowImageListEvent.TYPE, event -> {
            CtrlToolBar.this.outreachImagePanel.toggle();
            closeAllOtherPanels(outreachImageButton);
        });

        MainLayoutPanel.addMainAreaResizeHandler(event -> setTargetDialogSuggestedPosition(false));
    }

    private void initView() {

        ctrlToolBarPanel = new FlowPanel();

        selectSkyPanel = SelectSkyPanel.init(this.HiPSFromURL);
        selectSkyPanel.hide();
        ctrlToolBarPanel.add(createSkiesMenuBtn());
        ctrlToolBarPanel.add(selectSkyPanel);

        ctrlToolBarPanel.add(createObservationBtn());
        ctrlToolBarPanel.add(observationTreeMapContainer);
        observationTreeMapContainer.registerObserver(new TreeMapChanged() {
            @Override
            public void onClose() {
                observationButton.setToggleStatus(false);
            }
        });

        ctrlToolBarPanel.add(createCatalogBtn());
        ctrlToolBarPanel.add(catalogTreeMapContainer);
        catalogTreeMapContainer.registerObserver(new TreeMapChanged() {
            @Override
            public void onClose() {
                catalogButton.setToggleStatus(false);
            }
        });

        ctrlToolBarPanel.add(createSpectraBtn());
        ctrlToolBarPanel.add(spectraTreeMapContainer);
        spectraTreeMapContainer.registerObserver(new TreeMapChanged() {
            @Override
            public void onClose() {
                spectraButton.setToggleStatus(false);
            }
        });

        ctrlToolBarPanel.add(createExtTapBtn());
        ctrlToolBarPanel.add(extTapTreeMapContainer);
        extTapTreeMapContainer.registerObserver(new TreeMapChanged() {
            @Override
            public void onClose() {
                extTapButton.setToggleStatus(false);
                CommonEventBus.getEventBus().fireEvent(new ExtTapToggleEvent(false));
            }
        });
        if (!Modules.getModule(EsaSkyWebConstants.MODULE_EXTTAP)) {
            hideWidget(extTapButton);
        }

        ctrlToolBarPanel.add(createSsoBtn());
        ctrlToolBarPanel.add(ssoTreeMapContainer);
        ssoTreeMapContainer.registerObserver(new TreeMapChanged() {
            @Override
            public void onClose() {
                ssoButton.setToggleStatus(false);
            }
        });

        gwButton = createGwBtn();

        ctrlToolBarPanel.add(gwButton);
		MainLayoutPanel.addElementToMainArea(gwPanel);
        if (!Modules.getModule(EsaSkyWebConstants.MODULE_GW)) {
            hideWidget(gwButton);
        }

        publicationPanel = new PublicationPanel();
        ctrlToolBarPanel.add(createPublicationsBtn());
        ctrlToolBarPanel.add(publicationPanel);
        publicationPanel.hide();

        planObservationPanel = PlanObservationPanel.getInstance();
        ctrlToolBarPanel.add(createPlanObservationBtn());
        ctrlToolBarPanel.add(planObservationPanel);

        exploreBtn = createExploreButton();
        ctrlToolBarPanel.add(exploreBtn);

        outreachImageButton = createImageButton();
        ctrlToolBarPanel.add(outreachImageButton);
        MainLayoutPanel.addElementToMainArea(outreachImagePanel);
        if (!Modules.getModule(EsaSkyWebConstants.MODULE_OUTREACH_IMAGE)) {
            hideWidget(outreachImageButton);
        }

        initWidget(ctrlToolBarPanel);

        updateModuleVisibility();
    }

    private EsaSkyButton createSkiesMenuBtn() {
        selectSkyButton = new EsaSkyToggleButton(Icons.getSelectSkyIcon());
        addCommonButtonStyle(selectSkyButton, TextMgr.getInstance().getText("webConstants_manageSkies"));

        selectSkyButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                CommonEventBus.getEventBus().fireEvent(new CloseOtherPanelsEvent(selectSkyButton));
                sendGAEvent(GoogleAnalytics.ACT_CTRLTOOLBAR_SKIES);
            }
        });

        selectSkyPanel.addCloseHandler(new CloseHandler<PopupPanel>() {

            @Override
            public void onClose(CloseEvent<PopupPanel> event) {
                selectSkyButton.setToggleStatus(false);
            }
        });
        return selectSkyButton;
    }

    private EsaSkyToggleButton createPlanObservationBtn() {
        planObservationButton = new EsaSkyToggleButton(Icons.getPlanObservationIcon());
        addCommonButtonStyle(planObservationButton, TextMgr.getInstance().getText("webConstants_projectFutureObservations"));
        planObservationButton.addClickHandler(
                new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                        CtrlToolBar.this.planObservationPanel.toggle();
                        CommonEventBus.getEventBus().fireEvent(new CloseOtherPanelsEvent(planObservationButton));
                        sendGAEvent(GoogleAnalytics.ACT_CTRLTOOLBAR_PLANNINGTOOL);
                    }
                });

        planObservationPanel.addCloseHandler(new CloseHandler<PopupPanel>() {

            @Override
            public void onClose(CloseEvent<PopupPanel> event) {
                planObservationButton.setToggleStatus(false);
            }
        });

        return planObservationButton;
    }

    private BadgeButton createObservationBtn() {
        observationButton = createDataPanelBtn(Icons.getObservationIcon(),
                TextMgr.getInstance().getText("webConstants_exploreImageObservations"), EntityContext.ASTRO_IMAGING, observationTreeMapContainer);
        return observationButton;
    }

    private EsaSkyToggleButton createExtTapBtn() {

        extTapButton = new EsaSkyToggleButton(Icons.getExtTapIcon());
        addCommonButtonStyle(extTapButton, TextMgr.getInstance().getText("webConstants_exploreExtTaps"));
        extTapButton.addClickHandler(
                new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                        extTapTreeMapContainer.toggleTreeMap();
                        CommonEventBus.getEventBus().fireEvent(new ExtTapToggleEvent(extTapTreeMapContainer.isOpen()));
                        CommonEventBus.getEventBus().fireEvent(new CloseOtherPanelsEvent(extTapButton));
                        sendGAEvent(EntityContext.EXT_TAP.toString());
                    }
                });
        return extTapButton;
    }

    private EsaSkyToggleButton createGwBtn() {
        gwButton = new EsaSkyToggleButton(Icons.getGwIcon());
        addCommonButtonStyle(gwButton, TextMgr.getInstance().getText("ctrlToolBar_gwTooltip"));
        gwButton.addClickHandler(event -> {
            CtrlToolBar.this.gwPanel.toggle();
            CommonEventBus.getEventBus().fireEvent(new CloseOtherPanelsEvent(gwButton));
            sendGAEvent(GoogleAnalytics.ACT_CTRLTOOLBAR_GW);
        });

        gwPanel = new GwPanel();
        gwPanel.setSuggestedPosition(5, 77);
        gwPanel.definePositionFromTopAndLeft();
        gwPanel.hide();
        gwPanel.addCloseHandler(event -> gwButton.setToggleStatus(false));
        return gwButton;
    }

    private BadgeButton createCatalogBtn() {
        catalogButton = createDataPanelBtn(Icons.getCatalogIcon(),
                TextMgr.getInstance().getText("webConstants_exploreCatalogue"), EntityContext.ASTRO_CATALOGUE, catalogTreeMapContainer);
        return catalogButton;
    }

    private BadgeButton createSpectraBtn() {
        spectraButton = createDataPanelBtn(Icons.getSpectraIcon(),
                TextMgr.getInstance().getText("webConstants_exploreSpectral"), EntityContext.ASTRO_SPECTRA, spectraTreeMapContainer);
        return spectraButton;
    }

    private BadgeButton createSsoBtn() {
        ssoButton = createDataPanelBtn(Icons.getSsoIcon(),
                TextMgr.getInstance().getText("webConstants_exploreData"), EntityContext.SSO, ssoTreeMapContainer);
        ssoButton.setDisabledTooltip(TextMgr.getInstance().getText("webConstants_trackSSO"));
        hideWidget(ssoButton);
        return ssoButton;
    }

    private EsaSkyToggleButton createPublicationsBtn() {
        publicationsButton = new EsaSkyToggleButton(Icons.getPublicationsIcon());
        addCommonButtonStyle(publicationsButton, TextMgr.getInstance().getText("webConstants_explorePublications"));

        publicationPanel.addCloseHandler(new CloseHandler<PopupPanel>() {

            @Override
            public void onClose(CloseEvent<PopupPanel> event) {
                publicationsButton.setToggleStatus(false);
            }
        });
        return publicationsButton;
    }

    private BadgeButton createDataPanelBtn(ImageResource imageResource, String tooltip, final EntityContext context, final TreeMapContainer treeMapContainer) {
        final EsaSkyToggleButton toggleButton = new EsaSkyToggleButton(imageResource);
        addCommonButtonStyle(toggleButton, tooltip);
        if (context == EntityContext.ASTRO_CATALOGUE) {
            toggleButton.addStyleName("catalogButton");
        }
        final BadgeButton badgeButton = new BadgeButton(toggleButton);
        toggleButton.addClickHandler(
                new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                        treeMapContainer.toggleTreeMap();
                        CommonEventBus.getEventBus().fireEvent(new CloseOtherPanelsEvent(badgeButton));
                        sendGAEvent(context.toString());
                    }
                });

        return badgeButton;
    }

    private void addCommonButtonStyle(EsaSkyButton button, String tooltip) {
        button.setNonTransparentBackground();
        button.setBigStyle();
        button.addStyleName("ctrlToolBarBtn");
        button.setTitle(tooltip);
    }

    public EsaSkyButton addCustomButton(ImageResource icon, String iconText, String description) {
        EsaSkyButton button;
        if (icon != null) {
            button = new EsaSkyButton(icon);
        } else {
            button = new EsaSkyButton(iconText);
        }
        addCommonButtonStyle(button, description);
        ctrlToolBarPanel.add(button);
        return button;
    }

    public void removeCustomButton(EsaSkyButton button) {
        ctrlToolBarPanel.remove(button);
    }

    @Override
    public void updateObservationCount(long newCount) {
        observationButton.updateCount(newCount);

    }

    @Override
    public void updateCatalogCount(long newCount) {
        catalogButton.updateCount(newCount);

    }

    @Override
    public void updateSpectraCount(long newCount) {
        spectraButton.updateCount(newCount);

    }

    @Override
    public void updateSsoCount(long newCount) {
        ssoButton.updateCount(newCount);

    }

    @Override
    public void onIsTrackingSSOEventChanged() {
        if (GUISessionStatus.getIsTrackingSSO()) {
            showWidget(ssoButton);
            ssoButton.setTargetName(GUISessionStatus.getTrackedSso().name);
            ssoButton.setToggleStatus(true);

            ssoTreeMapContainer.open();
            sendGAEvent(EntityContext.SSO.toString());
            CommonEventBus.getEventBus().fireEvent(new CloseOtherPanelsEvent(ssoButton));
        } else {
            hideWidget(ssoButton);
            ssoTreeMapContainer.close();
            sendGAEvent(EntityContext.SSO.toString());
        }
    }

    @Override
    public void closeTreeMap() {
        observationTreeMapContainer.close();
        catalogTreeMapContainer.close();
        spectraTreeMapContainer.close();
        ssoTreeMapContainer.close();
    }

    @Override
    public void closeAllOtherPanels(Widget button) {
        if (!button.equals(observationButton)) {
            observationTreeMapContainer.close();
        }
        if (!button.equals(catalogButton)) {
            catalogTreeMapContainer.close();
        }
        if (!button.equals(spectraButton)) {
            spectraTreeMapContainer.close();
        }
        if (!button.equals(ssoButton)) {
            ssoTreeMapContainer.close();
        }
        if (!button.equals(selectSkyButton)) {
            selectSkyPanel.hide();
        }
        if (!button.equals(planObservationButton)) {
            planObservationPanel.hide();
        }
        if (!button.equals(publicationsButton)) {
            publicationPanel.hide();
        }
        if (!button.equals(extTapButton)) {
            extTapTreeMapContainer.hide();
        }
        if (!button.equals(gwButton)) {
            gwPanel.hide();
        }
        if (!button.equals(outreachImageButton)) {
            outreachImagePanel.hide();
        }

        for (CustomTreeMap customTreeMap : customTreeMaps.values()) {
            if (!button.equals(customTreeMap.button)) {
                customTreeMap.treeMapContainer.hide();
            }
        }

    }

    public void updateModuleVisibility() {
        if (Modules.getModule(EsaSkyWebConstants.MODULE_SKIESMENU)) {
            showWidget(selectSkyButton);
        } else {
            hideWidget(selectSkyButton);
        }

        setScienceModeVisibility(GUISessionStatus.getIsInScienceMode());
    }

    private void setScienceModeVisibility(boolean isInScienceMode) {
        if (!isInScienceMode) {
            hideWidget(observationButton);
            hideWidget(catalogButton);
            hideWidget(spectraButton);
            hideWidget(publicationsButton);
            hideWidget(extTapButton);
            hideWidget(gwButton);
            hideWidget(planObservationButton);
        } else {
            showScienceModeWidgets();
        }

        if (Modules.getModule(EsaSkyWebConstants.MODULE_SSO) && GUISessionStatus.getIsTrackingSSO()) {
            showWidget(ssoButton);
        } else {
            hideWidget(ssoButton);
        }

        if (Modules.getModule(EsaSkyWebConstants.MODULE_DICE) && !isInScienceMode) {
            showWidget(exploreBtn);
        } else {
            hideWidget(exploreBtn);
        }
        showOrHideWidget(outreachImageButton, Modules.getModule(EsaSkyWebConstants.MODULE_OUTREACH_IMAGE) && !isInScienceMode);
    }

    private void showScienceModeWidgets() {
        showOrHideWidget(observationButton, Modules.getModule(EsaSkyWebConstants.MODULE_OBS));
        showOrHideWidget(catalogButton, Modules.getModule(EsaSkyWebConstants.MODULE_CAT));
        showOrHideWidget(extTapButton, Modules.getModule(EsaSkyWebConstants.MODULE_EXTTAP));
        showOrHideWidget(spectraButton, Modules.getModule(EsaSkyWebConstants.MODULE_SPE));
        showOrHideWidget(publicationsButton, Modules.getModule(EsaSkyWebConstants.MODULE_PUBLICATIONS));
        showOrHideWidget(extTapButton, Modules.getModule(EsaSkyWebConstants.MODULE_EXTTAP));
        showOrHideWidget(ssoButton, Modules.getModule(EsaSkyWebConstants.MODULE_SSO) && GUISessionStatus.getIsTrackingSSO());
        showOrHideWidget(gwButton, Modules.getModule(EsaSkyWebConstants.MODULE_GW));
        showOrHideWidget(planObservationButton, Modules.getModule(EsaSkyWebConstants.MODULE_JWST_PLANNING));
    }

    private void showOrHideWidget(Widget widget, boolean condition) {
        if (condition) {
            showWidget(widget);
        } else {
            hideWidget(widget);
        }
    }

    private void hideWidget(Widget widget) {
        if (widget != null) {
            widget.getElement().getStyle().setDisplay(Display.NONE);
        }
    }

    private void showWidget(Widget widget) {
        if (widget != null) {
            widget.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        }
    }


    public EsaSkyButton createExploreButton() {
        final EsaSkyButton button = new EsaSkyButton(Icons.getExploreIcon());
        button.getElement().setId("exploreButton");
        addCommonButtonStyle(button, TextMgr.getInstance().getText("webConstants_exploreRandomTarget"));
        button.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (!exploreActionInProgress) {
                    showRandomSource();
                    sendGAEvent(GoogleAnalytics.ACT_CTRLTOOLBAR_DICE);
                }
            }
        });

        return button;
    }

    public EsaSkyToggleButton createImageButton() {
        outreachImageButton = new EsaSkyToggleButton(Icons.getHubbleIcon());
        outreachImageButton.getElement().setId("imageButton");
        addCommonButtonStyle(outreachImageButton, TextMgr.getInstance().getText("webConstants_exploreHstImages"));
        outreachImageButton.addClickHandler(event -> {
                    CommonEventBus.getEventBus().fireEvent(new ShowImageListEvent());
                    CommonEventBus.getEventBus().fireEvent(new CloseOtherPanelsEvent(outreachImageButton));
                    sendGAEvent(GoogleAnalytics.ACT_CTRLTOOLBAR_OUTREACH_IMAGE);
                }
        );
        outreachImagePanel = new OutreachImagePanel();
        outreachImagePanel.setSuggestedPosition(5, 77);
        outreachImagePanel.definePositionFromTopAndLeft();
        outreachImagePanel.hide();
        outreachImagePanel.addCloseHandler(event -> outreachImageButton.setToggleStatus(false));

        return outreachImageButton;

    }

    //Workaround for enabling and disabling buttons, which causes incorrect style and click behaviour
    //if the mouse never leaves the button itself. Caused by problems in gwt buttons
    private boolean exploreActionInProgress = false;

    private MessageDialogBox targetDialogBox = new MessageDialogBox(new HTML(), "", "skyObject", "TargetDescriptionDialog");

    private void showRandomSource() {
        exploreActionInProgress = true;

        JSONUtils.getJSONFromUrl(EsaSkyWebConstants.RANDOM_SOURCE_URL + "?lang=" + TextMgr.getInstance().getLangCode()
                + "&UNWANTED=" + unwantedRandomTargets, new IJSONRequestCallback() {

            @Override
            public void onSuccess(String responseText) {

                try {

                    final ESASkyTarget esaSkyTarget = ParseUtils.parseJsonTarget(responseText);

                    if (esaSkyTarget.getName().equals("noMoreTargets")) {
                        CtrlToolBar.this.unwantedRandomTargets = "";
                        exploreActionInProgress = false;
                        CtrlToolBar.this.showRandomSource();
                    } else if (!esaSkyTarget.getTitle().isEmpty()
                            && !esaSkyTarget.getDescription().isEmpty()
                            && !esaSkyTarget.getRa().isEmpty()
                            && !esaSkyTarget.getDec().isEmpty()
                            && !esaSkyTarget.getFovDeg().isEmpty()) {
                        AladinLiteWrapper.getInstance().goToTarget(esaSkyTarget.getRa(), esaSkyTarget.getDec(), Double.parseDouble(esaSkyTarget.getFovDeg()), false, AladinLiteConstants.FRAME_J2000);

                        String surveyName = (!esaSkyTarget.getHipsName().isEmpty()) ? esaSkyTarget.getHipsName() : EsaSkyConstants.ALADIN_DEFAULT_SURVEY_NAME;
                        SelectSkyPanel.setSelectedHipsName(surveyName);
                        addTargetBox(esaSkyTarget.getTitle(), esaSkyTarget.getDescription(), false);

                        String targetName = esaSkyTarget.getName();
                        targetName = targetName.replaceAll("[\\[\\]]", "");

                        CtrlToolBar.this.unwantedRandomTargets += "," + targetName;

                        exploreActionInProgress = false;

                    }
                } catch (Exception ex) {
                    Log.error("[CtrlToolBar] getRandomSource onSuccess ERROR: ", ex);
                }
            }

            @Override
            public void onError(String errorCause) {
                Log.error("[CtrlToolBar] getRandomSource ERROR: " + errorCause);
                exploreActionInProgress = false;
            }

        });
    }

    private void addTargetBox(String targetName, String targetDescription, boolean rightSide) {
        targetDialogBox.updateContent(targetDescription, targetName);
        targetDialogBox.show();
        setTargetDialogSuggestedPosition(rightSide);
        if (latestHandler != null) {
            latestHandler.removeHandler();
        }
        final double ra = AladinLiteWrapper.getCenterRaDeg();
        final double dec = AladinLiteWrapper.getCenterDecDeg();
        final double fov = AladinLiteWrapper.getAladinLite().getFovDeg();

        latestHandler = CommonEventBus.getEventBus().addHandler(AladinLiteCoordinatesOrFoVChangedEvent.TYPE,
                new AladinLiteCoordinatesOrFoVChangedEventHandler() {

                    @Override
                    public void onChangeEvent(final AladinLiteCoordinatesOrFoVChangedEvent clickEvent) {
                        if (CoordinateUtils.isTargetOutOfFocus(ra, dec, fov)) {
                            targetDialogBox.hide();
                            latestHandler.removeHandler();
                        }
                    }

                });
    }

    @Override
    public EsaSkyToggleButton getPublicationButton() {
        return publicationsButton;
    }

    public boolean isExtTapOpen() {
        return extTapTreeMapContainer.isShowing();
    }

    @Override
    public EsaSkyToggleButton getSkyPanelButton() {
        return selectSkyButton;
    }

    @Override
    public View getSelectSkyView() {
        return selectSkyPanel;
    }

    @Override
    public PublicationPanelPresenter.View getPublicationPanelView() {
        return publicationPanel;
    }

    private void sendGAEvent(String eventAction) {
        GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_CTRLTOOLBAR, eventAction, "");
    }

    @Override
    public void addTreeMapData(List<IDescriptor> descriptors, List<Integer> counts) {
        List<IDescriptor> observationDescriptors = new LinkedList<IDescriptor>();
        List<Integer> observationCounts = new LinkedList<Integer>();
        List<IDescriptor> ssoDescriptors = new LinkedList<IDescriptor>();
        List<Integer> ssoCounts = new LinkedList<Integer>();
        List<IDescriptor> catalogDescriptors = new LinkedList<IDescriptor>();
        List<Integer> catalogCounts = new LinkedList<Integer>();
        List<IDescriptor> spectraDescriptors = new LinkedList<IDescriptor>();
        List<Integer> spectraCounts = new LinkedList<Integer>();
        List<IDescriptor> extTapDescriptors = new LinkedList<IDescriptor>();
        List<Integer> extTapCounts = new LinkedList<Integer>();

        for (int i = 0; i < descriptors.size(); i++) {
            if (descriptors.get(i) instanceof ImageDescriptor) {
                continue;
            }
            if (descriptors.get(i) instanceof ObservationDescriptor) {
                observationDescriptors.add(descriptors.get(i));
                observationCounts.add(counts.get(i));
            } else if (descriptors.get(i) instanceof SSODescriptor) {
                ssoDescriptors.add(descriptors.get(i));
                ssoCounts.add(counts.get(i));
            } else if (descriptors.get(i) instanceof CatalogDescriptor) {
                catalogDescriptors.add(descriptors.get(i));
                catalogCounts.add(counts.get(i));
            } else if (descriptors.get(i) instanceof SpectraDescriptor) {
                spectraDescriptors.add(descriptors.get(i));
                spectraCounts.add(counts.get(i));
            } else if (descriptors.get(i) instanceof ExtTapDescriptor) {
                extTapDescriptors.add(descriptors.get(i));
                extTapCounts.add(counts.get(i));
            }
        }

        if (observationDescriptors.size() > 0) {
            observationTreeMapContainer.addData(observationDescriptors, observationCounts);
        }
        if (ssoDescriptors.size() > 0) {
            ssoTreeMapContainer.addData(ssoDescriptors, ssoCounts);
        }
        if (catalogDescriptors.size() > 0) {
            catalogTreeMapContainer.addData(catalogDescriptors, catalogCounts);
        }
        if (spectraDescriptors.size() > 0) {
            spectraTreeMapContainer.addData(spectraDescriptors, spectraCounts);
        }
        if (extTapDescriptors.size() > 0) {
            extTapTreeMapContainer.addData(extTapDescriptors, extTapCounts);
        }
    }

    public class CustomTreeMap {
        public final TreeMapContainer treeMapContainer;
        public final EsaSkyToggleButton button;

        public CustomTreeMap(TreeMapContainer treeMapContainer, EsaSkyToggleButton button) {
            this.button = button;
            this.treeMapContainer = treeMapContainer;
        }
    }


    public void updateCustomTreeMap(CustomTreeMapDescriptor treeMapDescriptor) {
        CustomTreeMap customTreeMap = customTreeMaps.get(treeMapDescriptor.getName());
        if (customTreeMap != null) {
            LinkedList<Integer> counts = new LinkedList<Integer>();

            for (int i = 0; i < treeMapDescriptor.getMissionDescriptors().size(); i++) {
                counts.add(1);
            }
            customTreeMap.treeMapContainer.updateData(treeMapDescriptor.getMissionDescriptors(), counts);
        }

    }

    public void addCustomTreeMap(CustomTreeMapDescriptor treeMapDescriptor) {
        TreeMapContainer treeMapContainer = new TreeMapContainer(EntityContext.USER_TREEMAP, false);
        treeMapContainer.setHeaderText(treeMapDescriptor.getIconText());

        EsaSkyToggleButton button = new EsaSkyToggleButton(treeMapDescriptor.getIconText());

        customTreeMaps.put(treeMapDescriptor.getName(), new CustomTreeMap(treeMapContainer, button));

        addCommonButtonStyle(button, treeMapDescriptor.getDescription());
        button.addClickHandler(
                new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                        treeMapContainer.toggleTreeMap();
                        CommonEventBus.getEventBus().fireEvent(new CloseOtherPanelsEvent(button));
                        GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_CTRLTOOLBAR, GoogleAnalytics.ACT_CTRLTOOLBAR_PLANNINGTOOL, treeMapDescriptor.getName());
                    }
                });
        ctrlToolBarPanel.add(button);
        ctrlToolBarPanel.add(treeMapContainer);
        catalogTreeMapContainer.registerObserver(new TreeMapChanged() {
            @Override
            public void onClose() {
                button.setToggleStatus(false);
            }
        });

        LinkedList<Integer> counts = new LinkedList<Integer>();
        for (int i = 0; i < treeMapDescriptor.getMissionDescriptors().size(); i++) {
            counts.add(1);
        }
        treeMapContainer.addData(treeMapDescriptor.getMissionDescriptors(), counts);

    }

    private void setTargetDialogSuggestedPosition(boolean rightSide) {
        int newLeft = 0;
        int newTop = 30;
        if (MainLayoutPanel.getMainAreaWidth() > 800) {
            if (rightSide) {
                newLeft = 10;
                newTop = 77;
            } else {
                newLeft = MainLayoutPanel.getMainAreaWidth() - targetDialogBox.getOffsetWidth() - 10;
                newTop = 77;
            }

        } else {
            newLeft = (MainLayoutPanel.getMainAreaWidth()) / 2 - targetDialogBox.getOffsetWidth() / 2;
            if (newLeft < 0) {
                newLeft = 0;
            }
        }
        targetDialogBox.setSuggestedPosition(newLeft, newTop);
        if (targetDialogBox.getAbsoluteLeft() < (exploreBtn.getAbsoluteLeft() + exploreBtn.getOffsetWidth() + 5)) {
            targetDialogBox.setSuggestedPosition(targetDialogBox.getAbsoluteLeft() - MainLayoutPanel.getMainAreaAbsoluteLeft(), exploreBtn.getAbsoluteTop() - MainLayoutPanel.getMainAreaAbsoluteTop() + exploreBtn.getOffsetHeight() + 5);
        }
    }

    @Override
    public void openGWPanel() {
        if (!gwButton.getToggleStatus()) {
            gwButton.toggle();
            gwPanel.toggle();
            CommonEventBus.getEventBus().fireEvent(new CloseOtherPanelsEvent(gwButton));
        }
    }

    @Override
    public void closeGWPanel() {
        if (gwButton.getToggleStatus()) {
            gwButton.toggle();
            gwPanel.toggle();
        }
    }

    @Override
    public JSONArray getGWIds() {
        return gwPanel.getIds();
    }

    @Override
    public GeneralJavaScriptObject getGWData(String id) {
        return gwPanel.getData4Id(id);
    }

    @Override
    public GeneralJavaScriptObject getAllGWData() {
        return gwPanel.getAllData();
    }

    @Override
    public void showGWEvent(String id) {
        gwPanel.showEvent(id);
    }
}
