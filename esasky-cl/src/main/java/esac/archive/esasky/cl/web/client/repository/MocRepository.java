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
import esac.archive.esasky.cl.web.client.model.entities.ExtTapEntity;
import esac.archive.esasky.cl.web.client.model.entities.MOCEntity;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.CoordinateUtils;
import esac.archive.esasky.cl.web.client.view.allskypanel.MOCTooltip;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;

public class MocRepository {

	private static MocRepository _instance;
	private List<MOCEntity> allEntities = new LinkedList<MOCEntity>();
	private List<ExtTapEntity> extTapEntities = new LinkedList<ExtTapEntity>();

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
				
				
				GeneralJavaScriptObject mocsClicked = (GeneralJavaScriptObject) event.getObject();
				int length = GeneralJavaScriptObject.convertToInteger(mocsClicked.getProperty("length"));
				
				StringBuilder sb = new StringBuilder();
				
				for(int i = 0;i < length; i++) {
					GeneralJavaScriptObject data = mocsClicked.getProperty(Integer.toString(i));
					String name = data.getProperty("name").toString();
					for(MOCEntity entity : allEntities) {
						if(name.startsWith(entity.getDescriptor().getDescriptorId())) {
							name = entity.getDescriptor().getGuiShortName();
							break;
						}
					}
					
					sb.append("<h2>");
					sb.append(name);
					sb.append(":</h2> <b>Order:</b> ");
					sb.append(data.getProperty("order"));
					sb.append(" <b>Ipix:</b> ");
					sb.append(data.getProperty("ipix"));
					sb.append(" <b>Count:</b> ");
					sb.append(data.getProperty("count"));
					if(i < length - 1) {
						sb.append("<hr/>");
					}
				}
				String tooltipText = sb.toString();

				if(tooltipText != "") {
					MOCTooltip tooltip = new MOCTooltip(tooltipText, event.getX(), event.getY());
					tooltip.show(AladinLiteWrapper.getAladinLite().getCooFrame());
				}
				
			}
		});
		
		CommonEventBus.getEventBus().addHandler(AladinLiteFoVChangedEvent.TYPE, new AladinLiteFoVChangedEventHandler () {

			@Override
			public void onChangeEvent(AladinLiteFoVChangedEvent fovEvent) {
				for(MOCEntity entity : allEntities){
					entity.onFoVChanged(); 
				}
				for(ExtTapEntity entity : extTapEntities){
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
	public void addExtTapMocEntity(ExtTapEntity entity){
		extTapEntities.add(entity);
	}
	
	public void removeExtTapEntity(ExtTapEntity entity) {
		extTapEntities.remove(entity);
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
