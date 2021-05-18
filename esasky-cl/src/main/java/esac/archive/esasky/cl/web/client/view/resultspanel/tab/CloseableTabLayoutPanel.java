package esac.archive.esasky.cl.web.client.view.resultspanel.tab;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import esac.archive.esasky.ifcs.model.descriptor.ExtTapDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.cl.gwidgets.client.util.SaveAllView;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.Modules;
import esac.archive.esasky.cl.web.client.event.DataPanelAnimationCompleteEvent;
import esac.archive.esasky.cl.web.client.event.DataPanelAnimationCompleteEventHandler;
import esac.archive.esasky.cl.web.client.event.DataPanelResizeEvent;
import esac.archive.esasky.cl.web.client.event.DataPanelResizeEventHandler;
import esac.archive.esasky.cl.web.client.event.ExportCSVEvent;
import esac.archive.esasky.cl.web.client.event.ExportVOTableEvent;
import esac.archive.esasky.cl.web.client.event.SendTableToEvent;
import esac.archive.esasky.cl.web.client.event.UpdateNumRowsSelectedEvent;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.entities.ColorChangeObserver;
import esac.archive.esasky.cl.web.client.model.entities.GeneralEntityInterface;
import esac.archive.esasky.cl.web.client.status.GUISessionStatus;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.CoordinateUtils;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.view.animation.EsaSkyAnimation;
import esac.archive.esasky.cl.web.client.view.common.buttons.CloseButton;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyButton;
import esac.archive.esasky.cl.web.client.view.resultspanel.TableObserver;
import esac.archive.esasky.cl.web.client.view.resultspanel.ITablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.ResultsPanel;

/**
 * @author ESDC team Copyright (c) 2015- European Space Agency
 */
public class CloseableTabLayoutPanel extends Composite {	
	

    private final Resources resources = GWT.create(Resources.class);
    private CssResource style;
    private BiMap<MissionTabButtons, String> tabWidgetIds = HashBiMap.create();

    private List<MissionTabButtons> tabs = new ArrayList<MissionTabButtons>();
    private ScrollTabLayoutPanel tabLayout;
    private SaveAllView saveAllView;
    private final HTML emptyTableMessage;
    private EsaSkyButton toggleDataPanelButton = new EsaSkyButton(this.resources.arrowIcon());
    private EsaSkyAnimation toggleDataPanelButtonMoveAnimation = new EsaSkyAnimation() {
        
        @Override
        protected void setCurrentPosition(double newPosition) {
            toggleDataPanelButton.getElement().getStyle().setMarginTop(newPosition, Unit.PX);
        }
        
        @Override
        protected Double getCurrentPosition() {
            String marginLeftString = toggleDataPanelButton.getElement().getStyle().getMarginTop();
            if (marginLeftString.equals("")){
                marginLeftString = "0px";
            }
            //remove suffix "px"
            marginLeftString = marginLeftString.substring(0, marginLeftString.length()-2);
            Double currentPosition = new Double(marginLeftString);
            return currentPosition;
        }
    };
    
    private VerticalPanel shadedArea;
    private EsaSkyButton closeAllButton;
    private EsaSkyButton refreshButton;
    private EsaSkyButton styleButton;
    private EsaSkyButton recenterButton;
    private EsaSkyButton sendButton;
    private EsaSkyButton saveButton;
    private EsaSkyButton configureButton;

    LinkedList<TabObserver> closingObservers = new LinkedList<>();

    public interface Resources extends ClientBundle {

        @Source("up_arrow_outline.png")
        @ImageOptions(flipRtl = true)
        ImageResource arrowIcon();
    	
        @Source("refresh_outline.png")
        @ImageOptions(flipRtl = true)
        ImageResource refreshIcon();

        @Source("recenter_outline.png")
        @ImageOptions(flipRtl = true)
        ImageResource recenterIcon();

        @Source("send_outline.png")
        @ImageOptions(flipRtl = true)
        ImageResource sendIcon();

        @Source("download_outline.png")
        @ImageOptions(flipRtl = true)
        ImageResource downloadIcon();
        
        @Source("gear_icon_outline.png")
        @ImageOptions(flipRtl = true)
        ImageResource configureIcon();

        @Source("closeableTabLayoutPanel.css")
        @CssResource.NotStrict
        CssResource style();
    }
    
    public CloseableTabLayoutPanel(final double height, final Unit unit, final boolean showScroll) {
        this.style = this.resources.style();
        this.style.ensureInjected();

        FlowPanel closeableTabLayoutPanel = new FlowPanel();
        
        emptyTableMessage = new HTML();
        emptyTableMessage.addStyleName("emtpyTableMessage");
        closeableTabLayoutPanel.add(emptyTableMessage);

        FlowPanel buttonsAndObservationPanel = new FlowPanel();
        buttonsAndObservationPanel.addStyleName("observationPanel");
        
        VerticalPanel closeMinimizeButtonsPanel = new VerticalPanel();
		
        toggleDataPanelButton.addStyleName("toggleDataPanelButton");
        toggleDataPanelButton.setNonTransparentBackground();
        toggleDataPanelButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent arg0) {
				ResultsPanel.toggleOpenCloseDataPanel();
			}
		});
        
		closeAllButton = createCloseAllButton();
		
    	closeMinimizeButtonsPanel.add(toggleDataPanelButton);
    	closeMinimizeButtonsPanel.add(closeAllButton);
        
        CommonEventBus.getEventBus().addHandler(DataPanelResizeEvent.TYPE, new DataPanelResizeEventHandler() {
			
			@Override
			public void onDataPanelResize(DataPanelResizeEvent event) {
				if(event.getNewHeight() > 40) {
					toggleDataPanelButton.rotate(180, 1000);
					toggleDataPanelButtonMoveAnimation.animateTo(2, 1000);
				} else {
					toggleDataPanelButton.rotate(0, 1000);
					toggleDataPanelButtonMoveAnimation.animateTo(10, 1000);
				}
			}
		});
        
        CommonEventBus.getEventBus().addHandler(DataPanelAnimationCompleteEvent.TYPE, new DataPanelAnimationCompleteEventHandler() {
            
            @Override
            public void onDataPanelAnimationComplete(DataPanelAnimationCompleteEvent event) {
                if(GUISessionStatus.isDataPanelOpen()) {
                    closeAllButton.removeStyleName("hidden");
                } else {
                    closeAllButton.addStyleName("hidden");
                }
            }
        });


        shadedArea = new VerticalPanel();
        shadedArea.add(closeMinimizeButtonsPanel);
        
        refreshButton = createRefreshButton();
        styleButton = createStyleButton();
        recenterButton = createRecenterButton();
        
        shadedArea.add(refreshButton);
        shadedArea.add(styleButton);
        shadedArea.add(recenterButton);
        shadedArea.add(createSendButton());
        shadedArea.add(createSaveButton());
        if(Modules.getModule(EsaSkyWebConstants.MODULE_TOGGLE_COLUMNS)){
            configureButton = createConfigureButton();
            shadedArea.add(configureButton);
        }

        shadedArea.addStyleName("tabButtons");
        buttonsAndObservationPanel.add(shadedArea);

        tabLayout = new ScrollTabLayoutPanel(height, unit, showScroll);
        tabLayout.addSelectionHandler(new SelectionHandler<Integer>() {

            @Override
            public void onSelection(final SelectionEvent<Integer> event) {
                final int index = event.getSelectedItem().intValue();
                
                updateStyleOnTab(index);
                
                ITablePanel tabPanel = CloseableTabLayoutPanel.this.getWidget(index);
                final GeneralEntityInterface entity = tabPanel.getEntity();
                
                ensureCorrectRelatedInformationVisibilty(entity);
                ensureCorrectButtonClickability(getSelectedWidget().getNumberOfShownRows());
            }
        });
        buttonsAndObservationPanel.add(tabLayout);

        closeableTabLayoutPanel.add(buttonsAndObservationPanel);

        initWidget(closeableTabLayoutPanel);
    }

	private void toggleButtonRotation(){
		if(GUISessionStatus.isDataPanelOpen()){
            	toggleDataPanelButton.removeStyleName("hidden");
            	closeAllButton.removeStyleName("hidden");
            	toggleDataPanelButton.rotate(180, 1000);
            	toggleDataPanelButtonMoveAnimation.animateTo(2, 1000);
		} else {
        	    toggleDataPanelButton.rotate(0, 1000);
        	    toggleDataPanelButtonMoveAnimation.animateTo(10, 1000);
		}
	}
	
    private void ensureCorrectRelatedInformationVisibilty (GeneralEntityInterface entity){

    	if(entity.isSampEnabled()) {
    		sendButton.getElement().getStyle().setDisplay(Display.BLOCK);
    	} else {
    		sendButton.getElement().getStyle().setDisplay(Display.NONE);
    	}
    	if(entity.isRefreshable()) {
    		refreshButton.getElement().getStyle().setDisplay(Display.BLOCK);
    	} else {
    		refreshButton.getElement().getStyle().setDisplay(Display.NONE);
    	}
        if (entity.isCustomizable()) {
            styleButton.getElement().getStyle().setDisplay(Display.BLOCK);
            styleButton.setCircleColor(entity.getColor());
        } else {
            styleButton.getElement().getStyle().setDisplay(Display.NONE);
        }
        if (entity.getDescriptor() instanceof ExtTapDescriptor
                || "publications".equals(entity.getDescriptor().getIcon())) {
            configureButton.getElement().getStyle().setDisplay(Display.NONE);
        } else {
            configureButton.getElement().getStyle().setDisplay(Display.BLOCK);
        }
        
    }

    private EsaSkyButton createSaveButton() {
        saveButton = new EsaSkyButton(resources.downloadIcon());
        saveButton.setMediumStyle();
        saveButton.setTitle(TextMgr.getInstance().getText("closeableTabLayoutPanel_saveResultsTableOrDownload"));
        saveButton.getElement().setId("tabButtonDownload");
        
        saveAllView = new SaveAllView();
        // Bind download products Anchor.
        saveAllView.getDowloadProductsAnchor().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(final ClickEvent event) {
            	getSelectedWidget().downloadSelected(saveAllView.getDDRequestForm());
            }
        });

        // Bind save as VOTABLE anchor
        saveAllView.getSaveAsVOTableAnchor().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(final ClickEvent event) {
                String selectedTabId = tabs.get(tabLayout.getSelectedIndex()).getId();
                CommonEventBus.getEventBus().fireEvent(
                        new ExportVOTableEvent(selectedTabId, saveAllView));
            }

        });

        // Bind save as CSV anchor
        saveAllView.getSaveAsCSVAnchor().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(final ClickEvent event) {
                String selectedTabId = tabs.get(tabLayout.getSelectedIndex()).getId();
                CommonEventBus.getEventBus().fireEvent(
                        new ExportCSVEvent(selectedTabId, saveAllView));
            }
        });

        saveButton.addClickHandler((final ClickEvent event) -> {
                String selectedTabId = tabs.get(tabLayout.getSelectedIndex()).getId();
                // Update number of observation selected before display the pop-up
                CommonEventBus.getEventBus().fireEvent(
                        new UpdateNumRowsSelectedEvent(selectedTabId, saveAllView));
                GeneralEntityInterface entity = CloseableTabLayoutPanel.this.getWidget(tabLayout.getSelectedIndex()).getEntity();
                
                saveAllView.setProductsDownloadVisible(hasProductUrl(entity.getDescriptor()) && !getSelectedWidget().isDataProductDatalink());
                // Set pop-up position.
                saveAllView.getSaveOrDownloadDialog().setPopupPositionAndShow(
                        new PopupPanel.PositionCallback() {

                            @Override
                            public void setPosition(final int offsetWidth, final int offsetHeight) {
                                saveAllView.getSaveOrDownloadDialog().showRelativeTo(saveButton);
                                saveAllView.getSaveOrDownloadDialog().show();
                            }
                        });
        });
        saveButton.addStyleName("tabButton");
        
        return saveButton;
    }
    
    private boolean hasProductUrl(IDescriptor descriptor) {
        return descriptor instanceof ExtTapDescriptor 
                || descriptor.getMetadataDescriptorByTapName("product_url") != null
                || (descriptor.getMetadataDescriptorByTapName("access_url") != null
                        && !"ASTRO_IMAGING_ALMA".equals(descriptor.getDescriptorId()));
    }

    private EsaSkyButton createSendButton() {

        sendButton = new EsaSkyButton(resources.sendIcon());
        sendButton.setMediumStyle();
        sendButton.setTitle(TextMgr.getInstance().getText("closeableTabLayoutPanel_sendTableToVOA"));

        // Bind 'send' Icon
        sendButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(final ClickEvent event) {
                String selectedTabId = tabs.get(tabLayout.getSelectedIndex()).getId() + "-" + GUISessionStatus.getNextUniqueSampNumber();
                Log.debug("Samp on ObservationsTablePanel");
                CommonEventBus.getEventBus().fireEvent(
                        new SendTableToEvent(selectedTabId));
            }
        });
        sendButton.addStyleName("tabButton");
        return sendButton;
    }

    private EsaSkyButton createConfigureButton() {
        
        configureButton = new EsaSkyButton(resources.configureIcon());
        configureButton.setMediumStyle();
        configureButton.setTitle(TextMgr.getInstance().getText("closeableTabLayoutPanel_configure"));
        configureButton.addClickHandler(new ClickHandler() {
            
            @Override
            public void onClick(final ClickEvent arg0) {
                getSelectedWidget().openConfigurationPanel();
            }
        });
        configureButton.addStyleName("tabButton");
        
        return configureButton;
    }
    private EsaSkyButton createRecenterButton() {
        
        EsaSkyButton recenterButton = new EsaSkyButton(resources.recenterIcon());
        recenterButton.setMediumStyle();
        recenterButton.setTitle(TextMgr.getInstance().getText("closeableTabLayoutPanel_recentre"));
        recenterButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(final ClickEvent arg0) {
                GeneralEntityInterface entity = getSelectedWidget().getEntity();
                AladinLiteWrapper.getAladinLite().goToRaDec(Double.toString(entity.getSkyViewPosition().getCoordinate().ra),
                        Double.toString(entity.getSkyViewPosition().getCoordinate().dec));
                AladinLiteWrapper.getAladinLite().setZoom(entity.getSkyViewPosition().getFov());

                GoogleAnalytics.sendEventWithURL(GoogleAnalytics.CAT_TabToolbar_Recenter, entity.getEsaSkyUniqId());
            }
        });
        recenterButton.addStyleName("tabButton");
        
        return recenterButton;
    }

    private EsaSkyButton createCloseAllButton() {
        
    	EsaSkyButton closeAllButton = new CloseButton();
    	closeAllButton.setSmallStyle();
    	closeAllButton.setTitle(TextMgr.getInstance().getText("closeableTabLayoutPanel_closeAll"));
    	closeAllButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(final ClickEvent arg0) {
                List<ITablePanel> tablePanels = tabLayout.getTablePanels();
                for(ITablePanel tablePanel : tablePanels) {
    				tablePanel.closeTablePanel();
    			}
                GoogleAnalytics.sendEventWithURL(GoogleAnalytics.CAT_TabToolbar_CloseAll, "");
            }
        });
    	closeAllButton.addStyleName("closeAllTabsButton");
    	closeAllButton.setNonTransparentBackground();
        return closeAllButton;
    }

    private EsaSkyButton createRefreshButton() {
    	
    	EsaSkyButton refreshButton = new EsaSkyButton(resources.refreshIcon());
    	refreshButton.setMediumStyle();
    	refreshButton.setTitle(TextMgr.getInstance().getText("closeableTabLayoutPanel_refreshData"));
    	refreshButton.addClickHandler(new ClickHandler() {
    		
    		@Override
    		public void onClick(final ClickEvent arg0) {
    			ITablePanel tabPanel = getSelectedWidget();
    			tabPanel.getEntity().setSkyViewPosition(CoordinateUtils.getCenterCoordinateInJ2000());
    			tabPanel.updateData();
    			
    			GoogleAnalytics.sendEventWithURL(GoogleAnalytics.CAT_TabToolbar_Refresh, tabPanel.getFullId());
    		}
    	});
    	refreshButton.addStyleName("tabButton");
    	return refreshButton;
    }

    private EsaSkyButton createStyleButton() {
        
        EsaSkyButton styleButton = new EsaSkyButton("#000000", false);
        styleButton.setMediumStyle();
        styleButton.setTitle(TextMgr.getInstance().getText("closeableTabLayoutPanel_styleBtn"));
        styleButton.addClickHandler(new ClickHandler() {
    
            @Override
            public void onClick(final ClickEvent arg0) {
                final String selectedTabId = tabs.get(tabLayout.getSelectedIndex()).getId();
                CloseableTabLayoutPanel.this.fireShowStylePanel(selectedTabId);
                
                GoogleAnalytics.sendEventWithURL(GoogleAnalytics.CAT_TabToolbar_SetStyle, getSelectedWidget().getEsaSkyUniqID());
            }
        });
        styleButton.addStyleName("tabButton");
        return styleButton;
    }
    
    public void selectTab(ITablePanel tablePanel) {
    	tabLayout.selectTab(tablePanel.getWidget());
    }
    
    public final void addTab(final ITablePanel tabPanel,  final String helpTitle, final String helpDescription) {
        addTab(new MissionTabButtons(helpTitle, helpDescription, tabPanel.getEntity()), tabPanel);
    }
    
    private final void addTab(final MissionTabButtons tab, final ITablePanel tabPanel) {

        tab.setCloseClickHandler(new ClickHandler() {

            @Override
            public void onClick(final ClickEvent event) {
                tabPanel.closeTablePanel();
            }
        });
        
        tab.setStyleClickHandler(new ClickHandler() {

            @Override
            public void onClick(final ClickEvent event) {
                // Fires the show style menu delayed to allow the event bus to propagate the ResultTabSelectedEvent first,
                // to avoid that this stylePanel get closed just after being opened. If the tab is different that selected on
                // the call is delayed, else none
                Timer timer = new Timer() {
                    public void run () {
                        CloseableTabLayoutPanel.this.fireShowStylePanel(tab.getId());
                    }
                };
  
                timer.schedule((!tabs.get(tabLayout.getSelectedIndex()).getId().equals(tab.getId())) ? 300 : 5);
            }
        });
        
        tabPanel.registerClosingObserver(() -> removeTab(tab));
        
        
        this.tabs.add(tab);
        this.tabWidgetIds.put(tab, tab.getId());
        shadedArea.removeStyleName("hidden");
        
        this.tabLayout.add(tabPanel.getWidget(), tab);
        tabPanel.registerObserver(new TableObserver() {
            
			@Override
			public void numberOfShownRowsChanged(int numberOfShownRows) {
				ensureCorrectButtonClickability(numberOfShownRows);
			}

            @Override
            public void onSelection(ITablePanel selectedTablePanel) {
                selectTab(selectedTablePanel);
            }

            @Override
            public void onUpdateStyle(ITablePanel panel) {
            	if(getSelectedWidget() == panel) {
            		final GeneralEntityInterface entity = tabPanel.getEntity();
                    ensureCorrectRelatedInformationVisibilty(entity);
            	}
            }
        });
	    updateStyleOnTab(getWidgetIndex(tabPanel.getWidget()));

        
        GoogleAnalytics.sendEventWithURL(GoogleAnalytics.CAT_TabOpened, tabPanel.getFullId());
        tabPanel.getEntity().registerColorChangeObserver(new ColorChangeObserver() {
			
			@Override
			public void onColorChange(String newColor) {
				if (styleButton != null && styleButton.isVisible()) {
					styleButton.setCircleColor(newColor);
				}
			}
		});
        styleButton.setCircleColor(tabPanel.getDescriptor().getPrimaryColor());
        setCloseAllButtonVisibility();
        
        notifyOpeningObservers(tab.getId());
    }
    
    private void setCloseAllButtonVisibility() {
        if(this.tabs.size() > 1) {
            closeAllButton.removeStyleName("hidden");
        } else {
            closeAllButton.addStyleName("hidden");
        }
    }
    
    public boolean checkIfIdExists(String id) {
    	if(tabWidgetIds.inverse().get(id) != null) {
    		return true;
    	}
    	
    	return false;
    }
    
    private void ensureCorrectButtonClickability(int numberOfShownRows) {
        int selectedIndex = getSelectedTabIndex();
        if (selectedIndex == -1) {return;}
        ITablePanel tabPanel = tabLayout.getWidget(selectedIndex);
        sendButton.setEnabled(!tabPanel.isMOCMode() && numberOfShownRows > 0 && !tabPanel.getIsHidingTable());
        saveButton.setEnabled(!tabPanel.isMOCMode() && numberOfShownRows > 0 && !tabPanel.getIsHidingTable());
    }

    public final int getSelectedTabIndex() {
        return this.tabLayout.getSelectedIndex();
    }
    
    public ITablePanel getSelectedWidget() {
        final int selectedIdx = getSelectedTabIndex();
        if(selectedIdx != -1) {
           return tabLayout.getWidget(selectedIdx);
        }
        return null;
    }
    
    public final ITablePanel getWidget(final int index) {
        return (ITablePanel)this.tabLayout.getWidget(index);
    }

    public final int getWidgetIndex(final Widget w) {
        return this.tabLayout.getWidgetIndex(w);
    }
    
    public void registerClosingObserver(TabObserver obs) {
    	closingObservers.add(obs);
    }

    public void notifyClosingObservers(String id) {
    	for(TabObserver obs : closingObservers) {
    		obs.onClose(id);
    	}
    }

    public void notifyOpeningObservers(String id) {
    	for(TabObserver obs : closingObservers) {
    		obs.onOpen(id);
    	}
    }

    public final void removeTab(final MissionTabButtons tab) {
        int index = this.tabs.indexOf(tab);

        this.tabs.remove(index);
        this.tabLayout.remove(index);
        
        String id = tabWidgetIds.get(tab);
        notifyClosingObservers(id);
        this.tabWidgetIds.remove(tab);
        
        if(tabs.isEmpty()){
        	ResultsPanel.closeDataPanel();
        	toggleDataPanelButton.addStyleName("hidden");
        	closeAllButton.addStyleName("hidden");
        	shadedArea.addStyleName("hidden");
        }
        setCloseAllButtonVisibility();
    }
    
    public final boolean removeTabById(final String id) {
    	MissionTabButtons tab = getTabFromId(id);
    	if(tab != null) {
    		removeTab(tab);
    		return true;
    	}
    	
    	return false;
    }

    public final String getIdFromTab(final Widget w) {
        return this.tabWidgetIds.get(w);
    }
    
    public final MissionTabButtons getTabFromId(final String id) {
        return this.tabWidgetIds.inverse().get(id);
    }

    public final ITablePanel getTablePanelFromId(final String id) {
    	MissionTabButtons tab = getTabFromId(id);
        if (tab != null) {
            return this.tabLayout.getWidget(this.tabs.indexOf(tab));
        }
        return null;
    }

    private void updateStyleOnTab(final int index) {
        // remove style from previous selection (if any)
        for (MissionTabButtons tab : this.tabs) {
            tab.updateStyle(false);
        }
        // update style (dark font color, show close icon) from selected index
        MissionTabButtons tab = this.tabs.get(index);
        if (tab != null) {
            tab.updateStyle(true);
        }
    }

    public final BiMap<MissionTabButtons, String> getTabWidgetIds() {
        return tabWidgetIds;
    }
	
	public void notifyDataPanelToggled() {
		toggleButtonRotation();
	}
	
	private void fireShowStylePanel(String tabId) {
		ITablePanel tablePanel = getTablePanelFromId(tabId);
		int offset = styleButton.getAbsoluteTop();
		if(refreshButton != null && refreshButton.getAbsoluteTop() > 0) {
			offset = refreshButton.getAbsoluteTop();
		}
		if(tablePanel != null ) {
			tablePanel.showStylePanel(styleButton.getAbsoluteLeft() + styleButton.getOffsetWidth() + 2, 
					offset);
		}else {
			Log.error("Can't show stylePanel. Panel not found for ID: " + tabId);
		}
	}
	
}
