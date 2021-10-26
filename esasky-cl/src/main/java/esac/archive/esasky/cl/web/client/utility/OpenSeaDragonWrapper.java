package esac.archive.esasky.cl.web.client.utility;

import com.google.gwt.core.client.JavaScriptObject;

public class OpenSeaDragonWrapper {

	private double ra;
	private double dec;
	private double fov;
	private double rotation;
	
	private int width;
	private int height;
	
	private String id;
	private String url;
	private OpenSeaDragonType type;
	
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
	}
	
	public  JavaScriptObject createOpenSeaDragonObject() {
		removeOpenSeaDragonFromAladin();
		return createOpenSeaDragonObject(id, url, type.getType(), ra, dec, fov, rotation, width, height);
	}
	
	public void addOpenSeaDragonToAladin(JavaScriptObject openSeaDragonObject) {
		removeOpenSeaDragonFromAladin();
		AladinLiteWrapper.getAladinLite().addOpenSeaDragon(openSeaDragonObject);
	}
	
	public void removeOpenSeaDragonFromAladin() {
		AladinLiteWrapper.getAladinLite().removeOpenSeaDragon("");
	}
	
	private native JavaScriptObject createOpenSeaDragonObject(String name, String url, String type, double ra, double dec,
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
