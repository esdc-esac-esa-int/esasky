package esac.archive.esasky.cl.web.client.presenter;

import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Widget;

import esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteConstants;
import esac.archive.esasky.ifcs.model.client.SkiesMenu;
import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinateValidator.SearchInputType;
import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesConversion;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.PublicationsDescriptor;
import esac.archive.esasky.ifcs.model.shared.ESASkySearchResult;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.IsInScienceModeChangeEvent;
import esac.archive.esasky.cl.web.client.event.IsInScienceModeEventHandler;
import esac.archive.esasky.cl.web.client.event.IsTrackingSSOEvent;
import esac.archive.esasky.cl.web.client.event.IsTrackingSSOEventHandler;
import esac.archive.esasky.cl.web.client.event.MultiTargetClickEvent;
import esac.archive.esasky.cl.web.client.event.MultiTargetClickEventHandler;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPopEvent;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPushEvent;
import esac.archive.esasky.cl.web.client.event.TreeMapNewDataEvent;
import esac.archive.esasky.cl.web.client.event.TreeMapNewDataEventHandler;
import esac.archive.esasky.cl.web.client.event.TreeMapSelectionEvent;
import esac.archive.esasky.cl.web.client.event.TreeMapSelectionEventHandler;
import esac.archive.esasky.cl.web.client.event.ShowPublicationSourcesEvent;
import esac.archive.esasky.cl.web.client.event.ShowPublicationSourcesEventHandler;
import esac.archive.esasky.cl.web.client.event.banner.ToggleSkyPanelEvent;
import esac.archive.esasky.cl.web.client.event.banner.ToggleSkyPanelEventHandler;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.TapRowList;
import esac.archive.esasky.cl.web.client.presenter.ResultsPresenter.TapRowListMapper;
import esac.archive.esasky.cl.web.client.repository.DescriptorRepository;
import esac.archive.esasky.cl.web.client.repository.EntityRepository;
import esac.archive.esasky.cl.web.client.status.GUISessionStatus;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.DeviceUtils;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.utility.JSONUtils;
import esac.archive.esasky.cl.web.client.utility.JSONUtils.IJSONRequestCallback;
import esac.archive.esasky.cl.web.client.utility.ParseUtils;
import esac.archive.esasky.cl.web.client.view.common.ESASkyJavaScriptLibrary;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyToggleButton;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.cl.web.client.view.resultspanel.column.LinkListColumn;

/**
 * @author ESDC team Copyright (c) 2015- European Space Agency
 */
public class CtrlToolBarPresenter {

    private View view;
    
    private SelectSkyPanelPresenter selectSkyPresenter;
    private PublicationPanelPresenter publicationPresenter;

    public interface SkiesMenuMapper extends ObjectMapper<SkiesMenu> {}
    
    public interface View {

        void updateObservationCount(int newCount);
        void updateCatalogCount(int newCount);
        void updateSpectraCount(int newCount);
        void updateSsoCount(int newCount);
        void onIsTrackingSSOEventChanged();
        void closeTreeMap();
        void enterScienceMode();
        void leaveScienceMode();
        void closeAllOtherPanels(Widget button);

        void showSearchResultsOnTargetList(List<ESASkySearchResult> searchResults, String title);
        
        EsaSkyToggleButton getPublicationButton();
        EsaSkyToggleButton getSkyPanelButton();
        
        SelectSkyPanelPresenter.View getSelectSkyView();
        PublicationPanelPresenter.View getPublicationPanelView();

        void addTreeMapData(List<IDescriptor> descriptors, List<Integer> counts);
    }

    public CtrlToolBarPresenter(final View inputView, DescriptorRepository descriptorRepo, EntityRepository entityRepo) {
        this.view = inputView;
        selectSkyPresenter = new SelectSkyPanelPresenter(view.getSelectSkyView());
        publicationPresenter = new PublicationPanelPresenter(view.getPublicationPanelView(), descriptorRepo, entityRepo);
        bind();
        updateScienceModeElements();
    }

    private void bind() {
        /*
         * Multitarget pointer in the middle of the current sky
         */
        CommonEventBus.getEventBus().addHandler(MultiTargetClickEvent.TYPE, new MultiTargetClickEventHandler() {

            @Override
            public void onClickEvent(final MultiTargetClickEvent clickEvent) {
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
                                Log.debug("mtl source SIMBADinput [" + target.getSimbadRaDeg() + ","
                                		+ target.getSimbadDecDeg() + "]");
                                double[] raDecDegJ2000 = CoordinatesConversion
                                        .convertPointGalacticToJ2000(
                                                Double.parseDouble(target.getSimbadRaDeg()),
                                                Double.parseDouble(target.getSimbadDecDeg()));
                                Log.debug("mtl (after conversion) source input ["
                                        + raDecDegJ2000[0] + "," + raDecDegJ2000[1] + "]");
                                raDecDeg[0] = target.getSimbadRaDeg();
                                raDecDeg[1] = target.getSimbadDecDeg();
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
                GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_CtrlToolbar, GoogleAnalytics.ACT_CtrlToolbar_Publications, "");
                publicationPresenter.toggle();
                view.closeAllOtherPanels(view.getPublicationButton());
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
				view.closeAllOtherPanels(view.getSkyPanelButton());
			}
		});
        
        CommonEventBus.getEventBus().addHandler(ShowPublicationSourcesEvent.TYPE, new ShowPublicationSourcesEventHandler() {
            
            @Override
            public void onEvent(ShowPublicationSourcesEvent event) {
                showPublicationInfo(event.rowData);
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
    
    private static long latestBibCodeTimeCall;
    
    public void showPublicationInfo (final GeneralJavaScriptObject rowData) {
        String bibcode = rowData.getStringProperty("bibcode");
        String authors = rowData.getStringProperty("author");
        getPublicationSources(bibcode, rowData.getStringProperty("title"), authors, 
                rowData.getStringProperty("pub"), rowData.getStringProperty("pubdate"));
        
    }
    public void showPublicationInfo (final String bibcode) {
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
                    String authorList = "";
                    
                    for(int i = 0; i < rowList.getData().size(); i++) {
                    	authorList += rowList.getDataValue("author", i) + "\n";
                    }
                    if(authorList.length() > 0) {
                    	authorList = authorList.substring(0, authorList.length() - 1);
                    }
                    
                    getPublicationSources(bibcode, rowList.getDataValue("title", 0), authorList, rowList.getDataValue("pub", 0), rowList.getDataValue("pubdate", 0));
                    
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
    

    private void getPublicationSources(final String bibcode, final String title, final String authors, final String journal, final String date) {
        final String retrievingSourcesId = "Retrieving Sources";
        final int maxSources = (DeviceUtils.isMobile() ? EsaSkyWebConstants.MAX_SHAPES_FOR_MOBILE : EsaSkyWebConstants.MAX_SOURCES_IN_TARGETLIST);
        
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
                
                
                final PublicationsDescriptor descriptor = DescriptorRepository.getInstance().getPublicationsDescriptors().getDescriptors().get(0);   
                final String titleHtml = "<h3 style='font-size: 0.85em;'>" + title + "</h3>" +
                        "<h5>" + TextMgr.getInstance().getText("ctrlToolBarPresenter_bibcode").replace("$HTML$", getLinkHtml(bibcode, descriptor.getArchiveURL(), descriptor.getArchiveProductURI()).asString()) + "</h5>" + 
                        "<h5>" + TextMgr.getInstance().getText("ctrlToolBarPresenter_authors").replace("$HTML$", ESASkyJavaScriptLibrary.createLinkList(authors, 3)) + "</h5>" +
                        "<h5>" + TextMgr.getInstance().getText("ctrlToolBarPresenter_journal").replace("$JOURNAL$", journal).replace("$DATE$", date) + "</h5>" +
                        "<h4>" + TextMgr.getInstance().getText("ctrlToolBarPresenter_pubSources") + "</h4>";
                view.showSearchResultsOnTargetList(searchResult, titleHtml + getNumSourcesText(searchResult.size(), maxSources));
            }
            
            @Override
            public void onError(String errorCause) {
                Log.error("[CtrlToolBarPresenter] showPublicationInfo ERROR: " + errorCause);
                CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPopEvent(retrievingSourcesId));
            }
            
        });
    }
    
    
    public void showAuthorInfo (final String author, final String splitByString, final String authorsLinkUrl, final String replaceString) {
        
        Log.info("[CtrlToolBarPresenter] showAuthorInfo AUTHOR received: " + author + " , preparing author info.");
        
        final int maxSources = (DeviceUtils.isMobile() ? EsaSkyWebConstants.MAX_SHAPES_FOR_MOBILE : EsaSkyWebConstants.MAX_SOURCES_IN_TARGETLIST);
        CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPushEvent("LoadingAuthorPublicatoinSources", 
        		TextMgr.getInstance().getText("ctrlToolBarPresenter_loadingAuthorSources").replace("$AUTHOR$", author)));
        //Retrieves the sources for this bibcode and shows the upload panel
        JSONUtils.getJSONFromUrl(EsaSkyWebConstants.PUBLICATIONS_SOURCES_BY_AUTHOR_URL + "?AUTHOR="
                + URL.encodeQueryString(author) + "&ROWS=" + maxSources, new IJSONRequestCallback() {
            
            @Override
            public void onSuccess(String responseText) {
            	try {
            		CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPopEvent("LoadingAuthorPublicatoinSources"));
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
            	} catch(Exception e) {
            		onError(e.getMessage());
            	}
            }
            
            @Override
            public void onError(String errorCause) {
                Log.error("[CtrlToolBarPresenter] showAuthorInfo ERROR: " + errorCause);
                CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPopEvent("LoadingAuthorPublicatoinSources"));
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
    
    private SafeHtml getLinkHtml(String value, String archiveURL, String archiveProductUrl) {
        
        String[] archiveProductURI = archiveProductUrl.split("@@@");

        StringBuilder finalURI = new StringBuilder(archiveURL);
        for (int i = 0; i < archiveProductURI.length; i++) {
            if (i % 2 == 0) {
                finalURI.append(archiveProductURI[i]);
            } else {
                finalURI.append(value);
            }
        }
        
        SafeHtmlBuilder sb = new SafeHtmlBuilder();
        
        sb.appendHtmlConstant("<a href='" + finalURI.toString()
                + "' onclick=\"trackOutboundLink('" + finalURI.toString()
                + "'); return false; \" target='_blank' >"
                + value + "</a>");
     
        return sb.toSafeHtml();
    }
}