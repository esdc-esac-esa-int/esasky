package esac.archive.esasky.cl.web.client.view.common;

public class ESASkyJavaScriptLibrary {

    public static void initialize() {
        createLinkListFormatter();
    }
    

    public static native void createLinkListFormatter() /*-{
        if(!$wnd.esasky){$wnd.esasky = {}}
        $wnd.esasky.linkListFormatter = function(value, maxShowingLinks){ 
            var valueList = value.split("\n");
            var appendedLinks = 0;
            var styleStr = "";
            var showAllAppended = false;
            var linkList = "";
            valueList.forEach(function(item, index, array){
                var url = "https://ui.adsabs.harvard.edu/#search/q=author%3A%22" + encodeURIComponent(item) + "%22&sort=date%20desc%2C%20bibcode%20desc";
                var isLastLink = (appendedLinks == valueList.length - 1);
                linkList += "<a href='" + url + "' onclick=\"trackOutboundLink('" + url 
                    + "'); event.stopPropagation(); return false; \" target='_blank' " 
                    + styleStr + ">" + item + ((!isLastLink) ? "," : "" ) + "</a>&nbsp;";
                                        
                if (appendedLinks > maxShowingLinks && !showAllAppended && !isLastLink) {
                    linkList += "<a href='#' " 
                                    + "onclick=\"$(this).parent().find('a').fadeIn(); $(this).hide(); " 
                                    + "event.stopPropagation(); return false; \" >et al.</a>";
                    styleStr = "style=\"display: none;\" ";
                    showAllAppended = true;
                }
                appendedLinks ++;
                    
            });
            return linkList;
        };
       
    }-*/;
    
    public static native String createLinkList(String value, int maxShowingLinks) /*-{
        return $wnd.esasky.linkListFormatter(value, maxShowingLinks);
    }-*/;
}
