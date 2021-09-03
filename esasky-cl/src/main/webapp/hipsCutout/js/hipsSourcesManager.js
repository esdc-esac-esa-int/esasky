// HiPS Sources Manager
function HipsSourcesManager (onHipsReadyFn) {

  var currentObj = this;

  this.onHipsReadyFn = onHipsReadyFn;
  this.hipsSources = null;

  this.init = function() {

    this.downloadHipsSources();

  };

  this.downloadHipsSources = function () {
    var url = CONFIG.BASE_URL + CONFIG.HIPS_SOURCES_ENDPOINT + "?_=" + new Date().getTime();
    $.ajax({
      url: url,
      success: function( hipsSources ) {
        currentObj.setHipsSources(hipsSources);
      },
      error: function( result ) {
        console.error("Error downloading HiPS Sources: " + url);
      }
    });
  }

  this.setHipsSources = function (hipsSources) {
    this.hipsSources = hipsSources;
    this.onHipsReadyFn();
  }

  this.init();
}

function fillSkySelector (skySelector, hipsSources){
  skySelector.html("");

  for (menuEntryIdx in hipsSources.menuEntries){
   if (hipsSources.menuEntries.hasOwnProperty(menuEntryIdx)) {
	    var menuEntry = hipsSources.menuEntries[menuEntryIdx];
	    for (hipsIdx in menuEntry.hips){
	    	 if (menuEntry.hips.hasOwnProperty(hipsIdx)) {
		      var hips = menuEntry.hips[hipsIdx];
		      var sky = "<option value=\"" + hips.surveyId + "\">" + hips.surveyName + "</option>"
		      skySelector.append(sky);
		     }
	    }
    }
  }

  skySelector.val(CONFIG.DEFAULT_SKY_SURVEY_ID);
}
