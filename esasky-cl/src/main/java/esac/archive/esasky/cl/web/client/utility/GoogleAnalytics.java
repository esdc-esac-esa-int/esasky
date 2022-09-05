package esac.archive.esasky.cl.web.client.utility;

import com.allen_sauer.gwt.log.client.Log;

public final class GoogleAnalytics {

    //Categories
    public static final String CAT_OUTBOUND = "outbound";
	public static final String CAT_SOURCE_TOOLTIP = "SourceTooltip";
	public static final String CAT_CONTEXT_MENU = "ContextMenu";
	public static final String CAT_TREEMAP_RESIZE = "TreeMap_Resize";
    public static final String CAT_TREEMAP = "TreeMap";
	public static final String CAT_TAB_RESIZE = "Tab_Resize";
    public static final String CAT_TAB_OPENED = "TabOpened";
    public static final String CAT_DOWNLOAD_CSV = "Download_CSV";
    public static final String CAT_DOWNLOAD_VOT = "Download_VOT";
    public static final String CAT_DOWNLOAD_PREVIEW = "Download_Preview";
    public static final String CAT_DOWNLOAD_DD = "Download_DD";
    public static final String CAT_CTRLTOOLBAR = "CtrlToolbar";
    public static final String CAT_SKIESMENU = "SkiesMenu";
    public static final String CAT_GW = "GravitationalWaves";
    public static final String CAT_CONVENIENCE = "Convenience";
    public static final String CAT_HELP = "Help";
    public static final String CAT_HEADER = "Header";
    public static final String CAT_HEADER_STATUS = "Header_Status";
    public static final String CAT_TARGETLIST = "TargetList";
    public static final String CAT_PLANNINGTOOL = "PlanningTool";
    public static final String CAT_TABTOOLBAR_RECENTER = "TabToolbar_Recenter";
    public static final String CAT_TABTOOLBAR_REFRESH = "TabToolbar_Refresh";
    public static final String CAT_TABTOOLBAR_CLOSEALL = "TabToolbar_CloseAll";
    public static final String CAT_TABTOOLBAR_SENDTOSAMP = "TabToolbar_SendToSAMP";
    public static final String CAT_TABTOOLBAR_SETSTYLE = "TabToolbar_SetStyle";
    public static final String CAT_TABROW_SENDTOVOTOOLS = "TabRow_SendToVOTools";
    public static final String CAT_TABROW_SOURCESINPUBLICATION = "TabRow_SourcesInPublication";
    public static final String CAT_TABROW_DOWNLOAD = "TabRow_Download";
    public static final String CAT_TABROW_RECENTER = "TabRow_Recenter";
    public static final String CAT_API = "API";
    public static final String CAT_PUBLICATION = "Publication";
    public static final String CAT_WELCOME = "Welcome";
    public static final String CAT_PREVIEW = "Preview";
    public static final String CAT_DATALINK = "Datalink";
    public static final String CAT_DOWNLOADROW = "DownloadRow";
    public static final String CAT_DOWNLOADDIALOG = "DownloadDialog";
    public static final String CAT_SEARCH = "Search";
    public static final String CAT_SAMP = "Samp";
    public static final String CAT_COUNT = "Count";
    public static final String CAT_FILTER = "Filter";
    public static final String CAT_SCREENSHOT = "Screenshot";
    public static final String CAT_INTERNATIONALIZATION = "Internationalization";
    public static final String CAT_PYESASKY = "Pyesasky";
    public static final String CAT_JAVASCRIPTAPI = "JavaScriptAPI";
    public static final String CAT_SLIDER = "Slider";
    public static final String CAT_REQUESTERROR = "RequestError";
    public static final String CAT_EXTERNALTAPS = "ExternalTaps";
    public static final String CAT_TEXTMANAGER = "TextManager";
    public static final String CAT_TOGGLECOLUMNS = "ToggleColumns";
    public static final String CAT_BROWSEHIPS = "BrowseHips";
    public static final String CAT_IMAGES = "Images";
    public static final String CAT_OUTREACHIMAGES = "OutreachImages";
    public static final String CAT_JWSTOUTREACHIMAGES = "jwstOutreachImages";
    public static final String CAT_SELECTSKY = "SelectSky";
    public static final String CAT_EVA = "Eva";
    
    //Actions
    public static final String ACT_MISSINGTRANSLATION = "MissingTranslation";
    public static final String ACT_LOADINGOFXMLFAILED = "LoadingOfXMLFailed";
    public static final String ACT_HEADER_VIEWINWWT = "ViewInWWT";
    public static final String ACT_HEADER_HIPSNAME = "HipsName";
    public static final String ACT_HEADER_SHARE = "Share";
    public static final String ACT_HEADER_HELP = "Help";
    public static final String ACT_HEADER_EVA = "Eva";
    public static final String ACT_HEADER_MENU = "Menu";
    public static final String ACT_HEADER_FEEDBACK = "Feedback";
    public static final String ACT_HEADER_VIDEOTUTORIALS = "VideoTutorials";
    public static final String ACT_HEADER_RELEASENOTES = "ReleaseNotes";
    public static final String ACT_HEADER_COORDINATEGRID = "CoordinateGrid";
    public static final String ACT_HEADER_NEWSLETTER = "Newsletter";
    public static final String ACT_HEADER_ABOUTUS = "AboutUs";
    public static final String ACT_HEADER_ACKNOWLEDGE = "Acknowledge";
    public static final String ACT_HEADER_SCREENSHOT = "ScreenShot";
    public static final String ACT_HEADER_SCIMODE = "SciMode";
    public static final String ACT_HEADER_LANGUAGE = "Language";
    public static final String ACT_HEADER_STATUS_ERROR = "Error";
    public static final String ACT_HEADER_COOFRAMECHANGED= "CoordinateFrameChanged";
    
    public static final String ACT_TOGGLECOLUMNSOPEN = "ToggleColumnsOpen";
    public static final String ACT_TOGGLECOLUMNSSHOW = "ShowingColumn";
    public static final String ACT_TOGGLECOLUMNSHIDE = "HidingColumn";
    
    public static final String ACT_CONTEXTMENU_VIEWINWWT = "ViewInWWT";
    public static final String ACT_CONTEXTMENU_SEARCHINSIMBAD = "SearchInSimbad";
    public static final String ACT_CONTEXTMENU_SEARCHINNED = "SearchInNed";
    public static final String ACT_CONTEXTMENU_SEARCHINVIZIER = "SearchInVizier";
    public static final String ACT_CONTEXTMENU_SEARCHINVIZIERPHOTOMETRY = "SearchInVizierPhotometry";
    
    public static final String ACT_SOURCETOOLTIP_VIEWINSIMBAD = "ViewInSimbad";
    public static final String ACT_SOURCETOOLTIP_VIEWINNED = "ViewInNed";
    public static final String ACT_SOURCETOOLTIP_VIEWINVIZIER = "ViewInVizier";
    public static final String ACT_SOURCETOOLTIP_VIEWINVIZIERPHOTOMETRY = "ViewInVizierPhotometry";
    public static final String ACT_SOURCETOOLTIP_VIEWINWWT = "ViewInWWT";
    
    public static final String ACT_CTRLTOOLBAR_SKIES = "Skies";
    public static final String ACT_CTRLTOOLBAR_TARGETLIST = "TargetList";
    public static final String ACT_CTRLTOOLBAR_PLANNINGTOOL = "PlanningTool";
    public static final String ACT_CTRLTOOLBAR_PUBLICATIONS = "Publications";
    public static final String ACT_CTRLTOOLBAR_GW = "GravitationalWave";
    public static final String ACT_CTRLTOOLBAR_OUTREACH_IMAGE = "OutreachImage";
    public static final String ACT_CTRLTOOLBAR_JWST_IMAGE = "jwstOutreachImage";
    public static final String ACT_CTRLTOOLBAR_SESSION_SAVE = "SaveSession";
    public static final String ACT_CTRLTOOLBAR_SESSION_RESTORE = "RestoreSession";
    public static final String ACT_CTRLTOOLBAR_DICE = "Dice";
    public static final String ACT_CTRLTOOLBAR_CUSTOMBUTTON = "CustomButton";
    
    public static final String ACT_SKIESMENU_SELECTEDSKY = "SelectedSky";
    public static final String ACT_SKIESMENU_SKYINFOSHOWN = "SkyInfoShown";
    public static final String ACT_SKIESMENU_ADDSKYCLICK = "AddSkyClick";
    public static final String ACT_SKIESMENU_ADDURL = "AddUrl";
    public static final String ACT_SKIESMENU_ADDURL_FAIL = "AddUrlFail";
    public static final String ACT_SKIESMENU_BROWSEHIPS = "BrowseHips";
    public static final String ACT_SKIESMENU_ADDLOCAL = "AddLocal";
    public static final String ACT_SKIESMENU_ADDLOCALCLICK = "AddLocalClick";

    public static final String ACT_GW_SHOW_HIPS = "ShowHips";
    public static final String ACT_GW_SHOW_HIPS_FAIL = "ShowHipsFail";
    public static final String ACT_GW_ROW_SELECTED = "RowSelected";
    
    public static final String ACT_TARGETLIST_LISTSELECTED = "ListSelected";
    public static final String ACT_TARGETLIST_UPLOADERROR = "UploadError";
    public static final String ACT_TARGETLIST_UPLOADSUCCESS = "UploadSuccess";
    
    public static final String ACT_PLANNINGTOOL_INSTRUMENTSELECTED = "InstrumentSelected";
    public static final String ACT_PLANNINGTOOL_ALLINSTRUMENTSCLICK = "AllInstrumentsClick";
    public static final String ACT_PLANNINGTOOL_COPYCOORDINATES = "CopyCoordinates";
    public static final String ACT_PLANNINGTOOL_DETECTORSELECTED = "DetectorSelected";

    public static final String ACT_SAMP_ERROR = "SAMP failed";
    
    public static final String ACT_API_AUTHORINURL = "AuthorInURL";
    public static final String ACT_API_BIBCODEINURL = "BibcodeInURL";
    
    public static final String ACT_DATAPANEL_PAGER_NEXTPAGE = "NextPage";
    public static final String ACT_DATAPANEL_PAGER_PREVIOUSPAGE = "PreviousPage";
    public static final String ACT_MOVED = "Moved";
    
    public static final String ACT_SEARCH_SEARCHQUERY = "SearchQuery";
    public static final String ACT_SEARCH_SEARCHRESULTCLICK = "SearchResultClick";
    public static final String ACT_SEARCH_SEARCHAUTHORRESULTCLICK = "SearchResultAuthorClick";
    public static final String ACT_SEARCH_SEARCHAUTHORRESULTSHOWMORECLICK = "SearchResultAuthorShowMoreClick";
    public static final String ACT_SEARCH_SEARCHBIBCODERESULTCLICK = "SearchResultBibcodeClick";
    public static final String ACT_SEARCH_SEARCHBIBCODERESULTSHOWMORECLICK = "SearchResultBibcodeShowMoreClick";
    public static final String ACT_SEARCH_SEARCHSSORESULTSHOWMORECLICK = "SearchResultSsoShowMoreClick";
    public static final String ACT_SEARCH_SEARCHRESULTAUTO = "SearchResultAuto";
    public static final String ACT_SEARCH_SEARCHTARGETNOTFOUND = "SearchTargetNotFound";
    public static final String ACT_SEARCH_SEARCHWRONGCOORDS = "SearchWrongCoords";
    public static final String ACT_SEARCH_SEARCHCOORDSSUCCESS = "SearchCoordsSuccess";
    
    public static final String ACT_PREVIEW_POSTCARDLOADFAILED = "PostcardLoadFailed";
    
    public static final String ACT_DATALINK_LOADFAILED = "DatalinkLoadFailed";
    
    public static final String ACT_PUBLICATION_BOXQUERY = "PublicationBoxQueryTotal";
    public static final String ACT_PUBLICATION_BOXQUERYFAILED = "PublicationBoxQueryFailed";
    public static final String ACT_PUBLICATION_UPDATE = "PublicationUpdateButton";
    public static final String ACT_PUBLICATION_REMOVE = "PublicationRemoveButton";
    public static final String ACT_PUBLICATION_UPDATEONMOVE = "PublicationUpdateOnMoveChanged";
    public static final String ACT_PUBLICATION_MOSTORLEAST = "PublicationMostOrLeastChanged";
    public static final String ACT_PUBLICATION_MOVETRIGGEREDBOXQUERY = "PublicationBoxQueryTriggeredByMoveOperation";
    public static final String ACT_PUBLICATION_TRUNCATIONNUMBERCHANGED = "PublicationTruncationNumbrtChanged";
    
    public static final String ACT_TAB_RESIZE = "TabResize";
    public static final String ACT_TREEMAP_RESIZE = "TreeMapResize";
    
    public static final String ACT_TAB_DOWNLOAD = "TabDownload";
    public static final String ACT_TAB_DOWNLOAD_FAILURE = "TabDownloadFailure";

    public static final String ACT_COUNT_COUNT = "Count";
    
    public static final String ACT_PLAYER_PLAY = "Play";
    public static final String ACT_PLAYER_PAUSE = "Pause";
    public static final String ACT_PLAYER_NEXT = "Next";
    public static final String ACT_PLAYER_PREVIOUS = "Previous";
    
    public static final String ACT_WELCOME_SCIENCE = "Science";
    public static final String ACT_WELCOME_EXPLORER = "Explorer";
    public static final String ACT_WELCOME_CLOSE = "CloseWithoutSelection";
    public static final String ACT_WELCOME_DONOTSHOWAGAIN = "DoNotShowAgain";

    public static final String ACT_DOWNLOAD_JUPYTER = "DownloadJupyter";
    public static final String ACT_DOWNLOAD_COMPUTER= "DownloadComputer";
    
    public static final String ACT_SLIDER_MOVED = "Moved";
    
    public static final String ACT_PYESASKY_GOTOTARGETNAME = "goToTargetName";
    public static final String ACT_PYESASKY_SETFOV = "setFoV";
    public static final String ACT_PYESASKY_GOTORADEC = "goToRADec";
    public static final String ACT_PYESASKY_SETHIPSCOLORPALETTE = "setHiPSColorPalette";
    public static final String ACT_PYESASKY_CHANGEHIPS = "changeHiPS";
    public static final String ACT_PYESASKY_CHANGEHIPSWITHPARAMS = "changeHiPSWithParams";
    public static final String ACT_PYESASKY_OVERLAYFOOTPRINTS = "overlayFootprints";
    public static final String ACT_PYESASKY_OVERLAYFOOTPRINTSWITHDETAILS = "overlayFootprintsWithDetails";
    public static final String ACT_PYESASKY_CLEARFOOTPRINTSOVERLAY = "clearFootprintsOverlay";
    public static final String ACT_PYESASKY_DELETEFOOTPRINTSOVERLAY = "deleteFootprintsOverlay";
    public static final String ACT_PYESASKY_OVERLAYCATALOGUE = "overlayCatalogue";
    public static final String ACT_PYESASKY_OVERLAYCATALOGUEWITHDETAILS = "overlayCatalogueWithDetails";
    public static final String ACT_PYESASKY_REMOVECATALOGUE = "removeCatalogue";
    public static final String ACT_PYESASKY_CLEARCATALOGUE = "clearCatalogue";
    public static final String ACT_PYESASKY_GETAVAILABLEHIPS = "getAvailableHiPS";
    public static final String ACT_PYESASKY_ADDJWSTWITHCOORDINATES = "addJwstWithCoordinates";
    public static final String ACT_PYESASKY_ADDJWST = "addJwst";
    public static final String ACT_PYESASKY_CLOSEJWSTPANEL = "closeJwstPanel";
    public static final String ACT_PYESASKY_OPENJWSTPANEL = "openJwstPanel";
    public static final String ACT_PYESASKY_CLEARJWSTALL = "clearJwstAll";
    public static final String ACT_PYESASKY_GETCENTER = "getCenter";
    public static final String ACT_PYESASKY_GETOBSERVATIONSCOUNT = "getObservationsCount";
    public static final String ACT_PYESASKY_COUNT = "count";
    public static final String ACT_PYESASKY_GETCATALOGUESCOUNT = "getCataloguesCount";
    public static final String ACT_PYESASKY_GETSPECTRACOUNT = "getSpectraCount";
    public static final String ACT_PYESASKY_PLOTOBSERVATIONS = "plotObservations";
    public static final String ACT_PYESASKY_PLOTCATALOGUES = "plotCatalogues";
    public static final String ACT_PYESASKY_PLOTSPECTRA = "plotSpectra";
    public static final String ACT_PYESASKY_PLOTPUBLICATIONS = "plotPublications";
    public static final String ACT_PYESASKY_GETRESULTPANELDATA = "getResultPanelData";
    public static final String ACT_PYESASKY_SHOWCOORDINATEGRID = "showCoordinateGrid";
    public static final String ACT_PYESASKY_OPENSKYPANEL = "openSkyPanel";
    public static final String ACT_PYESASKY_CLOSESKYPANEL = "closeSkyPanel";
    public static final String ACT_PYESASKY_ADDHIPS = "addHips";
    public static final String ACT_PYESASKY_REMOVEHIPSONINDEX = "removeHipsOnIndex";
    public static final String ACT_PYESASKY_GETNUMBEROFSKYROWS = "getNumberOfSkyRows";
    public static final String ACT_PYESASKY_SETHIPSSLIDERVALUE = "setHipsSliderValue";
    
    public static final String ACT_EXTTAP_MISSINGCOLLECTION = "missingCollection";
    public static final String ACT_EXTTAP_MISSINGPRODUCTTYPE = "missingDataproduct";
    public static final String ACT_EXTTAP_GETTINGDATA = "gettingData";
    public static final String ACT_EXTTAP_BROWSING = "browsing";
    public static final String ACT_EXTTAP_COUNT = "count";
    
    public static final String ACT_TEXTMANAGER_SETLANG = "setLang";
    
    public static final String ACT_OUTBOUND_CLICK = "click";
    
    public static final String ACT_IMAGES_HSTIMAGE_SUCCESS = "hstImageSuccess";
    public static final String ACT_IMAGES_HSTIMAGE_FAIL = "hstImageFail";

    
    //Send events methods
	public static native void sendEvent(String eventCategory, String eventAction, String eventLabel)/*-{
		try{
        	$wnd._paq.push(['trackEvent', eventCategory, eventAction, eventLabel]);
        	@esac.archive.esasky.cl.web.client.utility.GoogleAnalytics::logEvent(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(eventCategory, eventAction, eventLabel);
		}
		catch(e){}			
	}-*/; 

	public static void sendEventWithURL(String eventCategory, String eventAction){
	    sendEventWithURL(eventCategory, eventAction, "");
    }
	
	public static void sendEventWithURL(String eventCategory, String eventAction, String extra){
        sendEvent(eventCategory, eventAction, UrlUtils.getUrlForCurrentState() + "Extras: " + extra);
    }
	
	public static void logEvent(String eventCategory, String eventAction, String label){
	    Log.debug("GA Event - Category: " + eventCategory + " - Action: " + eventAction + " - Label: " + label);
	}
}
