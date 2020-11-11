package esac.archive.esasky.cl.web.client.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.allen_sauer.gwt.log.client.Log;
import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;

import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.BaseDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.CatalogDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.CatalogDescriptorList;
import esac.archive.esasky.ifcs.model.descriptor.DescriptorList;
import esac.archive.esasky.ifcs.model.descriptor.ExtTapDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.ExtTapDescriptorList;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.MetadataDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.ObservationDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.ObservationDescriptorList;
import esac.archive.esasky.ifcs.model.descriptor.PublicationsDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.PublicationsDescriptorList;
import esac.archive.esasky.ifcs.model.descriptor.SSODescriptor;
import esac.archive.esasky.ifcs.model.descriptor.SSODescriptorList;
import esac.archive.esasky.ifcs.model.descriptor.SpectraDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.SpectraDescriptorList;
import esac.archive.esasky.ifcs.model.descriptor.UserCatalogueDescriptor;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;
import esac.archive.esasky.ifcs.model.shared.ColumnType;
import esac.archive.esasky.ifcs.model.shared.ESASkyColors;
import esac.archive.esasky.ifcs.model.shared.ESASkySSOSearchResult.ESASkySSOObjType;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.Modules;
import esac.archive.esasky.cl.web.client.api.APIMetadataConstants;
import esac.archive.esasky.cl.web.client.api.model.FootprintListJSONWrapper;
import esac.archive.esasky.cl.web.client.api.model.IJSONWrapper;
import esac.archive.esasky.cl.web.client.api.model.SourceListJSONWrapper;
import esac.archive.esasky.cl.web.client.callback.CountRequestCallback;
import esac.archive.esasky.cl.web.client.callback.ExtTapCheckCallback;
import esac.archive.esasky.cl.web.client.callback.ICountRequestHandler;
import esac.archive.esasky.cl.web.client.callback.JsonRequestCallback;
import esac.archive.esasky.cl.web.client.callback.SsoCountRequestCallback;
import esac.archive.esasky.cl.web.client.event.ExtTapFovEvent;
import esac.archive.esasky.cl.web.client.event.ExtTapToggleEvent;
import esac.archive.esasky.cl.web.client.event.ExtTapToggleEventHandler;
import esac.archive.esasky.cl.web.client.event.TreeMapNewDataEvent;
import esac.archive.esasky.cl.web.client.utility.ExtTapUtils;
import esac.archive.esasky.cl.web.client.model.SingleCount;
import esac.archive.esasky.cl.web.client.model.TapRowList;
import esac.archive.esasky.cl.web.client.presenter.ResultsPresenter.TapRowListMapper;
import esac.archive.esasky.cl.web.client.query.TAPExtTapService;
import esac.archive.esasky.cl.web.client.query.TAPCatalogueService;
import esac.archive.esasky.cl.web.client.query.TAPObservationService;
import esac.archive.esasky.cl.web.client.query.TAPSSOService;
import esac.archive.esasky.cl.web.client.query.TAPSingleCountService;
import esac.archive.esasky.cl.web.client.query.TAPUtils;
import esac.archive.esasky.cl.web.client.status.CountObserver;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.cl.web.client.status.GUISessionStatus;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.CoordinateUtils;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.JSONUtils;
import esac.archive.esasky.cl.web.client.utility.WavelengthUtils;
import esac.archive.esasky.cl.web.client.utility.JSONUtils.IJSONRequestCallback;

public class DescriptorRepository {

	/** Descriptor List of adapter. */
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

	public interface SingleCountListMapper extends ObjectMapper<List<SingleCount>> {
	}

	private DescriptorListAdapter<CatalogDescriptor> catDescriptors;
	private DescriptorListAdapter<ObservationDescriptor> obsDescriptors;
	private DescriptorListAdapter<SSODescriptor> ssoDescriptors;
	private DescriptorListAdapter<SpectraDescriptor> spectraDescriptors;
	private DescriptorListAdapter<PublicationsDescriptor> publicationsDescriptors;
	private DescriptorListAdapter<ExtTapDescriptor> extTapDescriptors;

	/** Descriptor and CountStatus hashMaps for improve counts */
	private HashMap<String, List<IDescriptor>> descriptorsMap; 
	private HashMap<String, List<CountStatus>> countStatusMap;

	private boolean catDescriptorsIsReady = false;
	private boolean obsDescriptorsIsReady = false;
	private boolean spectraDescriptorsIsReady = false;

	private final boolean isInitialPositionDescribedInCoordinates;
	private boolean isExtTapOpen = false;
	
	private static final String NOT_SET = "<not_set>";
	private static final String COLOR_STRING = "color";

	private ICountRequestHandler countRequestHandler;
	
	private static DescriptorRepository _instance; 
	
	private LinkedList<PublicationDescriptorLoadObserver> publicationDescriptorLoadObservers = new LinkedList<PublicationDescriptorLoadObserver>();
	public interface PublicationDescriptorLoadObserver{
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
				
				for(ExtTapDescriptor tapService : extTapDescriptors.getDescriptors()) {
					tapService.setInBackend(true);
					descriptorsList.add(tapService);
					counts.add(0);
					Map<String, ExtTapDescriptor> addedProductTypes = new HashMap<>();
					
					for(String facilityName : tapService.getCollections().keySet()) {
						Map<String, ArrayList<String>> facility = tapService.getCollections().get(facilityName);
						
						for(String dataproductType : facility.get(EsaSkyConstants.OBSCORE_DATAPRODUCT)) {
							
							ExtTapDescriptor dataDesc;
							if(addedProductTypes.containsKey(dataproductType)) {
								dataDesc = addedProductTypes.get(dataproductType);
							}else {
								dataDesc = ExtTapUtils.createDataproductDescriptor(tapService, dataproductType);
								addedProductTypes.put(dataproductType, dataDesc);
								descriptorsList.add(dataDesc);
								counts.add(0);
							}

							IDescriptor collectionDesc = ExtTapUtils.createCollectionDescriptor(tapService, dataDesc, facilityName);
							
							if(facility.containsKey("range")) {
								double minWavelength = Double.parseDouble(facility.get("range").get(0));
								double maxWavelength = Double.parseDouble(facility.get("range").get(1));
								
								collectionDesc.setWavelengths(WavelengthUtils.createWavelengthDescriptor(minWavelength, maxWavelength));
								collectionDesc.setPrimaryColor(ESASkyColors.getColorFromWavelength((maxWavelength + minWavelength) / 2));
							}else if(facility.containsKey(COLOR_STRING)) {
								collectionDesc.setPrimaryColor(facility.get(COLOR_STRING).get(0));
							}
							else {
								collectionDesc.setPrimaryColor(ESASkyColors.getNext());
							}
							
							descriptorsList.add(collectionDesc);
							counts.add(0);
						}
						
					}
				}
				
				CommonEventBus.getEventBus().fireEvent(new TreeMapNewDataEvent(descriptorsList, counts));
				
				for(IDescriptor descriptor : descriptorsList) {
					if(extTapDescriptors.getDescriptorByMissionNameCaseInsensitive(descriptor.getMission()) == null) {
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
		if(descriptor == null) {
			descriptor = new ExtTapDescriptor();
		}
		
		descriptor.setGuiShortName(name);
		descriptor.setGuiLongName(name);
		descriptor.setMission(name);
		descriptor.setCreditedInstitutions(name);
		descriptor.setTapRaColumn("s_ra");
		descriptor.setTapDecColumn("s_dec");
		descriptor.setTapSTCSColumn("s_region");
		descriptor.setFovLimit(180.0);
		descriptor.setShapeLimit(3000);
		descriptor.setTapUrl(tapUrl);
		descriptor.setUniqueIdentifierField("obs_id");
		if(dataOnlyInView) {
			descriptor.setSearchFunction("polygonIntersect");
		}else {
			descriptor.setSearchFunction("");
		}
		descriptor.setResponseFormat("VOTable");
		descriptor.setInBackend(false);
		
		adql = adql.replace("from", "FROM");
		adql = adql.replace("where", "WHERE");
		String[] whereSplit = adql.split("WHERE");
		if(whereSplit.length > 1) {
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
				
				for(ObservationDescriptor desc : obsDescriptors.getDescriptors()) {
					for(MetadataDescriptor md : desc.getMetadata()) {
						if(md.getType() == ColumnType.RA) {
							desc.setTapRaColumn(md.getTapName());
						}
						else if(md.getType() == ColumnType.DEC) {
							desc.setTapDecColumn(md.getTapName());
						}
						else if(md.getTapName().equalsIgnoreCase("s_region")){
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

	public void initSSODescriptors(final CountObserver ssoCountObserver) {

		Log.debug("[DescriptorRepository] Into DescriptorRepository.initSSODescriptors");
		JSONUtils.getJSONFromUrl(EsaSkyWebConstants.SSO_URL, new IJSONRequestCallback() {

			@Override
			public void onSuccess(String responseText) {

				SSODescriptorListMapper mapperSSO = GWT.create(SSODescriptorListMapper.class);
				SSODescriptorList ssoMappedDescriptorList = mapperSSO.read(responseText);
				// ssoMappedDescriptorList.setIsSsoDescriptors();
				ssoDescriptors = new DescriptorListAdapter<SSODescriptor>(ssoMappedDescriptorList, ssoCountObserver);

				// obsDescriptorsIsReady = true;

				Log.debug("[DescriptorRepository] [initSSODescriptors] Total observation entries: " + ssoDescriptors.getTotal());
				WavelengthUtils.setWavelengthRangeMaxMin(ssoDescriptors.getDescriptors());
				if (!GUISessionStatus.getIsInScienceMode()) {
					GUISessionStatus.setDoCountOnEnteringScienceMode();
				}
			}

			@Override
			public void onError(String errorCause) {
				Log.error("[DescriptorRepository] initSSODescriptors ERROR: " + errorCause);
				// obsDescriptorsIsReady = true;
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
						
						
						for(SpectraDescriptor desc : spectraDescriptors.getDescriptors()) {
							for(MetadataDescriptor md : desc.getMetadata()) {
								if(md.getType() == ColumnType.RA) {
									desc.setTapRaColumn(md.getTapName());
								}
								else if(md.getType() == ColumnType.DEC) {
									desc.setTapDecColumn(md.getTapName());
								}
								else if(md.getTapName().equalsIgnoreCase("s_region")){
									desc.setTapSTCSColumn(md.getTapName());
								}
							}
						}
						
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

	public void initPubDescriptors() {
		Log.debug("[DescriptorRepository] Into DescriptorRepository.initPubDescriptors");

		JSONUtils.getJSONFromUrl(EsaSkyWebConstants.PUBLICATIONS_URL, new IJSONRequestCallback() {

			@Override
			public void onSuccess(String responseText) {
				PublicationsDescriptorListMapper mapper = GWT.create(PublicationsDescriptorListMapper.class);
				publicationsDescriptors = new DescriptorListAdapter<PublicationsDescriptor>(mapper.read(responseText),
						new CountObserver() {
							
							@Override
							public void onCountUpdate(int newCount) {
							}
						});
				for(PublicationDescriptorLoadObserver observer : publicationDescriptorLoadObservers) {
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

		if (EsaSkyWebConstants.SINGLE_COUNT_ENABLED) {
			// Single dynamic count
			requestSingleCount();	
			if(isExtTapOpen) {
				updateCount4AllExtTaps();
			}

		} else {

			// Normal dynamic count per mission
			updateCount4Catalogs();
			updateCount4Observations();

			if (Modules.spectraModule) {
				updateCount4Spectras();
			}

			if(isExtTapOpen) {
				updateCount4AllExtTaps();
			}
			
		}
	}
	
	public void updateCount4AllExtTaps() {
		double fov = CoordinateUtils.getCenterCoordinateInJ2000().getFov();
		CommonEventBus.getEventBus().fireEvent(new ExtTapFovEvent(fov));
		if(fov < EsaSkyWebConstants.EXTTAP_FOV_LIMIT) {
			for(ExtTapDescriptor descriptor : extTapDescriptors.getDescriptors()) {
				if(descriptor.getTreeMapType() == EsaSkyConstants.TREEMAP_TYPE_SERVICE) {
					if(extTapDescriptors.getCountStatus().hasMoved(descriptor)) {
						updateCount4ExtTap(descriptor);
					}
				}
			}
		}
	}
	
	public void updateCount4ExtTap(ExtTapDescriptor descriptor) {
		final CountStatus cs = extTapDescriptors.getCountStatus();
		if(!cs.containsDescriptor(descriptor)) {
			cs.addDescriptor(descriptor);
		}
			
		String adql = TAPExtTapService.getInstance().getCountAdql(descriptor);
		String url = descriptor.getTapQuery(EsaSkyWebConstants.EXT_TAP_REQUEST_URL, adql, descriptor.getResponseFormat());
		
		JSONUtils.getJSONFromUrl(url, new ExtTapCheckCallback(adql, descriptor, cs,
					countRequestHandler.getProgressIndicatorMessage() + " " + descriptor.getMission()));
	}
	
	private void updateCount4Catalogs() {
		final CountStatus cs = catDescriptors.getCountStatus();
		for (CatalogDescriptor currCat : catDescriptors.getDescriptors()) {
			doUpdateCount(currCat, cs);
		}
	}

	private void updateCount4Observations() {
		final CountStatus cs = obsDescriptors.getCountStatus();
		for (ObservationDescriptor currObs : obsDescriptors.getDescriptors()) {
			doUpdateCount(currObs, cs);
		}
	}

	private void updateCount4Spectras() {
		final CountStatus cs = spectraDescriptors.getCountStatus();
		for (SpectraDescriptor currSpectra : spectraDescriptors.getDescriptors()) {
			doUpdateCount(currSpectra, cs);
		}
	}

	private final void doUpdateCount(IDescriptor descriptor, CountStatus cs) {

		Log.debug("[doUpdateCount][" + descriptor.getGuiShortName() + "]");

		String url;

		if (descriptor instanceof CatalogDescriptor) {

			url = TAPCatalogueService.getInstance().getCount(AladinLiteWrapper.getAladinLite(), descriptor);

		} else {

			url = TAPObservationService.getInstance().getCount(AladinLiteWrapper.getAladinLite(), descriptor);
		}

		JSONUtils.getJSONFromUrl(url, new CountRequestCallback(descriptor, cs, countRequestHandler, url));
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

	private static long lastestSingleCountTimecall;

	private final void requestSingleCount() {

		final SkyViewPosition skyViewPosition = CoordinateUtils.getCenterCoordinateInJ2000();
		
		final long timecall = System.currentTimeMillis();
		lastestSingleCountTimecall = timecall;
		String url = TAPUtils.getTAPQuery(URL.encodeQueryString(
				TAPSingleCountService.getInstance().getCount(AladinLiteWrapper.getAladinLite())), EsaSkyConstants.JSON);
		JSONUtils.getJSONFromUrl(url, new JsonRequestCallback(countRequestHandler.getProgressIndicatorMessage(), url) {

			@Override
			protected void onSuccess(Response response) {
				try {
					if (timecall < lastestSingleCountTimecall) {
						Log.warn(this.getClass().getSimpleName() + " discarded server answer with timecall=" + timecall
								+ " , dif:" + (lastestSingleCountTimecall - timecall));
						return;
					}
					TapRowListMapper mapper = GWT.create(TapRowListMapper.class);
					TapRowList rowList = mapper.read(response.getText());

					if (rowList.getData().size() > 0) {

						SingleCountListMapper scMapper = GWT.create(SingleCountListMapper.class);
						List<SingleCount> singleCountList = scMapper
								.read(rowList.getDataValue("esasky_dynamic_count", 0));

						doUpdateSingleCount(singleCountList, skyViewPosition);

					} else {
						Log.warn("[DescriptorRepository] requestSingleCount. TapRowList is empty!");
					}

				} catch (Exception ex) {
					Log.error("[DescriptorRepository] requestSingleCount.onSuccess ERROR: " + ex.getMessage(), ex);
				}
			}
			@Override
			public void onError(Request request, Throwable exception) {
				super.onError(request, exception);
				List<IDescriptor> descriptors = new ArrayList<IDescriptor>();
				List<Integer> counts = new ArrayList<Integer>();
				
				for(List<IDescriptor> descriptorList : descriptorsMap.values()) {
				    for(IDescriptor descriptor : descriptorList) {
				        if(descriptors.contains(descriptor)) {
				    
        					List<CountStatus> countStatuses = countStatusMap.get(descriptor.getTapTable());
        					for(CountStatus cs: countStatuses) {
            					final int count = 0;
            					cs.setCountDetails(descriptor, count, System.currentTimeMillis(), skyViewPosition);
            		
        						descriptors.add(descriptor);
        						counts.add(count);
        					}
				        }
				    }
				}
				if (descriptors.size() > 0) {
					CommonEventBus.getEventBus().fireEvent(new TreeMapNewDataEvent(descriptors, counts));
					for (String key : countStatusMap.keySet()) {
					    for(CountStatus cs: countStatusMap.get(key)) {
					        cs.updateCount();
					    }
					}
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
	}

	private void addDescriptorsToHashMaps(DescriptorListAdapter<?> descriptorListAdapter) {
		if (descriptorListAdapter != null) {
			final CountStatus cs = descriptorListAdapter.getCountStatus();
			for (IDescriptor descriptor : descriptorListAdapter.getDescriptors()) {
			    if(!descriptorsMap.containsKey(descriptor.getTapTable())) {
			        descriptorsMap.put(descriptor.getTapTable(), new LinkedList<IDescriptor>());
			    }
			    if(!countStatusMap.containsKey(descriptor.getTapTable())) {
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
		
		ArrayList<String> remainingDescriptors = new ArrayList<String>();
		for(String key : descriptorsMap.keySet()) {
			remainingDescriptors.add(key);
		}

		List<IDescriptor> descriptors = new ArrayList<IDescriptor>();
		List<Integer> counts = new ArrayList<Integer>();

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
            for(CountStatus cs: countStatusMap.get(key)) {
                if(!statusToBeUpdated.contains(cs)) {
                	statusToBeUpdated.add(cs);
                }
            }
        }
        for(CountStatus cs: statusToBeUpdated) {
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
				for(IDescriptor descriptor : descriptorList) {
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
        for(String mission : remainingDescriptors) {
			List<IDescriptor> descriptorList = descriptorsMap.get(mission);
			List<CountStatus> countStatusList = countStatusMap.get(mission);
            int i = 0;
            for(IDescriptor descriptor : descriptorList) {
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
		ObservationDescriptor descriptor = new ObservationDescriptor();

		descriptor.setMetadata(metadata);

		descriptor.setMission(footprintsSet.getOverlaySet().getOverlayName());
		descriptor.setGuiLongName(footprintsSet.getOverlaySet().getOverlayName());
		descriptor.setGuiShortName(footprintsSet.getOverlaySet().getOverlayName());
		descriptor.setDescriptorId(footprintsSet.getOverlaySet().getOverlayName());
		descriptor.setPrimaryColor(footprintsSet.getOverlaySet().getColor());
		
		descriptor.setUniqueIdentifierField(APIMetadataConstants.OBS_NAME);
		
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
		if(options.hasProperty(COLOR_STRING)) {
			descriptor.setPrimaryColor(options.getStringProperty(COLOR_STRING));
		}else {
			descriptor.setPrimaryColor(ESASkyColors.getNext());
		}
		
		descriptor.setUniqueIdentifierField(APIMetadataConstants.OBS_NAME);
		
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

		descriptor.setTapRaColumn(APIMetadataConstants.CENTER_RA_DEG);
		descriptor.setTapDecColumn(APIMetadataConstants.CENTER_DEC_DEG);
		descriptor.setUniqueIdentifierField(APIMetadataConstants.CAT_NAME);

		return descriptor;
	}

	public void registerExtTapObserver() {
		
		CommonEventBus.getEventBus().addHandler(ExtTapToggleEvent.TYPE,
                new ExtTapToggleEventHandler() {
					
            @Override
            public void onToggle(final ExtTapToggleEvent event) {
            	boolean wasOpen = isExtTapOpen;
            	isExtTapOpen = event.isOpen();
            	if(!wasOpen && isExtTapOpen) {
            		updateCount4AllExtTaps();
            	}
            }
        });
	}
	
	public void addPublicationDescriptorLoadObserver(PublicationDescriptorLoadObserver observer) {
		publicationDescriptorLoadObservers.add(observer);
	}
}