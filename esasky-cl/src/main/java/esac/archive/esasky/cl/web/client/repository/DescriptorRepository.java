package esac.archive.esasky.cl.web.client.repository;

import com.allen_sauer.gwt.log.client.Log;
import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.SearchArea;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.api.ApiConstants;
import esac.archive.esasky.cl.web.client.api.model.FootprintListJSONWrapper;
import esac.archive.esasky.cl.web.client.api.model.IJSONWrapper;
import esac.archive.esasky.cl.web.client.api.model.SourceListJSONWrapper;
import esac.archive.esasky.cl.web.client.callback.ExtTapCheckCallback;
import esac.archive.esasky.cl.web.client.callback.ICountRequestHandler;
import esac.archive.esasky.cl.web.client.callback.JsonRequestCallback;
import esac.archive.esasky.cl.web.client.callback.SsoCountRequestCallback;
import esac.archive.esasky.cl.web.client.event.ExtTapFovEvent;
import esac.archive.esasky.cl.web.client.event.ExtTapToggleEvent;
import esac.archive.esasky.cl.web.client.event.ExtTapToggleEventHandler;
import esac.archive.esasky.cl.web.client.event.TreeMapNewDataEvent;
import esac.archive.esasky.cl.web.client.model.SingleCount;
import esac.archive.esasky.cl.web.client.query.TAPExtTapService;
import esac.archive.esasky.cl.web.client.query.TAPSSOService;
import esac.archive.esasky.cl.web.client.query.TAPSingleCountService;
import esac.archive.esasky.cl.web.client.query.TAPUtils;
import esac.archive.esasky.cl.web.client.status.CountObserver;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.cl.web.client.status.GUISessionStatus;
import esac.archive.esasky.cl.web.client.utility.*;
import esac.archive.esasky.cl.web.client.utility.JSONUtils.IJSONRequestCallback;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.*;
import esac.archive.esasky.ifcs.model.shared.ColumnType;
import esac.archive.esasky.ifcs.model.shared.ESASkyColors;
import esac.archive.esasky.ifcs.model.shared.ESASkySSOSearchResult.ESASkySSOObjType;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class DescriptorRepository {

    /**
     * Descriptor List of adapter.
     */
    public class DescriptorListAdapter<T extends IDescriptor> extends DescriptorList<T> {

        private CountStatus countStatus;

        public DescriptorListAdapter(DescriptorList<T> descriptorList, CountObserver countObserver) {
            descriptors = descriptorList.getDescriptors();
            setTotal(descriptorList.getTotal());
            countStatus = new CountStatus(descriptorList);
            countStatus.registerObserver(countObserver);
        }

        public CountStatus getCountStatus() {
            return countStatus;
        }

    }

    public interface ObservationDescriptorListMapper extends ObjectMapper<ObservationDescriptorList> {
    }

    public interface SSODescriptorListMapper extends ObjectMapper<SSODescriptorList> {
    }

    public interface SpectraDescriptorListMapper extends ObjectMapper<SpectraDescriptorList> {
    }

    public interface CatalogDescriptorListMapper extends ObjectMapper<CatalogDescriptorList> {
    }

    public interface ExternalTapDescriptorListMapper extends ObjectMapper<ExtTapDescriptorList> {
    }

    public interface PublicationsDescriptorListMapper extends ObjectMapper<PublicationsDescriptorList> {
    }

    public interface ImageDescriptorListMapper extends ObjectMapper<ImageDescriptorList> {
    }

    public interface GwDescriptorListMapper extends ObjectMapper<GwDescriptorList> {
    }

    public interface IceCubeDescriptorListMapper extends ObjectMapper<IceCubeDescriptorList> {
    }

    public interface SingleCountListMapper extends ObjectMapper<List<SingleCount>> {
    }

    private DescriptorListAdapter<CatalogDescriptor> catDescriptors;
    private DescriptorListAdapter<ObservationDescriptor> obsDescriptors;
    private DescriptorListAdapter<SSODescriptor> ssoDescriptors;
    private DescriptorListAdapter<SpectraDescriptor> spectraDescriptors;
    private DescriptorListAdapter<PublicationsDescriptor> publicationsDescriptors;
    private DescriptorListAdapter<ExtTapDescriptor> extTapDescriptors;
    private DescriptorListAdapter<ImageDescriptor> imageDescriptors;
    private DescriptorListAdapter<GwDescriptor> gwDescriptors;
    private DescriptorListAdapter<IceCubeDescriptor> iceCubeDescriptors;

    /**
     * Descriptor and CountStatus hashMaps for improve counts
     */
    private HashMap<String, List<IDescriptor>> descriptorsMap;
    private HashMap<String, List<CountStatus>> countStatusMap;

    private CountObserver imageCountObserver = count -> {};

    private boolean catDescriptorsIsReady = false;
    private boolean obsDescriptorsIsReady = false;
    private boolean spectraDescriptorsIsReady = false;

    private final boolean isInitialPositionDescribedInCoordinates;
    private boolean isExtTapOpen = false;

    private static final String NOT_SET = "<not_set>";
    private static final String COLOR_STRING = "color";

    private ICountRequestHandler countRequestHandler;
    private SearchArea searchArea;

    private static DescriptorRepository _instance;

    private LinkedList<PublicationDescriptorLoadObserver> publicationDescriptorLoadObservers = new LinkedList<PublicationDescriptorLoadObserver>();

    public interface PublicationDescriptorLoadObserver {
        void onLoad();
    }

    public static DescriptorRepository init(boolean isInitialPositionDescribedInCoordinates) {
        _instance = new DescriptorRepository(isInitialPositionDescribedInCoordinates);
        return _instance;
    }

    public static DescriptorRepository getInstance() {
        if (_instance == null) {
            throw new AssertionError("You have to call init first");
        }
        return _instance;
    }

    private DescriptorRepository(boolean isInitialPositionDescribedInCoordinates) {
        this.isInitialPositionDescribedInCoordinates = isInitialPositionDescribedInCoordinates;
    }

    public void setCountRequestHandler(ICountRequestHandler countRequestHandler) {
        this.countRequestHandler = countRequestHandler;
    }

    public DescriptorListAdapter<CatalogDescriptor> getCatDescriptors() {
        return catDescriptors;
    }

    public DescriptorListAdapter<ExtTapDescriptor> getExtTapDescriptors() {
        return extTapDescriptors;
    }

    public DescriptorListAdapter<ObservationDescriptor> getObsDescriptors() {
        return obsDescriptors;
    }

    public DescriptorListAdapter<SSODescriptor> getSsoDescriptors() {
        return ssoDescriptors;
    }

    public DescriptorListAdapter<SpectraDescriptor> getSpectraDescriptors() {
        return spectraDescriptors;
    }

    public DescriptorListAdapter<PublicationsDescriptor> getPublicationsDescriptors() {
        return publicationsDescriptors;
    }

    public DescriptorListAdapter<ImageDescriptor> getImageDescriptors() {
        return imageDescriptors;
    }

    public DescriptorListAdapter<GwDescriptor> getGwDescriptors() {
        return gwDescriptors;
    }

    public DescriptorListAdapter<IceCubeDescriptor> getIceCubeDescriptors() {
        return iceCubeDescriptors;
    }

    public void initExtDescriptors(final CountObserver countObserver) {

        Log.debug("[DescriptorRepository] Into DescriptorRepository.initExtDescriptors");
        JSONUtils.getJSONFromUrl(EsaSkyWebConstants.EXT_TAP_GET_TAPS_URL, new IJSONRequestCallback() {

            @Override
            public void onSuccess(String responseText) {
                ExternalTapDescriptorListMapper mapper = GWT.create(ExternalTapDescriptorListMapper.class);
                extTapDescriptors = new DescriptorListAdapter<ExtTapDescriptor>(mapper.read(responseText), countObserver);
                registerExtTapObserver();

                List<IDescriptor> descriptorsList = new LinkedList<IDescriptor>();
                List<Integer> counts = new LinkedList<Integer>();

                for (ExtTapDescriptor tapService : extTapDescriptors.getDescriptors()) {
                    tapService.setInBackend(true);
                    descriptorsList.add(tapService);
                    counts.add(0);

                    for (String level1Name : tapService.getSubLevels().keySet()) {

                        ExtTapDescriptor level1Desc = ExtTapUtils.createLevelDescriptor(tapService, EsaSkyConstants.TREEMAP_LEVEL_1,
                                level1Name, tapService.getLevelColumnNames().get(0), tapService.getSubLevels().get(level1Name));

                        descriptorsList.add(level1Desc);
                        counts.add(0);

                        for (String level2Name : level1Desc.getSubLevels().keySet()) {
                            ExtTapDescriptor level2Desc = ExtTapUtils.createLevelDescriptor(level1Desc, EsaSkyConstants.TREEMAP_LEVEL_2,
                                    level2Name, tapService.getLevelColumnNames().get(1), level1Desc.getSubLevels().get(level2Name));

                            level2Desc.setInBackend(true);
                            descriptorsList.add(level2Desc);
                            counts.add(0);
                        }


                    }
                }

                CommonEventBus.getEventBus().fireEvent(new TreeMapNewDataEvent(descriptorsList, counts));

                for (IDescriptor descriptor : descriptorsList) {
                    if (extTapDescriptors.getDescriptorByMissionNameCaseInsensitive(descriptor.getMission()) == null) {
                        extTapDescriptors.getDescriptors().add((ExtTapDescriptor) descriptor);
                    }
                }

                Log.debug("[DescriptorRepository] Total extTap entries: " + extTapDescriptors.getTotal());
            }


            @Override
            public void onError(String errorCause) {
                Log.error("[DescriptorRepository] initExtDescriptors ERROR: " + errorCause);
            }

        });
    }

    public ExtTapDescriptor addExtTapDescriptorFromAPI(String name, String tapUrl, boolean dataOnlyInView, String adql) {
        ExtTapDescriptor descriptor = extTapDescriptors.getDescriptorByMissionNameCaseInsensitive(name);
        if (descriptor == null) {
            descriptor = new ExtTapDescriptor();
        }

        descriptor.setGuiShortName(name);
        descriptor.setGuiLongName(name);
        descriptor.setMission(name);
        descriptor.setCreditedInstitutions(name);
        descriptor.setTapRaColumn(EsaSkyWebConstants.S_RA);
        descriptor.setTapDecColumn(EsaSkyWebConstants.S_DEC);
        descriptor.setTapSTCSColumn(EsaSkyWebConstants.S_REGION);
        descriptor.setFovLimit(180.0);
        descriptor.setShapeLimit(3000);
        descriptor.setTapUrl(tapUrl);
        descriptor.setUniqueIdentifierField("obs_id");
        if (dataOnlyInView) {
            descriptor.setSearchFunction("polygonIntersect");
        } else {
            descriptor.setSearchFunction("");
        }
        descriptor.setResponseFormat("VOTable");
        descriptor.setInBackend(false);

        adql = adql.replace("from", "FROM");
        adql = adql.replace("where", "WHERE");
        String[] whereSplit = adql.split("WHERE");
        if (whereSplit.length > 1) {
            descriptor.setWhereADQL(whereSplit[1]);
        }
        String[] fromSplit = adql.split("FROM");
        descriptor.setSelectADQL(fromSplit[0]);

        String[] tapTable = fromSplit[1].split("\\s");
        descriptor.setTapTable(tapTable[1]);
        extTapDescriptors.getDescriptors().add(descriptor);
        return descriptor;
    }


    public void initCatDescriptors(final CountObserver countObserver) {

        Log.debug("[DescriptorRepository] Into DescriptorRepository.initCatDescriptors");
        JSONUtils.getJSONFromUrl(EsaSkyWebConstants.CATALOGS_URL, new IJSONRequestCallback() {

            @Override
            public void onSuccess(String responseText) {
                CatalogDescriptorListMapper mapper = GWT.create(CatalogDescriptorListMapper.class);
                catDescriptors = new DescriptorListAdapter<CatalogDescriptor>(mapper.read(responseText), countObserver);
                catDescriptorsIsReady = true;

                Log.debug("[DescriptorRepository] Total catalog entries: " + catDescriptors.getTotal());
                WavelengthUtils.setWavelengthRangeMaxMin(catDescriptors.getDescriptors());
                if (!GUISessionStatus.getIsInScienceMode()) {
                    GUISessionStatus.setDoCountOnEnteringScienceMode();
                }
            }

            @Override
            public void onError(String errorCause) {
                Log.error("[DescriptorRepository] initCatDescriptors ERROR: " + errorCause);
                DescriptorList<CatalogDescriptor> list = new DescriptorList<CatalogDescriptor>() {};
                catDescriptors = new DescriptorListAdapter<CatalogDescriptor>(list, countObserver);
                catDescriptorsIsReady = true;
            }

        });
    }

    public void initObsDescriptors(final CountObserver obsCountObserver) {

        Log.debug("[DescriptorRepository] Into DescriptorRepository.initObsDescriptors");
        JSONUtils.getJSONFromUrl(EsaSkyWebConstants.OBSERVATIONS_URL, new IJSONRequestCallback() {

            @Override
            public void onSuccess(String responseText) {
                ObservationDescriptorListMapper mapper = GWT.create(ObservationDescriptorListMapper.class);
                ObservationDescriptorList mappedDescriptorList = mapper.read(responseText);

                obsDescriptors = new DescriptorListAdapter<ObservationDescriptor>(mappedDescriptorList,
                        obsCountObserver);

                for (ObservationDescriptor desc : obsDescriptors.getDescriptors()) {
                    for (MetadataDescriptor md : desc.getMetadata()) {
                        if (md.getType() == ColumnType.RA) {
                            desc.setTapRaColumn(md.getTapName());
                        } else if (md.getType() == ColumnType.DEC) {
                            desc.setTapDecColumn(md.getTapName());
                        } else if (EsaSkyWebConstants.S_REGION.equalsIgnoreCase(md.getTapName())) {
                            desc.setTapSTCSColumn(md.getTapName());
                        }
                    }
                }
                obsDescriptorsIsReady = true;

                Log.debug("[DescriptorRepository] [init obs]Total observation entries: " + obsDescriptors.getTotal());
                WavelengthUtils.setWavelengthRangeMaxMin(obsDescriptors.getDescriptors());
                if (!GUISessionStatus.getIsInScienceMode()) {
                    GUISessionStatus.setDoCountOnEnteringScienceMode();
                }
            }

            @Override
            public void onError(String errorCause) {
                Log.error("[DescriptorRepository] initObsDescriptors ERROR: " + errorCause);
                DescriptorList<ObservationDescriptor> list = new DescriptorList<ObservationDescriptor>() {};
                obsDescriptors = new DescriptorListAdapter<ObservationDescriptor>(list, obsCountObserver);
                obsDescriptorsIsReady = true;
            }

        });
    }

    public void setOutreachImageCountObserver(final CountObserver imageCountObserver) {
        this.imageCountObserver = imageCountObserver;
    }

    public void initImageDescriptors() {

        Log.debug("[DescriptorRepository] Into DescriptorRepository.initImageDescriptors");
        JSONUtils.getJSONFromUrl(EsaSkyWebConstants.IMAGES_URL, new IJSONRequestCallback() {

            @Override
            public void onSuccess(String responseText) {
                ImageDescriptorListMapper mapper = GWT.create(ImageDescriptorListMapper.class);
                DescriptorList<ImageDescriptor> mappedDescriptorList = mapper.read(responseText);

                imageDescriptors = new DescriptorListAdapter<>(mappedDescriptorList,
                        imageCountObserver);

                for (ImageDescriptor desc : imageDescriptors.getDescriptors()) {
                    for (MetadataDescriptor md : desc.getMetadata()) {
                        if (md.getType() == ColumnType.RA) {
                            desc.setTapRaColumn(md.getTapName());
                        } else if (md.getType() == ColumnType.DEC) {
                            desc.setTapDecColumn(md.getTapName());
                        } else if (EsaSkyWebConstants.S_REGION.equalsIgnoreCase(md.getTapName())) {
                            desc.setTapSTCSColumn(md.getTapName());
                        }
                    }
                }

                Log.debug("[DescriptorRepository] [init image ]Total image entries: " + imageDescriptors.getTotal());
                WavelengthUtils.setWavelengthRangeMaxMin(imageDescriptors.getDescriptors());

                imageCountObserver.onCountUpdate(imageDescriptors.getTotal());
            }

            @Override
            public void onError(String errorCause) {
                Log.error("[DescriptorRepository] initImageDescriptors ERROR: " + errorCause);
                DescriptorList<ImageDescriptor> list = new DescriptorList<ImageDescriptor>() {};
                imageDescriptors = new DescriptorListAdapter<>(list, imageCountObserver);
            }

        });
    }

    public void initGwDescriptors(final CountObserver gwCountObserver) {
        Log.debug("[DescriptorRepository] Into DescriptorRepository.initGwDescriptors");
        JSONUtils.getJSONFromUrl(EsaSkyWebConstants.GW_URL, new IJSONRequestCallback() {

            @Override
            public void onSuccess(String responseText) {
                GwDescriptorListMapper mapper = GWT.create(GwDescriptorListMapper.class);
                DescriptorList<GwDescriptor> mappedDescriptorList = mapper.read(responseText);

                gwDescriptors = new DescriptorListAdapter<>(mappedDescriptorList, gwCountObserver);

                for (GwDescriptor desc : gwDescriptors.getDescriptors()) {
                    for (MetadataDescriptor md : desc.getMetadata()) {
                        if (md.getType() == ColumnType.RA) {
                            desc.setTapRaColumn(md.getTapName());
                        } else if (md.getType() == ColumnType.DEC) {
                            desc.setTapDecColumn(md.getTapName());
                        } else if (EsaSkyWebConstants.S_REGION.equalsIgnoreCase(md.getTapName())) {
                            desc.setTapSTCSColumn(md.getTapName());
                        }
                    }
                }

                Log.debug("[DescriptorRepository] [init gw] Total gw entries: " + gwDescriptors.getTotal());
                //WavelengthUtils.setWavelengthRangeMaxMin(gwDescriptors.getDescriptors());

                gwCountObserver.onCountUpdate(gwDescriptors.getTotal());
            }

            @Override
            public void onError(String errorCause) {
                Log.error("[DescriptorRepository] initGwDescriptors ERROR: " + errorCause);
                DescriptorList<GwDescriptor> list = new DescriptorList<GwDescriptor>() {};
                gwDescriptors = new DescriptorListAdapter<>(list, gwCountObserver);
            }

        });
    }

    public void initIceCubeDescriptors(final CountObserver iceCubeCountObserver) {
        Log.debug("[DescriptorRepository] Into DescriptorRepository.initIceCubeDescriptors");
        JSONUtils.getJSONFromUrl(EsaSkyWebConstants.ICECUBE_URL, new IJSONRequestCallback() {

            @Override
            public void onSuccess(String responseText) {
                IceCubeDescriptorListMapper mapper = GWT.create(IceCubeDescriptorListMapper.class);
                DescriptorList<IceCubeDescriptor> mappedDescriptorList = mapper.read(responseText);

                iceCubeDescriptors = new DescriptorListAdapter<>(mappedDescriptorList, iceCubeCountObserver);

                for (IceCubeDescriptor desc : iceCubeDescriptors.getDescriptors()) {
                    for (MetadataDescriptor md : desc.getMetadata()) {
                        if (md.getType() == ColumnType.RA) {
                            desc.setTapRaColumn(md.getTapName());
                        } else if (md.getType() == ColumnType.DEC) {
                            desc.setTapDecColumn(md.getTapName());
                        } else if (EsaSkyWebConstants.S_REGION.equalsIgnoreCase(md.getTapName())) {
                            desc.setTapSTCSColumn(md.getTapName());
                        }
                    }
                }

                Log.debug("[DescriptorRepository] [init iceCube] Total iceCube entries: " + iceCubeDescriptors.getTotal());
                //WavelengthUtils.setWavelengthRangeMaxMin(gwDescriptors.getDescriptors());

                iceCubeCountObserver.onCountUpdate(iceCubeDescriptors.getTotal());
            }

            @Override
            public void onError(String errorCause) {
                Log.error("[DescriptorRepository] initIceCubeDescriptor ERROR: " + errorCause);
                DescriptorList<IceCubeDescriptor> list = new DescriptorList<IceCubeDescriptor>() {};
                iceCubeDescriptors = new DescriptorListAdapter<>(list, iceCubeCountObserver);
            }

        });
    }

    public void initSSODescriptors(final CountObserver ssoCountObserver) {

        Log.debug("[DescriptorRepository] Into DescriptorRepository.initSSODescriptors");
        JSONUtils.getJSONFromUrl(EsaSkyWebConstants.SSO_URL, new IJSONRequestCallback() {

            @Override
            public void onSuccess(String responseText) {

                SSODescriptorListMapper mapperSSO = GWT.create(SSODescriptorListMapper.class);
                SSODescriptorList ssoMappedDescriptorList = mapperSSO.read(responseText);
                ssoDescriptors = new DescriptorListAdapter<SSODescriptor>(ssoMappedDescriptorList, ssoCountObserver);


                Log.debug("[DescriptorRepository] [initSSODescriptors] Total observation entries: " + ssoDescriptors.getTotal());
                WavelengthUtils.setWavelengthRangeMaxMin(ssoDescriptors.getDescriptors());
                if (!GUISessionStatus.getIsInScienceMode()) {
                    GUISessionStatus.setDoCountOnEnteringScienceMode();
                }
            }

            @Override
            public void onError(String errorCause) {
                Log.error("[DescriptorRepository] initSSODescriptors ERROR: " + errorCause);
                checkDoCountAll();
            }

        });
    }

    public void initSpectraDescriptors(final CountObserver countObserver) {

        Log.debug("[DescriptorRepository] Into DescriptorRepository.initSpectraDescriptors");
        JSONUtils.getJSONFromUrl(EsaSkyWebConstants.SPECTRA_URL,
                new esac.archive.esasky.cl.web.client.utility.JSONUtils.IJSONRequestCallback() {

                    @Override
                    public void onSuccess(String responseText) {
                        SpectraDescriptorListMapper mapper = GWT.create(SpectraDescriptorListMapper.class);
                        spectraDescriptors = new DescriptorListAdapter<SpectraDescriptor>(mapper.read(responseText),
                                countObserver);
                        initializeSpectraDescriptorPositions();
                        spectraDescriptorsIsReady = true;

                        Log.debug("[DescriptorRepository] Total spectra entries: " + spectraDescriptors.getTotal());
                        WavelengthUtils.setWavelengthRangeMaxMin(spectraDescriptors.getDescriptors());
                        if (!GUISessionStatus.getIsInScienceMode()) {
                            GUISessionStatus.setDoCountOnEnteringScienceMode();
                        }
                    }

                    @Override
                    public void onError(String errorCause) {
                        Log.error("[DescriptorRepository] initSpectraDescriptors ERROR: " + errorCause);
                        spectraDescriptorsIsReady = true;
                    }

                });
    }

    private void initializeSpectraDescriptorPositions() {
        for (SpectraDescriptor desc : spectraDescriptors.getDescriptors()) {
            for (MetadataDescriptor md : desc.getMetadata()) {
                if (md.getType() == ColumnType.RA) {
                    desc.setTapRaColumn(md.getTapName());
                } else if (md.getType() == ColumnType.DEC) {
                    desc.setTapDecColumn(md.getTapName());
                } else if (EsaSkyWebConstants.S_REGION.equalsIgnoreCase(md.getTapName())) {
                    desc.setTapSTCSColumn(md.getTapName());
                }
            }
        }
    }

    public void initPubDescriptors() {
        Log.debug("[DescriptorRepository] Into DescriptorRepository.initPubDescriptors");

        JSONUtils.getJSONFromUrl(EsaSkyWebConstants.PUBLICATIONS_URL, new IJSONRequestCallback() {

            @Override
            public void onSuccess(String responseText) {
                PublicationsDescriptorListMapper mapper = GWT.create(PublicationsDescriptorListMapper.class);
                publicationsDescriptors = new DescriptorListAdapter<PublicationsDescriptor>(mapper.read(responseText),
                        new CountObserver() {

                            @Override
                            public void onCountUpdate(long newCount) {
                            }
                        });
                for (PublicationDescriptorLoadObserver observer : publicationDescriptorLoadObservers) {
                    observer.onLoad();
                }

                Log.debug("[DescriptorRepository] Total publications entries: " + publicationsDescriptors.getTotal());
            }

            @Override
            public void onError(String errorCause) {
                Log.error("[DescriptorRepository] initPubDescriptors ERROR: " + errorCause);
            }

        });
    }

    private void checkDoCountAll() {
        if (EsaSkyWebConstants.SINGLE_COUNT_ENABLED && catDescriptorsIsReady && obsDescriptorsIsReady
                && spectraDescriptorsIsReady
                && isInitialPositionDescribedInCoordinates) {
            doCountAll();
        }
    }

    public void doCountAll() {

		// Single dynamic count
        requestSingleCount();
        if (isExtTapOpen) {
            updateCount4AllExtTaps();
        }
    }

    public void updateCount4AllExtTaps() {
        double fov = CoordinateUtils.getCenterCoordinateInJ2000().getFov();
        CommonEventBus.getEventBus().fireEvent(new ExtTapFovEvent(fov));
        if (fov < EsaSkyWebConstants.EXTTAP_FOV_LIMIT) {
            for (ExtTapDescriptor descriptor : extTapDescriptors.getDescriptors()) {
                if (EsaSkyConstants.TREEMAP_LEVEL_SERVICE == descriptor.getTreeMapLevel()) {
                    if (extTapDescriptors.getCountStatus().hasMoved(descriptor)) {
                        updateCount4ExtTap(descriptor);
                    }
                }
            }
        }
    }

    public void updateCount4ExtTap(ExtTapDescriptor descriptor) {
        final CountStatus cs = extTapDescriptors.getCountStatus();
        if (!cs.containsDescriptor(descriptor)) {
            cs.addDescriptor(descriptor);
        }

        String adql = TAPExtTapService.getInstance().getCountAdql(descriptor);
        String url = descriptor.getTapQuery(EsaSkyWebConstants.EXT_TAP_REQUEST_URL, adql, descriptor.getResponseFormat());

        JSONUtils.getJSONFromUrl(url, new ExtTapCheckCallback(adql, descriptor, cs,
                countRequestHandler.getProgressIndicatorMessage() + " " + descriptor.getMission()));
    }

    public void doCountExtTap(IDescriptor descriptor, CountStatus cs) {

    }

    public void doCountSSO(String ssoName, ESASkySSOObjType ssoType) {

        String url = TAPUtils.getTAPQuery(URL.encodeQueryString(TAPSSOService.getInstance().getCount(ssoName, ssoType)),
                EsaSkyConstants.JSON);

        Log.debug("[doCountSSO] SSO count Query [" + url + "]");
        JSONUtils.getJSONFromUrl(url,
                new SsoCountRequestCallback(ssoDescriptors, ssoName, ssoType));
    }

    public void updateSearchArea(SearchArea area) {
        this.searchArea = area;
        for (List<IDescriptor> descriptors : descriptorsMap.values()) {
            for (IDescriptor descriptor : descriptors) {
                descriptor.setSearchArea(area);
            }
        }

        doCountAll();
    }

    private static long lastestSingleCountTimecall;

    private final void requestSingleCount() {

        final SkyViewPosition skyViewPosition = CoordinateUtils.getCenterCoordinateInJ2000();

        final long timecall = System.currentTimeMillis();
        lastestSingleCountTimecall = timecall;
        String url = "";

        if (searchArea != null) {
            url = TAPUtils.getTAPCountQuery(URL.encodeQueryString(
                    TAPSingleCountService.getInstance().getCountSearchArea(searchArea)));
        } else {
            url = TAPUtils.getTAPCountQuery(URL.encodeQueryString(
                    TAPSingleCountService.getInstance().getCountStcs(AladinLiteWrapper.getAladinLite())));
        }

        JSONUtils.getJSONFromUrl(url, new JsonRequestCallback(countRequestHandler.getProgressIndicatorMessage(), url) {

            @Override
            protected void onSuccess(Response response) {
                try {
                    if (timecall < lastestSingleCountTimecall) {
                        Log.warn(this.getClass().getSimpleName() + " discarded server answer with timecall=" + timecall
                                + " , dif:" + (lastestSingleCountTimecall - timecall));
                        return;
                    }
                    SingleCountListMapper scMapper = GWT.create(SingleCountListMapper.class);
                    List<SingleCount> singleCountList = scMapper.read(response.getText());
                    doUpdateSingleCount(singleCountList, skyViewPosition);


                } catch (Exception ex) {
                    Log.error("[DescriptorRepository] requestSingleCount.onSuccess ERROR: " + ex.getMessage(), ex);
                }
            }

        });
    }


    private void prepareDescriptorsMap() {
        descriptorsMap = new HashMap<String, List<IDescriptor>>();
        countStatusMap = new HashMap<String, List<CountStatus>>();

        addDescriptorsToHashMaps(catDescriptors);
        addDescriptorsToHashMaps(obsDescriptors);
        addDescriptorsToHashMaps(spectraDescriptors);
        addDescriptorsToHashMaps(publicationsDescriptors);
        addDescriptorsToHashMaps(imageDescriptors);
    }

    private void addDescriptorsToHashMaps(DescriptorListAdapter<?> descriptorListAdapter) {
        if (descriptorListAdapter != null) {
            final CountStatus cs = descriptorListAdapter.getCountStatus();
            for (IDescriptor descriptor : descriptorListAdapter.getDescriptors()) {
                if (!descriptorsMap.containsKey(descriptor.getTapTable())) {
                    descriptorsMap.put(descriptor.getTapTable(), new LinkedList<IDescriptor>());
                }
                if (!countStatusMap.containsKey(descriptor.getTapTable())) {
                    countStatusMap.put(descriptor.getTapTable(), new LinkedList<CountStatus>());
                }
                List<IDescriptor> descriptorList = descriptorsMap.get(descriptor.getTapTable());
                List<CountStatus> countStatusList = countStatusMap.get(descriptor.getTapTable());
                descriptorList.add(descriptor);
                countStatusList.add(cs);
            }
        }
    }

    private void doUpdateSingleCount(List<SingleCount> singleCountList, final SkyViewPosition skyViewPosition) {
        if (descriptorsMap == null) {
            prepareDescriptorsMap();
        }

        ArrayList<String> remainingDescriptors = new ArrayList<>(descriptorsMap.keySet());

        List<IDescriptor> descriptors = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();

        setCount(singleCountList, skyViewPosition, remainingDescriptors, descriptors, counts);

        //Handling that the fast count doesn't give any results for missing missions in the area so we set them to 0
        setZeroCountOnNoResponseMissions(skyViewPosition, remainingDescriptors, descriptors, counts);

        if (descriptors.size() > 0) {
            notifyCountChange(descriptors, counts);
        }
    }

    private void notifyCountChange(List<IDescriptor> descriptors, List<Integer> counts) {
        CommonEventBus.getEventBus().fireEvent(new TreeMapNewDataEvent(descriptors, counts));

        LinkedList<CountStatus> statusToBeUpdated = new LinkedList<>();
        for (String key : countStatusMap.keySet()) {
            for (CountStatus cs : countStatusMap.get(key)) {
                if (!statusToBeUpdated.contains(cs)) {
                    statusToBeUpdated.add(cs);
                }
            }
        }
        for (CountStatus cs : statusToBeUpdated) {
            cs.updateCount();
        }
    }

    private void setCount(List<SingleCount> singleCountList, final SkyViewPosition skyViewPosition,
                          ArrayList<String> remainingDescriptors, List<IDescriptor> descriptors, List<Integer> counts) {
        for (SingleCount singleCount : singleCountList) {

            if (descriptorsMap.containsKey(singleCount.getTableName())) {

                List<IDescriptor> descriptorList = descriptorsMap.get(singleCount.getTableName());
                List<CountStatus> countList = countStatusMap.get(singleCount.getTableName());
                int i = 0;
                for (IDescriptor descriptor : descriptorList) {
                    CountStatus cs = countList.get(i);
                    i++;
                    final int count = (singleCount.getCount() != null) ? singleCount.getCount() : 0;
                    cs.setCountDetails(descriptor, count, System.currentTimeMillis(), skyViewPosition);

                    remainingDescriptors.remove(singleCount.getTableName());

                    descriptors.add(descriptor);
                    counts.add(count);
                }

            } else {
                Log.warn("[DescriptorRepository] doUpdateSingleCount. TABLE_NAME: '" + singleCount.getTableName()
                        + "' NOT FOUND IN DESCRIPTORS!");
            }
        }
    }

    private void setZeroCountOnNoResponseMissions(final SkyViewPosition skyViewPosition,
                                                  ArrayList<String> remainingDescriptors, List<IDescriptor> descriptors, List<Integer> counts) {
        for (String mission : remainingDescriptors) {
            List<IDescriptor> descriptorList = descriptorsMap.get(mission);
            List<CountStatus> countStatusList = countStatusMap.get(mission);
            int i = 0;
            for (IDescriptor descriptor : descriptorList) {
                CountStatus cs = countStatusList.get(i);
                i++;
                final int count = 0;
                cs.setCountDetails(descriptor, count, System.currentTimeMillis(), skyViewPosition);

                descriptors.add(descriptor);
                counts.add(count);
            }
        }
    }

    public IDescriptor initUserDescriptor(List<MetadataDescriptor> metadata, IJSONWrapper jsonWrapper) {
        if (jsonWrapper instanceof FootprintListJSONWrapper) {
            return initUserDescriptor4Footprint(metadata, (FootprintListJSONWrapper) jsonWrapper);
        } else if (jsonWrapper instanceof SourceListJSONWrapper) {
            return initUserDescriptor4Catalogue(metadata, (SourceListJSONWrapper) jsonWrapper);
        }
        return null;
    }

    private ObservationDescriptor initUserDescriptor4Footprint(List<MetadataDescriptor> metadata,
                                                               FootprintListJSONWrapper footprintsSet) {
        ObservationDescriptor descriptor = new UserObservationDescriptor();

        descriptor.setMetadata(metadata);

        descriptor.setMission(footprintsSet.getOverlaySet().getOverlayName());
        descriptor.setGuiLongName(footprintsSet.getOverlaySet().getOverlayName());
        descriptor.setGuiShortName(footprintsSet.getOverlaySet().getOverlayName());
        descriptor.setDescriptorId(footprintsSet.getOverlaySet().getOverlayName());
        descriptor.setPrimaryColor(footprintsSet.getOverlaySet().getColor());

        descriptor.setUniqueIdentifierField(ApiConstants.OBS_NAME);

        descriptor.setTapSTCSColumn("stcs");
        descriptor.setSampEnabled(false);

        descriptor.setFovLimit(360.0);

        descriptor.setTapTable(NOT_SET);
        descriptor.setTabCount(0);

        return descriptor;
    }

    public BaseDescriptor initUserDescriptor4MOC(String name, GeneralJavaScriptObject options) {
        BaseDescriptor descriptor = new BaseDescriptor() {

            @Override
            public String getIcon() {
                return "catalog";
            }
        };

        descriptor.setMission(name);
        descriptor.setGuiLongName(name);
        descriptor.setGuiShortName(name);
        descriptor.setDescriptorId(name);
        if (options.hasProperty(COLOR_STRING)) {
            descriptor.setPrimaryColor(options.getStringProperty(COLOR_STRING));
        } else {
            descriptor.setPrimaryColor(ESASkyColors.getNext());
        }

        descriptor.setUniqueIdentifierField(ApiConstants.OBS_NAME);

        descriptor.setSampEnabled(false);

        descriptor.setFovLimit(360.0);

        descriptor.setTapTable(NOT_SET);
        descriptor.setTabCount(0);

        return descriptor;
    }

    private CatalogDescriptor initUserDescriptor4Catalogue(List<MetadataDescriptor> metadata,
                                                           SourceListJSONWrapper userCatalogue) {
        CatalogDescriptor descriptor = new UserCatalogueDescriptor();

        descriptor.setMetadata(metadata);

        descriptor.setMission(userCatalogue.getOverlaySet().getOverlayName());
        descriptor.setGuiLongName(userCatalogue.getOverlaySet().getOverlayName());
        descriptor.setGuiShortName(userCatalogue.getOverlaySet().getOverlayName());
        descriptor.setPrimaryColor(userCatalogue.getOverlaySet().getColor());

        descriptor.setFovLimit(360.0);

        descriptor.setShapeLimit(10000);

        descriptor.setTapTable(NOT_SET);
        descriptor.setTabCount(0);

        descriptor.setTapRaColumn(ApiConstants.CENTER_RA_DEG);
        descriptor.setTapDecColumn(ApiConstants.CENTER_DEC_DEG);
        descriptor.setUniqueIdentifierField(ApiConstants.CAT_NAME);

        return descriptor;
    }

    public void registerExtTapObserver() {

        CommonEventBus.getEventBus().addHandler(ExtTapToggleEvent.TYPE,
                new ExtTapToggleEventHandler() {

                    @Override
                    public void onToggle(final ExtTapToggleEvent event) {
                        boolean wasOpen = isExtTapOpen;
                        isExtTapOpen = event.isOpen();
                        if (!wasOpen && isExtTapOpen) {
                            updateCount4AllExtTaps();
                        }
                    }
                });
    }

    public void addPublicationDescriptorLoadObserver(PublicationDescriptorLoadObserver observer) {
        publicationDescriptorLoadObservers.add(observer);
    }
}