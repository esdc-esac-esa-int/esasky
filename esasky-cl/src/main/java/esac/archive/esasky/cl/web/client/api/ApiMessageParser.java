package esac.archive.esasky.cl.web.client.api;

public class ApiMessageParser {
	
	public static native void init(Api api,  ApiCounts apiCounts, ApiEvents apiEvents, 
			ApiExtTap apiExtTap, ApiHips apiHips, ApiMoc apiMoc, ApiModules apiModules, ApiOverlay apiOverlay,
			ApiPanel apiPanel, ApiPlanning apiPlanning, ApiPlot apiPlot, ApiView apiView) /*-{
	
		function handleMessage(e){
			var msg = e.data
			console.log('message received');
			console.log(msg);
			
			if('origin' in msg){
				if(msg.origin == 'pyesasky'){
					@esac.archive.esasky.cl.web.client.api.Api::setGoogleAnalyticsCatToPython()();
				}
				else{
					@esac.archive.esasky.cl.web.client.api.Api::setGoogleAnalyticsCatToAPI()();
				}
			}else{
					@esac.archive.esasky.cl.web.client.api.Api::setGoogleAnalyticsCatToAPI()();
			}
			
			//Handling of empty properties on java side
			if(!msg.hasOwnProperty('content')){
				msg['content'] = {};
			}
			
			switch (msg.event) {
				case 'initTest':
					console.log('initTest event captured');
					apiEvents.@esac.archive.esasky.cl.web.client.api.ApiEvents::sendInitMessage(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
					break;
	
	
				//API VIEW
	
				case 'goToTargetName':
					console.log('goToTargetName event captured');
					apiView.@esac.archive.esasky.cl.web.client.api.ApiView::goToTargetName(Ljava/lang/String;)(msg.content.targetName);
					break;
				
				case 'setFov':
					console.log('setFoV event captured');
					apiView.@esac.archive.esasky.cl.web.client.api.ApiView::setFoV(D)(msg.content.fov);
					break;
				
				case 'goToRaDec':
					console.log('goToRADec event captured!');
					apiView.@esac.archive.esasky.cl.web.client.api.ApiView::goTo(Ljava/lang/String;Ljava/lang/String;)
						(msg.content.ra, msg.content.dec);
					break;
	
				case 'getCenter':
					console.log('getCenter event captured');
					if(!msg.content.hasOwnProperty('cooFrame')){
						msg.content['cooFrame'] = "";
					}
					apiView.@esac.archive.esasky.cl.web.client.api.ApiView::getCenter(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)
						(msg.content.cooFrame,e);
					break;	
					
					
					
				// HIPS	
					
				case 'setHipsColorPalette':
					console.log('setHiPSColorPalette event captured!');
					apiHips.@esac.archive.esasky.cl.web.client.api.ApiHips::setHiPSColorPalette(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)
						(msg.content.colorPalette, e);
					break;
	
				case 'changeHips':
					console.log('changeHips event captured!');
					console.log(msg);
					apiHips.@esac.archive.esasky.cl.web.client.api.ApiHips::setHiPS(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)
						(msg.content.hipsName,e);
					break;
	
				case 'addHips':
					console.log('addHips event captured!');
					console.log(msg);
					apiHips.@esac.archive.esasky.cl.web.client.api.ApiHips::addHiPS(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)
						(msg.content.hipsName,e);
					break;
				
				case 'changeHipsWithParamsOld':
					console.log('changeHiPSWithParams event captured!');
					console.log(msg);
					console.log("HiPS URL "+msg.content.hips.url);
					apiHips.@esac.archive.esasky.cl.web.client.api.ApiHips::setHiPSWithParams(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;)
						(msg.content.hips.name, msg.content.hips.url, msg.content.hips.cooframe, msg.content.hips.maxnorder, msg.content.hips.imgformat);
					break
				
				case 'changeHipsWithParams':
					console.log('changeHiPSWithParams event captured!');
					console.log(msg);
					console.log("HiPS URL "+msg.content.hips.url);
					apiHips.@esac.archive.esasky.cl.web.client.api.ApiHips::setHiPSWithParams(Ljava/lang/String;Ljava/lang/String;ZLcom/google/gwt/core/client/JavaScriptObject;)
						(msg.content.hips.name, msg.content.hips.url, false, e);
					break
	
				case 'addHipsWithParams':
					console.log('changeHiPSWithParams event captured!');
					console.log(msg);
					console.log("HiPS URL "+msg.content.hips.url);
					apiHips.@esac.archive.esasky.cl.web.client.api.ApiHips::setHiPSWithParams(Ljava/lang/String;Ljava/lang/String;ZLcom/google/gwt/core/client/JavaScriptObject;)
						(msg.content.hips.name, msg.content.hips.url, true, e);
					break
	
				case 'addHipsWithParamsOld':
					console.log('addHipsWithParams event captured!');
					console.log(msg);
					console.log("HiPS URL "+msg.content.hips.url);
					apiHips.@esac.archive.esasky.cl.web.client.api.ApiHips::addHiPSWithParams(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;)
						(msg.content.hips.name, msg.content.hips.url, msg.content.hips.cooframe, msg.content.hips.maxnorder, msg.content.hips.imgformat);
					break
					
				case 'removeHips':
					console.log('removeHipsOnIndex event captured!');
					console.log(msg);
					apiHips.@esac.archive.esasky.cl.web.client.api.ApiHips::removeSkyRow(ILcom/google/gwt/core/client/JavaScriptObject;)(msg.content.index,e);
					break;
	
				case 'openSkyPanel':
					console.log('openSkyPanel event captured!');
					console.log(msg);
					apiHips.@esac.archive.esasky.cl.web.client.api.ApiHips::openSkyPanel()();
					break;
				
				case 'closeSkyPanel':
					console.log('closeSkyPanel event captured!');
					console.log(msg);
					apiHips.@esac.archive.esasky.cl.web.client.api.ApiHips::closeSkyPanel()();
					break;
				
				case 'getNumberOfSkyRows':
					console.log('getNumberOfSkyRows event captured!');
					console.log(msg);
					apiHips.@esac.archive.esasky.cl.web.client.api.ApiHips::getNumberOfSkyRows(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
					break;

				case 'getAvailableHiPS':
				case 'getAvailableHips':
					console.log('getAvailableHiPS event captured!');
					console.log(msg);
					if(!msg.content.hasOwnProperty('wavelength')){
						msg.content['wavelength'] = "";
					}
					apiHips.@esac.archive.esasky.cl.web.client.api.ApiHips::getAvailableHiPS(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)
						(msg.content.wavelength,e);
					break;	
					
				case 'setHipsSliderValue':
					console.log('setHipsSliderValue event captured!');
					console.log(msg);
					apiHips.@esac.archive.esasky.cl.web.client.api.ApiHips::setHiPSSliderValue(D)(msg.content.value);
					break;
					
					
				// OVERLAY	
					
					
				case 'overlayFootprints':
					console.log('overlayFootprints event captured!');
					console.log(msg);
					var footprintSetJSON = JSON.stringify(msg.content);
					apiOverlay.@esac.archive.esasky.cl.web.client.api.ApiOverlay::overlayFootprints(Ljava/lang/String;Z)(footprintSetJSON, false);
					break;
	
				case 'overlayFootprintsWithDetails':
					console.log('overlayFootprintsWithDetails event captured!');
					console.log(msg);
					var footprintSetJSON = JSON.stringify(msg.content);
					apiOverlay.@esac.archive.esasky.cl.web.client.api.ApiOverlay::overlayFootprints(Ljava/lang/String;Z)(footprintSetJSON, true);
					break;
				
				case 'clearFootprintsOverlay':
					console.log('clearFootprintsOverlay event captured!');
					console.log(msg);
					apiOverlay.@esac.archive.esasky.cl.web.client.api.ApiOverlay::removeOverlay(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)
						(msg.content.overlayName, e);
					break;
	
				case 'deleteFootprintsOverlay':
					console.log('deleteFootprintsOverlay event captured!');
					console.log(msg);
					apiOverlay.@esac.archive.esasky.cl.web.client.api.ApiOverlay::removeOverlay(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)
						(msg.content.overlayName, e);
					break;
				
				case 'removeAllOverlays':
					console.log('removeAllOverlays event captured!');
					console.log(msg);
					apiOverlay.@esac.archive.esasky.cl.web.client.api.ApiOverlay::removeAllOverlays(Lcom/google/gwt/core/client/JavaScriptObject;)
						(e);
					break;
	
				case 'setOverlayColor':
					console.log('setOverlayColor event captured!');
					console.log(msg);
					apiOverlay.@esac.archive.esasky.cl.web.client.api.ApiOverlay::setOverlayColor(Ljava/lang/String;Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)
						(msg.content.overlayName, msg.content.color, e);
					break;
	
				case 'setOverlaySize':
					console.log('setOverlayColor event captured!');
					console.log(msg);
					apiOverlay.@esac.archive.esasky.cl.web.client.api.ApiOverlay::setOverlaySize(Ljava/lang/String;DLcom/google/gwt/core/client/JavaScriptObject;)
						(msg.content.overlayName, msg.content.size, e);
					break;
					
				case 'setOverlayShape':
					console.log('setOverlayColor event captured!');
					console.log(msg);
					apiOverlay.@esac.archive.esasky.cl.web.client.api.ApiOverlay::setOverlayShape(Ljava/lang/String;Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)
						(msg.content.overlayName, msg.content.shape, e);
					break;
	
				case 'getActiveOverlays':
					console.log('getActiveOverlays event captured!');
					console.log(msg);
					apiOverlay.@esac.archive.esasky.cl.web.client.api.ApiOverlay::getActiveOverlays(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
					break;
	
				case 'overlayCatalogue':
					console.log('overlayCatalogue event captured!');
					console.log(msg);
					var catJSON = JSON.stringify(msg.content);
					apiOverlay.@esac.archive.esasky.cl.web.client.api.ApiOverlay::overlayCatalogue(Ljava/lang/String;Z)(catJSON, false);
					break;
	
				case 'overlayCatalogueWithDetails':
					console.log('overlayCatalogueWithDetails event captured!');
					console.log(msg);
					var userCatalogueJSON = JSON.stringify(msg.content);
					apiOverlay.@esac.archive.esasky.cl.web.client.api.ApiOverlay::overlayCatalogue(Ljava/lang/String;Z)(userCatalogueJSON, true);
					break;
				
				case 'clearCatalogue':
					console.log('clear catalgue event captured!');
					console.log(msg);
					apiOverlay.@esac.archive.esasky.cl.web.client.api.ApiOverlay::removeOverlay(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)
						(msg.content.overlayName, e);
					break;
	
				case 'deleteCatalogue':
					console.log('remove catalgue event captured!');
					console.log(msg);
					apiOverlay.@esac.archive.esasky.cl.web.client.api.ApiOverlay::removeOverlay(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)
						(msg.content.overlayName, e);
					break;
					
					
				case 'selectShape':
					console.log('selectShape event captured!');
					console.log(msg);
					apiOverlay.@esac.archive.esasky.cl.web.client.api.ApiOverlay::selectShape(Ljava/lang/String;Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)
						(msg.content.overlayName, msg.content.shapeName, e);
					break;
	
				case 'deselectShape':
					console.log('deselectShape event captured!');
					console.log(msg);
					apiOverlay.@esac.archive.esasky.cl.web.client.api.ApiOverlay::deselectShape(Ljava/lang/String;Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)
						(msg.content.overlayName, msg.content.shapeName, e);
					break;
	
	
				// API PLANNING
					
				case 'addJwst':
					console.log('addJwst event captured!');
					console.log(msg);
					apiPlanning.@esac.archive.esasky.cl.web.client.api.ApiPlanning::addJwst(Ljava/lang/String;Ljava/lang/String;ZLcom/google/gwt/core/client/JavaScriptObject;)
						(msg.content.instrument, msg.content.detector, msg.content.showAllInstruments, e);
					break;
	
				case 'addJwstWithCoordinates':
					console.log('addJwstWithCoordinates event captured!');
					console.log(msg);
					apiPlanning.@esac.archive.esasky.cl.web.client.api.ApiPlanning::addJwstWithCoordinates(Ljava/lang/String;Ljava/lang/String;ZLjava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)
						(msg.content.instrument, msg.content.detector, msg.content.showAllInstruments, msg.content.ra, msg.content.dec, msg.content.rotation, e);
					break;
					
				case 'closeJwstPanel':
					console.log('closeJwst event captured!');
					console.log(msg);
					apiPlanning.@esac.archive.esasky.cl.web.client.api.ApiPlanning::closeJwstPanel()();
					break;
					
				case 'openJwstPanel':
					console.log('openJwst event captured!');
					console.log(msg);
					apiPlanning.@esac.archive.esasky.cl.web.client.api.ApiPlanning::openJwstPanel()();
					break;
		
				case 'clearJwstAll':
					console.log('clearJwstAll event captured!');
					console.log(msg);
					apiPlanning.@esac.archive.esasky.cl.web.client.api.ApiPlanning::clearJwst()();
					break;
					
				// COUNTS	
					
				case 'getObservationsCount':
					console.log('getObservations event captured');
					apiCounts.@esac.archive.esasky.cl.web.client.api.ApiCounts::getObservationsCount(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
					break;	
					
				case 'getCataloguesCount':
					console.log('getCataloguesCount event captured');
					apiCounts.@esac.archive.esasky.cl.web.client.api.ApiCounts::getCataloguesCount(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
					break;	
					
				case 'getSpectraCount':
					console.log('getSpectraCount event captured');
					apiCounts.@esac.archive.esasky.cl.web.client.api.ApiCounts::getSpectraCount(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
					break;	
					
					
				// PLOT	
					
				case 'plotObservations':
					console.log('plotObservations event captured');
					if(msg.content.ra != null){
						apiPlot.@esac.archive.esasky.cl.web.client.api.ApiPlot::coneSearchObservations(Ljava/lang/String;DDDLcom/google/gwt/core/client/JavaScriptObject;)
							(msg.content.missionId, msg.content.ra, msg.content.dec, msg.content.radius, e);
					}else{
						apiPlot.@esac.archive.esasky.cl.web.client.api.ApiPlot::plotObservations(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)
							(msg.content.missionId,e);
					}
					break;	
					
				case 'plotCatalogues':
					console.log('plotCatalogues event captured');
					if(msg.content.ra != null){
						apiPlot.@esac.archive.esasky.cl.web.client.api.ApiPlot::coneSearchCatalogues(Ljava/lang/String;DDDLcom/google/gwt/core/client/JavaScriptObject;)
							(msg.content.missionId, msg.content.ra, msg.content.dec, msg.content.radius, e);
					}else{
						apiPlot.@esac.archive.esasky.cl.web.client.api.ApiPlot::plotCatalogues(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)
							(msg.content.missionId,e);
					}
					break;	
	
				case 'plotSpectra':
					console.log('plotSpectra event captured');
					if(msg.content.ra != null){
						apiPlot.@esac.archive.esasky.cl.web.client.api.ApiPlot::coneSearchSpectra(Ljava/lang/String;DDDLcom/google/gwt/core/client/JavaScriptObject;)
							(msg.content.missionId, msg.content.ra, msg.content.dec, msg.content.radius, e);
					}else{
						apiPlot.@esac.archive.esasky.cl.web.client.api.ApiPlot::plotSpectra(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)
							(msg.content.missionId,e);
					}
					break;	
	
				case 'plotPublications':
					console.log('plotPublications event captured');
					if(msg.content.ra != null){
						apiPlot.@esac.archive.esasky.cl.web.client.api.ApiPlot::coneSearchPublications(DDDLcom/google/gwt/core/client/JavaScriptObject;)
							(msg.content.missionId, msg.content.ra, msg.content.dec, msg.content.radius, e);
					}else{
						apiPlot.@esac.archive.esasky.cl.web.client.api.ApiPlot::plotPublications(Lcom/google/gwt/core/client/JavaScriptObject;)
							(e);
					}
					break;	
					
					
				//API PANEL	
					
					
				case 'getResultPanelData':
					console.log('getResultPanelData event captured');
					apiPanel.@esac.archive.esasky.cl.web.client.api.ApiPanel::getResultPanelData(Lcom/google/gwt/core/client/JavaScriptObject;)
						(e);
					break;	
	
				case 'closeResultPanelTab':
					console.log('closeResultPanelTab event captured');
					apiPanel.@esac.archive.esasky.cl.web.client.api.ApiPanel::closeResultPanelTab(I)(msg.content.index);
					break;	
	
				//Keeping this for legacy
				case 'closeDataPanel':
					console.log('closeDataPanel event captured');
					apiPanel.@esac.archive.esasky.cl.web.client.api.ApiPanel::hideResultPanel()();
					break;	
	
				case 'hideResultPanel':
					console.log('hideResultPanel event captured');
					apiPanel.@esac.archive.esasky.cl.web.client.api.ApiPanel::hideResultPanel()();
					break;	
	
				case 'showResultPanel':
					console.log('hideResultPanel event captured');
					apiPanel.@esac.archive.esasky.cl.web.client.api.ApiPanel::showResultPanel()();
					break;	
					
				case 'closeResultPanelTabById':
					console.log('closeResultPanelTabById event captured');
					apiPanel.@esac.archive.esasky.cl.web.client.api.ApiPanel::closeResultPanelTabById(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)
					(msg.content.id, e);
					break;	

				case 'closeAllResultPanelTabs':
					console.log('closeResultPanelTab event captured');
					apiPanel.@esac.archive.esasky.cl.web.client.api.ApiPanel::closeAllResultPanelTabs()();
					break;	
					
					
				// API EXTTAP	
					
				case 'plotTapService':
					console.log('plotTapService event captured');
					apiExtTap.@esac.archive.esasky.cl.web.client.api.ApiExtTap::plotExtTap(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)
					(msg.content.tapService, e);
					break;	
					
				case 'getTapServiceCount':
					console.log('getTapServiceCount event captured');
					apiExtTap.@esac.archive.esasky.cl.web.client.api.ApiExtTap::extTapCount(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)
						(msg.content.tapService, e);
					break;	
	
				case 'getAvailableTapServices':
					console.log('getAvailableTapServices event captured');
					apiExtTap.@esac.archive.esasky.cl.web.client.api.ApiExtTap::getAvailableTapServices(Lcom/google/gwt/core/client/JavaScriptObject;)
						(e);
					break;	
	
				case 'getAllAvailableTapMissions':
					console.log('getAllAvailableTapMissions event captured');
					apiExtTap.@esac.archive.esasky.cl.web.client.api.ApiExtTap::getAllAvailableTapMissions(Lcom/google/gwt/core/client/JavaScriptObject;)
						(e);
					break;	
					
				case 'getTapADQL':
					console.log('getTapADQL event captured');
					apiExtTap.@esac.archive.esasky.cl.web.client.api.ApiExtTap::getExtTapADQL(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)
						(msg.content.tapService, e);
					break;	
	
				case 'plotTapServiceWithDetails':
					console.log('plotTapServiceWithDetails event captured');
					if(!msg.content.hasOwnProperty('color')){
						msg.content['color'] = "";
					}
					if(!msg.content.hasOwnProperty('limit')){ 
						msg.content['limit'] = -1;
					}
					if(!msg.content.hasOwnProperty('dataOnlyInView')){ 
						msg.content['dataOnlyInView'] = false;
					}
					
					if(msg.content['dataOnlyInView']){
						apiExtTap.@esac.archive.esasky.cl.web.client.api.ApiExtTap::plotExtTapWithDetails(Ljava/lang/String;Ljava/lang/String;ZLjava/lang/String;Ljava/lang/String;ILesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject; )
							(msg.content.name, msg.content.tapUrl, msg.content.dataOnlyInView, msg.content.adql, msg.content.color, msg.content.limit, msg.content);
					
					}else{
						apiExtTap.@esac.archive.esasky.cl.web.client.api.ApiExtTap::plotExtTapADQL(Lesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)
							(msg.content, e);
					}
					break;	
					
					
				// MOC	
	
				case 'getVisibleNpix':
					console.log('getVisibleNpix event captured');
					apiMoc.@esac.archive.esasky.cl.web.client.api.ApiMoc::getVisibleNpix(I)
						(msg.content.norder);
					break;	
					
				case 'addMOC':
					console.log('addMOC event captured');
					var name = msg.content.name || 'MOC';
					apiMoc.@esac.archive.esasky.cl.web.client.api.ApiMoc::addMOC(Ljava/lang/String;Lesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject;Lesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject;)
						(name, msg.content.options, msg.content.mocData);
					break;	
					
				case 'removeMOC':
					console.log('removeMOC event captured');
					apiMoc.@esac.archive.esasky.cl.web.client.api.ApiMoc::removeMOC(Ljava/lang/String;)
						(msg.content.name);
					break;	
					
				case 'addQ3CMOC':
					console.log('addQ3CMOC event captured');
					apiMoc.@esac.archive.esasky.cl.web.client.api.ApiMoc::addQ3CMOC(Ljava/lang/String;Ljava/lang/String;)
						(msg.content.options, msg.content.mocData);
					break;
					
					
				// API Modules		
					
				case 'addCustomTreeMap':
					console.log('addCustomTreeMap event captured');
					apiModules.@esac.archive.esasky.cl.web.client.api.ApiModules::addCustomTreeMap(Lesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)
						(msg.content.treeMap,e);
					break;	
					
				case 'setModuleVisibility':
					console.log('setModuleVisibility event captured');
					apiModules.@esac.archive.esasky.cl.web.client.api.ApiModules::setModuleVisibility(Lesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)
						(msg.content.modules, e);
					break;	

				case 'getAvailableModules':
					console.log('getAvailableModules event captured');
					apiModules.@esac.archive.esasky.cl.web.client.api.ApiModules::getAvailableModules(Lcom/google/gwt/core/client/JavaScriptObject;)
						(e);
					break;	
					
									
				case 'showCoordinateGrid':
					console.log('showCoordinateGrid event captured');
					apiModules.@esac.archive.esasky.cl.web.client.api.ApiModules::showCoordinateGrid(Z)(msg.content.show);
					break;	
					
					
					
				// API EVENTS
				
				case 'registerShapeSelectionCallback':
					console.log('registerShapeSelectionCallback event captured');
					apiEvents.@esac.archive.esasky.cl.web.client.api.ApiEvents::registerShapeSelectionCallback(Lcom/google/gwt/core/client/JavaScriptObject;)
						(e);
					break;	

				case 'registerFoVChangedListener':
					console.log('registerFoVChangedListener event captured');
					apiEvents.@esac.archive.esasky.cl.web.client.api.ApiEvents::registerFoVChangedListener(Lcom/google/gwt/core/client/JavaScriptObject;)
						(e);
					break;	
					
				case 'registerEventListener':
					console.log('registerEventListener event captured');
					apiEvents.@esac.archive.esasky.cl.web.client.api.ApiEvents::registerEventListener(Lcom/google/gwt/core/client/JavaScriptObject;)
						(e);
					break;	
					
					
				// TESTING	
					
				case 'overlayJPG':
					console.log('registerEventListener event captured');
					api.@esac.archive.esasky.cl.web.client.api.Api::displayJPG(Ljava/lang/String;Ljava/lang/String;)
						(msg.content.url, msg.content.opacity);
					break;	
					
					
				default:
					console.log('No event associated');
					if(!msg.hasOwnProperty('event')){
					api.@esac.archive.esasky.cl.web.client.api.Api::noSuchEventError(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)
						(msg.event, e);
					}
			}
		}	
	
		$wnd.ApiMessageHandler.setMessageFunction(handleMessage);
		$wnd.ApiMessageHandler.emptyQueue();
	}-*/;

}
