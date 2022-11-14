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
import esac.archive.esasky.cl.web.client.api.model.GeneralSkyObject;
import esac.archive.esasky.cl.web.client.api.model.IJSONWrapper;
import esac.archive.esasky.cl.web.client.api.model.SourceListJSONWrapper;
import esac.archive.esasky.cl.web.client.callback.ICountRequestHandler;
import esac.archive.esasky.cl.web.client.callback.JsonRequestCallback;
import esac.archive.esasky.cl.web.client.callback.Promise;
import esac.archive.esasky.cl.web.client.event.ExtTapToggleEvent;
import esac.archive.esasky.cl.web.client.event.ExtTapToggleEventHandler;
import esac.archive.esasky.cl.web.client.event.TreeMapNewDataEvent;
import esac.archive.esasky.cl.web.client.model.DescriptorCountAdapter;
import esac.archive.esasky.cl.web.client.model.SingleCount;
import esac.archive.esasky.cl.web.client.query.*;
import esac.archive.esasky.cl.web.client.status.CountObserver;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.cl.web.client.utility.*;
import esac.archive.esasky.cl.web.client.utility.JSONUtils.IJSONRequestCallback;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.*;
import esac.archive.esasky.ifcs.model.shared.ESASkyColors;
import esac.archive.esasky.ifcs.model.shared.ESASkySSOSearchResult.ESASkySSOObjType;

import java.util.*;

public class DescriptorRepository {

    /**
     * Descriptor List of adapter.
     */
    public static class DescriptorListAdapter<T extends IDescriptor> extends DescriptorList<T> {


        public DescriptorListAdapter(DescriptorList<T> descriptorList, CountObserver countObserver) {
//            descriptors = descriptorList.getDescriptors();
//            setTotal(descriptorList.getTotal());
//            countStatus = new CountStatus(descriptorList);
//            countStatus.registerObserver(countObserver);
        }

        public CountStatus getCountStatus() {
            return null;
        }

    }


    public interface CommonTapDescriptorListMapper extends ObjectMapper<CommonTapDescriptorList> {
    }

    public interface TapDescriptorListMapper extends ObjectMapper<TapDescriptorList> {
    }

    public interface SingleCountListMapper extends ObjectMapper<List<SingleCount>> {
    }


    private Map<String, DescriptorCountAdapter> descriptorCountAdapterMap = new HashMap<>();
    private Map<String, String> tableCategoryMap = new HashMap<>();

    /**
     * Descriptor and CountStatus hashMaps for improve counts
     */
//    private HashMap<String, List<CommonTapDescriptor>> descriptorsMap;
//    private HashMap<String, List<CountStatus>> countStatusMap;

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

    public DescriptorListAdapter<ExtTapDescriptor> getExtTapDescriptors() {
        return null;
    }


    public void setDescriptors(String category, DescriptorCountAdapter descriptors) {
        descriptorCountAdapterMap.put(category, descriptors);

        for (CommonTapDescriptor desc : descriptors.getDescriptors()) {
            tableCategoryMap.put(desc.getTableName(), desc.getCategory());
        }
    }

    public DescriptorCountAdapter getDescriptorCountAdapter(String category) {
       return descriptorCountAdapterMap.get(category);
    }

    public CommonTapDescriptorList getDescriptorList(String category) {
        DescriptorCountAdapter dca = getDescriptorCountAdapter(category);
        return dca != null ? dca.getTapDescriptorList() : null;
    }

    public List<CommonTapDescriptor> getDescriptors(String category) {
        CommonTapDescriptorList tdl = getDescriptorList(category);
        return tdl != null ? tdl.getDescriptors() : null;
    }

    public boolean hasDescriptors(String category) {
        List<CommonTapDescriptor> descriptors = getDescriptors(category);
        return descriptors != null && !descriptors.isEmpty();
    }

    public CommonTapDescriptor getFirstDescriptor(String category) {
        List<CommonTapDescriptor> descriptors = getDescriptors(category);

        if (descriptors != null && !descriptors.isEmpty()) {
            return descriptors.get(0);
        } else {
            return null;
        }
    }

    public CommonTapDescriptor getFirstDescriptor(String category, String mission) {
        List<CommonTapDescriptor> descriptors = getDescriptors(category);

        if (descriptors != null && !descriptors.isEmpty()) {
            return descriptors.stream().filter(d -> d.getMission().equals(mission)).findFirst().orElse(null);
        } else {
            return null;
        }
    }

    public DescriptorListAdapter<SSODescriptor> getSsoDescriptors() {
        return null;
    }

    public DescriptorCountAdapter getSpectraDescriptors() {
        return null;
    }


    public DescriptorListAdapter<ImageDescriptor> getImageDescriptors() {
        return null;
    }


    public void initExtDescriptors(final CountObserver countObserver) {

//        Log.debug("[DescriptorRepository] Into DescriptorRepository.initExtDescriptors");
//        JSONUtils.getJSONFromUrl(EsaSkyWebConstants.EXT_TAP_GET_TAPS_URL, new IJSONRequestCallback() {
//
//            @Override
//            public void onSuccess(String responseText) {
//                ExternalTapDescriptorListMapper mapper = GWT.create(ExternalTapDescriptorListMapper.class);
//                extTapDescriptors = new DescriptorListAdapter<ExtTapDescriptor>(mapper.read(responseText), countObserver);
//                registerExtTapObserver();
//
//                List<IDescriptor> descriptorsList = new LinkedList<IDescriptor>();
//                List<Integer> counts = new LinkedList<Integer>();
//
//                for (ExtTapDescriptor tapService : extTapDescriptors.getDescriptors()) {
////                    tapService.setInBackend(true);
//                    descriptorsList.add(tapService);
//                    counts.add(0);
//
//                    for (String level1Name : tapService.getSubLevels().keySet()) {
//
//                        ExtTapDescriptor level1Desc = ExtTapUtils.createLevelDescriptor(tapService, EsaSkyConstants.TREEMAP_LEVEL_1,
//                                level1Name, tapService.getLevelColumnNames().get(0), tapService.getSubLevels().get(level1Name));
//
//                        descriptorsList.add(level1Desc);
//                        counts.add(0);
//
//                        for (String level2Name : level1Desc.getSubLevels().keySet()) {
//                            ExtTapDescriptor level2Desc = ExtTapUtils.createLevelDescriptor(level1Desc, EsaSkyConstants.TREEMAP_LEVEL_2,
//                                    level2Name, tapService.getLevelColumnNames().get(1), level1Desc.getSubLevels().get(level2Name));
//
//                            level2Desc.setInBackend(true);
//                            descriptorsList.add(level2Desc);
//                            counts.add(0);
//                        }
//
//
//                    }
//                }
//
//                CommonEventBus.getEventBus().fireEvent(new TreeMapNewDataEvent(descriptorsList, counts));
//
//                for (IDescriptor descriptor : descriptorsList) {
//                    if (extTapDescriptors.getDescriptorByMissionNameCaseInsensitive(descriptor.getMission()) == null) {
//                        extTapDescriptors.getDescriptors().add((ExtTapDescriptor) descriptor);
//                    }
//                }
//
//                Log.debug("[DescriptorRepository] Total extTap entries: " + extTapDescriptors.getTotal());
//            }
//
//
//            @Override
//            public void onError(String errorCause) {
//                Log.error("[DescriptorRepository] initExtDescriptors ERROR: " + errorCause);
//            }
//
//        });
    }

    public ExtTapDescriptor addExtTapDescriptor(String tapUrl, String tableName, String adql, GeneralJavaScriptObject metadata, boolean fovLimit) {
//        String longName = tapUrl+"/"+tableName ;
//        String umission = longName + "?" + adql;
//
//        ExtTapDescriptor descriptor = extTapDescriptors.getDescriptorByMissionNameCaseInsensitive(umission);
//        if (descriptor == null) {
//            descriptor = new ExtTapDescriptor();
//        }
//        descriptor.setTapTableMetadata(metadata);
//        descriptor.setUseUcd(true);
//        descriptor.setGuiShortName(tableName);
//        descriptor.setGuiLongName(longName);
//        descriptor.setMission(umission);
//        descriptor.setCreditedInstitutions(tapUrl);
//        descriptor.setFovLimit(180.0);
//        descriptor.setShapeLimit(3000);
//
//        if (!tapUrl.contains("/sync")) {
//            tapUrl += "/sync";
//        }
//        descriptor.setTapUrl(tapUrl);
//        descriptor.setResponseFormat("VOTable");
//        descriptor.setInBackend(false);
//        descriptor.setPrimaryColor(ESASkyColors.getNext());
//        descriptor.setTapTable(tableName);
//
//        if(fovLimit && descriptor.tapMetadataContainsPos()) {
//            descriptor.setSearchFunction("cointainsPoint");
//        } else {
//            descriptor.setSearchFunction("");
//        }
//
//        adql = adql.replace("from", "FROM");
//        adql = adql.replace("where", "WHERE");
//        String[] whereSplit = adql.split("WHERE");
//        if (whereSplit.length > 1) {
//            descriptor.setWhereADQL(whereSplit[1]);
//        }
//        String[] fromSplit = adql.split("FROM");
//        descriptor.setSelectADQL(fromSplit[0]);
//
//
//        extTapDescriptors.getDescriptors().add(descriptor);
//        return descriptor;
        return null;
    }

    public ExtTapDescriptor addExtTapDescriptorFromAPI(String name, String tapUrl, boolean dataOnlyInView, String adql) {
        return null;
//        ExtTapDescriptor descriptor = extTapDescriptors.getDescriptorByMissionNameCaseInsensitive(name);
//        if (descriptor == null) {
//            descriptor = new ExtTapDescriptor();
//        }
//
//        descriptor.setGuiShortName(name);
//        descriptor.setGuiLongName(name);
//        descriptor.setMission(name);
//        descriptor.setCreditedInstitutions(name);
//        descriptor.setTapRaColumn(EsaSkyWebConstants.S_RA);
//        descriptor.setTapDecColumn(EsaSkyWebConstants.S_DEC);
//        descriptor.setTapSTCSColumn(EsaSkyWebConstants.S_REGION);
//        descriptor.setFovLimit(180.0);
//        descriptor.setShapeLimit(3000);
//        descriptor.setTapUrl(tapUrl);
//        descriptor.setUniqueIdentifierField("obs_id");
//        if (dataOnlyInView) {
//            descriptor.setSearchFunction("polygonIntersect");
//        } else {
//            descriptor.setSearchFunction("");
//        }
//        descriptor.setResponseFormat("VOTable");
//        descriptor.setInBackend(false);
//
//        adql = adql.replace("from", "FROM");
//        adql = adql.replace("where", "WHERE");
//        String[] whereSplit = adql.split("WHERE");
//        if (whereSplit.length > 1) {
//            descriptor.setWhereADQL(whereSplit[1]);
//        }
//        String[] fromSplit = adql.split("FROM");
//        descriptor.setSelectADQL(fromSplit[0]);
//
//        String[] tapTable = fromSplit[1].split("\\s");
//        descriptor.setTapTable(tapTable[1]);
//        extTapDescriptors.getDescriptors().add(descriptor);
//        return descriptor;
    }


    public void initCatalogueDescriptors(final CountObserver countObserver) {

//        Log.debug("[DescriptorRepository] Into DescriptorRepository.initCatDescriptors");
//        JSONUtils.getJSONFromUrl(EsaSkyWebConstants.CATALOGS_URL, new IJSONRequestCallback() {
//
//            @Override
//            public void onSuccess(String responseText) {
//                CatalogDescriptorListMapper mapper = GWT.create(CatalogDescriptorListMapper.class);
//                catDescriptors = new DescriptorListAdapter<CatalogDescriptor>(mapper.read(responseText), countObserver);
//                catDescriptorsIsReady = true;
//
//                Log.debug("[DescriptorRepository] Total catalog entries: " + catDescriptors.getTotal());
//                WavelengthUtils.setWavelengthRangeMaxMin(catDescriptors.getDescriptors());
//                if (!GUISessionStatus.getIsInScienceMode()) {
//                    GUISessionStatus.setDoCountOnEnteringScienceMode();
//                }
//            }
//
//            @Override
//            public void onError(String errorCause) {
//                Log.error("[DescriptorRepository] initCatDescriptors ERROR: " + errorCause);
//                DescriptorList<CatalogDescriptor> list = new DescriptorList<CatalogDescriptor>() {};
//                catDescriptors = new DescriptorListAdapter<CatalogDescriptor>(list, countObserver);
//                catDescriptorsIsReady = true;
//            }
//
//        });
    }


    public void initDescriptors(String schema, String category, Promise<CommonTapDescriptorList> promise) {
        Log.debug("[DescriptorRepository] Into DescriptorRepository.initDescriptors");

        TAPDescriptorService.getInstance().fetchDescriptors(schema, category, new IJSONRequestCallback() {
            @Override
            public void onSuccess(String responseText) {
                CommonTapDescriptorListMapper mapper = GWT.create(CommonTapDescriptorListMapper.class);
                CommonTapDescriptorList mappedDescriptorList  = mapper.read(responseText);
                for(CommonTapDescriptor descriptor : mappedDescriptorList.getDescriptors()) {
                    TAPDescriptorService.getInstance().initializeColumns(descriptor, new IJSONRequestCallback() {
                        @Override
                        public void onSuccess(String responseText) {
                            TapDescriptorListMapper mapper = GWT.create(TapDescriptorListMapper.class);
                            TapDescriptorList mappedDesc  = mapper.read(responseText);
                            TapDescriptor tapDesc = mappedDesc.getDescriptors().get(0);
                            descriptor.setTapDescriptor(tapDesc);
                        }

                        @Override
                        public void onError(String errorCause) {
                            Log.error("[DescriptorRepository] initDescriptors ERROR: " + errorCause);
                        }
                    });
                }

                promise.fulfill(mappedDescriptorList);
            }

            @Override
            public void onError(String errorCause) {
                Log.error("[DescriptorRepository] initDescriptors ERROR: " + errorCause);
                promise.error();
            }
        });

    }

    public void setOutreachImageCountObserver(final CountObserver imageCountObserver) {
        this.imageCountObserver = imageCountObserver;
    }

    public void initImageDescriptors() {

//        Log.debug("[DescriptorRepository] Into DescriptorRepository.initImageDescriptors");
//        JSONUtils.getJSONFromUrl(EsaSkyWebConstants.IMAGES_URL, new IJSONRequestCallback() {
//
//            @Override
//            public void onSuccess(String responseText) {
//                ImageDescriptorListMapper mapper = GWT.create(ImageDescriptorListMapper.class);
//                DescriptorList<ImageDescriptor> mappedDescriptorList = mapper.read(responseText);
//
//                imageDescriptors = new DescriptorListAdapter<>(mappedDescriptorList,
//                        imageCountObserver);
//
//                for (ImageDescriptor desc : imageDescriptors.getDescriptors()) {
//                    if (desc.isHst()) {
//                        desc.setBaseUrl("https://esahubble.org/images/");
//                    } else {
//                        desc.setBaseUrl("https://esawebb.org/images/");
//                    }
//
//                    for (MetadataDescriptor md : desc.getMetadata()) {
//                        if (md.getType() == ColumnType.RA) {
//                            desc.setTapRaColumn(md.getTapName());
//                        } else if (md.getType() == ColumnType.DEC) {
//                            desc.setTapDecColumn(md.getTapName());
//                        } else if (EsaSkyWebConstants.S_REGION.equalsIgnoreCase(md.getTapName())) {
//                            desc.setTapSTCSColumn(md.getTapName());
//                        }
//                    }
//                }
//
//                Log.debug("[DescriptorRepository] [init image ]Total image entries: " + imageDescriptors.getTotal());
//                WavelengthUtils.setWavelengthRangeMaxMin(imageDescriptors.getDescriptors());
//
//                imageCountObserver.onCountUpdate(imageDescriptors.getTotal());
//            }
//
//            @Override
//            public void onError(String errorCause) {
//                Log.error("[DescriptorRepository] initImageDescriptors ERROR: " + errorCause);
//                DescriptorList<ImageDescriptor> list = new DescriptorList<ImageDescriptor>() {};
//                imageDescriptors = new DescriptorListAdapter<>(list, imageCountObserver);
//            }
//
//        });
    }

    public void initGwDescriptors(final CountObserver gwCountObserver) {
//        Log.debug("[DescriptorRepository] Into DescriptorRepository.initGwDescriptors");
//        JSONUtils.getJSONFromUrl(EsaSkyWebConstants.GW_URL, new IJSONRequestCallback() {
//
//            @Override
//            public void onSuccess(String responseText) {
//                GwDescriptorListMapper mapper = GWT.create(GwDescriptorListMapper.class);
//                DescriptorList<GwDescriptor> mappedDescriptorList = mapper.read(responseText);
//
//                gwDescriptors = new DescriptorListAdapter<>(mappedDescriptorList, gwCountObserver);
//
//                for (GwDescriptor desc : gwDescriptors.getDescriptors()) {
//                    for (MetadataDescriptor md : desc.getMetadata()) {
//                        if (md.getType() == ColumnType.RA) {
//                            desc.setTapRaColumn(md.getTapName());
//                        } else if (md.getType() == ColumnType.DEC) {
//                            desc.setTapDecColumn(md.getTapName());
//                        } else if (EsaSkyWebConstants.S_REGION.equalsIgnoreCase(md.getTapName())) {
//                            desc.setTapSTCSColumn(md.getTapName());
//                        }
//                    }
//                }
//
//                Log.debug("[DescriptorRepository] [init gw] Total gw entries: " + gwDescriptors.getTotal());
//                //WavelengthUtils.setWavelengthRangeMaxMin(gwDescriptors.getDescriptors());
//
//                gwCountObserver.onCountUpdate(gwDescriptors.getTotal());
//            }
//
//            @Override
//            public void onError(String errorCause) {
//                Log.error("[DescriptorRepository] initGwDescriptors ERROR: " + errorCause);
//                DescriptorList<GwDescriptor> list = new DescriptorList<GwDescriptor>() {};
//                gwDescriptors = new DescriptorListAdapter<>(list, gwCountObserver);
//            }
//
//        });
    }

    public void initIceCubeDescriptors(final CountObserver iceCubeCountObserver) {
//        Log.debug("[DescriptorRepository] Into DescriptorRepository.initIceCubeDescriptors");
//        JSONUtils.getJSONFromUrl(EsaSkyWebConstants.ICECUBE_URL, new IJSONRequestCallback() {
//
//            @Override
//            public void onSuccess(String responseText) {
//                IceCubeDescriptorListMapper mapper = GWT.create(IceCubeDescriptorListMapper.class);
//                DescriptorList<IceCubeDescriptor> mappedDescriptorList = mapper.read(responseText);
//
//                iceCubeDescriptors = new DescriptorListAdapter<>(mappedDescriptorList, iceCubeCountObserver);
//
//                for (IceCubeDescriptor desc : iceCubeDescriptors.getDescriptors()) {
//                    for (MetadataDescriptor md : desc.getMetadata()) {
//                        if (md.getType() == ColumnType.RA) {
//                            desc.setTapRaColumn(md.getTapName());
//                        } else if (md.getType() == ColumnType.DEC) {
//                            desc.setTapDecColumn(md.getTapName());
//                        } else if (EsaSkyWebConstants.S_REGION.equalsIgnoreCase(md.getTapName())) {
//                            desc.setTapSTCSColumn(md.getTapName());
//                        }
//                    }
//                }
//
//                Log.debug("[DescriptorRepository] [init iceCube] Total iceCube entries: " + iceCubeDescriptors.getTotal());
//                //WavelengthUtils.setWavelengthRangeMaxMin(gwDescriptors.getDescriptors());
//
//                iceCubeCountObserver.onCountUpdate(iceCubeDescriptors.getTotal());
//            }
//
//            @Override
//            public void onError(String errorCause) {
//                Log.error("[DescriptorRepository] initIceCubeDescriptor ERROR: " + errorCause);
//                DescriptorList<IceCubeDescriptor> list = new DescriptorList<IceCubeDescriptor>() {};
//                iceCubeDescriptors = new DescriptorListAdapter<>(list, iceCubeCountObserver);
//            }
//
//        });
    }

    public void initSSODescriptors(final CountObserver ssoCountObserver) {
//
//        Log.debug("[DescriptorRepository] Into DescriptorRepository.initSSODescriptors");
//        JSONUtils.getJSONFromUrl(EsaSkyWebConstants.SSO_URL, new IJSONRequestCallback() {
//
//            @Override
//            public void onSuccess(String responseText) {
//
//                SSODescriptorListMapper mapperSSO = GWT.create(SSODescriptorListMapper.class);
//                SSODescriptorList ssoMappedDescriptorList = mapperSSO.read(responseText);
//                ssoDescriptors = new DescriptorListAdapter<SSODescriptor>(ssoMappedDescriptorList, ssoCountObserver);
//
//
//                Log.debug("[DescriptorRepository] [initSSODescriptors] Total observation entries: " + ssoDescriptors.getTotal());
//                WavelengthUtils.setWavelengthRangeMaxMin(ssoDescriptors.getDescriptors());
//                if (!GUISessionStatus.getIsInScienceMode()) {
//                    GUISessionStatus.setDoCountOnEnteringScienceMode();
//                }
//            }
//
//            @Override
//            public void onError(String errorCause) {
//                Log.error("[DescriptorRepository] initSSODescriptors ERROR: " + errorCause);
//                checkDoCountAll();
//            }
//
//        });
    }



    public void initPubDescriptors() {
//        Log.debug("[DescriptorRepository] Into DescriptorRepository.initPubDescriptors");
//
//        JSONUtils.getJSONFromUrl(EsaSkyWebConstants.PUBLICATIONS_URL, new IJSONRequestCallback() {
//
//            @Override
//            public void onSuccess(String responseText) {
//                PublicationsDescriptorListMapper mapper = GWT.create(PublicationsDescriptorListMapper.class);
//                publicationsDescriptors = new DescriptorListAdapter<PublicationsDescriptor>(mapper.read(responseText),
//                        new CountObserver() {
//
//                            @Override
//                            public void onCountUpdate(long newCount) {
//                            }
//                        });
//                for (PublicationDescriptorLoadObserver observer : publicationDescriptorLoadObservers) {
//                    observer.onLoad();
//                }
//
//                Log.debug("[DescriptorRepository] Total publications entries: " + publicationsDescriptors.getTotal());
//            }
//
//            @Override
//            public void onError(String errorCause) {
//                Log.error("[DescriptorRepository] initPubDescriptors ERROR: " + errorCause);
//            }
//
//        });
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
//        double fov = CoordinateUtils.getCenterCoordinateInJ2000().getFov();
//        CommonEventBus.getEventBus().fireEvent(new ExtTapFovEvent(fov));
//        if (fov < EsaSkyWebConstants.EXTTAP_FOV_LIMIT) {
//            for (ExtTapDescriptor descriptor : extTapDescriptors.getDescriptors()) {
//                if (EsaSkyConstants.TREEMAP_LEVEL_SERVICE == descriptor.getTreeMapLevel()) {
//                    if (extTapDescriptors.getCountStatus().hasMoved(descriptor)) {
//                        updateCount4ExtTap(descriptor);
//                    }
//                }
//            }
//        }
    }

    public void updateCount4ExtTap(ExtTapDescriptor descriptor) {
//        final CountStatus cs = extTapDescriptors.getCountStatus();
//        if (!cs.containsDescriptor(descriptor)) {
//            cs.addDescriptor(descriptor);
//        }
//
//        String adql = TAPExtTapService.getInstance().getCountAdql(descriptor);
//        String url = descriptor.getTapQuery(EsaSkyWebConstants.EXT_TAP_REQUEST_URL, adql, descriptor.getResponseFormat());
//
//        JSONUtils.getJSONFromUrl(url, new ExtTapCheckCallback(adql, descriptor, cs,
//                countRequestHandler.getProgressIndicatorMessage() + " " + descriptor.getMission()));
    }

    public void doCountExtTap(IDescriptor descriptor, CountStatus cs) {

    }

    public void doCountSSO(String ssoName, ESASkySSOObjType ssoType) {

//        String url = TAPUtils.getTAPQuery(URL.encodeQueryString(TAPSSOService.getInstance().getCount(ssoName, ssoType)),
//                EsaSkyConstants.JSON);
//
//        Log.debug("[doCountSSO] SSO count Query [" + url + "]");
//        JSONUtils.getJSONFromUrl(url,
//                new SsoCountRequestCallback(ssoDescriptors, ssoName, ssoType));
    }

    public void updateSearchArea(SearchArea area) {
        this.searchArea = area;
        for (DescriptorCountAdapter countAdapters : descriptorCountAdapterMap.values()) {
            for (CommonTapDescriptor descriptor : countAdapters.getDescriptors()) {
                descriptor.setSearchArea(area);
            }
        }

        doCountAll();
    }

    public SearchArea getSearchArea() {
        return this.searchArea;
    }

    public boolean hasSearchArea() {
        return this.searchArea != null;
    }

    private static long lastestSingleCountTimecall;

    private void requestSingleCount() {

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


    // TODO: Kolla denna (session använder)
    public CommonTapDescriptor getDescriptorFromTable(String tableName, String mission) {
        String category = tableCategoryMap.get(tableName);
        if (category != null) {
            return getDescriptorCountAdapter(category).getDescriptorByMission(mission);
        }

    	return null;
    }
    
//    private HashMap<String, List<CommonTapDescriptor>> getDescriptorsMap() {
//        if (descriptorsMap == null) {
//            prepareDescriptorsMap();
//        }
//        return descriptorsMap;
//    }

//    private void prepareDescriptorsMap() {
//        descriptorsMap = new HashMap<>();
//        countStatusMap = new HashMap<>();
//
////        addDescriptorsToHashMaps(catDescriptors);
//        addDescriptorsToHashMaps(observationDescriptors);
////        addDescriptorsToHashMaps(spectraDescriptors);
////        addDescriptorsToHashMaps(publicationsDescriptors);
////        addDescriptorsToHashMaps(imageDescriptors);
////        addDescriptorsToHashMaps(extTapDescriptors);
//    }

//    private void addDescriptorsToHashMaps(DescriptorCountAdapter descriptorListAdapter) {
//        if (descriptorListAdapter != null) {
//            final CountStatus cs = descriptorListAdapter.getCountStatus();
//            for (CommonTapDescriptor descriptor : descriptorListAdapter.getTapDescriptorList().getDescriptors()) {
//                if (!descriptorsMap.containsKey(descriptor.getTableName())) {
//                    descriptorsMap.put(descriptor.getTableName(), new LinkedList<>());
//                }
//                if (!countStatusMap.containsKey(descriptor.getTableName())) {
//                    countStatusMap.put(descriptor.getTableName(), new LinkedList<>());
//                }
//                List<CommonTapDescriptor> descriptorList = descriptorsMap.get(descriptor.getTableName());
//                List<CountStatus> countStatusList = countStatusMap.get(descriptor.getTableName());
//                descriptorList.add(descriptor);
//                countStatusList.add(cs);
//            }
//        }
//    }

        private void doUpdateSingleCount(List<SingleCount> singleCountList, final SkyViewPosition skyViewPosition) {

        ArrayList<String> remainingDescriptors = new ArrayList<>(tableCategoryMap.keySet());

        List<CommonTapDescriptor> descriptors = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();

        setCount(singleCountList, skyViewPosition, remainingDescriptors, descriptors, counts);
        //Handling that the fast count doesn't give any results for missing missions in the area, so we set them to 0
        setZeroCountOnNoResponseMissions(skyViewPosition, remainingDescriptors, descriptors, counts);

        if (!descriptors.isEmpty()) {
            notifyCountChange(descriptors, counts);
        }
    }

    private void notifyCountChange(List<CommonTapDescriptor> descriptors, List<Integer> counts) {
        CommonEventBus.getEventBus().fireEvent(new TreeMapNewDataEvent(descriptorCountAdapterMap.values()));

        for (DescriptorCountAdapter dca : descriptorCountAdapterMap.values()) {
            dca.getCountStatus().updateCount();
        }
    }

    private void setCount(List<SingleCount> singleCountList, final SkyViewPosition skyViewPosition,
                          ArrayList<String> remainingDescriptors, List<CommonTapDescriptor> descriptors, List<Integer> counts) {

        for (SingleCount singleCount : singleCountList) {
            String category = tableCategoryMap.get(singleCount.getTableName());
            if (category != null) {
                DescriptorCountAdapter descriptorCountAdapter = descriptorCountAdapterMap.get(category);
                CountStatus cs = descriptorCountAdapter.getCountStatus();
                CommonTapDescriptor descriptor = descriptorCountAdapter.getDescriptorByTable(singleCount.getTableName());

                if (descriptor != null) {
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

    private void setZeroCountOnNoResponseMissions(final SkyViewPosition skyViewPosition, ArrayList<String> remainingDescriptors,
                                                  List<CommonTapDescriptor> descriptors, List<Integer> counts) {
        for (String tableName : remainingDescriptors) {
            String category = tableCategoryMap.get(tableName);
            DescriptorCountAdapter descriptorCountAdapter = descriptorCountAdapterMap.get(category);

            final int count = 0;
            for (CommonTapDescriptor descriptor : descriptorCountAdapter.getDescriptors()) {
                CountStatus cs = descriptorCountAdapter.getCountStatus();
                cs.setCountDetails(descriptor, count, System.currentTimeMillis(), skyViewPosition);
                descriptors.add(descriptor);
                counts.add(count);
            }
        }
    }

    public CommonTapDescriptor initUserDescriptor(List<TapMetadataDescriptor> metadataList, IJSONWrapper jsonWrapper, GeneralSkyObject generalSkyObject) {
        TapDescriptor tapDescriptor = new TapDescriptor();
        tapDescriptor.setMetadata(metadataList);

        tapDescriptor.setColor(jsonWrapper.getOverlaySet().getColor());
        tapDescriptor.setProperties(tapDescriptor.getDecColumn(), generalSkyObject.getDec_deg());
        tapDescriptor.setProperties(tapDescriptor.getRaColumn(), generalSkyObject.getRa_deg());
        tapDescriptor.setProperties(tapDescriptor.getLongNameColumn(), generalSkyObject.getName());
        tapDescriptor.setProperties(tapDescriptor.getShortNameColumn(), generalSkyObject.getName());
        tapDescriptor.setProperties(tapDescriptor.getIdColumn(), generalSkyObject.getId());


        CommonTapDescriptor commonTapDescriptor = new CommonTapDescriptor();
        commonTapDescriptor.setTapDescriptor(tapDescriptor);
        commonTapDescriptor.setColumns(metadataList);
        commonTapDescriptor.setLongName(jsonWrapper.getOverlaySet().getOverlayName());
        commonTapDescriptor.setShortName(jsonWrapper.getOverlaySet().getOverlayName());
        commonTapDescriptor.setMission(jsonWrapper.getOverlaySet().getOverlayName());
        commonTapDescriptor.setSampEnabled(false);
        commonTapDescriptor.setColor(jsonWrapper.getOverlaySet().getColor());

        if (jsonWrapper instanceof FootprintListJSONWrapper) {
            commonTapDescriptor.setCategory(EsaSkyWebConstants.CATEGORY_OBSERVATIONS);
        } else if (jsonWrapper instanceof SourceListJSONWrapper) {
            commonTapDescriptor.setCategory(EsaSkyWebConstants.CATEGORY_CATALOGUES);
        }

        return commonTapDescriptor;
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