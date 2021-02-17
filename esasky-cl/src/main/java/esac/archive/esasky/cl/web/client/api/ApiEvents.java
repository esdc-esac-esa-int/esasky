package esac.archive.esasky.cl.web.client.api;

import java.util.LinkedList;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Timer;

import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteCoordinatesOrFoVChangedEvent;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteShapeSelectedEvent;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.Controller;
import esac.archive.esasky.cl.web.client.utility.CoordinateUtils;
import esac.archive.esasky.cl.web.client.view.resultspanel.tab.TabObserver;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;

public class ApiEvents extends ApiBase{
	
	private LinkedList<TreeMapListener> treeMapListeners = new LinkedList<ApiEvents.TreeMapListener>();
	public ApiEvents(Controller controller) {
		this.controller = controller;
	}

	public void registerShapeSelectionCallback(final JavaScriptObject widget) {
		
		 CommonEventBus.getEventBus().addHandler(AladinLiteShapeSelectedEvent.TYPE,
				 selectEvent -> {

                  	GeneralJavaScriptObject shape = (GeneralJavaScriptObject) selectEvent.getShape().cast();
                  	String overlayName = selectEvent.getOverlayName();
                  	String name = shape.getStringProperty(ApiConstants.SHAPE_NAME);
                  	if(name == null) {
                  		name = "";
              		}
                  	
         			String id = shape.getStringProperty(ApiConstants.SHAPE_ID);
         			if(id == null) {
	         				id = "";
       				}
       				
              		JSONObject item = new JSONObject();
              		item.put(ApiConstants.SHAPE_OVERLAY, new JSONString(overlayName));
              		item.put(ApiConstants.SHAPE_NAME, new JSONString(name));
              		item.put(ApiConstants.SHAPE_ID, new JSONString(id));
              		
              		JSONObject result = new JSONObject();
              		result.put(ApiConstants.ACTION, new JSONString(ApiConstants.EVENT_SHAPE_SELECTION));
      				result.put(ApiConstants.VALUES, item);
      				sendBackEventToWidget(result, widget);
                  }
          );
	}
	
	public void registerFoVChangedListener(JavaScriptObject widget) {
		ApiEvents _this = this;
		
		Timer viewMovedTimer = new Timer() {
			@Override
			public void run() {
				JSONObject result = new JSONObject();
				JSONObject fov = new JSONObject();
				SkyViewPosition pos = CoordinateUtils.getCenterCoordinateInJ2000();
				
				fov.put(ApiConstants.RA, new JSONNumber(pos.getCoordinate().ra));
				fov.put(ApiConstants.DEC, new JSONNumber(pos.getCoordinate().dec));
				fov.put(ApiConstants.FOV, new JSONNumber(pos.getFov()));
				fov.put(ApiConstants.FOVRA, new JSONNumber(pos.getFov()));
				result.put(ApiConstants.ACTION, new JSONString(ApiConstants.EVENT_VIEW_CHANGED));
				result.put(ApiConstants.VALUES, fov);
				_this.sendBackEventToWidget(result, widget);
			}
			
			@Override
			public void schedule(int delayMillis) {
				super.cancel();
				super.schedule(delayMillis);
			}
		};

		CommonEventBus.getEventBus().addHandler(AladinLiteCoordinatesOrFoVChangedEvent.TYPE, 
				fovEvent -> viewMovedTimer.schedule(2000)
		);  
		
	}
	
	public void registerTabListener(JavaScriptObject widget) {
		controller.getRootPresenter().getResultsPresenter().getTabPanel().registerClosingObserver(
			new TabObserver() {
					
					@Override
					public void onClose(String id) {
						JSONObject result = new JSONObject();
						JSONObject item = new JSONObject();
						
						item.put(ApiConstants.PANEL_ID, new JSONString(id));
						result.put(ApiConstants.ACTION, new JSONString(ApiConstants.EVENT_PANEL_CLOSED));
						result.put(ApiConstants.VALUES, item);
						sendBackToWidget(result, widget);
					}
		
					@Override
					public void onOpen(String id) {
						JSONObject result = new JSONObject();
						JSONObject item = new JSONObject();
						
						item.put(ApiConstants.PANEL_ID, new JSONString(id));
						result.put(ApiConstants.ACTION, new JSONString(ApiConstants.EVENT_PANEL_OPENED));
						result.put(ApiConstants.VALUES, item);
						sendBackToWidget(result, widget);				
					}
				});
	}
	
	
	public void registerTreeMapListener(JavaScriptObject widget) {
		treeMapListeners.add(new TreeMapListener(widget));
	}
	
	public void treeMapClicked(String treeMapName, String mission) {
		for(TreeMapListener treeMapListener : treeMapListeners) {
			treeMapListener.onTreeMapClicked(treeMapName, mission);
		}
	}

	public void registerEventListener(JavaScriptObject widget) {
		registerFoVChangedListener(widget);
		registerTabListener(widget);
		registerShapeSelectionCallback(widget);
		registerTreeMapListener(widget);
		
	}
	
	private class TreeMapListener{
		private JavaScriptObject widget;
		public TreeMapListener(JavaScriptObject widget) {
			this.widget = widget;
		}
		
		public void onTreeMapClicked(String treeMapName, String mission) {
			JSONObject result = new JSONObject();
			JSONObject treeMap = new JSONObject();
			
			treeMap.put(ApiConstants.TREEMAP , new JSONString(treeMapName));
			treeMap.put(ApiConstants.TREEMAP_MISSION , new JSONString(mission));
			
			SkyViewPosition pos = CoordinateUtils.getCenterCoordinateInJ2000();
			
			treeMap.put(ApiConstants.RA, new JSONNumber(pos.getCoordinate().ra));
			treeMap.put(ApiConstants.DEC, new JSONNumber(pos.getCoordinate().dec));
			treeMap.put(ApiConstants.FOV, new JSONNumber(pos.getFov()));
			
			result.put(ApiConstants.ACTION, new JSONString(ApiConstants.EVENT_TREEMAP_MISSION_CLICKED));
			result.put(ApiConstants.VALUES, treeMap);
			
            sendBackToWidget(result, widget);
		}
		
	}
}
