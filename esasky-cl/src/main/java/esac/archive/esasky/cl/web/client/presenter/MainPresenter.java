package esac.archive.esasky.cl.web.client.presenter;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.*;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.AladinShape;
import esac.archive.esasky.cl.wcstransform.module.utility.DS9Loader;
import esac.archive.esasky.cl.wcstransform.module.utility.SiafDescriptor;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.Modules;
import esac.archive.esasky.cl.web.client.callback.Promise;
import esac.archive.esasky.cl.web.client.event.*;
import esac.archive.esasky.cl.web.client.event.exttap.TapRegistrySelectEvent;
import esac.archive.esasky.cl.web.client.model.DescriptorCountAdapter;
import esac.archive.esasky.cl.web.client.model.entities.EntityContext;
import esac.archive.esasky.cl.web.client.model.entities.GeneralEntityInterface;
import esac.archive.esasky.cl.web.client.repository.DescriptorRepository;
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
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesFrame;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptorList;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;

import java.util.*;

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

    private static MainPresenter instance;

    public interface View {

        Widget asWidget();

        AllSkyPanel getAllSkyPanel();

        CtrlToolBar getCtrlToolBar();

        ResultsPanel getResultsPanel();

        SearchPanel getSearchPanel();

        void initEvaPanel();

        boolean isEvaShowing();

        void toggleEvaPanelWithDrag();

        HeaderPresenter.View getHeaderPanel();

        BannerPresenter.View getBannerPanel();

        BannerPresenter.View getBannerPanelLeftSide();

        BannerPresenter.View getBannerPanelRightSide();

        BannerPresenter.View getBannerPanelBottom();
    }

    public MainPresenter(final View inputView, String coordinateFrameFromUrl) {
        this.view = inputView;

        // Creates the descriptors repository
        descriptorRepo = DescriptorRepository.getInstance();

        entityRepo = EntityRepository.init(descriptorRepo);
        MocRepository.init();

        initChildPresenters(coordinateFrameFromUrl);

        descriptorRepo.setCountRequestHandler(resultsPresenter);

        // Wait for these categories to load before performing first count
        String[] requiredCategoryArr = {EsaSkyWebConstants.CATEGORY_OBSERVATIONS,
                EsaSkyWebConstants.CATEGORY_CATALOGUES,
                EsaSkyWebConstants.CATEGORY_SPECTRA,
                EsaSkyWebConstants.CATEGORY_SSO};

        fetchDescriptorList(EsaSkyWebConstants.SCHEMA_OBSERVATIONS, EsaSkyWebConstants.CATEGORY_OBSERVATIONS, requiredCategoryArr, newCount -> ctrlTBPresenter.updateObservationCount(newCount));
        fetchDescriptorList(EsaSkyWebConstants.SCHEMA_CATALOGUES, EsaSkyWebConstants.CATEGORY_CATALOGUES, requiredCategoryArr, newCount -> ctrlTBPresenter.updateCatalogCount(newCount));
        fetchDescriptorList(Arrays.asList(EsaSkyWebConstants.SCHEMA_OBSERVATIONS, EsaSkyWebConstants.SCHEMA_CATALOGUES), EsaSkyWebConstants.CATEGORY_SPECTRA, requiredCategoryArr, newCount -> ctrlTBPresenter.updateSpectraCount(newCount));
        fetchDescriptorList(EsaSkyWebConstants.SCHEMA_OBSERVATIONS, EsaSkyWebConstants.CATEGORY_SSO, requiredCategoryArr, newCount -> ctrlTBPresenter.updateSsoCount(newCount));
        fetchDescriptorList(EsaSkyWebConstants.SCHEMA_ALERTS, EsaSkyWebConstants.CATEGORY_GRAVITATIONAL_WAVES, requiredCategoryArr);
        fetchDescriptorList(EsaSkyWebConstants.SCHEMA_ALERTS, EsaSkyWebConstants.CATEGORY_NEUTRINOS, requiredCategoryArr);
        fetchDescriptorList(EsaSkyWebConstants.SCHEMA_PUBLIC, EsaSkyWebConstants.CATEGORY_PUBLICATIONS, requiredCategoryArr);
        fetchDescriptorList(EsaSkyWebConstants.SCHEMA_IMAGES, EsaSkyWebConstants.CATEGORY_IMAGES, requiredCategoryArr);
        fetchDescriptorList(EsaSkyWebConstants.SCHEMA_EXTERNAL, EsaSkyWebConstants.CATEGORY_EXTERNAL, requiredCategoryArr);
        new SiafDescriptor(EsaSkyWebConstants.BACKEND_CONTEXT);
        new DS9Loader(EsaSkyWebConstants.BACKEND_CONTEXT);


        bindSampRequests();
        bind();

        addShiftListener(this);

        instance = this;
    }

    public static MainPresenter getInstance() {
        return instance;
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
        if (Modules.getModule(EsaSkyWebConstants.MODULE_BANNERS_ALL_SIDE)) {
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

        if (Modules.getModule(EsaSkyWebConstants.MODULE_PUBLICATIONS)) {
            if (UrlUtils.urlHasAuthor()) {
                loadOrQueueAuthorInformationFromSimbad(
                        Window.Location.getParameterMap().get(EsaSkyWebConstants.PUBLICATIONS_AUTHOR_URL_PARAM).get(0));
            } else if (UrlUtils.urlHasBibcode()) {
                loadOrQueueBibcodeTargetListFromSimbad(Window.Location.getParameterMap()
                        .get(EsaSkyWebConstants.PUBLICATIONS_BIBCODE_URL_PARAM).get(0));
            }
        }

        if (Modules.getModule(EsaSkyWebConstants.MODULE_EVA)) {
            CommonEventBus.getEventBus().fireEvent(new ShowEvaEvent());
        }
    }

    public final void bind() {

        CommonEventBus.getEventBus().addHandler(AladinLiteShapeSelectedEvent.TYPE,
                selectEvent -> {
                    GeneralEntityInterface entity = entityRepo.getEntity(selectEvent.getOverlayName());
                    if (entity != null) {
                        entity.onShapeSelection(selectEvent.getShape());
                    }
                });

        CommonEventBus.getEventBus().addHandler(AladinLiteSelectAreaEvent.TYPE,
                selectEvent -> {
                    GeneralJavaScriptObject[] shapes = GeneralJavaScriptObject.convertToArray((GeneralJavaScriptObject) selectEvent.getObjects());
                    HashMap<String, LinkedList<AladinShape>> shapesToadd = new HashMap<String, LinkedList<AladinShape>>();
                    for (GeneralJavaScriptObject shape : shapes) {
                        String overlayName = null;
                        if (shape.hasProperty("overlay")) {
                            overlayName = shape.getProperty("overlay").getStringProperty("name");
                        } else if (shape.hasProperty("catalog")) {
                            overlayName = shape.getProperty("catalog").getStringProperty("name");
                        }

                        if (!shapesToadd.containsKey(overlayName)) {
                            shapesToadd.put(overlayName, new LinkedList<AladinShape>());
                        }

                        shapesToadd.get(overlayName).add((AladinShape) (JavaScriptObject) shape);
                    }

                    for (String overlayName : shapesToadd.keySet()) {

                        GeneralEntityInterface entity = entityRepo.getEntity(overlayName);

                        if (entity != null) {
                            entity.onMultipleShapesSelection(shapesToadd.get(overlayName));
                        }
                    }


                    areaSelectionFinished();
                });

        CommonEventBus.getEventBus().addHandler(AladinLiteDeselectAreaEvent.TYPE,
                deselectEvent -> {
                    GeneralJavaScriptObject[] shapes = GeneralJavaScriptObject.convertToArray((GeneralJavaScriptObject) deselectEvent.getObjects());
                    HashMap<String, LinkedList<AladinShape>> shapesToRemove = new HashMap<String, LinkedList<AladinShape>>();
                    for (GeneralJavaScriptObject shape : shapes) {
                        String overlayName = null;
                        if (shape.hasProperty("overlay")) {
                            overlayName = shape.getProperty("overlay").getStringProperty("name");
                        } else if (shape.hasProperty("catalog")) {
                            overlayName = shape.getProperty("catalog").getStringProperty("name");
                        }

                        if (!shapesToRemove.containsKey(overlayName)) {
                            shapesToRemove.put(overlayName, new LinkedList<AladinShape>());
                        }

                        shapesToRemove.get(overlayName).add((AladinShape) (JavaScriptObject) shape);
                    }

                    for (String overlayName : shapesToRemove.keySet()) {

                        GeneralEntityInterface entity = entityRepo.getEntity(overlayName);

                        if (entity != null) {
                            entity.onMultipleShapesDeselection(shapesToRemove.get(overlayName));
                        }
                    }
                    areaSelectionFinished();
                });

        CommonEventBus.getEventBus().addHandler(AladinLiteSelectSearchAreaEvent.TYPE, (searchAreaEvent) -> {
            if (searchAreaEvent != null && searchAreaEvent.getSearchArea() != null) {
                DescriptorRepository.getInstance().updateSearchArea(searchAreaEvent.getSearchArea());
            }

        });

        CommonEventBus.getEventBus().addHandler(AladinLiteClearSearchAreaEvent.TYPE, () -> {
            DescriptorRepository.getInstance().updateSearchArea(null);
        });

        CommonEventBus.getEventBus().addHandler(AladinLiteShapeDeselectedEvent.TYPE,
                selectEvent -> {
                    GeneralEntityInterface entity = entityRepo.getEntity(selectEvent.getOverlayName());
                    if (entity != null) {
                        entity.onShapeDeselection(selectEvent.getShape());
                    }
                });

        CommonEventBus.getEventBus().addHandler(AladinLiteShapeHoverStartEvent.TYPE,
                hoverEvent -> {
                    GeneralEntityInterface entity = entityRepo.getEntity(hoverEvent.getOverlayName());
                    if (entity != null) {
                        entity.onShapeHover(hoverEvent.getShape());
                    }
                });

        CommonEventBus.getEventBus().addHandler(AladinLiteShapeHoverStopEvent.TYPE,
                hoverEvent -> {
                    GeneralEntityInterface entity = entityRepo.getEntity(hoverEvent.getOverlayName());
                    if (entity != null) {
                        entity.onShapeUnhover(hoverEvent.getShape());
                    }
                });

        CommonEventBus.getEventBus().addHandler(TreeMapSelectionEvent.TYPE, event -> {
            if (event.getContext() == EntityContext.EXT_TAP) {
                PointInformation pointInformation = event.getPointInformation();
                if (EsaSkyConstants.TREEMAP_LEVEL_2 == pointInformation.getTreemapLevel()
                        || (Objects.equals(pointInformation.descriptor.getMission(), EsaSkyWebConstants.HEASARC_MISSION)
                        && EsaSkyConstants.TREEMAP_LEVEL_1 == pointInformation.getTreemapLevel())) {

                    getRelatedMetadata(event.getDescriptor());
                    GoogleAnalytics.sendEventWithURL(GoogleAnalytics.CAT_EXTERNALTAPS,
                            GoogleAnalytics.ACT_EXTTAP_GETTINGDATA, pointInformation.longName);

                } else {

                    GoogleAnalytics.sendEventWithURL(GoogleAnalytics.CAT_EXTERNALTAPS,
                            GoogleAnalytics.ACT_EXTTAP_BROWSING, pointInformation.longName);
                }

            } else if (event.getContext() == EntityContext.USER_TREEMAP) {
                ctrlTBPresenter.customTreeMapClicked(event);
            } else {
                getRelatedMetadata(event.getDescriptor());
            }
        });

        CommonEventBus.getEventBus().addHandler(TapRegistrySelectEvent.TYPE, event -> {
            if (event.getDescriptor() != null) {
                if (event.hasData()) {
                    insertRelatedMetadata(event.getDescriptor(), event.getData());
                } else {
                    getRelatedMetadata(event.getDescriptor());
                }
            }
        });

        /*
         * When the url changed because the state has changed
         */
        CommonEventBus.getEventBus().addHandler(UrlChangedEvent.TYPE, event -> {
            // Updates the url in the browser address bar
            UrlUtils.updateURLWithoutReloadingJS(UrlUtils.getUrlForCurrentState());
        });

        CommonEventBus.getEventBus().addHandler(AuthorSearchEvent.TYPE, event -> loadOrQueueAuthorInformationFromSimbad(event.getAuthorName()));

        CommonEventBus.getEventBus().addHandler(BibcodeSearchEvent.TYPE, event -> loadOrQueueBibcodeTargetListFromSimbad(event.getBibcode()));

        CommonEventBus.getEventBus().addHandler(ModuleUpdatedEvent.TYPE, event -> {
            if (EsaSkyWebConstants.MODULE_SCIENCE_MODE.equals(event.getKey())) {
                GUISessionStatus.onScienceModeChanged(event.getValue());
                updateModuleVisibility();
            }
        });

        CommonEventBus.getEventBus().addHandler(ShowEvaEvent.TYPE, event -> {
            showEva();
        });
    }

    private void areaSelectionFinished() {
        if (isShiftPressed()) {
            AladinLiteWrapper.getAladinLite().startSelectionMode();
        } else {
            allSkyPresenter.areaSelectionFinished();
        }
    }

    public DescriptorRepository getDescriptorRepository() {
        return descriptorRepo;
    }


    public void coneSearch(CommonTapDescriptor descriptor, SkyViewPosition conePos) {
        resultsPresenter.coneSearch(entityRepo.createEntity(descriptor), conePos);
    }

    public void insertRelatedMetadata(CommonTapDescriptor descriptor, GeneralJavaScriptObject data) {
        resultsPresenter.getMetadata(entityRepo.createEntity(descriptor), data);
    }

    public void getImagesMetadata(CommonTapDescriptor descriptor) {
    	resultsPresenter.getMetadata(entityRepo.createImageListEntity(descriptor));
    }

    public void getRelatedMetadata(CommonTapDescriptor descriptor) {
        resultsPresenter.getMetadata(entityRepo.createEntity(descriptor));
    }

    public void getRelatedMetadataWithoutMOC(CommonTapDescriptor descriptor) {
        resultsPresenter.getMetadataWithoutMOC(entityRepo.createEntity(descriptor));
    }

    public void getRelatedMetadata(GeneralEntityInterface entity, String adql) {
        resultsPresenter.getMetadata(entity, adql);
    }


    public void showUserRelatedMetadata(CommonTapDescriptor descriptor, GeneralJavaScriptObject userData, boolean shouldHavePanel) {
        Log.debug("[MainPresenter][showUserRelatedMetadata]");
        GeneralEntityInterface entity = entityRepo.getEntityByName(descriptor.getLongName());
        if (entity == null) {
            entity = entityRepo.createEntity(descriptor);
        }
        if(userData.getProperty("overlaySet").hasProperty("refreshable") && userData.getProperty("overlaySet").getStringProperty("refreshable").equals("true")) {
            entity.setRefreshable(false);
            entity.setCustomRefreshable(true);
        }else{
            entity.setRefreshable(false);
            entity.setCustomRefreshable(false);
        }

        resultsPresenter.getUserMetadataAndPolygons(entity, userData, shouldHavePanel);
    }

    private void fetchDescriptorList(List<String> schemas, String category, String[] requiredCategoryArr) {
        fetchDescriptorList(schemas, category, requiredCategoryArr, null);
    }

    private void fetchDescriptorList(String schema, String category, String[] requiredCategoryArr) {
        fetchDescriptorList(Arrays.asList(schema), category, requiredCategoryArr, null);
    }

    private void fetchDescriptorList(String schema, String category, String[] requiredCategoryArr, CountObserver observer) {
        fetchDescriptorList(Arrays.asList(schema), category, requiredCategoryArr, observer);
    }

    private void fetchDescriptorList(List<String> schemas, String category, String[] requiredCategoryArr, CountObserver observer) {
        Log.debug("[MainPresenter] MainPresenter.fetchDescriptorList - Schema: " + String.join(",", schemas) + ", Category: " + category);
        descriptorRepo.initDescriptors(schemas, category, new Promise<CommonTapDescriptorList>() {
            @Override
            protected void success(CommonTapDescriptorList descriptorList) {
                DescriptorCountAdapter dca = new DescriptorCountAdapter(descriptorList, category, observer);
                descriptorRepo.setDescriptorCountAdapter(category, dca);
            }

            @Override
            protected void failure() {
                Log.debug("Failed to initialise descriptors with Schema: " +  String.join(",", schemas) + ", Category: " + category);
            }

            @Override
            protected void whenComplete() {
                if (Arrays.asList(requiredCategoryArr).contains(category)) {
                    boolean descriptorsLoaded = descriptorRepo.hasAllDescriptors(requiredCategoryArr);
                    if (descriptorsLoaded) {
                        descriptorRepo.requestSingleCount();
                    }
                } else if (Objects.equals(category, EsaSkyWebConstants.CATEGORY_EXTERNAL)) {
                    descriptorRepo.registerExtTapObserver();
                    descriptorRepo.setIsExtTapOpen(ctrlTBPresenter.isExtTapPanelOpen());

                    if (ctrlTBPresenter.isExtTapPanelOpen()) {
                        descriptorRepo.updateCount4AllExtTaps();
                    }
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

    public void loadOrQueueAuthorInformationFromSimbad(final String author) {
        if (descriptorRepo.hasDescriptors(EsaSkyWebConstants.CATEGORY_PUBLICATIONS)) {
            loadAuthorInformationFromSimbad(author);

        } else {
            descriptorRepo.registerDescriptorLoadedObserver(EsaSkyWebConstants.CATEGORY_PUBLICATIONS, () -> {
                Log.debug("[MainPresenter] PublicationDescriptor ready, loading author informaiton");
                loadAuthorInformationFromSimbad(author);
            });
            Log.debug(
                    "[MainPresenter] Can't show author information, publicationsDescriptor is not ready. Waiting for descriptor...");
        }
    }

    public void loadOrQueueBibcodeTargetListFromSimbad(final String bibcode) {
        if (descriptorRepo.hasDescriptors(EsaSkyWebConstants.CATEGORY_PUBLICATIONS)) {
            loadBibcodeInformaitonFromSimbad(bibcode);
        } else {
            descriptorRepo.registerDescriptorLoadedObserver(EsaSkyWebConstants.CATEGORY_PUBLICATIONS, () -> {
                Log.debug("[MainPresenter] PublicationDescriptor ready, loading bibcode informaiton");
                loadBibcodeInformaitonFromSimbad(bibcode);
            });
            Log.debug(
                    "[MainPresenter] Can't show soruces from bibcode, publicationsDescriptor is not ready. Waiting for descriptor...");
        }
    }

    private void loadAuthorInformationFromSimbad(String author) {
        final String authorUrl = "https://ui.adsabs.harvard.edu/#search/q=author%3A%22@@@AUTHOR@@@%22&sort=date%20desc%2C%20bibcode%20desc";
        if(!author.endsWith(".")) {
        	author += ".";
        }
        getTargetPresenter().showAuthorInfo(author, "\n", authorUrl, "@@@AUTHOR@@@");
        CommonEventBus.getEventBus().fireEvent(new AddTableEvent(entityRepo.createPublicationsByAuthorEntity(author)));

        GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_API, GoogleAnalytics.ACT_API_AUTHORINURL, author);
    }

    private void loadBibcodeInformaitonFromSimbad(String bibcode) {
        getTargetPresenter().showPublicationInfo(bibcode);
        GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_API, GoogleAnalytics.ACT_API_BIBCODEINURL, bibcode);
    }

    public void updateModuleVisibility() {
        ctrlTBPresenter.updateModuleVisibility();
        targetPresenter.updateModuleVisibility();
        headerPresenter.updateModuleVisibility();
    }

    public void onShiftPressed() {
        if (EntityRepository.getInstance().checkNumberOfEntitesWithMultiSelection() > 0) {
            AladinLiteWrapper.getAladinLite().startSelectionMode();
            allSkyPresenter.areaSelectionKeyboardShortcutStart();
        }
    }

    public void onShiftReleased() {
        AladinLiteWrapper.getAladinLite().endSelectionMode();
        allSkyPresenter.areaSelectionFinished();
    }

    private native void addShiftListener(MainPresenter mainPresenter) /*-{
        if (!$wnd.esasky) {
            $wnd.esasky = {}
        }
        $doc.addEventListener('keydown', function (e) {
            if (e.key == "Shift") {
                if (!$wnd.esasky.isShiftPressed) {
                    $wnd.esasky.isShiftPressed = true;
                    mainPresenter.@esac.archive.esasky.cl.web.client.presenter.MainPresenter::onShiftPressed()();
                }
            }
        });
        $doc.addEventListener('keyup', function (e) {
            if (e.key == "Shift") {
                if ($wnd.esasky.isShiftPressed) {
                    $wnd.esasky.isShiftPressed = false;
                    mainPresenter.@esac.archive.esasky.cl.web.client.presenter.MainPresenter::onShiftReleased()();
                }
            }
        });
    }-*/;

    public static native boolean isShiftPressed() /*-{
        return $wnd.esasky.isShiftPressed;
    }-*/;

    private void showEva() {
        if (view.isEvaShowing()) {
            view.toggleEvaPanelWithDrag();
        } else {
            view.initEvaPanel();
        }

    }

}