package esac.archive.esasky.cl.web.client.presenter;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteShapeDeselectedEvent;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteShapeDeselectedEventHandler;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteShapeHoverStartEvent;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteShapeHoverStartEventHandler;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteShapeHoverStopEvent;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteShapeHoverStopEventHandler;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteShapeSelectedEvent;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteShapeSelectedEventHandler;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesFrame;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;
import esac.archive.esasky.cl.wcstransform.module.utility.SiafDescriptor;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.Modules;
import esac.archive.esasky.cl.web.client.event.AddTableEvent;
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
import esac.archive.esasky.cl.web.client.repository.DescriptorRepository;
import esac.archive.esasky.cl.web.client.repository.DescriptorRepository.PublicationDescriptorLoadObserver;
import esac.archive.esasky.cl.web.client.repository.EntityRepository;
import esac.archive.esasky.cl.web.client.repository.MocRepository;
import esac.archive.esasky.cl.web.client.status.CountObserver;
import esac.archive.esasky.cl.web.client.status.GUISessionStatus;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.utility.UrlUtils;
import esac.archive.esasky.cl.web.client.view.allskypanel.AllSkyPanel;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.CtrlToolBar;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.treemap.PointInformation;
import esac.archive.esasky.cl.web.client.view.resultspanel.ResultsPanel;
import esac.archive.esasky.cl.web.client.view.searchpanel.SearchPanel;

/**
 * @author ESDC team Copyright (c) 2015- European Space Agency
 */
public class MainPresenter {

    private AllSkyPresenter allSkyPresenter;
    private CtrlToolBarPresenter ctrlTBPresenter;
    private ResultsPresenter resultsPresenter;
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
        descriptorRepo = DescriptorRepository.init(isInitialPositionDescribedInCoordinates);
        entityRepo = EntityRepository.init(descriptorRepo);
        MocRepository.init();
        
        initChildPresenters(coordinateFrameFromUrl);

        descriptorRepo.setCountRequestHandler(resultsPresenter);
        
        // Retrieve available observations entries
        getObservationsList();
        
        getSsoList();

        // Retrieve available catalogs entries
        getCatalogsList();
        
        getExtTapList();
        
        new SiafDescriptor(EsaSkyWebConstants.BACKEND_CONTEXT);

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
        this.headerPresenter = new HeaderPresenter(view.getHeaderPanel(), coordinateFrameFromUrl);
        new BannerPresenter(view.getBannerPanel());
        if (Modules.bannersOnAllSides) {
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
            if (UrlUtils.urlHasAuthor()) {
                loadOrQueueAuthorInformationFromSimbad(
                        Window.Location.getParameterMap().get(EsaSkyWebConstants.PUBLICATIONS_AUTHOR_URL_PARAM).get(0));
            } else if (UrlUtils.urlHasBibcode()) {
                loadOrQueueBibcodeTargetListFromSimbad(Window.Location.getParameterMap()
                        .get(EsaSkyWebConstants.PUBLICATIONS_BIBCODE_URL_PARAM).get(0));
            }
        }
    }

    public final void bind() {

        CommonEventBus.getEventBus().addHandler(AladinLiteShapeSelectedEvent.TYPE,
                new AladinLiteShapeSelectedEventHandler() {

                    @Override
                    public void onShapeSelectionEvent(AladinLiteShapeSelectedEvent selectEvent) {
                        entityRepo.getEntity(selectEvent.getOverlayName()).onShapeSelection(selectEvent.getShape());
                    }
                });

        CommonEventBus.getEventBus().addHandler(AladinLiteShapeDeselectedEvent.TYPE,
                new AladinLiteShapeDeselectedEventHandler() {

                    @Override
                    public void onShapeDeselectionEvent(AladinLiteShapeDeselectedEvent selectEvent) {
                        entityRepo.getEntity(selectEvent.getOverlayName()).onShapeDeselection(selectEvent.getShape());
                    }
                });

        CommonEventBus.getEventBus().addHandler(AladinLiteShapeHoverStartEvent.TYPE,
                new AladinLiteShapeHoverStartEventHandler() {

                    @Override
                    public void onShapeHoverStartEvent(AladinLiteShapeHoverStartEvent hoverEvent) {
                        entityRepo.getEntity(hoverEvent.getOverlayName()).onShapeHover(hoverEvent.getShape());

                    }
                });

        CommonEventBus.getEventBus().addHandler(AladinLiteShapeHoverStopEvent.TYPE,
                new AladinLiteShapeHoverStopEventHandler() {

                    @Override
                    public void onShapeHoverStopEvent(AladinLiteShapeHoverStopEvent hoverEvent) {
                        entityRepo.getEntity(hoverEvent.getOverlayName()).onShapeUnhover(hoverEvent.getShape());
                    }
                });

        CommonEventBus.getEventBus().addHandler(TreeMapSelectionEvent.TYPE, new TreeMapSelectionEventHandler() {

            @Override
            public void onSelection(TreeMapSelectionEvent event) {
                if (event.getContext() == EntityContext.EXT_TAP) {
                    PointInformation pointInformation = event.getPointInformation();

                    if (pointInformation.getType() == EsaSkyConstants.TREEMAP_TYPE_SUBCOLLECTION) {

                        getRelatedMetadata(event.getDescriptor());
                        GoogleAnalytics.sendEventWithURL(GoogleAnalytics.CAT_ExternalTaps,
                                GoogleAnalytics.ACT_ExtTap_gettingData, pointInformation.longName);

                    } else {

                        GoogleAnalytics.sendEventWithURL(GoogleAnalytics.CAT_ExternalTaps,
                                GoogleAnalytics.ACT_ExtTap_browsing, pointInformation.longName);
                    }

                } else {
                    getRelatedMetadata(event.getDescriptor());
                }
            }
        });

        /*
         * When the url changed because the state has changed
         */
        CommonEventBus.getEventBus().addHandler(UrlChangedEvent.TYPE, new UrlChangedEventHandler() {

            @Override
            public void onUrlChanged(UrlChangedEvent event) {
                // Updates the url in the browser address bar
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

    public DescriptorRepository getDescriptorRepository() {
        return descriptorRepo;
    }

    public EntityRepository getEntityRepository() {
        return entityRepo;
    }

    public void coneSearch(IDescriptor descriptor, SkyViewPosition conePos) {
        resultsPresenter.coneSearch(entityRepo.createEntity(descriptor), conePos);
    }

    public void getRelatedMetadata(IDescriptor descriptor) {
        resultsPresenter.getMetadata(entityRepo.createEntity(descriptor));
    }

    public void showUserRelatedMetadata(IDescriptor descriptor, GeneralJavaScriptObject userData) {
        Log.debug("[MainPresenter][showUserRelatedMetadata]");

        // To make sure that no tab with the same ID already exists in the layout panel
        while (resultsPresenter.getTabPanel().checkIfIdExists(descriptor.generateId())) {
        }
        descriptor.setTabCount(descriptor.getTabCount() - 1);

        GeneralEntityInterface entity = entityRepo.createEntity(descriptor);
        entity.setRefreshable(false);
        resultsPresenter.getUserMetadataAndPolygons(entity, userData);
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
        Log.debug("[MainPresenter] Into MainPresenter.getExtTapList");
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
            headerPresenter = new HeaderPresenter(this.view.getHeaderPanel(), CoordinatesFrame.J2000.toString());
        }
        return headerPresenter;
    }

    private void bindSampRequests() {
        CommonEventBus.getEventBus().addHandler(ESASkySampEvent.TYPE, new ESASkySampEventHandlerImpl());
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
            Log.debug(
                    "[MainPresenter] Can't show author information, publicationsDescriptor is not ready. Waiting for descriptor...");
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
            Log.debug(
                    "[MainPresenter] Can't show soruces from bibcode, publicationsDescriptor is not ready. Waiting for descriptor...");
        }
    }

    private void loadAuthorInformationFromSimbad(String author) {
        final IDescriptor descriptor = descriptorRepo.getPublicationsDescriptors().getDescriptors().get(0);

        getCtrlTBPresenter().showAuthorInfo(author, descriptor.getAdsAuthorSeparator(), descriptor.getAdsAuthorUrl(),
                descriptor.getAdsAuthorUrlReplace());
        CommonEventBus.getEventBus().fireEvent(new AddTableEvent(entityRepo.createPublicationsByAuthorEntity(author)));

        GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_API, GoogleAnalytics.ACT_API_AuthorInURL, author);
    }

    private void loadBibcodeInformaitonFromSimbad(String bibcode) {
        getCtrlTBPresenter().showPublicationInfo(bibcode);
        GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_API, GoogleAnalytics.ACT_API_BibcodeInURL, bibcode);
    }

}