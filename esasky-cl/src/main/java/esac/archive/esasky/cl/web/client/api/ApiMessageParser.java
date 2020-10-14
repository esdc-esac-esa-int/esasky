package esac.archive.esasky.cl.web.client.api;

public class ApiMessageParser {
	
	public static native void init(Api instance) /*-{
	
		function handleMessage(e){
			var msg = e.data
			console.log('message received');
			console.log(msg);
			
			if('origin' in msg){
				if(msg.origin == 'pyesasky'){
					instance.@esac.archive.esasky.cl.web.client.api.Api::setGoogleAnalyticsCatToPython()();
				}
				else{
					instance.@esac.archive.esasky.cl.web.client.api.Api::setGoogleAnalyticsCatToAPI()();
				}
			}else{
					instance.@esac.archive.esasky.cl.web.client.api.Api::setGoogleAnalyticsCatToAPI()();
			}
			
			switch (msg.event) {
				
				case 'initTest':
					console.log('initTest event captured');
					instance.@esac.archive.esasky.cl.web.client.api.Api::sendInitMessage(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
					break;
	
				case 'goToTargetName':
					console.log('goToTargetName event captured');
					instance.@esac.archive.esasky.cl.web.client.api.Api::goToTargetName(Ljava/lang/String;)(msg.content.targetName);
					break;
				
				case 'setFov':
					console.log('setFoV event captured');
					instance.@esac.archive.esasky.cl.web.client.api.Api::setFoV(D)(msg.content.fov);
					break;
				
				case 'goToRaDec':
					console.log('goToRADec event captured!');
					return instance.@esac.archive.esasky.cl.web.client.api.Api::goTo(Ljava/lang/String;Ljava/lang/String;)(msg.content.ra, msg.content.dec);
	
				case 'setHipsColorPalette':
					console.log('setHiPSColorPalette event captured!');
					return instance.@esac.archive.esasky.cl.web.client.api.Api::setHiPSColorPalette(Ljava/lang/String;)(msg.content.colorPalette);
	
				case 'changeHips':
					console.log('changeHips event captured!');
					console.log(msg);
					instance.@esac.archive.esasky.cl.web.client.api.Api::setHiPS(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(msg.content.hipsName,e);
					break;
	
				case 'addHips':
					console.log('addHips event captured!');
					console.log(msg);
					instance.@esac.archive.esasky.cl.web.client.api.Api::addHiPS(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(msg.content.hipsName,e);
					break;
				
				case 'changeHipsWithParams':
					console.log('changeHiPSWithParams event captured!');
					console.log(msg);
					console.log("HiPS URL "+msg.content.hips.url);
					instance.@esac.archive.esasky.cl.web.client.api.Api::setHiPSWithParams(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;)
						(msg.content.hips.name, msg.content.hips.url, msg.content.hips.cooframe, msg.content.hips.maxnorder, msg.content.hips.imgformat);
					break
	
				case 'addHipsWithParams':
					console.log('addHipsWithParams event captured!');
					console.log(msg);
					console.log("HiPS URL "+msg.content.hips.url);
					instance.@esac.archive.esasky.cl.web.client.api.Api::addHiPSWithParams(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;)
						(msg.content.hips.name, msg.content.hips.url, msg.content.hips.cooframe, msg.content.hips.maxnorder, msg.content.hips.imgformat);
					break
					
				case 'removeHips':
					console.log('removeHipsOnIndex event captured!');
					console.log(msg);
					instance.@esac.archive.esasky.cl.web.client.api.Api::removeSkyRow(ILcom/google/gwt/core/client/JavaScriptObject;)(msg.content.index,e);
					break;
	
				case 'openSkyPanel':
					console.log('openSkyPanel event captured!');
					console.log(msg);
					instance.@esac.archive.esasky.cl.web.client.api.Api::openSkyPanel()();
					break;
				
				case 'closeSkyPanel':
					console.log('closeSkyPanel event captured!');
					console.log(msg);
					instance.@esac.archive.esasky.cl.web.client.api.Api::closeSkyPanel()();
					break;
				
				case 'getNumberOfSkyRows':
					console.log('getNumberOfSkyRows event captured!');
					console.log(msg);
					instance.@esac.archive.esasky.cl.web.client.api.Api::getNumberOfSkyRows(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
					break;
					
				case 'setHipsSliderValue':
					console.log('setHipsSliderValue event captured!');
					console.log(msg);
					instance.@esac.archive.esasky.cl.web.client.api.Api::setHiPSSliderValue(D)(msg.content.value);
					break;
					
				case 'overlayFootprints':
					console.log('overlayFootprints event captured!');
					console.log(msg);
					var footprintSetJSON = JSON.stringify(msg.content);
					instance.@esac.archive.esasky.cl.web.client.api.Api::overlayFootprints(Ljava/lang/String;Z)(footprintSetJSON, false);
					break;
	
				case 'overlayFootprintsWithDetails':
					console.log('overlayFootprintsWithDetails event captured!');
					console.log(msg);
					var footprintSetJSON = JSON.stringify(msg.content);
					instance.@esac.archive.esasky.cl.web.client.api.Api::overlayFootprints(Ljava/lang/String;Z)(footprintSetJSON, true);
					break;
				
				case 'clearFootprintsOverlay':
					console.log('clearFootprintsOverlay event captured!');
					console.log(msg);
					instance.@esac.archive.esasky.cl.web.client.api.Api::removeOverlay(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)
						(msg.content.overlayName, e);
					break;
	
				case 'deleteFootprintsOverlay':
					console.log('deleteFootprintsOverlay event captured!');
					console.log(msg);
					instance.@esac.archive.esasky.cl.web.client.api.Api::removeOverlay(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)
						(msg.content.overlayName, e);
					break;
	
				case 'setOverlayColor':
					console.log('setOverlayColor event captured!');
					console.log(msg);
					instance.@esac.archive.esasky.cl.web.client.api.Api::setOverlayColor(Ljava/lang/String;Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)
						(msg.content.overlayName, msg.content.color, e);
					break;
	
				case 'setOverlaySize':
					console.log('setOverlayColor event captured!');
					console.log(msg);
					instance.@esac.archive.esasky.cl.web.client.api.Api::setOverlaySize(Ljava/lang/String;DLcom/google/gwt/core/client/JavaScriptObject;)
						(msg.content.overlayName, msg.content.size, e);
					break;
					
				case 'setOverlayShape':
					console.log('setOverlayColor event captured!');
					console.log(msg);
					instance.@esac.archive.esasky.cl.web.client.api.Api::setOverlayShape(Ljava/lang/String;Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)
						(msg.content.overlayName, msg.content.shape, e);
					break;
	
				case 'getActiveOverlays':
					console.log('getActiveOverlays event captured!');
					console.log(msg);
					instance.@esac.archive.esasky.cl.web.client.api.Api::getActiveOverlays(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
					break;
	
				case 'overlayCatalogue':
					console.log('overlayCatalogue event captured!');
					console.log(msg);
					var catJSON = JSON.stringify(msg.content);
					instance.@esac.archive.esasky.cl.web.client.api.Api::overlayCatalogue(Ljava/lang/String;Z)(catJSON, false);
					break;
	
				case 'overlayCatalogueWithDetails':
					console.log('overlayCatalogueWithDetails event captured!');
					console.log(msg);
					var userCatalogueJSON = JSON.stringify(msg.content);
					instance.@esac.archive.esasky.cl.web.client.api.Api::overlayCatalogue(Ljava/lang/String;Z)(userCatalogueJSON, true);
					break;
				
				case 'clearCatalogue':
					console.log('clear catalgue event captured!');
					console.log(msg);
					instance.@esac.archive.esasky.cl.web.client.api.Api::removeOverlay(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)
						(msg.content.overlayName, e);
					break;
	
				case 'deleteCatalogue':
					console.log('remove catalgue event captured!');
					console.log(msg);
					instance.@esac.archive.esasky.cl.web.client.api.Api::removeOverlay(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)
						(msg.content.overlayName, e);
					break;
	
				case 'getAvailableHiPS':
					console.log('getAvailableHiPS event captured!');
					console.log(msg);
					instance.@esac.archive.esasky.cl.web.client.api.Api::getAvailableHiPS(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(msg.content.wavelength,e);
					break;	
					
				case 'addJwst':
					console.log('addJwst event captured!');
					console.log(msg);
					instance.@esac.archive.esasky.cl.web.client.api.Api::addJwst(Ljava/lang/String;Ljava/lang/String;ZLcom/google/gwt/core/client/JavaScriptObject;)
						(msg.content.instrument, msg.content.detector, msg.content.showAllInstruments, e);
					break;
	
				case 'addJwstWithCoordinates':
					console.log('addJwstWithCoordinates event captured!');
					console.log(msg);
					instance.@esac.archive.esasky.cl.web.client.api.Api::addJwstWithCoordinates(Ljava/lang/String;Ljava/lang/String;ZLjava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)
						(msg.content.instrument, msg.content.detector, msg.content.showAllInstruments, msg.content.ra, msg.content.dec, msg.content.rotation, e);
					break;
					
				case 'closeJwstPanel':
					console.log('closeJwst event captured!');
					console.log(msg);
					instance.@esac.archive.esasky.cl.web.client.api.Api::closeJwstPanel()();
					break;
					
				case 'openJwstPanel':
					console.log('openJwst event captured!');
					console.log(msg);
					instance.@esac.archive.esasky.cl.web.client.api.Api::openJwstPanel()();
					break;
		
				case 'clearJwstAll':
					console.log('clearJwstAll event captured!');
					console.log(msg);
					instance.@esac.archive.esasky.cl.web.client.api.Api::clearJwst()();
					break;
					
				case 'getCenter':
					console.log('getCenter event captured');
					var coors = instance.@esac.archive.esasky.cl.web.client.api.Api::getCenter(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(msg.content.cooFrame,e);
					break;	
					
				case 'getObservationsCount':
					console.log('getObservations event captured');
					instance.@esac.archive.esasky.cl.web.client.api.Api::getObservationsCount(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
					break;	
					
				case 'getCataloguesCount':
					console.log('getCataloguesCount event captured');
					instance.@esac.archive.esasky.cl.web.client.api.Api::getCataloguesCount(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
					break;	
					
				case 'getSpectraCount':
					console.log('getSpectraCount event captured');
					instance.@esac.archive.esasky.cl.web.client.api.Api::getSpectraCount(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
					break;	
					
				case 'plotObservations':
					console.log('plotObservations event captured');
					if(msg.content.ra != null){
						var callbackMessage = instance.@esac.archive.esasky.cl.web.client.api.Api::coneSearchObservations(Ljava/lang/String;DDDLcom/google/gwt/core/client/JavaScriptObject;)
							(msg.content.missionId, msg.content.ra, msg.content.dec, msg.content.radius, e);
					}else{
						var callbackMessage = instance.@esac.archive.esasky.cl.web.client.api.Api::plotObservations(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)
							(msg.content.missionId,e);
					}
					break;	
					
				case 'plotCatalogues':
					console.log('plotCatalogues event captured');
					if(msg.content.ra != null){
						var callbackMessage = instance.@esac.archive.esasky.cl.web.client.api.Api::coneSearchCatalogues(Ljava/lang/String;DDDLcom/google/gwt/core/client/JavaScriptObject;)
							(msg.content.missionId, msg.content.ra, msg.content.dec, msg.content.radius, e);
					}else{
						var callbackMessage = instance.@esac.archive.esasky.cl.web.client.api.Api::plotCatalogues(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)
							(msg.content.missionId,e);
					}
					break;	
	
				case 'plotSpectra':
					console.log('plotSpectra event captured');
					if(msg.content.ra != null){
						var callbackMessage = instance.@esac.archive.esasky.cl.web.client.api.Api::coneSearchSpectra(Ljava/lang/String;DDDLcom/google/gwt/core/client/JavaScriptObject;)
							(msg.content.missionId, msg.content.ra, msg.content.dec, msg.content.radius, e);
					}else{
						var callbackMessage = instance.@esac.archive.esasky.cl.web.client.api.Api::plotSpectra(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)
							(msg.content.missionId,e);
					}
					break;	
	
				case 'plotPublications':
					console.log('plotPublications event captured');
					if(msg.content.ra != null){
						var callbackMessage = instance.@esac.archive.esasky.cl.web.client.api.Api::coneSearchPublications(DDDLcom/google/gwt/core/client/JavaScriptObject;)
							(msg.content.missionId, msg.content.ra, msg.content.dec, msg.content.radius, e);
					}else{
						var callbackMessage = instance.@esac.archive.esasky.cl.web.client.api.Api::plotPublications(Lcom/google/gwt/core/client/JavaScriptObject;)
							(e);
					}
					break;	
					
				case 'getResultPanelData':
					console.log('getResultPanelData event captured');
					instance.@esac.archive.esasky.cl.web.client.api.Api::getResultPanelData(Lcom/google/gwt/core/client/JavaScriptObject;)
						(e);
					break;	
	
				case 'closeResultPanelTab':
					console.log('closeResultPanelTab event captured');
					instance.@esac.archive.esasky.cl.web.client.api.Api::closeResultPanelTab(I)(msg.content.index);
					break;	
	
				//Keeping this for legacy
				case 'closeDataPanel':
					console.log('closeDataPanel event captured');
					instance.@esac.archive.esasky.cl.web.client.api.Api::hideResultPanel()();
					break;	
	
				case 'hideResultPanel':
					console.log('hideResultPanel event captured');
					instance.@esac.archive.esasky.cl.web.client.api.Api::hideResultPanel()();
					break;	
	
				case 'showResultPanel':
					console.log('hideResultPanel event captured');
					instance.@esac.archive.esasky.cl.web.client.api.Api::showResultPanel()();
					break;	
					
				case 'closeAllResultPanelTabs':
					console.log('closeResultPanelTab event captured');
					instance.@esac.archive.esasky.cl.web.client.api.Api::closeAllResultPanelTabs()();
					break;	
					
				case 'showCoordinateGrid':
					console.log('showCoordinateGrid event captured');
					instance.@esac.archive.esasky.cl.web.client.api.Api::showCoordinateGrid(Z)(msg.content.show);
					break;	
					
				case 'plotTapService':
					console.log('plotTapService event captured');
					instance.@esac.archive.esasky.cl.web.client.api.Api::plotExtTap(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)
					(msg.content.tapService, e);
					break;	
					
				case 'getTapServiceCount':
					console.log('getTapServiceCount event captured');
					instance.@esac.archive.esasky.cl.web.client.api.Api::extTapCount(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)
						(msg.content.tapService, e);
					break;	
	
				case 'getAvailableTapServices':
					console.log('getAvailableTapServices event captured');
					instance.@esac.archive.esasky.cl.web.client.api.Api::getAvailableTapServices(Lcom/google/gwt/core/client/JavaScriptObject;)
						(e);
					break;	
	
				case 'getAllAvailableTapMissions':
					console.log('getAllAvailableTapMissions event captured');
					instance.@esac.archive.esasky.cl.web.client.api.Api::getAllAvailableTapMissions(Lcom/google/gwt/core/client/JavaScriptObject;)
						(e);
					break;	
					
				case 'getTapADQL':
					console.log('getTapADQL event captured');
					instance.@esac.archive.esasky.cl.web.client.api.Api::getExtTapADQL(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)
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
					instance.@esac.archive.esasky.cl.web.client.api.Api::plotExtTapWithDetails(Ljava/lang/String;Ljava/lang/String;ZLjava/lang/String;Ljava/lang/String;ILesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject; )
						(msg.content.name, msg.content.tapUrl, msg.content.dataOnlyInView, msg.content.adql, msg.content.color, msg.content.limit, msg.content);
					
					break;	
	
				case 'getVisibleNpix':
					console.log('getVisibleNpix event captured');
					instance.@esac.archive.esasky.cl.web.client.api.Api::getVisibleNpix(I)
						(msg.content.norder);
					break;	
					
				case 'addMOC_old':
					console.log('addMOC event captured');
					var name = msg.content.name || 'MOC';
					instance.@esac.archive.esasky.cl.web.client.api.Api::addMOC_Old(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)
						(name, msg.content.options, msg.content.mocData);
					break;	

				case 'addMOC':
					console.log('addMOC event captured');
					var name = msg.content.name || 'MOC';
					instance.@esac.archive.esasky.cl.web.client.api.Api::addMOC(Ljava/lang/String;Lesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject;Lesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject;)
						(name, msg.content.options, msg.content.mocData);
					break;	
					
				case 'removeMOC':
					console.log('removeMOC event captured');
					instance.@esac.archive.esasky.cl.web.client.api.Api::removeMOC(Ljava/lang/String;)
						(msg.content.name);
					break;	
					
				case 'addQ3CMOC':
					console.log('addQ3CMOC event captured');
					instance.@esac.archive.esasky.cl.web.client.api.Api::addQ3CMOC(Ljava/lang/String;Ljava/lang/String;)
						(msg.content.options, msg.content.mocData);
					break;	
					
				case 'registerShapeSelectionCallback':
					console.log('registerShapeSelectionCallback event captured');
					instance.@esac.archive.esasky.cl.web.client.api.Api::registerShapeSelectionCallback(Lcom/google/gwt/core/client/JavaScriptObject;)
						(e);
					break;	
					
				case 'addCustomTreeMap':
					console.log('addCustomTreeMap event captured');
					instance.@esac.archive.esasky.cl.web.client.api.Api::addCustomTreeMap(Lesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)
						(msg.content.treeMap,e);
					break;	
					
				case 'setModuleVisibility':
					console.log('setModuleVisibility event captured');
					instance.@esac.archive.esasky.cl.web.client.api.Api::setModuleVisibility(Lesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)
						(msg.content.modules, e);
					break;	
					
				case 'registerFoVChangedListener':
					console.log('registerFoVChangedListener event captured');
					instance.@esac.archive.esasky.cl.web.client.api.Api::registerFoVChangedListener(Lcom/google/gwt/core/client/JavaScriptObject;)
						(e);
					break;	
					
				default:
					console.log('No event associated');
			}
		}	
	
		$wnd.ApiMessageHandler.setMessageFunction(handleMessage);
		$wnd.ApiMessageHandler.emptyQueue();
	}-*/;

}
