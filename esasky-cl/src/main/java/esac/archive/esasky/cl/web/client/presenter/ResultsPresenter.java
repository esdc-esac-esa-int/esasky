/*
ESASky
Copyright (C) 2025 Henrik Norman

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published
by the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package esac.archive.esasky.cl.web.client.presenter;

import com.allen_sauer.gwt.log.client.Log;
import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.user.client.ui.Widget;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteCoordinatesOrFoVChangedEvent;
import esac.archive.esasky.cl.gwidgets.client.util.SaveAllView;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.callback.ICountRequestHandler;
import esac.archive.esasky.cl.web.client.event.*;
import esac.archive.esasky.cl.web.client.event.sso.SSOCrossMatchEvent;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.TapRowList;
import esac.archive.esasky.cl.web.client.model.entities.GeneralEntityInterface;
import esac.archive.esasky.cl.web.client.repository.DescriptorRepository;
import esac.archive.esasky.cl.web.client.status.GUISessionStatus;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.utility.SampConstants.SampAction;
import esac.archive.esasky.cl.web.client.view.resultspanel.ITablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.tab.CloseableTabLayoutPanel;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.multiretrievalbean.MultiRetrievalBeanList;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants.ReturnType;

import java.util.HashMap;

/**
 * @author ESDC team Copyright (c) 2015- European Space Agency
 */
public class ResultsPresenter implements ICountRequestHandler {

    /** local instance of view. */
    private final View view;
    
    /** local instance reference to descriptor repository */
    private final DescriptorRepository descriptorRepo;
    
    private static final String ENTITY_TYPE = " ENTITY TYPE: ";
    private static final String GET_METADATA = "[getMetadata][";

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

        void removeAllTabs();
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
                clickEvent -> {
                    if(GUISessionStatus.getIsInScienceMode()) {
                        if (!descriptorRepo.hasSearchArea()) {
                            descriptorRepo.doCountAll();
                        }
                    } else {
                        GUISessionStatus.setDoCountOnEnteringScienceMode();
                    }
                    CommonEventBus.getEventBus().fireEvent(new UrlChangedEvent());
                    GoogleAnalytics.sendEventWithURL(GoogleAnalytics.CAT_COUNT, GoogleAnalytics.CAT_COUNT);
                });
        
        // Send Table to Event
        CommonEventBus.getEventBus().addHandler(SendTableToEvent.TYPE,
                clickEvent -> sendToSamp());

        CommonEventBus.getEventBus().addHandler(ExportVOTableEvent.TYPE,
                clickEvent -> {
                    doSaveTableAs(ReturnType.VOTABLE);
                    clickEvent.getSaveAllView().getSaveOrDownloadDialog().hide();
                });

        CommonEventBus.getEventBus().addHandler(ExportCSVEvent.TYPE,
                clickEvent -> {
                    doSaveTableAs(ReturnType.CSV);
                    clickEvent.getSaveAllView().getSaveOrDownloadDialog().hide();
                });

        // Update number of rows selected event.
        CommonEventBus.getEventBus().addHandler(UpdateNumRowsSelectedEvent.TYPE,
                clickEvent -> updateNumberOfObservationsSelected(clickEvent.getSaveAllView()));

        CommonEventBus.getEventBus().addHandler(SSOCrossMatchEvent.TYPE,
                event -> descriptorRepo.doCountSSO(event.getSsoName(), event.getSsoType()));
        
      CommonEventBus.getEventBus().addHandler(AddTableEvent.TYPE,
              event -> getMetadata(event.getEntity()));
    }
    
    public final CloseableTabLayoutPanel getTabPanel() {
        return this.view.getTabPanel();
    }
    
    protected final void getUserMetadataAndPolygons(final GeneralEntityInterface entity, GeneralJavaScriptObject userData, boolean shouldHavePanel) {
    	Log.debug("[ResultPresenter][getUserMetadataAndPolygons]");
		if(shouldHavePanel) {
			
	    	ITablePanel panel = entity.getTablePanel();
	    	if(panel == null) {
	    		panel = this.view.addResultsTab(entity, entity.getHelpTitle(),
	    				"User defined metadata table for " + entity.getHelpTitle());
	    	}
	        panel.insertData(userData);
	        panel.goToCoordinateOfFirstRow();
	        entity.setTablePanel(panel);
    	}else {
    		GeneralJavaScriptObject footprintList = userData.getProperty("overlaySet").getProperty("skyObjectList");
    		entity.addShapes(footprintList, null);
    	}
    }

    protected final void coneSearch(final GeneralEntityInterface entity, final SkyViewPosition conePos) {
        final String debugPrefix = "[coneSearch][" + entity.getDescriptor().getShortName() + "]";
        Log.debug(debugPrefix + ENTITY_TYPE + entity.getClass().getSimpleName());
        
    	this.view.addResultsTab(entity, entity.getHelpTitle(), entity.getHelpText());

        entity.coneSearch(conePos);
    }

    protected final void getMetadata(final GeneralEntityInterface entity) {
        final String debugPrefix = GET_METADATA + entity.getDescriptor().getShortName() + "]";
        Log.debug(debugPrefix + ENTITY_TYPE + entity.getClass().getSimpleName());

    	this.view.addResultsTab(entity, entity.getHelpTitle(), entity.getHelpText());

        entity.fetchData();
    }

    protected final void getMetadata(final GeneralEntityInterface entity, final GeneralJavaScriptObject data) {
        final String debugPrefix = GET_METADATA + entity.getDescriptor().getLongName() + "]";
        Log.debug(debugPrefix + ENTITY_TYPE + entity.getClass().getSimpleName());

        this.view.addResultsTab(entity, entity.getHelpTitle(), entity.getHelpText());

        entity.insertExternalData(data);
    }
   
    protected final void getMetadataWithoutMOC(final GeneralEntityInterface entity) {
    	final String debugPrefix = GET_METADATA + entity.getDescriptor().getShortName() + "]";
    	Log.debug(debugPrefix + ENTITY_TYPE + entity.getClass().getSimpleName());
    	
    	this.view.addResultsTab(entity, entity.getHelpTitle(), entity.getHelpText());

    	entity.fetchDataWithoutMOC();
    }
    
    protected final void getMetadata(final GeneralEntityInterface entity, String adql) {
    	final String debugPrefix = GET_METADATA + entity.getDescriptor().getLongName() + "]";
    	Log.debug(debugPrefix + ENTITY_TYPE + entity.getClass().getSimpleName());

    	this.view.addResultsTab(entity, entity.getHelpTitle(), entity.getHelpText());
    	entity.fetchData(adql);
    }

    public final ITablePanel addResultsTab(final GeneralEntityInterface entity) {
    	final String debugPrefix = GET_METADATA + entity.getDescriptor().getShortName() + "]";
    	Log.debug(debugPrefix + ENTITY_TYPE + entity.getClass().getSimpleName());
    	
    	return this.view.addResultsTab(entity, entity.getHelpTitle(), entity.getHelpText());
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

    public void removeAllTabs() {
        view.removeAllTabs();
    }
}