package esac.archive.esasky.cl.web.client.presenter;

import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.URL;

import esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteConstants;
import esac.archive.esasky.ifcs.model.client.SkiesMenu;
import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinateValidator.SearchInputType;
import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesConversion;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.shared.ESASkySearchResult;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.IsInScienceModeChangeEvent;
import esac.archive.esasky.cl.web.client.event.IsInScienceModeEventHandler;
import esac.archive.esasky.cl.web.client.event.IsTrackingSSOEvent;
import esac.archive.esasky.cl.web.client.event.IsTrackingSSOEventHandler;
import esac.archive.esasky.cl.web.client.event.MTClickEvent;
import esac.archive.esasky.cl.web.client.event.MTClickEventHandler;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPopEvent;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPushEvent;
import esac.archive.esasky.cl.web.client.event.PublicationsClickEvent;
import esac.archive.esasky.cl.web.client.event.TreeMapNewDataEvent;
import esac.archive.esasky.cl.web.client.event.TreeMapNewDataEventHandler;
import esac.archive.esasky.cl.web.client.event.TreeMapSelectionEvent;
import esac.archive.esasky.cl.web.client.event.TreeMapSelectionEventHandler;
import esac.archive.esasky.cl.web.client.event.banner.ToggleSkyPanelEvent;
import esac.archive.esasky.cl.web.client.event.banner.ToggleSkyPanelEventHandler;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.TapRowList;
import esac.archive.esasky.cl.web.client.presenter.ResultsPresenter.TapRowListMapper;
import esac.archive.esasky.cl.web.client.status.GUISessionStatus;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.DeviceUtils;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.utility.JSONUtils;
import esac.archive.esasky.cl.web.client.utility.JSONUtils.IJSONRequestCallback;
import esac.archive.esasky.cl.web.client.utility.ParseUtils;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyToggleButton;
import esac.archive.esasky.cl.web.client.view.resultspanel.column.Link2ArchiveColumn;
import esac.archive.esasky.cl.web.client.view.resultspanel.column.LinkListColumn;

/**
 * @author ESDC team Copyright (c) 2015- European Space Agency
 */
public class CtrlToolBarPresenter {

    private View view;
    
    private SelectSkyPanelPresenter selectSkyPresenter;

    public interface SkiesMenuMapper extends ObjectMapper<SkiesMenu> {}
    
    public interface View {

        void updateObservationCount(int newCount);
        void updateCatalogCount(int newCount);
        void updateSpectraCount(int newCount);
        void updateSsoCount(int newCount);
        void updatePublicationsCount(int newCount);
        void onIsTrackingSSOEventChanged();
        void closeTreeMap();
        void enterScienceMode();
        void leaveScienceMode();
        void closeAllPanelsExceptSkyPanel();

        void showSearchResultsOnTargetList(List<ESASkySearchResult> searchResults, String title);
        
        HasClickHandlers getPublicationButton();
        EsaSkyToggleButton getSkyPanelButton();
        
        SelectSkyPanelPresenter.View getSelectSkyView();

        void addTreeMapData(List<IDescriptor> descriptors, List<Integer> counts);
    }

    public CtrlToolBarPresenter(final View inputView) {
        this.view = inputView;
        selectSkyPresenter = new SelectSkyPanelPresenter(view.getSelectSkyView());
        bind();
        updateScienceModeElements();
    }

    private void bind() {
        /*
         * Multitarget pointer in the middle of the current sky
         */
        CommonEventBus.getEventBus().addHandler(MTClickEvent.TYPE, new MTClickEventHandler() {

            @Override
            public void onClickEvent(final MTClickEvent clickEvent) {
                ESASkySearchResult target = clickEvent.getTarget();
                if (target.getValidInput()) {

                    if (target.getUserInputType() == SearchInputType.TARGET) {
                        AladinLiteWrapper.getInstance().goToTarget(target.getSimbadRaDeg(), target.getSimbadDecDeg(), target.getFoVDeg(), false, target.getCooFrame());
                    } else {

                        String[] raDecDeg = { target.getUserRaDeg(), target.getUserDecDeg() };
                        if (target.getCooFrame() != null) {

                            if (target.getCooFrame().equals(AladinLiteConstants.FRAME_GALACTIC)) {
                                // Since Aladin.View.pointTo does the always the conversion from GAL
                                // to
                                // J2000 even if the coordinates are already in GAL we need to
                                // convert
                                // the coordinates back to J2000 and leave AladinLite to do the
                                // conversion

                                Log.debug("Clicked on source uploaded with FRAME GALACTIC");
                                Log.debug("mtl source input [" + target.getUserRaDeg() + ","
                                        + target.getUserDecDeg() + "]");
                                Double[] raDecDegJ2000 = CoordinatesConversion
                                        .convertPointGalacticToJ2000(
                                                Double.parseDouble(target.getUserRaDeg()),
                                                Double.parseDouble(target.getUserDecDeg()));
                                Log.debug("mtl (after conversion) source input ["
                                        + raDecDegJ2000[0] + "," + raDecDegJ2000[1] + "]");
                                raDecDeg[0] = Double.toString(raDecDegJ2000[0]);
                                raDecDeg[1] = Double.toString(raDecDegJ2000[1]);
                            }
                            AladinLiteWrapper.getInstance().goToTarget(raDecDeg[0], raDecDeg[1],
                                    target.getFoVDeg(), false, AladinLiteConstants.FRAME_J2000);
                        }
                        AladinLiteWrapper.getInstance().goToTarget(raDecDeg[0], raDecDeg[1],
                                target.getFoVDeg(), false, AladinLiteConstants.FRAME_J2000);
                    }
                }
            }
        });
        
        CommonEventBus.getEventBus().addHandler(IsTrackingSSOEvent.TYPE, new IsTrackingSSOEventHandler() {

			@Override
			public void onIsTrackingSSOEventChanged() {
				view.onIsTrackingSSOEventChanged();
			}

        });       

        CommonEventBus.getEventBus().addHandler(TreeMapSelectionEvent.TYPE,
                new TreeMapSelectionEventHandler() {

            @Override
            public void onSelection(TreeMapSelectionEvent event) {
                if(DeviceUtils.isMobile()) {
        	        view.closeTreeMap();
                }
            }
        });
        
        CommonEventBus.getEventBus().addHandler(TreeMapNewDataEvent.TYPE, new TreeMapNewDataEventHandler() {
			
			@Override
			public void onNewDataEvent(TreeMapNewDataEvent newDataEvent) {
				view.addTreeMapData(newDataEvent.getDescriptors(), newDataEvent.getCounts());
			}
		});
         
        
        view.getPublicationButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
            	GUISessionStatus.setIsPublicationsActive(!GUISessionStatus.getIsPublicationsActive());
                CommonEventBus.getEventBus().fireEvent(new PublicationsClickEvent(GUISessionStatus.getIsPublicationsActive()));
                GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_CtrlToolbar, GoogleAnalytics.ACT_CtrlToolbar_Publications, "");
            }
        });
        
        CommonEventBus.getEventBus().addHandler(IsInScienceModeChangeEvent.TYPE, new IsInScienceModeEventHandler() {
			
			@Override
			public void onIsInScienceModeChanged() {
				updateScienceModeElements();
			}
		});    
        
        CommonEventBus.getEventBus().addHandler(ToggleSkyPanelEvent.TYPE, new ToggleSkyPanelEventHandler() {
			
			@Override
			public void onEvent(ToggleSkyPanelEvent event) {
				selectSkyPresenter.toggle();
				view.getSkyPanelButton().setToggleStatus(selectSkyPresenter.isShowing());;
				view.closeAllPanelsExceptSkyPanel();
			}
		});
        
        view.getSkyPanelButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				selectSkyPresenter.toggle();
			}
		});
    }
    
    private void updateScienceModeElements() {
		if(GUISessionStatus.getIsInScienceMode()) {
			view.enterScienceMode();
		} else {
			view.leaveScienceMode();
		}
    }
    
    public void updateObservationCount(int newCount){
        view.updateObservationCount(newCount);
    }
    
    public void updateCatalogCount(int newCount){
        view.updateCatalogCount(newCount);
    }
    
    public void updateSpectraCount(int newCount){
        view.updateSpectraCount(newCount);
    }
    
    public void updateSsoCount(int newCount){
        view.updateSsoCount(newCount);
    }

    public void updatePublicationsCount(int newCount) {
        view.updatePublicationsCount(newCount);
    }
    
    private static long latestBibCodeTimeCall;
    
    public void showPublicationInfo (final String bibcode, final String bibcodeLinkUrl, final String bibcodeUri, final String splitByString, final String authorsLinkUrl, final String replaceString) {
        Log.info("[CtrlToolBarPresenter] showPublicationInfo BIBCODE received: " + bibcode + " , preparing publication info.");
        final String publicationDetailsId = "Publication Details";
        CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPushEvent(publicationDetailsId, TextMgr.getInstance().getText("ctrlToolBarPresenter_loadPublicationDetails")));

        
        //Retrieves the publication details for this bibcode and checks if publications exists
        JSONUtils.getJSONFromUrl(EsaSkyWebConstants.PUBLICATIONS_DETAILS_URL + "?BIBCODE="
                + URL.encodeQueryString(bibcode), new IJSONRequestCallback() {
            
            @Override
            public void onSuccess(String responseText) {
            	CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPopEvent(publicationDetailsId));
                
                TapRowListMapper mapper = GWT.create(TapRowListMapper.class);
                TapRowList rowList = mapper.read(responseText);
                
                if (rowList.getData().size() > 0) {
                    
                    final String bibcodeHtml = Link2ArchiveColumn.getLinkHtml(bibcode, bibcodeLinkUrl, bibcodeUri).asString();
                    
                    String authorsHtml = LinkListColumn.getLinkList(rowList.getDataValue("author", 0), 
                                                                    splitByString,
                                                                    authorsLinkUrl,
                                                                    replaceString,
                                                                    EsaSkyWebConstants.PUBLICATIONS_SHOW_ALL_AUTHORS_TEXT, 
                                                                    EsaSkyWebConstants.PUBLICATIONS_MAX_AUTHORS).asString();
                    
                    final String titleHtml = "<h3 style='font-size: 0.85em;'>" + rowList.getDataValue("title", 0) + "</h3>" +
                                      "<h5>" + TextMgr.getInstance().getText("ctrlToolBarPresenter_bibcode").replace("$HTML$", bibcodeHtml) + "</h5>" + 
                                      "<h5>" + TextMgr.getInstance().getText("ctrlToolBarPresenter_authors").replace("$HTML$", authorsHtml) + "</h5>" +
                                      "<h5>" + TextMgr.getInstance().getText("ctrlToolBarPresenter_journal").replace("$JOURNAL$", rowList.getDataValue("pub", 0)).replace("$DATE$", rowList.getDataValue("pubdate", 0)) + "</h5>" +
                                      "<h4>" + TextMgr.getInstance().getText("ctrlToolBarPresenter_pubSources") + "</h4>";
                    
                    final String retrievingSourcesId = "Retrieving Sources";
                    final int maxSources = (DeviceUtils.isMobile() ? EsaSkyWebConstants.MAX_SOURCES_FOR_MOBILE : EsaSkyWebConstants.MAX_SOURCES_IN_TARGETLIST);
                    
                    final long timecall = System.currentTimeMillis();
                    latestBibCodeTimeCall = timecall;
                    
                    String url = EsaSkyWebConstants.PUBLICATIONS_SOURCES_BY_BIBCODE_URL + "?BIBCODE="
                            + URL.encodeQueryString(bibcode) + "&ROWS=" + maxSources;
                    CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPushEvent(retrievingSourcesId, 
                    		TextMgr.getInstance().getText("ctrlToolBarPresenter_retrievingPublicationTargetList"),
                    				url));
                    //Retrieves the sources for this bibcode and shows the upload panel
                    JSONUtils.getJSONFromUrl(url, new IJSONRequestCallback() {
                        
                        @Override
                        public void onSuccess(String responseText) {
                        	CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPopEvent(retrievingSourcesId));

                        	if(timecall < latestBibCodeTimeCall) {
                        		Log.warn("discarded bibcode " + bibcode + " target list, since there are newer list requests");
                        		return;
                        	}
                            //Shows the sources for this publication
                            final List<ESASkySearchResult> searchResult = ParseUtils.parseJsonSearchResults(responseText);
                            view.showSearchResultsOnTargetList(searchResult, titleHtml + getNumSourcesText(searchResult.size(), maxSources));
                        }
                        
                        @Override
                        public void onError(String errorCause) {
                            Log.error("[CtrlToolBarPresenter] showPublicationInfo ERROR: " + errorCause);
                            CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPopEvent(retrievingSourcesId));
                        }
                        
                    });
                    
                } else {
                    Log.warn("[CtrlToolBarPresenter] showPublicationInfo, no publication details found for bibcode: " + bibcode);
                }
            }
            
            @Override
            public void onError(String errorCause) {
                Log.error("[CtrlToolBarPresenter] showPublicationInfo, error fetching details. ERROR: " + errorCause);
                CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPopEvent(publicationDetailsId));
            }
            
        });
    }
    
    public void showAuthorInfo (final String author, final String splitByString, final String authorsLinkUrl, final String replaceString) {
        
        Log.info("[CtrlToolBarPresenter] showAuthorInfo AUTHOR received: " + author + " , preparing author info.");
        
        final int maxSources = (DeviceUtils.isMobile() ? EsaSkyWebConstants.MAX_SOURCES_FOR_MOBILE : EsaSkyWebConstants.MAX_SOURCES_IN_TARGETLIST);
        CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPushEvent("LoadingAuthorPublicatoinSorces", 
        		TextMgr.getInstance().getText("ctrlToolBarPresenter_loadingAuthorSources")));
        //Retrieves the sources for this bibcode and shows the upload panel
        JSONUtils.getJSONFromUrl(EsaSkyWebConstants.PUBLICATIONS_SOURCES_BY_AUTHOR_URL + "?AUTHOR="
                + URL.encodeQueryString(author) + "&ROWS=" + maxSources, new IJSONRequestCallback() {
            
            @Override
            public void onSuccess(String responseText) {
            	CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPopEvent("LoadingAuthorPublicatoinSorces"));
                String authorHtml = LinkListColumn.getLinkList(author, 
                        splitByString,
                        authorsLinkUrl,
                        replaceString,
                        EsaSkyWebConstants.PUBLICATIONS_SHOW_ALL_AUTHORS_TEXT, 
                        EsaSkyWebConstants.PUBLICATIONS_MAX_AUTHORS).asString();

                final String titleHtml = "<h3 style='font-size: 0.85em;'>" + author + "</h3>" +
                "<h5>" + TextMgr.getInstance().getText("ctrlToolBarPresenter_adsSearch").replace("$HTML$", authorHtml) + "</h5>" + 
                "<h4>" + TextMgr.getInstance().getText("ctrlToolBarPresenter_authorSources") + "</h4>";
                
                //Shows the sources for this publication
                final List<ESASkySearchResult> searchResult = ParseUtils.parseJsonSearchResults(responseText);
                view.showSearchResultsOnTargetList(searchResult, titleHtml + getNumSourcesText(searchResult.size(), maxSources));
            }
            
            @Override
            public void onError(String errorCause) {
                Log.error("[CtrlToolBarPresenter] showAuthorInfo ERROR: " + errorCause);
                CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPopEvent("LoadingAuthorPublicatoinSorces"));
            }
            
        });
    }
    
    private String getNumSourcesText(int numSources, int maxSources) {
        if (numSources == maxSources) {
            return "<div style='font-size: 0.7em; color: yellow;'>" + TextMgr.getInstance().getText("ctrlToolBarPresenter_showingMaxSources").replace("$COUNT$", maxSources + "") + "</div>";
        } else {
            return "<div style='font-size: 0.7em; color: #CCCCCC;'>" + TextMgr.getInstance().getText("ctrlToolBarPresenter_showingCountSources").replace("$COUNT$", numSources + "") + "</div>";
        }
    }
    
    public SelectSkyPanelPresenter getSelectSkyPresenter(){
    	return selectSkyPresenter;
    }
}