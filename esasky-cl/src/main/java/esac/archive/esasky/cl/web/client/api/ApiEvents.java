package esac.archive.esasky.cl.web.client.api;

import java.util.HashMap;
import java.util.LinkedList;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Timer;

import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteCoordinatesOrFoVChangedEvent;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteSelectAreaEvent;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteShapeSelectedEvent;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.Controller;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.CoordinateUtils;
import esac.archive.esasky.cl.web.client.view.resultspanel.tab.TabObserver;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;

public class ApiEvents extends ApiBase{
	
	private LinkedList<TreeMapListener> treeMapListeners = new LinkedList<TreeMapListener>();
	private LinkedList<ButtonListener> buttonListeners = new LinkedList<ButtonListener>();
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

	public void registerShapeAreaSelectionCallback(final JavaScriptObject widget) {
		
		CommonEventBus.getEventBus().addHandler(AladinLiteSelectAreaEvent.TYPE,
				selectEvent -> {
					
					GeneralJavaScriptObject[] shapes = GeneralJavaScriptObject.convertToArray((GeneralJavaScriptObject) selectEvent.getObjects());
	        		HashMap<String, LinkedList<GeneralJavaScriptObject>> shapesToadd = new HashMap<String, LinkedList<GeneralJavaScriptObject>>();
	        		for(GeneralJavaScriptObject shape : shapes) {
	        			String overlayName = null;
	        			if(shape.hasProperty("overlay")) {
	        				overlayName = shape.getProperty("overlay").getStringProperty("name");
	        			}else if(shape.hasProperty("catalog")) {
	        				overlayName = shape.getProperty("catalog").getStringProperty("name");
	        			}
	        			
	        			if(!shapesToadd.containsKey(overlayName)) {
	        				shapesToadd.put(overlayName, new LinkedList<GeneralJavaScriptObject>());
	        			}
	        			
	        			shapesToadd.get(overlayName).add((GeneralJavaScriptObject) shape);
	        		}
	        		
	        		JSONObject overlays = new JSONObject();
	        		
	        		
	        		for(String overlayName : shapesToadd.keySet()) {
	        			JSONArray shapeArray = new JSONArray();
	        			for(GeneralJavaScriptObject shape : shapesToadd.get(overlayName)) {
	        				String name = shape.getStringProperty(ApiConstants.SHAPE_NAME);
	                      	if(name == null) {
	                      		name = "";
	                  		}
	                      	
	             			String id = shape.getStringProperty(ApiConstants.SHAPE_ID);
	             			if(id == null) {
	    	         				id = "";
	           				}
	             			JSONObject item = new JSONObject();
	             			item.put(ApiConstants.SHAPE_NAME, new JSONString(name));
	             			item.put(ApiConstants.SHAPE_ID, new JSONString(id));
	             			shapeArray.set(shapeArray.size(), item);
	        			}
	        			
	        			overlays.put(overlayName, shapeArray);
	        			
	        		}
	        		
	        		JSONObject values = new JSONObject();
	        		values.put(ApiConstants.SHAPE_OVERLAYS, overlays);
	        		values.put(ApiConstants.SHAPE_AREA_POLYGON, new JSONObject(selectEvent.getArea()));
	        		
	        		
    				JSONObject result = new JSONObject();
	        		result.put(ApiConstants.VALUES, values);
	        		result.put(ApiConstants.ACTION, new JSONString(ApiConstants.EVENT_SHAPE_AREA_SELECTION));
	        		sendBackEventToWidget(result, widget);
				});
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
						sendBackValuesToWidget(result, widget);
					}
		
					@Override
					public void onOpen(String id) {
						JSONObject result = new JSONObject();
						JSONObject item = new JSONObject();
						
						item.put(ApiConstants.PANEL_ID, new JSONString(id));
						result.put(ApiConstants.ACTION, new JSONString(ApiConstants.EVENT_PANEL_OPENED));
						result.put(ApiConstants.VALUES, item);
						sendBackValuesToWidget(result, widget);				
					}
				});
	}
	
	
	public void registerTreeMapListener(JavaScriptObject widget) {
		treeMapListeners.add(new TreeMapListener(widget));
	}
	
	public void registerButtonListener(JavaScriptObject widget) {
		buttonListeners.add(new ButtonListener(widget));
	}
	
	public void treeMapClicked(String treeMapName, String mission) {
		for(TreeMapListener treeMapListener : treeMapListeners) {
			treeMapListener.onTreeMapClicked(treeMapName, mission);
		}
	}

	public void CtrlBarButtonClicked(String name) {
		for(ButtonListener buttonListener : buttonListeners) {
			buttonListener.onButtonClicked(name);
		}
	}

	public void registerEventListener(JavaScriptObject widget) {
		registerFoVChangedListener(widget);
		registerTabListener(widget);
		registerShapeSelectionCallback(widget);
		registerTreeMapListener(widget);
		registerButtonListener(widget);
		registerShapeAreaSelectionCallback(widget);
		
	}

	public void startSelectionEvent(JavaScriptObject widget) {
//		AladinLiteWrapper.getAladinLite().startSelectionMode();
	}

	public void endSelectionEvent(JavaScriptObject widget) {
//		AladinLiteWrapper.getAladinLite().endSelectionMode();
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
			JSONObject location = new JSONObject();

			location.put(ApiConstants.RA, new JSONNumber(pos.getCoordinate().ra));
			location.put(ApiConstants.DEC, new JSONNumber(pos.getCoordinate().dec));
			location.put(ApiConstants.FOV, new JSONNumber(pos.getFov()));

			result.put(ApiConstants.TREEMAP_LOCATION , location);
			result.put(ApiConstants.ACTION, new JSONString(ApiConstants.EVENT_TREEMAP_MISSION_CLICKED));
			result.put(ApiConstants.VALUES, treeMap);
			
			//To be removed
			result.put(ApiConstants.TREEMAP_INFO, treeMap);
			
			sendBackValuesToWidget(result, widget);
		}
		
	}

	private class ButtonListener{
		private JavaScriptObject widget;
		
		public ButtonListener(JavaScriptObject widget) {
			this.widget = widget;
		}
		
		public void onButtonClicked(String name) {
			JSONObject result = new JSONObject();
			JSONObject button = new JSONObject();
			
			button.put(ApiConstants.BUTTON , new JSONString(name));
			
			SkyViewPosition pos = CoordinateUtils.getCenterCoordinateInJ2000();
			JSONObject location = new JSONObject();
			
			location.put(ApiConstants.RA, new JSONNumber(pos.getCoordinate().ra));
			location.put(ApiConstants.DEC, new JSONNumber(pos.getCoordinate().dec));
			location.put(ApiConstants.FOV, new JSONNumber(pos.getFov()));
			
			result.put(ApiConstants.TREEMAP_LOCATION , location);
			result.put(ApiConstants.ACTION, new JSONString(ApiConstants.EVENT_BUTTON_CLICKED));
			result.put(ApiConstants.VALUES, button);
			
			
			sendBackValuesToWidget(result, widget);
		}
		
	}
}
