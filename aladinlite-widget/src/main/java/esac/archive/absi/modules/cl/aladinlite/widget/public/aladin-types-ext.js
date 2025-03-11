const originalAladinReference = A

// Aladin
function Aladin(_super) {
    return function (divSelector, options) {
        const self = _super.call(this, divSelector, options);

        self.constructor.AVAILABLE_CALLBACKS.push(
            'mocPixClicked',
            'selectArea',
            'deselectArea',
            'selectSearchArea',
            'clearSearchArea',
            'selectionToolBox');

        self.hasTouchEvents = 'ontouchstart' in window;

        SELECT_SHAPES = 1;
        SELECT_SEARCH_AREA = 2;

        self.onDblClick = function (e) {
            if (self.view.mode === 1) {
                e.preventDefault();
                e.stopImmediatePropagation();
                self.view.selector.dispatch("finish", {});
            }
        };

        self.aladinDiv.ondrop = (event) => {
            event.preventDefault();
            event.stopPropagation();

            const files = getDroppedFilesHandler(event);

            window.handleFilesFromJS(files,function(success) {
                if (success) {
                    files.forEach((file) => {
                        const url = URL.createObjectURL(file);

                        // Consider other cases
                        try {
                            const image = self.createImageFITS(
                                url,
                                {name: file.name},
                                (ra, dec, fov, _) => {
                                    // Center the view around the new fits object
                                    self.gotoRaDec(ra, dec);
                                    self.setFoV(fov * 1.1);
                                }
                            );
                            self.setOverlayImageLayer(image, file.name)
                        } catch(e) {
                            let moc = A.MOCFromURL(url);
                            self.addMOC(moc);

                            console.error("Only valid fits files supported (i.e. containig a WCS)", e)
                        }
                    });
                }
            });
        }

        if (!self.hasTouchEvents) {
            self.view.catalogCanvas.addEventListener('dblclick', self.onDblClick, true);
        }

        self.view.searchArea = {};
        self.view.filterOverlay = A.graphicOverlay({
            color: '#ffffff',
            name: 'filterOverlay',
            lineWidth: 4,
            selectable: false
        });
        self.view.filterOverlay.setView(self.view);

        self.view.selectType = SELECT_SHAPES;
        self.view.setSelectionType = function (type) {
            if (type === "SEARCH") {
                self.view.selectType = SELECT_SEARCH_AREA;
            } else {
                self.view.selectType = SELECT_SHAPES;
            }
        }

        self.view.isShapeSelect = function () {
            return !self.view.selectType || self.view.selectType === SELECT_SHAPES;
        }

        self.view.clearSearchArea = function () {
            if (Object.keys(this.searchArea).length > 0) {
                self.view.searchArea = {}
                self.view.filterOverlay.removeAll();
                self.view.mustClearCatalog = true;
                self.view.mustClearFilter = true;
                var clearSearchAreaFunction = self.view.aladin.callbacksByEventName['clearSearchArea'];
                if (typeof clearSearchAreaFunction === 'function') {
                    clearSearchAreaFunction();

                }
                self.view.requestRedraw();
            }
        }

        self.view.openseadragons = []

        self.view.getCanvasDataURLCopy = function(imgType, width, height, includeOpenSeaDragon) {
            imgType = imgType || "image/png";

            const canvas = includeOpenSeaDragon === true
                && self.view.getCanvasWithOpenSeaDragon(width, height)
                || self.view.getCanvasCopy(width, height, true);

            return canvas.toDataURL(imgType);
        }

        self.view.getCanvasCopy = function(width, height, includeReticle) {
            var c = document.createElement('canvas');
            width = width || self.view.width;
            height = height || self.view.height;
            includeReticle = (typeof includeReticle === 'undefined') ? true : includeReticle;
            c.width = width;
            c.height = height;
            var ctx = c.getContext('2d');
            ctx.drawImage(self.view.imageCanvas, 0, 0, c.width, c.height);
            ctx.drawImage(self.view.catalogCanvas, 0, 0, c.width, c.height);
            // if (includeReticle) {
            //     ctx.drawImage(self.view.reticleCanvas, 0, 0, c.width, c.height);
            // }

            return c;
        };
        self.view.getCanvasWithOpenSeaDragon = function(width, height) {
            var c = self.view.getCanvasCopy(width, height, false);
            var ctx = c.getContext('2d');
            self.view.openseadragons.forEach(element => ctx.drawImage(element.canvas, 0, 0, c.width, c.height));

            return c;
        }

        const originalCreateCanvases = self.view.createCanvases;
        self.view.createCanvases = function () {
            self.view.createExtraCanvases();
            originalCreateCanvases.call(self.view);
        }

        self.view.createExtraCanvases = function () {
            var filterCanvas = this.aladinDiv.querySelector('.aladin-filterCanvas')
            if (filterCanvas) {
                filterCanvas.remove();
            }

            var createCanvas = (name) => {
                // Create a new canvas element
                let canvas = document.createElement('canvas');
                canvas.className = name;

                // Append the canvas to the aladinDiv
                self.view.aladinDiv.insertBefore(canvas, self.view.aladinDiv.firstChild);

                return canvas;
            };

            self.view.filterCanvas = createCanvas('aladin-filterCanvas');
            self.view.filterCanvas.style = 'filter: blur(5px)';
        }

        const originalFixLayoutDimensions = self.view.fixLayoutDimensions;
        self.view.fixLayoutDimensions = function () {
            originalFixLayoutDimensions.call(self.view);
            self.view.filterCtx = self.view.filterCanvas.getContext("2d");
            self.view.filterCtx.canvas.width = self.view.width;
            self.view.filterCtx.canvas.height = self.view.height;
        }


        self.view.createExtraCanvases();
        self.view.fixLayoutDimensions();

        // Draw Q3C MOCs
        const originalDrawAllOverlays = self.view.drawAllOverlays;
        self.view.drawAllOverlays = function () {
            originalDrawAllOverlays.call(self.view);

            // Draw OSD
            if (self.view.openseadragons && self.view.openseadragons.length > 0) {
                for (var i = 0; i < self.view.openseadragons.length; i++) {
                    var openSeaDragonObject = self.view.openseadragons[i];
                    openSeaDragonObject.draw(self.view);
                }
            }

            // Draw Q3C MOCS
            self.view.mocs.forEach(function (moc) {
                if (moc instanceof Q3CMOC) {
                    moc.draw(self.view.catalogCtx);
                }
            });
            self.view.filterCtx.clearRect(0, 0, self.view.width, self.view.height);

            // Draw Search area
            if (Object.keys(self.view.searchArea).length > 0) {
                // Blur
                let ctx = self.view.filterCtx;
                ctx.globalCompositeOperation = "source-over";
                ctx.fillStyle = 'rgba(100, 100, 100, 0.1)';
                ctx.drawImage(self.view.imageCanvas, 0, 0, self.view.width, self.view.height);
                ctx.fillRect(0, 0, self.view.width, self.view.height);

                // Fill clear area
                self.view.filterOverlay.overlayItems.forEach((item) => item.setLineWidth(0.0))
                const anyDrawn = self.view.filterOverlay.overlayItems.some(item => item.draw(ctx, self.view));
                if (anyDrawn) {
                    ctx.globalCompositeOperation = "destination-out";
                    ctx.fillStyle = 'rgba(255, 255, 255, 1)';
                    ctx.fill();
                }

            } else if (self.view.mustClearFilter) {
                self.view.filterCtx.clearRect(0, 0, self.view.width, self.view.height);
            }


            // Draw search area outline
            var catalogCtx = self.view.catalogCtx;
            if (Object.keys(self.view.searchArea).length > 0) {
                self.view.mustClearCatalog = true;
                self.view.filterOverlay.overlayItems.forEach((item) => item.setLineWidth(4.0));
                self.view.filterOverlay.draw(catalogCtx);
            }
        };

        self.getVisiblePixelsTargetOrderInMOC = function (moc, targetOrder, hasToBeLeaf) {
            var view = self.view;
            return moc.getVisiblePixelsTargetOrder(targetOrder, hasToBeLeaf, view.projection, view.cooFrame, view.width, view.height, view.largestDim, view.zoomFactor, view.fov);
        }

        self.getVisiblePixelsInMOC = function (moc) {
            var view = self.view;
            return moc.getVisiblePixels(view.projection, view.cooFrame, view.width, view.height, view.largestDim, view.zoomFactor, view.fov);
        }

        // Subscribe to AL events instead of binding directly since call order is important
        self.view.aladinDiv.addEventListener(
            "AL:Event",
            function (e) {
                const details = e.detail;
                if (details && (details.type === "click" || details.type === "touchend" || details.type === "touchcancel")) {
                    const xymouse = EventUtils().relMouseCoords(details.ev);
                    var wasDragging = self.view.realDragging;
                    var objs = self.view.closestObjects(xymouse.x, xymouse.y, 5);

                    // Footprints click
                    if (!wasDragging && objs?.length > 0 && objs[0].isFootprint()) {
                        dispatchFootprintClickEvent(objs[0]);
                    }

                    // Click Q3C MOC
                    if (!wasDragging && self.view.mode !== 1) {
                        try {
                            const pos = self.pix2world(xymouse.x, xymouse.y, 'icrs');
                            let mocsClicked = [];
                            self.view.mocs.forEach(function (moc) {
                                if (moc instanceof Q3CMOC) {
                                    let foundPix = moc.selectPix(14, pos[0], pos[1]);

                                    if (foundPix[1] > 0) {
                                        let pixels = {[foundPix[0]]: foundPix[1]};
                                        mocsClicked.push({"name": moc.name, "pixels": pixels, "count": foundPix[2]});
                                    }
                                }
                            });

                            if (mocsClicked.length > 0) {
                                var mocPixClickedFn = self.view.aladin.callbacksByEventName['mocPixClicked'];
                                (typeof mocPixClickedFn === 'function') && mocPixClickedFn(mocsClicked, xymouse.x, xymouse.y);
                            }
                        } catch (err) {
                            console.log(err);
                        }
                    }
                }
            }
        );

        self.createQ3CMOC = function (options) {
            return new Q3CMOC(options);
        };

        self.createTextLabel = function (text, ra, dec, angle, options) {
            return new TextLabel(text, [ra, dec], angle, options);
        }

        self.getVisibleCountInMOC = function (moc) {
            return moc.getVisibleCount(
                self.view.projection,
                self.view.cooFrame,
                self.view.width,
                self.view.height,
                self.view.largestDim,
                self.view.zoomFactor,
                self.view.fov);
        }

        self.addOpenSeaDragon = function (openSeaDragonObject) {
            self.openSeaDragonWrapper = new OpenSeaDragonWrapper(openSeaDragonObject);
            self.removeAllOpenSeaDragons();
            self.view.openseadragons.push(self.openSeaDragonWrapper);
            self.view.requestRedraw()
        };

        self.setOpenSeaDragonOpacity = function (opacity) {
            self.setOpenSeaDragonOpacity(opacity);
        }

        self.removeOpenSeaDragon = function (name) {
            self.removeOpenSeaDragonWrapper(name);
        };

        self.setOpenSeaDragonOpacity = function (opacity) {
            for (var i = 0; i < self.view.openseadragons.length; i++) {
                var image = self.view.openseadragons[i].openSeaDragonObject.image
                    ?? self.view.openseadragons[i].openSeaDragonObject.world.getItemAt(0);

                if (image) {
                    image.setOpacity(opacity);
                }
            }
        };

        self.removeAllOpenSeaDragons = function () {
            for (var i = 0; i < self.view.openseadragons.length; i++) {
                self.view.openseadragons[i].openSeaDragonObject.destroy();
                self.view.openseadragons[i].openSeaDragonObject = null;
            }
            self.view.openseadragons = [];
        }

        self.removeOpenSeaDragonWrapper = function (name) {
            self.removeAllOpenSeaDragons();
            for (var i = 0; i < self.view.openseadragons.length; i++) {
                if (self.view.openseadragons[i].name === name) {
                    self.view.openseadragons[i].openSeaDragonObject.destroy();
                    self.view.openseadragons[i].openSeaDragonObject = null;
                    self.view.openseadragons.splice(i, 1);
                }
            }
        };


        return self;
    }
}

A.aladin = Aladin(originalAladinReference.aladin);


// Catalog
function Catalog(_super) {
    return function (options) {
        // Call the original A.catalog function to get the instance of Catalog
        var self = _super.call(this, options);

        if (options.arrowColor) {
            self.arrowColor = options.arrowColor;
        }
        if (options.arrowWidth) {
            self.arrowWidth = options.arrowWidth;
        }
        if (options.arrowLength) {
            self.arrowLength = options.arrowLength;
            self.arrowScale = 1.0;
        }

        // Add new methods or override existing methods
        self.setArrowColor = function (arrowColor) {
            self.arrowColor = arrowColor;
            self.reportChange();
        };

        self.setArrowScale = function (arrowScale) {
            self.arrowScale = arrowScale;
            self.reportChange();
        };

        self.setCatalogAvgProperMotion = function (removeAvgPM, useMedian, reportChange) {
            self.removeAvgPM = removeAvgPM;
            self.avgPMRaInc = 0.0;
            self.avgPMDecInc = 0.0;

            if (self.removeAvgPM) {
                if (!useMedian) {
                    //Recalculates the average proper motion
                    var sumPMRaInc = 0.0;
                    var sumPMDecInc = 0.0;
                    var visibleSources = 0;

                    for (var k = 0, len = self.sources.length; k < len; k++) {
                        var source = self.sources[k];
                        if (source.isShowing && source.data && source.data["arrowRa"] && source.data["arrowDec"]) {

                            if (typeof source.data["arrowRa"] === 'string' || source.data["arrowRa"] instanceof String) {
                                //Avoid parsing to float each draw, data comes as string from GWT
                                source.data["arrowRa"] = parseFloat(source.data["arrowRa"]);
                                source.data["arrowDec"] = parseFloat(source.data["arrowDec"]);
                            }

                            sumPMRaInc += (source.data["arrowRa"] - parseFloat(source.ra));
                            sumPMDecInc += (source.data["arrowDec"] - parseFloat(source.dec));
                            visibleSources++;

                            //Cleans stored calculations on source
                            source.data["avgPMra"] = null;
                            source.data["avgPMdec"] = null;
                        }
                    }

                    if (visibleSources > 0) {
                        self.avgPMRaInc = sumPMRaInc / visibleSources;
                        self.avgPMDecInc = sumPMDecInc / visibleSources;
                    }

                } else {
                    //Calculates the median of the sources pm slopes -> decInc / raInc
                    var pmSlopes = [];
                    for (var k = 0, len = self.sources.length; k < len; k++) {
                        var source = self.sources[k];
                        if (source.isShowing && source.data && source.data["arrowRa"] && source.data["arrowDec"]) {

                            if (typeof source.data["arrowRa"] === 'string' || source.data["arrowRa"] instanceof String) {
                                //Avoid parsing to float each draw, data comes as string from GWT
                                source.data["arrowRa"] = parseFloat(source.data["arrowRa"]);
                                source.data["arrowDec"] = parseFloat(source.data["arrowDec"]);
                            }

                            // Calculates the slope and stores it in an object
                            var raInc = (source.data["arrowRa"] - parseFloat(source.ra));
                            var decInc = (source.data["arrowDec"] - parseFloat(source.dec));
                            var slope = decInc / raInc;
                            pmSlopes.push({
                                "slope": slope,
                                "raInc": raInc,
                                "decInc": decInc
                            });

                            //Cleans stored calculations on source
                            source.data["avgPMra"] = null;
                            source.data["avgPMdec"] = null;
                        }
                    }

                    if (pmSlopes.length > 0) {
                        pmSlopes.sort(function (a, b) {
                            return a.slope - b.slope;
                        });

                        var half = Math.floor(pmSlopes.length / 2);

                        if (pmSlopes.length % 2) {
                            self.avgPMRaInc = pmSlopes[half].raInc;
                            self.avgPMDecInc = pmSlopes[half].decInc;
                        } else {
                            self.avgPMRaInc = (pmSlopes[half - 1].raInc + pmSlopes[half].raInc) / 2.0;
                            self.avgPMDecInc = (pmSlopes[half - 1].decInc + pmSlopes[half].decInc) / 2.0;
                        }

                    }
                }
            }

            if (reportChange) {
                self.reportChange();
            }
        };


        const originalDraw = self.draw;
        self.draw = function (ctx, frame, width, height, largestDim, zoomFactor) {
            // Call the original draw function
            originalDraw.call(self, ctx, frame, width, height, largestDim, zoomFactor);
        };

        return self;
    };
}

// Intercept A.catalog function
A.catalog = Catalog(originalAladinReference.catalog);


function Source(_super) {
    return function (ra, dec, data, options) {
        var self = _super.call(this, ra, dec, data, options);

        const originalActionClicked = self.actionClicked;
        self.actionClicked = function (obj) {
            originalActionClicked.call(self, obj);

            var selectEventParams = {
                detail: {
                    id: self.id,
                    overlayName: self.catalog.name,
                    shapeobj: self
                }
            }

            // if (!self.isSelected) {
            //     window.dispatchEvent(new CustomEvent("shapeSelected", selectEventParams));
            // } else {
            //     window.dispatchEvent(new CustomEvent("shapeDeselected", selectEventParams));
            // }
        };

        return self;
    };
}

// Intercept A.source function
A.source = Source(originalAladinReference.source);