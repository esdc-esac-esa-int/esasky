  var _paq = window._paq || [];
  /* tracker methods like "setCustomDimension" should be called before "trackPageView" */
  var siteId = '@matomo.site.id@';
  if(siteId == '2' || siteId == '3'){
  	_paq.push(['alwaysUseSendBeacon']);
  	_paq.push(['trackPageView']);
  	_paq.push(['enableLinkTracking']);
  	(function() {
    	var u="https://esdcwebanalytics.esac.esa.int/matomo/";
    	_paq.push(['setTrackerUrl', u+'matomo.php']);
    	_paq.push(['setSiteId', siteId]);
    	var d=document, g=d.createElement('script'), s=d.getElementsByTagName('script')[0];
    	g.type='text/javascript'; g.async=true; g.defer=true; g.src=u+'matomo.js'; s.parentNode.insertBefore(g,s);
  	})();
  };