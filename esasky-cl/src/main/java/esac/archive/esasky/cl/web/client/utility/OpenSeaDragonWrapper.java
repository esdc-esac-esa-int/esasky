package esac.archive.esasky.cl.web.client.utility;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import esac.archive.esasky.cl.web.client.event.TileLoadedEvent;
import esac.archive.esasky.cl.web.client.event.TileLoadedEventHandler;

public class OpenSeaDragonWrapper implements HasHandlers {

	private double ra;
	private double dec;
	private double fov;
	private double rotation;
	
	private int width;
	private int height;
	
	private String id;
	private String url;
	private OpenSeaDragonType type;

	private HandlerManager handlerManager;

	
	public OpenSeaDragonWrapper(String id, String url, OpenSeaDragonType type, double ra, double dec,
			double fov, double rotation, int width, int height) {
		this.ra = ra;
		this.dec = dec;
		this.fov = fov;
		this.rotation = rotation;
		
		this.width = width;
		this.height = height;
		
		this.id = id;
		this.url = url;
		this.type = type;

		handlerManager = new HandlerManager(this);
	}
	
	public  JavaScriptObject createOpenSeaDragonObject() {
		removeOpenSeaDragonFromAladin();
		return createOpenSeaDragonObject(this, id, url, type.getType(), ra, dec, fov, rotation, width, height);
	}
	
	public void addOpenSeaDragonToAladin(JavaScriptObject openSeaDragonObject) {
		removeOpenSeaDragonFromAladin();
		AladinLiteWrapper.getAladinLite().addOpenSeaDragon(openSeaDragonObject);
	}
	
	public void removeOpenSeaDragonFromAladin() {
		AladinLiteWrapper.getAladinLite().removeOpenSeaDragon("");
	}

	private void onTileLoaded(boolean success, String message) {
		TileLoadedEvent event = new TileLoadedEvent(success,message);
		fireEvent(event);
	}

	@Override
	public void fireEvent(GwtEvent<?> event) {
		handlerManager.fireEvent(event);
	}

	public HandlerRegistration addTileLoadedEventHandler(TileLoadedEventHandler handler) {
		return handlerManager.addHandler(TileLoadedEvent.TYPE, handler);
	}

	private native JavaScriptObject createOpenSeaDragonObject(OpenSeaDragonWrapper wrapper, String name, String url, String type, double ra, double dec,
			double fov, double rot, int width, int height)/*-{
		var tileSources;

		var openseadragon = $wnd.OpenSeadragon({
		    id: "openseadragonCanvas",
			maxZoomPixelRatio: 3,
		    animationTime: .01,
		    showFullPageControl: false,
		    showHomeControl: false,
		    showZoomControl: false,
			crossOriginPolicy: "Anonymous"
		});
		if (type == 'image'){
			options = {
				type: type,
	       		url: url,
				success: function(event) {
	                   openseadragon.image = event.item;
	            }
			};
			openseadragon.addSimpleImage(options)
		}else{
			options = {
				tileSource :{
			        type: type,
			        width: width,
			        height: height,
			        tilesUrl: url
		    	},
				success: function(event) {
	                   openseadragon.image = event.item;
	            }
			};
			openseadragon.addTiledImage(options)
		};



        // Fail event emitted by Image sources
        openseadragon.addOnceHandler('add-item-failed', function(event) {
			wrapper.@esac.archive.esasky.cl.web.client.utility.OpenSeaDragonWrapper::onTileLoaded(*)(false, event.message);
		});

		openseadragon.addOnceHandler('tile-load-failed', function(event) {
			wrapper.@esac.archive.esasky.cl.web.client.utility.OpenSeaDragonWrapper::onTileLoaded(*)(false, event.message);
		});

		openseadragon.addOnceHandler('tile-loaded', function(event) {
			wrapper.@esac.archive.esasky.cl.web.client.utility.OpenSeaDragonWrapper::onTileLoaded(*)(true, "");
		});

		openseadragon.name = name;
	    openseadragon.fov = fov;
	    openseadragon.ra = ra;
	    openseadragon.dec = dec;
	    openseadragon.rot = rot;
	    openseadragon.whScale = width / height;
	    openseadragon.viewport.setRotation(rot); 
	    return openseadragon;
	}-*/;



	public enum OpenSeaDragonType {
	    
		TILED("zoomifytileservice"), SINGLE("image");

	    private String type;

	    OpenSeaDragonType(String type) {
	        this.type = type;
	    }

	    public String getType() {
	        return this.type;
	    }
	    
	    public static OpenSeaDragonType getImageType(String type){
			for (OpenSeaDragonType openSeaDragonType : OpenSeaDragonType.values()) {
				if (openSeaDragonType.getType().equals(type)) {
					return openSeaDragonType;
				}
			}
			
			return null;
		}
	}
}
