package esac.archive.esasky.cl.web.client.presenter;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.allen_sauer.gwt.log.client.Log;
import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteCoordinatesOrFoVChangedEvent;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteCoordinatesOrFoVChangedEventHandler;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteShapeDeselectedEvent;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteShapeDeselectedEventHandler;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteShapeHoverStartEvent;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteShapeHoverStartEventHandler;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteShapeHoverStopEvent;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteShapeHoverStopEventHandler;
import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesFrame;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.multiretrievalbean.MultiRetrievalBeanList;
import esac.archive.esasky.ifcs.model.shared.ESASkySSOSearchResult.ESASkySSOObjType;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants.ReturnType;
import esac.archive.esasky.cl.gwidgets.client.util.SaveAllView;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.Modules;
import esac.archive.esasky.cl.web.client.api.model.IJSONWrapper;
import esac.archive.esasky.cl.web.client.callback.GetMissionDataCountRequestCallback;
import esac.archive.esasky.cl.web.client.callback.ICountRequestHandler;
import esac.archive.esasky.cl.web.client.callback.ISSOCountRequestHandler;
import esac.archive.esasky.cl.web.client.callback.PublicationsBySourceCallback;
import esac.archive.esasky.cl.web.client.event.ESASkySampEvent;
import esac.archive.esasky.cl.web.client.event.ExportCSVEvent;
import esac.archive.esasky.cl.web.client.event.ExportCSVEventHandler;
import esac.archive.esasky.cl.web.client.event.ExportVOTableEvent;
import esac.archive.esasky.cl.web.client.event.ExportVOTableEventHandler;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPopEvent;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPushEvent;
import esac.archive.esasky.cl.web.client.event.SendTableToEvent;
import esac.archive.esasky.cl.web.client.event.SendTableToEventHandler;
import esac.archive.esasky.cl.web.client.event.UpdateNumRowsSelectedEvent;
import esac.archive.esasky.cl.web.client.event.UpdateNumRowsSelectedEventHandler;
import esac.archive.esasky.cl.web.client.event.UrlChangedEvent;
import esac.archive.esasky.cl.web.client.event.sso.SSOCrossMatchEvent;
import esac.archive.esasky.cl.web.client.event.sso.SSOCrossMatchEventHandler;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.TableRow;
import esac.archive.esasky.cl.web.client.model.TapRowList;
import esac.archive.esasky.cl.web.client.model.converter.TapToMmiDataConverter;
import esac.archive.esasky.cl.web.client.model.entities.EntityContext;
import esac.archive.esasky.cl.web.client.model.entities.GeneralEntityInterface;
import esac.archive.esasky.cl.web.client.model.entities.PublicationsBySourceEntity;
import esac.archive.esasky.cl.web.client.query.TAPCountObservationService;
import esac.archive.esasky.cl.web.client.query.TAPUtils;
import esac.archive.esasky.cl.web.client.repository.DescriptorRepository;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.cl.web.client.status.GUISessionStatus;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.DisplayUtils;
import esac.archive.esasky.cl.web.client.utility.DownloadUtils;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.utility.SampConstants.SampAction;
import esac.archive.esasky.cl.web.client.view.resultspanel.AbstractTablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.AbstractTablePanel.IPreviewClickedHandler;
import esac.archive.esasky.cl.web.client.view.resultspanel.ExtTapTablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.tab.CloseableTabLayoutPanel;

/**
 * @author ESDC team Copyright (c) 2015- European Space Agency
 */
public class ResultsPresenter implements ICountRequestHandler, ISSOCountRequestHandler {

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

        LayoutPanel getResultsLP();

        AbstractTablePanel addResultsTab(GeneralEntityInterface entity, String helpTitle, String helpDescription);

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
                    	GoogleAnalytics.sendEventWithURL(GoogleAnalytics.CAT_Count, GoogleAnalytics.CAT_Count);
                    }
        });
        
        CommonEventBus.getEventBus().addHandler(AladinLiteShapeDeselectedEvent.TYPE,
                new AladinLiteShapeDeselectedEventHandler() {

            @Override
            public void onShapeDeselectionEvent(AladinLiteShapeDeselectedEvent selectEvent) {

                AbstractTablePanel tableContainingShape = view.getTabPanel().getAbstractTablePanelFromId(selectEvent
                        .getOverlayName());

                if (tableContainingShape != null) {
                    tableContainingShape.deselectRow(selectEvent.getShapeId());
                }
            }
        });

        CommonEventBus.getEventBus().addHandler(AladinLiteShapeHoverStartEvent.TYPE,
                new AladinLiteShapeHoverStartEventHandler() {

            @Override
            public void onShapeHoverStartEvent(AladinLiteShapeHoverStartEvent hoverEvent) {

                AbstractTablePanel tablePanel = view
                        .getTabPanel().getAbstractTablePanelFromId(hoverEvent.getOverlayName());

                if (tablePanel != null) {
                    tablePanel.hoverStartRow(hoverEvent.getShapeId());
                }    
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
                descriptorRepo.doCountSSO(event.getSsoName(), event.getSsoType(), ResultsPresenter.this);
            }
        });
        
        CommonEventBus.getEventBus().addHandler(AladinLiteShapeHoverStopEvent.TYPE,
                new AladinLiteShapeHoverStopEventHandler() {

            @Override
            public void onShapeHoverStopEvent(AladinLiteShapeHoverStopEvent hoverEvent) {
                
                if (!Modules.publicationsModule || !hoverEvent.getOverlayName().startsWith(descriptorRepo.getPublicationsDescriptors()
                        .getDescriptors().get(0).getMission())) {
                    // Deselects a table row
                    AbstractTablePanel tablePanel = view.getTabPanel().getAbstractTablePanelFromId(
                                    hoverEvent.getOverlayName());
                    if (tablePanel != null) {
                        tablePanel.hoverStopRow(hoverEvent.getShapeId());
                    }
                }
            }
        });
    }
    
    public final CloseableTabLayoutPanel getTabPanel() {
        return this.view.getTabPanel();
    }
    
    protected final void getUserMetadataAndPolygons(final GeneralEntityInterface entity, final boolean showProgress,
            AbstractTablePanel panel, IJSONWrapper userData, CoordinatesFrame convertToFrame) {
    	Log.debug("[ResultPresenter][getUserMetadataAndPolygons]");
    	if (panel == null) {
    		panel = this.view.addResultsTab(entity, entity.getDescriptor().getGuiLongName(), 
            		"User defined metadata table for "+entity.getDescriptor().getGuiLongName());
        }
    	panel.clearTable();
    	TapRowList rowList = TapToMmiDataConverter.convertCSVToTAPRowList(userData, convertToFrame);
    	
    	if (rowList != null){
    		List<TableRow> tabRowList = TapToMmiDataConverter.convertTapToMMIData(rowList, entity.getDescriptor());
            entity.setMetadata(rowList);
            panel.insertData(tabRowList);	
            entity.addShapes(rowList);
    	}
    }

    public final void getExtTapMetadata(final GeneralEntityInterface entity, final boolean showProgress,
    		AbstractTablePanel panel) {
    	
    	final String debugPrefix = "[getMissionData][" + entity.getDescriptor().getGuiShortName() + "]";
    	
    	Log.debug(debugPrefix + " ENTITY TYPE: " + entity.getClass().getSimpleName());
    	
    	if (panel == null) {
    		panel = this.view.addResultsTab(entity, entity.getDescriptor().getGuiShortName(), entity.getDescriptor().getGuiLongName());
    	}
    	
		entity.fetchData(panel);
    }
    
    protected final void getMetadataAndFootprints(final GeneralEntityInterface entity, final boolean showProgress,
            AbstractTablePanel panel) {

        final String debugPrefix = "[getMissionData][" + entity.getDescriptor().getGuiShortName() + "]";

        Log.debug(debugPrefix + " ENTITY TYPE: " + entity.getClass().getSimpleName());

        final String missionId = entity.getDescriptor().getMission();
        final CountStatus countStatus = entity.getCountStatus();

        if (panel == null) {
            panel = this.view.addResultsTab(entity, entity.getDescriptor().getGuiLongName(), 
            		TextMgr.getInstance().getText("resultsPresenter_helpDescription_" + entity.getContext() + "_"+ missionId));
        }

        if (countStatus.hasMoved(missionId)) {
            String url = TAPCountObservationService.getInstance().getCount(
                    AladinLiteWrapper.getAladinLite(), entity.getDescriptor());
            Log.debug(debugPrefix + "Query [" + url + "]");

            RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
            try {
                panel.clearTable();
                builder.sendRequest(null, 
                		new GetMissionDataCountRequestCallback(entity, 
                				panel, 
                				TextMgr.getInstance().getText("GetMissionDataCountRequestCallback_searchingInArchive").replace("$NAME$", entity.getDescriptor().getGuiShortName()),
                				url)
                		);
            } catch (RequestException e) {
                Log.error(e.getMessage());
                Log.error(debugPrefix + "Error fetching JSON data from server");
            }
        } else {
            entity.fetchData(panel);
        }
    }

    private void sendToSamp() {

        Log.debug("[ResultsPresenter] Into sendToSamp()");

        HashMap<String, String> sampUrlsPerMissionMap = new HashMap<String, String>();
        Map.Entry<String, String> tempEntry = getResultsTableURLPerMission();

        if (tempEntry != null) {

            final String tableName = tempEntry.getKey();
            final String adqlQuery = tempEntry.getValue();

            String sampUrl = Window.Location.getProtocol() + "//" + Window.Location.getHost()
                    + TAPUtils.getTAPQuery(URL.encodeQueryString(adqlQuery), EsaSkyConstants.VOTABLE);

            Log.debug("[ResultsPresenter/getResultsTableURLPerMission()] Adding query to Samp: ["
                    + sampUrl + "] for tab: [" + tableName + "]");

            // Store Tab/list of urls into the hashmap
            sampUrlsPerMissionMap.put(tableName, sampUrl);

            // Send all URL to Samp
            CommonEventBus.getEventBus().fireEvent(new ESASkySampEvent(SampAction.SEND_VOTABLE,
                    sampUrlsPerMissionMap));

            // Display top progress bar...
            Log.debug("[ResultsPresenter/sendToSamp()] About to send 'show top progress bar...' event!!!");

            CommonEventBus.getEventBus().fireEvent(
                    new ProgressIndicatorPushEvent(SampAction.SEND_VOTABLE.toString(),
                            TextMgr.getInstance().getText("sampConstants_sendingViaSamp")
                            .replace(EsaSkyConstants.REPLACE_PATTERN, tableName), "Table: " + tableName + ", adqlQuery: " + adqlQuery + ", " + ", sampUrl: " + sampUrl));
            
            sendGAEventWithCurrentTab(GoogleAnalytics.CAT_TabToolbar_SendToSAMP, "");
        }
    }

    /**
     * getResultsTableURLPerMission().
     * @return Map.Entry<String, String>
     */
    private Map.Entry<String, String> getResultsTableURLPerMission() {
        Log.debug("[ResultsPresenter] Into getResultsTableURLPerMission()");

        // Get Current Tab
        int currentTab = view.getTabPanel().getSelectedTabIndex();
        Widget tab = view.getTabPanel().getWidget(currentTab);

        if (tab instanceof AbstractTablePanel) {

            final AbstractTablePanel tabPanel = (AbstractTablePanel) tab;

            // Get Table label
            final String tableName = tabPanel.getLabel();
            String adqlQuery;
            
            if (tabPanel.getEntity().getContext().equals(EntityContext.PUBLICATIONS)) {
                //If is a publications tab,
                adqlQuery = EntityContext.PUBLICATIONS.toString();
            } else {
            	if(Modules.improvedDownload){
            		adqlQuery = tabPanel.getADQLQueryForChosenRows();
            	} else{
                // Extract original ADQL query.
            		adqlQuery = tabPanel.getADQLQueryUrl();
            	}
            }

            return new AbstractMap.SimpleEntry<String, String>(tableName, adqlQuery);
        }  

        return null;
    }
    
    private void sendGAEventWithCurrentTab (String eventCategory, String extra) {
        final int currentTab = view.getTabPanel().getSelectedTabIndex();
        final Widget tab = view.getTabPanel().getWidget(currentTab);
        if (tab instanceof AbstractTablePanel) {
            GoogleAnalytics.sendEventWithURL(eventCategory, ((AbstractTablePanel)tab).getFullId(), extra);
        }
    }

    /**
     * updateNumberOfObservationsSelected().
     * @param saveAllView Input SaveAllView
     */
    private void updateNumberOfObservationsSelected(final SaveAllView saveAllView) {
        Log.debug("[ResultPresenter][updateNumberOfObservationsSelected] AAAA1 ");
        // Get Current Tab.
        int currentTab = view.getTabPanel().getSelectedTabIndex();
        AbstractTablePanel tab = view.getTabPanel().getWidget(currentTab);

        Set<TableRow> subset = tab.getSelectedRows();
        Log.debug("[ResultPresenter][updateNumberOfObservationsSelected]NUMBER OF SELECTED OBS "
                + subset.size());
        saveAllView.updateNumberOfSelectedElementsLabel(subset.size());
    }

    /**
     * doSaveTableAs().
     * @param votable Input ReturnType
     */
    private void doSaveTableAs(final ReturnType type) {
        Log.debug("[ResultsPresenter/doSaveTableAs()] Saving results table in format: "
                + type.toString());

        Map.Entry<String, String> tempEntry = getResultsTableURLPerMission();
        final String eventCategory = (type.equals(ReturnType.CSV)) ? GoogleAnalytics.CAT_Download_CSV : GoogleAnalytics.CAT_Download_VOT;

        if (tempEntry != null) {

            final String tableName = tempEntry.getKey();
            final String adqlQuery = tempEntry.getValue();
            Log.debug(adqlQuery);
            
            final int currentTab = view.getTabPanel().getSelectedTabIndex();
            final Widget tab = view.getTabPanel().getWidget(currentTab);
            if (tab instanceof ExtTapTablePanel) {
                Log.debug("[ResultsPresenter/doSaveTableAs()] Saving " + tableName + " in format: " + type.toString());
                if (type.equals(ReturnType.CSV)) {
                	((ExtTapTablePanel) tab).exportAsCSV();
                    return;
                    
                } else if (type.equals(ReturnType.VOTABLE)) {
                	((ExtTapTablePanel) tab).exportAsVOTABLE();
                    return;
                }
            }
            
            if (adqlQuery.equals(EntityContext.PUBLICATIONS.toString())) {
                
                Log.debug("[ResultsPresenter/doSaveTableAs()] Saving " + tableName + " in format: " + type.toString());
                if (type.equals(ReturnType.CSV)) {
                    view.getTabPanel().getAbstractTablePanelFromId(tableName).exportAsCSV();
                    
                } else if (type.equals(ReturnType.VOTABLE)) {
                    view.getTabPanel().getAbstractTablePanelFromId(tableName).exportAsVOTABLE();
                }
                
            } else {
	        	if(Modules.improvedDownload){
	                final String tableUrl = TAPUtils.getTAPQuery("", type.toString());
	                Log.debug("[ResultsPresenter/getResultsTableURLPerMission()] Getting results table: ["
	                        + tableUrl + "] for tab: [" + tableName + "]");
	                RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST, tableUrl);
	                requestBuilder.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=ISO-8859-1");
	                
	                final String indicatorId = UUID.randomUUID().toString();
	                CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPushEvent(indicatorId, "Downloading " + type.toString() + " file"));
	                try {
	                    requestBuilder.sendRequest("query=" + URL.encodeQueryString(adqlQuery), new RequestCallback() {

	                        @Override
	                        public void onError(final com.google.gwt.http.client.Request request,
	                                final Throwable exception) {
	                        	CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPopEvent(indicatorId));
	                        	GoogleAnalytics.sendEvent(eventCategory, GoogleAnalytics.ACT_Tab_Download_Failure, tableUrl + URL.encodeQueryString(adqlQuery));
	                            Log.debug("Failed",exception);
	                        }
	
	                        @Override
	                        public void onResponseReceived(
	                                final com.google.gwt.http.client.Request request,
	                                final Response response) {
	                        	Log.debug("[Download success]");
	                    		DownloadUtils.downloadFile(DownloadUtils.getValidFilename(tableName) + "." + type.toString(), response.getText(), type.getMimeType());
	                    		CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPopEvent(indicatorId));
	                        }
	
	                    });
	                } catch (RequestException e) {
	                	CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPopEvent(indicatorId));
	                	GoogleAnalytics.sendEvent(eventCategory, GoogleAnalytics.ACT_Tab_Download_Failure, tableUrl + URL.encodeQueryString(adqlQuery));
	                    Log.debug("Failed to get file",e);
	                }
	            } else {
	            	String tableUrl = TAPUtils.getTAPQuery(URL.encodeQueryString(adqlQuery), type.toString());
	            	Window.open(tableUrl, "_self", "location=0,status=0,toolbar=0,scrollbars=1,menubar=0");
	            }
            }
            // Send download event
            sendGAEventWithCurrentTab(eventCategory, "");
        }
    }
    
    @Override
    public String getProgressIndicatorMessage() {
        return TextMgr.getInstance().getText("CountRequestCallback_countingAvailableData");
    }

    // ------------------------
    // ----- SSO
    // ------------------------
    
    public void getTableSSOMetadata(final GeneralEntityInterface entity) {

        final String debugPrefix = "[getTableSSOMetadata]["
                + entity.getDescriptor().getGuiShortName() + "]";
        Log.debug(debugPrefix);
        
        AbstractTablePanel panel = this.view.addResultsTab(entity, entity.getDescriptor().getGuiLongName(), 
        		TextMgr.getInstance().getText("resultsPresenter_helpDescription_" + entity.getContext() + "_" + entity.getDescriptor().getMission()));

        entity.fetchData(panel);
    }

    @Override
    public String getSSOProgressIndicatorMessage(String ssoName, ESASkySSOObjType ssoType) {
        return TextMgr.getInstance().getText("resultsPresenter_computingDataCrossMatch").replace("$SSOTEXT$", ssoType.getType() + " " + ssoName);
    }

    @Override
    public void showObjectNotAvailableInEsaSkyMsg(String progressIndicatorId) {
    	String missions = "";
    	for(IDescriptor descriptor : descriptorRepo.getSsoDescriptors().getDescriptors()) {
    		missions += descriptor.getGuiLongName() + ", ";
    	}
        DisplayUtils
        .showMessageDialogBox(TextMgr.getInstance().getText("SsoCountRequestCallback_noCrossMatchResultsMessage").replace("$MISSIONS$", missions.subSequence(0, missions.length() - 2)),
                TextMgr.getInstance().getText("SsoCountRequestCallback_noCrossMatchResultsTitle"),
                progressIndicatorId);
    }
    
    
    // ------------------------
    // ----- PUBLICATIONS
    // ------------------------
    
    
    public void showPublications(String id, final PublicationsBySourceEntity entity, boolean byAuthor, IPreviewClickedHandler previewClickedHandler) {
        final String debugPrefix = "[showPublications][" + id + "]";
        Log.debug(debugPrefix);

        AbstractTablePanel panel;
        if(byAuthor) {
        	panel = this.view.addResultsTab(entity,
        			TextMgr.getInstance().getText("PublicationsByAuthorCallback_helpTitle"),
        			TextMgr.getInstance().getText("PublicationsByAuthorCallback_helpDescription"));
        	
        } else {
        	panel = this.view.addResultsTab(entity,
        			TextMgr.getInstance().getText("PublicationsBySourceCallback_helpTitle"),
        			TextMgr.getInstance().getText("PublicationsBySourceCallback_helpDescription"));
        }
        panel.setPreviewClickedHandler(previewClickedHandler);
        
        //SOME EXAMPLE QUERIES:
        // DECODED: publications-by-source?SOURCE=UGC 12163&ROWS=10&ORDERBY=title asc&WHERE=title like '%high%' AND a.name like '%FAB%'
        // ENCODED: publications-by-source?SOURCE=UGC%2012163&ROWS=10&ORDERBY=title%20asc&WHERE=title%20like%20%27%25high%25%27%20AND%20a.name%20like%20%27%25FAB%25%27
        
        // DECODED: publications-by-author?AUTHOR=MERIN B.&ROWS=10&ORDERBY=title desc&WHERE=title like '%ESASky%'
        // ENCODED: publications-by-author?AUTHOR=MERIN%20B.&ROWS=10&ORDERBY=title%20asc&WHERE=title%20like%20%27%25ESASky%25%27
        
        //WHERE FIELDS: bibcode, title, name, pub, pubdate
        //SORT FIELDS: bibcode, title, name, pub, pubdate -> default pubdate desc
        //WHERE PARAM: &WHERE=whereClause
        //SORT PARAM: &ORDERBY=orderClause
        //WHERE and ORDERBY params are optional, don not pass them if not required
        
        String url;
        if (!byAuthor) {
            url = EsaSkyWebConstants.PUBLICATIONS_BY_SOURCE_URL + "?SOURCE=" + URL.encodeQueryString(id) + "&ROWS=" + entity.getDescriptor().getAdsPublicationsMaxRows();
        } else {
            url = EsaSkyWebConstants.PUBLICATIONS_BY_AUTHOR_URL + "?AUTHOR=" + URL.encodeQueryString(id) + "&ROWS=" + entity.getDescriptor().getAdsPublicationsMaxRows();
        }
        Log.debug(debugPrefix + "Query [" + url + "]");
        
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
        try {
            builder.sendRequest(null, new PublicationsBySourceCallback(entity, panel,
                    TextMgr.getInstance().getText("PublicationsBySourceCallback_retrievingPubsOf").replace("$NAME$", id), url));

        } catch (RequestException e) {
            Log.error(e.getMessage());
            Log.error(debugPrefix + "Error fetching JSON data from server");
        }
        
    }
    
    public void removeTab(String id) {
    	    view.removeTab(id);
    }
}