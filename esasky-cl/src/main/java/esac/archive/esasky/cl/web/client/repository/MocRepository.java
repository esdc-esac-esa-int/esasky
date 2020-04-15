package esac.archive.esasky.cl.web.client.repository;

import java.util.LinkedList;
import java.util.List;

import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteCoordinatesOrFoVChangedEvent;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteCoordinatesOrFoVChangedEventHandler;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteFoVChangedEvent;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteFoVChangedEventHandler;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteMOCIpixClickedEvent;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteMOCIpixClickedEventHandler;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.model.entities.MOCEntity;
import esac.archive.esasky.cl.web.client.utility.CoordinateUtils;
import esac.archive.esasky.cl.web.client.view.allskypanel.MOCTooltip;

public class MocRepository {

	private static MocRepository _instance;
	private List<MOCEntity> allEntities = new LinkedList<MOCEntity>();

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
		
		CommonEventBus.getEventBus().addHandler(AladinLiteMOCIpixClickedEvent.TYPE, new AladinLiteMOCIpixClickedEventHandler () {

			@Override
			public void onMOCClicked(AladinLiteMOCIpixClickedEvent event) {
				String tooltipText = "";
				for(MOCEntity entity : allEntities){
					tooltipText += entity.MOCClicked(event.getOrders(), event.getIpixels(), event.getScreenX(), event.getScreenY());
				}

				if(tooltipText != "") {
					MOCTooltip tooltip = new MOCTooltip(event.getScreenX(), event.getScreenY());
					tooltip.show(tooltipText);
				}
				
			}
		});
		
		CommonEventBus.getEventBus().addHandler(AladinLiteFoVChangedEvent.TYPE, new AladinLiteFoVChangedEventHandler () {

			@Override
			public void onChangeEvent(AladinLiteFoVChangedEvent fovEvent) {
				for(MOCEntity entity : allEntities){
					entity.onFoVChanged(); 
				}
			}
		});
		
		CommonEventBus.getEventBus().addHandler(AladinLiteCoordinatesOrFoVChangedEvent.TYPE, new AladinLiteCoordinatesOrFoVChangedEventHandler () {
			
			@Override
			public void onChangeEvent(AladinLiteCoordinatesOrFoVChangedEvent fovEvent) {
				for(MOCEntity entity : allEntities){
					entity.onMove(); 
				}
			}
		});

	}
	
	public void addMocEntity(MOCEntity entity){
		allEntities.add(entity);
	}
	
	public void removeEntity(MOCEntity entity) {
		allEntities.remove(entity);
	}
	
	public static int getMinOrderFromFoV() {
		double fov = CoordinateUtils.getCenterCoordinateInJ2000().getFov();
				
		if(fov > 60) {
			return 3;
		}else if(fov > 40) {
			return 4;
		}else if(fov > 20) {
			return 5;
		}else if(fov > 10) {
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

		if(fov > 60) {
			return 5;
		}else if(fov > 40) {
			return 6;
		}else if(fov > 20) {
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

		if(fov > 4) {
			return 8;
//		}else if(fov > 1) {
//			return 10;
//		}else if(fov > .2) {
//			return 12;
		}else {
			return 14;
		}					
	}
}
