


var EventUtils = (function (){
    var cssScale = undefined;

    var relMouseCoords = function (event) {
        if (event.offsetX) {
            return {x: event.offsetX, y: event.offsetY}
        } else {
            if (!this.cssScale) {
                var st = window.getComputedStyle(document.body, null)
                var tr = st.getPropertyValue('-webkit-transform') ||
                    st.getPropertyValue('-moz-transform') ||
                    st.getPropertyValue('-ms-transform') ||
                    st.getPropertyValue('-o-transform') ||
                    st.getPropertyValue('transform')
                var matrixRegex = /matrix\((-?\d*\.?\d+),\s*0,\s*0,\s*(-?\d*\.?\d+),\s*0,\s*0\)/
                var matches = tr.match(matrixRegex)
                if (matches) {
                    this.cssScale = parseFloat(matches[1])
                } else {
                    this.cssScale = 1
                }
            }
            var e = event
            // http://www.jacklmoore.com/notes/mouse-position/
            var target = e.target || e.srcElement
            var style = target.currentStyle || window.getComputedStyle(target, null)
            var borderLeftWidth = parseInt(style['borderLeftWidth'], 10)
            var borderTopWidth = parseInt(style['borderTopWidth'], 10)
            var rect = target.getBoundingClientRect()

            var clientX = e.clientX
            var clientY = e.clientY
            if (e && e.changedTouches) {
                clientX = e.changedTouches[0].clientX
                clientY = e.changedTouches[0].clientY
            }

            var offsetX = clientX - borderLeftWidth - rect.left
            var offsetY = clientY - borderTopWidth - rect.top


            return {x: Math.round(offsetX / this.cssScale), y: Math.round(offsetY / this.cssScale)}
        }
    }

    return {relMouseCoords : relMouseCoords};

})

var LineStyle = (function() {

    function style2lineDash(style){
        if(style == 'dashed'){
            return [10,10];
        }

        if(style == 'dot'){
            return [2,10];
        }

        return [];
    }

    return {style2lineDash : style2lineDash};

});


var dispatchFootprintClickEvent = function (footprint) {
    if (footprint.overlay) {
        window.dispatchEvent(new CustomEvent("footprintClicked", {
            detail: {
                shape: footprint
            }
        }));
    }
};

var uuidv4 = function () {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
        var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8)
        return v.toString(16)
    })
}


var CooFrameEnum = (function() {

    var systems = {J2000: 'J2000', GAL: 'Galactic'};
    return {
        SYSTEMS: systems,

        J2000: {label: "J2000", system: systems.J2000},
        J2000d: {label: "J2000d", system: systems.J2000},
        GAL:  {label: "Galactic", system: systems.GAL},

        fromString: function(str, defaultValue) {
            if (! str) {
                return defaultValue ? defaultValue : null;
            }

            str = str.toLowerCase().replace(/^\s+|\s+$/g, ''); // convert to lowercase and trim

            if (str.indexOf('j2000d')==0 || str.indexOf('icrsd')==0) {
                return CooFrameEnum.J2000d;
            }
            else if (str.indexOf('j2000')==0 || str.indexOf('icrs')==0) {
                return CooFrameEnum.J2000;
            }
            else if (str.indexOf('gal')==0) {
                return CooFrameEnum.GAL;
            }
            else {
                return defaultValue ? defaultValue : null;
            }
        }
    };

})();


var CooConversion = (function() {

    let CooConversion = {};

    CooConversion.GALACTIC_TO_J2000 = [
        -0.0548755604024359,  0.4941094279435681, -0.8676661489811610,
        -0.8734370902479237, -0.4448296299195045, -0.1980763734646737,
        -0.4838350155267381,  0.7469822444763707,  0.4559837762325372 ];

    CooConversion.J2000_TO_GALACTIC = [
        -0.0548755604024359, -0.873437090247923, -0.4838350155267381,
        0.4941094279435681, -0.4448296299195045, 0.7469822444763707,
        -0.8676661489811610, -0.1980763734646737, 0.4559837762325372 ];

    // adapted from www.robertmartinayers.org/tools/coordinates.html
    // radec : array of ra, dec in degrees
    // return coo in degrees
    CooConversion.Transform = function( radec, matrix ) {// returns a radec array of two elements
        radec[0] = radec[0]*Math.PI/180;
        radec[1] = radec[1]*Math.PI/180;
        var r0 = new Array (
            Math.cos(radec[0]) * Math.cos(radec[1]),
            Math.sin(radec[0]) * Math.cos(radec[1]),
            Math.sin(radec[1]) );

        var s0 = new Array (
            r0[0]*matrix[0] + r0[1]*matrix[1] + r0[2]*matrix[2],
            r0[0]*matrix[3] + r0[1]*matrix[4] + r0[2]*matrix[5],
            r0[0]*matrix[6] + r0[1]*matrix[7] + r0[2]*matrix[8] );

        var r = Math.sqrt ( s0[0]*s0[0] + s0[1]*s0[1] + s0[2]*s0[2] );

        var result = new Array ( 0.0, 0.0 );
        result[1] = Math.asin ( s0[2]/r ); // New dec in range -90.0 -- +90.0
        // or use sin^2 + cos^2 = 1.0
        var cosaa = ( (s0[0]/r) / Math.cos(result[1] ) );
        var sinaa = ( (s0[1]/r) / Math.cos(result[1] ) );
        result[0] = Math.atan2 (sinaa,cosaa);
        if ( result[0] < 0.0 ) result[0] = result[0] + 2*Math.PI;

        result[0] = result[0]*180/Math.PI;
        result[1] = result[1]*180/Math.PI;
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


var getDroppedFilesHandler = function (ev) {
    // Prevent default behavior (Prevent file from being opened)
    ev.preventDefault()

    let items
    if (ev.dataTransfer.items) {
        // Use DataTransferItemList interface to access the file(s)
        items = [...ev.dataTransfer.items]
    } else {
        // Use DataTransfer interface to access the file(s)
        items = [...ev.dataTransfer.files]
    }

    const files = items.filter((item) => {
        // If dropped items aren't files, reject them
        return item.kind === 'file'
    })
        .map((item) => item.getAsFile())

    return files
}