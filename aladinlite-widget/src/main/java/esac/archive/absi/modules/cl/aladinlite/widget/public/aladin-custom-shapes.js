// Define custom draw function for sources with proper motion, just draws an arrow from the source.
// Usage: A.source(186.549225, 12.945969, {name: 'M 86', size: 6.03, otype: 'Galaxy in group of galaxies',
//										  arrowRa: '186.449225', arrowDec: '12.845969', arrowColor: '#0000ff', arrowWidth: '2.0' });
// arrowColor and arrowWidth are optionals.
// See: http://aladin.u-strasbg.fr/AladinLite/doc/API/examples/cat-custom-draw-function/
var drawSourceWithProperMotion = function (source, canvasCtx, viewParams) {

    // Skipping computeFootprints, no footprints here
    if (!canvasCtx) return;

    // Draw the main shape
    this._shapeIsFunction = false;
    this.drawSource(source, canvasCtx, this.view.width, this.view.height);
    this._shapeIsFunction = true;


    try {
        if (source.data && source.data["arrowRa"] && source.data["arrowDec"]) {

            if (typeof source.data["arrowRa"] === 'string' || source.data["arrowRa"] instanceof String) {
                //Avoid parsing to float each draw, data comes as string from GWT
                source.data["arrowRa"] = parseFloat(source.data["arrowRa"]);
                source.data["arrowDec"] = parseFloat(source.data["arrowDec"]);
                source.data["arrowFlipped"] = (source.data["arrowFlipped"] === "true");
            }

            var arrowRa = source.data["arrowRa"];
            var arrowDec = source.data["arrowDec"];
            var arrowFlipped = source.data["arrowFlipped"];

            if (this.removeAvgPM) {
                if (!source.data["avgPMra"] || !source.data["avgPMdec"]) {

                    //Calculates the coordinates increments and removes the averages
                    var ra = parseFloat(source.ra);
                    var dec = parseFloat(source.dec);
                    var raInc = (arrowRa - ra) - this.avgPMRaInc;
                    var decInc = (arrowDec - dec) - this.avgPMDecInc;

                    //Normalizes the increments to 10 degrees to avoid zero incX and incY after calling AladinUtils.xyToView
                    var m = Math.sqrt((raInc * raInc) + (decInc * decInc));
                    var mRatio = 2 / m; // 2 degrees
                    source.data["avgPMra"] = ra + (raInc * mRatio);
                    source.data["avgPMdec"] = dec + (decInc * mRatio);

                    //Calculates the scale factor, this function is also in CatalogEntity.java (modify there also)
                    // See: https://rechneronline.de/function-graphs/  , using function: 4 - (3*(log((x* 1000)+2.72)^(-2)))
                    source.data["avgPMratio"] = 4.0 - (3.0 * Math.pow(Math.log((m * 1000) + 2.72), -2.0));
                }
                arrowRa = source.data["avgPMra"];
                arrowDec = source.data["avgPMdec"];
            }

            var xy = source.catalog.view.aladin.world2pix([arrowRa], [arrowDec]);

            if (xy) {


                //Draws the arrow

                var incX = (xy[0] - source.x) * ((arrowFlipped) ? -1.0 : 1.0);
                var incY = (xy[1] - source.y) * ((arrowFlipped) ? -1.0 : 1.0);
                var dist = Math.sqrt((incX * incX) + (incY * incY));

                if (this.removeAvgPM) {

                    //Draws the average proper motion with a dotted line
                    // The ratio bettween the dist and this.arrowLength
                    var ratio = (this.arrowLength * this.arrowScale * source.data["avgPMratio"]) / dist;
                    canvasCtx.beginPath();
                    canvasCtx.strokeStyle = this.arrowColor;
                    canvasCtx.lineWidth = this.arrowWidth;
                    canvasCtx.setLineDash([2, 2]);
                    canvasCtx.moveTo(source.x, source.y);
                    canvasCtx.lineTo(source.x + (incX * ratio), source.y + (incY * ratio));
                    canvasCtx.stroke();
                    canvasCtx.setLineDash([]);

                } else {

                    if (dist < this.arrowLength) {

                        //Uses the normalized ra dec for calculating the vector
                        if (typeof source.data["arrowRaNorm"] === 'string' || source.data["arrowRaNorm"] instanceof String) {
                            //Avoid parsing to float each draw, data comes as string from GWT
                            source.data["arrowRaNorm"] = parseFloat(source.data["arrowRaNorm"]);
                            source.data["arrowDecNorm"] = parseFloat(source.data["arrowDecNorm"]);
                            source.data["arrowRatio"] = parseFloat(source.data["arrowRatio"]);
                        }

                        var arrowRaNorm = source.data["arrowRaNorm"];
                        var arrowDecNorm = source.data["arrowDecNorm"];
                        var arrowRatio = source.data["arrowRatio"];

                        var xy = source.catalog.view.aladin.world2pix(arrowRaNorm, arrowDecNorm);

                        if (xy) {
                            incX = (xy[0] - source.x) * ((arrowFlipped) ? -1.0 : 1.0);
                            incY = (xy[1] - source.y) * ((arrowFlipped) ? -1.0 : 1.0);
                            dist = Math.sqrt((incX * incX) + (incY * incY));

                            //Draws the dotted line
                            // The ratio bettween the dist and this.arrowLength
                            var ratio = (this.arrowLength * this.arrowScale * arrowRatio) / dist;
                            canvasCtx.beginPath();
                            canvasCtx.strokeStyle = this.arrowColor;
                            canvasCtx.lineWidth = this.arrowWidth;
                            canvasCtx.setLineDash([2, 2]);
                            canvasCtx.moveTo(source.x, source.y);
                            canvasCtx.lineTo(source.x + (incX * ratio), source.y + (incY * ratio));
                            canvasCtx.stroke();
                            canvasCtx.setLineDash([]);

                        }

                    } else {

                        // Draws the real arrow
                        var angle = Math.atan2(incY, incX);

                        canvasCtx.save();
                        if (!arrowFlipped) {
                            canvasCtx.translate(source.x, source.y);
                        } else {
                            canvasCtx.translate(xy[0], xy[1]);
                        }
                        canvasCtx.rotate(angle);

                        // line
                        canvasCtx.beginPath();
                        canvasCtx.strokeStyle = this.arrowColor;
                        canvasCtx.lineWidth = this.arrowWidth;
                        var size = canvasCtx.lineWidth * 2.5;
                        canvasCtx.moveTo(0, 0);
                        canvasCtx.lineTo(dist - size, 0);
                        canvasCtx.stroke();

                        // triangle
                        canvasCtx.fillStyle = canvasCtx.strokeStyle;
                        canvasCtx.beginPath();
                        canvasCtx.lineTo(dist - size, size);
                        canvasCtx.lineTo(dist, 0);
                        canvasCtx.lineTo(dist - size, -size);
                        canvasCtx.fill();

                        // circle
                        if (arrowFlipped) {
                            canvasCtx.beginPath();
                            canvasCtx.arc(0, 0, this.arrowWidth, 0, 2 * Math.PI, false);
                            canvasCtx.fill();
                        }

                        canvasCtx.restore();
                    }
                }

            }
        }
    } catch (e) {
    }
};

const Q3CMOC = (function () {
    const Q3CMOC = function (options) {

        options = options || {};
        this.name = options.name || "Q3CMOC";
        this.color = options.color || '#00cc99';
        this.lineStyle = options.lineStyle || "solid"
        this.opacity = options.opacity || 1;
        this.opacity = Math.max(0, Math.min(1, this.opacity)); // 0 <= this.opacity <= 1
        this.lineWidth = options["lineWidth"] || 1;

        this.mode = options.mode || "q3c"
        this.isShowing = true
        this.ready = false
        this.minShowOrder = 3;
        this.maxShowOrder = 8;
        this.coverageLimit = 0.1;
        this.highlighted = []
        this.selected = []
        this.geometryObject = new geometryObject(this.mode);
        this._mocs = new Q3CIpix(1, -1)
        this._polygons = {}
        this._shownPolygons = new Set();
        this._overlay = A.graphicOverlay({name: this.name + "_overlay", color: this.color});
    }

    class geometryObject {
        constructor(mode) {
            this.mode = mode;
        }

        raDec2ipix(ra, dec, order) {

            if (this.mode == "q3c") {
                return Q3C.ang2ipix(ra, dec, order)

            } else if (this.mode == "healpix") {
                var hpxIdx = HealpixCache.getIndexByNside(1 << order);
                var polar = Utils.radecToPolar(ra, dec);
                return hpxIdx.ang2pix_nest(polar.theta, polar.phi);
            }

            //No mode == failure
            return -1;
        }

        ipix2radec(order, ipix) {
            if (this.mode == "q3c") {
                var raDec = Q3C.ipix2ang(order, ipix);
                return {ra: raDec[0], dec: raDec[1]};

            } else if (this.mode == "healpix") {
                var hpxIdx = HealpixCache.getIndexByNside(1 << order);
                var polar = hpxIdx.pix2ang_nest(ipix);
                return Utils.polarToRadec(polar.theta, polar.phi);
            }
            return null;
        }

        approxSize(order) {
            return 90.0 / (1 << order);
        }

        ipix2angCorners(order, ipix) {
            if (this.mode == "q3c") {
                return Q3C.ipix2angCorners(order, ipix);

            } else if (this.mode == "healpix") {
                var cornersXY = HealpixCache.corners_nest(ipix, 1 << order);
                var corners = []

                for (var k = 0; k < cornersXY.length; k++) {
                    corners.push([cornersXY[k].ra(), cornersXY[k].dec()])
                }
                return corners;
            }
            return null;
        }
    }

    const Q3C = (function () {
        var nbits = 16;
        var nside = 1 << nbits - 2;
        init();

        function init() {
            xybits_size = 1 << nbits;

            xbits = []
            ybits = []

            xbits[0] = 0;
            xbits[1] = 1;
            ybits[0] = 0;
            ybits[1] = 2;
            for (i = 2, m = 1; i < xybits_size; i++) {
                k = i / m;
                if (k == 2) {
                    xbits[i] = xbits[i / 2] * 4;
                    ybits[i] = 2 * xbits[i];
                    m *= 2;
                    continue;
                } else {
                    xbits[i] = xbits[m] + xbits[i % m];
                    ybits[i] = 2 * xbits[i];
                    continue;
                }
            }

            this.xbits = xbits
            this.ybits = ybits

            xbits1 = []
            ybits1 = []

            xbits1[0] = 0;
            xbits1[1] = 1;

            for (i = 2, m = 2, l = 2; i < xybits_size; i++) {

                k = i / m;

                if (k < 2) {
                    xbits1[i] = xbits1[i - m];
                } else {
                    if (k == 4) {
                        xbits1[i] = xbits1[0];
                        m *= 4;
                        l *= 2;
                    } else
                        xbits1[i] = xbits1[i - 2 * m] + l;
                }
            }
            this.xbits1 = xbits1;
            ybits1[0] = 0;
            ybits1[1] = 0;

            for (i = 2, m = 1, l = 1; i < xybits_size; i++) {
                k = i / m;
                if (k < 2) {
                    ybits1[i] = ybits1[i - m];
                } else {
                    if (k == 4) {
                        ybits1[i] = ybits1[0];
                        m *= 4;
                        l *= 2;
                    } else
                        ybits1[i] = ybits1[i - 2 * m] + l;
                }
            }

            this.ybits1 = ybits1;

            instance = this;
            return instance;

        }


        function ipix2ang(order, ipix) {

            Q3C_RADEG = 180 / Math.PI;
            ii1 = 1 << nbits / 2;
            q3c_i = 1 << nbits
            var depth = (nbits * 2) - 4 - 2 * order

            ipix = ipix << depth
            var face_num = ipix >> (nbits * 2) - 4;
            ipix1 = ipix % (nside * nside);

            i3 = ipix1 % q3c_i;
            i2 = ipix1 >> nbits;
            x0 = xbits1[i3];
            y0 = ybits1[i3];
            i3 = i2 % q3c_i;
            x0 += xbits1[i3] * ii1;
            y0 += ybits1[i3] * ii1;

            ix1 = x0;
            iy1 = y0;

            x1 = (ix1 / nside) * 2 - 1;
            y1 = (iy1 / nside) * 2 - 1;
            var ra, dec;

            if ((face_num >= 1) && (face_num <= 4)) {

                ra = Math.atan(x1);
                dec = Q3C_RADEG * Math.atan(y1 * Math.cos(ra));
                ra = ra * Q3C_RADEG + (face_num - 1) * 90;
                if (ra < 0) {
                    ra += 360;
                }
            } else {
                if (face_num == 0) {
                    ra = Q3C_RADEG * (Math.atan2(-x1, y1) + Math.PI);
                    dec = Q3C_RADEG * Math.atan(1 / Math.sqrt(x1 * x1 + y1 * y1));
                }
                if (face_num == 5) {

                    ra = Q3C_RADEG * (Math.atan2(-x1, -y1) + Math.PI);
                    dec = -Q3C_RADEG * Math.atan(1 / Math.sqrt(x1 * x1 + y1 * y1));
                }
            }

            return [ra, dec]
        }

        function ipix2angCorners(order, ipix) {

            Q3C_RADEG = 180 / Math.PI;
            ii1 = 1 << nbits / 2;
            var depth = (nbits * 2) - 4 - 2 * order
            q3c_i = 1 << nbits

            ipix = ipix << depth
            var face_num = ipix >> (nbits * 2) - 4;
            ipix1 = ipix % (nside * nside);

            i3 = ipix1 % q3c_i;
            i2 = ipix1 >> nbits;
            x0 = xbits1[i3];
            y0 = ybits1[i3];
            i3 = i2 % q3c_i;
            x0 += xbits1[i3] * ii1;
            y0 += ybits1[i3] * ii1;

            ix1 = x0;
            iy1 = y0;
            idx = 1 << depth / 2;
            ix2 = ix1 + idx;
            iy2 = iy1 + idx;

            x1 = (ix1 / nside) * 2 - 1;
            y1 = (iy1 / nside) * 2 - 1;
            x2 = (ix2 / nside) * 2 - 1;
            y2 = (iy2 / nside) * 2 - 1;

            x = [x1, x2];
            y = [y1, y2];
            corners = [];
            xIndexes = [0, 0, 1, 1];
            yIndexes = [0, 1, 1, 0];
            if ((face_num >= 1) && (face_num <= 4)) {

                for (var i = 0; i < xIndexes.length; i++) {
                    ra = Math.atan(x[xIndexes[i]]);
                    dec = Q3C_RADEG * Math.atan(y[yIndexes[i]] * Math.cos(ra));
                    ra = ra * Q3C_RADEG + (face_num - 1) * 90;
                    if (ra < 0) {
                        ra += 360;
                    }
                    corners.push([ra, dec])
                }
            } else {
                if (face_num == 0) {
                    for (var i = 0; i < xIndexes.length; i++) {

                        ra = Q3C_RADEG * (Math.atan2(-x[xIndexes[i]], y[yIndexes[i]]) + Math.PI);
                        dec = Q3C_RADEG * Math.atan(1 / Math.sqrt(x[xIndexes[i]] * x[xIndexes[i]] + y[yIndexes[i]] * y[yIndexes[i]]));
                        corners.push([ra, dec])
                    }
                }
                if (face_num == 5) {
                    for (var i = 0; i < xIndexes.length; i++) {

                        ra = Q3C_RADEG * (Math.atan2(-x[xIndexes[i]], -y[yIndexes[i]]) + Math.PI);
                        dec = -Q3C_RADEG * Math.atan(1 / Math.sqrt(x[xIndexes[i]] * x[xIndexes[i]] + y[yIndexes[i]] * y[yIndexes[i]]));
                        corners.push([ra, dec])
                    }
                }
            }

            return corners
        }

        function ipix2angCenter(order, ipix) {

            Q3C_RADEG = 180 / Math.PI;
            ii1 = 1 << nbits / 2;
            var depth = (nbits * 2) - 4 - 2 * order
            q3c_i = 1 << nbits

            ipix = ipix << depth
            var face_num = ipix >> (nbits * 2) - 4;
            ipix1 = ipix % (nside * nside);

            i3 = ipix1 % q3c_i;
            i2 = ipix1 >> nbits;
            x0 = xbits1[i3];
            y0 = ybits1[i3];
            i3 = i2 % q3c_i;
            x0 += xbits1[i3] * ii1;
            y0 += ybits1[i3] * ii1;

            ix1 = x0;
            iy1 = y0;
            idx = 1 << depth / 2;
            ix2 = ix1 + idx;
            iy2 = iy1 + idx;

            x1 = (ix1 / nside) * 2 - 1;
            y1 = (iy1 / nside) * 2 - 1;
            x2 = (ix2 / nside) * 2 - 1;
            y2 = (iy2 / nside) * 2 - 1;

            x = (x1 + x2) / 2;
            y = (y1 + y2) / 2;
            fov = [];
            if ((face_num >= 1) && (face_num <= 4)) {

                ra1 = Math.atan(x1);
                ra = Math.atan(x);
                fov[0] = 2 * Math.abs(ra - ra1);
                dec1 = Q3C_RADEG * Math.atan(y1 * Math.cos(ra1));
                dec = Q3C_RADEG * Math.atan(y * Math.cos(ra));
                fov[1] = 2 * Math.abs(dec1 - dec);
                ra = ra * Q3C_RADEG + (face_num - 1) * 90;
                if (ra < 0) {
                    ra += 360;
                }
            } else {
                if (face_num == 0) {
                    ra1 = Q3C_RADEG * (Math.atan2(-x1, y1) + Math.PI);
                    ra = Q3C_RADEG * (Math.atan2(-x, y) + Math.PI);
                    fov[0] = 2 * Math.abs(ra1 - ra);
                    dec1 = Q3C_RADEG * Math.atan(1 / Math.sqrt(x1 * x1 + y1 * y1));
                    dec = Q3C_RADEG * Math.atan(1 / Math.sqrt(x * x + y * y));
                    fov[1] = 2 * Math.abs(dec1 - dec);
                }
                if (face_num == 5) {
                    ra1 = Q3C_RADEG * (Math.atan2(-x1, -y1) + Math.PI);
                    ra = Q3C_RADEG * (Math.atan2(-x, -y) + Math.PI);
                    fov[0] = 2 * Math.abs(ra1 - ra);
                    dec1 = -Q3C_RADEG * Math.atan(1 / Math.sqrt(x1 * x1 + y1 * y1));
                    dec = -Q3C_RADEG * Math.atan(1 / Math.sqrt(x * x + y * y));
                    fov[1] = 2 * Math.abs(dec1 - dec);
                }
            }

            return {"center": [ra, dec], "fov": Math.max(fov[0], fov[1])}

        }

        function approxSize(order) {
            depth = nbits - 2 - order
            return 90.0 / (nside >> depth)
        }

        function ang2ipix(ra, dec, order) {

            Q3C_DEGRA = Math.PI / 180;

            ra = (ra + 360) % 360
            dec = Math.min(Math.max(-90, dec), 90)

            face_num = ((ra + 45) / 90) % 4 | 0;
            /* for equatorial pixels we'll have face_num from 1 to 4 */
            ra1 = Q3C_DEGRA * (ra - 90 * face_num);
            dec1 = Q3C_DEGRA * dec;
            x0 = Math.tan(ra1);
            y0 = Math.tan(dec1) / Math.cos(ra1);
            face_num++;

            if (y0 > 1) {
                face_num = 0;
                ra1 = Q3C_DEGRA * ra;
                tmp0 = 1 / Math.tan(dec1);
                x0 = Math.sin(ra1);
                y0 = Math.cos(ra1)

                x0 *= tmp0;
                y0 *= (-tmp0);
            } else if (y0 < -1) {
                face_num = 5;
                ra1 = Q3C_DEGRA * ra;
                tmp0 = 1 / Math.tan(dec1);
                x0 = Math.sin(ra1);
                y0 = Math.cos(ra1)

                x0 *= (-tmp0);
                y0 *= (-tmp0);
            }

            x0 = (x0 + 1) / 2;
            y0 = (y0 + 1) / 2;

            /* Now I produce the final pixel value by converting x and y values
             * to bitfields and combining them by interleaving, using the
             * predefined arrays xbits and ybits
             */

            xi = x0 * nside | 0;
            yi = y0 * nside | 0;

            /* This two following statements are written to handle the
             * case of upper right corner of base square */
            if (xi == nside) {
                xi--;
            }
            if (yi == nside) {
                yi--;
            }

            ipix = face_num * nside * nside + xbits[xi] + ybits[yi]

            return ipix >> (nbits * 2) - 4 - 2 * order
        }

        return {
            ang2ipix: ang2ipix,
            approxSize: approxSize,
            ipix2angCenter: ipix2angCenter,
            ipix2angCorners: ipix2angCorners,
            ipix2ang: ipix2ang
        };
    }());

    Q3CMOC.prototype._addPix = function (order, ipix, count = 1) {

        this._mocs.addPixel(order, ipix, parseInt(count))
    }

    Q3CMOC.prototype.setShowOrders = function (minOrder, maxOrder) {
        this.minShowOrder = minOrder;
        this.maxShowOrder = maxOrder;
    }

    Q3CMOC.prototype.setCoverageLimit = function (minOrder, maxOrder) {
        this.minShowOrder = minOrder;
        this.maxShowOrder = maxOrder;
    }

    Q3CMOC.prototype.setLineStyle = function (lineStyle) {
        this._overlay.setLineDash(LineStyle().style2lineDash(lineStyle));
    }

    Q3CMOC.prototype.setOpacity = function (opacity) {
        this.opacity = opacity;
    };

    Q3CMOC.prototype.setColor = function (color) {
        this.color = color;
        this._overlay.setColor(color);
    };

    Q3CMOC.prototype.setView = function (view) {
        this.view = view;
        this.view.mocs.push(this);
        this.view.allOverlayLayers.push(this);
    };

    Q3CMOC.prototype.selectArea = function (selectionArea, selectionCircle) {
        var visiblePixels = this.getVisiblePixels()


    }

    Q3CMOC.prototype.getOverlay = function () {
        return this._overlay;
    }

    Q3CMOC.prototype.dataFromJSON = function (jsonMOC) {
        var order, ipix;
        for (var orderStr in jsonMOC) {
            if (jsonMOC.hasOwnProperty(orderStr)) {
                order = parseInt(orderStr);
                for (var k = 0; k < jsonMOC[orderStr].length; k++) {
                    ipix = jsonMOC[orderStr][k];
                    this._addPix(order, ipix);
                }
            }
        }
        this._generatePolygons()
        this.ready = true;
        this.reportChange();
    }

    Q3CMOC.prototype.dataFromESAJSON = function (jsonMOC) {
        var order, ipix, count;
        var data = jsonMOC["data"]
        for (var i = 0; i < data.length; i++) {
            order = data[i][0];
            ipix = data[i][1];
            count = data[i][2] || 1;
            this._addPix(order, ipix, count);
        }

        this._mocs.updateCount();
        this._mocs.updateCoverage();

        this._generatePolygons()
        this.ready = true;
        this.reportChange();
    }


    Q3CMOC.prototype.draw = function (ctx) {
        if (!this.isShowing || !this.ready) {
            return;
        }
        ctx.save();
        this._drawCells(ctx);
        ctx.restore();
    };

    Q3CMOC.prototype._generatePolygons = function () {
        var options = {
            fill: true,
            opacity: this.opacity,
            lineWidth: this.lineWidth,
        }


        this._polygons = {}
        for (key in this._mocs.children) {
            ipix = this._mocs.children[key]
            ipix.createPolygons(this.geometryObject, options, this._polygons)
        }

        this._overlay.removeAll();
        Object.values(this._polygons).forEach(polygon => {
            this._overlay.add(polygon, false);
            polygon.isShowing = false;
        });

        if (!this.view.layerNameExists(this._overlay.name)) {
            this.view.add(this._overlay);
        }
    }

    Q3CMOC.prototype._drawCells = function (ctx) {
        this._shownPolygons.forEach(polygon => {
            polygon.isShowing = false;
        });

        this._shownPolygons.clear();

        for (key in this._mocs.children) {
            ipix = this._mocs.children[key]
            ipix.draw(this.minShowOrder, this.maxShowOrder, this.coverageLimit, this.geometryObject, ctx, this.view, this._polygons, this._shownPolygons)
        }
        this._overlay.reportChange();
    };

    var increaseBrightness = function (hex, percent) {
        // strip the leading # if it's there
        hex = hex.replace(/^\s*#|\s*$/g, '');

        // convert 3 char codes --> 6, e.g. `E0F` --> `EE00FF`
        if (hex.length == 3) {
            hex = hex.replace(/(.)/g, '$1$1');
        }

        var r = parseInt(hex.substr(0, 2), 16),
            g = parseInt(hex.substr(2, 2), 16),
            b = parseInt(hex.substr(4, 2), 16);

        return '#' +
            ((0 | (1 << 8) + r + (256 - r) * percent / 100).toString(16)).substr(1) +
            ((0 | (1 << 8) + g + (256 - g) * percent / 100).toString(16)).substr(1) +
            ((0 | (1 << 8) + b + (256 - b) * percent / 100).toString(16)).substr(1);
    };


    Q3CMOC.prototype.getVisiblePixelsTargetOrder = function (targetOrder, hasToBeLeaf) {
        var pixels = {};

        for (key in this._mocs.children) {
            ipix = this._mocs.children[key]

            ipix.getVisiblePixels(pixels, targetOrder, this.coverageLimit, hasToBeLeaf, this.geometryObject, this.view)

        }
        return pixels;
    }


    Q3CMOC.prototype.getVisiblePixels = function () {
        var pixels = {};

        for (key in this._mocs.children) {
            ipix = this._mocs.children[key]

            ipix.returnVisible(pixels, this.minShowOrder, this.maxShowOrder, this.coverageLimit, this.geometryObject, this.view)

        }

        return pixels;
    }

    Q3CMOC.prototype.getVisibleCount = function () {
        var count = 0;

        for (key in this._mocs.children) {
            ipix = this._mocs.children[key]

            count += ipix.getVisibleCount(this.minShowOrder, this.maxShowOrder, this.coverageLimit, this.geometryObject, this.view)

        }

        return count;
    }

    Q3CMOC.prototype.selectPix = function (breakOrder, ra, dec) {
        var ipix = this.geometryObject.raDec2ipix(ra, dec, this.maxShowOrder)
        //return this._mocs.checkPixExists(this.maxShowOrder, ipix, this.minShowOrder, this.maxShowOrder, this.coverageLimit);
        var pix = this._mocs.getShownIpixAtPosition(this.maxShowOrder, ipix, this.minShowOrder, this.maxShowOrder, this.coverageLimit)
        if (pix) {
            if (!this.selected.includes(pix)) {
                this.selected = [];
                this.selected.push(pix);
                this.reportChange();
            }
            return [pix.order, pix.ipix, pix.count];
        } else {
            if (this.selected.length > 0) {
                this.selected = [];
                this.reportChange();
            }
            return [-1, -1, -1];
        }
    };

    Q3CMOC.prototype.clearAll = function () {
        this._mocs = new Q3CIpix(1, -1)
        this._overlay.removeAll();
    }

    Q3CMOC.prototype.reportChange = function () {
        this._overlay.reportChange();
    };

    Q3CMOC.prototype.checkAreaIntersection = function (selectionArea, shouldSelect) {
        var list = []
        var count = 0;
        if (shouldSelect) {

            for (key in this._mocs.children) {
                ipix = this._mocs.children[key]

                list = ipix.getIntersectionSelectionArea(list, selectionArea, this.geometryObject, this.view, this.minShowOrder, this.maxShowOrder, this.coverageLimit)
            }
            var pixels = {};
            for (var i = 0; i < list.length; i++) {
                if (!this.selected.includes(list[i])) {
                    this.selected.push(list[i]);
                }
            }
        } else {
            for (var i = 0; i < this.selected.length; i++) {
                if (this.selected[i].intersectsSelectionArea(selectionArea, this.geometryObject, this.view)) {
                    this.selected.splice(i, 1);
                    i--;
                }
            }

        }

        for (var i = 0; i < this.selected.length; i++) {
            count += this.selected[i].count;
            if (!pixels.hasOwnProperty(this.selected[i].order)) {
                pixels[this.selected[i].order] = [];
            }
            pixels[this.selected[i].order].push(this.selected[i].ipix);
        }
        return {count: count, pixels: pixels};
    };

    Q3CIpix = (function () {
        var children, order, ipix, count;

        Q3CIpix = function (order, ipix, count) {
            this.order = order
            this.ipix = ipix
            this.count = count
            this.coverage = 0.0;
            this.children = {}
        }

        Q3CIpix.prototype.addPixel = function (order, ipix, count) {
            var currOrderIpix = ipix >> 2 * (order - this.order - 1)
            if (this.children.hasOwnProperty(currOrderIpix)) {
                this.children[currOrderIpix].addPixel(order, ipix, count)
            } else {
                var child = new Q3CIpix(this.order + 1, currOrderIpix, count);
                if (order > this.order + 1) {
                    child.addPixel(order, ipix, count)
                }
                this.children[currOrderIpix] = child
            }
            //Sets the count to 0 to indicate to update count function that this needs to be updated.
            this.count = 0;
        }

        Q3CIpix.prototype.updateCount = function () {

            if (this.count > 0) {
                return this.count
            }

            for (key in this.children) {
                this.count += this.children[key].updateCount();
            }

            return this.count
        }

        Q3CIpix.prototype.updateCoverage = function () {

            if (Object.keys(this.children).length == 0) {
                this.coverage = 1.0;
                return 1.0;
            }

            this.coverage = 0.0;
            for (key in this.children) {
                this.coverage += this.children[key].updateCoverage() / 4.0;
            }

            return this.coverage;
        }

        Q3CIpix.prototype.isWithinCyclicRange = function (value, minVal, maxVal, size, cycle) {
            if (minVal <= maxVal) {
                return minVal <= value && value <= maxVal;
            } else {
                return value >= minVal || value <= (maxVal % cycle);
            }
        }

        Q3CIpix.prototype.isInScreen = function (geometryObject, view) {
            const fov = view.fov;

            const quadSize = geometryObject.approxSize(this.order) * 2;
            const quadRaDec = geometryObject.ipix2radec(this.order, this.ipix);
            const lonLat = view.aladin.getRaDec();
            const raCenter = lonLat[0];
            const decCenter = lonLat[1];

            try {
                var xyCenter = view.aladin.world2pix(raCenter, decCenter);
                var xyQuad = view.aladin.world2pix(quadRaDec.ra, quadRaDec.dec);
            } catch (_) {
                return false;
            }

            if (!xyCenter || !xyQuad) {
                return false;
            }

            // Calculate the angular distance between the view center and the ipix
            const distance = view.aladin.angularDist(xyCenter[0], xyCenter[1], xyQuad[0], xyQuad[1]);

            // Check if the angular distance is within half the FOV plus quadSize
            return distance <= (fov / 2 + quadSize);
        }

        Q3CIpix.prototype.draw = function (minOrder, maxOrder, coverageLimit, geometryObject, ctx, view, polygons, shownPolygons) {
            const polygonKey = this.ipix + "-" + this.order;
            const polygon = polygons[polygonKey];

            if (!this.isInScreen(geometryObject, view)) {
                return;
            }

            if (this.shouldBeShown(minOrder, maxOrder, coverageLimit)) {
                polygon.isShowing = true;
                shownPolygons.add(polygon);
            } else {
                polygon.isShowing = false;
                for (let key in this.children) {
                    this.children[key].draw(minOrder, maxOrder, coverageLimit, geometryObject, ctx, view, polygons, shownPolygons);
                }
            }
        }

        Q3CIpix.prototype.createPolygons = function (geometryObject, options, polygons = {}) {
            let raDecCorners = geometryObject.ipix2angCorners(this.order, this.ipix);
            polygons[this.ipix + "-" + this.order] = A.polygon(raDecCorners, options);
            for (let key in this.children) {
                this.children[key].createPolygons(geometryObject, options, polygons);
            }
            return polygons;
        }


        Q3CIpix.prototype.shouldBeShown = function (minOrder, maxOrder, coverageLimit) {
            var nChildren = Object.keys(this.children).length;
            return !(this.order < maxOrder && nChildren > 0 && !(this.order >= minOrder && this.coverage > coverageLimit));
            // return this.order >= maxOrder || nChildren == 0 || (this.order >= minOrder && this.coverage <= coverageLimit);
        }

        Q3CIpix.prototype.returnVisible = function (pixels, minOrder, maxOrder, coverageLimit, geometryObject, view) {

            if (this.isInScreen(geometryObject, view)) {

                if (!this.shouldBeShown(minOrder, maxOrder, coverageLimit)) {

                    for (key in this.children) {
                        this.children[key].returnVisible(pixels, minOrder, maxOrder, coverageLimit, geometryObject, view);
                    }

                } else {

                    if (!(this.order in pixels)) {
                        pixels[this.order] = [this.ipix]
                    } else {
                        pixels[this.order].push(this.ipix);
                    }
                }
            }

        }

        Q3CIpix.prototype.getVisiblePixels = function (pixels, targetOrder, coverageLimit, hasToBeLeaf, geometryObject, view) {
            var nChildren = Object.keys(this.children).length;

            if (this.isInScreen(geometryObject, view)) {

                if (this.order < targetOrder && nChildren > 0) {

                    for (key in this.children) {
                        this.children[key].getVisiblePixels(pixels, targetOrder, coverageLimit, hasToBeLeaf, geometryObject, view)
                    }

                } else {

                    if (nChildren == 0 || !hasToBeLeaf) {
                        if (!(this.order in pixels)) {
                            pixels[this.order] = [this.ipix]
                        } else {
                            pixels[this.order].push(this.ipix);
                        }
                    }
                }
            }

        }

        Q3CIpix.prototype.getVisibleCount = function (minOrder, maxOrder, coverageLimit, geometryObject, view) {
            var nChildren = Object.keys(this.children).length;
            var count = 0;

            if (this.isInScreen(geometryObject, view)) {

                if (!this.shouldBeShown(minOrder, maxOrder, coverageLimit)) {

                    for (key in this.children) {
                        count += this.children[key].getVisibleCount(minOrder, maxOrder, coverageLimit, geometryObject, view)
                    }

                } else {

                    return this.count;
                }
            }
            return count;

        }

        Q3CIpix.prototype.checkPixExists = function (pixOrder, ipix, minOrder, maxOrder, coverageLimit) {
            var pix = this.getShownIpixAtPosition(pixOrder, ipix, minOrder, maxOrder, coverageLimit)
            if (pix && pix.order > 2) {
                return [pix.order, pix.ipix, pix.count];
            } else {
                return [-1, -1, -1];
            }
        }

        Q3CIpix.prototype.getShownIpixAtPosition = function (pixOrder, ipix, minOrder, maxOrder, coverageLimit) {
            currPix = ipix >> 2 * (pixOrder - this.order - 1);
            nChildren = Object.keys(this.children).length;
            if (this.children.hasOwnProperty(currPix) && !this.shouldBeShown(minOrder, maxOrder, coverageLimit)) {
                return this.children[currPix].getShownIpixAtPosition(pixOrder, ipix, minOrder, maxOrder, coverageLimit);
            } else if (this.order >= minOrder && (nChildren == 0 || this.coverage > coverageLimit || this.order == maxOrder)) {
                return this;
            } else if (nChildren == 0 && this.order > 2) {
                return this;
            } else {
                return null;
            }
        }

        Q3CIpix.prototype.getIntersectionSelectionArea = function (list, selectionArea, geometryObject, view, minOrder, maxOrder, coverageLimit) {

            if (this.intersectsSelectionArea(selectionArea, geometryObject, view)) {
                if (this.shouldBeShown(minOrder, maxOrder, coverageLimit)) {
                    list.push(this);
                    return list;
                }

                for (key in this.children) {
                    list = this.children[key].getIntersectionSelectionArea(list, selectionArea, geometryObject, view, minOrder, maxOrder, coverageLimit);
                }
                return list;
            }

            return list;
        }

        Q3CIpix.prototype.intersectsSelectionArea = function (selectionArea, geometryObject, view) {
            let raDecCorners = geometryObject.ipix2angCorners(this.order, this.ipix);
            let footprint = {isShowing: true, polygons: raDecCorners};
            return view.checkFootprintOverlapsSelection(selectionArea.polygon, selectionArea.circle, footprint, true);
        }

    });
    return Q3CMOC;
})();


TextLabel = (function () {
    // constructor
    TextLabel = function (text, raDecCenter, angle, options) {
        options = options || {};

        this.id = 'textlabel-' + uuidv4();
        this.color = options['color'] || undefined;
        this.selectionColor = options["selectionColor"] || '#00ff00';
        this.font = options['font'] || "14px Helvetica, Arial, sans-serif";
        this.lineWidth = options['lineWidth'] || 1.5;
        this.text = text;
        this.overlay = null;
        this.isShowing = true;
        this.isSelected = false;
        this.setCenter(raDecCenter);
        this.setAngle(angle);
    };

    TextLabel.prototype.setOverlay = function (overlay) {
        this.overlay = overlay;
    };

    TextLabel.prototype.show = function () {
        if (this.isShowing) {
            return;
        }
        this.isShowing = true;
        if (this.overlay) {
            this.overlay.reportChange();
        }
    };

    TextLabel.prototype.hide = function () {
        if (!this.isShowing) {
            return;
        }
        this.isShowing = false;
        if (this.overlay) {
            this.overlay.reportChange();
        }
    };


    TextLabel.prototype.setCenter = function (centerRaDec) {
        this.centerRaDec = centerRaDec;
        if (this.overlay) {
            this.overlay.reportChange();
        }
    };

    TextLabel.prototype.setAngle = function (angle) {
        this.angle = angle;
        if (this.overlay) {
            this.overlay.reportChange();
        }
    };

    TextLabel.prototype.isInStroke = function (ctx, view, x, y) {
        return false;
    };

    TextLabel.prototype.setSelectionColor = function (color) {
        if (!color || this.selectionColor == color) {
            return;
        }
        this.selectionColor = color;
        if (this.overlay) {
            this.overlay.reportChange();
        }
    };

    TextLabel.prototype.setHoverColor = function (color) {
        if (!color || this.hoverColor == color) {
            return;
        }
        this.hoverColor = color;
        if (this.overlay) {
            this.overlay.reportChange();
        }
    };

    TextLabel.prototype.draw = function (ctx, view) {
        if (!this.isShowing) {
            return;
        }

        var baseColor = this.color;
        if (!baseColor && this.overlay) {
            baseColor = this.overlay.color;
        }
        if (!baseColor) {
            baseColor = '#008000'
        }


        var centerScreen = view.aladin.world2pix(this.centerRaDec[0], this.centerRaDec[1]);
        if (!centerScreen) {
            return;
        }

        ctx.save();

        ctx.translate(centerScreen[0], centerScreen[1]);
        ctx.rotate(-this.angle * Math.PI / 180);
        ctx.translate(-centerScreen[0], -centerScreen[1]);

        ctx.beginPath();
        ctx.strokeStyle = baseColor;
        ctx.fillStyle = baseColor;
        ctx.font = this.font;
        ctx.font = ctx.font.replace(/(?<value>\d+\.?\d*)/, 14 / (this.overlay.view.fov));
        ctx.lineWidth = this.lineWidth;
        ctx.textAlign = "center";
        ctx.strokeText(this.text, centerScreen[0], centerScreen[1]);
        ctx.fillText(this.text, centerScreen[0], centerScreen[1]);

        ctx.restore();
    };

    TextLabel.prototype.setLineWidth = function (lineWidth) {
        if (this.lineWidth === lineWidth) {
            return;
        }
        this.lineWidth = lineWidth;
        if (this.overlay) {
            this.overlay.reportChange();
        }
    };

    TextLabel.prototype.getLineWidth = function () {
        return this.lineWidth;
    }

    return TextLabel;
})();


var OpenSeaDragonWrapper = (function () {

    "use strict";

    /** Constructor
     *
     */
    var OpenSeaDragonWrapper = function (openSeaDragonObject) {
        this.openSeaDragonObject = openSeaDragonObject;
        this.name = openSeaDragonObject.name;
        this.canvas = openSeaDragonObject.drawer.canvas;
    }

    OpenSeaDragonWrapper.prototype = {

        draw: function (view) {
            var xy = view.aladin.world2pix(this.openSeaDragonObject.ra, this.openSeaDragonObject.dec);

            if (xy) {
                if (!this.openSeaDragonObject.isVisible()) {
                    this.openSeaDragonObject.setVisible(true);
                }

                var xy2 = view.aladin.world2pix(this.openSeaDragonObject.ra + view.fov / 100.0, this.openSeaDragonObject.dec);
                if (!xy2) {
                    var xy2 = view.aladin.world2pix(this.openSeaDragonObject.ra - view.fov / 100.0, this.openSeaDragonObject.dec);
                }

                var dx = (xy[0] - xy2[0]);
                var dy = (xy[1] - xy2[1]);

                var screenRot = Math.atan2(dy, dx) * 180 / Math.PI;

                var screenPos = {x: xy[0] / view.width - 0.5, y: (xy[1] - view.height / 2) / view.width}

                var newZoom = this.openSeaDragonObject.fov / view.fov;
                var angRad = -Math.PI * (this.openSeaDragonObject.rot + screenRot) / 180.0;

                var seaPos = {
                    x: screenPos.x * Math.cos(angRad) - screenPos.y * Math.sin(angRad),
                    y: screenPos.x * Math.sin(angRad) + screenPos.y * Math.cos(angRad)
                }


                var yCenter = 0.5 / this.openSeaDragonObject.whScale;
                var newPoint = new OpenSeadragon.Point(0.5 - seaPos.x / newZoom, yCenter - seaPos.y / newZoom);
                this.openSeaDragonObject.viewport.setRotation(screenRot + this.openSeaDragonObject.rot)
                this.openSeaDragonObject.viewport.panTo(newPoint, true);
                this.openSeaDragonObject.viewport.zoomTo(newZoom, newPoint, true);
            } else {
                this.openSeaDragonObject.setVisible(false);
            }
        }
    }

    return OpenSeaDragonWrapper;
})();