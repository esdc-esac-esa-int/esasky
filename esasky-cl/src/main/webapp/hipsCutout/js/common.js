// ------- MATH FUNCTIONS -------

function isNull (value) {
  return (value === undefined) || (value == null);
}

function isInt(n) {
   return n % 1 === 0;
}

function fixedPrecision(value, precision) {
   return (precision > 0) ? parseFloat(value.toFixed(precision)) : Math.floor(value);
}

function fillWithZeros(num, length) {
  num = ""+num;
  while(num.length < length) num = "0"+num;
  return num;
}

function truncateText(string, maxLength){
   if (string.length > maxLength)
      return string.substring(0,maxLength)+'...';
   else
      return string;
}

function toProperCase (text) {
    return text.replace(/\w\S*/g, function(txt){return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();});
}

function closest(arr, closestTo){
    var closest = minMax2DArray(arr).max;
    for(var i = 0; i < arr.length; i++){
        if(arr[i] >= closestTo && arr[i] < closest) closest = arr[i];
    }
    return closest;
}

function minMax2DArray(arr) {
  var max = Number.MIN_VALUE,
      min = Number.MAX_VALUE;
  arr.forEach(function(e) {
    if (e != null && !isNaN(e)){
      if (max < e) { max = e; }
      if (min > e) { min = e; }
    }
  });
  return {max: max, min: min};
}

function getPrecisionFromFloat(value) {
  var strVal = value + "";
  if (strVal.indexOf(".") > -1){
    return strVal.split(".")[1].length;
  } else {
    return Math.pow(10, -strVal.length);
  }
}

// ------- CLIPBOARD AND FILE MEHTODS -------
function uploadFile (filename, folder, data, onSuccessFn, onErrorFn){
  $.ajax({
    url: CONFIG.BASE_URL + CONFIG.UPLOAD_ENDPOINT,
    type: "POST",
    data: { filename: filename, folder: folder, content: data },
    success: function(results) {
      onSuccessFn();
    },
    error: function(error) {
      onErrorFn();
    }
  });
}

function saveToFile (filename, contents) {
  var a = document.createElement("a");
  var file = new Blob([contents], {type: 'text/plain'});
  a.href = URL.createObjectURL(file);
  a.download = filename;
  a.click();
}

function saveRawToFile(filename, text) {
  var encodedUri = encodeURI(text);
  var link = document.createElement("a");
  link.setAttribute("href", encodedUri);
  link.setAttribute("download", filename);
  link.click();
}

function showLoadFile(onLoadFn, fileExtension) {
  var input = $('<input type="file" id="load-input" />');
  input.on('change', function (e) {
    if (e.target.files.length == 1) {
      var file = e.target.files[0];
      if (isNull(fileExtension) || file.name.endsWith(fileExtension)){
        var reader = new FileReader();
        reader.onload = function (e) { onLoadFn (e, file) };
        reader.readAsText(file);
      } else {
        onLoadFn (null, file);
      }
    }
   });
   input.click();
}

function encodeXML (input){

    var charset = {
        name : 'US-ASCII',
        containsChar : function(c) {
            return c.charCodeAt(0) < 128;
        }
    };

    var output = '';
    for (var i = 0; i < input.length; i++) {
        var j = '<>"&\''.indexOf(input.charAt(i));
        if (j != -1) {
            output += '&' + ['lt', 'gt', 'quot', 'amp', '#39'][j] + ';';
        } else if (!charset.containsChar(input.charAt(i))) {
            output += '&#' + input.charCodeAt(i) + ';';
        } else {
            output += input.charAt(i);
        }
    }
    return output;
}


// ------- CONNECTIVITY MEHTODS -------
function UrlExists(url, cb){
    jQuery.ajax({
        url:      url,
        dataType: 'text',
        type:     'GET',
        complete:  function(xhr){
            if(typeof cb === 'function')
               cb.apply(this, [xhr.status]);
        }
    });
}

// ------- HTML MEHTODS -------
function cloneHtmlElement(id, classSelector) {
  return $("." + classSelector).clone().removeClass(classSelector).addClass(id);
}

function getInputIntValue($input, defaultValue) {
  return getInputValue($input, "int", defaultValue);
}

function getInputIntValueCropped ($input, defaultValue, min, max) {
  var value = Math.min(Math.max(getInputIntValue($input, defaultValue), min), max);
  $input.val(value).removeClass("wrongValue");
  return value;
}

function getInputFloatValue($input, defaultValue) {
  return getInputValue($input, "float", defaultValue);
}

function getInputFloatValueCropped ($input, defaultValue, min, max) {
  var value = Math.min(Math.max(getInputFloatValue($input, defaultValue), min), max);
  $input.val(value).removeClass("wrongValue");
  return value;
}

function getInputFloatValueMinMax ($input, defaultValue, min, max) {
  var value = Math.min(Math.max(getInputFloatValue($input, defaultValue), min), max);
  if (parseFloat($input.val()) != value){
    $input.addClass("wrongValue");
    return defaultValue;
  } else {
    return value;
  }
}

function getInputValue($input, type, defaultValue) {
  try {

      var value = NaN;
      var textVal = $input.val().replace(",", ".");
      $input.val(textVal);

      if (type == "float") {
        value = parseFloat(textVal);
      } else if (type == "int") {
        value = parseInt(textVal);
      }

      if (jQuery.isNumeric(textVal) && !isNaN(value)) {
        $input.removeClass("wrongValue");
        return value;
      } else {
        $input.addClass("wrongValue");
        return defaultValue;
      }

  } catch (e) {
    $input.addClass("wrongValue");
    return defaultValue;
  }
}

function getCheckedState(value) {
  return value ? 'checked="checked"' : "";
}

function getBootstrapRow() {
  return $('<div class="row"></div>');
}
