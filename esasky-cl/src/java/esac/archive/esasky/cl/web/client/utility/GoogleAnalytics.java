package esac.archive.esasky.cl.web.client.utility;

public final class GoogleAnalytics {

    //Categories
	public static final String CAT_SourceTooltip = "SourceTooltip";
	public static final String CAT_ContextMenu = "ContextMenu";
	public static final String CAT_TreeMap_Resize = "TreeMap_Resize";
	public static final String CAT_Tab_Resize = "Tab_Resize";
    public static final String CAT_TabOpened = "TabOpened";
    public static final String CAT_Download_CSV = "Download_CSV";
    public static final String CAT_Download_VOT = "Download_VOT";
    public static final String CAT_Download_Preview = "Download_Preview";
    public static final String CAT_Download_DD = "Download_DD";
    public static final String CAT_CtrlToolbar = "CtrlToolbar";
    public static final String CAT_SkiesMenu = "SkiesMenu";
    public static final String CAT_Help = "Help";
    public static final String CAT_Header = "Header";
    public static final String CAT_Header_Status = "Header_Status";
    public static final String CAT_TargetList = "TargetList";
    public static final String CAT_PlanningTool = "PlanningTool";
    public static final String CAT_TabToolbar_Recenter = "TabToolbar_Recenter";
    public static final String CAT_TabToolbar_Refresh = "TabToolbar_Refresh";
    public static final String CAT_TabToolbar_SendToSAMP = "TabToolbar_SendToSAMP";
    public static final String CAT_TabToolbar_SetStyle = "TabToolbar_SetStyle";
    public static final String CAT_TabRow_SendToVOTools = "TabRow_SendToVOTools";
    public static final String CAT_TabRow_SourcesInPublication = "TabRow_SourcesInPublication";
    public static final String CAT_TabRow_Download = "TabRow_Download";
    public static final String CAT_TabRow_Recenter = "TabRow_Recenter";
    public static final String CAT_API = "API";
    public static final String CAT_Welcome = "Welcome";
    public static final String CAT_Preview = "Preview";
    public static final String CAT_DataPanel_Pager = "DataPanel_Pager";
    public static final String CAT_Search = "Search";
    public static final String CAT_Count = "Count";
    public static final String CAT_Filter = "Filter";
    public static final String CAT_Screenshot = "Screenshot";
    public static final String CAT_Internationalization = "Internationalization";
    
    //Actions
    public static final String ACT_MissingTranslation = "MissingTranslation";
    public static final String ACT_LoadingOfXMLFailed = "LoadingOfXMLFailed";
    public static final String ACT_Header_ViewInWwt = "ViewInWWT";
    public static final String ACT_Header_HipsName = "HipsName";
    public static final String ACT_Header_Share = "Share";
    public static final String ACT_Header_Help = "Help";
    public static final String ACT_Header_Menu = "Menu";
    public static final String ACT_Header_Feedback = "Feedback";
    public static final String ACT_Header_VideoTutorials = "VideoTutorials";
    public static final String ACT_Header_ReleaseNotes = "ReleaseNotes";
    public static final String ACT_Header_Newsletter = "Newsletter";
    public static final String ACT_Header_AboutUs = "AboutUs";
    public static final String ACT_Header_ScreenShot = "ScreenShot";
    public static final String ACT_Header_SciMode = "SciMode";
    public static final String ACT_Header_Language = "Language";
    public static final String ACT_Header_Status_Error = "Error";
    
    public static final String ACT_ContextMenu_ViewInWwt = "ViewInWWT";
    public static final String ACT_ContextMenu_SearchInSimbad = "SearchInSimbad";
    public static final String ACT_ContextMenu_SearchInNed = "SearchInNed";
    public static final String ACT_ContextMenu_SearchInVizier = "SearchInVizier";
    public static final String ACT_ContextMenu_SearchInVizierPhotometry = "SearchInVizierPhotometry";
    
    public static final String ACT_SourceTooltip_ViewInSimbad = "ViewInSimbad";
    public static final String ACT_SourceTooltip_ViewInNed = "ViewInNed";
    public static final String ACT_SourceTooltip_ViewInVizier = "ViewInVizier";
    public static final String ACT_SourceTooltip_ViewInVizierPhotometry = "ViewInVizierPhotometry";
    public static final String ACT_SourceTooltip_ViewInWWT = "ViewInWWT";
    
    public static final String ACT_CtrlToolbar_Skies = "Skies";
    public static final String ACT_CtrlToolbar_TargetList = "TargetList";
    public static final String ACT_CtrlToolbar_PlanningTool = "PlanningTool";
    public static final String ACT_CtrlToolbar_Publications = "Publications";
    public static final String ACT_CtrlToolbar_Dice = "Dice";
    
    public static final String ACT_SkiesMenu_SelectedSky = "SelectedSky";
    public static final String ACT_SkiesMenu_SkyInfoShown = "SkyInfoShown";
    public static final String ACT_SkiesMenu_AddSkyClick = "AddSkyClick";
    
    public static final String ACT_TargetList_ListSelected = "ListSelected";
    public static final String ACT_TargetList_UploadError = "UploadError";
    public static final String ACT_TargetList_UploadSuccess = "UploadSuccess";
    
    public static final String ACT_PlanningTool_InstrumentSelected = "InstrumentSelected";
    public static final String ACT_PlanningTool_AllInstrumentsClick = "AllInstrumentsClick";
    public static final String ACT_PlanningTool_CopyCoordinates = "CopyCoordinates";
    public static final String ACT_PlanningTool_DetectorSelected = "DetectorSelected";
    
    public static final String ACT_API_AuthorInURL = "AuthorInURL";
    public static final String ACT_API_BibcodeInURL = "BibcodeInURL";
    
    public static final String ACT_DataPanel_Pager_NextPage = "NextPage";
    public static final String ACT_DataPanel_Pager_PreviousPage = "PreviousPage";
    public static final String ACT_Moved = "Moved";
    
    public static final String ACT_Search_SearchQuery = "SearchQuery";
    public static final String ACT_Search_SearchResultClick = "SearchResultClick";
    public static final String ACT_Search_SearchResultAuto = "SearchResultAuto";
    public static final String ACT_Search_SearchTargetNotFound = "SearchTargetNotFound";
    public static final String ACT_Search_SearchWrongCoords = "SearchWrongCoords";
    
    public static final String ACT_Tab_Resize = "TabResize";
    public static final String ACT_TreeMap_Resize = "TreeMapResize";
    
    public static final String ACT_Count_Count = "Count";
    
    
    //Send events methods
	public static native void sendEvent(String eventCategory, String eventAction, String eventLabel)/*-{
		try{
        	$wnd.ga('send', 'event', eventCategory, eventAction, eventLabel);
		}
		catch(e){}			
	}-*/; 

	public static void sendEventWithURL(String eventCategory, String eventAction){
	    sendEventWithURL(eventCategory, eventAction, "");
    }
	
	public static void sendEventWithURL(String eventCategory, String eventAction, String extra){
        sendEvent(eventCategory, eventAction, UrlUtils.getUrlForCurrentState() + extra);
    }
}
