package esac.archive.esasky.cl.web.client.view.ctrltoolbar;

import java.util.LinkedList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

import esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteConstants;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteCoordinatesOrFoVChangedEvent;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteCoordinatesOrFoVChangedEventHandler;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.Modules;
import esac.archive.esasky.cl.web.client.event.TargetDescriptionEvent;
import esac.archive.esasky.cl.web.client.event.TargetDescriptionEventHandler;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.entities.EntityContext;
import esac.archive.esasky.cl.web.client.presenter.CtrlToolBarPresenter;
import esac.archive.esasky.cl.web.client.presenter.PublicationPanelPresenter;
import esac.archive.esasky.cl.web.client.presenter.SelectSkyPanelPresenter.View;
import esac.archive.esasky.cl.web.client.status.GUISessionStatus;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.CoordinateUtils;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.utility.JSONUtils;
import esac.archive.esasky.cl.web.client.utility.JSONUtils.IJSONRequestCallback;
import esac.archive.esasky.cl.web.client.utility.MessageDialogBox;
import esac.archive.esasky.cl.web.client.utility.ParseUtils;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyButton;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyToggleButton;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.planningmenu.PlanObservationPanel;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.publication.PublicationPanel;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.selectsky.SelectSkyPanel;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.treemap.TreeMapChanged;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.treemap.TreeMapContainer;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.uploadtargetlist.TargetListPanel;
import esac.archive.esasky.ifcs.model.descriptor.CatalogDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.ObservationDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.SSODescriptor;
import esac.archive.esasky.ifcs.model.descriptor.SpectraDescriptor;
import esac.archive.esasky.ifcs.model.shared.ESASkySearchResult;
import esac.archive.esasky.ifcs.model.shared.ESASkyTarget;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;

/**
 * @author ESDC team Copyright (c) 2015- European Space Agency
 */
public class CtrlToolBar extends Composite implements CtrlToolBarPresenter.View {

	private FlowPanel ctrlToolBarPanel;
	private SelectSkyPanel selectSkyPanel;
	private PublicationPanel publicationPanel;
	private PlanObservationPanel planObservationPanel;
	private TargetListPanel targetListPanel;
	private String HiPSFromURL = null;
	private String unwantedRandomTargets ="";
	private final TreeMapContainer observationTreeMapContainer = new TreeMapContainer(EntityContext.ASTRO_IMAGING);
	private final TreeMapContainer catalogTreeMapContainer = new TreeMapContainer(EntityContext.ASTRO_CATALOGUE);
	private final TreeMapContainer spectraTreeMapContainer = new TreeMapContainer(EntityContext.ASTRO_SPECTRA);
	private final TreeMapContainer ssoTreeMapContainer = new TreeMapContainer(EntityContext.SSO);
	
	private EsaSkyButton exploreBtn;
	private EsaSkyToggleButton selectSkyButton;
	private EsaSkyToggleButton targetListButton;
	private EsaSkyToggleButton planObservationButton;
	private BadgeButton observationButton;
	private BadgeButton catalogButton;
	private BadgeButton spectraButton;
	private BadgeButton ssoButton;
	private EsaSkyToggleButton publicationsButton;
	
	private final CssResource style;
	private Resources resources = GWT.create(Resources.class);
	
	private HandlerRegistration latestHandler;

	public static interface Resources extends ClientBundle {

		@Source("selectSky.png")
		ImageResource selectSky();

		@Source("target_list.png")
		ImageResource targetList();
		
		@Source("plan_observation.png")
		ImageResource planObservation();
		
		@Source("galaxy_light_outline.png")
		ImageResource observationIcon();
		
		@Source("catalog_map_outline.png")
		ImageResource catalogIcon();
		
		@Source("spectra_light_outline.png")
		ImageResource spectraIcon();
		
		@Source("saturn_light_outline.png")
		ImageResource ssoIcon();

		@Source("publications_outline.png")
        ImageResource publicationsIcon();
		
		@Source("random_dice.png")
		ImageResource explore();
		
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
				addTargetBox(event.getTargetName(), event.getTargetDescription());
			}
		});
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
		if(Modules.spectraModule){
			ctrlToolBarPanel.add(createSpectraBtn());
			ctrlToolBarPanel.add(spectraTreeMapContainer);
			spectraTreeMapContainer.registerObserver(new TreeMapChanged() {
				@Override
				public void onClose() {
					spectraButton.setToggleStatus(false);
				}
			});
		}
		if(Modules.ssoModule){
			ctrlToolBarPanel.add(createSsoBtn());
			ctrlToolBarPanel.add(ssoTreeMapContainer);
			ssoTreeMapContainer.registerObserver(new TreeMapChanged() {
				@Override
				public void onClose() {
					ssoButton.setToggleStatus(false);
				}
			});
		}
		if(Modules.publicationsModule){
			publicationPanel = new PublicationPanel();
			ctrlToolBarPanel.add(createPublicationsBtn());
			ctrlToolBarPanel.add(publicationPanel);
			publicationPanel.hide();
		}
		
		ctrlToolBarPanel.add(createTargetListBtn());
		ctrlToolBarPanel.add(targetListPanel);
		targetListPanel.hide();
		
		if (Modules.proposalModule) {
			planObservationPanel = PlanObservationPanel.getInstance();
			ctrlToolBarPanel.add(createPlanObservationBtn());
			ctrlToolBarPanel.add(planObservationPanel);
		}

		exploreBtn = createExploreButton();
		ctrlToolBarPanel.add(exploreBtn);
		
		MainLayoutPanel.addMainAreaResizeHandler(new ResizeHandler() {
			
			@Override
			public void onResize(ResizeEvent event) {
				ensureTargetDialogDoesNotCoverDiceButton();
			}
		});
		
		initWidget(ctrlToolBarPanel);
	}

	private EsaSkyButton createSkiesMenuBtn() {
		selectSkyButton = new EsaSkyToggleButton(resources.selectSky());
		addCommonButtonStyle(selectSkyButton, TextMgr.getInstance().getText("webConstants_manageSkies"));

        selectSkyButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				closeAllOtherPanels(selectSkyButton);
				sendGAEvent(GoogleAnalytics.ACT_CtrlToolbar_Skies);
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

	private EsaSkyButton createTargetListBtn() {
		targetListPanel = new TargetListPanel();
		
		targetListButton = new EsaSkyToggleButton(resources.targetList());
		targetListButton.getElement().setId("targetListImg");
		addCommonButtonStyle(targetListButton, TextMgr.getInstance().getText("webConstants_uploadTargetList"));
		targetListButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				CtrlToolBar.this.targetListPanel.toggle();
				closeAllOtherPanels(targetListButton);
				sendGAEvent(GoogleAnalytics.ACT_CtrlToolbar_TargetList);
			}
		});
		
		targetListPanel.addCloseHandler(new CloseHandler<PopupPanel>() {
			
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				targetListButton.setToggleStatus(false);
			}
		});

		return targetListButton;
	}

	private EsaSkyToggleButton createPlanObservationBtn() {
		planObservationButton = new EsaSkyToggleButton(resources.planObservation());
		addCommonButtonStyle(planObservationButton, TextMgr.getInstance().getText("webConstants_projectFutureObservations"));
		planObservationButton.addClickHandler( 
				new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				CtrlToolBar.this.planObservationPanel.toggle();
				closeAllOtherPanels(planObservationButton);
				sendGAEvent(GoogleAnalytics.ACT_CtrlToolbar_PlanningTool);
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
		observationButton = createDataPanelBtn(resources.observationIcon(), 
		        TextMgr.getInstance().getText("webConstants_exploreImageObservations"), EntityContext.ASTRO_IMAGING, observationTreeMapContainer);
		return observationButton;
	}
	
	private BadgeButton createCatalogBtn() {
		catalogButton = createDataPanelBtn(resources.catalogIcon(), 
		        TextMgr.getInstance().getText("webConstants_exploreCatalogue"), EntityContext.ASTRO_CATALOGUE, catalogTreeMapContainer);
		return catalogButton;
	}
	
	private BadgeButton createSpectraBtn() {
		spectraButton = createDataPanelBtn(resources.spectraIcon(), 
		        TextMgr.getInstance().getText("webConstants_exploreSpectral"), EntityContext.ASTRO_SPECTRA, spectraTreeMapContainer);
		return spectraButton;
	}
	
	private BadgeButton createSsoBtn() {
		ssoButton = createDataPanelBtn(resources.ssoIcon(), 
		        TextMgr.getInstance().getText("webConstants_exploreData"), EntityContext.SSO, ssoTreeMapContainer);
		ssoButton.setDisabledTooltip(TextMgr.getInstance().getText("webConstants_trackSSO"));
		ssoButton.disable();
		return ssoButton;
	}
	
	private EsaSkyToggleButton createPublicationsBtn() {
        publicationsButton = new EsaSkyToggleButton(resources.publicationsIcon());
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
		if(context == EntityContext.ASTRO_CATALOGUE) {
			toggleButton.addStyleName("catalogButton");
		}
		final BadgeButton badgeButton = new BadgeButton(toggleButton);
		toggleButton.addClickHandler(
				new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				treeMapContainer.toggleTreeMap();
				closeAllOtherPanels(badgeButton);
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
	
	@Override
	public void updateObservationCount(int newCount) {
		observationButton.updateCount(newCount);
		
	}

	@Override
	public void updateCatalogCount(int newCount) {
		catalogButton.updateCount(newCount);
		
	}

	@Override
	public void updateSpectraCount(int newCount) {
		spectraButton.updateCount(newCount);
		
	}

	@Override
	public void updateSsoCount(int newCount) {
		ssoButton.updateCount(newCount);
		
	}
   
	@Override
	public void onIsTrackingSSOEventChanged(){
		if(GUISessionStatus.getIsTrackingSSO()){
			ssoButton.setTargetName(GUISessionStatus.getTrackedSso().name);
			ssoButton.setToggleStatus(true);
			
			ssoTreeMapContainer.open();
			sendGAEvent(EntityContext.SSO.toString());
			closeAllOtherPanels(ssoButton);
		} else{
			ssoButton.disable();
			ssoTreeMapContainer.close();
			sendGAEvent(EntityContext.SSO.toString());
		}
	}
	
	@Override
	public void closeTreeMap(){
		observationTreeMapContainer.close();
		catalogTreeMapContainer.close();
		spectraTreeMapContainer.close();
		ssoTreeMapContainer.close();
	}
	
	@Override
	public void closeAllOtherPanels(Widget button){
		if(!button.equals(observationButton)){
			observationTreeMapContainer.close();
		}
		if(!button.equals(catalogButton)){
			catalogTreeMapContainer.close();
		}
		if(!button.equals(spectraButton)){
			spectraTreeMapContainer.close();
		}
		if(!button.equals(ssoButton)){
			ssoTreeMapContainer.close();
		}
		if(!button.equals(selectSkyButton)) {
			selectSkyPanel.hide();
		}
		if(!button.equals(targetListButton)) {
			targetListPanel.hide();
		}
		if(!button.equals(planObservationButton)) {
			planObservationPanel.hide();
		}
		if(!button.equals(publicationsButton)) {
			publicationPanel.hide();
		}
	}
	
	
	public void enterScienceMode() {
		showWidget(observationButton);
		showWidget(catalogButton);
		showWidget(spectraButton);
		showWidget(publicationsButton);
		showWidget(planObservationButton);
		showWidget(ssoButton);
		hideWidget(exploreBtn);
	}
	
	public void leaveScienceMode() {
		hideWidget(observationButton);
		hideWidget(catalogButton);
		hideWidget(spectraButton);
		hideWidget(publicationsButton);
		hideWidget(planObservationButton);
		hideWidget(ssoButton);
		showWidget(exploreBtn);
	}
	
	private void hideWidget(Widget widget) {
		widget.getElement().getStyle().setDisplay(Display.NONE);
	}
	private void showWidget(Widget widget) {
		widget.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
	}

	
	public EsaSkyButton createExploreButton() {
		final EsaSkyButton button = new EsaSkyButton(resources.explore());
		button.getElement().setId("exploreButton");
		addCommonButtonStyle(button, TextMgr.getInstance().getText("webConstants_exploreRandomTarget"));
		button.addClickHandler(new ClickHandler() {
        	    	  public void onClick(ClickEvent event) {
        	    		  if(!exploreActionInProgress) {
        	    			  showRandomSource();
        	    			  sendGAEvent(GoogleAnalytics.ACT_CtrlToolbar_Dice);
        	    		  }
        	    	  }
		});

		return button;
	}
	
	//Workaround for enabling and disabling buttons, which causes incorrect style and click behaviour 
	//if the mouse never leaves the button itself. Caused by problems in gwt buttons
	private boolean exploreActionInProgress = false;
	
	private MessageDialogBox targetDialogBox = new MessageDialogBox(new HTML(), "", "skyObject");
	
	private void showRandomSource () {
	    exploreActionInProgress = true;
	    
        JSONUtils.getJSONFromUrl(EsaSkyWebConstants.RANDOM_SOURCE_URL + "?lang=" + TextMgr.getInstance().getLangCode()
        		+ "&UNWANTED=" + unwantedRandomTargets, new IJSONRequestCallback() {
            
            @Override
            public void onSuccess(String responseText) {

                try {
                    
                    final ESASkyTarget esaSkyTarget = ParseUtils.parseJsonTarget(responseText);
                    
                    if(esaSkyTarget.getName().equals("noMoreTargets")) {
                    	CtrlToolBar.this.unwantedRandomTargets = "";
                    	exploreActionInProgress = false;
                    	CtrlToolBar.this.showRandomSource();           	
                    }else if (!esaSkyTarget.getTitle().isEmpty()
                        && !esaSkyTarget.getDescription().isEmpty()
                        && !esaSkyTarget.getRa().isEmpty()
                        && !esaSkyTarget.getDec().isEmpty()
                        && !esaSkyTarget.getFovDeg().isEmpty()) {
                    	AladinLiteWrapper.getInstance().goToTarget(esaSkyTarget.getRa(), esaSkyTarget.getDec(), Double.parseDouble(esaSkyTarget.getFovDeg()), false, AladinLiteConstants.FRAME_J2000);
                        
                        String surveyName = (!esaSkyTarget.getHipsName().isEmpty()) ? esaSkyTarget.getHipsName() : EsaSkyConstants.ALADIN_DEFAULT_SURVEY_NAME;
                        SelectSkyPanel.setSelectedHipsName(surveyName);
                    	addTargetBox(esaSkyTarget.getTitle(), esaSkyTarget.getDescription());
                    	
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
	
    private void addTargetBox(String targetName, String targetDescription) {
    	targetDialogBox.updateContent(targetDescription, targetName);
    	targetDialogBox.show();
    	ensureTargetDialogDoesNotCoverDiceButton();
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
                        	if(CoordinateUtils.isTargetOutOfFocus(ra, dec, fov)) {
                        		targetDialogBox.hide();
                        		latestHandler.removeHandler();
                        	}
                    }

                });
    }
	
	private void ensureTargetDialogDoesNotCoverDiceButton() {
		int newLeft = (MainLayoutPanel.getMainAreaWidth())/2 - targetDialogBox.getOffsetWidth()/2;
		if (newLeft < 0) {
			newLeft = 0;
		}
    	targetDialogBox.setSuggestedPosition(newLeft, 30);
    	if(targetDialogBox.getAbsoluteLeft() < (exploreBtn.getAbsoluteLeft() + exploreBtn.getOffsetWidth() + 5)) {
    		targetDialogBox.setSuggestedPosition(targetDialogBox.getAbsoluteLeft() - MainLayoutPanel.getMainAreaAbsoluteLeft(), exploreBtn.getAbsoluteTop() - MainLayoutPanel.getMainAreaAbsoluteTop() + exploreBtn.getOffsetHeight() + 5);
    	}
	}
	
    @Override
    public void showSearchResultsOnTargetList(List<ESASkySearchResult> searchResults, String title) {

        //Shows the UploadTargetListPanel
        Log.debug("[CtrlToolBar] showSearchResultsOnTargetList...");
        targetListPanel.show();
        targetListButton.setToggleStatus(true);
        closeAllOtherPanels(targetListButton);
        
        //Prepares target list
        targetListPanel.setTargetsTableData(searchResults, title);

    }

	@Override
	public EsaSkyToggleButton getPublicationButton() {
		return publicationsButton;
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
	    GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_CtrlToolbar, eventAction, "");
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
		
		for(int i = 0; i < descriptors.size(); i++) {
			if(descriptors.get(i) instanceof ObservationDescriptor) {
				observationDescriptors.add(descriptors.get(i));
				observationCounts.add(counts.get(i));
			}
			else if(descriptors.get(i) instanceof SSODescriptor) {
				ssoDescriptors.add(descriptors.get(i));
				ssoCounts.add(counts.get(i));
			}
			else if(descriptors.get(i) instanceof CatalogDescriptor) {
				catalogDescriptors.add(descriptors.get(i));
				catalogCounts.add(counts.get(i));
			}
			else if(descriptors.get(i) instanceof SpectraDescriptor) {
				spectraDescriptors.add(descriptors.get(i));
				spectraCounts.add(counts.get(i));
			}
		}
		
		if(observationDescriptors.size() > 0) {
			observationTreeMapContainer.addData(observationDescriptors, observationCounts);
		}
		if(ssoDescriptors.size() > 0) {
			ssoTreeMapContainer.addData(ssoDescriptors, ssoCounts);
		}
		if(catalogDescriptors.size() > 0) {
			catalogTreeMapContainer.addData(catalogDescriptors, catalogCounts);
		}
		if(spectraDescriptors.size() > 0) {
			spectraTreeMapContainer.addData(spectraDescriptors, spectraCounts);
		}
	}
}
