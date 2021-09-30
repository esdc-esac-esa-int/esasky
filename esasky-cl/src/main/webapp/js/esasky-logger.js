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
	    window._paq.push(['trackEvent',  "Error", "console.error", "Time since startup: " 
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
    window._paq.push(['trackEvent',   "Error", "Problem with source file: " + e.target.src, "Time since startup: " 
    + (Date.now() - startupTime) + " (millis)"
    + " |||| Logged Errors: " + console.errors + " |||| Logged Warnings: " + console.warns 
    + " |||| Logged debugs: " + console.debugs + " |||| Logged logs: " + console.logs]);
    } catch(e){
    }
}, true);

