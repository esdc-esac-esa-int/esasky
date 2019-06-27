// Sky Image Manager
function SkyImageManager (containerId, hipsSources) {

  var currentObj = this;

  this.container = $(".container").find("[containerId='" + containerId + "']" );
  this.hipsSources = hipsSources;

  currentObj.container.find("[name='coordinates_radio']").change(function() {
    currentObj.updateCoordinatesMode();
    ga('send', 'event', CONFIG.GA_CATEGORY, "ChangeInputAreaType", currentObj.container.find("input[name='coordinates_radio']:checked").val());
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
    ga('send', 'event', CONFIG.GA_CATEGORY, "aspectRatioChanged", currentObj.container.find("[type='aspectRatio']").val());
  });
  currentObj.container.find("[type='norder']").change(function() {
    getInputIntValueCropped(currentObj.container.find("[type='norder']"), CONFIG.NORDER.DEFAULT, CONFIG.NORDER.MIN, CONFIG.NORDER.MAX);
    ga('send', 'event', CONFIG.GA_CATEGORY, "norderChanged", currentObj.container.find("[type='norder']").val());
  });
  currentObj.container.find("[type='size']").change(function() {
    getInputIntValueCropped(currentObj.container.find("[type='size']"), CONFIG.SIZE.DEFAULT, CONFIG.SIZE.MIN, CONFIG.SIZE.MAX);
    ga('send', 'event', CONFIG.GA_CATEGORY, "sizeChanged", currentObj.container.find("[type='size']").val());
  });
  currentObj.container.find("[type='timeout']").change(function() {
    getInputIntValueCropped(currentObj.container.find("[type='timeout']"), CONFIG.TIMEOUT.DEFAULT, CONFIG.TIMEOUT.MIN, CONFIG.TIMEOUT.MAX);
    ga('send', 'event', CONFIG.GA_CATEGORY, "timeoutChanged", currentObj.container.find("[type='timeout']").val());
  });
  currentObj.container.find("#norderCheck").change(function() {
    if (currentObj.container.find("#norderCheck").is(":checked")){
      currentObj.container.find("[type='norder']").show();
    } else {
      currentObj.container.find("[type='norder']").hide();
    }
    ga('send', 'event', CONFIG.GA_CATEGORY, "nOrderCheckClick", currentObj.container.find("#norderCheck").is(":checked"));
  });
  currentObj.container.find("#timeoutCheck").change(function() {
    if (currentObj.container.find("#timeoutCheck").is(":checked")){
      currentObj.container.find("[type='timeout']").show();
    } else {
      currentObj.container.find("[type='timeout']").hide();
    }
    ga('send', 'event', CONFIG.GA_CATEGORY, "timeoutCheckClick", currentObj.container.find("#timeoutCheck").is(":checked"));
  });
  currentObj.container.find("#aladinCheck").change(function() {
    currentObj.updateAladin();
    ga('send', 'event', CONFIG.GA_CATEGORY, "aladinCheckClick", currentObj.container.find("#aladinCheck").is(":checked"));
  });
  currentObj.container.find(".btnFovCorners").click(function(e) {
    e.preventDefault();

    currentObj.container.find("[type='ra']").val(currentObj.aladin.view.ra);
    currentObj.container.find("[type='dec']").val(currentObj.aladin.view.dec);
    currentObj.container.find("[type='fov']").val(currentObj.aladin.view.fov);
    currentObj.container.find("[type='aspectRatio']").val(currentObj.aladin.view.width / currentObj.aladin.view.height);

    currentObj.container.find(".polygon").val(JSON.stringify(currentObj.aladin.getFovCorners(2)).replace(/\]/g, '').replace(/\[/g, ''));
    currentObj.refreshAladinPolygon();

    ga('send', 'event', CONFIG.GA_CATEGORY, "getFovFromAladin", currentObj.container.find(".polygon").val());
  });
  currentObj.container.find(".btnRefresh").click(function(e) {
    e.preventDefault();
    currentObj.showPreview();

    ga('send', 'event', CONFIG.GA_CATEGORY, "RefreshImage", currentObj.container.find(".skyPreviewContainer").find("img").attr("src"));

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
      var ra = currentObj.container.find("[type='ra']").val();
      var dec = currentObj.container.find("[type='dec']").val();
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
      ga('send', 'event', CONFIG.GA_CATEGORY, "OpenImageInTab", $(this).attr("href"));
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
      currentObj.container.find(".polygonRow").hide();
    } else {
      currentObj.container.find(".coordinatesRow").hide();
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

  this.init();
}
