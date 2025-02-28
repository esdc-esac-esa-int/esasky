/*
ESASky
Copyright (C) 2025 European Space Agency

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published
by the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package esac.archive.esasky.cl.web.client.view.common;

public class ESASkyJavaScriptLibrary {

    public static void initialize() {
        createLinkListFormatter();
        createPlotSineFunction();
        javaScriptInit();
    }
    

    public static native void javaScriptInit() /*-{
        $wnd.esasky.escapeXml = function(unsafe) {
            if(!unsafe){ return ""} 
            if(!unsafe.replace) {return unsafe}
            return unsafe.replace(/[<>&'"]/g, function (c) {
                switch (c) {
                    case '<': return '&lt;';
                    case '>': return '&gt;';
                    case '&': return '&amp;';
                    case '\'': return '&apos;';
                    case '"': return '&quot;';
                }
            });
        }
        $wnd.esasky.trackOutbound = function(element) {
            $wnd._paq.push(['trackEvent', 'Outbound', 'Click', element.href]);
        }
        
    }-*/;
        
    private static native void createLinkListFormatter() /*-{
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
                linkList += "<a href='" + url + "' target='_blank' onclick=\"esasky.trackOutbound(this); event.stopPropagation();\"" 
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
    
    public static native void plotSine(String id, int amplitude, double frequency, String color) /*-{
        return $wnd.esasky.plotSine(id, amplitude, frequency, color);
    }-*/;
    
    private static native void createPlotSineFunction() /*-{
        if(!$wnd.esasky){$wnd.esasky = {}}
        $wnd.esasky.plotSine = function(id, amplitude, frequency, color){ 
            var canvas = $doc.getElementById(id);
            if(!canvas){return;}
            var ctx = canvas.getContext("2d");
            var width = ctx.canvas.width;
            var height = ctx.canvas.height;
            ctx.clearRect(0, 0, canvas.width, canvas.height)
        
            ctx.beginPath();
            ctx.lineWidth = 1;
            ctx.strokeStyle = color;
            
            var x = 0;
            var y = 0;
            var amplitude = amplitude || 14;
            var frequency = frequency || 5;
        
            while (x < width) {
                y = height/2 + amplitude * Math.sin(x/frequency);
                ctx.lineTo(x, y);
                x = x + 1;
            }
            ctx.stroke();
        };
       
    }-*/;
    
    public static native String createLinkList(String value, int maxShowingLinks) /*-{
        return $wnd.esasky.linkListFormatter(value, maxShowingLinks);
    }-*/;


}
