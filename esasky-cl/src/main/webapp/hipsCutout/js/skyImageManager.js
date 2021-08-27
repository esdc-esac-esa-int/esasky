// Sky Image Manager
function SkyImageManager (containerId, hipsSources) {

  var currentObj = this;

  this.container = $(".container").find("[containerId='" + containerId + "']" );
  this.hipsSources = hipsSources;

  currentObj.container.find("[name='coordinates_radio']").change(function() {
    currentObj.updateCoordinatesMode();
    window._paq.push(['trackEvent', CONFIG.GA_CATEGORY, "ChangeInputAreaType", currentObj.container.find("input[name='coordinates_radio']:checked").val()]);
  });
  currentObj.container.find(".polygon").change(function() {
    currentObj.refreshAladinPolygon();
  });
  currentObj.container.find("[type='ra']").change(function() {
    getInputFloatValueCropped(currentObj.container.find("[type='ra']"), CONFIG.COORDS.RA.DEFAULT, CONFIG.COORDS.RA.MIN, CONFIG.COORDS.RA.MAX);
  });
  currentObj.container.find("[type='dec']").change(function() {
    getInputFloatValueCropped(currentObj.container.find("[type='dec']"), CONFIG.COORDS.DEC.DEFAULT, CONFIG.COORDS.DEC.MIN, CONFIG.COORDS.DEC.MAX);
  });
  currentObj.container.find("[type='fov']").change(function() {
    getInputFloatValueCropped(currentObj.container.find("[type='fov']"), CONFIG.FOV.DEFAULT, CONFIG.FOV.MIN, CONFIG.FOV.MAX);
  });
  currentObj.container.find("[type='aspectRatio']").change(function() {
    getInputFloatValueCropped(currentObj.container.find("[type='aspectRatio']"), CONFIG.ASPECT_RATIO.DEFAULT, CONFIG.ASPECT_RATIO.MIN, CONFIG.ASPECT_RATIO.MAX);
    window._paq.push(['trackEvent', CONFIG.GA_CATEGORY, "aspectRatioChanged", currentObj.container.find("[type='aspectRatio']").val()]);
  });
  currentObj.container.find("[type='norder']").change(function() {
    getInputIntValueCropped(currentObj.container.find("[type='norder']"), CONFIG.NORDER.DEFAULT, CONFIG.NORDER.MIN, CONFIG.NORDER.MAX);
    window._paq.push(['trackEvent', CONFIG.GA_CATEGORY, "norderChanged", currentObj.container.find("[type='norder']").val()]);
  });
  currentObj.container.find("[type='size']").change(function() {
    getInputIntValueCropped(currentObj.container.find("[type='size']"), CONFIG.SIZE.DEFAULT, CONFIG.SIZE.MIN, CONFIG.SIZE.MAX);
    window._paq.push(['trackEvent', CONFIG.GA_CATEGORY, "sizeChanged", currentObj.container.find("[type='size']").val()]);
  });
  currentObj.container.find("[type='timeout']").change(function() {
    getInputIntValueCropped(currentObj.container.find("[type='timeout']"), CONFIG.TIMEOUT.DEFAULT, CONFIG.TIMEOUT.MIN, CONFIG.TIMEOUT.MAX);
    window._paq.push(['trackEvent', CONFIG.GA_CATEGORY, "timeoutChanged", currentObj.container.find("[type='timeout']").val()]);
  });
  currentObj.container.find("#norderCheck").change(function() {
    if (currentObj.container.find("#norderCheck").is(":checked")){
      currentObj.container.find("[type='norder']").show();
    } else {
      currentObj.container.find("[type='norder']").hide();
    }
    window._paq.push(['trackEvent', CONFIG.GA_CATEGORY, "nOrderCheckClick", currentObj.container.find("#norderCheck").is(":checked")]);
  });
  currentObj.container.find("#timeoutCheck").change(function() {
    if (currentObj.container.find("#timeoutCheck").is(":checked")){
      currentObj.container.find("[type='timeout']").show();
    } else {
      currentObj.container.find("[type='timeout']").hide();
    }
    window._paq.push(['trackEvent', CONFIG.GA_CATEGORY, "timeoutCheckClick", currentObj.container.find("#timeoutCheck").is(":checked")]);
  });
  currentObj.container.find("#aladinCheck").change(function() {
    currentObj.updateAladin();
    window._paq.push(['trackEvent', CONFIG.GA_CATEGORY, "aladinCheckClick", currentObj.container.find("#aladinCheck").is(":checked")]);
  });
  currentObj.container.find(".btnFovCorners").click(function(e) {
    e.preventDefault();
    
    var coordsFrame = currentObj.container.find("input[name='coordinates_frame_radio']:checked").val();
    
	let coords = coordsFrame == "J2000" ? [currentObj.aladin.view.ra, currentObj.aladin.view.dec] 
		: CooConversion.J2000ToGalactic([currentObj.aladin.view.ra, currentObj.aladin.view.dec]);
    currentObj.container.find("[type='ra']").val(coords[0]);
    currentObj.container.find("[type='dec']").val(coords[1]);
    currentObj.container.find("[type='fov']").val(currentObj.aladin.view.fov);
    currentObj.container.find("[type='aspectRatio']").val(currentObj.aladin.view.width / currentObj.aladin.view.height);

    currentObj.container.find(".polygon").val(JSON.stringify(currentObj.aladin.getFovCorners(2)).replace(/\]/g, '').replace(/\[/g, ''));
    currentObj.refreshAladinPolygon();

    window._paq.push(['trackEvent', CONFIG.GA_CATEGORY, "getFovFromAladin", currentObj.container.find(".polygon").val()]);
  });
  currentObj.container.find(".btnRefresh").click(function(e) {
    e.preventDefault();
    currentObj.showPreview();

    window._paq.push(['trackEvent', CONFIG.GA_CATEGORY, "RefreshImage", currentObj.container.find(".skyPreviewContainer").find("img").attr("src")]);

  });

  this.init = function() {
    this.fillSkySelector();
    this.updateCoordinatesMode();
    this.updateAladin();
  };

  this.fillSkySelector = function (){
    var skySelector = currentObj.container.find(".skySelector");
    fillSkySelector (skySelector, currentObj.hipsSources);
  }

  this.showPreview = function (){

    var imageUrl = CONFIG.SKYIMAGE_BASE_URL;

    var coordsMode = currentObj.container.find("input[name='coordinates_radio']:checked").val();
    if (coordsMode == "TARGET"){
      var coordsFrame = currentObj.container.find("input[name='coordinates_frame_radio']:checked").val();
      var coords = coordsFrame == "J2000" ? [currentObj.container.find("[type='ra']").val(), currentObj.container.find("[type='dec']").val()] 
		: CooConversion.GalacticToJ2000([currentObj.container.find("[type='ra']").val(), currentObj.container.find("[type='dec']").val()]);
      var ra = coords[0];
      var dec = coords[1];
      var fov = currentObj.container.find("[type='fov']").val();
      var aspectRatio = Math.min(parseFloat(currentObj.container.find("[type='aspectRatio']").val()), CONFIG.ASPECT_RATIO.MAX);

      imageUrl += "?target=" + ra + "%20" + dec + "&fov=" + fov + "&aspectratio=" + aspectRatio;

    } else {
      imageUrl += "?polygon=" + currentObj.container.find(".polygon").val();
    }

    if (currentObj.container.find("#norderCheck").is(":checked")){
      imageUrl += "&norder=" + currentObj.container.find("[type='norder']").val();
    }

    imageUrl += "&hips=" + currentObj.container.find(".skySelector").val();

    var hipsFormat = currentObj.container.find("input[name='hips_format_radio']:checked").val();
    if (hipsFormat != "DEFAULT"){
      imageUrl += "&hipsfmt=" + hipsFormat;
    }

    imageUrl += "&size=" + currentObj.container.find("[type='size']").val();

    var projection = currentObj.container.find("input[name='projection_radio']:checked").val();
    if (projection != "DEFAULT"){
      imageUrl += "&proj=" + projection;
    }

    imageUrl += "&fmt=" + currentObj.container.find("[name='out_format_radio']:checked").val();

    if (!currentObj.container.find("#croppedCheck").is(":checked")){
      imageUrl += "&crop=false";
    }

    if (currentObj.container.find("#timeoutCheck").is(":checked")){
      imageUrl += "&timeout=" + currentObj.container.find("[type='timeout']").val();
    }

    imageUrl += "&_=" + new Date().getTime()

    var img = $("<img style=\"width:100%\" src=\"" + imageUrl + "\">");
    var openLink = $("<a class=\"openTab btn btn-info\" href=\"" + imageUrl + "\" target=\"_blank\" style=\"float:right\">Open image in new tab</a>");

    openLink.click(function(e) {
      window._paq.push(['trackEvent', CONFIG.GA_CATEGORY, "OpenImageInTab", $(this).attr("href")]);
    });

    currentObj.container.find(".skyPreviewContainer").html("");
    currentObj.container.find(".skyPreviewContainer").append(img);
    currentObj.container.find(".buttonContainer").find(".openTab").remove();
    currentObj.container.find(".buttonContainer").append(openLink);

  }

  this.updateCoordinatesMode = function () {
    var coordsMode = currentObj.container.find("input[name='coordinates_radio']:checked").val();
    if (coordsMode == "TARGET"){
      currentObj.container.find(".coordinatesRow").show();
      currentObj.container.find(".coordinateFrame").show();
      currentObj.container.find(".polygonRow").hide();
    } else {
      currentObj.container.find(".coordinatesRow").hide();
      currentObj.container.find(".coordinateFrame").hide();
      currentObj.container.find(".polygonRow").show();
    }
  }

  this.updateAladin = function() {

    currentObj.aladinVisible = currentObj.container.find("#aladinCheck").is(":checked");;

    if (currentObj.aladinVisible){

      if (isNull(currentObj.aladin)){
        currentObj.aladin = A.aladin('#aladin-lite-div', {target: CONFIG.ALADIN_DEFAULT_TARGET, fov: CONFIG.ALADIN_DEFAULT_FOV});
        currentObj.overlay = A.graphicOverlay({color: '#ee2345', lineWidth: 3});
        currentObj.aladin.addOverlay(currentObj.overlay);

        setTimeout(function(){
          currentObj.container.find(".btnFovCorners").click();
          currentObj.showPreview();
        }, 1500);
      } else {
        currentObj.refreshAladinPolygon();
      }

      currentObj.container.find(".aladinContainer").show();
      currentObj.container.find(".btnFovCorners").show();

    } else {

      currentObj.container.find(".aladinContainer").hide();
      currentObj.container.find(".btnFovCorners").hide();
    }

  }

  this.refreshAladinPolygon = function(){
    if (!isNull(currentObj.aladin) && currentObj.aladinVisible){

      currentObj.overlay.removeAll();

      var ployStr = currentObj.container.find(".polygon").val();
      var pointsArr = currentObj.getPointsArrFromStrPoly(ployStr);
      if (!isNull(pointsArr)){
        currentObj.overlay.addFootprints([A.polygon(pointsArr)]);

        //var polyCenter = currentObj.getPointsArrCenter(pointsArr);
        //currentObj.aladin.gotoRaDec(polyCenter.ra, polyCenter.dec);
      }

    }
  }

  this.getPointsArrFromStrPoly = function(polygonStr) {

      var pointsArr = [];
      var strArr = polygonStr.split(",");
      if ((strArr.length >= 2) && (strArr.length % 2 == 0)) {

          for (var idx = 0; idx < strArr.length; idx += 2) {
              var ra = parseFloat(strArr[idx]);
              var dec = parseFloat(strArr[idx + 1]);
              pointsArr.push([ra, dec]);
          }

      }

      if (pointsArr.length > 0) {
        return pointsArr;
      }

      return null;
  }

  this.getPointsArrCenter = function(pointsArr) {
      var ra = 0;
      var dec = 0;

      for (var idx in pointsArr) {
          ra += pointsArr[idx][0];
          dec += pointsArr[idx][1];
      }

      return { ra: ra / pointsArr.length, dec: dec / pointsArr.length };
  }



	CooConversion = (function() {
	
	    var CooConversion = {};
	
	    CooConversion.GALACTIC_TO_J2000 = [
	        -0.0548755604024359, 0.4941094279435681, -0.8676661489811610,
	        -0.8734370902479237, -0.4448296299195045, -0.1980763734646737,
	        -0.4838350155267381, 0.7469822444763707, 0.4559837762325372
	    ];
	
	    CooConversion.J2000_TO_GALACTIC = [
	        -0.0548755604024359, -0.873437090247923, -0.4838350155267381,
	        0.4941094279435681, -0.4448296299195045, 0.7469822444763707,
	        -0.8676661489811610, -0.1980763734646737, 0.4559837762325372
	    ];
	
	    // adapted from www.robertmartinayers.org/tools/coordinates.html
	    // radec : array of ra, dec in degrees
	    // return coo in degrees
	    CooConversion.Transform = function(radec, matrix) { // returns a radec array of two elements
	        radec[0] = radec[0] * Math.PI / 180;
	        radec[1] = radec[1] * Math.PI / 180;
	        var r0 = new Array(
	            Math.cos(radec[0]) * Math.cos(radec[1]),
	            Math.sin(radec[0]) * Math.cos(radec[1]),
	            Math.sin(radec[1]));
	
	        var s0 = new Array(
	            r0[0] * matrix[0] + r0[1] * matrix[1] + r0[2] * matrix[2],
	            r0[0] * matrix[3] + r0[1] * matrix[4] + r0[2] * matrix[5],
	            r0[0] * matrix[6] + r0[1] * matrix[7] + r0[2] * matrix[8]);
	
	        var r = Math.sqrt(s0[0] * s0[0] + s0[1] * s0[1] + s0[2] * s0[2]);
	
	        var result = new Array(0.0, 0.0);
	        result[1] = Math.asin(s0[2] / r); // New dec in range -90.0 -- +90.0 
	        // or use sin^2 + cos^2 = 1.0  
	        var cosaa = ((s0[0] / r) / Math.cos(result[1]));
	        var sinaa = ((s0[1] / r) / Math.cos(result[1]));
	        result[0] = Math.atan2(sinaa, cosaa);
	        if (result[0] < 0.0) result[0] = result[0] + 2 * Math.PI;
	
	        result[0] = result[0] * 180 / Math.PI;
	        result[1] = result[1] * 180 / Math.PI;
	        return result;
	    };
	
	    // coo : array of lon, lat in degrees
	    CooConversion.GalacticToJ2000 = function(coo) {
	        return CooConversion.Transform(coo, CooConversion.GALACTIC_TO_J2000);
	    };
	    // coo : array of lon, lat in degrees
	    CooConversion.J2000ToGalactic = function(coo) {
	        return CooConversion.Transform(coo, CooConversion.J2000_TO_GALACTIC);
	    };
	    return CooConversion;
	})();

  this.init();
}
