package esac.archive.esasky.cl.web.client.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;

import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
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
import esac.archive.esasky.cl.web.client.callback.ISSOCountRequestHandler;
import esac.archive.esasky.cl.web.client.callback.JsonRequestCallback;
import esac.archive.esasky.cl.web.client.callback.SsoCountRequestCallback;
import esac.archive.esasky.cl.web.client.event.TreeMapNewDataEvent;
import esac.archive.esasky.cl.web.client.model.SingleCount;
import esac.archive.esasky.cl.web.client.model.TapRowList;
import esac.archive.esasky.cl.web.client.presenter.ResultsPresenter.TapRowListMapper;
import esac.archive.esasky.cl.web.client.query.TAPCountCatalogueService;
import esac.archive.esasky.cl.web.client.query.TAPCountObservationService;
import esac.archive.esasky.cl.web.client.query.TAPCountPublicationsService;
import esac.archive.esasky.cl.web.client.query.TAPCountSSOService;
import esac.archive.esasky.cl.web.client.query.TAPExtTapService;
import esac.archive.esasky.cl.web.client.query.TAPSingleCountService;
import esac.archive.esasky.cl.web.client.query.TAPUtils;
import esac.archive.esasky.cl.web.client.status.CountObserver;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.cl.web.client.status.GUISessionStatus;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.CoordinateUtils;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.JSONUtils;
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
	private HashMap<String, IDescriptor> descriptorsMap;
	private HashMap<String, CountStatus> countStatusMap;

	private boolean extTapDescriptorsIsReady = false;
	private boolean catDescriptorsIsReady = false;
	private boolean obsDescriptorsIsReady = false;
	private boolean spectraDescriptorsIsReady = false;
	private boolean publicationsDescriptorsIsReady = false;

	private final boolean isInitialPositionDescribedInCoordinates;

	private ICountRequestHandler countRequestHandler;
	
	private LinkedList<PublicationDescriptorLoadObserver> publicationDescriptorLoadObservers = new LinkedList<PublicationDescriptorLoadObserver>();
	public interface PublicationDescriptorLoadObserver{
		void onLoad();
	}

	public DescriptorRepository(boolean isInitialPositionDescribedInCoordinates) {
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
		JSONUtils.getJSONFromUrl(URL.encode(EsaSkyWebConstants.EXT_TAP_GET_TAPS_URL), new IJSONRequestCallback() {

			@Override
			public void onSuccess(String responseText) {
				ExternalTapDescriptorListMapper mapper = GWT.create(ExternalTapDescriptorListMapper.class);
				extTapDescriptors = new DescriptorListAdapter<ExtTapDescriptor>(mapper.read(responseText), countObserver);
				extTapDescriptorsIsReady = true;

				Log.debug("[DescriptorRepository] Total extTap entries: " + extTapDescriptors.getTotal());
			}

			@Override
			public void onError(String errorCause) {
				Log.error("[DescriptorRepository] initExtDescriptors ERROR: " + errorCause);
				extTapDescriptorsIsReady = true;
			}

		});
	}
	

	public void initCatDescriptors(final CountObserver countObserver) {

		Log.debug("[DescriptorRepository] Into DescriptorRepository.initCatDescriptors");
		JSONUtils.getJSONFromUrl(URL.encode(EsaSkyWebConstants.CATALOGS_URL), new IJSONRequestCallback() {

			@Override
			public void onSuccess(String responseText) {
				CatalogDescriptorListMapper mapper = GWT.create(CatalogDescriptorListMapper.class);
				catDescriptors = new DescriptorListAdapter<CatalogDescriptor>(mapper.read(responseText), countObserver);
				catDescriptorsIsReady = true;

				Log.debug("[DescriptorRepository] Total catalog entries: " + catDescriptors.getTotal());
				if (GUISessionStatus.getIsInScienceMode()) {
					if (!EsaSkyWebConstants.SINGLE_COUNT_ENABLED) {
						updateCount4Catalogs();
					} else {
						checkDoCountAll();
					}
				} else {
					GUISessionStatus.setDoCountOnEnteringScienceMode();
				}
			}

			@Override
			public void onError(String errorCause) {
				Log.error("[DescriptorRepository] initCatDescriptors ERROR: " + errorCause);
				catDescriptorsIsReady = true;
				checkDoCountAll();
			}

		});
	}

	public void initObsDescriptors(final CountObserver obsCountObserver) {

		Log.debug("[DescriptorRepository] Into DescriptorRepository.initObsDescriptors");
		JSONUtils.getJSONFromUrl(URL.encode(EsaSkyWebConstants.OBSERVATIONS_URL), new IJSONRequestCallback() {

			@Override
			public void onSuccess(String responseText) {
				ObservationDescriptorListMapper mapper = GWT.create(ObservationDescriptorListMapper.class);
				ObservationDescriptorList mappedDescriptorList = mapper.read(responseText);

				obsDescriptors = new DescriptorListAdapter<ObservationDescriptor>(mappedDescriptorList,
						obsCountObserver);
				obsDescriptorsIsReady = true;

				Log.debug("[DescriptorRepository] [init obs]Total observation entries: " + obsDescriptors.getTotal());
				if (GUISessionStatus.getIsInScienceMode()) {
					if (!EsaSkyWebConstants.SINGLE_COUNT_ENABLED) {
						updateCount4Observations();
					} else {
						checkDoCountAll();
					}
				} else {
					GUISessionStatus.setDoCountOnEnteringScienceMode();
				}
			}

			@Override
			public void onError(String errorCause) {
				Log.error("[DescriptorRepository] initObsDescriptors ERROR: " + errorCause);
				obsDescriptorsIsReady = true;
				checkDoCountAll();
			}

		});
	}

	public void initSSODescriptors(final CountObserver ssoCountObserver) {

		Log.debug("[DescriptorRepository] Into DescriptorRepository.initSSODescriptors");
		JSONUtils.getJSONFromUrl(URL.encode(EsaSkyWebConstants.SSO_URL), new IJSONRequestCallback() {

			@Override
			public void onSuccess(String responseText) {

				SSODescriptorListMapper mapperSSO = GWT.create(SSODescriptorListMapper.class);
				SSODescriptorList ssoMappedDescriptorList = mapperSSO.read(responseText);
				// ssoMappedDescriptorList.setIsSsoDescriptors();
				ssoDescriptors = new DescriptorListAdapter<SSODescriptor>(ssoMappedDescriptorList, ssoCountObserver);

				// obsDescriptorsIsReady = true;

				Log.debug("[DescriptorRepository] [initSSODescriptors] Total observation entries: " + ssoDescriptors.getTotal());
				if (GUISessionStatus.getIsInScienceMode()) {
					if (!EsaSkyWebConstants.SINGLE_COUNT_ENABLED) {
						updateCount4Observations();
					} else {
						checkDoCountAll();
					}
				} else {
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
		JSONUtils.getJSONFromUrl(URL.encode(EsaSkyWebConstants.SPECTRA_URL),
				new esac.archive.esasky.cl.web.client.utility.JSONUtils.IJSONRequestCallback() {

					@Override
					public void onSuccess(String responseText) {
						SpectraDescriptorListMapper mapper = GWT.create(SpectraDescriptorListMapper.class);
						spectraDescriptors = new DescriptorListAdapter<SpectraDescriptor>(mapper.read(responseText),
								countObserver);
						spectraDescriptorsIsReady = true;

						Log.debug("[DescriptorRepository] Total spectra entries: " + spectraDescriptors.getTotal());
						if (GUISessionStatus.getIsInScienceMode()) {
							if (!EsaSkyWebConstants.SINGLE_COUNT_ENABLED) {
								updateCount4Spectras();
							} else {
								checkDoCountAll();
							}
						} else {
							GUISessionStatus.setDoCountOnEnteringScienceMode();
						}
					}

					@Override
					public void onError(String errorCause) {
						Log.error("[DescriptorRepository] initSpectraDescriptors ERROR: " + errorCause);
						spectraDescriptorsIsReady = true;
						checkDoCountAll();
					}

				});
	}

	public void initPubDescriptors() {
		Log.debug("[DescriptorRepository] Into DescriptorRepository.initPubDescriptors");

		JSONUtils.getJSONFromUrl(URL.encode(EsaSkyWebConstants.PUBLICATIONS_URL), new IJSONRequestCallback() {

			@Override
			public void onSuccess(String responseText) {
				PublicationsDescriptorListMapper mapper = GWT.create(PublicationsDescriptorListMapper.class);
				publicationsDescriptors = new DescriptorListAdapter<PublicationsDescriptor>(mapper.read(responseText),
						new CountObserver() {
							
							@Override
							public void onCountUpdate(int newCount) {
							}
						});
				publicationsDescriptorsIsReady = true;
				for(PublicationDescriptorLoadObserver observer : publicationDescriptorLoadObservers) {
					observer.onLoad();
				}

				Log.debug("[DescriptorRepository] Total publications entries: " + publicationsDescriptors.getTotal());
				if (GUISessionStatus.getIsInScienceMode()) {
					if (!EsaSkyWebConstants.SINGLE_COUNT_ENABLED) {
						updateCount4Publications();
					} else {
						checkDoCountAll();
					}
				} else {
					GUISessionStatus.setDoCountOnEnteringScienceMode();
				}
			}

			@Override
			public void onError(String errorCause) {
				Log.error("[DescriptorRepository] initPubDescriptors ERROR: " + errorCause);
				publicationsDescriptorsIsReady = true;
				checkDoCountAll();
			}

		});
	}

	private void checkDoCountAll() {
		if (EsaSkyWebConstants.SINGLE_COUNT_ENABLED && catDescriptorsIsReady && obsDescriptorsIsReady
				&& spectraDescriptorsIsReady && publicationsDescriptorsIsReady
				&& isInitialPositionDescribedInCoordinates) {
			doCountAll();
		}
	}

	public void doCountAll() {

		if (EsaSkyWebConstants.SINGLE_COUNT_ENABLED) {
			// Single dynamic count
			requestSingleCount();

		} else {

			// Normal dynamic count per mission
			updateCount4Catalogs();
			updateCount4Observations();

			if (Modules.spectraModule) {
				updateCount4Spectras();
			}

			if (Modules.publicationsModule) {
				updateCount4Publications();
			}
		}
	}
	
	public void updateCount4AllExtTaps() {
		for(ExtTapDescriptor descriptor : extTapDescriptors.getDescriptors()) {
			updateCount4ExtTap(descriptor);
		}
	}
	
	public void updateCount4ExtTap(ExtTapDescriptor descriptor) {
		final CountStatus cs = extTapDescriptors.getCountStatus();
		String adql = TAPExtTapService.getInstance().getCountAdql(descriptor);
		String url = TAPUtils.getExtTAPQuery(URL.encode(adql), EsaSkyConstants.JSON, descriptor.getMission());
		
		
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
		try {
			builder.sendRequest(null, new ExtTapCheckCallback(adql, descriptor, cs, countRequestHandler.getProgressIndicatorMessage()));
		}catch (RequestException e) {
			Log.error(e.getMessage());
			Log.error("Error fetching JSON data from server");
		}
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

	private void updateCount4Publications() {
		final CountStatus cs = publicationsDescriptors.getCountStatus();
		for (PublicationsDescriptor currPub : publicationsDescriptors.getDescriptors()) {
			doUpdateCount(currPub, cs);
		}
	}

	private final void doUpdateCount(IDescriptor descriptor, CountStatus cs) {

		Log.debug("[doUpdateCount][" + descriptor.getGuiShortName() + "]");

		String url;

		if (descriptor instanceof PublicationsDescriptor) {
			if (EsaSkyWebConstants.PUBLICATIONS_RETRIEVE_PUB_COUNT_FROM_SIMBAD) {

				url = URL.encode(TAPCountPublicationsService.getInstance()
						.getCountQueryForSIMBAD(AladinLiteWrapper.getAladinLite()));
			} else {

				url = URL.encode(TAPCountPublicationsService.getInstance().getCount(AladinLiteWrapper.getAladinLite(),
						(PublicationsDescriptor) descriptor));
			}

		} else if (descriptor instanceof CatalogDescriptor) {

			url = URL.encode(TAPCountCatalogueService.getInstance().getCount(AladinLiteWrapper.getAladinLite(),
					(CatalogDescriptor) descriptor));

		} else {

			url = URL.encode(
					TAPCountObservationService.getInstance().getCount(AladinLiteWrapper.getAladinLite(), descriptor));
		}

		JSONUtils.getJSONFromUrl(url, new CountRequestCallback(descriptor, cs, countRequestHandler, url));
	}
	
	public void doCountExtTap(IDescriptor descriptor, CountStatus cs) {
		
	}

	public void doCountSSO(String ssoName, ESASkySSOObjType ssoType, ISSOCountRequestHandler countRequestHandler) {

		String url = URL.encode(TAPUtils.getTAPQuery(TAPCountSSOService.getInstance().getCount(ssoName, ssoType),
				EsaSkyConstants.JSON));

		Log.debug("[doCountSSO] SSO count Query [" + url + "]");
		JSONUtils.getJSONFromUrl(url,
				new SsoCountRequestCallback(ssoDescriptors, ssoName, ssoType, countRequestHandler));
	}

	private static long lastestSingleCountTimecall;

	private final void requestSingleCount() {

		final SkyViewPosition skyViewPosition = CoordinateUtils.getCenterCoordinateInJ2000();
		
		final long timecall = System.currentTimeMillis();
		lastestSingleCountTimecall = timecall;
		String url = URL.encode(TAPUtils.getTAPQuery(
				TAPSingleCountService.getInstance().getCount(AladinLiteWrapper.getAladinLite()), EsaSkyConstants.JSON));
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
				
				for(IDescriptor descriptor : descriptorsMap.values()) {
					CountStatus cs = countStatusMap.get(descriptor.getTapTable());
					final int count = 0;
					cs.setCountDetails(descriptor.getMission(), count, System.currentTimeMillis(), skyViewPosition);
		
					if (!(descriptor instanceof PublicationsDescriptor)) {
						// Publications do not use Treemap
						descriptors.add(descriptor);
						counts.add(count);
					}
				}
				if (descriptors.size() > 0) {
					CommonEventBus.getEventBus().fireEvent(new TreeMapNewDataEvent(descriptors, counts));
					for (String key : countStatusMap.keySet()) {
						countStatusMap.get(key).updateCount();
					}
				}
				
			}

		});
	}

	private void prepareDescriptorsMap() {
		descriptorsMap = new HashMap<String, IDescriptor>();
		countStatusMap = new HashMap<String, CountStatus>();

		addDescriptorsToHashMaps(catDescriptors);
		addDescriptorsToHashMaps(obsDescriptors);
		addDescriptorsToHashMaps(spectraDescriptors);
		addDescriptorsToHashMaps(publicationsDescriptors);
	}

	private void addDescriptorsToHashMaps(DescriptorListAdapter<?> descriptorListAdapter) {
		if (descriptorListAdapter != null) {
			final CountStatus cs = descriptorListAdapter.getCountStatus();
			for (IDescriptor descriptor : descriptorListAdapter.getDescriptors()) {
				descriptorsMap.put(descriptor.getTapTable(), descriptor);
				countStatusMap.put(descriptor.getTapTable(), cs);
			}
		}
	}

	private void doUpdateSingleCount(List<SingleCount> singleCountList, final SkyViewPosition skyViewPosition) {


		if (descriptorsMap == null) {
			prepareDescriptorsMap();
		}

		List<IDescriptor> descriptors = new ArrayList<IDescriptor>();
		List<Integer> counts = new ArrayList<Integer>();

		for (SingleCount singleCount : singleCountList) {

			if (descriptorsMap.containsKey(singleCount.getTableName())) {

				IDescriptor descriptor = descriptorsMap.get(singleCount.getTableName());
				CountStatus cs = countStatusMap.get(singleCount.getTableName());
				final int count = (singleCount.getCount() != null) ? singleCount.getCount() : 0;
				cs.setCountDetails(descriptor.getMission(), count, System.currentTimeMillis(), skyViewPosition);

				if (!(descriptor instanceof PublicationsDescriptor)) {
					// Publications do not use Treemap
					descriptors.add(descriptor);
					counts.add(count);
				}

			} else {
				Log.warn("[DescriptorRepository] doUpdateSingleCount. TABLE_NAME: '" + singleCount.getTableName()
						+ "' NOT FOUND IN DESCRIPTORS!");
			}
		}

		if (descriptors.size() > 0) {
			CommonEventBus.getEventBus().fireEvent(new TreeMapNewDataEvent(descriptors, counts));
			for (String key : countStatusMap.keySet()) {
				countStatusMap.get(key).updateCount();
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

	// public IDescriptor initUserDescriptor(List<MetadataDescriptor> metadata,
	// JSONWrapper jsonWrapper) {
	//
	// if
	// (jsonWrapper.getOverlaySet().getType().equals(DefaultValues.JSON_TYPE_FOOTPRINT))
	// {
	// return initUserDescriptor4Footprint(metadata, jsonWrapper);
	// } else if
	// (jsonWrapper.getOverlaySet().getType().equals(DefaultValues.JSON_TYPE_CATALOGUE))
	// {
	// return initUserDescriptor4Catalogue(metadata, jsonWrapper);
	// }
	// return null;
	// }

	private ObservationDescriptor initUserDescriptor4Footprint(List<MetadataDescriptor> metadata,
			FootprintListJSONWrapper footprintsSet) {
		ObservationDescriptor descriptor = new ObservationDescriptor();

		descriptor.setMetadata(metadata);
		descriptor.setWavelengths(null);

		descriptor.setMission(footprintsSet.getOverlaySet().getOverlayName());
		descriptor.setGuiLongName(footprintsSet.getOverlaySet().getOverlayName());
		descriptor.setGuiShortName(footprintsSet.getOverlaySet().getOverlayName());
		descriptor.setHistoColor(footprintsSet.getOverlaySet().getColor());
		
		descriptor.setTapObservationId(APIMetadataConstants.OBS_NAME);
		descriptor.setUniqueIdentifierField(APIMetadataConstants.ID);
		
		descriptor.setTapSTCSColumn("stcs");
		descriptor.setSampEnabled(false);
		descriptor.setIsSurveyMission(false);

		descriptor.setFovLimit(360.0);
		descriptor.setArchiveURL("<not_set>");
		descriptor.setArchiveProductURI("<not_set>");

		descriptor.setTapTable("<not_set>");
		descriptor.setCountColumn("<not_set>");
		descriptor.setCountFovLimit(0);
		descriptor.setAdsPublicationsMaxRows(0);
		descriptor.setTabCount(0);
		descriptor.setMocTapTable("<not_set>");
		descriptor.setMocSTCSColumn("<not_set>");
		descriptor.setDdBaseURL("<not_set>");
		descriptor.setDdProductIDParameter("<not_set>");
		descriptor.setDdProductIDColumn("<not_set>");
		descriptor.setSsoCardReductionTapTable("<not_set>");
		descriptor.setSsoXMatchTapTable("<not_set>");

		return descriptor;
	}

	private CatalogDescriptor initUserDescriptor4Catalogue(List<MetadataDescriptor> metadata,
			SourceListJSONWrapper userCatalogue) {
		CatalogDescriptor descriptor = new UserCatalogueDescriptor();

		descriptor.setMetadata(metadata);
		descriptor.setWavelengths(null);

		descriptor.setMission(userCatalogue.getOverlaySet().getOverlayName());
		descriptor.setGuiLongName(userCatalogue.getOverlaySet().getOverlayName());
		descriptor.setGuiShortName(userCatalogue.getOverlaySet().getOverlayName());
		descriptor.setHistoColor(userCatalogue.getOverlaySet().getColor());
		descriptor.setUniqueIdentifierField(APIMetadataConstants.ID);

		descriptor.setFovLimit(360.0);
		descriptor.setArchiveURL("<not_set>");
		descriptor.setArchiveProductURI("<not_set>");
		
		descriptor.setSourceLimit(10000);
		descriptor.setSourceLimitDescription("sourceLimitDescription");

		descriptor.setTapTable("<not_set>");
		descriptor.setCountColumn("<not_set>");
		descriptor.setCountFovLimit(0);
		descriptor.setAdsPublicationsMaxRows(0);
		descriptor.setTabCount(0);

		descriptor.setPolygonRaTapColumn(APIMetadataConstants.CENTER_RA_DEG);
		descriptor.setPolygonDecTapColumn(APIMetadataConstants.CENTER_DEC_DEG);
		descriptor.setPolygonNameTapColumn(APIMetadataConstants.CAT_NAME);

		return descriptor;
	}
	
	public void addPublicationDescriptorLoadObserver(PublicationDescriptorLoadObserver observer) {
		publicationDescriptorLoadObservers.add(observer);
	}
}