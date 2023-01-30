package esac.archive.esasky.cl.web.client.repository;

import esac.archive.absi.modules.cl.aladinlite.widget.client.event.*;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.model.MOCInfo;
import esac.archive.esasky.cl.web.client.model.entities.MOCEntity;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.CoordinateUtils;
import esac.archive.esasky.cl.web.client.view.allskypanel.MOCTooltip;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


public class MocRepository {

	private static MocRepository _instance;
	private final List<MOCEntity> allEntities = new LinkedList<>();

	public static MocRepository init() {
		_instance = new MocRepository();
		return _instance;
	}	
	
	public static MocRepository getInstance() {
		if (_instance == null) {
            throw new AssertionError("You have to call init first");
        }
        return _instance;
	}
	
	private MocRepository() {
		
		CommonEventBus.getEventBus().addHandler(AladinLiteMOCIpixClickedEvent.TYPE, event -> {


			GeneralJavaScriptObject mocsClicked = (GeneralJavaScriptObject) event.getObject();
			int length = GeneralJavaScriptObject.convertToInteger(mocsClicked.getProperty("length"));

			List<MOCInfo> mocInfos = new LinkedList<>();
			for(int i = 0; i < length; i++) {
				GeneralJavaScriptObject data = mocsClicked.getProperty(Integer.toString(i));
				String name = data.getProperty("name").toString();
				CommonTapDescriptor descriptor;
				for(MOCEntity entity : allEntities) {
					if(name.equals(entity.getId())) {
						descriptor = entity.getDescriptor();
						mocInfos.add(new MOCInfo(descriptor, entity,
								GeneralJavaScriptObject.convertToInteger(data.getProperty("count")),
								data.getProperty("pixels")));

						break;
					}
				}
			}

			if(!mocInfos.isEmpty()) {
				MOCTooltip tooltip = new MOCTooltip(mocInfos, event.getX(), event.getY());

				tooltip.registerObserver(mocInfo -> mocInfo.entity.sendLoadQuery(mocInfo));
				tooltip.show(AladinLiteWrapper.getAladinLite().getCooFrame());
			}

		});
		
		CommonEventBus.getEventBus().addHandler(AladinLiteFoVChangedEvent.TYPE, fovEvent -> {
			for(MOCEntity entity : allEntities){
				entity.onFoVChanged();
			}
		});
		
		CommonEventBus.getEventBus().addHandler(AladinLiteCoordinatesOrFoVChangedEvent.TYPE, fovEvent -> {
			for(MOCEntity entity : allEntities){
				entity.onMove();
			}
		});

	}
	
	public void addMocEntity(MOCEntity entity){
		allEntities.add(entity);
	}
	
	public void removeEntity(MOCEntity entity) {
		allEntities.remove(entity);
	}

	public MOCEntity getEntity(String name) {
		for(MOCEntity entity : allEntities) {
			if(entity.getId().equals(name)) {
				return entity;
			}
		}
		
		return null;
	}
	
	public static int getMinOrderFromFoV() {
		double fov = CoordinateUtils.getCenterCoordinateInJ2000().getFov();
				
		if(fov > 80) {
			return 3;
		}else if(fov > 60) {
			return 4;
		}else if(fov > 30) {
			return 5;
		}else if(fov > 15) {
			return 6;
		}else if(fov > 5) {
			return 7;
		}else if(fov > 3) {
			return 8;
		}else if(fov > 1.5) {
			return 9;
		}else if(fov > 0.75) {
			return 10;
		}else if(fov > 0.3) {
			return 11;
		}else if(fov > 0.15) {
			return 12;
		}else if(fov > 0.075) {
			return 13;
		}else {
			return 14;
		}			
	}
	
	public static int getMaxOrderFromFoV() {
		double fov = CoordinateUtils.getCenterCoordinateInJ2000().getFov();

		if(fov > 80) {
			return 5;
		}
		else if(fov > 40) {
			return 6;
		}
		else if(fov > 20) {
			return 7;
		}else if(fov > 10) {
			return 8;
		}else if(fov > 4) {
			return 9;
		}else if(fov > 2) {
			return 10;
		}else if(fov > 1) {
			return 11;
		}else if(fov > .5) {
			return 12;
		}else if(fov > .25) {
			return 13;
		}else {
			return 14;
		}					
	}
	
	public static int getTargetOrderFromFoV() {
		double fov = CoordinateUtils.getCenterCoordinateInJ2000().getFov();

		if(fov > 2) {
			return 8;
		}else {
			return 14;
		}					
	}
	
	public interface MocLoadedObserver {
		void onLoaded();
	}
	
	HashMap<String, MocLoadedObserver> mocLoadedObservers = new HashMap<>();
	
	public void registerMocLoadedObserver(String key, MocLoadedObserver observer) {
		mocLoadedObservers.put(key, observer);
	}
	
	public void unRegisterMocLoadedObserver(String key) {
		mocLoadedObservers.remove(key);
	}
	
	public void notifyMocLoaded(String key) {
		if(mocLoadedObservers.containsKey(key)) {
			mocLoadedObservers.get(key).onLoaded();
			unRegisterMocLoadedObserver(key);
		}
	}
}
