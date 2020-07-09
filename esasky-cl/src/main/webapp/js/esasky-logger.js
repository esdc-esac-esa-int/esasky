var startupTime = Date.now();
console.defaultLog = console.log.bind(console);
console.logs = [];
console.log = function(){
	if(window.location.href.includes("log_level=DEBUG")){
    	console.defaultLog.apply(console, arguments);
	}
    console.logs.push(Array.from(arguments));
}
console.defaultError = console.error.bind(console);
console.errors = [];
console.error = function(){
	if(window.location.href.includes("log_level=DEBUG")){
	    console.defaultError.apply(console, arguments);
	}	
    console.errors.push(Array.from(arguments));
    try{
	    var lastDebugs = [];
	    if(console.debugs.length < 20){
			lastDebugs = console.debugs;    
	    } else {
	    	for(var i = 20; i>0; i--){
	    		lastDebugs.push(console.debugs[console.debugs.length - i]);
	    	}
	    }
	    //window.ga('send', 'event', "Error", "console.error", "Time since startup: " 
	    $wnd._paq.push(['trackEvent',  "Error", "console.error", "Time since startup: " 
	    	+ (Date.now() - startupTime) + " (millis)"
	    	+ " |||| Logged Errors: " + console.errors + " |||| Logged Warnings: " + console.warns 
	    	+ " |||| Last Logged debugs: " + lastDebugs]);
	    	
    } catch(e){
    }
}
console.defaultWarn = console.warn.bind(console);
console.warns = [];
console.warn = function(){
	if(window.location.href.includes("log_level=DEBUG")){
    	console.defaultWarn.apply(console, arguments);
    }
    console.warns.push(Array.from(arguments));
}

console.defaultDebug = console.debug.bind(console);
console.debugs = [];
console.debug = function(){
	if(window.location.href.includes("log_level=DEBUG")){
    	console.defaultDebug.apply(console, arguments);
    }
    console.debugs.push(Array.from(arguments));
}
window.addEventListener('error', function(e) {
try{
    //window.ga('send', 'event', "Error", "Problem with source file: " + e.target.src, "Time since startup: " 
    $wnd._paq.push(['trackEvent',   "Error", "Problem with source file: " + e.target.src, "Time since startup: " 
    + (Date.now() - startupTime) + " (millis)"
    + " |||| Logged Errors: " + console.errors + " |||| Logged Warnings: " + console.warns 
    + " |||| Logged debugs: " + console.debugs + " |||| Logged logs: " + console.logs]);
    } catch(e){
    }
}, true);






var slowLoadTime = 60000;
var timer = setTimeout(tellUserAboutReload, slowLoadTime);

function tellUserAboutReload(){

	var reloadMessage = document.getElementById("reloadMessage");
	if(reloadMessage != null){
		reloadMessage.innerHTML = "<p style=\"color:white; font-family: Arial Unicode MS, Arial, sans-serif;\">ESASky should have finished loading by now. Please <a target=\"_blank\" href=\"https://esdc.userecho.com/communities/1-esasky\" style=\"color:#AAAAFF\">get in touch</a> so we can help you get ESASky running.";
		
		var nVer = navigator.appVersion;
		var nAgt = navigator.userAgent;
		var browserName  = navigator.appName;
		var fullVersion  = ''+parseFloat(navigator.appVersion); 
		var majorVersion = parseInt(navigator.appVersion,10);
		var nameOffset,verOffset,ix;

		if ((verOffset=nAgt.indexOf("Opera"))!=-1) {
		 browserName = "Opera";
		 fullVersion = nAgt.substring(verOffset+6);
		 if ((verOffset=nAgt.indexOf("Version"))!=-1) 
		   fullVersion = nAgt.substring(verOffset+8);
		}
		else if ((verOffset=nAgt.indexOf("MSIE"))!=-1) {
		 browserName = "Microsoft Internet Explorer";
		 fullVersion = nAgt.substring(verOffset+5);
		}
		else if ((verOffset=nAgt.indexOf("Chrome"))!=-1) {
		 browserName = "Chrome";
		 fullVersion = nAgt.substring(verOffset+7);
		}
		else if ((verOffset=nAgt.indexOf("Safari"))!=-1) {
		 browserName = "Safari";
		 fullVersion = nAgt.substring(verOffset+7);
		 if ((verOffset=nAgt.indexOf("Version"))!=-1) 
		   fullVersion = nAgt.substring(verOffset+8);
		}
		else if ((verOffset=nAgt.indexOf("Firefox"))!=-1) {
		 browserName = "Firefox";
		 fullVersion = nAgt.substring(verOffset+8);
		}
		else if ( (nameOffset=nAgt.lastIndexOf(' ')+1) < 
		          (verOffset=nAgt.lastIndexOf('/')) ) 
		{
		 browserName = nAgt.substring(nameOffset,verOffset);
		 fullVersion = nAgt.substring(verOffset+1);
		 if (browserName.toLowerCase()==browserName.toUpperCase()) {
		  browserName = navigator.appName;
		 }
		}
		if ((ix=fullVersion.indexOf(";"))!=-1)
		   fullVersion=fullVersion.substring(0,ix);
		if ((ix=fullVersion.indexOf(" "))!=-1)
		   fullVersion=fullVersion.substring(0,ix);

		majorVersion = parseInt(''+fullVersion,10);
		if (isNaN(majorVersion)) {
		 fullVersion  = ''+parseFloat(navigator.appVersion); 
		 majorVersion = parseInt(navigator.appVersion,10);
		}

		var OSName="Unknown OS";
		if (navigator.appVersion.indexOf("Win")!=-1) OSName="Windows";
		if (navigator.appVersion.indexOf("Mac")!=-1) OSName="MacOS";
		if (navigator.appVersion.indexOf("X11")!=-1) OSName="UNIX";
		if (navigator.appVersion.indexOf("Linux")!=-1) OSName="Linux";

		var debugInfo = 'OS: '+OSName
		 +', Browser name  = '+browserName
		 +', Full version  = '+fullVersion
		 +', Major version = '+majorVersion
		 +', navigator.appName = '+navigator.appName
		 +', navigator.userAgent = '+navigator.userAgent;
		
		//window.ga('send', 'event', "slowLoad", "HasNotLoadedAfter " + slowLoadTime + " milliseconds", debugInfo);
		$wnd._paq.push(['trackEvent', "slowLoad", "HasNotLoadedAfter " + slowLoadTime + " milliseconds", debugInfo]);
	}
}
