package esac.archive.esasky.cl.web.client.presenter;

import com.allen_sauer.gwt.log.client.Log;
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
import esac.archive.esasky.ifcs.model.descriptor.ExtTapDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.PublicationsDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.SSODescriptor;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.Modules;
import esac.archive.esasky.cl.web.client.api.model.FootprintListJSONWrapper;
import esac.archive.esasky.cl.web.client.api.model.IJSONWrapper;
import esac.archive.esasky.cl.web.client.api.model.SourceListJSONWrapper;
import esac.archive.esasky.cl.web.client.event.AuthorSearchEvent;
import esac.archive.esasky.cl.web.client.event.AuthorSearchEventHandler;
import esac.archive.esasky.cl.web.client.event.BibcodeSearchEvent;
import esac.archive.esasky.cl.web.client.event.BibcodeSearchEventHandler;
import esac.archive.esasky.cl.web.client.event.ESASkySampEvent;
import esac.archive.esasky.cl.web.client.event.ESASkySampEventHandlerImpl;
import esac.archive.esasky.cl.web.client.event.TreeMapSelectionEvent;
import esac.archive.esasky.cl.web.client.event.TreeMapSelectionEventHandler;
import esac.archive.esasky.cl.web.client.event.UrlChangedEvent;
import esac.archive.esasky.cl.web.client.event.UrlChangedEventHandler;
import esac.archive.esasky.cl.web.client.model.entities.EntityContext;
import esac.archive.esasky.cl.web.client.model.entities.GeneralEntityInterface;
import esac.archive.esasky.cl.web.client.model.entities.PublicationsBySourceEntity;
import esac.archive.esasky.cl.web.client.repository.DescriptorRepository;
import esac.archive.esasky.cl.web.client.repository.DescriptorRepository.PublicationDescriptorLoadObserver;
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
    private HeaderPresenter headerPresenter;

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
        
        getExtTapList();

        if (Modules.spectraModule) {
            getSpectraList();
        }

        if (Modules.publicationsModule) {
        	descriptorRepo.initPubDescriptors();
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
        this.headerPresenter =  new HeaderPresenter(view.getHeaderPanel(), coordinateFrameFromUrl);
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
        
        if (Modules.publicationsModule) {
        	if(UrlUtils.urlHasAuthor()) {
        		loadOrQueueAuthorInformationFromSimbad(Window.Location.getParameterMap().get(EsaSkyWebConstants.PUBLICATIONS_AUTHOR_URL_PARAM).get(0));
        	} else if(UrlUtils.urlHasBibcode()){
            	loadOrQueueBibcodeTargetListFromSimbad(Window.Location.getParameterMap().get(EsaSkyWebConstants.PUBLICATIONS_BIBCODE_URL_PARAM).get(0));
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
         
         CommonEventBus.getEventBus().addHandler(AuthorSearchEvent.TYPE, new AuthorSearchEventHandler() {
			
			@Override
			public void onAuthorSelected(AuthorSearchEvent event) {
				loadOrQueueAuthorInformationFromSimbad(event.getAuthorName());
			}
		});
         
         CommonEventBus.getEventBus().addHandler(BibcodeSearchEvent.TYPE, new BibcodeSearchEventHandler() {
        	 
        	 @Override
        	 public void onBibcodeSelected(BibcodeSearchEvent event) {
        		 loadOrQueueBibcodeTargetListFromSimbad(event.getBibcode());
        	 }
         });
    }
    
    public DescriptorRepository getDescriptorRepository(){
    	return descriptorRepo;
    }
    
    public EntityRepository getEntityRepository(){
    	return entityRepo;
    }
    
    private final void showPublicationsTabPanel (String id, boolean byAuthor) {
        
    	if(entityRepo.getPublications() != null) {
    		entityRepo.getPublications().deselectAllShapes();
    	}
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
                
            case EXT_TAP:
                GeneralEntityInterface extEntity = entityRepo.createExtTapEntity((ExtTapDescriptor) descriptor, context);
                if (extEntity != null) {
                    resultsPresenter.getExtTapMetadata(extEntity, true, null);
                }
                break;
                
            default:
                throw new IllegalArgumentException("Does not recognize entity context");
        }
    }
    
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
    
    private void getExtTapList() {
        Log.debug("[MainPresenter] Into MainPresenter.getCatalogsList");
        descriptorRepo.initExtDescriptors(new CountObserver() {
            @Override
            public void onCountUpdate(int newCount) {
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
            ctrlTBPresenter = new CtrlToolBarPresenter(this.view.getCtrlToolBar(), descriptorRepo, entityRepo);
        }
        return ctrlTBPresenter;
    }

    public final SearchPresenter getTargetPresenter() {
        if (targetPresenter == null) {
            targetPresenter = new SearchPresenter(this.view.getSearchPanel());
        }
        return targetPresenter;
    }

    public final HeaderPresenter getHeaderPresenter() {
    	if (headerPresenter == null) {
    		headerPresenter = new HeaderPresenter(this.view.getHeaderPanel() , CoordinatesFrame.J2000.toString());
    	}
    	return headerPresenter;
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
    
    private void loadOrQueueAuthorInformationFromSimbad(final String author) {
 		if (descriptorRepo.getPublicationsDescriptors() != null 
 				&& descriptorRepo.getPublicationsDescriptors().getDescriptors().size() > 0) {
 			loadAuthorInformationFromSimbad(author);
 			
 		} else {
 			descriptorRepo.addPublicationDescriptorLoadObserver(new PublicationDescriptorLoadObserver() {
 				
 				@Override
 				public void onLoad() {
 					Log.debug("[MainPresenter] PublicationDescriptor ready, loading author informaiton");
 					loadAuthorInformationFromSimbad(author);
 				}
 			});
 			Log.debug("[MainPresenter] Can't show author information, publicationsDescriptor is not ready. Waiting for descriptor...");
 		}
     }
     
     private void loadOrQueueBibcodeTargetListFromSimbad(final String bibcode) {
     	if (descriptorRepo.getPublicationsDescriptors() != null 
     			&& descriptorRepo.getPublicationsDescriptors().getDescriptors().size() > 0) {
     		loadBibcodeInformaitonFromSimbad(bibcode);
     		
     	} else {
     		descriptorRepo.addPublicationDescriptorLoadObserver(new PublicationDescriptorLoadObserver() {
     			
     			@Override
     			public void onLoad() {
     				Log.debug("[MainPresenter] PublicationDescriptor ready, loading bibcode informaiton");
     				loadBibcodeInformaitonFromSimbad(bibcode);
     			}
     		});
     		Log.debug("[MainPresenter] Can't show soruces from bibcode, publicationsDescriptor is not ready. Waiting for descriptor...");
     	}
     }
     
     private void loadAuthorInformationFromSimbad(String author) {
     	final IDescriptor descriptor = descriptorRepo.getPublicationsDescriptors().getDescriptors().get(0);
 			
 		getCtrlTBPresenter().showAuthorInfo(author,
 				descriptor.getAdsAuthorSeparator(),
 				descriptor.getAdsAuthorUrl(),
 				descriptor.getAdsAuthorUrlReplace());
 		showPublicationsTabPanel(author, true);
 		
 		GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_API, GoogleAnalytics.ACT_API_AuthorInURL, author);
     }
     
     private void loadBibcodeInformaitonFromSimbad(String bibcode) {
     	final IDescriptor descriptor = descriptorRepo.getPublicationsDescriptors().getDescriptors().get(0);
     	getCtrlTBPresenter().showPublicationInfo(bibcode,
     			descriptor.getArchiveURL(),
     			descriptor.getArchiveProductURI(),
     			descriptor.getAdsAuthorSeparator(),
     			descriptor.getAdsAuthorUrl(),
     			descriptor.getAdsAuthorUrlReplace());
     	
     	GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_API, GoogleAnalytics.ACT_API_BibcodeInURL, bibcode);
     }
    
}