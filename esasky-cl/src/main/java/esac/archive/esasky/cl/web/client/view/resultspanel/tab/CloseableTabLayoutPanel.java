package esac.archive.esasky.cl.web.client.view.resultspanel.tab;

import java.util.ArrayList;
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
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import esac.archive.esasky.ifcs.model.descriptor.ColorChangeObserver;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.MetadataDescriptor;
import esac.archive.esasky.cl.gwidgets.client.util.SaveAllView;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.Modules;
import esac.archive.esasky.cl.web.client.event.DataPanelResizeEvent;
import esac.archive.esasky.cl.web.client.event.DataPanelResizeEventHandler;
import esac.archive.esasky.cl.web.client.event.ExportCSVEvent;
import esac.archive.esasky.cl.web.client.event.ExportVOTableEvent;
import esac.archive.esasky.cl.web.client.event.SendTableToEvent;
import esac.archive.esasky.cl.web.client.event.UpdateNumRowsSelectedEvent;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.entities.GeneralEntityInterface;
import esac.archive.esasky.cl.web.client.status.GUISessionStatus;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyButton;
import esac.archive.esasky.cl.web.client.view.resultspanel.AbstractTableObserver;
import esac.archive.esasky.cl.web.client.view.resultspanel.AbstractTablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.ResultsPanel;

/**
 * @author ESDC team Copyright (c) 2015- European Space Agency
 */
public class CloseableTabLayoutPanel extends Composite {	
	
    /** Default filler width. */
    private static final String DEFAULT_FILLER_WIDTH = "6px";

    private final Resources resources = GWT.create(Resources.class);
    private CssResource style;
    private BiMap<MissionTabButtons, String> tabWidgetIds = HashBiMap.create();

    private List<MissionTabButtons> tabs = new ArrayList<MissionTabButtons>();
    private ScrollTabLayoutPanel tabLayout;
    private SaveAllView saveAllView;
    private final HTML emptyTableMessage;
    private EsaSkyButton toggleDataPanelButton = new EsaSkyButton(this.resources.arrowIcon());
    
    private VerticalPanel tabButtonsPanel;
    private EsaSkyButton refreshButton;
    private EsaSkyButton styleButton;
    private EsaSkyButton recenterButton;
    private EsaSkyButton sendButton;
    private EsaSkyButton saveButton;

    /**
     * Public resources interface.
     * @author ESDC team
     */
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

        @Source("closeableTabLayoutPanel.css")
        @CssResource.NotStrict
        CssResource style();
    }
    
    /**
     * CloseableTabLayoutPanel().
     * @param height Input double
     * @param unit Input Unit
     * @param showScroll Input boolean.
     */
    public CloseableTabLayoutPanel(final double height, final Unit unit, final boolean showScroll) {
        this.style = this.resources.style();
        this.style.ensureInjected();

        FlowPanel closeableTabLayoutPanel = new FlowPanel();
        
        emptyTableMessage = new HTML();
        emptyTableMessage.addStyleName("emtpyTableMessage");
        closeableTabLayoutPanel.add(emptyTableMessage);

        FlowPanel buttonsAndObservationPanel = new FlowPanel();
        buttonsAndObservationPanel.addStyleName("observationPanel");
		
        toggleDataPanelButton.addStyleName("toggleDataPanelButton");
        toggleDataPanelButton.setNonTransparentBackground();
        toggleDataPanelButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent arg0) {
				ResultsPanel.toggleOpenCloseDataPanel();
			}
		});
        
        CommonEventBus.getEventBus().addHandler(DataPanelResizeEvent.TYPE, new DataPanelResizeEventHandler() {
			
			@Override
			public void onDataPanelResize(DataPanelResizeEvent event) {
				if(event.getNewHeight() > 40) {
					toggleDataPanelButton.rotate(180, 1000);
				} else {
					toggleDataPanelButton.rotate(0, 1000);
				}
			}
		});
		buttonsAndObservationPanel.add(toggleDataPanelButton);

        tabButtonsPanel = new VerticalPanel();
        refreshButton = createRefreshButton();
        styleButton = createStyleButton();
        recenterButton = createRecenterButton();
        
        tabButtonsPanel.add(refreshButton);
        tabButtonsPanel.add(styleButton);
        tabButtonsPanel.add(recenterButton);
        tabButtonsPanel.add(createSendButton());
        tabButtonsPanel.add(createSaveButton());

        tabButtonsPanel.addStyleName("tabButtons");
        buttonsAndObservationPanel.add(tabButtonsPanel);

        tabLayout = new ScrollTabLayoutPanel(height, unit, showScroll);
        tabLayout.addSelectionHandler(new SelectionHandler<Integer>() {

            @Override
            public void onSelection(final SelectionEvent<Integer> event) {
                final int index = event.getSelectedItem().intValue();
                
                updateStyleOnTab(index);
                
                AbstractTablePanel tabPanel = (AbstractTablePanel)CloseableTabLayoutPanel.this.getWidget(index);
                final GeneralEntityInterface entity = tabPanel.getEntity();
                
                ensureCorrectRelatedInformationVisibilty(entity);
            }
        });
        buttonsAndObservationPanel.add(tabLayout);

        closeableTabLayoutPanel.add(buttonsAndObservationPanel);

        initWidget(closeableTabLayoutPanel);
    }

	private void toggleButtonRotation(){
		if(GUISessionStatus.isDataPanelOpen()){
            	toggleDataPanelButton.removeStyleName("hidden");
            	toggleDataPanelButton.rotate(180, 1000);
		} else {
        	    toggleDataPanelButton.rotate(0, 1000);
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
    	ensureCorrectButtonClickability();
    }

    private EsaSkyButton createSaveButton() {
        saveButton = new EsaSkyButton(resources.downloadIcon());
        saveButton.setMediumStyle();
        saveButton.setTitle(TextMgr.getInstance().getText("closeableTabLayoutPanel_saveResultsTableOrDownload"));

        final String tabType = AbstractTablePanel.class.getSimpleName();
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
                        new ExportVOTableEvent(selectedTabId, tabType, saveAllView));
            }

        });

        // Bind save as CSV anchor
        saveAllView.getSaveAsCSVAnchor().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(final ClickEvent event) {
                String selectedTabId = tabs.get(tabLayout.getSelectedIndex()).getId();
                CommonEventBus.getEventBus().fireEvent(
                        new ExportCSVEvent(selectedTabId, tabType, saveAllView));
            }
        });

        saveButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(final ClickEvent event) {
                String selectedTabId = tabs.get(tabLayout.getSelectedIndex()).getId();
                // Update number of observation selected before display the pop-up
                CommonEventBus.getEventBus().fireEvent(
                        new UpdateNumRowsSelectedEvent(selectedTabId, saveAllView));
                GeneralEntityInterface entity = CloseableTabLayoutPanel.this.getWidget(tabLayout.getSelectedIndex()).getEntity();
                List<MetadataDescriptor> metadataDescriptors = entity.getDescriptor().getMetadata();
                boolean hasProductUrl = false;
                for(MetadataDescriptor descriptor : metadataDescriptors) {
                	if(descriptor.getTapName().equals("product_url")) {
                		hasProductUrl = true;
                		break;
                	}
                }
                saveAllView.setProductsDownloadVisible(
                		entity.hasDownloadableDataProducts()
                		&& hasProductUrl);
                // Set pop-up position.
                saveAllView.getSaveOrDownloadDialog().setPopupPositionAndShow(
                        new PopupPanel.PositionCallback() {

                            @Override
                            public void setPosition(final int offsetWidth, final int offsetHeight) {
                                saveAllView.getSaveOrDownloadDialog().showRelativeTo(saveButton);
                                saveAllView.getSaveOrDownloadDialog().show();
                            }
                        });

            }
        });
        saveButton.addStyleName("tabButton");
        
        return saveButton;
    }

    private EsaSkyButton createSendButton() {

        	sendButton = new EsaSkyButton(resources.sendIcon());
        	sendButton.setMediumStyle();
        sendButton.setTitle(TextMgr.getInstance().getText("closeableTabLayoutPanel_sendTableToVOA"));

        // Bind 'send' Icon
        sendButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(final ClickEvent event) {
                String selectedTabId = tabs.get(tabLayout.getSelectedIndex()).getId();
                Log.debug("Samp on ObservationsTablePanel");
                CommonEventBus.getEventBus().fireEvent(
                        new SendTableToEvent(selectedTabId));
            }
        });
        sendButton.addStyleName("tabButton");
        return sendButton;
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

                GoogleAnalytics.sendEventWithURL(GoogleAnalytics.CAT_TabToolbar_Recenter, getSelectedWidget().getFullId());
            }
        });
        recenterButton.addStyleName("tabButton");
        
        return recenterButton;
    }

    private EsaSkyButton createRefreshButton() {
        
        	EsaSkyButton refreshButton = new EsaSkyButton(resources.refreshIcon());
        	refreshButton.setMediumStyle();
        refreshButton.setTitle(TextMgr.getInstance().getText("closeableTabLayoutPanel_refreshData"));
        refreshButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(final ClickEvent arg0) {
                AbstractTablePanel tabPanel = getSelectedWidget();
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
                
                GoogleAnalytics.sendEventWithURL(GoogleAnalytics.CAT_TabToolbar_SetStyle, getSelectedWidget().getFullId());
            }
        });
        styleButton.addStyleName("tabButton");
        return styleButton;
    }
    
    public void selectTab(AbstractTablePanel tablePanel) {
    	tabLayout.selectTab(tablePanel);
    }
    
    public final void addTab(final AbstractTablePanel tabPanel,  final String helpTitle, final String helpDescription) {
        addTab(new MissionTabButtons(helpTitle, helpDescription, tabPanel.getEntity()), tabPanel);
    }
    
    private final void addTab(final MissionTabButtons tab, final AbstractTablePanel tabPanel) {

        tab.setCloseClickHandler(new ClickHandler() {

            @Override
            public void onClick(final ClickEvent event) {
                tabPanel.closeTablePanel();
                removeTab(tab);
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
        
        this.tabs.add(tab);
        this.tabWidgetIds.put(tab, tab.getId());
        
        tabPanel.registerObserver(new AbstractTableObserver() {
            
			@Override
			public void numberOfShownRowsChanged(int numberOfShownRows) {
				ensureCorrectButtonClickability();
			}
        });
        this.tabLayout.add(tabPanel, tab);
	    updateStyleOnTab(getWidgetIndex(tabPanel));

        
        GoogleAnalytics.sendEventWithURL(GoogleAnalytics.CAT_TabOpened, tabPanel.getFullId());
        tabPanel.getDescriptor().registerColorChangeObservers(new ColorChangeObserver() {
			
			@Override
			public void onColorChange(IDescriptor descriptor, String newColor) {
				if (styleButton != null && styleButton.isVisible() && descriptor.getMission().equals(tabPanel.getDescriptor().getMission())) {
					styleButton.setCircleColor(newColor);
				}
			}
		});
        styleButton.setCircleColor(tabPanel.getDescriptor().getHistoColor());
    }
    
    private void ensureCorrectButtonClickability() {
        AbstractTablePanel tabPanel = ((AbstractTablePanel)tabLayout.getWidget(getSelectedTabIndex()));
        if(Modules.improvedDownload){
	        sendButton.setEnabled(tabPanel.getFilteredRows().size() > 0 && !tabPanel.getIsHidingTable());
	        saveButton.setEnabled(tabPanel.getFilteredRows().size() > 0 && !tabPanel.getIsHidingTable());
        } else {
        	sendButton.setEnabled(!tabPanel.getIsHidingTable());
        	saveButton.setEnabled(!tabPanel.getIsHidingTable());
        }
    }

    /**
     * getSelectedTabIndex().
     * @return integer
     */
    public final int getSelectedTabIndex() {
        return this.tabLayout.getSelectedIndex();
    }
    
    /**
     * getSelectedWidget().
     * @return AbstractTablePanel
     */
    public AbstractTablePanel getSelectedWidget() {
        final int selectedIdx = getSelectedTabIndex();
        if(selectedIdx != -1) {
           return ((AbstractTablePanel)tabLayout.getWidget(selectedIdx));
        }
        return null;
    }
    
    /**
     * getWidget().
     * @param index Input integer
     * @return Widget
     */
    public final AbstractTablePanel getWidget(final int index) {
        return this.tabLayout.getWidget(index);
    }

    /**
     * getWidgetIndex().
     * @param w Input Widget.
     * @return integer
     */
    public final int getWidgetIndex(final AbstractTablePanel w) {
        return this.tabLayout.getWidgetIndex(w);
    }

    /**
     * removeTab().
     * @param tab Input Tab.
     */
    public final void removeTab(final MissionTabButtons tab) {
        int index = this.tabs.indexOf(tab);

        this.tabs.remove(index);
        this.tabLayout.remove(index);
        this.tabWidgetIds.remove(tab);
        
        if(tabs.isEmpty()){
            	ResultsPanel.closeDataPanel();
            	toggleDataPanelButton.addStyleName("hidden");
        }
    }

    /**
     * getIdFromTab().
     * @param w Input Widget
     * @return String
     */
    public final String getIdFromTab(final Widget w) {
        return this.tabWidgetIds.get(w);
    }
    
    /**
     * getTabFromId().
     * @param id Input String
     * @return Widget.
     */
    public final MissionTabButtons getTabFromId(final String id) {
        return this.tabWidgetIds.inverse().get(id);
    }

    /**
     * getTabFromId().
     * @param id Input String
     * @return Widget.
     */
    public final AbstractTablePanel getAbstractTablePanelFromId(final String id) {
    	MissionTabButtons tab = getTabFromId(id);
        if (tab != null) {
            return (AbstractTablePanel)this.tabLayout.getWidget(this.tabs.indexOf(tab));
        }
        return null;
    }

    /**
     * updateStyleOnTab().
     * @param index input int
     */
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

    /**
     * getTabWidgetIds.
     * @return BiMap<Tab, String>
     */
    public final BiMap<MissionTabButtons, String> getTabWidgetIds() {
        return tabWidgetIds;
    }

    /**
     * getFillerPanel().
     * @param width Input String with the width of the filler panel.
     * @return SimplePanel
     */
    public final SimplePanel getFillerPanel(final String width) {

        SimplePanel auxPanel = new SimplePanel();
        auxPanel.getElement().setId("few");
        if ((width != null) && (width != "")) {
            auxPanel.setWidth(width);
        } else {
            auxPanel.setWidth(DEFAULT_FILLER_WIDTH);
        }
        return auxPanel;

    }

	public void refreshHeight() {
		tabLayout.refreshHeight();
	}
	
	public void notifyDataPanelToggled() {
		toggleButtonRotation();
		if(getSelectedWidget() != null) {
			getSelectedWidget().toggleVisibilityOfFreeFlowingElements();
		}
	}
	
	private void fireShowStylePanel(String tabId) {
		AbstractTablePanel tablePanel = getAbstractTablePanelFromId(tabId);
		tablePanel.showStylePanel(styleButton.getAbsoluteLeft() + styleButton.getOffsetWidth() + 2, 
                styleButton.getAbsoluteTop());
	}
}
