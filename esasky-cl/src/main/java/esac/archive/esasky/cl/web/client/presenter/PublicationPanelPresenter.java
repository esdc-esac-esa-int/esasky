package esac.archive.esasky.cl.web.client.presenter;


import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;

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
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyButton;

public class PublicationPanelPresenter {

    private View view;
    
    private final DescriptorRepository descriptorRepo;
    private final EntityRepository entityRepo;
    private final int MAX_FOV_DEG = 25;
    private PublicationsEntity entity;
    private int sourceLimit = 3000; //TODO mobile default source limit
    private long lastTimecall;
    private long lastSuccessfulTimecall;

    public interface View {
    	void hide();
    	void toggle();
    	boolean isShowing();

    	EsaSkyButton getDoPublicationsQueryButton();
    	EsaSkyButton getUpdateButton();
    	HasClickHandlers getRecentreButton();
    	HasClickHandlers getRemoveButton();
    	void setPublicationStatusText(String statusText);
    	
    	void setInitialLayout();
    	void setPublicationResultsAvailableLayout();
    	void setMaxFoV(boolean maxFov);
    	void setLoadingSpinnerVisible(boolean visible);
    	
    	boolean getUpdateOnMoveValue();
    	void addUpdateOnMoveCheckboxOnValueChangeHandler(ValueChangeHandler<Boolean> handler);
    	void addSourceLimitOnValueChangeHandler(ValueChangeHandler<String> handler);
    	
    	int getLimit();
    }
    
    public PublicationPanelPresenter(final View inputView, final DescriptorRepository descriptorRepo, final EntityRepository entityRepo) {
        this.view = inputView;
        this.descriptorRepo = descriptorRepo;
        this.entityRepo = entityRepo;
        view.setInitialLayout();
        

		CommonEventBus.getEventBus().addHandler(AladinLiteFoVChangedEvent.TYPE, new AladinLiteFoVChangedEventHandler () {

			@Override
			public void onChangeEvent(AladinLiteFoVChangedEvent fovEvent) {
				view.setMaxFoV(AladinLiteWrapper.getInstance().getFovDeg() >= MAX_FOV_DEG);
			}
		});

		CommonEventBus.getEventBus().addHandler(AladinLiteCoordinatesOrFoVChangedEvent.TYPE, new AladinLiteCoordinatesOrFoVChangedEventHandler() {
			
			@Override
			public void onChangeEvent(AladinLiteCoordinatesOrFoVChangedEvent clickEvent) {
				if(view.getUpdateOnMoveValue()) {
					getPublications();
				}
			}
		});
		
		view.addUpdateOnMoveCheckboxOnValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if(event.getValue() 
						&& (entity == null || !entity.getSkyViewPosition().compare(CoordinateUtils.getCenterCoordinateInJ2000(), 0.01))
						) {
					getPublications();
				}
			}
		});
        
        view.getRemoveButton().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				view.setInitialLayout();
				cleanPublicationSources();
				entity = null;
			}
		});
        
        view.getDoPublicationsQueryButton().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				getPublications();
			}
		});
        
        view.getUpdateButton().addClickHandler(new ClickHandler() {
        	
        	@Override
        	public void onClick(ClickEvent event) {
        		getPublications();
        	}
        });
        
        view.getRecentreButton().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
                AladinLiteWrapper.getAladinLite().goToRaDec(Double.toString(entity.getSkyViewPosition().getCoordinate().ra),
                        Double.toString(entity.getSkyViewPosition().getCoordinate().dec));
                AladinLiteWrapper.getAladinLite().setZoom(entity.getSkyViewPosition().getFov());
			}
		});
        
        view.addSourceLimitOnValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				sourceLimit = view.getLimit();
				if(entity != null) {
					entity.setPublicationsSourceLimit(view.getLimit());
				}
			}
		});
        
    }
    
    private void getPublications() {
    	
		view.setMaxFoV(AladinLiteWrapper.getInstance().getFovDeg() >= MAX_FOV_DEG);
		if(AladinLiteWrapper.getInstance().getFovDeg() >= MAX_FOV_DEG) {
			return;
		}
		
		view.setPublicationStatusText("Updating...");
		view.setLoadingSpinnerVisible(true);
		
		final PublicationsDescriptor descriptor = descriptorRepo.getPublicationsDescriptors().getDescriptors().get(0);
		
        if (entity == null) {            
            entity = entityRepo.createPublicationsEntity(descriptor);
            entity.setPublicationsSourceLimit(sourceLimit);
        } else {
        	entity.setSkyViewPosition(CoordinateUtils.getCenterCoordinateInJ2000());
        }
        
        final String debugPrefix = "[getPublicationsSources][" + descriptor.getGuiShortName() + "]";
        
        // Get Query in ADQL format for SIMBAD TAP or ESASKY TAP.
        String url = "";
        if (EsaSkyWebConstants.PUBLICATIONS_RETRIEVE_DATA_FROM_SIMBAD) {
            final String adql = TAPMetadataPublicationsService.getMetadataAdqlforSIMBAD(descriptor, view.getLimit());
            url = TAPUtils.getSIMBADTAPQuery("pub_sources", URL.encode(adql), null);
        } else {
            final String adql = TAPMetadataPublicationsService.getMetadataAdqlFromEsaSkyTap(descriptor, view.getLimit());
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
					
					//TODO replace GUiS..isPubActive with local equivalent... What if you have exited publication mode?
					if(entity != null && timecall > lastSuccessfulTimecall) {
						view.setPublicationResultsAvailableLayout();
						TapRowListMapper mapper = GWT.create(TapRowListMapper.class);
						TapRowList rowList = mapper.read(response.getText());
						entity.setMetadata(rowList);
						entity.addShapes(rowList);
						lastSuccessfulTimecall = timecall;
			    		if(timecall == lastTimecall) {
				    		//TODO source limit text / 0 results text?
			    			view.setPublicationStatusText(rowList.getData().size() + " sources with related publications");
				    		view.setLoadingSpinnerVisible(false);
				    	}
		    		}
				}
				
				@Override
				public void onError(Request request, Throwable exception) {
					super.onError(request, exception);
					if(timecall == lastTimecall) {
						view.setPublicationStatusText("Failed to retreive publications");
						view.setLoadingSpinnerVisible(false);
					}
				};
				
			});

        } catch (RequestException e) {
            Log.error(e.getMessage());
            Log.error(debugPrefix + "Error fetching JSON data from server");
        }
    }

    public void hide() {
    	view.hide();
    }
    
    public void toggle() {
		view.toggle();
    }
    
    public boolean isShowing() {
    	return view.isShowing();
    }
    
    private final void cleanPublicationSources () {
        final JavaScriptObject catalog = AladinLiteWrapper.getInstance().getPublicationCatalogue();
        AladinLiteWrapper.getAladinLite().removeAllSourcesFromCatalog(catalog);
    }
    
}
