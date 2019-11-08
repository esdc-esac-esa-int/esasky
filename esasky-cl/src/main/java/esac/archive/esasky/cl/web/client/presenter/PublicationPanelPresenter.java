package esac.archive.esasky.cl.web.client.presenter;


import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Timer;

import esac.archive.esasky.ifcs.model.descriptor.PublicationsDescriptor;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteCoordinatesOrFoVChangedEvent;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteCoordinatesOrFoVChangedEventHandler;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteFoVChangedEvent;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteFoVChangedEventHandler;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPopEvent;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPushEvent;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.TapRowList;
import esac.archive.esasky.cl.web.client.model.entities.PublicationsEntity;
import esac.archive.esasky.cl.web.client.presenter.ResultsPresenter.TapRowListMapper;
import esac.archive.esasky.cl.web.client.query.TAPMetadataPublicationsService;
import esac.archive.esasky.cl.web.client.query.TAPUtils;
import esac.archive.esasky.cl.web.client.repository.DescriptorRepository;
import esac.archive.esasky.cl.web.client.repository.EntityRepository;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.CoordinateUtils;
import esac.archive.esasky.cl.web.client.utility.DeviceUtils;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.utility.NumberFormatter;
import esac.archive.esasky.cl.web.client.utility.UrlUtils;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyButton;

public class PublicationPanelPresenter {

    private View view;
    
    private final DescriptorRepository descriptorRepo;
    private final EntityRepository entityRepo;
    private PublicationsEntity entity;
    private int sourceLimit;
    private long lastTimecall;
    private long lastSuccessfulTimecall;
    private int numberOfShownSources = 0;
    private int defaultSourceLimit;
    private boolean isShowingDataOrCallInProgress;
    private boolean isCallInProgress;
    private boolean isShowingTruncatedDataset = false;
    private boolean isUpdateOnMoveChecked = false;
    private boolean isMostChecked = true;
    private String progressIndicatorId = "publicationPresenterUpdatingId";
    
    private Timer sourceLimitChangedTimer = new Timer() {
		
		@Override
		public void run() {
			GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_Publication, GoogleAnalytics.ACT_Publication_TruncationNumberChanged, "Source Limit: " + sourceLimit + " URL: " + UrlUtils.getUrlForCurrentState());
			if(isShowingTruncatedDataset || isCallInProgress || numberOfShownSources > view.getLimit()) {
				getPublications();
			}
		}
	};

    public interface View {
    	void hide();
    	void toggle();
    	boolean isShowing();

    	EsaSkyButton getUpdateButton();
    	void addRemoveButtonClickHandler(ClickHandler handler);
    	void setPublicationStatusText(String statusText);
    	
    	void setLoadingSpinnerVisible(boolean visible);
    	
    	void addUpdateOnMoveSwitchClickHandler(ClickHandler handler);
    	void setUpdateOnMoveSwitchValue(boolean checked);
    	void setIsMostCheckedValue(boolean checked);
    	void addMostOrLeastSwitchClickHandler(ClickHandler handler);
    	
    	void addResetButtonClickHandler(ClickHandler handler);
    	void addSourceLimitOnValueChangeHandler(ValueChangeHandler<String> handler);
    	void setSourceLimitValues(int value, int min, int max);
    	void setSourceLimit(int value);
    	
    	int getLimit();
    	
    }
    
    public PublicationPanelPresenter(final View inputView, final DescriptorRepository descriptorRepo, final EntityRepository entityRepo) {
        this.view = inputView;
        this.descriptorRepo = descriptorRepo;
        this.entityRepo = entityRepo;
        defaultSourceLimit = DeviceUtils.isMobileOrTablet() ? 300 : 3000;
        sourceLimit = defaultSourceLimit;

        
        view.setSourceLimitValues(sourceLimit, 1, 10000);
		
		CommonEventBus.getEventBus().addHandler(AladinLiteFoVChangedEvent.TYPE, new AladinLiteFoVChangedEventHandler () {

			@Override
			public void onChangeEvent(AladinLiteFoVChangedEvent fovEvent) {
				if(isShowing() && !isShowingDataOrCallInProgress) {
					GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_Publication, GoogleAnalytics.ACT_Publication_MoveTriggeredBoxQuery, "");
					getPublications();
				}
			}
		});

		CommonEventBus.getEventBus().addHandler(AladinLiteCoordinatesOrFoVChangedEvent.TYPE, new AladinLiteCoordinatesOrFoVChangedEventHandler() {
			
			@Override
			public void onChangeEvent(AladinLiteCoordinatesOrFoVChangedEvent clickEvent) {
				if(isUpdateOnMoveChecked) {
					GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_Publication, GoogleAnalytics.ACT_Publication_MoveTriggeredBoxQuery, "");
					getPublications();
				}
			}
		});
		
		view.addUpdateOnMoveSwitchClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				isUpdateOnMoveChecked = !isUpdateOnMoveChecked;
				GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_Publication, GoogleAnalytics.ACT_Publication_UpdateOnMove, "UpdateOnMove: " + isUpdateOnMoveChecked);
				view.setUpdateOnMoveSwitchValue(isUpdateOnMoveChecked);
				if(isUpdateOnMoveChecked
						&& (entity == null || !entity.getSkyViewPosition().compare(CoordinateUtils.getCenterCoordinateInJ2000(), 0.01))
						) {
					getPublications();
				}
			}
		});

		view.addMostOrLeastSwitchClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				isMostChecked = !isMostChecked;
				GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_Publication, GoogleAnalytics.ACT_Publication_MostOrLeast, "Most: " + isMostChecked);
				view.setIsMostCheckedValue(isMostChecked);
				if(isShowingTruncatedDataset || isCallInProgress || numberOfShownSources > view.getLimit()) {
					getPublications();
				}
			}
		});
        
        view.addRemoveButtonClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				removeProgressIndicator();
				entity.removeSourceLimitNotificationNow();
				numberOfShownSources = 0;
				GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_Publication, GoogleAnalytics.ACT_Publication_Remove, UrlUtils.getUrlForCurrentState());
				cleanPublicationSources();
				entity = null;
				isShowingDataOrCallInProgress = false;
				view.hide();
			}
		});
        
        view.getUpdateButton().addClickHandler(new ClickHandler() {
        	
        	@Override
        	public void onClick(ClickEvent event) {
        		GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_Publication, GoogleAnalytics.ACT_Publication_Update, UrlUtils.getUrlForCurrentState());
        		getPublications();
        	}
        });
        
        view.addSourceLimitOnValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				sourceLimit = view.getLimit();
				if(entity != null) {
					entity.setPublicationsSourceLimit(view.getLimit());
				}
				sourceLimitChangedTimer.schedule(1000);
			}
		});
        
        view.addResetButtonClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(sourceLimit != defaultSourceLimit) {
					view.setSourceLimit(defaultSourceLimit);
				}
			}
		});
        
    }
    
    private void getPublications() {
		GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_Publication, GoogleAnalytics.ACT_Publication_BoxQuery, UrlUtils.getUrlForCurrentState());
		view.setPublicationStatusText(TextMgr.getInstance().getText("publicationPanel_updating"));
		view.setLoadingSpinnerVisible(true);
		
		
		final PublicationsDescriptor descriptor = descriptorRepo.getPublicationsDescriptors().getDescriptors().get(0);
		isShowingDataOrCallInProgress = true;
		
		String mostOrLeastAdql = isMostChecked ? "bibcount DESC" : "bibcount ASC";
		String mostOrLeastDescription = isMostChecked ? TextMgr.getInstance().getText("publicationPanel_truncationValuePublicationMost") : TextMgr.getInstance().getText("publicationPanel_truncationValuePublicationLeast");
        if (entity == null) {            
            entity = entityRepo.createPublicationsEntity(descriptor);
            entity.setPublicationsSourceLimit(sourceLimit);
            entity.setOrderByDescription(mostOrLeastDescription);
        } else {
        	entity.setSkyViewPosition(CoordinateUtils.getCenterCoordinateInJ2000());
        }
        
        final String debugPrefix = "[getPublicationsSources][" + descriptor.getGuiShortName() + "]";
        
        // Get Query in ADQL format for SIMBAD TAP or ESASKY TAP.
        String url = "";
        if (EsaSkyWebConstants.PUBLICATIONS_RETRIEVE_DATA_FROM_SIMBAD) {
            final String adql = TAPMetadataPublicationsService.getMetadataAdqlforSIMBAD(descriptor, view.getLimit(), mostOrLeastAdql);
            url = TAPUtils.getSIMBADTAPQuery("pub_sources", URL.encodeQueryString(adql), null);
        } else {
            final String adql = TAPMetadataPublicationsService.getMetadataAdqlFromEsaSkyTap(descriptor, view.getLimit(), mostOrLeastAdql);
            url = TAPUtils.getTAPQuery(URL.encodeQueryString(adql), EsaSkyConstants.JSON);
        }
        
        Log.debug(debugPrefix + "Query [" + url + "]");

        isCallInProgress = true;
        final long timecall = System.currentTimeMillis();
        lastTimecall = timecall;
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
        try {
        	addProgressIndicator(TextMgr.getInstance().getText("PublicationsSourcesCallback_retrievingPubSources"), url);
            builder.sendRequest(null, new RequestCallback() {
				
				@Override
			    public void onResponseReceived(final Request request, final Response response) {
			        if (200 != response.getStatusCode()) {
			            onError(request, new Exception(response.getStatusCode() + " ("
			                    + response.getStatusText() + ")"));
			        }
					
					if(entity != null && timecall > lastSuccessfulTimecall) {
						TapRowListMapper mapper = GWT.create(TapRowListMapper.class);
						TapRowList rowList = mapper.read(response.getText());
						entity.setMetadata(rowList);
						entity.addShapes(rowList);
						numberOfShownSources = rowList.getData().size();
						lastSuccessfulTimecall = timecall;
			    		if(timecall == lastTimecall) {
			    			removeProgressIndicator();
			    			if(rowList.getData().size() == 0) {
			    				view.setPublicationStatusText(TextMgr.getInstance().getText("publicationPanel_statusTextNoPublications"));
			    				isShowingTruncatedDataset = false;
			    			} else {
			    				String formattedNumber = NumberFormatter.formatToNumberWithSpaces(Integer.toString(rowList.getData().size()));
			    				if(rowList.getData().size() >= entity.getSourceLimit()) {
			    					isShowingTruncatedDataset = true;
			    					view.setPublicationStatusText(TextMgr.getInstance().getText("publicationPanel_statusTextTruncated") + " "
			    							+ TextMgr.getInstance().getText("publicationPanel_statusTextNumSources").replace("$NUM_SOURCES$", formattedNumber));
			    				} else {
			    					isShowingTruncatedDataset = false;
			    					view.setPublicationStatusText(TextMgr.getInstance().getText("publicationPanel_statusTextNumSources").replace("$NUM_SOURCES$", formattedNumber));
			    				}
			    			}
				    		view.setLoadingSpinnerVisible(false);
				    		isCallInProgress = false;
				    	}
		    		}
				}
				
				@Override
				public void onError(Request request, Throwable exception) {
					isCallInProgress = false;
					numberOfShownSources = 0;
					GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_Publication, GoogleAnalytics.ACT_Publication_BoxQueryFailed, "Exception: " + exception + " URL: " + UrlUtils.getUrlForCurrentState());
					if(timecall == lastTimecall) {
						removeProgressIndicator();
						view.setPublicationStatusText(TextMgr.getInstance().getText("publicationPanel_statusTextFailed"));
						view.setLoadingSpinnerVisible(false);
					}
				};
				
			});

        } catch (RequestException e) {
        	numberOfShownSources = 0;
        	isCallInProgress = false;
        	GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_Publication, GoogleAnalytics.ACT_Publication_BoxQueryFailed, "Exception: " + e.toString() + " URL: " + UrlUtils.getUrlForCurrentState());
            Log.error(e.getMessage());
            Log.error(debugPrefix + "Error fetching JSON data from server");
            if(timecall == lastTimecall) {
            	removeProgressIndicator();
            	view.setPublicationStatusText(TextMgr.getInstance().getText("publicationPanel_statusTextFailed"));
            	view.setLoadingSpinnerVisible(false);
            }
        }
    }

    public void hide() {
    	view.hide();
    }
    
    public void toggle() {
		view.toggle();
		if(isShowing() && !isShowingDataOrCallInProgress) {
			getPublications();
		}
    }
    
    public boolean isShowing() {
    	return view.isShowing();
    }
    
    private final void cleanPublicationSources () {
        final JavaScriptObject catalog = AladinLiteWrapper.getInstance().getPublicationCatalogue();
        AladinLiteWrapper.getAladinLite().removeAllSourcesFromCatalog(catalog);
    }
    
    private void addProgressIndicator(String progressIndicatorMessage, String googleAnalyticsErrorMessage) {
        CommonEventBus.getEventBus().fireEvent(
                new ProgressIndicatorPushEvent(progressIndicatorId, progressIndicatorMessage, googleAnalyticsErrorMessage));
    }

    private void removeProgressIndicator() {
        CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPopEvent(progressIndicatorId));
    }
    
}
