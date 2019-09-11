package esac.archive.esasky.cl.web.client.presenter;


import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.Request;
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
import esac.archive.esasky.cl.web.client.callback.JsonRequestCallback;
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
import esac.archive.esasky.cl.web.client.view.common.MenuItem;
import esac.archive.esasky.cl.web.client.view.common.MenuObserver;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyButton;

public class PublicationPanelPresenter {

    private View view;
    
    private final DescriptorRepository descriptorRepo;
    private final EntityRepository entityRepo;
    private final int MAX_FOV_DEG = 181;
    private PublicationsEntity entity;
    private int sourceLimit;
    private long lastTimecall;
    private long lastSuccessfulTimecall;
    private boolean isShowingDataOrCallInProgress;
    private boolean isUpdateOnMoveChecked = false;
    
    private Timer sourceLimitChangedTimer = new Timer() {
		
		@Override
		public void run() {
			GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_Publication, GoogleAnalytics.ACT_Publication_TruncationNumberChanged, "Source Limit: " + sourceLimit + " URL: " + UrlUtils.getUrlForCurrentState());
		}
	};

    public interface View {
    	void hide();
    	void toggle();
    	boolean isShowing();

    	EsaSkyButton getUpdateButton();
    	void addRemoveButtonClickHandler(ClickHandler handler);
    	void setPublicationStatusText(String statusText);
    	
    	void setMaxFoV(boolean maxFov);
    	void setLoadingSpinnerVisible(boolean visible);
    	
    	void addUpdateOnMoveSwitchClickHandler(ClickHandler handler);
    	void setUpdateOnMoveSwitchValue(boolean checked);
    	void addSourceLimitOnValueChangeHandler(ValueChangeHandler<String> handler);
    	void setSourceLimitValues(int value, int min, int max);
    	
    	void onlyShowFovWarning(boolean onlyShowFovWarning);
    	void setMaxFovText(String text);
    	
    	String getOrderByValue();
    	String getOrderByDescription();
    	
    	int getLimit();
    	
    	void addTruncationOption(MenuItem<String> menuItem);
    	void addTruncationOptionObserver(MenuObserver observer);
    }
    
    public PublicationPanelPresenter(final View inputView, final DescriptorRepository descriptorRepo, final EntityRepository entityRepo) {
        this.view = inputView;
        this.descriptorRepo = descriptorRepo;
        this.entityRepo = entityRepo;
        sourceLimit = DeviceUtils.isMobileOrTablet() ? 300 : 3000;

		view.addTruncationOption(new MenuItem<String> ("bibcount DESC", TextMgr.getInstance().getText("publicationPanel_truncationValuePublicationMost"), true));
		view.addTruncationOption(new MenuItem<String> ("bibcount ASC", TextMgr.getInstance().getText("publicationPanel_truncationValuePublicationLeast"), true));
		view.addTruncationOption(new MenuItem<String> ("name ASC", TextMgr.getInstance().getText("publicationPanel_truncationValueSourceNameAZ"), true));
		view.addTruncationOption(new MenuItem<String> ("name DESC", TextMgr.getInstance().getText("publicationPanel_truncationValueSourceNameZA"), true));
		view.addTruncationOption(new MenuItem<String> ("ra DESC", TextMgr.getInstance().getText("publicationPanel_truncationValueRaHigh"), true));
		view.addTruncationOption(new MenuItem<String> ("ra ASC", TextMgr.getInstance().getText("publicationPanel_truncationValueRaLow"), true));
		view.addTruncationOption(new MenuItem<String> ("dec DESC", TextMgr.getInstance().getText("publicationPanel_truncationValueDecHigh"), true));
		view.addTruncationOption(new MenuItem<String> ("dec ASC", TextMgr.getInstance().getText("publicationPanel_truncationValueDecLow"), true));
        
		view.setMaxFovText(TextMgr.getInstance().getText("publicationPanel_maxFovWarning").replace("$MAX_FOV$", String.valueOf(MAX_FOV_DEG)));
        view.setSourceLimitValues(sourceLimit, 1, 50000);
		
		CommonEventBus.getEventBus().addHandler(AladinLiteFoVChangedEvent.TYPE, new AladinLiteFoVChangedEventHandler () {

			@Override
			public void onChangeEvent(AladinLiteFoVChangedEvent fovEvent) {
				view.setMaxFoV(AladinLiteWrapper.getInstance().getFovDeg() >= MAX_FOV_DEG);
				if(AladinLiteWrapper.getInstance().getFovDeg() < MAX_FOV_DEG && isShowing() && !isShowingDataOrCallInProgress) {
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
        
        view.addRemoveButtonClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
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
        
        view.addTruncationOptionObserver(new MenuObserver() {
			
			@Override
			public void onSelectedChange() {
				if(entity != null) {
					entity.setOrderByDescription(view.getOrderByDescription());
				}
				GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_Publication, GoogleAnalytics.ACT_Publication_TruncationOptionsChanged, "Order By: " + view.getOrderByValue() + " URL: " + UrlUtils.getUrlForCurrentState());
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
        
    }
    
    private void getPublications() {
		view.setMaxFoV(AladinLiteWrapper.getInstance().getFovDeg() >= MAX_FOV_DEG);
		if(AladinLiteWrapper.getInstance().getFovDeg() >= MAX_FOV_DEG) {
			return;
		}
		GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_Publication, GoogleAnalytics.ACT_Publication_BoxQuery, UrlUtils.getUrlForCurrentState());
		view.onlyShowFovWarning(false);
		
		view.setPublicationStatusText(TextMgr.getInstance().getText("publicationPanel_updating"));
		view.setLoadingSpinnerVisible(true);
		
		final PublicationsDescriptor descriptor = descriptorRepo.getPublicationsDescriptors().getDescriptors().get(0);
		isShowingDataOrCallInProgress = true;
		
        if (entity == null) {            
            entity = entityRepo.createPublicationsEntity(descriptor);
            entity.setPublicationsSourceLimit(sourceLimit);
            entity.setOrderByDescription(view.getOrderByDescription());
        } else {
        	entity.setSkyViewPosition(CoordinateUtils.getCenterCoordinateInJ2000());
        }
        
        final String debugPrefix = "[getPublicationsSources][" + descriptor.getGuiShortName() + "]";
        
        // Get Query in ADQL format for SIMBAD TAP or ESASKY TAP.
        String url = "";
        if (EsaSkyWebConstants.PUBLICATIONS_RETRIEVE_DATA_FROM_SIMBAD) {
            final String adql = TAPMetadataPublicationsService.getMetadataAdqlforSIMBAD(descriptor, view.getLimit(), view.getOrderByValue());
            url = TAPUtils.getSIMBADTAPQuery("pub_sources", URL.encode(adql), null);
        } else {
            final String adql = TAPMetadataPublicationsService.getMetadataAdqlFromEsaSkyTap(descriptor, view.getLimit(), view.getOrderByValue());
            url = TAPUtils.getTAPQuery(URL.encode(adql), EsaSkyConstants.JSON);
        }
        
        Log.debug(debugPrefix + "Query [" + url + "]");

        final long timecall = System.currentTimeMillis();
        lastTimecall = timecall;
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
        try {
            builder.sendRequest(null, new JsonRequestCallback(TextMgr.getInstance().getText("PublicationsSourcesCallback_retrievingPubSources"), url) {
				
				@Override
				protected void onSuccess(Response response) {
					
					if(entity != null && timecall > lastSuccessfulTimecall) {
						TapRowListMapper mapper = GWT.create(TapRowListMapper.class);
						TapRowList rowList = mapper.read(response.getText());
						entity.setMetadata(rowList);
						entity.addShapes(rowList);
						lastSuccessfulTimecall = timecall;
			    		if(timecall == lastTimecall) {
			    			if(rowList.getData().size() == 0) {
			    				view.setPublicationStatusText(TextMgr.getInstance().getText("publicationPanel_statusTextNoPublications"));
			    			} else {
			    				String formattedNumber = NumberFormatter.formatToNumberWithSpaces(Integer.toString(rowList.getData().size()));
			    				if(rowList.getData().size() >= entity.getSourceLimit()) {
			    					view.setPublicationStatusText(TextMgr.getInstance().getText("publicationPanel_statusTextTruncated") + " "
			    							+ TextMgr.getInstance().getText("publicationPanel_statusTextNumSources").replace("$NUM_SOURCES$", formattedNumber));
			    				} else {
			    					view.setPublicationStatusText(TextMgr.getInstance().getText("publicationPanel_statusTextNumSources").replace("$NUM_SOURCES$", formattedNumber));
			    				}
			    			}
				    		view.setLoadingSpinnerVisible(false);
				    	}
		    		}
				}
				
				@Override
				public void onError(Request request, Throwable exception) {
					super.onError(request, exception);
					GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_Publication, GoogleAnalytics.ACT_Publication_BoxQueryFailed, "Exception: " + exception + " URL: " + UrlUtils.getUrlForCurrentState());
					if(timecall == lastTimecall) {
						view.setPublicationStatusText(TextMgr.getInstance().getText("publicationPanel_statusTextFailed"));
						view.setLoadingSpinnerVisible(false);
					}
				};
				
			});

        } catch (RequestException e) {
        	GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_Publication, GoogleAnalytics.ACT_Publication_BoxQueryFailed, "Exception: " + e.toString() + " URL: " + UrlUtils.getUrlForCurrentState());
            Log.error(e.getMessage());
            Log.error(debugPrefix + "Error fetching JSON data from server");
            if(timecall == lastTimecall) {
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
			view.onlyShowFovWarning(AladinLiteWrapper.getInstance().getFovDeg() >= MAX_FOV_DEG);
		}
    }
    
    public boolean isShowing() {
    	return view.isShowing();
    }
    
    private final void cleanPublicationSources () {
        final JavaScriptObject catalog = AladinLiteWrapper.getInstance().getPublicationCatalogue();
        AladinLiteWrapper.getAladinLite().removeAllSourcesFromCatalog(catalog);
    }
    
}
