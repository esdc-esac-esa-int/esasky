package esac.archive.esasky.cl.web.client.presenter;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteShapeHoverStopEvent;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteShapeHoverStopEventHandler;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteShapeSelectedEvent;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteShapeSelectedEventHandler;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.Shape;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.Source;
import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesFrame;
import esac.archive.esasky.ifcs.model.descriptor.CatalogDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.CommonObservationDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.PublicationsDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.SSODescriptor;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.Modules;
import esac.archive.esasky.cl.web.client.api.model.FootprintListJSONWrapper;
import esac.archive.esasky.cl.web.client.api.model.IJSONWrapper;
import esac.archive.esasky.cl.web.client.api.model.SourceListJSONWrapper;
import esac.archive.esasky.cl.web.client.event.ESASkySampEvent;
import esac.archive.esasky.cl.web.client.event.ESASkySampEventHandlerImpl;
import esac.archive.esasky.cl.web.client.event.PublicationsClickEvent;
import esac.archive.esasky.cl.web.client.event.PublicationsClickEventHandler;
import esac.archive.esasky.cl.web.client.event.TreeMapSelectionEvent;
import esac.archive.esasky.cl.web.client.event.TreeMapSelectionEventHandler;
import esac.archive.esasky.cl.web.client.event.UrlChangedEvent;
import esac.archive.esasky.cl.web.client.event.UrlChangedEventHandler;
import esac.archive.esasky.cl.web.client.model.entities.EntityContext;
import esac.archive.esasky.cl.web.client.model.entities.GeneralEntityInterface;
import esac.archive.esasky.cl.web.client.model.entities.PublicationsBySourceEntity;
import esac.archive.esasky.cl.web.client.repository.DescriptorRepository;
import esac.archive.esasky.cl.web.client.repository.EntityRepository;
import esac.archive.esasky.cl.web.client.status.CountObserver;
import esac.archive.esasky.cl.web.client.status.GUISessionStatus;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.utility.SourceConstant;
import esac.archive.esasky.cl.web.client.utility.UrlUtils;
import esac.archive.esasky.cl.web.client.view.allskypanel.AllSkyPanel;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.CtrlToolBar;
import esac.archive.esasky.cl.web.client.view.resultspanel.AbstractTablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.ResultsPanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.AbstractTablePanel.IPreviewClickedHandler;
import esac.archive.esasky.cl.web.client.view.searchpanel.SearchPanel;

/**
 * @author ESDC team Copyright (c) 2015- European Space Agency
 */
public class MainPresenter {

    private AllSkyPresenter allSkyPresenter;
    private ResultsPresenter resultsPresenter;
    private CtrlToolBarPresenter ctrlTBPresenter;
    private SearchPresenter targetPresenter;

    private DescriptorRepository descriptorRepo;
    private EntityRepository entityRepo;
    
    private View view;
    
    public interface View {

        Widget asWidget();

        AllSkyPanel getAllSkyPanel();

        CtrlToolBar getCtrlToolBar();
        
        ResultsPanel getResultsPanel();

        SearchPanel getSearchPanel();
        
        HeaderPresenter.View getHeaderPanel();
        
        BannerPresenter.View getBannerPanel();
        BannerPresenter.View getBannerPanelLeftSide();
        BannerPresenter.View getBannerPanelRightSide();
        BannerPresenter.View getBannerPanelBottom();
    }

    public MainPresenter(final View inputView, String coordinateFrameFromUrl, boolean isInitialPositionDescribedInCoordinates) {
        this.view = inputView;
        
        // Creates the descriptors repository
        descriptorRepo = new DescriptorRepository(isInitialPositionDescribedInCoordinates);
        entityRepo = new EntityRepository(descriptorRepo);
        
        initChildPresenters(coordinateFrameFromUrl);

        descriptorRepo.setCountRequestHandler(resultsPresenter);
        
        // Retrieve available observations entries
        getObservationsList();
        
        getSsoList();

        // Retrieve available catalogs entries
        getCatalogsList();

        if (Modules.spectraModule) {
            getSpectraList();
        }

        if (Modules.publicationsModule) {
            getPublicationsList();
        }

        bindSampRequests();
        bind();
    }

    /**
     * initChildPresenters(). Only the Presenters needed at start up are initialized here.
     */
    private void initChildPresenters(String coordinateFrameFromUrl) {
        
        this.allSkyPresenter = getAllSkyPresenter();
        this.ctrlTBPresenter = getCtrlTBPresenter();
        this.resultsPresenter = getResultsPresenter();
        new HeaderPresenter(view.getHeaderPanel(), coordinateFrameFromUrl);
        new BannerPresenter(view.getBannerPanel());
        if(Modules.bannersOnAllSides) {
	        new BannerPresenter(view.getBannerPanelLeftSide());
	        new BannerPresenter(view.getBannerPanelRightSide());
	        new BannerPresenter(view.getBannerPanelBottom());
        }
    }

    public final void go(final HasWidgets root) {
        
        root.add(this.view.asWidget());
        GUISessionStatus.setDataPanelOpen(false);
        
        // init the targetPresenter
        getTargetPresenter();
        AladinLiteWrapper.getAladinLite().enableReduceDeformations();
        
        //If url has bibcode or author parammeter
        if (Modules.publicationsModule) {
            if(UrlUtils.urlHasBibcode() || UrlUtils.urlHasAuthor()){
               
                //Uses a delayed call to ensures that environment is just created and ready
                //TODO timer does not ensure that environment is ready
                Timer timer = new Timer() {
                    public void run () {
                        
                        if (descriptorRepo.getPublicationsDescriptors() != null 
                                && descriptorRepo.getPublicationsDescriptors().getDescriptors().size() > 0) {
                            final IDescriptor descriptor = descriptorRepo.getPublicationsDescriptors().getDescriptors().get(0);
                            
                            if (UrlUtils.urlHasBibcode()) {
                                final String bibcode = Window.Location.getParameterMap().get(EsaSkyWebConstants.PUBLICATIONS_BIBCODE_URL_PARAM).get(0);
                                getCtrlTBPresenter().showPublicationInfo(bibcode,
                                                                        descriptor.getArchiveURL(),
                                                                        descriptor.getArchiveProductURI(),
                                                                        descriptor.getAdsAuthorSeparator(),
                                                                        descriptor.getAdsAuthorUrl(),
                                                                        descriptor.getAdsAuthorUrlReplace());
                                
                                GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_API, GoogleAnalytics.ACT_API_BibcodeInURL, bibcode);
                                
                            } else if (UrlUtils.urlHasAuthor()) {
                                final String author = Window.Location.getParameterMap().get(EsaSkyWebConstants.PUBLICATIONS_AUTHOR_URL_PARAM).get(0);
                                getCtrlTBPresenter().showAuthorInfo(author,
                                                                    descriptor.getAdsAuthorSeparator(),
                                                                    descriptor.getAdsAuthorUrl(),
                                                                    descriptor.getAdsAuthorUrlReplace());
                                showPublicationsTabPanel(author, true);
                                
                                GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_API, GoogleAnalytics.ACT_API_AuthorInURL, author);
                            }
                            
                        } else {
                            Log.error("[MainPresenter] Can't show soruces from bibcode, publicationsDescriptor is not ready!");
                        }
                    }
                  };
    
                timer.schedule(1500);
            }
        }
    }

    public final void bind() {

        CommonEventBus.getEventBus().addHandler(AladinLiteShapeHoverStopEvent.TYPE,
                new AladinLiteShapeHoverStopEventHandler() {

            @Override
            public void onShapeHoverStopEvent(AladinLiteShapeHoverStopEvent hoverEvent) {
                
                if (Modules.publicationsModule && hoverEvent.getOverlayName().startsWith(descriptorRepo.getPublicationsDescriptors()
                        .getDescriptors().get(0).getMission())) {
                    // Is a publication shape
                    MainPresenter.this.getAllSkyPresenter().hideTooltip();
                }
            }
        });
        
        CommonEventBus.getEventBus().addHandler(AladinLiteShapeSelectedEvent.TYPE,
                new AladinLiteShapeSelectedEventHandler() {

            @Override
            public void onShapeSelectionEvent(AladinLiteShapeSelectedEvent selectEvent) {

                if (Modules.publicationsModule && selectEvent.getOverlayName().equals(EntityContext.PUBLICATIONS.toString())) {
                    
                    // Is a publication shape
                    Shape shapeJs = selectEvent.getShapeobj();
                    if (shapeJs instanceof Source) {
                        final Source sourceJs = (Source) shapeJs;
                        final String sourceName = sourceJs.getDataDetailsByKey(SourceConstant.SOURCE_NAME);
                        showPublicationsTabPanel(sourceName, false);
                    }
                    
                } else {

                    // Selects a table row
                    AbstractTablePanel tableContainingShape = resultsPresenter.getTabPanel().getAbstractTablePanelFromId(selectEvent.getOverlayName());
                    
                    if (tableContainingShape != null) {
                        
                        AbstractTablePanel selectedTabPanel = resultsPresenter.getTabPanel().getSelectedWidget();
                        if (selectedTabPanel == null 
                                || !selectedTabPanel.getEsaSkyUniqID().equals(tableContainingShape.getEsaSkyUniqID())) {
                            resultsPresenter.getTabPanel().selectTab(tableContainingShape);
                        }
                        
                        tableContainingShape.selectRow(selectEvent.getShapeId());
                    }
                }
            }
        });


         CommonEventBus.getEventBus().addHandler(TreeMapSelectionEvent.TYPE,
                 new TreeMapSelectionEventHandler() {

             @Override
             public void onSelection(TreeMapSelectionEvent event) {
                 getRelatedMetadata(event.getDescriptor(), event.getContext());
             }
         });
         
         /*
          * When the user clicks on publications CtrlToolBar button
          */
         CommonEventBus.getEventBus().addHandler(PublicationsClickEvent.TYPE,
                 new PublicationsClickEventHandler() {

             @Override
             public void onClick(PublicationsClickEvent event) {
                 if (Modules.publicationsModule) {
                     if (GUISessionStatus.getIsPublicationsActive()) {
                         getRelatedMetadata(descriptorRepo.getPublicationsDescriptors().getDescriptors().get(0),
                                 EntityContext.PUBLICATIONS);
                     } else {
                         cleanPublicationSources();
                     }
                 }
             }
         });
         
         /*
          * When the url changed because the state has changed
          */
         CommonEventBus.getEventBus().addHandler(UrlChangedEvent.TYPE,
                 new UrlChangedEventHandler() {

             @Override
             public void onUrlChanged(UrlChangedEvent event) {
                 //Updates the url in the browser address bar
                 UrlUtils.updateURLWithoutReloadingJS(UrlUtils.getUrlForCurrentState());
             }
         });
    }
    
    public DescriptorRepository getDescriptorRepository(){
    	return descriptorRepo;
    }
    
    private final void showPublicationsTabPanel (String id, boolean byAuthor) {
        
    	entityRepo.getPublications().deselectAllShapes();
        //Creates a new TablePanel or selects the existing one
        final AbstractTablePanel tabPanel = resultsPresenter.getTabPanel().getAbstractTablePanelFromId(id);
        if (tabPanel == null) {
            Log.debug("[MainPresenter][showPublicationsTabPanel] id: " + id);
            PublicationsBySourceEntity pubBySourceEntity = entityRepo.createPublicationsBySourceEntity(id, byAuthor);
            
            if (pubBySourceEntity.getSourceMetadata() != null) {
                //Draws the source for this publication list, only valid if not by author
                pubBySourceEntity.addShapes(pubBySourceEntity.getSourceMetadata());
            }
            
            resultsPresenter.showPublications(id, pubBySourceEntity, byAuthor, new IPreviewClickedHandler() {
                @Override
                public void onPreviewClicked(String id) {
                    final PublicationsDescriptor descriptor = descriptorRepo.getPublicationsDescriptors().getDescriptors().get(0);   
                    getCtrlTBPresenter().showPublicationInfo(id,
                            descriptor.getArchiveURL(),
                            descriptor.getArchiveProductURI(),
                            descriptor.getAdsAuthorSeparator(),
                            descriptor.getAdsAuthorUrl(),
                            descriptor.getAdsAuthorUrlReplace());
                }
            });
        } else {
            resultsPresenter.getTabPanel().selectTab(tabPanel);
        }     
    }
    
    private final void cleanPublicationSources () {
        if (!Modules.publicationsModule) {
            return;
        }
        
        //Clears the catalog
        final JavaScriptObject catalog = AladinLiteWrapper.getInstance().getPublicationCatalogue();
        AladinLiteWrapper.getAladinLite().removeAllSourcesFromCatalog(catalog);
    }
    
    public void getRelatedMetadata(IDescriptor descriptor, EntityContext context) {
        switch (context) {
            case ASTRO_IMAGING:
            case ASTRO_SPECTRA:
                GeneralEntityInterface newEntity = entityRepo.createCommonObservationEntity((CommonObservationDescriptor) descriptor, context);
                if (newEntity != null) {
                    resultsPresenter.getMetadataAndFootprints(newEntity, true, null);
                }
                break;
                
            case ASTRO_CATALOGUE:
            	GeneralEntityInterface catEntity = entityRepo.createCatalogueEntity((CatalogDescriptor) descriptor, context);
                resultsPresenter.getMetadataAndFootprints(catEntity, true, null);
                break;
                
            case SSO:
                getSSOOrbitAndObservation((SSODescriptor)descriptor);
                break;
                
            case PUBLICATIONS:
                getPublicationSourcesPolygons((PublicationsDescriptor) descriptor);
                break;
                
            default:
                throw new IllegalArgumentException("Does not recognize entity context");
        }
    }
    
//    public void showUserRelatedMetadata(IDescriptor descriptor, EntityContext context, IJSONWrapper footprintsSet) {
	public void showUserRelatedMetadata(IDescriptor descriptor, IJSONWrapper userDataJSONWrapper, CoordinatesFrame convertToFrame) {
    	Log.debug("[MainPresenter][showUserRelatedMetadata]");
    	
    
    	GeneralEntityInterface entity = null;
    	if (userDataJSONWrapper instanceof SourceListJSONWrapper){
    		Log.debug("[MainPresenter][showUserRelatedMetadata] USER_SOURCES");
    		entity = entityRepo.createCatalogueEntity((CatalogDescriptor) descriptor, EntityContext.USER_CATALOGUE);
    	}else if (userDataJSONWrapper instanceof FootprintListJSONWrapper){
    		Log.debug("[MainPresenter][showUserRelatedMetadata] USER_IMAGING");
    		entity = entityRepo.createCommonObservationEntity((CommonObservationDescriptor) descriptor, EntityContext.USER_IMAGING);
    	}
    	if (entity != null){
    		resultsPresenter.getUserMetadataAndPolygons(entity, true, null, userDataJSONWrapper, convertToFrame);	
    	}
    	
//    	switch (context) {
//            case USER_CATALOGUE:
//            	break;
//            
//            case USER_SPECTRA:
//            case USER_IMAGING:
//            	Log.debug("[MainPresenter][showUserRelatedMetadata] USER_IMAGING");
//                resultsPresenter.getUserMetadataAndFootprints(entityRepo.createCommonObservationEntity((CommonObservationDescriptor) descriptor, EntityContext.USER_IMAGING), true, null, footprintsSet);
//                break;
//            	
//                
//            default:
//                throw new IllegalArgumentException("Does not recognize entity context");
//        }
    }

    public final boolean getPublicationSourcesPolygons(PublicationsDescriptor descriptor) {
        
        if (entityRepo.getPublications() == null) {            
            entityRepo.createPublicationsEntity(descriptor);   
        }
        
        resultsPresenter.getPublicationsSources(entityRepo.getPublications(), true);
        return true;
    }
    
    private void getObservationsList() {
        Log.debug("[MainPresenter] Into MainPresenter.getObservationsList");
        descriptorRepo.initObsDescriptors(new CountObserver() {

            @Override
            public void onCountUpdate(int newCount) {
                ctrlTBPresenter.updateObservationCount(newCount);
            }
        });
    }
    
    private void getSsoList() {
    	Log.debug("[MainPresenter] Into MainPresenter.getSsoList");
    	descriptorRepo.initSSODescriptors(new CountObserver() {
    		
    		@Override
    		public void onCountUpdate(int newCount) {
    			ctrlTBPresenter.updateSsoCount(newCount);
    		}
    	});
    }

    private void getSpectraList() {
        Log.debug("[MainPresenter] Into MainPresenter.getSpectrasList");
        descriptorRepo.initSpectraDescriptors(new CountObserver() {
            @Override
            public void onCountUpdate(int newCount) {
                ctrlTBPresenter.updateSpectraCount(newCount);
            }
        });
    }

    private void getCatalogsList() {
        Log.debug("[MainPresenter] Into MainPresenter.getCatalogsList");
        descriptorRepo.initCatDescriptors(new CountObserver() {

            @Override
            public void onCountUpdate(int newCount) {
                ctrlTBPresenter.updateCatalogCount(newCount);
            }
        });
    }

    private void getPublicationsList() {
        Log.debug("[MainPresenter] Into MainPresenter.getPublicationsList");
        descriptorRepo.initPubDescriptors(new CountObserver() {

            @Override
            public void onCountUpdate(int newCount) {
                if (descriptorRepo.getPublicationsDescriptors().getDescriptors().size() > 0) {
                    ctrlTBPresenter.updatePublicationsCount(newCount);
//                    if (GUISessionStatus.getIsPublicationsActive()
//                        && EsaSkyWebConstants.PUBLICATIONS_UPDATE_ON_FOV_CHANGE) {
//                        getRelatedMetadata(descriptorRepo.getPublicationsDescriptors().getDescriptors().get(0),
//                                EntityContext.PUBLICATIONS);
//                    }
                }
            }
        });
    }

    public final AllSkyPresenter getAllSkyPresenter() {
        if (allSkyPresenter == null) {
            allSkyPresenter = new AllSkyPresenter(this.view.getAllSkyPanel());
        }
        return allSkyPresenter;
    }

    public final ResultsPresenter getResultsPresenter() {
        if (resultsPresenter == null) {
            resultsPresenter = new ResultsPresenter(this.view.getResultsPanel(), descriptorRepo);
        }
        return resultsPresenter;
    }

    public final CtrlToolBarPresenter getCtrlTBPresenter() {
        if (ctrlTBPresenter == null) {
            ctrlTBPresenter = new CtrlToolBarPresenter(this.view.getCtrlToolBar());
        }
        return ctrlTBPresenter;
    }

    public final SearchPresenter getTargetPresenter() {
        if (targetPresenter == null) {
            targetPresenter = new SearchPresenter(this.view.getSearchPanel());
        }
        return targetPresenter;
    }

    private void bindSampRequests() {
        CommonEventBus.getEventBus().addHandler(ESASkySampEvent.TYPE,
                new ESASkySampEventHandlerImpl());
    }

    public final boolean getSSOOrbitAndObservation(SSODescriptor descriptor) {
        Log.debug("[MainPresenter.getSSOOrbitAndObservation][] AND NOW GET THE ORBIT 4 " + GUISessionStatus.getTrackedSso().name + " (internal oid: " + GUISessionStatus.getTrackedSso().id + ")");
        Log.debug("[MainPresenter.getSSOOrbitAndObservation][] AND THEN THE CROSSMATCH OBS WITH MISSION " + descriptor.getGuiShortName());

        GeneralEntityInterface entity = entityRepo.createSSOEntity(descriptor);
        entityRepo.addEntity(entity);
        resultsPresenter.getTableSSOMetadata(entity);
        return true;
    }
}