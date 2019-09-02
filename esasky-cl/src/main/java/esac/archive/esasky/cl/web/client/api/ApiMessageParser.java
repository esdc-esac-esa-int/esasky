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
				instance.@esac.archive.esasky.cl.web.client.api.Api::setHiPSWithParams(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;)(msg.content.hips.id, msg.content.hips.name, msg.content.hips.url, msg.content.hips.cooframe, msg.content.hips.maxnorder, msg.content.hips.imgformat);
				break

			case 'addHipsWithParams':
				console.log('addHipsWithParams event captured!');
				console.log(msg);
				console.log("HiPS URL "+msg.content.hips.url);
				instance.@esac.archive.esasky.cl.web.client.api.Api::addHiPSWithParams(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;)(msg.content.hips.id, msg.content.hips.name, msg.content.hips.url, msg.content.hips.cooframe, msg.content.hips.maxnorder, msg.content.hips.imgformat);
				break
				
			case 'removeHipsOnIndex':
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
				instance.@esac.archive.esasky.cl.web.client.api.Api::overlayFootprints(Ljava/lang/String;)(footprintSetJSON);
				break;

			case 'overlayFootprintsWithDetails':
				console.log('overlayFootprintsWithDetails event captured!');
				console.log(msg);
				var footprintSetJSON = JSON.stringify(msg.content);
				instance.@esac.archive.esasky.cl.web.client.api.Api::overlayFootprintsWithData(Ljava/lang/String;)(footprintSetJSON);
				break;
			
			case 'clearFootprintsOverlay':
				console.log('clearFootprintsOverlay event captured!');
				console.log(msg);
				instance.@esac.archive.esasky.cl.web.client.api.Api::clearFootprints(Ljava/lang/String;)(msg.content.overlayName);
				break;
				
			case 'deleteFootprintsOverlay':
				console.log('deleteFootprintsOverlay event captured!');
				console.log(msg);
				instance.@esac.archive.esasky.cl.web.client.api.Api::deleteFootprints(Ljava/lang/String;)(msg.content.overlayName);
				break;

			case 'overlayCatalogue':
				console.log('overlayCatalogue event captured!');
				console.log(msg);
				var catJSON = JSON.stringify(msg.content);
				instance.@esac.archive.esasky.cl.web.client.api.Api::overlayCatalogue(Ljava/lang/String;)(catJSON);
				break;

			case 'overlayCatalogueWithDetails':
				console.log('overlayCatalogueWithDetails event captured!');
				console.log(msg);
				var userCatalogueJSON = JSON.stringify(msg.content);
				instance.@esac.archive.esasky.cl.web.client.api.Api::overlayCatalogueWithData(Ljava/lang/String;)(userCatalogueJSON);
				break;
			
			case 'clearCatalogue':
				console.log('clear catalgue event captured!');
				console.log(msg);
				instance.@esac.archive.esasky.cl.web.client.api.Api::clearCatalogue(Ljava/lang/String;)(msg.content.overlayName);
				break;

			case 'deleteCatalogue':
				console.log('remove catalgue event captured!');
				console.log(msg);
				instance.@esac.archive.esasky.cl.web.client.api.Api::removeCatalogue(Ljava/lang/String;)(msg.content.overlayName);
				break;

			case 'getAvailableHiPS':
				console.log('getAvailableHiPS event captured!');
				console.log(msg);
				instance.@esac.archive.esasky.cl.web.client.api.Api::getAvailableHiPS(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(msg.content.wavelength,e);
				break;	
				
			case 'addJwst':
				console.log('addJwst event captured!');
				console.log(msg);
				instance.@esac.archive.esasky.cl.web.client.api.Api::addJwst(Ljava/lang/String;Ljava/lang/String;Z)(msg.content.instrument, msg.content.detector, msg.content.showAllInstruments);
				break;

			case 'addJwstWithCoordinates':
				console.log('addJwstWithCoordinates event captured!');
				console.log(msg);
				instance.@esac.archive.esasky.cl.web.client.api.Api::addJwstWithCoordinates(Ljava/lang/String;Ljava/lang/String;ZLjava/lang/String;Ljava/lang/String;Ljava/lang/String;)(msg.content.instrument, msg.content.detector, msg.content.showAllInstruments, msg.content.ra, msg.content.dec, msg.content.rotation);
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
				
			case 'getPublicationsCount':
				console.log('getPublicationsCount event captured');
				instance.@esac.archive.esasky.cl.web.client.api.Api::getPublicationsCount(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
				break;	

			case 'plotObservations':
				console.log('plotObservations event captured');
				var callbackMessage = instance.@esac.archive.esasky.cl.web.client.api.Api::plotObservations(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(msg.content.missionId,e);
				break;	
				
			case 'plotCatalogues':
				console.log('plotCatalogues event captured');
				var callbackMessage = instance.@esac.archive.esasky.cl.web.client.api.Api::plotCatalogues(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(msg.content.missionId,e);
				break;	
				
			case 'plotSpectra':
				console.log('plotSpectra event captured');
				var callbackMessage = instance.@esac.archive.esasky.cl.web.client.api.Api::plotSpectra(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(msg.content.missionId,e);
				break;	
				
			case 'getResultPanelData':
				console.log('getResultPanelData event captured');
				instance.@esac.archive.esasky.cl.web.client.api.Api::getResultPanelData(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
				break;	
				
			case 'showCoordinateGrid':
				console.log('showCoordinateGrid event captured');
				instance.@esac.archive.esasky.cl.web.client.api.Api::showCoordinateGrid(Z)(msg.content.show);
				break;	

			default:
				console.log('No event associated');
		}
	}	
	
	$wnd.addEventListener('message', handleMessage);

	}-*/;

}
