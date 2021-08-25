package esac.archive.esasky.cl.web.client.presenter;

import java.util.HashMap;
import com.allen_sauer.gwt.log.client.Log;
import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.user.client.ui.Widget;

import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteCoordinatesOrFoVChangedEvent;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteCoordinatesOrFoVChangedEventHandler;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.multiretrievalbean.MultiRetrievalBeanList;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants.ReturnType;
import esac.archive.esasky.cl.gwidgets.client.util.SaveAllView;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.callback.ICountRequestHandler;
import esac.archive.esasky.cl.web.client.event.AddTableEvent;
import esac.archive.esasky.cl.web.client.event.AddTableEventHandler;
import esac.archive.esasky.cl.web.client.event.ESASkySampEvent;
import esac.archive.esasky.cl.web.client.event.ExportCSVEvent;
import esac.archive.esasky.cl.web.client.event.ExportCSVEventHandler;
import esac.archive.esasky.cl.web.client.event.ExportVOTableEvent;
import esac.archive.esasky.cl.web.client.event.ExportVOTableEventHandler;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPushEvent;
import esac.archive.esasky.cl.web.client.event.SendTableToEvent;
import esac.archive.esasky.cl.web.client.event.SendTableToEventHandler;
import esac.archive.esasky.cl.web.client.event.UpdateNumRowsSelectedEvent;
import esac.archive.esasky.cl.web.client.event.UpdateNumRowsSelectedEventHandler;
import esac.archive.esasky.cl.web.client.event.UrlChangedEvent;
import esac.archive.esasky.cl.web.client.event.sso.SSOCrossMatchEvent;
import esac.archive.esasky.cl.web.client.event.sso.SSOCrossMatchEventHandler;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.TapRowList;
import esac.archive.esasky.cl.web.client.model.entities.GeneralEntityInterface;
import esac.archive.esasky.cl.web.client.repository.DescriptorRepository;
import esac.archive.esasky.cl.web.client.status.GUISessionStatus;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.utility.SampConstants.SampAction;
import esac.archive.esasky.cl.web.client.view.resultspanel.ITablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.tab.CloseableTabLayoutPanel;

/**
 * @author ESDC team Copyright (c) 2015- European Space Agency
 */
public class ResultsPresenter implements ICountRequestHandler {

    /** local instance of view. */
    private final View view;
    
    /** local instance reference to descriptor repository */
    private final DescriptorRepository descriptorRepo;

    /** Row list mapper. */
    public interface TapRowListMapper extends ObjectMapper<TapRowList> {
    }

    /**
     * MultiRetrievalBeanListMapper interface.
     * @author fgiordan
     */
    public interface MultiRetrievalBeanListMapper extends ObjectMapper<MultiRetrievalBeanList> {
    }

    public interface View {
        CloseableTabLayoutPanel getTabPanel();

        Widget getTabFromTableId(String id);

        ITablePanel addResultsTab(GeneralEntityInterface entity, String helpTitle, String helpDescription);

        void removeTab(String id);
    }

    public ResultsPresenter(final View inputView, DescriptorRepository descriptorRepo) {
        this.view = inputView;
        this.descriptorRepo = descriptorRepo;
        
        bind();
    }

    /**
     * Bind view with presenter.
     */
    private void bind() {

        CommonEventBus.getEventBus().addHandler(AladinLiteCoordinatesOrFoVChangedEvent.TYPE,
                new AladinLiteCoordinatesOrFoVChangedEventHandler() {

                    @Override
                    public void onChangeEvent(
                            final AladinLiteCoordinatesOrFoVChangedEvent clickEvent) {
                    	if(GUISessionStatus.getIsInScienceMode()) {
                    		descriptorRepo.doCountAll();
                    	} else {
                    		GUISessionStatus.setDoCountOnEnteringScienceMode();
                    	}
                    	CommonEventBus.getEventBus().fireEvent(new UrlChangedEvent());
                    	GoogleAnalytics.sendEventWithURL(GoogleAnalytics.CAT_COUNT, GoogleAnalytics.CAT_COUNT);
                    }
        });
        
        // Send Table to Event
        CommonEventBus.getEventBus().addHandler(SendTableToEvent.TYPE,
                new SendTableToEventHandler() {

            @Override
            public void onSendTableClick(final SendTableToEvent clickEvent) {
                sendToSamp();
            }
        });

        CommonEventBus.getEventBus().addHandler(ExportVOTableEvent.TYPE, 
                new ExportVOTableEventHandler() {
            
            @Override
            public void onExportClick(final ExportVOTableEvent clickEvent) {
                doSaveTableAs(EsaSkyConstants.ReturnType.VOTABLE);
                clickEvent.getSaveAllView().getSaveOrDownloadDialog().hide();
            }
        });

        CommonEventBus.getEventBus().addHandler(ExportCSVEvent.TYPE, 
                new ExportCSVEventHandler() {
            
            @Override
            public void onExportClick(final ExportCSVEvent clickEvent) {
                doSaveTableAs(EsaSkyConstants.ReturnType.CSV);
                clickEvent.getSaveAllView().getSaveOrDownloadDialog().hide();
            }
        });

        // Update number of rows selected event.
        CommonEventBus.getEventBus().addHandler(UpdateNumRowsSelectedEvent.TYPE,
                new UpdateNumRowsSelectedEventHandler() {

            @Override
            public void onUpdateClick(final UpdateNumRowsSelectedEvent clickEvent) {
                updateNumberOfObservationsSelected(clickEvent.getSaveAllView());
            }
        });

        CommonEventBus.getEventBus().addHandler(SSOCrossMatchEvent.TYPE,
                new SSOCrossMatchEventHandler() {

            @Override
            public void newSsoSelected(final SSOCrossMatchEvent event) {
                descriptorRepo.doCountSSO(event.getSsoName(), event.getSsoType());
            }
        });
        
      CommonEventBus.getEventBus().addHandler(AddTableEvent.TYPE,
          new AddTableEventHandler() {
    
            @Override
            public void onEvent(AddTableEvent event) {
                getMetadata(event.getEntity());
            }
          });
    }
    
    public final CloseableTabLayoutPanel getTabPanel() {
        return this.view.getTabPanel();
    }
    
    protected final void getUserMetadataAndPolygons(final GeneralEntityInterface entity, GeneralJavaScriptObject userData, boolean shouldHavePanel) {
    	Log.debug("[ResultPresenter][getUserMetadataAndPolygons]");
		if(shouldHavePanel) {
			
	    	ITablePanel panel = entity.getTablePanel();
	    	if(panel == null) {
	    		panel = this.view.addResultsTab(entity, entity.getDescriptor().getGuiLongName(), 
	    				"User defined metadata table for " + entity.getDescriptor().getGuiLongName());
	    	}
	        panel.insertData(userData);
	        panel.goToCoordinateOfFirstRow();
	        entity.setTablePanel(panel);
    	}else {
    		GeneralJavaScriptObject footprintList = userData.getProperty("overlaySet").getProperty("skyObjectList");
    		entity.addShapes(footprintList);
    	}
    }

    protected final void coneSearch(final GeneralEntityInterface entity, final SkyViewPosition conePos) {
        final String debugPrefix = "[coneSearch][" + entity.getDescriptor().getGuiShortName() + "]";
        Log.debug(debugPrefix + " ENTITY TYPE: " + entity.getClass().getSimpleName());
        
    	this.view.addResultsTab(entity, entity.getDescriptor().getGuiLongName(), entity.getHelpText());

        entity.coneSearch(conePos);
    }

    protected final void getMetadata(final GeneralEntityInterface entity) {
        final String debugPrefix = "[getMetadata][" + entity.getDescriptor().getGuiShortName() + "]";
        Log.debug(debugPrefix + " ENTITY TYPE: " + entity.getClass().getSimpleName());

    	this.view.addResultsTab(entity, entity.getDescriptor().getGuiLongName(), entity.getHelpText());

        entity.fetchData();
    }
   
    protected final void getMetadataWithoutMOC(final GeneralEntityInterface entity) {
    	final String debugPrefix = "[getMetadata][" + entity.getDescriptor().getGuiShortName() + "]";
    	Log.debug(debugPrefix + " ENTITY TYPE: " + entity.getClass().getSimpleName());
    	
    	this.view.addResultsTab(entity, entity.getDescriptor().getGuiLongName(), entity.getHelpText());

    	entity.fetchDataWithoutMOC();
    }
    
    protected final void getMetadata(final GeneralEntityInterface entity, String adql) {
    	final String debugPrefix = "[getMetadata][" + entity.getDescriptor().getGuiShortName() + "]";
    	Log.debug(debugPrefix + " ENTITY TYPE: " + entity.getClass().getSimpleName());
    	
    	this.view.addResultsTab(entity, entity.getDescriptor().getGuiLongName(), entity.getHelpText());
    	entity.fetchData(adql);
    }

    public final ITablePanel addResultsTab(final GeneralEntityInterface entity) {
    	final String debugPrefix = "[getMetadata][" + entity.getDescriptor().getGuiShortName() + "]";
    	Log.debug(debugPrefix + " ENTITY TYPE: " + entity.getClass().getSimpleName());
    	
    	return this.view.addResultsTab(entity, entity.getDescriptor().getGuiLongName(), entity.getHelpText());
    }

    private void sendToSamp() {

        Log.debug("[ResultsPresenter] Into sendToSamp()");
        
        HashMap<String, String> sampUrlsPerMissionMap = new HashMap<String, String>();
        	
    	int currentTab = view.getTabPanel().getSelectedTabIndex();
        ITablePanel tab = view.getTabPanel().getWidget(currentTab);
        
		final String tableName = tab.getLabel();
		final String voTableString = tab.getVoTableString();
		
		// Store Tab/list of urls into the hashmap
		sampUrlsPerMissionMap.put(tableName, voTableString);
		
		// Send all URL to Samp
		CommonEventBus.getEventBus().fireEvent(new ESASkySampEvent(SampAction.SEND_VOTABLE,
				sampUrlsPerMissionMap));
		
		// Display top progress bar...
		Log.debug("[ResultsPresenter/sendToSamp()] About to send 'show top progress bar...' event!!!");
		
		CommonEventBus.getEventBus().fireEvent(
				new ProgressIndicatorPushEvent(SampAction.SEND_VOTABLE.toString(),
						TextMgr.getInstance().getText("sampConstants_sendingViaSamp")
						.replace(EsaSkyConstants.REPLACE_PATTERN, tableName), "Table: " + tableName + ", voTable: " + voTableString));
		
		sendGAEventWithCurrentTab(GoogleAnalytics.CAT_TABTOOLBAR_SENDTOSAMP, "");
    }

    private void sendGAEventWithCurrentTab (String eventCategory, String extra) {
        final int currentTab = view.getTabPanel().getSelectedTabIndex();
        final Widget tab = view.getTabPanel().getWidget(currentTab).getWidget();
        if (tab instanceof ITablePanel) {
            GoogleAnalytics.sendEventWithURL(eventCategory, ((ITablePanel)tab).getFullId(), extra);
        }
    }

    /**
     * updateNumberOfObservationsSelected().
     * @param saveAllView Input SaveAllView
     */
    private void updateNumberOfObservationsSelected(final SaveAllView saveAllView) {
        // Get Current Tab.
        int currentTab = view.getTabPanel().getSelectedTabIndex();
        ITablePanel tab = view.getTabPanel().getWidget(currentTab);

        GeneralJavaScriptObject[] subset = tab.getSelectedRows();
        Log.debug("[ResultPresenter][updateNumberOfObservationsSelected]NUMBER OF SELECTED OBS "
                + subset.length);
        saveAllView.updateNumberOfSelectedElementsLabel(subset.length);
    }

    /**
     * doSaveTableAs().
     * @param votable Input ReturnType
     */
    private void doSaveTableAs(final ReturnType type) {
        Log.debug("[ResultsPresenter/doSaveTableAs()] Saving results table in format: "
                + type.toString());
        
        String eventCategory = "";
    	if (type.equals(ReturnType.CSV)) {
    		view.getTabPanel().getSelectedWidget().exportAsCsv();
    		eventCategory = GoogleAnalytics.CAT_DOWNLOAD_CSV;
    	} else {
    		view.getTabPanel().getSelectedWidget().exportAsVot();
    		eventCategory = GoogleAnalytics.CAT_DOWNLOAD_VOT;
    	}
    	GoogleAnalytics.sendEventWithURL(eventCategory, view.getTabPanel().getSelectedWidget().getFullId(), "");
    }
    
    @Override
    public String getProgressIndicatorMessage() {
        return TextMgr.getInstance().getText("CountRequestCallback_countingAvailableData");
    }
    
    public void removeTab(String id) {
    	    view.removeTab(id);
    }
}